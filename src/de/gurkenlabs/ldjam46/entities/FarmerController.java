package de.gurkenlabs.ldjam46.entities;

import java.awt.event.KeyEvent;

import de.gurkenlabs.ldjam46.GameManager;
import de.gurkenlabs.ldjam46.GameManager.GameState;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.input.KeyboardEntityController;

public class FarmerController extends KeyboardEntityController<Farmer> {

  public FarmerController(Farmer entity) {
    super(entity);
  }

  @Override
  public void handlePressedKey(KeyEvent keyCode) {
    if (Game.isDebug() && keyCode.getKeyCode() == KeyEvent.VK_F5) {
      GameManager.loadCurrentDay();
    }
    
    if (GameManager.getState() != GameState.INGAME) {
      return;
    }

    super.handlePressedKey(keyCode);
    if (keyCode.getKeyCode() == KeyEvent.VK_SPACE) {
      this.getEntity().perform("use");
    }

    if (keyCode.getKeyCode() == KeyEvent.VK_E) {
      this.getEntity().perform("fart");
    }

  }

}
