package de.gurkenlabs.ldjam46.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;

import de.gurkenlabs.ldjam46.entities.Pumpkin;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.gui.GuiComponent;

public class Hud extends GuiComponent {

  public Hud() {
    super(0, 0);
  }

  @Override
  public void render(Graphics2D g) {
    super.render(g);

    this.renderPumpkinUI(g);
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
