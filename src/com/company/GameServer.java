package com.company;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.*;

/**
 * Game Server starts the server for the game and controls all the connections
 * that go into port
 */
public class GameServer {
    /**
     *
     * @param args
     *
     * The main method runs our server and allows our clients to connect.
     */
    public  static void main(String[] args){
        int portNumber = 23;  // // This is our port for TCP
        int udpPortNumber = 32; //This is our port for UDP
        byte[] buf = new byte[1024];
        try(
                ServerSocket serverSocket = new ServerSocket(portNumber); // This creates our server on a specific port
                DatagramSocket udpSocket = new DatagramSocket(udpPortNumber); //
                ) {
            while(true) {// This while loop will help connect multiple games at once

                Thread tcpRun = new Thread(new Runnable() { // This thread takes care of our TCP run
                    @Override
                    public void run() {
                        try {
                            Socket TCPClient1=serverSocket.accept(); // Every odd number to connect is client 1
                            Socket TCPClient2=serverSocket.accept(); // Every even number to connect is client 2
                            new ConnectionThread( TCPClient1,TCPClient2).start(); // Start a new thread for our clients
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                tcpRun.start(); // Start the TCP thread so we can focus on our UDP run
                DatagramPacket c1InPacket = new DatagramPacket(buf, buf.length); // This datagram packet will let our server receive the appropriate client 1 packets
                udpSocket.receive(c1InPacket); // We receive a packet from player 1

                InetAddress client1Address = c1InPacket.getAddress(); // We save his address
                int client1Port = c1InPacket.getPort(); // we save his port
                String message = "Waiting for User 2"; // A message to player 1
                DatagramPacket c1OutPacket = new DatagramPacket(message.getBytes(), message.getBytes().length, client1Address, client1Port); // WE send a packet to player 1
                PacketHandler client1 = new PacketHandler(c1OutPacket, c1InPacket, udpSocket); // This Class handles our send and receive for packets from a certain Client
                client1.sendPacketData(message); // We send a packet to client 1 with Waiting for user 2 message
                DatagramPacket c2InPacket = new DatagramPacket(buf, buf.length); // This packet will be receiving
                // We send a packet to player 1
                while(c1InPacket.getPort() == client1Port & c1InPacket.getAddress() == client1Address){ // We make sure that we don't get a replica from client1
                    udpSocket.receive(c2InPacket); // This should receive client2 packet
                    client1Port = c2InPacket.getPort(); // We change these values to make sure we got data from the right client.
                    client1Address= c2InPacket.getAddress();
                }
                InetAddress client2Address = c2InPacket.getAddress(); // This will get the client address
                int client2Port = c2InPacket.getPort(); // This will get the client 2 port

                DatagramPacket c2OutPacket = new DatagramPacket(message.getBytes(), message.getBytes().length, client2Address, client2Port); // We have a packet for sending out to client 2
                PacketHandler client2 = new PacketHandler(c2OutPacket, c2InPacket, udpSocket); // create packet handler for client 2
                new UdpConnectionThread(client1, client2).start(); // We start our thread for UDP connection
                //break;
                while(!udpSocket.isClosed()){ // UDP cannot have multiple sockets on the same port so we keep this infinite loop

                }



            }
        } catch(IOException | InterruptedException e) {
            System.out.println("Exception caught when trying to listen on port " + udpPortNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }

}
