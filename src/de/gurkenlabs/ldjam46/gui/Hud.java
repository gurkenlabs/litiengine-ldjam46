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
  private static final int PADDING = 30;

  private static final BufferedImage PUMPKIN;
  private static final BufferedImage DROP;
  private static final BufferedImage DROP_DISABLED;
  private static final BufferedImage FART;
  private static final BufferedImage FART_DISABLED;

  private static final BufferedImage CONTROLS1;
  private static final BufferedImage CONTROLS2;

  static {
    PUMPKIN = Imaging.scale(Resources.images().get("pumpkin-ui.png"), GameManager.INGAME_RENDER_SCALE / 2);
    DROP = Imaging.scale(Resources.images().get("drop-ui.png"), GameManager.INGAME_RENDER_SCALE / 2);
    DROP_DISABLED = Imaging.setOpacity(Imaging.scale(Resources.images().get("drop-disabled-ui.png"), GameManager.INGAME_RENDER_SCALE / 2), 0.5f);

    FART = Imaging.scale(Resources.images().get("cloud-ui.png"), GameManager.INGAME_RENDER_SCALE);
    FART_DISABLED = Imaging.setOpacity(Imaging.scale(Resources.images().get("cloud-disabled-ui.png"), GameManager.INGAME_RENDER_SCALE), 0.5f);

    CONTROLS1 = Resources.images().get("controls1.png");
    CONTROLS2 = Resources.images().get("controls2.png");
  }

  public static boolean displayControl1;
  public static boolean displayControl2;

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
    this.renderFartUI(g);

    this.renderTime(g);
    this.renderCurrentLevelInfo(g);

    this.renderLevelEnd(g);

    this.renderControls(g);
  }

  private void renderFartUI(Graphics2D g) {
    if (!Farmer.instance().getFartAbility().isEnabled()) {
      return;
    }

    int cans = Farmer.instance().getWaterAbility().getCharges().getMax();

    double locationX = g.getClipBounds().getX() + Align.LEFT.getLocation(g.getClipBounds().getWidth(), FART.getWidth()) + cans * DROP.getWidth() + cans * PADDING + PADDING * 4;
    double locationY = g.getClipBounds().getY() + Valign.DOWN.getLocation(g.getClipBounds().getHeight(), FART.getHeight()) + -PADDING;

    if (!Farmer.instance().getFartAbility().isOnCooldown()) {
      ImageRenderer.render(g, FART, locationX, locationY);
    } else {
      ImageRenderer.render(g, FART_DISABLED, locationX, locationY);
    }
  }

  private void renderControls(Graphics2D g) {
    if (displayControl1) {
      double locationX = g.getClipBounds().getX() + Align.CENTER.getLocation(g.getClipBounds().getWidth(), CONTROLS1.getWidth());
      double locationY = g.getClipBounds().getY() + Valign.MIDDLE_TOP.getLocation(g.getClipBounds().getHeight(), CONTROLS1.getHeight());
      ImageRenderer.render(g, CONTROLS1, locationX, locationY);
    }

    if (displayControl2) {
      double locationX = g.getClipBounds().getX() + Align.CENTER.getLocation(g.getClipBounds().getWidth(), CONTROLS2.getWidth());
      double locationY = g.getClipBounds().getY() + Valign.MIDDLE_TOP.getLocation(g.getClipBounds().getHeight(), CONTROLS2.getHeight());
      ImageRenderer.render(g, CONTROLS2, locationX, locationY);
    }
  }

  private void renderLevelEnd(Graphics2D g) {
    if (GameManager.isLevelFailed()) {
      g.setColor(Color.WHITE);
      g.setFont(GameManager.GUI_FONT.deriveFont(48f));
      TextRenderer.render(g, "YOU FAILED", Align.CENTER, Valign.MIDDLE);
    }
  }

  private void renderCurrentLevelInfo(Graphics2D g) {
    final int LEVEL_INFO_DURATION = 3500;

    final long timeSince = GameManager.getTimeSinceLastLoad();
    if (timeSince < LEVEL_INFO_DURATION && timeSince != 0) {
      g.setColor(Color.WHITE);
      g.setFont(GameManager.GUI_FONT.deriveFont(56f));
      TextRenderer.render(g, GameManager.getCurrentDay().name(), Align.CENTER, Valign.MIDDLE, 0, 0);
    }

    if (GameManager.getState() == GameState.INGAME && g.getClipBounds() != null || GameManager.isTutorialActive() && GameManager.isPumpkinCountVisible()) {
      g.setColor(Color.WHITE);
      g.setFont(GameManager.GUI_FONT.deriveFont(24f));
      TextRenderer.render(g, "req. harvest: " + GameManager.getRequiredPumpkins() + "x", Align.RIGHT, Valign.DOWN, -70, -PADDING);

      double locationX = g.getClipBounds().getX() + Align.RIGHT.getLocation(g.getClipBounds().getWidth(), PUMPKIN.getWidth()) + -PADDING;
      double locationY = g.getClipBounds().getY() + Valign.DOWN.getLocation(g.getClipBounds().getHeight(), PUMPKIN.getHeight()) + -PADDING;
      ImageRenderer.render(g, PUMPKIN, locationX, locationY);
    }
  }

  private void renderCanUI(Graphics2D g) {
    if (!Farmer.instance().hasCan()) {
      return;
    }

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

    if (GameManager.getCurrentTime() == null || !GameManager.isClockVisible()) {
      return;
    }

    String currentTime = GameManager.getCurrentTime();
    if (currentTime.equals("6:00 PM")) {
      g.setFont(GameManager.GUI_FONT.deriveFont(48f));
    } else {
      g.setFont(GameManager.GUI_FONT.deriveFont(24f));
    }

    g.setColor(Color.WHITE);

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
