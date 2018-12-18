package networksfinal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

/**
 *
 * @author Logan
 */
public class srv {
    public static void main(String args[]) {
        try {
                //Start server socket
            ServerSocket ss = new ServerSocket(1234);
            System.out.println("Server initiated.");
                //Accepts two clients
            Socket s1 = ss.accept();
            System.out.println("Client 1 has connected.");
            Socket s2 = ss.accept();
            System.out.println("Client 2 has connected.");
                //Creates input/output streams
            DataInputStream s1In = new DataInputStream(s1.getInputStream());
            DataInputStream s2In = new DataInputStream(s2.getInputStream());
            System.out.println("Scanners in initiated.");
            DataOutputStream s1Out = new DataOutputStream(s1.getOutputStream());
            DataOutputStream s2Out = new DataOutputStream(s2.getOutputStream());
            System.out.println("Input/output streams initiated.");
                //Writes to clients id's
            String u1 = "Player 1";
            String u2 = "Player 2";
            s1Out.writeUTF(u1);
            System.out.println("Client 1 id sent.");
            s2Out.writeUTF(u2);
            System.out.println("Client 2 id sent.");
                //Creates board arrays for each player
            int[][] p1 = new int[5][5];
            int[][] p2 = new int[5][5];
                //Accepts board values from each player
            for (int n = 0; n < 5; n++) {
                for (int m = 0; m < 5; m++) {
                    p1[n][m] = s1In.readInt();
                }
            }
            for (int n = 0; n < 5; n++) {
                for (int m = 0; m < 5; m++) {
                    p2[n][m] = s2In.readInt();
                }
            }
            System.out.println("Boards read in.");
                //Initializes scores
            int p1Score = 0;
            int p2Score = 0;
            System.out.println("Scores initialized.");
                //Chooses a random player to go first
            Random r = new Random();
            int turn = (int) Math.pow(-1, r.nextInt() % 2);
            System.out.println("Player order decided.");
                //To loop
            boolean repeat = true;
            System.out.println("Beginning game.");
            do {
                    //If 1 then player 1 goes
                if (0 < turn) {
                        //Tells clients whos turn it is
                    s1Out.writeBoolean(true);
                    s2Out.writeBoolean(false);
                    System.out.println("Player 1's turn.");
                        //Reads in attack info
                    String coordinates = s1In.readUTF();
                    int n = s1In.readInt();
                    int m = s1In.readInt();
                    System.out.println("Attack info read.");
                    boolean ifHit = false;
                        //If hit, increment score
                    if (p2[n][m] == 1) {
                        p2[n][m] = 0;
                        p1Score++;
                        ifHit = true;
                        System.out.println("Correct attack.");
                    }
                        //Tells attacker if it hit
                    s1Out.writeBoolean(ifHit);
                    System.out.println("Attack results sent to Player 1.");
                        //Sends attack info to attacked
                    s2Out.writeUTF(coordinates);
                    s2Out.writeInt(n);
                    s2Out.writeInt(m);
                    s2Out.writeBoolean(ifHit);
                    System.out.println("Info send to Player 2.");
                }
                    //If -1 player 2 goes 
                else {
                        //Tells clients whos turn it is
                    s1Out.writeBoolean(false);
                    s2Out.writeBoolean(true);
                    System.out.println("Player 2's turn.");
                        //Reads in attack info
                    String coordinates = s2In.readUTF();
                    int n = s2In.readInt();
                    int m = s2In.readInt();
                    System.out.println("Attack info read.");
                    boolean ifHit = false;
                        //If hit, increment score
                    if (p1[n][m] == 1) {
                        p1[n][m] = 0;
                        p2Score++;
                        ifHit = true;
                        System.out.println("Correct attack.");
                    }
                        //Tells attacker if it hit
                    s2Out.writeBoolean(ifHit);
                    System.out.println("Attack results sent to Player 2.");
                        //Sends attack info to attacked
                    s1Out.writeUTF(coordinates);
                    s1Out.writeInt(n);
                    s1Out.writeInt(m);
                    s1Out.writeBoolean(ifHit);
                    System.out.println("Info sent to Player 1.");
                }
                    //Dertermines if someone has won yet
                if (p1Score == 7) {
                    s1Out.writeBoolean(true);
                    s2Out.writeBoolean(true);
                    s1Out.writeBoolean(true);
                    s2Out.writeBoolean(false);
                    repeat = false;
                    System.out.println("Player 1 has won.");
                }
                else if (p2Score == 7) {
                    s1Out.writeBoolean(true);
                    s2Out.writeBoolean(true);
                    s1Out.writeBoolean(false);
                    s2Out.writeBoolean(true);
                    repeat = false;
                    System.out.println("Player 2 has won.");
                }
                else {
                    s1Out.writeBoolean(false);
                    s2Out.writeBoolean(false);
                }
                    //Switch whos turn it is
                turn *= -1;
                System.out.println("Switching player turn.");
            } while (repeat);
                //Send final scores
            s1Out.writeInt(p1Score);
            s1Out.writeInt(p2Score);
            s2Out.writeInt(p1Score);
            s2Out.writeInt(p2Score);
            System.out.println("Sending final scores.");
                //Close all 
            s1Out.close();
            s2Out.close();
            s1In.close();
            s2In.close();
            s1.close();
            s2.close();
            ss.close();
            System.out.println("Closed all.");
        } catch (IOException ex) {}
    }
}
