package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * This connection thread allows the server to run the game client from inputs from the clients.
 */
public class ConnectionThread extends Thread{
    private Socket client1, client2; // The sockets to transfer information

    public ConnectionThread(Socket c1, Socket c2){
        client1 = c1; client2 = c2; // we send the sockets for both our clients
    }

    public void run() {
        try (
                PrintWriter out1 = new PrintWriter(client1.getOutputStream(), true); //  this lets us send string to our client1
                PrintWriter out2 = new PrintWriter(client2.getOutputStream(), true); // this lets us send string to our client2.
                BufferedReader in1 = new BufferedReader(new InputStreamReader(client1.getInputStream())); // We receive strings form our clients
                BufferedReader in2 = new BufferedReader(new InputStreamReader(client2.getInputStream()));
                ) {
            out1.println("Welcome to Connect 4, you are player 1"); // Welcoming our players
            out2.println("Welcome to Connect 4, you are player 2"); // Welcoming our players.


            while(true){
                PlayC4Online game = new PlayC4Online(out1, out2, in1, in2); // This class will run our Connect 4 game and send outs and reads to buffer.
            }

        } catch(IOException e) {
            System.out.println("There is an issue with the buffered reader");

        }
    }
}
