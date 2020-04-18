package de.gurkenlabs.ldjam46.gui;

import java.awt.Graphics2D;

import de.gurkenlabs.litiengine.gui.screens.GameScreen;

public class IngameScreen extends GameScreen {
  private final Hud hud = new Hud();

  @Override
  public void render(Graphics2D g) {
    super.render(g);
    
    this.hud.render(g);
  }
}
