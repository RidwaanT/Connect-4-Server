package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;


public class PlayC4UDP {

    private String start = "\n GAME START!!! \n";
    PacketHandler client1;
    PacketHandler client2;
    public PlayC4UDP( PacketHandler client1, PacketHandler client2) throws IOException, InterruptedException {

        this.client1 = client1;
        this.client2 = client2;

        Connect4 c4= new Connect4(); // This holds our boards and some game methods
        client1.sendPacketData(start);
        client2.sendPacketData(start);
        client1.sendPacketData(c4.boardToString());
        client2.sendPacketData(c4.boardToString());
        outer:
        while(true) {
            int column=0; // this will represent which column we put our board in
            //player 1
            client1.sendPacketData("\n\n Player 1 play a column from 0-6:");
            client2.sendPacketData("\n\nwaiting for Player 1");
            column=Integer.parseInt(client1.receivePacketData()); // we parse our input into a string
            while(column>6 || column<0 || column % 1 !=0){ //checks for the proper number
                client1.sendPacketData("\n\n Player 1 play a column from 0-6, you entered an invalid number");
                column=Integer.parseInt(client1.receivePacketData()); // we parse our input into a string
            }
            if(humanMove2('1', column, c4)) break outer; // we play the move

            client1.sendPacketData("\n" +c4.boardToString());
            client2.sendPacketData("\n" +c4.boardToString());
            //player 2
            client2.sendPacketData("\n\n Player 2 play a column from 0-6:");
            client1.sendPacketData("\n\nwaiting for Player 2");
            column=Integer.parseInt(client2.receivePacketData());// The column will be based on what the player inputs
            while(column>6 || column<0 || column % 1 !=0){ //checks for the proper number
                client2.sendPacketData("\n\n Player 2 play a column from 0-6, you entered an invalid number");
                column=Integer.parseInt(client2.receivePacketData());
            }
            if(humanMove2('2', column, c4)) break outer;
            client1.sendPacketData("\n" +c4.boardToString());
            client2.sendPacketData("\n" +c4.boardToString());

            if(c4.isFull()) {
                client1.sendPacketData("Game drawn. Both of you suck at this!!! ");
                client2.sendPacketData("Game drawn. Both of you suck at this!!! ");
                break;
            }

        }
    }

    /**
     *
     * @param playNum Which player 1 or 2
     * @param column Which column are we playing?
     * @param c4 The board being played
     * @return
     */
    public boolean humanMove2(char playNum,int column, Connect4 c4) throws IOException, InterruptedException {

        if(playNum==1){ // If we're using player 1
            client1.sendPacketData("\n\nPlayer " +playNum+ " play"); // we let them know to play
        } else {
            client2.sendPacketData("\n\nPlayer " +playNum+ " play"); // we let them know to play
        }
        while (true) {
            if (c4.isPlayable(column)) { //Check if the move is playable
                if (c4.playMove(column, playNum)) {// we play the move and if it's a winning move.
                    String output = "\n" +c4.boardToString();
                    client1.sendPacketData(output); // we send both players the boards
                    client2.sendPacketData(output);
                    client1.sendPacketData("\n\n player " + playNum + " wins!!!"); // let both players know who won
                    client2.sendPacketData("\n\n player " + playNum + " wins!!!");
                    return true; // let the caller know it is a winning play
                }
                return false; // not a winning play
            } else {
                if(playNum==1){
                    client1.sendPacketData("Column " + column + "is already full!!");// that column is full so we let them try again.
                } else {
                    client2.sendPacketData("Column " + column + "is already full!!");
                }
            }
        }
    }
}
