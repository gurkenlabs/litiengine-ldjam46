package de.gurkenlabs.ldjam46.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import de.gurkenlabs.ldjam46.GameManager;
import de.gurkenlabs.ldjam46.gfx.HillBillyFonts;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.IUpdateable;
import de.gurkenlabs.litiengine.graphics.ImageRenderer;
import de.gurkenlabs.litiengine.graphics.TextRenderer;
import de.gurkenlabs.litiengine.gui.ImageComponent;
import de.gurkenlabs.litiengine.gui.Menu;
import de.gurkenlabs.litiengine.gui.screens.Screen;
import de.gurkenlabs.litiengine.input.Input;
import de.gurkenlabs.litiengine.resources.Resources;
import de.gurkenlabs.litiengine.util.ColorHelper;
import de.gurkenlabs.litiengine.util.Imaging;

public class MenuScreen extends Screen implements IUpdateable {
  private static final BufferedImage BG = Imaging.scale(Resources.images().get("landscape.png"), Game.window().getWidth(), Game.window().getHeight());
  private static final BufferedImage LOGO = Imaging.scale(Resources.images().get("logo_trans.png"), Game.window().getWidth() * 1 / 6);
  private static final BufferedImage CLOUD1 = Imaging.setOpacity(Imaging.scale(Resources.images().get("cloud1.png"), 6f), .5f);
  private static final BufferedImage CLOUD2 = Imaging.setOpacity(Imaging.scale(Resources.images().get("cloud2.png"), 6f), .5f);
  private static final BufferedImage CLOUD3 = Imaging.setOpacity(Imaging.scale(Resources.images().get("cloud3.png"), 6f), .5f);
  private static final BufferedImage CLOUD4 = Imaging.setOpacity(Imaging.scale(Resources.images().get("cloud4.png"), 6f), .5f);
  private static final BufferedImage CLOUD5 = Imaging.setOpacity(Imaging.scale(Resources.images().get("cloud5.png"), 6f), .5f);

  private static final int cloud1XOffset = Game.random().nextInt(BG.getWidth());
  private static final int cloud2XOffset = Game.random().nextInt(BG.getWidth());
  private static final int cloud3XOffset = Game.random().nextInt(BG.getWidth());
  private static final int cloud4XOffset = Game.random().nextInt(BG.getWidth());
  private static final int cloud5XOffset = Game.random().nextInt(BG.getWidth());
  private static final int cloud1YOffset = Game.random().nextInt(BG.getHeight() * 1 / 5);
  private static final int cloud2YOffset = Game.random().nextInt(BG.getHeight() * 1 / 5);
  private static final int cloud3YOffset = Game.random().nextInt(BG.getHeight() * 1 / 5);
  private static final int cloud4YOffset = Game.random().nextInt(BG.getHeight() * 1 / 5);
  private static final int cloud5YOffset = Game.random().nextInt(BG.getHeight() * 1 / 5);

  private static Color CARVING_COLOR = ColorHelper.decode("#593D35");

  public long lastPlayed;

  private Menu mainMenu;

  public MenuScreen() {
    super("MENU");
  }

  @Override
  public void prepare() {
    super.prepare();
    Game.loop().attach(this);
    Game.window().getRenderComponent().setBackground(Color.BLACK);
    Game.graphics().setBaseRenderScale(6f * Game.window().getResolutionScale());

    this.mainMenu.setForwardMouseEvents(false);
    this.mainMenu.getCellComponents().forEach(comp -> {
      comp.setFont(HillBillyFonts.MENU_FONT);
      comp.setSpriteSheet(Resources.spritesheets().get("button-background"));
      comp.getAppearance().setTextAntialiasing(true);
      comp.getAppearanceHovered().setTextAntialiasing(true);
      comp.getAppearance().setForeColor(CARVING_COLOR);
      comp.getAppearanceHovered().setForeColor(CARVING_COLOR.brighter());
      comp.setForwardMouseEvents(false);
    });

    this.mainMenu.getCellComponents().get(0).setHovered(true);
  }

  @Override
  public void render(Graphics2D g) {
    ImageRenderer.render(g, BG, 0, 0);
    this.renderScrollingStuff(g);
    double scale = 1.4 + 0.15 * Math.sin(Game.time().sinceEnvironmentLoad() / 400.0);
    ImageRenderer.render(g, Imaging.scale(LOGO, scale), Game.window().getCenter().getX() - (scale * LOGO.getWidth()) / 2,
        Game.window().getHeight() * 2.5 / 8 - (scale * LOGO.getHeight()) / 2);
    g.setFont(HillBillyFonts.MENU_FONT);
    g.setColor(Color.WHITE);
    TextRenderer.renderWithOutline(g, "a Gurkenlabs game", Game.window().getWidth() * 1 / 32, Game.window().getHeight() * 30 / 32, Color.DARK_GRAY,
        Game.window().getResolutionScale() * 4, true);
    super.render(g);
  }

  @Override
  protected void initializeComponents() {
    super.initializeComponents();
    final double centerX = Game.window().getResolution().getWidth() / 2.0;
    final double centerY = Game.window().getResolution().getHeight() * 1 / 2;
    final double buttonWidth = 450;

    this.mainMenu = new Menu(centerX - buttonWidth / 2, centerY * 1.3, buttonWidth, centerY / 2, "Play", "Exit");

    Input.keyboard().onKeyReleased(event -> {
      if (this.isSuspended()) {
        return;
      }

      if (event.getKeyCode() == KeyEvent.VK_UP || event.getKeyCode() == KeyEvent.VK_W) {
        this.mainMenu.setCurrentSelection(Math.max(0, this.mainMenu.getCurrentSelection() - 1));
        for (ImageComponent comp : this.mainMenu.getCellComponents()) {
          comp.setHovered(false);
        }
        this.mainMenu.getCellComponents().get(this.mainMenu.getCurrentSelection()).setHovered(true);
        Game.audio().playSound("select.wav");
      }

      if (event.getKeyCode() == KeyEvent.VK_DOWN || event.getKeyCode() == KeyEvent.VK_S) {
        this.mainMenu.setCurrentSelection(Math.min(1, this.mainMenu.getCurrentSelection() + 1));
        for (ImageComponent comp : this.mainMenu.getCellComponents()) {
          comp.setHovered(false);
        }
        this.mainMenu.getCellComponents().get(this.mainMenu.getCurrentSelection()).setHovered(true);
        Game.audio().playSound("select.wav");
      }

      if (event.getKeyCode() == KeyEvent.VK_ENTER || event.getKeyCode() == KeyEvent.VK_SPACE) {
        Game.audio().playSound("confirm.wav");
        switch (this.mainMenu.getCurrentSelection()) {
        case 0:
          this.startGame();
          break;
        case 1:
          System.exit(0);
          break;
        }

      }
    });

    this.getComponents().add(this.mainMenu);
  }

  private void startGame() {
    this.mainMenu.setEnabled(false);
    Game.window().getRenderComponent().fadeOut(1500);

    Game.loop().perform(1500, () -> {
      Game.window().getRenderComponent().fadeIn(1500);
      Game.screens().display("GAME");
      GameManager.levelTransition();
    });
  }

  @Override
  public void suspend() {
    super.suspend();
    Game.loop().detach(this);
    Game.audio().stopMusic();
  }

  @Override
  public void update() {
    if (this.lastPlayed == 0) {
      Game.audio().playMusic(Resources.sounds().get("pumpkinville.ogg"));
      this.lastPlayed = Game.loop().getTicks();
    }

  }

  private void renderScrollingStuff(Graphics2D g) {
    ImageRenderer.render(g, CLOUD1,
        -CLOUD1.getWidth() + Game.time().now() * 0.1 % (CLOUD1.getWidth() + Game.window().getResolution().getWidth()), cloud1YOffset);
    ImageRenderer.render(g, CLOUD2,
        -CLOUD2.getWidth() + Game.time().now() * 0.2 % (CLOUD2.getWidth() + Game.window().getResolution().getWidth()), cloud2YOffset);
    ImageRenderer.render(g, CLOUD3,
        -CLOUD3.getWidth() + Game.time().now() * 0.3 % (CLOUD3.getWidth() + Game.window().getResolution().getWidth()), cloud3YOffset);
    ImageRenderer.render(g, CLOUD4,
        -CLOUD4.getWidth() + Game.time().now() * 0.4 % (CLOUD4.getWidth() + Game.window().getResolution().getWidth()), cloud4YOffset);
    ImageRenderer.render(g, CLOUD5,
        -CLOUD5.getWidth() + Game.time().now() * 0.5 % (CLOUD5.getWidth() + Game.window().getResolution().getWidth()), cloud5YOffset);
    ImageRenderer.render(g, Imaging.horizontalFlip(CLOUD1),
        -CLOUD1.getWidth() + Game.time().now() * 0.5 % (CLOUD1.getWidth() + Game.window().getResolution().getWidth()) + cloud1XOffset, cloud5YOffset);
    ImageRenderer.render(g, Imaging.horizontalFlip(CLOUD2),
        -CLOUD2.getWidth() + Game.time().now() * 0.4 % (CLOUD2.getWidth() + Game.window().getResolution().getWidth()) + cloud3XOffset, cloud2YOffset);
    ImageRenderer.render(g, Imaging.horizontalFlip(CLOUD3),
        -CLOUD3.getWidth() + Game.time().now() * 0.3 % (CLOUD3.getWidth() + Game.window().getResolution().getWidth()) + cloud5XOffset, cloud1YOffset);
    ImageRenderer.render(g, Imaging.horizontalFlip(CLOUD4),
        -CLOUD4.getWidth() + Game.time().now() * 0.2 % (CLOUD4.getWidth() + Game.window().getResolution().getWidth()) + cloud2XOffset, cloud3YOffset);
    ImageRenderer.render(g, Imaging.horizontalFlip(CLOUD5),
        -CLOUD5.getWidth() + Game.time().now() * 0.1 % (CLOUD5.getWidth() + Game.window().getResolution().getWidth()) + cloud4XOffset, cloud2YOffset);

  }
}
