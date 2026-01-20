package org;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Tetromino {

  public enum Type {
    I,
    J,
    L,
    O,
    S,
    T,
    Z,
  }

  public static final int SIZE = 4; // 4x4 bounding box per rotation

  private final Type type;
  private int rotation; // 0..3

  public Tetromino(Type type) {
    this.type = type;
    this.rotation = 0;
  }

  public Type getType() {
    return type;
  }

  public int getRotation() {
    return rotation;
  }

  public void rotateCW() {
    rotation = (rotation + 1) % 4;
  }

  public void rotateCCW() {
    rotation = (rotation + 3) % 4;
  }

  public List<Point> getCells() {
    int[][] shape = getShapeMatrix(type, rotation);
    List<Point> cells = new ArrayList<Point>(4);
    for (int y = 0; y < SIZE; y++) {
      for (int x = 0; x < SIZE; x++) {
        if (shape[y][x] == 1) {
          cells.add(new Point(x, y));
        }
      }
    }
    return cells;
  }

  public int getWidth() {
    int[][] m = getShapeMatrix(type, rotation);
    int minX = SIZE,
      maxX = -1;
    for (int y = 0; y < SIZE; y++) {
      for (int x = 0; x < SIZE; x++) {
        if (m[y][x] == 1) {
          if (x < minX) minX = x;
          if (x > maxX) maxX = x;
        }
      }
    }
    if (maxX == -1) return 0;
    return (maxX - minX + 1);
  }

  public int getHeight() {
    int[][] m = getShapeMatrix(type, rotation);
    int minY = SIZE,
      maxY = -1;
    for (int y = 0; y < SIZE; y++) {
      for (int x = 0; x < SIZE; x++) {
        if (m[y][x] == 1) {
          if (y < minY) minY = y;
          if (y > maxY) maxY = y;
        }
      }
    }
    if (maxY == -1) return 0;
    return (maxY - minY + 1);
  }

  public static Color colorFor(Type t) {
    switch (t) {
      case I:
        return Color.CYAN;
      case J:
        return Color.BLUE;
      case L:
        return Color.ORANGE;
      case O:
        return Color.YELLOW;
      case S:
        return Color.GREEN;
      case T:
        return new Color(128, 0, 128); // purple
      case Z:
        return Color.RED;
      default:
        return Color.LIGHT_GRAY;
    }
  }

  public static Color colorForOrdinal(int ord) {
    if (ord < 0 || ord >= Type.values().length) return Color.DARK_GRAY;
    return colorFor(Type.values()[ord]);
  }

  public static int[][] getShapeMatrix(Type type, int rot) {
    switch (type) {
      case I:
        return I_SHAPES[rot];
      case J:
        return J_SHAPES[rot];
      case L:
        return L_SHAPES[rot];
      case O:
        return O_SHAPES[rot];
      case S:
        return S_SHAPES[rot];
      case T:
        return T_SHAPES[rot];
      case Z:
        return Z_SHAPES[rot];
      default:
        return new int[SIZE][SIZE];
    }
  }

  // Shape definitions: 4 rotations, each as 4x4 matrix of 0/1
  // Rotation 0 aims for a visually standard spawn orientation.

  private static final int[][][] I_SHAPES = new int[][][] {
    // 0: horizontal
    { { 0, 0, 0, 0 }, { 1, 1, 1, 1 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 } },
    // 1: vertical
    { { 0, 0, 1, 0 }, { 0, 0, 1, 0 }, { 0, 0, 1, 0 }, { 0, 0, 1, 0 } },
    // 2: horizontal
    { { 0, 0, 0, 0 }, { 1, 1, 1, 1 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 } },
    // 3: vertical
    { { 0, 0, 1, 0 }, { 0, 0, 1, 0 }, { 0, 0, 1, 0 }, { 0, 0, 1, 0 } },
  };

  private static final int[][][] J_SHAPES = new int[][][] {
    // 0
    { { 1, 0, 0, 0 }, { 1, 1, 1, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 } },
    // 1
    { { 0, 1, 1, 0 }, { 0, 1, 0, 0 }, { 0, 1, 0, 0 }, { 0, 0, 0, 0 } },
    // 2
    { { 1, 1, 1, 0 }, { 0, 0, 1, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 } },
    // 3
    { { 0, 1, 0, 0 }, { 0, 1, 0, 0 }, { 1, 1, 0, 0 }, { 0, 0, 0, 0 } },
  };

  private static final int[][][] L_SHAPES = new int[][][] {
    // 0
    { { 0, 0, 1, 0 }, { 1, 1, 1, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 } },
    // 1
    { { 0, 1, 0, 0 }, { 0, 1, 0, 0 }, { 0, 1, 1, 0 }, { 0, 0, 0, 0 } },
    // 2
    { { 1, 1, 1, 0 }, { 1, 0, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 } },
    // 3
    { { 1, 1, 0, 0 }, { 0, 1, 0, 0 }, { 0, 1, 0, 0 }, { 0, 0, 0, 0 } },
  };

  private static final int[][][] O_SHAPES = new int[][][] {
    // 0
    { { 0, 1, 1, 0 }, { 0, 1, 1, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 } },
    // 1
    { { 0, 1, 1, 0 }, { 0, 1, 1, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 } },
    // 2
    { { 0, 1, 1, 0 }, { 0, 1, 1, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 } },
    // 3
    { { 0, 1, 1, 0 }, { 0, 1, 1, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 } },
  };

  private static final int[][][] S_SHAPES = new int[][][] {
    // 0
    { { 0, 1, 1, 0 }, { 1, 1, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 } },
    // 1
    { { 0, 1, 0, 0 }, { 0, 1, 1, 0 }, { 0, 0, 1, 0 }, { 0, 0, 0, 0 } },
    // 2
    { { 0, 1, 1, 0 }, { 1, 1, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 } },
    // 3
    { { 0, 1, 0, 0 }, { 0, 1, 1, 0 }, { 0, 0, 1, 0 }, { 0, 0, 0, 0 } },
  };

  private static final int[][][] T_SHAPES = new int[][][] {
    // 0
    { { 0, 1, 0, 0 }, { 1, 1, 1, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 } },
    // 1
    { { 0, 1, 0, 0 }, { 0, 1, 1, 0 }, { 0, 1, 0, 0 }, { 0, 0, 0, 0 } },
    // 2
    { { 1, 1, 1, 0 }, { 0, 1, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 } },
    // 3
    { { 0, 1, 0, 0 }, { 1, 1, 0, 0 }, { 0, 1, 0, 0 }, { 0, 0, 0, 0 } },
  };

  private static final int[][][] Z_SHAPES = new int[][][] {
    // 0
    { { 1, 1, 0, 0 }, { 0, 1, 1, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 } },
    // 1
    { { 0, 0, 1, 0 }, { 0, 1, 1, 0 }, { 0, 1, 0, 0 }, { 0, 0, 0, 0 } },
    // 2
    { { 1, 1, 0, 0 }, { 0, 1, 1, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 } },
    // 3
    { { 0, 0, 1, 0 }, { 0, 1, 1, 0 }, { 0, 1, 0, 0 }, { 0, 0, 0, 0 } },
  };
}
