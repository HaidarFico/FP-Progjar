import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.awt.Font;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class TicTacToe implements Runnable {

    private String ip = "localhost";
    private int port = 8888;
    private Scanner scanner = new Scanner(System.in);
    private JFrame frame;

    private final int WIDTH = 506;
    private final int HEIGHT = 527;

    private Thread thread;
    private Painter painter;
    private Socket socket;
    private DataOutputStream dos;
    private DataInputStream dis;
    private ServerSocket serverSocket;

    private BufferedImage board;
    private BufferedImage redX;
    private BufferedImage blueX;
    private BufferedImage redCircle;
    private BufferedImage blueCircle;

    private String[] spaces = new String[9];
    private boolean yourTurn = false;
    private boolean circle = true;
    private boolean accepted = false;
    private boolean unableToCommunicateWithOpponent = false;
    private boolean won = false;
    private boolean enemyWon = false;

    private int lengthOfSpace = 160;
    private int errors = 0;
    private int firstSpot = -1;
    private int secondSpot = -1;

    private Font font = new Font("Verdana", Font.BOLD, 32);
    private Font smallerFont = new Font("Verdana", Font.BOLD, 20);
    private Font largerFont = new Font("Verdana", Font.BOLD, 50);

    private String waitingString = "Waiting for another player";
    private String unableToCommunicateWithOpponentString = "Unable to communicate with opponent";
    private String wonString = "You won!";
    private String enemyWonString = "Opponent won!";

    public TicTacToe() {
        System.out.println("Please input the IP");
        ip = scanner.nextLine();
        System.out.println("Please input the port");
        port = scanner.nextInt();

        while (port < 1 || port > 65535) {
            System.out.println("The port is invalid, please input another.");
            port = scanner.nextInt();
        }

        
    }

    public static void main(String[] args) throws Exception {
        TicTacToe ticTacToe = new TicTacToe();
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'run'");
    }

    public class Painter {

    }

    private void loadImages()
    {
        
    }
}
