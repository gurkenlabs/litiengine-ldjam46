package de.gurkenlabs.ldjam46.entities;

import java.awt.Color;
import java.awt.geom.RoundRectangle2D;

import de.gurkenlabs.ldjam46.GameManager;
import de.gurkenlabs.ldjam46.GameManager.Day;
import de.gurkenlabs.ldjam46.GameManager.GameState;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.IUpdateable;
import de.gurkenlabs.litiengine.attributes.AttributeModifier;
import de.gurkenlabs.litiengine.attributes.Modification;
import de.gurkenlabs.litiengine.entities.AnimationInfo;
import de.gurkenlabs.litiengine.entities.LightSource;
import de.gurkenlabs.litiengine.entities.Prop;
import de.gurkenlabs.litiengine.environment.Environment;
import de.gurkenlabs.litiengine.graphics.RenderType;
import de.gurkenlabs.litiengine.graphics.animation.IEntityAnimationController;

@AnimationInfo(spritePrefix = "prop-pumpkin")
public class Pumpkin extends Prop implements IUpdateable {
  private static int MOISTURE_DECAY = 400;

  private long lastMoistureDecay;

  public Pumpkin() {
    super("pumpkin");

    this.onDeath(l -> {
      GameManager.trackPumpkinDeath(this);
      System.out.println("pumpkin died");
    });
  }

  @Override
  public boolean isAddShadow() {
    return false;
  }

  @Override
  public void loaded(Environment environment) {
    super.loaded(environment);

    // first and second level
    if (environment.getMap().getName().equals("monday") || environment.getMap().getName().equals("tuesday")) {
      this.getHitPoints().setBaseValue(75);
    } else if (environment.getMap().getName().equals("friday")) {
      this.getHitPoints().setToMax();
    } else {
      this.getHitPoints().setBaseValue(Game.random().nextInt(75, 100));
    }

    environment.add(g -> {
      if (this.isDead() || GameManager.getState() != GameState.INGAME) {
        return;
      }

      final double width = 16;
      final double height = 2;
      double x = this.getX() - (width - this.getWidth()) / 2.0;
      double y = this.getY() - height * 2;
      RoundRectangle2D rect = new RoundRectangle2D.Double(x, y, width, height, 1.5, 1.5);

      final double currentWidth = width * (this.getHitPoints().get() / (double) this.getHitPoints().getMax());
      RoundRectangle2D actualRect = new RoundRectangle2D.Double(x, y, currentWidth, height, 1.5, 1.5);

      g.setColor(new Color(40, 42, 43, 150));
      Game.graphics().renderShape(g, rect);

      g.setColor(new Color(228, 59, 68));
      Game.graphics().renderShape(g, actualRect);
    }, RenderType.OVERLAY);
  }

  private boolean watered;

  public void water() {
    this.getHitPoints().modifyBaseValue(new AttributeModifier<>(Modification.ADD, 33));
    watered = true;

    if (GameManager.getCurrentDay() == Day.Monday) {
      for (LightSource light : Game.world().environment().getEntities(LightSource.class, e -> e.getBoundingBox().intersects(this.getBoundingBox()))) {
        light.deactivate();
      }
    }
  }

  @Override
  public void update() {
    if (this.isDead() || GameManager.getState() != GameState.INGAME) {
      return;
    }

    if (Game.time().since(this.lastMoistureDecay) > MOISTURE_DECAY) {
      this.hit(1);
      this.lastMoistureDecay = Game.loop().getTicks();
    }

    if (this.getHitPoints().get() == 0) {
      this.die();
    }
  }

  public boolean wasWatered() {
    return watered;
  }

  public void harvest() {
    Game.world().environment().remove(this);
    Game.audio().playSound("harvest.ogg");

    Prop harvestedPumpkin = new Prop("pumpkin2");
    harvestedPumpkin.setSize(22, 18);
    harvestedPumpkin.setX(this.getX() - 3);
    harvestedPumpkin.setY(this.getY() - 4);
    harvestedPumpkin.setScaling(true);

    Game.world().environment().add(harvestedPumpkin);

  }

  @Override
  protected IEntityAnimationController<?> createAnimationController() {
    return new PumpkinAnimationController(this);
  }
}
