package org;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Board {

  public static final int COLS = 10;
  public static final int ROWS = 20;

  private final int[][] cells; // -1 = empty, otherwise ordinal of Tetromino.Type

  public Board() {
    cells = new int[ROWS][COLS];
    reset();
  }

  public void reset() {
    for (int y = 0; y < ROWS; y++) {
      for (int x = 0; x < COLS; x++) {
        cells[y][x] = -1;
      }
    }
  }

  public int getCell(int x, int y) {
    if (x < 0 || x >= COLS || y < 0 || y >= ROWS) return -1;
    return cells[y][x];
  }

  public boolean canPlace(Tetromino piece, int px, int py) {
    List<java.awt.Point> blocks = piece.getCells();
    for (java.awt.Point p : blocks) {
      int x = px + p.x;
      int y = py + p.y;
      if (x < 0 || x >= COLS) return false;
      if (y >= ROWS) return false;
      if (y < 0) continue; // allow spawn above visible board
      if (cells[y][x] != -1) return false;
    }
    return true;
  }

  public void lockPiece(Tetromino piece, int px, int py) {
    int ordinal = piece.getType().ordinal();
    for (java.awt.Point p : piece.getCells()) {
      int x = px + p.x;
      int y = py + p.y;
      if (y >= 0 && y < ROWS && x >= 0 && x < COLS) {
        cells[y][x] = ordinal;
      }
    }
  }

  public List<Integer> getCompletedLines() {
    List<Integer> full = new ArrayList<Integer>();
    for (int y = 0; y < ROWS; y++) {
      boolean isFull = true;
      for (int x = 0; x < COLS; x++) {
        if (cells[y][x] == -1) {
          isFull = false;
          break;
        }
      }
      if (isFull) full.add(Integer.valueOf(y));
    }
    return full;
  }

  public int clearCompletedLines() {
    List<Integer> lines = getCompletedLines();
    if (lines.isEmpty()) return 0;
    removeLines(lines);
    return lines.size();
  }

  public void removeLines(List<Integer> lines) {
    if (lines == null || lines.isEmpty()) return;
    // Remove from bottom-most upwards to avoid reindexing issues
    Collections.sort(lines, Collections.reverseOrder());
    for (Integer rowObj : lines) {
      int row = rowObj.intValue();
      for (int y = row; y > 0; y--) {
        for (int x = 0; x < COLS; x++) {
          cells[y][x] = cells[y - 1][x];
        }
      }
      for (int x = 0; x < COLS; x++) {
        cells[0][x] = -1;
      }
    }
  }
}
