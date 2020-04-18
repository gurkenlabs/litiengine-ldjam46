package de.gurkenlabs.ldjam46;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import de.gurkenlabs.ldjam46.entities.EnemyFarmer;
import de.gurkenlabs.ldjam46.entities.Farmer;
import de.gurkenlabs.ldjam46.entities.Pumpkin;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.entities.Spawnpoint;
import de.gurkenlabs.litiengine.entities.behavior.AStarGrid;
import de.gurkenlabs.litiengine.entities.behavior.AStarNode;
import de.gurkenlabs.litiengine.environment.CreatureMapObjectLoader;
import de.gurkenlabs.litiengine.environment.PropMapObjectLoader;
import de.gurkenlabs.litiengine.gui.GuiProperties;
import de.gurkenlabs.litiengine.resources.Resources;

public final class GameManager {
  public static final Font GUI_FONT = Resources.fonts().get("fsex300.ttf").deriveFont(10f);

  public static final String MAP_PLAYGROUND = "playground";
  private static final Map<String, List<EnemyFarmerSpawnEvent>> spawnEvents = new ConcurrentHashMap<>();
  private static final Map<String, AStarGrid> grids = new ConcurrentHashMap<>();

  static {
    spawnEvents.put(MAP_PLAYGROUND, new ArrayList<>());
    spawnEvents.get(MAP_PLAYGROUND).add(new EnemyFarmerSpawnEvent("enemy", 5000));
    spawnEvents.get(MAP_PLAYGROUND).add(new EnemyFarmerSpawnEvent("enemy", 10000));
    spawnEvents.get(MAP_PLAYGROUND).add(new EnemyFarmerSpawnEvent("enemy", 15000));
    spawnEvents.get(MAP_PLAYGROUND).add(new EnemyFarmerSpawnEvent("enemy", 20000));
    spawnEvents.get(MAP_PLAYGROUND).add(new EnemyFarmerSpawnEvent("enemy2", 30000));
  }

  public static void init() {
    GuiProperties.setDefaultFont(GUI_FONT);
    Game.audio().setListenerLocationCallback((e) -> Farmer.instance().getCenter());

    CreatureMapObjectLoader.registerCustomCreatureType(Farmer.class);
    PropMapObjectLoader.registerCustomPropType(Pumpkin.class);

    Game.world().onLoaded(e -> {
      Game.world().camera().setFocus(e.getCenter());
      Spawnpoint spawn = e.getSpawnpoint("farmer");
      if (spawn != null) {
        spawn.spawn(Farmer.instance());
      }
      AStarGrid grid = new AStarGrid(e.getMap().getSizeInPixels(), 8);
      for (Pumpkin pumpkin : e.getEntities(Pumpkin.class)) {
        for (AStarNode node : grid.getIntersectedNodes(pumpkin.getBoundingBox())) {
          node.setPenalty(AStarGrid.PENALTY_STATIC_PROP);
        }
      }
      grid.setAllowCuttingCorners(false);
      grids.put(e.getMap().getName(), grid);
    });

    Game.loop().attach(GameManager::update);
  }

  public static AStarGrid getCurrentGrid() {
    if (Game.world().environment() == null || !grids.containsKey(Game.world().environment().getMap().getName())) {
      return null;
    }

    return grids.get(Game.world().environment().getMap().getName());
  }

  private static void update() {
    handleEnemyFarmerSpawns();
  }

  private static void handleEnemyFarmerSpawns() {
    if (Game.world().environment() == null) {
      return;
    }

    if (!spawnEvents.containsKey(Game.world().environment().getMap().getName())) {
      return;
    }

    for (EnemyFarmerSpawnEvent event : spawnEvents.get(Game.world().environment().getMap().getName())) {
      if (event.finished) {
        continue;
      }

      if (Game.time().sinceEnvironmentLoad() >= event.time) {
        spawnEnemyFarmer(event);
      }
    }
  }

  private static void spawnEnemyFarmer(EnemyFarmerSpawnEvent event) {
    event.finished = true;

    Spawnpoint spawn = Game.world().environment().getSpawnpoint(event.spawnPoint);
    if (spawn == null) {
      System.out.println("Spawn " + event.spawnPoint + " could not be found on map " + Game.world().environment().getMap().getName());
      return;
    }

    spawn.spawn(new EnemyFarmer(spawn));
  }

  private static class EnemyFarmerSpawnEvent {
    private final String spawnPoint;
    private final int time;
    private boolean finished;

    public EnemyFarmerSpawnEvent(String spawnPoint, int time) {
      this.spawnPoint = spawnPoint;
      this.time = time;
    }
  }
}
