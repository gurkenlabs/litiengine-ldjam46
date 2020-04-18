package de.gurkenlabs.ldjam46;

import de.gurkenlabs.ldjam46.entities.Farmer;
import de.gurkenlabs.ldjam46.entities.Pumpkin;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.entities.Spawnpoint;
import de.gurkenlabs.litiengine.environment.CreatureMapObjectLoader;
import de.gurkenlabs.litiengine.environment.PropMapObjectLoader;

public class GameManager {

  public static void init() {
    CreatureMapObjectLoader.registerCustomCreatureType(Farmer.class);
    PropMapObjectLoader.registerCustomPropType(Pumpkin.class);

    Game.world().onLoaded(e -> {
      Game.world().camera().setFocus(e.getCenter());
      Spawnpoint spawn = e.getSpawnpoint("farmer");
      if (spawn != null) {
        spawn.spawn(Farmer.instance());
      }
    });
  }
}
