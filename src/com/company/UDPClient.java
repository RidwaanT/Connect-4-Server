package com.company;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.*;
import java.util.Random;
import java.util.Scanner;

public class UDPClient {

    public static void main(String [] args) throws IOException {
        InetAddress address = InetAddress.getByName("localhost"); // Localhost
        Scanner userInput = new Scanner(System.in); // We use this to send our input
        int servPort = 32; // This is the port number we set for the game on UDP
        Random rand = new Random();
        int cliPort = rand.nextInt(1899) +1000; // We pick a random port number for the client there is a 1 in 2000 chance we get duplicate ports and a break.
        System.out.println(cliPort); // So we can see if the ports match.
        byte[] buf = new byte[1024];
        String start = "start"; // We let the server know that we're ready
        DatagramPacket sender = new DatagramPacket(start.getBytes(), start.getBytes().length, address, servPort); // We set up the packet that we'll be sending start with
        DatagramPacket receiver = new DatagramPacket(buf, buf.length); // This will be our receiver packet
        DatagramSocket cliSocket = new DatagramSocket(cliPort); // This will be the socket we use to get information
        cliSocket.send(sender); // We send start to the server
        while (true){
            cliSocket.receive(receiver); // We wait for the receiver to send a message
            String data = new String(receiver.getData(), 0, receiver.getLength());
            if(data.equals("ready")){ //If the receiver tells us it's ready for an input we send
                int x = rand.nextInt(900) + 100; //This will be our verification number
                String code = x + "";
                boolean sent = false;
                String output = userInput.nextLine(); // We input a number from the client
                output = output + code; // we add our code to the message so the sever can confirm.
                sender.setData(output.getBytes()); // We set our packet data to send
                for(int i=0; i<20; i++) { // We try 20 times to send to the server under worst case circumstances.
                    cliSocket.send(sender); // Send to our server
                    cliSocket.setSoTimeout(5000);
                    try{
                        cliSocket.receive(receiver); // We wait for a confirmation code after we sent
                        String confirmationCode = new String(receiver.getData(), 0, receiver.getLength());
                        sent = confirmationCode.equals(code); // if the code matches we can stop retrying since everything worked.
                    } catch(SocketTimeoutException e){
                        System.out.println("waited 5 seconds"); // 5 saeconds passed and we didn't get any data
                    }
                    if(sent){
                        System.out.println("successful send"); // We had asuccessful message sent.
                        break;
                    }
                }
            } else{ // If we get data that's just meant to be printed we just print it
                String code = data.substring(data.length()-3); // We seperate the code from our received data.
                String input = data.substring(0, data.length()-3); // We let the input = to the actual data.
                System.out.print(input); // we print it.
                sender.setData(code.getBytes()); // we set the code for the sender so they know we received the right message.
                cliSocket.send(sender); // we send to the sender.   
            }
            cliSocket.setSoTimeout(0);
        }




    }
}
