package com.company;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.*;
import java.util.Random;

public class UDPTest {


    public  static void main(String[] args) throws IOException {
        int i; // We will be using a counter to analyze our test cases and i will be that value
        int serverPort = 1000; // this will be our port for our simulated server
        int client1Port = 1100; // We will have 2 clients this will represent client 1
        int client2Port = 1200; // This will represent client 2
        byte[] buf = new byte[1024];
        DatagramSocket mockServerSocket = new DatagramSocket(1000); // We create a socket for our servers and clients
        DatagramSocket mockClient1Socket = new DatagramSocket(1100);
        DatagramSocket mockClient2Socket = new DatagramSocket(1200);
        InetAddress localAddress = InetAddress.getByName("localHost"); // This is the local computer address

        DatagramPacket c1ClientSender = new DatagramPacket(buf, buf.length, localAddress, client1Port); // We have a packet that is ready to be sent to client 1 or 2
        DatagramPacket c2ClientSender = new DatagramPacket(buf, buf.length, localAddress, client2Port);
        DatagramPacket serverSender = new DatagramPacket("client 1".getBytes(), "client 1".getBytes().length, localAddress, serverPort); // We use this for our clients to send to the server.
        mockClient1Socket.send(serverSender); // we send a packet to client 1

        DatagramPacket c1In = new DatagramPacket(buf, buf.length);
        mockServerSocket.receive(c1In); // We receive that packet as client 1
        PacketHandler client1 = new PacketHandler(c1ClientSender,c1In, mockServerSocket); // we use that packet and create a packet handler

        DatagramPacket c2In = new DatagramPacket(buf, buf.length); // Same thing but with client 2
        mockClient2Socket.send(serverSender);
        mockServerSocket.receive(c2In);
        PacketHandler client2 = new PacketHandler(c2ClientSender, c2In, mockServerSocket);

        DatagramPacket c1sideIn = new DatagramPacket(buf, buf.length); // we pass these 2 into our test cases
        DatagramPacket c2sideIn = new DatagramPacket(buf, buf.length);
        mockServerSocket.send(c1ClientSender);
        mockServerSocket.send(c2ClientSender);



        mockClient1Socket.receive(c1sideIn);
        mockClient2Socket.receive(c2sideIn);

        System.out.println("Lost Segments test"); // We do a count to 10 but we lose the #7 packet and see if it gets resent.
        for(i=0; i<10; i++){
            final int j= i+1; // We tell our client what number to send
            Thread t1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        lostSegments(mockClient1Socket, serverSender, c1sideIn, j);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            t1.start();

            System.out.println("server received: " +client1.receivePacketData()); // Our server requests the number form our client.
            t1.stop();
        }

        System.out.println("Delayed Segments test"); // we set a delay time of 10 seconds to replicate a delay
        for(i=0; i<2; i++){
            final int j= i+1;
            Thread t1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        delayedSegments(mockClient1Socket, serverSender, c1sideIn, j);
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            t1.start();

            System.out.println("server received: " +client1.receivePacketData());
            t1.stop();
        }

        System.out.println("Out of Order Delivery"); // We send packets that are in the wrong order to represent the out of order delivery.
        for(i=0; i<3; i++){
            final int j= i+1;
            Thread t1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        outOfOrderDelivery(mockClient1Socket, serverSender, c1sideIn, j);
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            t1.start();

            System.out.println("server received: " +client1.receivePacketData());
            t1.stop();
        }
    }

    /**
     * This will wait for the server to make a request and then send 'i' back to the server, if we don't get teh right
     * confirmation code from the server we resend the packet so the server can get it.
     * @param clientSocket This will be our representative client socket
     * @param clOut This will be our out packet for this mock client
     * @param c1In This will be our receiving packet for this mock client
     * @param i This will be the number printed which we go up to 10
     * @throws IOException
     */
    public static void lostSegments(DatagramSocket clientSocket, DatagramPacket clOut, DatagramPacket c1In, int i) throws IOException {
        Random rand = new Random();
        clientSocket.receive(c1In); // We wait for the receiver to send a message

        String data = new String(c1In.getData(), 0, c1In.getLength());
        if(data.equals("ready")) {
            int x = rand.nextInt(900) + 100; //This will be our verification number
            String code = x + "";
            boolean sent = false;
            String output = i + ""; // We input a number from the client
            output = output + code;
            clOut.setData(output.getBytes()); // We set our packet data to send
            for (int j = 0; j < 20; j++) {
                System.out.println("Make an attempt");
                if (!((j == 0) &&(i == 7))) {
                    clientSocket.send(clOut); // Send to our server

                } else {
                    System.out.println("Skipped #7 for testing / Purposely lost packet");
                }
                clientSocket.setSoTimeout(5000);
                try {
                    clientSocket.receive(c1In);
                    String confirmationCode = new String(c1In.getData(), 0, c1In.getLength());
                    sent = confirmationCode.equals(code);
                } catch (SocketTimeoutException e) {
                    System.out.println("waited 5 seconds to send");
                }
                if (sent) {
                    break;
                }
            }
        }
    }

    /**
     * This will delay sending a packet replicating a segment delay, when a delay happens the server times out and makes another request but will eventually abandon it.
     * @param clientSocket This will be our representative client socket
     * @param clOut This will be our out packet for this mock client
     * @param c1In This will be our receiving packet for this mock client
     * @param i This will be the number printed which we go up to 10
     * @throws IOException
     * @throws InterruptedException
     */
    public static void delayedSegments(DatagramSocket clientSocket, DatagramPacket clOut, DatagramPacket c1In, int i) throws IOException, InterruptedException {
        Random rand = new Random();
        clientSocket.receive(c1In); // We wait for the receiver to send a message

        String data = new String(c1In.getData(), 0, c1In.getLength());
        if(data.equals("ready")) {
            int x = rand.nextInt(900) + 100; //This will be our verification number
            String code = x + "";
            boolean sent = false;
            String output = i + ""; // We input a number from the client
            output = output + code;
            clOut.setData(output.getBytes()); // We set our packet data to send
            for (int j = 0; j < 20; j++) {
                System.out.println("Wait 10 seconds before sending.");
                Thread.sleep(10000);
                clientSocket.send(clOut);
                clientSocket.setSoTimeout(5000);
                try {
                    clientSocket.receive(c1In);
                    String confirmationCode = new String(c1In.getData(), 0, c1In.getLength());
                    sent = confirmationCode.equals(code);
                } catch (SocketTimeoutException e) {
                    System.out.println("waited 5 seconds to send");
                }
                if (sent) {
                    break;
                }
            }
        }
    }

    /**
     * This will send the numbers 6,7,8 to the server, which the server will toss out because it only accepts data when
     * ready.
     * @param clientSocket This will be our representative client socket
     * @param clOut This will be our out packet for this mock client
     * @param c1In This will be our receiving packet for this mock client
     * @param i This will be the number printed which we go up to 10
     * @throws IOException
     * @throws InterruptedException
     */
    public static void outOfOrderDelivery(DatagramSocket clientSocket, DatagramPacket clOut, DatagramPacket c1In, int i) throws IOException, InterruptedException {
        Random rand = new Random();
        clientSocket.receive(c1In); // We wait for the receiver to send a message
        clOut.setData("6".getBytes());
        clOut.setData("7".getBytes());
        clOut.setData("8".getBytes());
        String data = new String(c1In.getData(), 0, c1In.getLength());
        if(data.equals("ready")) {
            int x = rand.nextInt(900) + 100; //This will be our verification number
            String code = x + "";
            boolean sent = false;
            String output = i + ""; // We input a number from the client
            output = output + code;
            clOut.setData(output.getBytes()); // We set our packet data to send
            for (int j = 0; j < 20; j++) {
                clientSocket.send(clOut);
                clientSocket.setSoTimeout(5000);
                try {
                    clientSocket.receive(c1In);
                    String confirmationCode = new String(c1In.getData(), 0, c1In.getLength());
                    sent = confirmationCode.equals(code);
                } catch (SocketTimeoutException e) {
                    System.out.println("waited 5 seconds to send");
                }
                if (sent) {
                    break;
                }
            }
        }
    }



}
