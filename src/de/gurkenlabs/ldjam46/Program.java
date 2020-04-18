package de.gurkenlabs.ldjam46;

import de.gurkenlabs.ldjam46.gui.IngameScreen;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.resources.Resources;

public class Program {

  /**
   * The main entry point for the Game.
   * 
   * @param args
   *          The command line arguments.
   */
  public static void main(String[] args) {
    // set meta information about the game
    Game.info().setName("Pumpkin Keeper");
    Game.info().setSubTitle("");
    Game.info().setVersion("v0.0.1");
    Game.info().setWebsite("https://github.com/gurkenlabs/litiengine-ldjam46");
    Game.info().setDescription("");

    // init the game infrastructure
    Game.init(args);

    GameManager.init();

    // set the icon for the game (this has to be done after initialization because the ScreenManager will not be present otherwise)
    Game.window().setIcon(Resources.images().get("icon.png"));
    Game.graphics().setBaseRenderScale(4.001f);

    // load data from the utiLITI game file
    Resources.load("game.litidata");
    Game.screens().add(new IngameScreen());

    Game.start();
    GameManager.levelTransition();
  }
}