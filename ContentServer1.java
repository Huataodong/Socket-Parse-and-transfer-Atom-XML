package assignment2;

import java.io.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.sax.*;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class ContentServer1 {

    BufferedReader in;
    StreamResult out;
    TransformerHandler th;
    AttributesImpl atts;

    public static void main(String args[]) throws IOException {

        Socket socket = new Socket("localhost",4567);
        //external thread to send heartbeat to AggregationServer.
        ContentServer1Thread cst = new ContentServer1Thread(socket);
        cst.start();

        System.out.println("ContentServer start. Enter QUIT to end");
        System.out.println("Enter PUT to put message to AggregationServer");

        String line;
        try
        {
            int count =0;
            do{  //do while loop for input PUT

                PrintWriter os = new PrintWriter(socket.getOutputStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                line = br.readLine();

                //send message to AggregationServer
                if(line.equals("PUT")) {

                    // before parser, read the source file first
                    // reject any feed or entry with no title, link or id
                    StringBuilder sb = new StringBuilder();
                    File myObj = new File("data1.txt");
                    Scanner myReader = new Scanner(myObj);
                    while (myReader.hasNextLine())
                    {
                        sb.append(myReader.nextLine());
                    }
                    String strLine = sb.toString();
//                    System.out.println(strLine);
                    if ((!strLine.contains("title:"))|| (!strLine.contains("link:")) ||(!strLine.contains("id:"))||(strLine.isEmpty()))
                    {
                        System.out.println(" reject any feed or entry with no title, link or id");
                        socket.close();
                        return;

                    }
                    //if no error then parse source file to xml
                    new ContentServer1().read("data1.txt");

                    //the first line send to AggregationServer is 'PUT' for identify request
                    os.println(line);
                    os.flush();

                    //every PUT will has a lamport clock timestamp
                    count++;
                    os.println("Lamport Clock Timestamp: CS1: ");
                    os.println(count);

                    //count Content-Length
                    BufferedReader reader = new BufferedReader(new FileReader("data1.xml"));
                    int ContentLength = 0;
                    while (reader.readLine() != null) ContentLength++;
                    reader.close();

                    // HTTTP header
                    os.println("PUT /atom.xml HTTP/1.1");
                    os.println("User-Agent: ATOMClient/1/0");
                    os.println("Content-Type: XML");
                    os.println("Content-Length: "+ ContentLength);
                    os.flush();

                    //send xml feed to AggregationServer by reading xml file
                    String message = new String(Files.readAllBytes(Paths.get("data1.xml")));
                    socket.getOutputStream().write(message.getBytes("UTF-8"));

                    //receive response from aggregation server
                    String response = is.readLine();
                    System.out.println(response);

                }else if(line.equals(null)){
                    os.println(line);
                    os.flush();
                    System.out.println("Please enter 'PUT OR 'QUIT'");

                }else if(line.equals("QUIT")){
                    System.out.println("Connection closed...");
                    socket.close();
                }
                else{
                    os.println(line);
                    os.flush();
                    System.out.println("Please enter 'PUT OR 'QUIT'");
                }
            }while(line.compareTo("QUIT")!=0);
            socket.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    //external thread to send heartbeat to AggregationServer
    static class ContentServer1Thread extends Thread implements Runnable {

        Socket socket;
        public ContentServer1Thread(Socket s)
        {
            socket = s;
        }

        public void run ()
        {
            try{
                int index = 1;

                socket.setKeepAlive(true);
                //if we send OxFF after 5000 sec, an exception will be thrown.
                socket.setSoTimeout(5000);
                while (true) {
                    if (index > 1000) { //loop 1000 times

                        System.out.println("has closed the connection!");
                        socket.close();
                        break;
                    }
                    index++;
                    socket.sendUrgentData(0xFF); // send heartbeat to check whether the socket is still connected
                    System.out.println("[heartbeat] Currently connected!");
                    Thread.sleep(12 * 1000);//thread sleep 12 seconds
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //read input file
    public String read(String read) throws IOException {
        try {
            //read file
            in = new BufferedReader(new FileReader(read));

            //after parsing, save to an xml file
            out = new StreamResult("data1.xml");

            initXML();
            String str;
            //process tag entry, because entry tag does not has colon it is unique
            boolean flag_entry = false;
            while ((str = in.readLine()) != null) {
                if (str.equals("entry") && flag_entry == false) {
                    flag_entry = true;
                    process_entry_start(str);
                    continue;
                }
                if (str.equals("entry") && flag_entry == true) {
                    flag_entry = false;
                    process_entry_end(str);
                    continue;
                }
                process(str);
            }
            in.close();
            closeXML();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String message = new String(Files.readAllBytes(Paths.get("data1.xml")));

//        System.out.println(message);
        return message;
    }

    // xml parsing JAXP + SAX
    public void initXML() throws TransformerConfigurationException, SAXException {

        SAXTransformerFactory tf = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
        th = tf.newTransformerHandler();
        Transformer transformer = th.getTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "10");
        //result
        th.setResult(out);
        atts = new AttributesImpl();
        th.startElement("", "", "feed xml:lang=\"en-US\" xmlns=\"http://www.w3.org/2005/Atom\"", atts);
    }

    public void process(String s) throws SAXException {

        String[] elements = s.split(":", 2);
        atts.clear();
        if(elements[0].equals("author")) {
            th.startElement("", "", elements[0], atts);
            th.startElement("", "", "name", atts);
            th.characters(elements[1].toCharArray(), 0, elements[1].length());
            th.endElement("", "", "name");
            th.endElement("", "", elements[0]);
        } else {
            th.startElement("", "", elements[0], atts);
            th.characters(elements[1].toCharArray(), 0, elements[1].length());
            th.endElement("", "", elements[0]);
        }
    }

    public void process_entry_start(String s) throws SAXException {
        atts.clear();
        th.startElement("", "", s, atts);
    }

    public void process_entry_end(String s) throws SAXException {
        atts.clear();
        th.endElement("", "",s);
    }

    public void closeXML() throws SAXException {
        th.endElement("","","feed");
        th.endDocument();  }
}
