package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.*;
import java.io.*;

public class GUIClient {

    public static void main(String [] args) {
        String hostName="localhost";
        int portNumber=23;

        try (
                Socket conn = new Socket(hostName, portNumber);
                PrintWriter sockOut = new PrintWriter(conn.getOutputStream(),true);
                BufferedReader sockIn = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        ) {
            JTextArea textBox = new JTextArea();
            JLabel message1 = new JLabel(); // We create different messaging boxes to keep track of data
            JLabel message2 = new JLabel();
            JLabel message3 = new JLabel();
            String m1= ""; // we have temporary strings so we can move them down the list
            String m2= "";
            String m3= "";
            createFrame(sockOut, textBox, message1, message2, message3);
            while (true) {
                if(sockIn.ready()) { // only read if we have an input
                    String message = sockIn.readLine() + "\n"; // \n for a new line
                    if(message.length() == 22) { // if we have a line that matches the length of our board input we can process the board info.
                        for(int i=0; i<11; i++) {
                            if(i==0) {
                                textBox.append(message); //we add the board details to the output
                            } else {
                                textBox.append(sockIn.readLine() + "\n");
                            }
                            textBox.setCaretPosition(textBox.getDocument().getLength()); // we scrolldown to the last input
                        }
                    } else {
                        m3=m2; // we used this to have the messages move downards.
                        m2=m1;
                        m1=message;

                        message3.setText("3: " +m3);
                        message2.setText("2: " + m2);
                        message1.setText("Recent: " +m1);

                    }
                }
            }
        } catch (UnknownHostException e) {
            System.out.println("I think there's a problem with the host name.");
        } catch (IOException e){
            System.out.println("Had an IO error for the connection.");
        }
    }

    public static void createFrame(PrintWriter sockOut, JTextArea textBox, JLabel message1, JLabel message2, JLabel message3){
        JFrame f=new JFrame("Connect 4");
        JPanel panel=new JPanel();
        JButton[] buttons = new JButton[7]; // an array of buttons
        for(int i=0; i<7;i++){ // we label and pass the functions to each button.
            buttons[i] = new JButton(i+"");
            buttons[i].setActionCommand(i+"");
            int valueToPass = i;
            buttons[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e){
                    sockOut.println(valueToPass); // when the button is pressed it does a sockout.
                }
            });
            panel.add(buttons[i]);// we put the buttons in the panel
        }

        panel.setBounds(40,80,400,50);
        panel.setBackground(Color.gray);
        JScrollPane scrollBar = new JScrollPane(textBox); // put a scrollpanel on the text box
        textBox.setEditable(false);
        scrollBar.setBounds(80,100, 400,250);
        scrollBar.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        message1.setText("Waiting for another player"); // we add message boxes.
        message1.setBounds(50,30,300,30);
        message2.setText("");
        message2.setBounds(50,50,300,30);
        message3.setText("");
        message3.setBounds(50,70,300,30);

        f.add(message1);
        f.add(message2);
        f.add(message3);
        f.add(scrollBar);
        f.add(panel);
        f.setSize(600,400);//400 width and 500 height
        f.setVisible(true);//making the frame visible
    }
}
