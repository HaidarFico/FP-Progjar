import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.DataInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.BasicStroke;
import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("unused")

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
    private boolean won = false;
    private boolean tie = false;
    private boolean yourTurn = false;
    private boolean circle = true;
    private boolean accepted = false;
    private boolean unableToCommunicateWithOpponent = false;
    private boolean enemyWon = false;
    private int errors = 0;

    private final int lengthOfSpace = 160;
    private int firstSpot = -1;
    private int secondSpot = -1;

    private final Font font = new Font("Verdana", Font.BOLD, 32);
    private final Font smallerFont = new Font("Verdana", Font.BOLD, 20);
    private final Font largerFont = new Font("Verdana", Font.BOLD, 50);

    private final String waitingString = "Waiting for another player";
    private final String unableToCommunicateWithOpponentString = "Unable to communicate with opponent";
    private final String wonString = "You won!";
    private final String enemyWonString = "Opponent won!";
    private final String tieString = "Game ended in a tie.";

    private int[][] wins = new int[][] { { 0, 1, 2 }, { 3, 4, 5 }, { 6, 7, 8 }, { 0, 3, 6 }, { 1, 4, 7 }, { 2, 5, 8 },
            { 0, 4, 8 }, { 2, 4, 6 } };

    public TicTacToe() {
        System.out.println("Please input the IP");
        ip = scanner.nextLine();
        System.out.println("Please input the port");
        port = scanner.nextInt();

        while (port < 1 || port > 65535) {
            System.out.println("The port is invalid, please input another.");
            port = scanner.nextInt();
        }
        loadImages();

        painter = new Painter(this);
        painter.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        if (!connect())
            initializeServer();

        frame = new JFrame();
        frame.setTitle("Tic-Tac-Toe");
        frame.setContentPane(painter);
        frame.setSize(WIDTH, HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);

        thread = new Thread(this, "TicTacToe");
        thread.start();
    }
    @Override
    public void run() {
        while (true) {
            tick();
            painter.repaint();

            if (!circle && !accepted) {
                listenForServerRequest();
            }
        }
    }

    private void loadImages() {
        try {
            // board = ImageIO.read(getClass().getResourceAsStream("./board.png"));
            // redX = ImageIO.read(getClass().getResourceAsStream("/redX.png"));
            // redCircle = ImageIO.read(getClass().getResourceAsStream("redCircle.png"));
            // blueX = ImageIO.read(getClass().getResourceAsStream("./blueX.png"));
            // blueCircle = ImageIO.read(getClass().getResourceAsStream("./blueCircle.png"));
            board = ImageIO.read(new File("/home/haidarwsl/programmingLinux/FP-Progjar/res/board.png"));
            redX = ImageIO.read(new File("/home/haidarwsl/programmingLinux/FP-Progjar/res/redX.png"));
            redCircle = ImageIO.read(new File("/home/haidarwsl/programmingLinux/FP-Progjar/res/redCircle.png"));
            blueX = ImageIO.read(new File("/home/haidarwsl/programmingLinux/FP-Progjar/res/blueX.png"));
            blueCircle = ImageIO.read(new File("/home/haidarwsl/programmingLinux/FP-Progjar/res/blueCircle.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listenForServerRequest() {
        Socket socket = null;
        try {
            socket = serverSocket.accept();
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
            accepted = true;
            System.out.println("CLIENT HAS REQUESTED TO JOIN, ACCEPTED");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkForEnemyWin() {
        for (int i = 0; i < wins.length; i++) {
            if (circle) {
                if (spaces[wins[i][0]] == "X" && spaces[wins[i][1]] == "X" && spaces[wins[i][2]] == "X") {
                    firstSpot = wins[i][0];
                    secondSpot = wins[i][2];
                    enemyWon = true;
                }
            } else {
                if (spaces[wins[i][0]] == "O" && spaces[wins[i][1]] == "O" && spaces[wins[i][2]] == "O") {
                    firstSpot = wins[i][0];
                    secondSpot = wins[i][2];
                    enemyWon = true;
                }
            }
        }
    }

    private void checkForTie() {
        for (int i = 0; i < spaces.length; i++) {
            if (spaces[i] == null) {
                return;
            }
        }
        tie = true;
    }

    private void checkForWin() {
        for (int i = 0; i < wins.length; i++) {
            if (circle) {
                if (spaces[wins[i][0]] == "O" && spaces[wins[i][1]] == "O" && spaces[wins[i][2]] == "O") {
                    firstSpot = wins[i][0];
                    secondSpot = wins[i][2];
                    won = true;
                }
            } else {
                if (spaces[wins[i][0]] == "X" && spaces[wins[i][1]] == "X" && spaces[wins[i][2]] == "X") {
                    firstSpot = wins[i][0];
                    secondSpot = wins[i][2];
                    won = true;
                }
            }
        }
    }

    private void tick() {
        if (errors >= 10)
            unableToCommunicateWithOpponent = true;

        if (!yourTurn && !unableToCommunicateWithOpponent) {
            try {
                int space = dis.readInt();
                if (circle)
                    spaces[space] = "X";
                else
                    spaces[space] = "O";
                checkForEnemyWin();
                yourTurn = true;
            } catch (IOException e) {
                e.printStackTrace();
                errors++;
            }
        }
    }

    public void render(Graphics g) {
        g.drawImage(board, 0, 0, null);
        if (unableToCommunicateWithOpponent) {
            g.setColor(Color.RED);
            g.setFont(smallerFont);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int stringWidth = g2.getFontMetrics().stringWidth(unableToCommunicateWithOpponentString);
            g.drawString(unableToCommunicateWithOpponentString, WIDTH / 2 - stringWidth, HEIGHT / 2);
            return;
        }

        if (accepted) {
            for (int i = 0; i < spaces.length; i++) {
                if (spaces[i] != null) {
                    if (spaces[i].equals("X")) {
                        if (circle) {
                            g.drawImage(redX, (i % 3) * lengthOfSpace + 10 * (i % 3),
                                    (int) (i / 3) * lengthOfSpace + 10 * (int) (i / 3), null);
                        } else {
                            g.drawImage(blueX, (i % 3) * lengthOfSpace + 10 * (i % 3),
                                    (int) (i / 3) * lengthOfSpace + 10 * (int) (i / 3), null);
                        }
                    } else if (spaces[i].equals("O")) {
                        if (circle) {
                            g.drawImage(blueCircle, (i % 3) * lengthOfSpace + 10 * (i % 3),
                                    (int) (i / 3) * lengthOfSpace + 10 * (int) (i / 3), null);
                        } else {
                            g.drawImage(redCircle, (i % 3) * lengthOfSpace + 10 * (i % 3),
                                    (int) (i / 3) * lengthOfSpace + 10 * (int) (i / 3), null);
                        }
                    }
                }
            }
        }
        if (won || enemyWon) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(10));
            g.setColor(Color.BLACK);
            g.drawLine(firstSpot % 3 * lengthOfSpace + 10 * firstSpot % 3 + lengthOfSpace / 2,
                    (int) (firstSpot / 3) * lengthOfSpace + 10 * (int) (firstSpot / 3) + lengthOfSpace / 2,
                    secondSpot % 3 * lengthOfSpace + 10 * secondSpot % 3 + lengthOfSpace / 2,
                    (int) (secondSpot / 3) * lengthOfSpace + 10 * (int) (secondSpot / 3) + lengthOfSpace / 2);

            g.setColor(Color.RED);
            g.setFont(largerFont);
            if (won) {
                int stringWidth = g2.getFontMetrics().stringWidth(wonString);
                g.drawString(wonString, WIDTH / 2 - stringWidth / 2, HEIGHT / 2);
            } else if (enemyWon) {
                int stringWidth = g2.getFontMetrics().stringWidth(enemyWonString);
                g.drawString(enemyWonString, WIDTH / 2 - stringWidth / 2, HEIGHT / 2);
            }
        }
        if (tie) {
            Graphics2D g2 = (Graphics2D) g;
            g.setColor(Color.BLACK);
            g.setFont(largerFont);
            int stringWidth = g2.getFontMetrics().stringWidth(tieString);
            g.drawString(tieString, WIDTH / 2 - stringWidth / 2, HEIGHT / 2);
        } else {
            g.setColor(Color.RED);
            g.setFont(font);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int stringWidth = g2.getFontMetrics().stringWidth(waitingString);
            g.drawString(waitingString, WIDTH / 2 - stringWidth / 2, HEIGHT / 2);
        }
    }

    private boolean connect() {
        try {
            socket = new Socket(ip, port);
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
            accepted = true;
        } catch (IOException e) {
            System.out.println("Unable to connect to the address: " + ip + ":" + port + " | Starting a server");
            return false;
        }
        System.out.println("Successfully connected to the server.");
        return true;
    }

    private void initializeServer() {
        try {
            serverSocket = new ServerSocket(port, 8, InetAddress.getByName(ip));
        } catch (Exception e) {
            e.printStackTrace();
        }
        yourTurn = true;
        circle = false;
    }
}
