Tetris clone built with Java Swing (JFrame/JPanel) and javax.swing.Timer. Designed to compile and run in BlueJ without external libraries.

## Controls
- Left/Right Arrow: Move piece
- Down Arrow (hold): Soft drop (+1 point per cell)
- Space: Hard drop (+2 points per cell)
- Up Arrow or X: Rotate clockwise
- Z: Rotate counterclockwise
- R: Restart

## Scoring & Levels
- Line clears (per level multiplier):
  - Single: 100 × level
  - Double: 300 × level
  - Triple: 500 × level
  - Tetris: 800 × level
- Soft drop: +1 per cell
- Hard drop: +2 per cell
- Level increases every 10 cleared lines. Falling speed increases with level.

## Notes
- Uses `javax.swing.Timer` for a steady 60 FPS tick and time-based gravity.
- Subtle gridlines, ghost piece, shaded blocks, and a right-side panel for score/level/next piece.
- Code is beginner-friendly with comments and avoids any external dependencies.
