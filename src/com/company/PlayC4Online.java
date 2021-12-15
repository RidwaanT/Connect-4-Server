package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;


public class PlayC4Online {

    private String start = "GAME START!!!";
    PrintWriter out1; // our player1 out
    PrintWriter out2; // player 2 out
    public PlayC4Online(PrintWriter out1, PrintWriter out2, BufferedReader in1, BufferedReader in2) throws IOException {
        this.out1 = out1;
        this.out2 = out2;
        Connect4 c4= new Connect4(); // This holds our boards and some game methods


        out1.println(start);
        out2.println(start);
        out1.println(c4.boardToString()); // we send our board as a string to our clients.
        out2.println(c4.boardToString());
        outer:
        while(true) {
            int column=0; // this will represent which column we put our board in
            //player 1
            while(in1.ready()){  // We clear premature entries
                in1.readLine();
            };
            out1.println("\n\n Player 1 play a column from 0-6");
            out2.println("\n\nwaiting for Player 1");
            column=Integer.parseInt(in1.readLine()); // we parse our input into a string
            while(column>6 || column<0 || column % 1 !=0){ //checks for the proper number
                out1.println("\n\n Player 1 play a column from 0-6, you entered an invalid number");
                column=Integer.parseInt(in1.readLine()); // we parse our input into a string
            }
            if(humanMove2('1', column, c4)) break outer; // we play the move

            out1.println(c4.boardToString());  // we send a board to both players
            out2.println(c4.boardToString());

            //Player 2
            while(in2.ready()){  // We clear any premature entries
                in2.readLine();
            };
            out2.println("\n\n Player 2 play a column from 0-6");
            out1.println("\n\nwaiting for Player 2");
            column=Integer.parseInt(in2.readLine()); // The column will be based on what the player inputs
            while(column>6 || column<0 || column % 1 !=0){ //checks for the proper number
                out2.println("\n\n Player 2 play a column from 0-6, you entered an invalid number");
                column=Integer.parseInt(in2.readLine()); // we parse our input into a string
            }
            if(humanMove2('2', column, c4)) break outer;

            out1.println(c4.boardToString()); // WE send boards to both players
            out2.println(c4.boardToString());

            if(c4.isFull()) {
                out1.println("Game drawn. Both of you suck at this!!! ");
                out2.println("Game drawn. Both of you suck at this!!! ");
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
    public boolean humanMove2(char playNum,int column, Connect4 c4){

        if(playNum==1){ // If we're using player 1
            out1.println("\n\nPlayer " +playNum+ " play"); // we let them know to play
        } else {
            out2.println("\n\nPlayer " +playNum+ " play");
        }
        while (true) {
            if (c4.isPlayable(column)) { //Check if the move is playable
                if (c4.playMove(column, playNum)) {// we play the move and if it's a winning move.
                    String output = c4.boardToString();
                    out1.println(output); // we send both players the boards
                    out2.println(output);
                    out1.println("\n\n player " + playNum + " wins!!!"); // let both players know who won
                    out2.println("\n\n player " + playNum + " wins!!!");
                    return true; // let the caller know it is a winning play
                }
                return false; // not a winning play
            } else {
                if(playNum==1){
                    out1.println("Column " + column + "is already full!!");// that column is full so we let them try again.
                } else {
                    out2.println("Column " + column + "is already full!!");
                }
            }
        }
    }
}
