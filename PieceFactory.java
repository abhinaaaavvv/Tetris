import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PieceFactory {
    private final Random random = new Random();
    private final List<Tetromino.Type> queue = new ArrayList<Tetromino.Type>();

    public PieceFactory() {
        refillIfNeeded();
    }

    private void refillIfNeeded() {
        while (queue.size() < 7) {
            List<Tetromino.Type> bag = new ArrayList<Tetromino.Type>();
            for (Tetromino.Type t : Tetromino.Type.values()) {
                bag.add(t);
            }
            Collections.shuffle(bag, random);
            queue.addAll(bag);
        }
    }

    public Tetromino nextPiece() {
        refillIfNeeded();
        Tetromino.Type t = queue.remove(0);
        return new Tetromino(t);
    }

    public List<Tetromino.Type> peekNextTypes(int count) {
        refillIfNeeded();
        int n = Math.min(count, queue.size());
        List<Tetromino.Type> list = new ArrayList<Tetromino.Type>(n);
        for (int i = 0; i < n; i++) list.add(queue.get(i));
        return list;
    }

    public void reset() {
        queue.clear();
        refillIfNeeded();
    }
}
