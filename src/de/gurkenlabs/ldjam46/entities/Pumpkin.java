package de.gurkenlabs.ldjam46.entities;

import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.IUpdateable;
import de.gurkenlabs.litiengine.attributes.AttributeModifier;
import de.gurkenlabs.litiengine.attributes.Modification;
import de.gurkenlabs.litiengine.entities.AnimationInfo;
import de.gurkenlabs.litiengine.entities.Prop;
import de.gurkenlabs.litiengine.environment.Environment;

@AnimationInfo(spritePrefix = "prop-pumpkin")
public class Pumpkin extends Prop implements IUpdateable {
  private static int MOISTURE_DECAY = 400;

  private long lastMoistureDecay;

  public Pumpkin() {
    super("pumpkin");
  }

  @Override
  public void loaded(Environment environment) {
    super.loaded(environment);

    this.getHitPoints().setBaseValue(Game.random().nextInt(75, 100));
  }

  @Override
  public void update() {
    if (this.isDead()) {
      return;
    }

    if (Game.time().since(this.lastMoistureDecay) > MOISTURE_DECAY) {
      this.getHitPoints().modifyBaseValue(new AttributeModifier<>(Modification.SUBSTRACT, 1));
      this.lastMoistureDecay = Game.loop().getTicks();
    }

    if (this.getHitPoints().get() == 0) {
      this.die();
    }
  }
}
