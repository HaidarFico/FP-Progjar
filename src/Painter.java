import javax.swing.JPanel;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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