package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PlayC4 {
    public static void main(String[] args)throws IOException {
        Connect4 c4= new Connect4();

        BufferedReader br= new BufferedReader(new InputStreamReader(System.in));
        System.out.println("WELCOME TO CONNECT FOUR!!!");

        outer:
        while(true){
            int column=0;
            //Player 1
            while (true){
                System.out.println("\n\n Player 1 play a column from 0-6");
                column=Integer.parseInt(br.readLine());
                System.out.println("This is the column" + column);
                if(c4.isPlayable(column)){
                    if(c4.playMove(column, '1')){
                        System.out.println("before we print");
                        c4.printBoard(); // IMPORTANT FOR THE NETWORK
                        System.out.println("board printed");
                        System.out.println("\n\nPlayer 1 wins!!!"); // DOUBLE CHECK THIS
                        break outer;
                    }
                    break;
                } else System.out.println("Column " +column+ " is already full!!");
            }
            c4.printBoard();
            // Player 2
            while(true){
                System.out.println("\n\nPlayer 2 play");
                column=Integer.parseInt(br.readLine());
                if(c4.isPlayable(column)){
                    if(c4.playMove(column, '2')){
                        c4.printBoard();
                        System.out.println("\n\n Player 2 wins!!!");
                        break outer;
                    }
                    break;
                } else System.out.println("Column " +column+ "is already full!!");
            }
            c4.printBoard();

            if(c4.isFull()) {
                System.out.print("Game drawn. Both of you suck at this!!! ");
                break;
            }
        }
    }


    public static void humanMove(char playNum,int column, Connect4 c4){

        System.out.println("\n\nPlayer " +playNum+ " play");
        if(c4.isPlayable(column)){
            if(c4.playMove(column, playNum)){
                c4.printBoard();
                System.out.println("\n\n player " +playNum+ "wins!!!");
                return;
            }
            return;
        } else System.out.println("Column " +column+ "is alrady full!!");
    }

}
