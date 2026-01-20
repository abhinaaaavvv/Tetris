package org;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.Timer;

public class TetrisPanel extends JPanel {

  // Visual constants
  private static final int CELL = 30; // pixels
  private static final int BOARD_W = Board.COLS * CELL;
  private static final int BOARD_H = Board.ROWS * CELL;
  private static final int SIDE_W = 180;

  private final Board board = new Board();
  private final PieceFactory factory = new PieceFactory();
  private final Scoreboard score = new Scoreboard();

  private Tetromino current;
  private int pieceX;
  private int pieceY; // can be negative during spawn

  private boolean gameOver = false;
  private boolean softDropping = false;

  private final Timer timer;
  private long lastTickNanos;
  private long fallAccumulatorMs = 0;

  private final InputHandler input = new InputHandler();

  public TetrisPanel() {
    setPreferredSize(new Dimension(BOARD_W + SIDE_W, BOARD_H));
    setBackground(new Color(20, 20, 30));
    setDoubleBuffered(true);

    input.install(this, this);

    startGame();

    timer = new Timer(
      16,
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          onTick();
        }
      }
    );
    lastTickNanos = System.nanoTime();
    timer.start();
  }

  public void startGame() {
    board.reset();
    factory.reset();
    score.reset();
    gameOver = false;
    fallAccumulatorMs = 0;
    spawnNext();
    repaint();
  }

  public void restart() {
    startGame();
  }

  private void spawnNext() {
    current = factory.nextPiece();
    pieceX = Board.COLS / 2 - 2; // centered for 4x4 box
    pieceY = -2; // spawn slightly above
    if (!board.canPlace(current, pieceX, pieceY)) {
      gameOver = true;
    }
  }

  private void onTick() {
    long now = System.nanoTime();
    long deltaMs = (now - lastTickNanos) / 1_000_000L;
    lastTickNanos = now;

    if (gameOver) {
      repaint();
      return;
    }

    fallAccumulatorMs += deltaMs;
    int delay = softDropping ? 50 : score.getFallDelayMs();

    // Ensure reasonable delay changes
    if (fallAccumulatorMs >= delay) {
      // Attempt to step down
      boolean moved = tryMove(0, 1);
      if (!moved) {
        // lock and clear lines
        board.lockPiece(current, pieceX, pieceY);
        boolean toppedOut = isLockedAboveTop(current, pieceX, pieceY);
        int cleared = board.clearCompletedLines();
        if (cleared > 0) {
          score.addLines(cleared);
        }
        if (toppedOut) {
          gameOver = true;
        } else {
          spawnNext();
        }
      } else if (softDropping) {
        score.addSoftDrop(1);
      }
      fallAccumulatorMs = 0;
    }

    repaint();
  }

  // Control API (called by InputHandler)
  public void moveLeft() {
    if (gameOver) return;
    tryMove(-1, 0);
    repaint();
  }

  public void moveRight() {
    if (gameOver) return;
    tryMove(1, 0);
    repaint();
  }

  public void rotateCW() {
    if (gameOver) return;
    tryRotate(true);
    repaint();
  }

  public void rotateCCW() {
    if (gameOver) return;
    tryRotate(false);
    repaint();
  }

  public void setSoftDropping(boolean val) {
    if (gameOver) return;
    softDropping = val;
  }

  public void hardDrop() {
    if (gameOver) return;
    int dist = computeDropDistance();
    if (dist > 0) {
      pieceY += dist;
      score.addHardDrop(dist);
    }
    // lock immediately
    board.lockPiece(current, pieceX, pieceY);
    boolean toppedOut = isLockedAboveTop(current, pieceX, pieceY);
    int cleared = board.clearCompletedLines();
    if (cleared > 0) score.addLines(cleared);
    if (toppedOut) {
      gameOver = true;
    } else {
      spawnNext();
    }
    repaint();
  }

  private boolean tryMove(int dx, int dy) {
    if (current == null) return false;
    int nx = pieceX + dx;
    int ny = pieceY + dy;
    if (board.canPlace(current, nx, ny)) {
      pieceX = nx;
      pieceY = ny;
      return true;
    }
    return false;
  }

  private void tryRotate(boolean cw) {
    if (current == null) return;
    // Rotate and try simple wall kicks
    if (cw) current.rotateCW();
    else current.rotateCCW();
    if (board.canPlace(current, pieceX, pieceY)) return;
    int[][] kicks = new int[][] {
      { -1, 0 },
      { 1, 0 },
      { -2, 0 },
      { 2, 0 },
      { 0, -1 },
    };
    for (int i = 0; i < kicks.length; i++) {
      int nx = pieceX + kicks[i][0];
      int ny = pieceY + kicks[i][1];
      if (board.canPlace(current, nx, ny)) {
        pieceX = nx;
        pieceY = ny;
        return;
      }
    }
    // Revert if none fit
    if (cw) current.rotateCCW();
    else current.rotateCW();
  }

  private int computeDropDistance() {
    int dy = 0;
    while (board.canPlace(current, pieceX, pieceY + dy + 1)) {
      dy++;
    }
    return dy;
  }

  private boolean isLockedAboveTop(Tetromino piece, int gx, int gy) {
    for (java.awt.Point p : piece.getCells()) {
      if (gy + p.y < 0) return true;
    }
    return false;
  }

  @Override
  protected void paintComponent(Graphics g0) {
    super.paintComponent(g0);
    Graphics2D g = (Graphics2D) g0;
    g.setRenderingHint(
      RenderingHints.KEY_ANTIALIASING,
      RenderingHints.VALUE_ANTIALIAS_ON
    );

    // Draw board background
    int bx = 0;
    int by = 0;
    g.setColor(new Color(10, 10, 18));
    g.fillRect(bx, by, BOARD_W, BOARD_H);

    // Subtle grid lines
    g.setColor(new Color(255, 255, 255, 20));
    for (int x = 0; x <= Board.COLS; x++) {
      int px = bx + x * CELL;
      g.drawLine(px, by, px, by + BOARD_H);
    }
    for (int y = 0; y <= Board.ROWS; y++) {
      int py = by + y * CELL;
      g.drawLine(bx, py, bx + BOARD_W, py);
    }

    // Draw locked cells
    for (int y = 0; y < Board.ROWS; y++) {
      for (int x = 0; x < Board.COLS; x++) {
        int ord = board.getCell(x, y);
        if (ord >= 0) {
          drawCell(
            g,
            bx + x * CELL,
            by + y * CELL,
            Tetromino.colorForOrdinal(ord)
          );
        }
      }
    }

    // Ghost piece
    if (!gameOver && current != null) {
      int drop = computeDropDistance();
      drawPiece(g, current, pieceX, pieceY + drop, bx, by, true);
    }

    // Current piece
    if (!gameOver && current != null) {
      drawPiece(g, current, pieceX, pieceY, bx, by, false);
    }

    // Side panel
    int sx = BOARD_W + 10;
    int sy = 10;
    g.setColor(new Color(30, 30, 45));
    g.fillRoundRect(BOARD_W, 0, SIDE_W, BOARD_H, 16, 16);

    g.setColor(Color.WHITE);
    g.setFont(new Font("SansSerif", Font.BOLD, 16));
    g.drawString("Score", sx, sy + 20);
    g.setFont(new Font("SansSerif", Font.PLAIN, 18));
    g.drawString(String.valueOf(score.getScore()), sx, sy + 40);

    g.setFont(new Font("SansSerif", Font.BOLD, 16));
    g.drawString("Level", sx, sy + 70);
    g.setFont(new Font("SansSerif", Font.PLAIN, 18));
    g.drawString(String.valueOf(score.getLevel()), sx, sy + 90);

    g.setFont(new Font("SansSerif", Font.BOLD, 16));
    g.drawString("Lines", sx, sy + 120);
    g.setFont(new Font("SansSerif", Font.PLAIN, 18));
    g.drawString(String.valueOf(score.getTotalLines()), sx, sy + 140);

    // Next preview
    g.setFont(new Font("SansSerif", Font.BOLD, 16));
    g.drawString("Next", sx, sy + 180);
    List<Tetromino.Type> preview = factory.peekNextTypes(1);
    if (!preview.isEmpty()) {
      drawPreview(g, preview.get(0), sx, sy + 200);
    }

    // Game Over overlay
    if (gameOver) {
      g.setColor(new Color(0, 0, 0, 150));
      g.fillRect(bx, by, BOARD_W, BOARD_H);
      g.setColor(Color.WHITE);
      g.setFont(new Font("SansSerif", Font.BOLD, 28));
      g.drawString("Game Over", bx + 40, by + BOARD_H / 2 - 10);
      g.setFont(new Font("SansSerif", Font.PLAIN, 16));
      g.drawString("Press R to Restart", bx + 40, by + BOARD_H / 2 + 18);
    }
  }

  private void drawCell(Graphics2D g, int px, int py, Color base) {
    // Block with simple shading for a polished look
    int s = CELL - 2;
    px += 1;
    py += 1; // padding
    g.setColor(base);
    g.fillRoundRect(px, py, s, s, 6, 6);

    // Highlights and shadows
    g.setColor(base.brighter());
    g.drawLine(px + 2, py + 2, px + s - 3, py + 2);
    g.drawLine(px + 2, py + 2, px + 2, py + s - 3);
    g.setColor(base.darker());
    g.drawLine(px + 2, py + s - 2, px + s - 2, py + s - 2);
    g.drawLine(px + s - 2, py + 2, px + s - 2, py + s - 2);

    // Inner inset
    g.setColor(new Color(255, 255, 255, 30));
    g.fillRoundRect(px + 3, py + 3, s - 6, (s - 6) / 2, 6, 6);
  }

  private void drawPiece(
    Graphics2D g,
    Tetromino piece,
    int gx,
    int gy,
    int bx,
    int by,
    boolean ghost
  ) {
    Color c = Tetromino.colorFor(piece.getType());
    Color ghostC = new Color(c.getRed(), c.getGreen(), c.getBlue(), 70);
    for (java.awt.Point p : piece.getCells()) {
      int x = gx + p.x;
      int y = gy + p.y;
      if (y < 0) continue; // above visible area
      int px = bx + x * CELL;
      int py = by + y * CELL;
      if (ghost) {
        // ghost as translucent filled rectangle
        int s = CELL - 2;
        px += 1;
        py += 1;
        g.setColor(ghostC);
        g.fillRoundRect(px, py, s, s, 6, 6);
        g.setColor(new Color(255, 255, 255, 60));
        g.drawRoundRect(px, py, s, s, 6, 6);
      } else {
        drawCell(g, px, py, c);
      }
    }
  }

  private void drawPreview(Graphics2D g, Tetromino.Type type, int sx, int sy) {
    Tetromino tmp = new Tetromino(type);
    // Center preview in a 4x4 area
    int box = CELL * 4;
    int ox = sx + 10;
    int oy = sy + 20;
    // Draw a subtle box
    g.setColor(new Color(255, 255, 255, 20));
    g.drawRoundRect(ox - 6, oy - 16, box, box, 10, 10);

    Color c = Tetromino.colorFor(type);
    for (java.awt.Point p : tmp.getCells()) {
      int px = ox + p.x * CELL;
      int py = oy + p.y * CELL;
      if (type == Tetromino.Type.I) {
        // Better centering for I piece
        px -= CELL / 2;
        py -= CELL / 2;
      }
      drawCell(g, px, py, c);
    }
  }
}
