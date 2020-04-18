package de.gurkenlabs.ldjam46;

import de.gurkenlabs.litiengine.Game;

public class GameManager {

  public static void init() {
    Game.world().onLoaded(e -> {
      Game.world().camera().setFocus(e.getCenter());
    });
  }
}
