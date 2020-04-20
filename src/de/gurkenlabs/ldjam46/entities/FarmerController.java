package de.gurkenlabs.ldjam46.entities;

import java.awt.event.KeyEvent;

import de.gurkenlabs.ldjam46.GameManager;
import de.gurkenlabs.ldjam46.GameManager.Day;
import de.gurkenlabs.ldjam46.GameManager.GameState;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.input.KeyboardEntityController;

public class FarmerController extends KeyboardEntityController<Farmer> {

  public FarmerController(Farmer entity) {
    super(entity);
    this.addUpKey(KeyEvent.VK_UP);
    this.addDownKey(KeyEvent.VK_DOWN);
    this.addLeftKey(KeyEvent.VK_LEFT);
    this.addRightKey(KeyEvent.VK_RIGHT);
  }

  @Override
  public void handlePressedKey(KeyEvent keyCode) {
    if (Game.isDebug() && keyCode.getKeyCode() == KeyEvent.VK_F5 && GameManager.getState() == GameState.INGAME) {
      GameManager.loadDay(GameManager.getCurrentDay());
    }

    if (Game.isDebug() && keyCode.getKeyCode() == KeyEvent.VK_F6 && GameManager.getState() == GameState.INGAME) {
      GameManager.levelTransition();
    }

    if (GameManager.getState() != GameState.INGAME || Farmer.instance().movementBlocked || GameManager.getCurrentDay() == Day.Saturday) {
      this.getEntity().setAcceleration(0);
      this.setDx(0);
      this.setVelocityX(0);
      this.setDy(0);
      this.setVelocityY(0);
      return;
    }

    super.handlePressedKey(keyCode);

    if (keyCode.getKeyCode() == KeyEvent.VK_SPACE) {
      this.getEntity().perform("use");
    }

    if (keyCode.getKeyCode() == KeyEvent.VK_E || keyCode.getKeyCode() == KeyEvent.VK_X) {
      this.getEntity().perform("fart");
    }

  }

}
