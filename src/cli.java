package networksfinal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

/**
 * This client connects to a server to play battleship with another instance
 * of this client. In the maps 0 means it has not been fired upon, 1 means there
 * is an un-hit ship, 2 means it is hit but there was no ship, and 3 mean that
 * spot was hit and there was a ship there.
 * @author Joshua Willett
 */
public class cli {
    public static void main(String args[]) throws IOException {
            //Connects to server
        Socket sock = new Socket("127.0.0.1", 1234);
            //Creates in/out streams
        DataInputStream in = new DataInputStream(sock.getInputStream());
        DataOutputStream out = new DataOutputStream(sock.getOutputStream());
            //Input scanner
        Scanner sc = new Scanner(System.in);
            //Reads and assigns id
        String id = in.readUTF();
        System.out.println("Welcome to Battleship.");
        System.out.println("You are " + id + ".");
            //Create a board to show where the enemy has fired and where your
            //pieces are, and a board to show where you've fired
        int[][] board = new int[5][5];
        int[][] attack = new int[5][5];
            //Assign default values to all coordinates
        for (int n = 0; n < 5; n++) {
            for (int m = 0; m < 5; m++) {
                board[n][m] = 0;
                attack[n][m] = 0;
            }
        }
            //Assigns ships at seven random locations
        Random r = new Random();
        for (int i = 0; i < 7; i++) {
            int n = r.nextInt(5);
            int m = r.nextInt(5);
            if (board[n][m] == 1) {
                i--;
            }
            board[n][m] = 1;
        }
            //Sends the board to the server
        for (int n = 0; n < 5; n++) {
            for (int m = 0; m < 5; m++) {
                out.writeInt(board[n][m]);
            }
        }
            //Booleans for if the player won and if the loop should repeat
        boolean win = false;
        boolean repeat = true;
            //Loop until winner
        do {
                //Read in whos turn it is
            boolean turn = in.readBoolean();
                //Prints boards
            printArrays(board, attack);
            if (turn) {
                    //Initializes variables
                String input = null;
                int n = 0;
                int m = 0;
                boolean a = true;
                    //Loops until user input is correct
                while (a) {
                        //User input
                    System.out.print("Enter the coordinates for your attack: ");
                    input = sc.next();
                    input = input.toUpperCase();
                        //If too short
                    if (input.length() == 2) {
                            //If one or both of the values is outside scale
                        if (ifN(input) && ifM(input)) {
                            n = findN(input);
                            m = findM(input);
                                //If spot has already been attacked
                            if (attack[n][m] == 0) {
                                a = false;
                            }
                            else {
                                System.out.println("Error, spot has already been attacked.");
                            }
                        }
                        else {
                            System.out.println("Error, coordinates incorrect.");
                        }
                    }
                    else {
                        System.out.println("Error, too many characters.");
                    }
                }
                    //Send coordinates to server
                out.writeUTF(input);
                out.writeInt(n);
                out.writeInt(m);
                    //If it hit
                if (in.readBoolean()) {
                    System.out.println("You sunk their battleship!\n");
                    attack[n][m] = 3;
                }
                else {
                    System.out.println("You missed...\n");
                    attack[n][m] = 2;
                }
            }
            else {
                    //Wait message
                System.out.println("Wait for your opponent to attack.");
                    //Accept attack information
                String coordinates = in.readUTF();
                int n = in.readInt();
                int m = in.readInt();
                boolean ifHit = in.readBoolean();
                    //Notifies user if the attack was a hit
                System.out.print("Your opponent attacked " + coordinates + ",");
                if (ifHit) {
                    System.out.println("it's a hit.../n");
                    board[n][m] = 3;
                }
                else {
                    System.out.println("it missed!\n");
                    board[n][m] = 2;
                }
            }
                //Checks if the game is over
            if (in.readBoolean()) {
                win = in.readBoolean();
                repeat = false;
            }
        } while (repeat);
            //Accepts final scores
        int score1 = in.readInt();
        int score2 = in.readInt();
            //Win/loss message
        if (win) {
            System.out.println("You won!");
        }
        else {
            System.out.println("You lost...");
        }
        System.out.println("Player 1 had a score of " + score1 + 
                    ".\nPlayer 2 had a score of " + score2 + ".");
            //Close all
        sc.close();
        in.close();
        out.close();
        sock.close();
    }
    /**
     * This method takes in two arrays then prints them with the coordinates.
     * @param a
     * @param b
     */
    public static void printArrays(int[][] a, int[][] b) {
            //Prints boards
        System.out.println("  My Map       My Attacks");
        System.out.println("  1 2 3 4 5    1 2 3 4 5");
        System.out.println("  _________    _________");
        for (int n = 0; n < 5; n++) {
            switch (n) {
                case 0: System.out.print("A|");
                    break;
                case 1: System.out.print("B|");
                    break;
                case 2: System.out.print("C|");
                    break;
                case 3: System.out.print("D|");
                    break;
                case 4: System.out.print("E|");
                    break;
            }
            for (int m = 0; m < 5; m++) {
                System.out.print(a[n][m] + " ");
            }
            switch (n) {
                case 0: System.out.print(" A|");
                    break;
                case 1: System.out.print(" B|");
                    break;
                case 2: System.out.print(" C|");
                    break;
                case 3: System.out.print(" D|");
                    break;
                case 4: System.out.print(" E|");
                    break;
            }
            for (int m = 0; m < 5; m++) {
                System.out.print(b[n][m] + " ");
            }
            System.out.println();
        }
    }
    /**
     * Takes in the coordinate string and determines if the n value is within
     * scope.
     * @param a
     * @return boolean b
     */
    public static boolean ifN(String a) {
        boolean b = false;
            //b to true if within scope
        switch (a.charAt(0)) {
            case 'A': b = true;
                break;
            case 'B': b = true;
                break;
            case 'C': b = true;
                break;
            case 'D': b = true;
                break;
            case 'E': b = true;
                break;
        }
        return b;
    }
    /**
     * Takes in the coordinate string and determines if the n value is within
     * scope.
     * @param a
     * @return boolean b
     */
    public static boolean ifM(String a) {
        char b = a.charAt(1);
        return b == '1'|| b == '2'|| b == '3'|| b == '4'|| b == '5';
    }
    /**
     * Matches the first character to a n value then returns it
     * @param a
     * @return n
     */
    public static int findN(String a) {
        int n = 0;
            //Returns correlated value
        switch (a.charAt(0)) {
            case 'A': n = 0;
                break;
            case 'B': n = 1;
                break;
            case 'C': n = 2;
                break;
            case 'D': n = 3;
                break;
            case 'E': n = 4;
                break;
        }
        return n;
    }
    /**
     * Matches the second character to a m value then returns it
     * @param a
     * @return m
     */
    public static int findM(String a) {
        int n = 0;
            //Returns correlated value
        switch (a.charAt(1)) {
            case '1': n = 0;
                break;
            case '2': n = 1;
                break;
            case '3': n = 2;
                break;
            case '4': n = 3;
                break;
            case '5': n = 4;
                break;
        }
        return n;
    }
}
