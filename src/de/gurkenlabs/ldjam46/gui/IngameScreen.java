package de.gurkenlabs.ldjam46.gui;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import de.gurkenlabs.ldjam46.GameManager;
import de.gurkenlabs.ldjam46.GameManager.GameState;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.gui.screens.GameScreen;

public class IngameScreen extends GameScreen {
  private static final int CINEMATIC_BORDER = 100;
  private final Hud hud = new Hud();

  @Override
  public void render(Graphics2D g) {

    if (GameManager.getState() != GameState.INGAME) {
      g.setClip(new Rectangle2D.Double(0, CINEMATIC_BORDER, Game.window().getResolution().getWidth(), Game.window().getResolution().getHeight() - CINEMATIC_BORDER * 2));
    }

    super.render(g);

    this.hud.render(g);
  }

  @Override
  public void prepare() {
    // TODO Auto-generated method stub
    super.prepare();

    Game.graphics().setBaseRenderScale(4.001f);
  }
}
