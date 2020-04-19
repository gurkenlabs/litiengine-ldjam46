package de.gurkenlabs.ldjam46.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;

import de.gurkenlabs.ldjam46.GameManager;
import de.gurkenlabs.ldjam46.entities.Pumpkin;
import de.gurkenlabs.litiengine.Align;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.Valign;
import de.gurkenlabs.litiengine.graphics.TextRenderer;
import de.gurkenlabs.litiengine.gui.GuiComponent;

public class Hud extends GuiComponent {

  public Hud() {
    super(0, 0);
  }

  @Override
  public void render(Graphics2D g) {
    super.render(g);
    if (Game.world().environment() == null) {
      return;
    }

    this.renderPumpkinUI(g);

    this.renderTime(g);
    this.renderCurrentLevelInfo(g);
  }

  private void renderCurrentLevelInfo(Graphics2D g) {
    final int LEVEL_INFO_DURATION = 5000;
    final int LEVEL_DESC_DURATION = 1500;

    final long timeSince = GameManager.getTimeSinceLastLoad();
    if (timeSince < LEVEL_INFO_DURATION && timeSince != 0) {
      g.setColor(Color.WHITE);
      g.setFont(GameManager.GUI_FONT.deriveFont(48f));
      TextRenderer.render(g, GameManager.getCurrentDay().name(), Align.CENTER, Valign.MIDDLE, 0, 0);

      if (timeSince > LEVEL_DESC_DURATION) {
        g.setFont(GameManager.GUI_FONT.deriveFont(24f));
        TextRenderer.render(g, GameManager.getCurrentDay().getDescription(), Align.CENTER, Valign.MIDDLE, 0, 70);
      }
    }
  }

  private void renderTime(Graphics2D g) {

    if (GameManager.getCurrentTime() == null) {
      return;
    }

    g.setColor(Color.WHITE);
    g.setFont(GameManager.GUI_FONT.deriveFont(24f));
    TextRenderer.render(g, GameManager.getCurrentTime(), Align.CENTER, Valign.DOWN, 0, -20);

  }

  private void renderPumpkinUI(Graphics2D g) {
    for (Pumpkin pumpkin : Game.world().environment().getEntities(Pumpkin.class)) {
      if (!pumpkin.isDead()) {
        final double width = 16;
        final double height = 2;
        double x = pumpkin.getX() - (width - pumpkin.getWidth()) / 2.0;
        double y = pumpkin.getY() - height * 2;
        RoundRectangle2D rect = new RoundRectangle2D.Double(x, y, width, height, 1.5, 1.5);

        final double currentWidth = width * (pumpkin.getHitPoints().get() / (double) pumpkin.getHitPoints().getMax());
        RoundRectangle2D actualRect = new RoundRectangle2D.Double(x, y, currentWidth, height, 1.5, 1.5);

        g.setColor(new Color(40, 42, 43, 150));
        Game.graphics().renderShape(g, rect);

        g.setColor(new Color(228, 59, 68));
        Game.graphics().renderShape(g, actualRect);
      }
    }
  }
}
