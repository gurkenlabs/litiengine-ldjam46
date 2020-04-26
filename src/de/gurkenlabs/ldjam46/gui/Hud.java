package de.gurkenlabs.ldjam46.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import de.gurkenlabs.ldjam46.GameManager;
import de.gurkenlabs.ldjam46.GameManager.Day;
import de.gurkenlabs.ldjam46.GameManager.GameState;
import de.gurkenlabs.ldjam46.entities.Farmer;
import de.gurkenlabs.ldjam46.entities.Pumpkin;
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
import de.gurkenlabs.litiengine.util.geom.Trigonometry;

public class Hud extends GuiComponent {
  private static final Color BG_COLOR;
  private static final Color CONTOUR_COLOR;

  private static final BufferedImage PUMPKIN;
  private static final BufferedImage PUMPKIN_DISABLED;

  private static final BufferedImage CONTROLS1;
  private static final BufferedImage CONTROLS2;

  private static final BufferedImage POCKETWATCH;
  private static final Spritesheet CAN_ORIGINAL, CAN_SCALED;
  private static final Spritesheet BEANS_ORIGINAL, BEANS_SCALED;

  final int LEVEL_INFO_DURATION = 3500;

  static {
    PUMPKIN = Imaging.scale(Resources.images().get("pumpkin-alive.png"), GameManager.INGAME_RENDER_SCALE * 0.8);
    PUMPKIN_DISABLED = Imaging.scale(Resources.images().get("pumpkin-dead.png"), GameManager.INGAME_RENDER_SCALE * 0.8);

    CONTROLS1 = Resources.images().get("controls1.png");
    CONTROLS2 = Resources.images().get("controls2.png");

    POCKETWATCH = Imaging.scale(Resources.images().get("pocketwatch.png"), GameManager.INGAME_RENDER_SCALE);
    CAN_ORIGINAL = Resources.spritesheets().get("can");
    CAN_SCALED = new Spritesheet(Imaging.scale(CAN_ORIGINAL.getImage(), GameManager.INGAME_RENDER_SCALE), "can_scaled", CAN_ORIGINAL.getSpriteWidth() * (int) GameManager.INGAME_RENDER_SCALE, CAN_ORIGINAL.getSpriteHeight() * (int) GameManager.INGAME_RENDER_SCALE);
    BEANS_ORIGINAL = Resources.spritesheets().get("beans");
    BEANS_SCALED = new Spritesheet(Imaging.scale(BEANS_ORIGINAL.getImage(), GameManager.INGAME_RENDER_SCALE), "can_scaled", BEANS_ORIGINAL.getSpriteWidth() * (int) GameManager.INGAME_RENDER_SCALE, BEANS_ORIGINAL.getSpriteHeight() * (int) GameManager.INGAME_RENDER_SCALE);

    BG_COLOR = new Color(0, 0, 0, 80);
    CONTOUR_COLOR = new Color(76, 46, 32, 150);
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
    TextRenderer.enableTextAntiAliasing(g);
    this.renderCurrentLevelInfo(g);
    if (GameManager.getCurrentDay() != Day.Saturday) {
      this.renderCanUI(g);
      this.renderFartUI(g);

      this.renderTime(g);

      this.renderLevelEnd(g);

      this.renderControls(g);
    } else if (Game.time().sinceEnvironmentLoad() > LEVEL_INFO_DURATION + 1000) {
      g.setColor(Color.WHITE);
      g.setFont(HillBillyFonts.UI.deriveFont(80f));

      Valign valign = GameManager.getCurrentDay() == Day.Saturday ? Valign.MIDDLE_TOP : Valign.MIDDLE;
      TextRenderer.render(g, "YOU ARE THE PUMPKING!", Align.CENTER, valign, 0, -80);
      g.setColor(Color.WHITE);
    }
  }

  private void renderFartUI(Graphics2D g) {
    if (!Farmer.instance().getFartAbility().isEnabled() || GameManager.getState() != GameState.INGAME || g.getClipBounds() == null) {
      return;
    }
    double imageLocationX = (g.getClipBounds().getWidth() * 31 / 32d) - BEANS_SCALED.getSpriteWidth();
    double imageLocationY = (g.getClipBounds().getHeight() * 31 / 32d) - BEANS_SCALED.getSpriteHeight();
    double arcLocationX = imageLocationX + BEANS_SCALED.getSpriteWidth() * 1 / 36d;
    double arcLocationY = imageLocationY + BEANS_SCALED.getSpriteHeight() * 18 / 47d;
    double arcWidth = BEANS_SCALED.getSpriteWidth() * 22 / 36d;
    double arcHeight = BEANS_SCALED.getSpriteHeight() * 5 / 47d;

    if (!Farmer.instance().getFartAbility().isOnCooldown()) {
      ImageRenderer.render(g, BEANS_SCALED.getSprite(1), imageLocationX, imageLocationY);
    } else {
      ImageRenderer.render(g, BEANS_SCALED.getSprite(0), imageLocationX, imageLocationY);
      float cooldownProgress = Farmer.instance().getFartAbility().getRemainingCooldownInSeconds() / Farmer.instance().getFartAbility().getCooldownInSeconds();
      g.setColor(Color.WHITE);
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      ShapeRenderer.render(g, new Arc2D.Double(arcLocationX, arcLocationY, arcWidth, arcHeight, 90d, cooldownProgress * 360d, Arc2D.PIE));
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
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
      g.setColor(Color.BLACK);
      g.setFont(HillBillyFonts.UI.deriveFont(48f));
      TextRenderer.render(g, "YOU COULDN'T KEEP ENGOUGH PUMPKINS ALIVE", Align.CENTER, Valign.MIDDLE, -2, 2);
      g.setColor(Color.WHITE);
      TextRenderer.render(g, "YOU COULDN'T KEEP ENGOUGH PUMPKINS ALIVE", Align.CENTER, Valign.MIDDLE);
    }
  }

  private void renderCurrentLevelInfo(Graphics2D g) {
    final long timeSince = GameManager.getTimeSinceLastLoad();
    if (timeSince < LEVEL_INFO_DURATION && timeSince != 0) {
      g.setColor(Color.BLACK);
      g.setFont(HillBillyFonts.UI.deriveFont(56f));

      Valign valign = GameManager.getCurrentDay() == Day.Saturday ? Valign.MIDDLE_TOP : Valign.MIDDLE;
      TextRenderer.render(g, GameManager.getCurrentDay().name(), Align.CENTER, valign, -2, 2);

      g.setColor(Color.WHITE);
      TextRenderer.render(g, GameManager.getCurrentDay().name(), Align.CENTER, valign, 0, 0);

    }

    if (g.getClipBounds() == null) {
      return;
    }
    double pumpkinOffset;
    double backgroundWidth;
    double backgroundHeight = g.getClipBounds().getHeight() * 1 / 20d;
    double backgroundX;
    double backgroundY;
    double pumpkinY;
    double minLineX;
    double minLineY1;
    double minLineY2;

    int alive;
    int dead;
    if ((GameManager.getCurrentDay() != Day.Saturday && GameManager.getState() == GameState.INGAME || GameManager.isTutorialActive() && GameManager.isPumpkinCountVisible())) {
      pumpkinOffset = g.getClipBounds().getWidth() * 1 / 128d;
      backgroundWidth = (Game.world().environment().getEntities(Pumpkin.class).size() * (PUMPKIN.getWidth() + pumpkinOffset) + pumpkinOffset);
      backgroundX = g.getClipBounds().getWidth() / 2d - backgroundWidth / 2d;
      backgroundY = g.getClipBounds().getHeight() - backgroundHeight;
      pumpkinY = g.getClipBounds().getHeight() - backgroundHeight / 2d - PUMPKIN.getHeight() / 2d;
      alive = Game.world().environment().getEntities(Pumpkin.class, x -> !x.isDead()).size();
      dead = Game.world().environment().getEntities(Pumpkin.class, Pumpkin::isDead).size();
      minLineX = backgroundX + (GameManager.getRequiredPumpkins() * (PUMPKIN.getWidth() + pumpkinOffset) + pumpkinOffset / 2d);
      minLineY1 = pumpkinY - 5;
      minLineY2 = minLineY1 + PUMPKIN.getHeight() + 10;
    } else if (GameManager.getCurrentDay() == Day.Saturday && GameManager.getState() == GameState.LOCKED && Game.time().sinceEnvironmentLoad() > LEVEL_INFO_DURATION + 1000) {
      pumpkinOffset = g.getClipBounds().getWidth() * 1 / 256d;
      backgroundWidth = ((GameManager.getTotalHarvestedPumpkins() + GameManager.getTotalDeadPumpkins()) * (PUMPKIN.getWidth() + pumpkinOffset) + pumpkinOffset);
      backgroundX = g.getClipBounds().getWidth() / 2d - backgroundWidth / 2d;
      backgroundY = g.getClipBounds().getHeight() * 4 / 5d;
      pumpkinY = backgroundY + backgroundHeight / 2d - PUMPKIN.getHeight() / 2d;
      alive = GameManager.getTotalHarvestedPumpkins();
      dead = GameManager.getTotalDeadPumpkins();
      minLineX = backgroundX + (GameManager.getTotalRequiredPumpkins() * (PUMPKIN.getWidth() + pumpkinOffset) + pumpkinOffset / 2d);
      minLineY1 = pumpkinY - 5;
      minLineY2 = minLineY1 + PUMPKIN.getHeight() + 10;
    } else {
      return;
    }
    g.setColor(BG_COLOR);
    g.fillRect((int) (backgroundX), (int) (backgroundY), (int) (backgroundWidth), (int) (backgroundHeight));
    g.setColor(CONTOUR_COLOR);
    g.drawRect((int) (backgroundX), (int) (backgroundY), (int) (backgroundWidth), (int) (backgroundHeight));

    for (int i = 0; i < alive; i++) {
      ImageRenderer.render(g, PUMPKIN, backgroundX + (i * PUMPKIN.getWidth()) + ((i + 1) * pumpkinOffset), pumpkinY);
    }

    for (int i = 0; i < dead; i++) {
      ImageRenderer.render(g, PUMPKIN_DISABLED, backgroundX + ((alive + i) * PUMPKIN_DISABLED.getWidth()) + ((alive + i + 1) * pumpkinOffset), pumpkinY);
    }

    g.setColor(Color.WHITE);
    g.setStroke(new BasicStroke(3f));
    g.draw(new Line2D.Double(minLineX, minLineY1, minLineX, minLineY2));

    g.setColor(Color.BLACK);
    g.setFont(HillBillyFonts.MENU.deriveFont(24f));

    String min = "min.";
    TextRenderer.render(g, min, minLineX - g.getFontMetrics().stringWidth(min) / 2d - 2, backgroundY - 3 + 2);
    g.setColor(Color.WHITE);
    TextRenderer.render(g, min, minLineX - g.getFontMetrics().stringWidth(min) / 2d, backgroundY - 3);

    // System.out.println(String.format("harvested: %d. dead: %d. required: %d",
    // GameManager.getTotalHarvestedPumpkins(),
    // GameManager.getTotalDeadPumpkins(),
    // GameManager.getTotalRequiredPumpkins()));

  }

  private void renderCanUI(Graphics2D g) {
    if (!Farmer.instance().hasCan()) {
      return;
    }

    if (GameManager.getState() == GameState.INGAME && g.getClipBounds() != null) {
      double canX = g.getClipBounds().getWidth() * 1 / 32d;
      double canY = (g.getClipBounds().getHeight() * 31 / 32d) - CAN_SCALED.getSpriteHeight();

      ImageRenderer.render(g, CAN_SCALED.getSprite(Farmer.instance().getWaterAbility().getCharges().get()), canX, canY);
    }
  }

  private void renderTime(Graphics2D g) {

    if (GameManager.getCurrentTime() == null || !GameManager.isClockVisible() || GameManager.getCurrentDay() == null || g.getClipBounds() == null) {
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

    g.setColor(Color.DARK_GRAY);
    g.setStroke(new BasicStroke(4f));
    g.draw(hourArm);

    g.setColor(Color.BLACK);
    g.setStroke(new BasicStroke(2f));
    g.draw(minuteArm);

    Rectangle2D textBounds = new Rectangle2D.Double(g.getClipBounds().getMaxX() - 0.75 * POCKETWATCH.getWidth(), 1.1 * POCKETWATCH.getHeight(), 0.77 * POCKETWATCH.getWidth(), POCKETWATCH.getHeight() / 4);
    if (GameManager.getCurrentHours() > 15) {
      g.setFont(HillBillyFonts.MENU.deriveFont(48f));
    } else {
      g.setFont(HillBillyFonts.MENU.deriveFont(32f));
    }

    g.setColor(Color.WHITE);
    TextRenderer.render(g, GameManager.getCurrentTime(), textBounds, Align.CENTER, Valign.DOWN, false);
    g.setFont(HillBillyFonts.UI);
    g.setColor(Color.BLACK);
    TextRenderer.render(g, GameManager.getCurrentDay().name(), textBounds, Align.CENTER, Valign.TOP, -2, 2, true);
    g.setColor(Color.WHITE);
    TextRenderer.render(g, GameManager.getCurrentDay().name(), textBounds, Align.CENTER, Valign.TOP, true);
  }
}
