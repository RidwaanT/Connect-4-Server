package com.company;

public class Connect4 {

    final int boardWidth=7;
    final int boardHeight=6;
    int totalMovesPlayed;
    char[][] board;

    // initializes the real board and sets plays to 0
    public Connect4(){
        board=new char[boardHeight][boardWidth];
        totalMovesPlayed=0;
    }

    public void printBoard(){
        for(int i=0;i<board.length;i++){
            for(int j=0;j<board[0].length;j++){
                if(board[i][j] == 0)
                    System.out.print("_  ");
                else
                    System.out.print(board[i][j]+"  ");
            }
            System.out.println();
        }
        for(int i=0;i<boardWidth;i++)
            System.out.print("*  ");
        System.out.println();

        for(int i=0;i<boardWidth;i++)
            System.out.print(i+"  ");
        System.out.println();
    }

    public String boardToString(){
        String strBoard = "";
        for(int i=0;i<board.length;i++){
            for(int j=0;j<board[0].length;j++){
                if(board[i][j] == 0) {
                    //System.out.print("_  ");
                    strBoard += "_  ";
                }
                else {
                    strBoard += board[i][j] + "  ";
                    //System.out.print(board[i][j] + "  ");
                }
            }
            strBoard+= "\n";
            //System.out.println();
        }
        for(int i=0;i<boardWidth;i++) {
            strBoard += "*  ";
            //System.out.print("*  ");
        }
        strBoard+= "\n";
        //System.out.println();

        for(int i=0;i<boardWidth;i++) {
            strBoard+= i + "  ";
            //System.out.print(i + "  ");
        }
        //System.out.println();
        strBoard+= " \n";

        return strBoard;
    }

    public boolean playMove(int column, char playerNum){
        int i=0;
        for(i=0;i<boardHeight;i++){
            if(board[i][column] == '1' || board[i][column] == '2'){
                board[i-1][column]=playerNum;
                break;
            }
        }
        if(i == boardHeight)
            board[i-1][column]=playerNum;

        totalMovesPlayed++;
        return isConnected(i-1,column);
    }
    public boolean isPlayable(int column){
        return board[0][column] == 0;
    }
    public boolean isFull() { return totalMovesPlayed == (boardHeight)*(boardWidth);}
    public boolean isConnected(int x, int y) {
        int num = board[x][y];
        int count = 0;
        int i = y;

        //HORIZONTAL
        while(i<boardWidth && board[x][i] == num){
            count++;
            i++;
        }
        i=y-1;

        while(i>0 && board[x][i] == num){
            count++;
            i--;
        }
        if(count == 4) {
            return true;
        }

        //Vertical
        count=0;
        int j=x;
        while (j<boardHeight && board [j][y] == num){
            count++;
            j++;
        }
        if (count == 4){
            return true;
        }

        //SECONDARY DIAGONAL
        count=0;
        i=x;
        j=y;
        while (i<boardWidth && j<boardHeight && board[j][i] == num){
            count++;
            i++;
            j++;
        }
        i=x-1;
        j=y-1;
        while (i>=0 && j>0 && board[i][j] == num){
            count++;
            i--;
            j--;
        }
        if (count==4)return true;

        //LEADING DIAGONAL
        count=0;
        i=x;
        j=y;
        while (i<boardWidth && j<boardHeight &&  j>=0 && board[j][i] == num){
            count++;
            i++;
            j--;
        }
        i=x-1;
        j=y+1;
        while(i>0 && j<boardHeight && board[j][i] == num){
            count++;
            i--;
            j++;
        }
        return count == 4;
    }






}
