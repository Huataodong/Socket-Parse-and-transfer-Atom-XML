package assignment2;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class AggregationServer {
        public static void main(String[] args) throws IOException {
            
            System.out.println("AggregationServer listening....");
            ServerSocket serversocket = new ServerSocket(4567);
            Socket socket = new Socket();

            //check if there is new client to connect
            //multi-clients
            while (true)
            {
                try
                {
                    socket = serversocket.accept();
                    System.out.println("connection established");
                    System.out.println("status 201 - HTTP_CREATED");
                    //thread to receive message from clients -- multi clients
                    ServerThread st = new ServerThread(socket);
                    st.start();

                    //thread to send heartbeat
                    heartbeatThread htt = new heartbeatThread(socket);
                    htt.start();

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    System.out.println(socket.getRemoteSocketAddress());
                }
            }
        }
        //local lamport clock timestamp, local global counter
        private static int counter = 0;
        public static synchronized void increment()
        {
            counter++;
        }
        public static synchronized int getCounter()
        {
            return counter;
        }
}

class ServerThread extends Thread implements Runnable {

        String line = null;
        Socket socket;

        public ServerThread(Socket s) throws IOException {
            socket = s;
        }
        //call local counter for Local Time Stamp
        public static int LocalTimeStamp()
        {
            AggregationServer.increment();
            AggregationServer.getCounter();
            return AggregationServer.getCounter();
        }

        public void run ()
        {
            try
            {
                do{
                    //get input from clients
                    BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    //send message to clients
                    PrintWriter os = new PrintWriter(socket.getOutputStream());
                    StringBuilder sb = new StringBuilder();
                    line = is.readLine();
                    //---------------------------receive message from ContentServer-----------------------------
                    //ContentServer will send xml message to AggregationServer
                    //read the first line, if it equals to 'PUT' means it is from ContentServer
                    if(line.equals("PUT"))
                    {
                        System.out.println("Status 200 -- the 'PUT' request has succeeded");
                        os.println("AggregationServer received");
                        os.flush();
                        //get content server address for remove the items if this content server dead
//                        sb.append("\n");
                        sb.append(socket.getRemoteSocketAddress());
                        sb.append("\n");

                        //get lamport clock timestamp from ContentServer
                        line = is.readLine();
                        sb.append(line);
                        line = is.readLine();

                        int CStimestamp = Integer.parseInt(line);
                        int LocalTimeStamp = ServerThread.LocalTimeStamp();
                        //compare ContentServer timestamp with local then update
                        if (CStimestamp <LocalTimeStamp)
                        {
                            CStimestamp  = LocalTimeStamp+1;
                        }
                        else {
                            CStimestamp  = CStimestamp +1;
                        }
                        sb.append(CStimestamp );
                        //http headers
                        sb.append("\n");
                        line = is.readLine();
                        sb.append(line);
                        sb.append("\n");
                        line = is.readLine();
                        sb.append(line);
                        sb.append("\n");
                        line = is.readLine();
                        sb.append(line);
                        sb.append("\n");
                        line = is.readLine();
                        sb.append(line);
                        sb.append("\n");

                        //append xml message from ContentServer
                        for (int i = 0; i < line.length(); i++)
                        {
                            line = is.readLine();
                            sb.append(line);
                            sb.append("\n");
                        }
                        sb.append(socket.getRemoteSocketAddress());
                        sb.append("\n");
//                        sb.append("\n");
                        // so every xml message from ContentServer will be append in aggregation.xml
                        String str = sb.toString();
                        // output on the terminal to see what has been put
                        System.out.println("\n");
                        //display xml message PUT by content server
                        System.out.println(str);
                        File f1= new File("aggregation.xml");
                        byte[] buff=str.getBytes();
                        FileOutputStream oo= new FileOutputStream(f1,true);
                        oo.write(buff);//Write the data in the buffer to the file (aggregation.xml)
                        oo.flush();
                    }

                    //---------------------------send XML message to GETClient-----------------------------
                    // if the request is "GET", then aggragation can know it is from GETClient
                    else if(line.equals("GET")) {
                        // read XML message from aggregation.xml to save into a String
                        Scanner input = new Scanner(new File("aggregation.xml"));
                        StringBuilder toClient = new StringBuilder();

                        while (input.hasNextLine()){
                            toClient.append(input.nextLine());
                            toClient.append("\n");
                        }
                        System.out.println("Status 200 --- the 'GET' request has succeeded");
                        //send XML to GETClient
                        //send lamport clock time stamp
                        int LocalTimeStamp = ServerThread.LocalTimeStamp();
                        os.println("aggregation.xml");
                        os.flush();
                    //--------------else------------------------
                    }else if(line.equals("")){
                        System.out.println("Status 204 --- no content");
                    }else {
                        System.out.println("Status 400 --- the Server cannot or will not process the request");
                    }

                }while (!line.equals("QUIT"));

            } catch (Exception e) {
                //thrown the exception if ContentServer is disconnected by catch "this.line" is null
                String address = socket.getRemoteSocketAddress().toString();
                System.out.println("The ContentServer with address "+address+" is dead");
                System.out.println("The message send by "+address+" has been removed");
                e.printStackTrace();

                // if content server has not communicated with for 12 seconds, then it will thrown exception
                // or if we close content server
                // remove any items in the feed that have come from that disconnected ContentServer
                // to identify which is dead by recognising its address
                try{
                    FileReader fileReader = new FileReader("aggregation.xml");
                    BufferedReader br = new BufferedReader(fileReader);
                    StringBuffer stringBuffer = new StringBuffer();
                    String line;
                    boolean flag = true;
                    while ((line = br.readLine()) != null) {
                        if(line.contains(address)) {
                            if (flag) flag = false;
                            else flag = true;
                        }else if(flag){
                            stringBuffer.append(line);
                            stringBuffer.append("\n");
                        }
                    }
                    String myString = stringBuffer.toString();

                    //write to aggregation.xml
                    BufferedWriter writer = new BufferedWriter(new FileWriter("aggregation.xml"));
                    writer.write(myString);
                    fileReader.close();
                    writer.close();

                }catch(Exception c){
                    c.printStackTrace();
                }
            }

        }
}

class heartbeatThread extends Thread implements Runnable {

    Socket socket = null;
    public heartbeatThread(Socket s) {
        this.socket = s;
    }
    public void run() {
        try {
            int index = 1;
            while (true) {
                // loop 1000 times
                if (index > 1000) {
                    socket.close();
                    System.out.println("AggregationServer closed");
                    break;
                }
                index++;
                Thread.sleep(12 * 1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}