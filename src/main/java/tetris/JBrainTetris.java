package tetris;

import javax.swing.*;
import java.awt.*;

public class JBrainTetris extends JTetris {
    private JCheckBox brainMode;
    private boolean brainActive;
    private Brain brain;
    private Brain.Move currentMove;
    private int currentCount;

    private JSlider adversary;
    private JLabel okStatus;
    private Brain.Move move;

    JBrainTetris(int pixels) {
        super(pixels);
        brain = new DefaultBrain();
        currentMove = new Brain.Move();
        currentCount = -1;
    }

    @Override
    public JComponent createControlPanel() {
        JComponent panel = super.createControlPanel();
        panel.add(new JLabel("Brain:"));
        brainMode = new JCheckBox("Brain active");
        panel.add(brainMode);

        JPanel little = new JPanel();
        little.add(new JLabel("Adversary:"));
        adversary = new JSlider(0, 100, 0);    // min, max, current
        adversary.setPreferredSize(new Dimension(100, 15));
        little.add(adversary);
        panel.add(little);

        //Ok status label
        JPanel okPanel = new JPanel();
        okStatus = new JLabel("ok");
        okPanel.add(okStatus);
        panel.add(okPanel);

        return panel;
    }

    @Override
    public void tick(int verb) {

        brainActive = brainMode.isSelected();
        if (verb == DOWN && brainActive) {

            if (currentCount != count) {
                currentCount = count;
                board.undo();
                currentMove = brain.bestMove(board, currentPiece, HEIGHT, currentMove);
            }

            if (currentMove != null) {
                if (!currentPiece.equals(currentMove.piece)) {
                    super.tick(ROTATE);
                }

                if (currentX > currentMove.x) super.tick(LEFT);
                else if (currentX < currentMove.x) super.tick(RIGHT);
                else if (currentPiece.equals(currentMove.piece)
                        && currentMove.x == currentX
                        && currentMove.y != currentY)
                    verb = DROP;
            }
        }

        super.tick(verb);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        JBrainTetris jbraintetris = new JBrainTetris(16);
        JFrame frame = JTetris.createFrame(jbraintetris);
        frame.setVisible(true);
    }

    @Override
    public Piece pickNextPiece() {

        int adversaryValue = adversary.getValue();
        int rand = random.nextInt(100);

        if (rand >= adversaryValue) {
            okStatus.setText("ok");
            return super.pickNextPiece();
        } else {
            okStatus.setText("*ok*");
            Piece worstPiece = null;
            double worseScore = 0;

            for (int i = 0; i < pieces.length; i++) {
                currentMove = brain.bestMove(board, pieces[i], HEIGHT, currentMove);

                if (currentMove == null)

                return super.pickNextPiece();
                if (currentMove.score > worseScore) {
                    worstPiece = pieces[i];
                    worseScore = currentMove.score;
                }
            }
            return worstPiece;
        }
    }

}
