import javax.swing.JPanel;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.awt.Toolkit;
import java.awt.Graphics;

public class Painter extends JPanel implements MouseListener {
    private static final long SerialVersionUID = 1L;
    private TicTacToe ticTacToe;

    public Painter(TicTacToe ticTacToe) {
        setFocusable(true);
        requestFocus();
        setBackground(Color.WHITE);
        addMouseListener(this);
        this.ticTacToe = ticTacToe;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        ticTacToe.render(g);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (ticTacToe.isAccepted()) {
            if (ticTacToe.isYourTurn() && !ticTacToe.isUnableToCommunicateWithOpponent() && ticTacToe.isWon() && !ticTacToe.isEnemyWon()) {
                int x = e.getX() / ticTacToe.getLengthOfSpace();
                int y = e.getY() / ticTacToe.getLengthOfSpace();
                y *= 3;
                int position = x + y;

                if (ticTacToe.getSpaces()[position] == null) {
                    if (!ticTacToe.isCircle())
                        ticTacToe.getSpaces()[position] = "X";
                    else
                        ticTacToe.getSpaces()[position] = "O";
                    ticTacToe.setYourTurn(false);
                    repaint();
                    Toolkit.getDefaultToolkit().sync();

                    try {
                        ticTacToe.getDos().writeInt(position);
                        ticTacToe.getDos().flush();
                    } catch (IOException e1) {
                        ticTacToe.setErrors(ticTacToe.getErrors() + 1);
                        e1.printStackTrace();
                    }

                    System.out.println("DATA WAS SENT");
                    ticTacToe.checkForWin();
                    ticTacToe.checkForTie();

                }
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

}