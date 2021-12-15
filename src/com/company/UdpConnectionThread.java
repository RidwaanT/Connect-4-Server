package com.company;

import java.io.*;
import java.net.*;
import java.util.Random;

public class UdpConnectionThread extends Thread {
    private PacketHandler client1;
    private PacketHandler client2;

    public UdpConnectionThread(PacketHandler client1, PacketHandler client2) throws IOException {

        this.client1 = client1;
        this.client2 = client2;
    }

    public void run() {
        try {
            while(true){
                PlayC4UDP game = new PlayC4UDP(client1, client2); // This class will run our Connect 4 game and send outs and reads to buffer.
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
