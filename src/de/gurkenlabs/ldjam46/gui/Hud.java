package de.gurkenlabs.ldjam46.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

import de.gurkenlabs.ldjam46.GameManager;
import de.gurkenlabs.ldjam46.GameManager.GameState;
import de.gurkenlabs.ldjam46.entities.Farmer;
import de.gurkenlabs.ldjam46.entities.Pumpkin;
import de.gurkenlabs.litiengine.Align;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.Valign;
import de.gurkenlabs.litiengine.graphics.ImageRenderer;
import de.gurkenlabs.litiengine.graphics.TextRenderer;
import de.gurkenlabs.litiengine.gui.GuiComponent;
import de.gurkenlabs.litiengine.resources.Resources;
import de.gurkenlabs.litiengine.util.Imaging;

public class Hud extends GuiComponent {
  private static final int PADDING = 20;

  private static final BufferedImage PUMPKIN;
  private static final BufferedImage DROP;
  private static final BufferedImage DROP_DISABLED;

  static {
    PUMPKIN = Imaging.scale(Resources.images().get("pumpkin-ui.png"), Game.graphics().getBaseRenderScale() / 2);
    DROP = Imaging.scale(Resources.images().get("drop-ui.png"), Game.graphics().getBaseRenderScale() / 2);
    DROP_DISABLED = Imaging.scale(Resources.images().get("drop-disabled-ui.png"), Game.graphics().getBaseRenderScale() / 2);
  }

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
    this.renderCanUI(g);

    this.renderTime(g);
    this.renderCurrentLevelInfo(g);

    this.renderLevelEnd(g);
  }

  private void renderLevelEnd(Graphics2D g) {
    if (GameManager.isLevelFailed()) {
      g.setColor(Color.WHITE);
      g.setFont(GameManager.GUI_FONT.deriveFont(48f));
      TextRenderer.render(g, "YOU FAILED", Align.CENTER, Valign.MIDDLE);
    }
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

    if (GameManager.getState() == GameState.INGAME && g.getClipBounds() != null) {
      TextRenderer.render(g, "req. harvest: " + GameManager.getRequiredPumpkins() + "x", Align.RIGHT, Valign.DOWN, -60, -PADDING);

      double locationX = g.getClipBounds().getX() + Align.RIGHT.getLocation(g.getClipBounds().getWidth(), PUMPKIN.getWidth()) + -PADDING;
      double locationY = g.getClipBounds().getY() + Valign.DOWN.getLocation(g.getClipBounds().getHeight(), PUMPKIN.getHeight()) + -PADDING;
      ImageRenderer.render(g, PUMPKIN, locationX, locationY);
    }
  }

  private void renderCanUI(Graphics2D g) {
    if (GameManager.getState() == GameState.INGAME && g.getClipBounds() != null) {
      for (int i = 0; i < Farmer.instance().getWaterAbility().getCharges().getMax(); i++) {
        double locationX = g.getClipBounds().getX() + Align.LEFT.getLocation(g.getClipBounds().getWidth(), DROP.getWidth()) + i * DROP.getWidth() + i * PADDING + PADDING;
        double locationY = g.getClipBounds().getY() + Valign.DOWN.getLocation(g.getClipBounds().getHeight(), DROP.getHeight()) + -PADDING;

        if (Farmer.instance().getWaterAbility().getCharges().get() > i) {
          ImageRenderer.render(g, DROP, locationX, locationY);
        } else {
          ImageRenderer.render(g, DROP_DISABLED, locationX, locationY);
        }
      }
    }
  }

  private void renderTime(Graphics2D g) {

    if (GameManager.getCurrentTime() == null) {
      return;
    }

    g.setColor(Color.WHITE);
    g.setFont(GameManager.GUI_FONT.deriveFont(24f));
    TextRenderer.render(g, GameManager.getCurrentDay().name() + ", " + GameManager.getCurrentTime(), Align.CENTER, Valign.DOWN, 0, -PADDING);

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
