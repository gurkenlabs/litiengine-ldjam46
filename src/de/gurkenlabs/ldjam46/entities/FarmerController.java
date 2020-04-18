package de.gurkenlabs.ldjam46.entities;

import java.awt.event.KeyEvent;

import de.gurkenlabs.litiengine.input.KeyboardEntityController;

public class FarmerController extends KeyboardEntityController<Farmer> {

  public FarmerController(Farmer entity) {
    super(entity);
  }

  @Override
  public void handlePressedKey(KeyEvent keyCode) {
    super.handlePressedKey(keyCode);
    if (keyCode.getKeyCode() == KeyEvent.VK_SPACE) {
      this.getEntity().perform("use");
    }

    if (keyCode.getKeyCode() == KeyEvent.VK_E) {
      this.getEntity().perform("fart");
    }
  }

}
