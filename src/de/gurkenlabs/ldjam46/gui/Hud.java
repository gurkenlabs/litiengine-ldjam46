package de.gurkenlabs.ldjam46.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.awt.image.BufferedImage;

import de.gurkenlabs.ldjam46.GameManager;
import de.gurkenlabs.ldjam46.GameManager.Day;
import de.gurkenlabs.ldjam46.GameManager.GameState;
import de.gurkenlabs.ldjam46.entities.Farmer;
import de.gurkenlabs.ldjam46.gfx.HillBillyFonts;
import de.gurkenlabs.litiengine.Align;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.Valign;
import de.gurkenlabs.litiengine.graphics.ImageRenderer;
import de.gurkenlabs.litiengine.graphics.ShapeRenderer;
import de.gurkenlabs.litiengine.graphics.Spritesheet;
import de.gurkenlabs.litiengine.graphics.TextRenderer;
import de.gurkenlabs.litiengine.gui.GuiComponent;
import de.gurkenlabs.litiengine.resources.Resources;
import de.gurkenlabs.litiengine.util.Imaging;
import de.gurkenlabs.litiengine.util.MathUtilities;
import de.gurkenlabs.litiengine.util.geom.GeometricUtilities;
import de.gurkenlabs.litiengine.util.geom.Trigonometry;

public class Hud extends GuiComponent {
  private static final int PADDING = 30;

  private static final BufferedImage PUMPKIN;
  private static final BufferedImage DROP;
  private static final BufferedImage DROP_DISABLED;
  private static final BufferedImage FART;
  private static final BufferedImage FART_DISABLED;

  private static final BufferedImage CONTROLS1;
  private static final BufferedImage CONTROLS2;

  private static final BufferedImage POCKETWATCH;
  private static final Spritesheet CAN_ORIGINAL, CAN_SCALED;
  private static final Spritesheet BEANS_ORIGINAL, BEANS_SCALED;

  final int LEVEL_INFO_DURATION = 3500;

  static {
    PUMPKIN = Imaging.scale(Resources.images().get("pumpkin-ui.png"), GameManager.INGAME_RENDER_SCALE / 2);
    DROP = Imaging.scale(Resources.images().get("drop-ui.png"), GameManager.INGAME_RENDER_SCALE / 2);
    DROP_DISABLED = Imaging.setOpacity(Imaging.scale(Resources.images().get("drop-disabled-ui.png"), GameManager.INGAME_RENDER_SCALE / 2), 0.5f);

    FART = Imaging.scale(Resources.images().get("cloud-ui.png"), GameManager.INGAME_RENDER_SCALE);
    FART_DISABLED = Imaging.setOpacity(Imaging.scale(Resources.images().get("cloud-disabled-ui.png"), GameManager.INGAME_RENDER_SCALE), 0.5f);

    CONTROLS1 = Resources.images().get("controls1.png");
    CONTROLS2 = Resources.images().get("controls2.png");

    POCKETWATCH = Imaging.scale(Resources.images().get("pocketwatch.png"), GameManager.INGAME_RENDER_SCALE);
    CAN_ORIGINAL = Resources.spritesheets().get("can");
    CAN_SCALED = new Spritesheet(Imaging.scale(CAN_ORIGINAL.getImage(), GameManager.INGAME_RENDER_SCALE), "can_scaled", CAN_ORIGINAL.getSpriteWidth() * (int) GameManager.INGAME_RENDER_SCALE, CAN_ORIGINAL.getSpriteHeight() * (int) GameManager.INGAME_RENDER_SCALE);
    BEANS_ORIGINAL = Resources.spritesheets().get("beans");
    BEANS_SCALED = new Spritesheet(Imaging.scale(BEANS_ORIGINAL.getImage(), GameManager.INGAME_RENDER_SCALE), "can_scaled", BEANS_ORIGINAL.getSpriteWidth() * (int) GameManager.INGAME_RENDER_SCALE, BEANS_ORIGINAL.getSpriteHeight() * (int) GameManager.INGAME_RENDER_SCALE);
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
    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    this.renderCurrentLevelInfo(g);

    if (GameManager.getCurrentDay() != Day.Saturday) {
      this.renderCanUI(g);
      this.renderFartUI(g);

      this.renderTime(g);

      this.renderLevelEnd(g);

      this.renderControls(g);
    } else if (GameManager.getTimeSinceLastLoad() > LEVEL_INFO_DURATION + 1000) {
      g.setColor(Color.WHITE);
      g.setFont(HillBillyFonts.UI_FONT1.deriveFont(80f));

      Valign valign = GameManager.getCurrentDay() == Day.Saturday ? Valign.MIDDLE_TOP : Valign.MIDDLE;
      TextRenderer.render(g, "YOU ARE THE PUMPKING!", Align.CENTER, valign, 0, -80);
    }
    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
  }

  private void renderFartUI(Graphics2D g) {
    if (!Farmer.instance().getFartAbility().isEnabled() || GameManager.getState() != GameState.INGAME) {
      return;
    }

    double imageLocationX = g.getClipBounds().getWidth() - BEANS_SCALED.getSpriteWidth() * 1.3d;
    double imageLocationY = g.getClipBounds().getHeight() * 5 / 8d;
    double arcLocationX = imageLocationX + BEANS_SCALED.getSpriteWidth() * 0.56d;
    double arcLocationY = imageLocationY + BEANS_SCALED.getSpriteHeight() * 0.07d;
    double arcWidth = BEANS_SCALED.getSpriteWidth() * 1 / 3d;
    double arcHeight = BEANS_SCALED.getSpriteHeight() * 1 / 4d;

    if (!Farmer.instance().getFartAbility().isOnCooldown()) {
      ImageRenderer.render(g, BEANS_SCALED.getSprite(1), imageLocationX, imageLocationY);
    } else {
      ImageRenderer.render(g, BEANS_SCALED.getSprite(0), imageLocationX, imageLocationY);
      float cooldownProgress = Farmer.instance().getFartAbility().getRemainingCooldownInSeconds() / Farmer.instance().getFartAbility().getCooldownInSeconds();
      g.setColor(Color.RED);
      ShapeRenderer.render(g, new Arc2D.Double(arcLocationX, arcLocationY, arcWidth, arcHeight, 90d, cooldownProgress * 360d, Arc2D.PIE));
    }
  }

  private void renderControls(Graphics2D g) {
    if (g.getClipBounds() == null) {
      return;
    }

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
      g.setFont(HillBillyFonts.PIXEL_UI_FONT.deriveFont(48f));
      TextRenderer.render(g, "YOU COULDN'T KEEP ENGOUGH PUMPKINS ALIVE", Align.CENTER, Valign.MIDDLE);
    }
  }

  private void renderCurrentLevelInfo(Graphics2D g) {
    final long timeSince = GameManager.getTimeSinceLastLoad();
    if (timeSince < LEVEL_INFO_DURATION && timeSince != 0) {
      g.setColor(Color.BLACK);
      g.setFont(HillBillyFonts.UI_FONT1.deriveFont(56f));

      Valign valign = GameManager.getCurrentDay() == Day.Saturday ? Valign.MIDDLE_TOP : Valign.MIDDLE;
      TextRenderer.render(g, GameManager.getCurrentDay().name(), Align.CENTER, valign, -1, 1);

      g.setColor(Color.WHITE);
      TextRenderer.render(g, GameManager.getCurrentDay().name(), Align.CENTER, valign, 0, 0);

    }

    if (GameManager.getCurrentDay() == Day.Saturday) {
      return;
    }
    if (g.getClipBounds() != null && (GameManager.getCurrentDay() != Day.Saturday && GameManager.getState() == GameState.INGAME || GameManager.isTutorialActive() && GameManager.isPumpkinCountVisible())) {
      g.setColor(Color.WHITE);
      g.setFont(HillBillyFonts.PIXEL_UI_FONT.deriveFont(24f));
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
      ImageRenderer.render(g, CAN_SCALED.getSprite(Farmer.instance().getWaterAbility().getCharges().get()), g.getClipBounds().getWidth() - CAN_SCALED.getSpriteWidth() * 1.3, g.getClipBounds().getHeight() - CAN_SCALED.getSpriteHeight() * 1.3);
    }
  }

  private void renderTime(Graphics2D g) {

    if (GameManager.getCurrentTime() == null || !GameManager.isClockVisible() || GameManager.getCurrentDay() == null) {
      return;
    }

    ImageRenderer.render(g, POCKETWATCH, g.getClipBounds().getMaxX() - POCKETWATCH.getWidth(), 0);

    // render clock arms
    double centerX = g.getClipBounds().getMaxX() - POCKETWATCH.getWidth() * 11.5 / 32d;
    double centerY = POCKETWATCH.getHeight() * 15.5 / 32d;
    int hourArmLength = 75;
    int minuteArmLength = 100;

    float degminute = (GameManager.getCurrentMinutes() / 60f) * 360 - 90;
    float deghour = ((GameManager.getCurrentHours() + GameManager.getCurrentMinutes() / 60f) / 12f) * 360 - 90;

    Line2D minuteArm = new Line2D.Double(centerX, centerY, centerX + minuteArmLength * Trigonometry.cosDeg(degminute), centerY + minuteArmLength * Trigonometry.sinDeg(degminute));
    Line2D hourArm = new Line2D.Double(centerX, centerY, centerX + hourArmLength * Trigonometry.cosDeg(deghour), centerY + hourArmLength * Trigonometry.sinDeg(deghour));

    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    g.setColor(Color.DARK_GRAY);
    g.setStroke(new BasicStroke(4f));
    g.draw(hourArm);

    g.setColor(Color.BLACK);
    g.setStroke(new BasicStroke(2f));
    g.draw(minuteArm);

    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

    Rectangle2D textBounds = new Rectangle2D.Double(g.getClipBounds().getMaxX() - 0.75 * POCKETWATCH.getWidth(), 1.1 * POCKETWATCH.getHeight(), 0.77 * POCKETWATCH.getWidth(), POCKETWATCH.getHeight() / 4);
    if (GameManager.currentHour > 15) {
      g.setFont(HillBillyFonts.MENU_FONT.deriveFont(48f));
    } else {
      g.setFont(HillBillyFonts.MENU_FONT.deriveFont(32f));
    }

    g.setColor(Color.WHITE);
    TextRenderer.render(g, GameManager.getCurrentTime(), textBounds, Align.CENTER, Valign.DOWN, false);
    g.setFont(HillBillyFonts.UI_FONT1);
    TextRenderer.render(g, GameManager.getCurrentDay().name(), textBounds, Align.CENTER, Valign.TOP, true);
  }
}
