package de.gurkenlabs.ldjam46;

import java.awt.Color;
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
import de.gurkenlabs.litiengine.environment.Environment;
import de.gurkenlabs.litiengine.environment.EnvironmentListener;
import de.gurkenlabs.litiengine.environment.PropMapObjectLoader;
import de.gurkenlabs.litiengine.gui.GuiProperties;
import de.gurkenlabs.litiengine.gui.SpeechBubbleAppearance;
import de.gurkenlabs.litiengine.resources.Resources;

public final class GameManager {
  public enum GameState {
    MENU,
    LOADING,
    LOCKED,
    INGAME,
  }

  public enum Day {
    Monday(1, "Howdy partner, let's learn how to farm, aii.."),
    Tuesday(2, "Well that just dills my pickle!"),
    Wednesday(3, "These farmers are nuttier than a squirrel turd!"),
    Thursday(4, "I gotta hit the bushes."),
    Friday(5, "I’m as busy as a one-legged cat in a sandbox!"),
    Saturday(6, "Don't put your cart before your horse."),
    Sunday(7, "Today I'm happy as a dead pig in the sunshine!");

    private final String description;
    private final int day;

    private Day(int day, String description) {
      this.day = day;
      this.description = description;
    }

    public String getDescription() {
      return description;
    }

    public int getDay() {
      return this.day;
    }

    public Day getNext() {
      switch (this) {
      case Monday:
        return Tuesday;
      case Tuesday:
        return Wednesday;
      case Wednesday:
        return Thursday;
      case Thursday:
        return Friday;
      case Friday:
        return Saturday;
      case Saturday:
        return Sunday;
      default:
        return null;
      }
    }
  }

  public static final Font GUI_FONT = Resources.fonts().get("fsex300.ttf").deriveFont(10f);
  public static final Font SPEECHBUBBLE_FONT = GUI_FONT.deriveFont(6f);
  public static final SpeechBubbleAppearance SPEECHBUBBLE_APPEARANCE = new SpeechBubbleAppearance(Color.BLACK, new Color(255, 255, 255, 200), Color.BLACK, 2);

  public static final String MAP_PLAYGROUND = "playground";
  private static final Map<String, List<EnemyFarmerSpawnEvent>> spawnEvents = new ConcurrentHashMap<>();
  private static final Map<String, AStarGrid> grids = new ConcurrentHashMap<>();
  private static final Map<Day, String> maps = new ConcurrentHashMap<>();

  private static GameState state;

  private static Day currentDay;
  private static long ingameStartedTick;

  // time properties
  private static String currentTime;
  private static int currentHour;
  private static int currentMinutes;

  private static long lastLoaded;

  private static boolean levelFailed;

  // TODO: End screen after every day
  // TODO: track score (alive pumpkins * life)
  static {
    maps.put(Day.Monday, "monday");
    maps.put(Day.Tuesday, "tuesday");
    maps.put(Day.Wednesday, "playground");
    maps.put(Day.Thursday, "playground");
    maps.put(Day.Friday, "playground");
    maps.put(Day.Saturday, "playground");
    maps.put(Day.Sunday, "playground");

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
    Game.audio().setMaxDistance(300);

    CreatureMapObjectLoader.registerCustomCreatureType(Farmer.class);
    PropMapObjectLoader.registerCustomPropType(Pumpkin.class);

    Game.world().addListener(new EnvironmentListener() {
      @Override
      public void initialized(Environment e) {
        if (e.getMap().getName().equals("playground")) {
          Farmer.instance().getWaterAbility().getCharges().setBaseValue(2);
        } else {
          Farmer.instance().getWaterAbility().getCharges().setToMin();
        }

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
      };
    });

    Game.loop().attach(GameManager::update);
  }

  public static void levelTransition() {
    if (currentDay == null) {
      currentDay = Day.Monday;
    } else {
      currentDay = currentDay.getNext();
    }
    
    // TODO currentDay null -> transition to menu screen

    Farmer.instance().getFartAbility().setEnabled(currentDay.getDay() >= Day.Wednesday.getDay());

    loadCurrentDay();
  }

  public static synchronized void loadCurrentDay() {
    if (state == GameState.LOADING) {
      return;
    }

    state = GameState.LOADING;

    String currentMap = maps.get(currentDay);

    Game.window().getRenderComponent().fadeOut(1000);
    Game.loop().perform(1000, () -> {
      currentTime = null;
      ingameStartedTick = 0;
      if (Game.world().environment() != null && Game.world().environment().getMap().getName().equals(maps.get(currentDay))) {

        Game.world().environment().remove(Farmer.instance());
        Game.world().reset(maps.get(currentDay));
      }

      if (spawnEvents.containsKey(currentMap)) {
        for (EnemyFarmerSpawnEvent event : spawnEvents.get(currentMap)) {
          event.finished = false;
        }
      }

      Game.world().loadEnvironment(maps.get(currentDay));
      Game.world().environment().getAmbientLight().setColor(new Color(51, 51, 255, 50));

      Game.window().getRenderComponent().fadeIn(1000);

      Game.loop().perform(1000, () -> {
        state = GameState.LOCKED;
        lastLoaded = Game.loop().getTicks();
      });

      Game.loop().perform(6000, () -> {
        ingameStartedTick = Game.loop().getTicks();
        state = GameState.INGAME;
      });
    });
  }

  public static AStarGrid getCurrentGrid() {
    if (Game.world().environment() == null || !grids.containsKey(Game.world().environment().getMap().getName())) {
      return null;
    }

    return grids.get(Game.world().environment().getMap().getName());
  }

  public static long getTimeSinceLastLoad() {
    return state == GameState.LOADING ? 0 : Game.time().since(lastLoaded);
  }

  public static Day getCurrentDay() {
    return currentDay;
  }

  public static GameState getState() {
    return state;
  }

  private static void update() {
    if (Game.world().environment() == null) {
      return;
    }

    handleEnemyFarmerSpawns();

    handleDayTime();
  }

  private static void handleDayTime() {
    if (getState() != GameState.INGAME) {
      return;
    }

    final int STARTING = 6;
    final int ENDING = 18;

    final double DAY_LENGTH = 2.0; // minutes
    final double DAY_LENGTH_IN_MS = DAY_LENGTH * 60 * 1000;
    final double HOUR_LENGTH = DAY_LENGTH_IN_MS / (ENDING - STARTING);
    final double MINUTE_LENGTH = DAY_LENGTH_IN_MS / (ENDING - STARTING) / 60;

    // time in ms
    long elapsed = ingameStartedTick != 0 ? Game.time().since(ingameStartedTick) : 0;

    int hour = (int) (elapsed / HOUR_LENGTH) + STARTING % 24;
    currentMinutes = (int) (elapsed % HOUR_LENGTH / MINUTE_LENGTH);

    if (hour > currentHour) {
      adjustAmbientLight(hour);
    }

    currentHour = hour;

    String ampm = currentHour >= 12 ? "PM" : "AM";

    if (currentHour == ENDING && currentMinutes > 0 || currentHour > ENDING) {
      state = GameState.LOCKED;
      // TODO show result and either reload current map or transition to the next level
      levelTransition();
      return;
    }

    long formatHours = currentHour;
    if (currentHour > 12) {
      formatHours = currentHour % 12;
    }

    currentTime = formatHours + ":" + String.format("%02d", currentMinutes) + " " + ampm;
  }

  private static void adjustAmbientLight(int hour) {
    if (Game.world().environment() == null || Game.world().environment().getAmbientLight() == null) {
      return;
    }

    if (hour < 7 || hour >= 17) {
      Game.world().environment().getAmbientLight().setColor(new Color(51, 51, 255, 50));
    }

    if (hour >= 7 && hour < 9) {
      Game.world().environment().getAmbientLight().setColor(new Color(53, 233, 123, 30));
    }

    if (hour >= 9 && hour < 12) {
      Game.world().environment().getAmbientLight().setColor(new Color(181, 233, 53, 29));
    }

    if (hour >= 12 && hour < 15) {
      Game.world().environment().getAmbientLight().setColor(new Color(233, 176, 53, 39));
    }

    if (hour >= 15 && hour < 17) {
      Game.world().environment().getAmbientLight().setColor(new Color(233, 51, 122, 19));
    }
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

      if (ingameStartedTick > 0 && Game.time().since(ingameStartedTick) >= event.time) {
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

  public static String getCurrentTime() {
    return currentTime;
  }

  public static int getRequiredPumpkins() {
    if (Game.world().environment() == null) {
      return 0;
    }

    return Game.world().environment().getMap().getIntValue("minPumpkins", Game.world().environment().getEntities(Pumpkin.class).size() / 2);
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

  public static void trackPumpkinDeath(Pumpkin pumpkin) {
    int alive = Game.world().environment().getEntities(Pumpkin.class, x -> !x.isDead()).size();
    if (alive < getRequiredPumpkins()) {
      state = GameState.LOCKED;
      levelFailed = true;

      Game.loop().perform(5000, () -> {
        levelFailed = false;
        loadCurrentDay();
      });
    } else {
      // TODO pumpkin died events
    }
  }

  public static boolean isLevelFailed() {
    return levelFailed;
  }
}
