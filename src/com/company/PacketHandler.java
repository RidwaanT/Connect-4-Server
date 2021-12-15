package com.company;

import java.io.IOException;
import java.net.*;
import java.util.Random;

public class PacketHandler {
    DatagramPacket cOut;
    DatagramPacket cIn;
    DatagramSocket socket;
    byte[] buf = new byte[1024];

    /**
     *
     * @param c1Out This will be the packet that we send out to the client
     * @param c1In This will be the packet we use to receive from the client
     * @param socket This will be the global socket
     */
    public PacketHandler(DatagramPacket c1Out, DatagramPacket c1In, DatagramSocket socket ){
        this.cOut = c1Out;
        this.cIn = c1In;
        this.socket = socket;
    }

    /**
     * This method will attempt to send a packet data out, It handles lost segments, delayed segments, out of order segments. As well as flow control
     * @param data // This will be a string of the information we want to send.
     * @throws IOException
     * @throws InterruptedException
     */
    public boolean sendPacketData(String data) throws IOException, InterruptedException {
        Random rand = new Random();
        boolean sent = false;
        int x = rand.nextInt(900) + 100; //This will be our verification number
        String code = x + "";
        int tries = 0;
        while(!sent){
            data = data + code; // we add our verification number to our data and have the client strip it
            cOut.setData(data.getBytes()); // We set our output datagram to the new data with our verification number
            socket.send(cOut); // We send this datagram out
            for(int i=0; i<3; i++){ // Try 5 times to get a confirmation
                socket.setSoTimeout(20000); // We set the socket to wait 5 seconds to receive a datagram
                InetAddress clientAddy= cIn.getAddress();
                SocketAddress clientSocketAddy = cIn.getSocketAddress();
                try{
                    socket.receive(cIn);// We receive a datagram
                } catch (SocketTimeoutException e){
                    System.out.println("Waited 20 seconds to receive"); // We let the user name we waited 20 seconds for a message
                }

                while(!clientAddy.equals(cIn.getAddress()) && !clientSocketAddy.equals(clientSocketAddy)) { // We want to make sure we get it from the right client
                    try{
                        socket.receive(cIn);// We receive a datagram
                    } catch (SocketTimeoutException e){
                        System.out.println("Waited 20 seconds to receive");
                    }
                }
                String confirmationCode = new String(cIn.getData(), 0, cIn.getLength()); // We get the confirmation code
                if (confirmationCode.equals(code)) { // If it matches we can complete the send packet method.
                    sent = true;
                    break;
                }
            }
            if(tries >=5){// We will only try 5 times
                System.out.println("Failed to send");
                break;
            }
            tries++;
        }
        return sent;
    }


    /**
     * This receives a packet and once received will return the string of the packet but also sends out a confirm
     * packet to let the sender know we received our message.\
     * @return The data we get from the client without the code
     *  @throws IOException
     */
    public String receivePacketData() throws IOException {
        boolean received = false;
        String text = null;  //Should I send a request message first
        InetAddress clientAddy = cIn.getAddress();
        SocketAddress clientSocketAddy = cIn.getSocketAddress();
        cOut.setData("ready".getBytes()); // WE send this ready message so the client knows to send to us
        int tries = 0;
        while(!received) { // If we
            socket.send(cOut); // We send the client a message that we're ready
            socket.setSoTimeout(1); // We clear any old or out of order messages before we accept.
            try{
                socket.receive(cIn); // We try to receive the message
            } catch (SocketTimeoutException e){}

            for (int i = 0; i < 5; i++) {
                socket.setSoTimeout(20000); // We sset the timeout to 20 seconds to wait.
                try {
                    socket.receive(cIn);// We receive a datagram
                } catch (SocketTimeoutException e) {
                    System.out.println("Waited 20 seconds to receive");
                }
                if (cIn.getAddress().equals(clientAddy) && cIn.getSocketAddress().equals(clientSocketAddy)) {
                    received = true; // If we have gotten a message that matches the address than we can continue.
                    break;
                }
            }
            if(tries>=5){
                break;
            }
            tries++;
        }
        if(received){
            String data = new String(cIn.getData(), 0, cIn.getLength()); // We receive the data which should be text + 3 digit code
            String code = data.substring(data.length()-3); // We take the code from the string which is 3 digits
            text = data.substring(0, data.length()-3); //we get the data without the code.
            cOut.setData(code.getBytes()); // We set our data to the code for the receiver to receive.
            socket.send(cOut);
            return text;
        } else{
            return "Not Received";
        }

    }



}
