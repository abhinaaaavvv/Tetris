import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Tetris");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                TetrisPanel panel = new TetrisPanel();
                frame.setContentPane(panel);
                frame.pack();
                frame.setResizable(false);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
}
