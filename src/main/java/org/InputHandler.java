package org;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

public class InputHandler {

  public void install(JComponent comp, final TetrisPanel game) {
    comp.setFocusable(true);
    comp.requestFocusInWindow();

    javax.swing.InputMap im = comp.getInputMap(
      JComponent.WHEN_IN_FOCUSED_WINDOW
    );
    javax.swing.ActionMap am = comp.getActionMap();

    bind(
      im,
      am,
      "moveLeft",
      KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false),
      new Runnable() {
        public void run() {
          game.moveLeft();
        }
      }
    );
    bind(
      im,
      am,
      "moveRight",
      KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false),
      new Runnable() {
        public void run() {
          game.moveRight();
        }
      }
    );

    // Soft drop pressed/released
    im.put(
      KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false),
      "softDownPressed"
    );
    am.put(
      "softDownPressed",
      new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          game.setSoftDropping(true);
        }
      }
    );
    im.put(
      KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, true),
      "softDownReleased"
    );
    am.put(
      "softDownReleased",
      new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          game.setSoftDropping(false);
        }
      }
    );

    // Rotation: Up or X = clockwise, Z = counterclockwise
    bind(
      im,
      am,
      "rotateCWUp",
      KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false),
      new Runnable() {
        public void run() {
          game.rotateCW();
        }
      }
    );
    bind(
      im,
      am,
      "rotateCWX",
      KeyStroke.getKeyStroke(KeyEvent.VK_X, 0, false),
      new Runnable() {
        public void run() {
          game.rotateCW();
        }
      }
    );
    bind(
      im,
      am,
      "rotateCCWZ",
      KeyStroke.getKeyStroke(KeyEvent.VK_Z, 0, false),
      new Runnable() {
        public void run() {
          game.rotateCCW();
        }
      }
    );

    // Hard drop: space
    bind(
      im,
      am,
      "hardDrop",
      KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false),
      new Runnable() {
        public void run() {
          game.hardDrop();
        }
      }
    );

    // Restart: R
    bind(
      im,
      am,
      "restart",
      KeyStroke.getKeyStroke(KeyEvent.VK_R, 0, false),
      new Runnable() {
        public void run() {
          game.restart();
        }
      }
    );
  }

  private void bind(
    javax.swing.InputMap im,
    javax.swing.ActionMap am,
    String name,
    KeyStroke key,
    final Runnable action
  ) {
    im.put(key, name);
    am.put(
      name,
      new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          action.run();
        }
      }
    );
  }
}
