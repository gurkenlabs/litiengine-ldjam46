package de.gurkenlabs.ldjam46.entities;

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
  public void loaded(Environment environment) {
    super.loaded(environment);

    // first and second level
    if (GameManager.getCurrentDay() == null || GameManager.getCurrentDay() == Day.Monday) {
      this.getHitPoints().setBaseValue(75);
    } else {
      this.getHitPoints().setBaseValue(Game.random().nextInt(75, 100));
    }
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

    System.out.println("harvest " + this.getMapId());
    Game.world().environment().remove(this);
    Game.audio().playSound("harvest.ogg");

    Prop harvestedPumpkin = new Prop("pumpkin2");
    harvestedPumpkin.setSize(22, 18);
    harvestedPumpkin.setX(this.getX() - 3);
    harvestedPumpkin.setY(this.getY() - 4);
    harvestedPumpkin.setScaling(true);

    Game.world().environment().add(harvestedPumpkin);

  }
}
