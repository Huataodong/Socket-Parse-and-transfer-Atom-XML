package assignment2;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class GETClient2 {
    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {

        Socket socket = new Socket("localhost", 4567);
        System.out.println("GETClient start. Enter QUIT to end");
        System.out.println("Enter GET to get message from AggregationServer");

        //input request
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        //send message to AggregationServer
        PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
        //receive response from AggregationServer
        BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String line;

        try {
            int count = 1;
            do {
                line = input.readLine();

                if (line.equals("GET")) {
                    //send "GET" request to AggregationServer
                    printWriter.println(line);
                    printWriter.flush();

                    //response from AggregationServer
                    String response = is.readLine();
                    BufferedReader br = new BufferedReader(new FileReader(response));

                    // failure-tolerant: send request to AggregationServer, if no response 3 times, then close socket
                    if (br.readLine() == null) {
                        count++;
                        System.out.println("no response");
                        if (count == 3) {
                            socket.close();
                        }
                        // else if has response, go to parse xml to txt
                    } else {
                        StringBuilder sb = new StringBuilder();
                        File dataFile = new File(response);
                        Scanner scanner = new Scanner(dataFile);

                        while (scanner.hasNext()) {
                            String lineOfText = scanner.nextLine();
                            //remove txt headers to parse xml. see aggregation.xml
                            if (lineOfText.startsWith("/") || (lineOfText.startsWith("L")) || (lineOfText.startsWith("P")) ||
                                    (lineOfText.startsWith("U")) || (lineOfText.startsWith("C"))) {
                                continue;
                            }
                            sb.append(lineOfText);
                            sb.append("\n");
                        }

                        String str = sb.toString();
                        String message = new GETClient2().parseXML(str);
                        //output the result of xml to txt
                        System.out.println(message);
                    }
                }
                else if (line.equals(null)) {
                    printWriter.println(line);
                    printWriter.flush();
                    System.out.println("Please enter 'GET' OR 'QUIT'");
                } else if (line.equals("QUIT")) {
                    System.out.println("Connection closed...");
                    socket.close();
                } else {
                    printWriter.println(line);
                    printWriter.flush();
                    System.out.println("Please enter 'GET' OR 'QUIT'");
                }
            } while (!line.equals("QUIT")) ;
        }catch(Exception e)
        {
            e.printStackTrace();
        }

    }


    String parseXML(String str) throws ParserConfigurationException, IOException, SAXException {

        //split each feed then parse
        String[] test = str.split("((?<=</feed>)|(?=<feed>))");

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < test.length-1; i++) {

            test[i]=test[i].trim();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputStream inputStream = new ByteArrayInputStream(test[i].getBytes());
            org.w3c.dom.Document doc = builder.parse(inputStream);

            //parse xml to txt
            sb.append("------------------------------------");
            sb.append("\n");
            NodeList flowList = doc.getElementsByTagName("feed");
            for (int k = 0; k < flowList.getLength(); k++) {

                Node nNode = flowList.item(k);
                NodeList childList = flowList.item(k).getChildNodes();

                for (int j = 0; j < childList.getLength(); j++) {
                    Node childNode = childList.item(j);

                    if ("entry".equals(childNode.getNodeName())) {
                        sb.append("entry");
                        sb.append("\n");
                        if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element eElement = (Element) nNode;
                            sb.append("link : " + eElement.getElementsByTagName("link").item(0).getTextContent());
                            sb.append("\n");
                            sb.append("title : " + eElement.getElementsByTagName("title").item(0).getTextContent());
                            sb.append("\n");
                            sb.append("id : " + eElement.getElementsByTagName("id").item(0).getTextContent());
                            sb.append("\n");
                            sb.append("updated : " + eElement.getElementsByTagName("updated").item(0).getTextContent());
                            sb.append("\n");
                            sb.append("summary : " + eElement.getElementsByTagName("summary").item(0).getTextContent());
                            sb.append("\n");
                        }
                        sb.append("entry");
                        sb.append("\n");
                    }
                    if ("author".equals(childNode.getNodeName())) {
                        sb.append("author:" + childList.item(j).getTextContent().trim());
                        sb.append("\n");
                    }
                    if ("title".equals(childNode.getNodeName())) {
                        sb.append("title:" + childList.item(j).getTextContent().trim());
                        sb.append("\n");
                    }
                    if ("subtitle".equals(childNode.getNodeName())) {
                        sb.append("subtitle:" + childList.item(j).getTextContent().trim());
                        sb.append("\n");
                    }
                    if ("id".equals(childNode.getNodeName())) {
                        sb.append("id:" + childList.item(j).getTextContent().trim());
                        sb.append("\n");
                    }
                    if ("link".equals(childNode.getNodeName())) {
                        sb.append("link:" + childList.item(j).getTextContent().trim());
                        sb.append("\n");
                    }
                    if ("update".equals(childNode.getNodeName())) {
                        sb.append("update:" + childList.item(j).getTextContent().trim());
                        sb.append("\n");
                    }
                    if ("summary".equals(childNode.getNodeName())) {
                        sb.append("summary:" + childList.item(j).getTextContent().trim());
                        sb.append("\n");
                    }
                }
            }
        }
        System.out.println("\n");
        String string = sb.toString();
        return string;
    }
}
