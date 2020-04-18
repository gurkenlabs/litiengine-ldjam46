package de.gurkenlabs.ldjam46.entities;

import java.awt.Graphics2D;

import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.IUpdateable;
import de.gurkenlabs.litiengine.attributes.AttributeModifier;
import de.gurkenlabs.litiengine.attributes.Modification;
import de.gurkenlabs.litiengine.attributes.RangeAttribute;
import de.gurkenlabs.litiengine.entities.AnimationInfo;
import de.gurkenlabs.litiengine.entities.Prop;
import de.gurkenlabs.litiengine.graphics.IRenderable;

@AnimationInfo(spritePrefix = "prop-pumpkin")
public class Pumpkin extends Prop implements IUpdateable, IRenderable {
  private static int MOISTURE_DECAY = 200;
  private final RangeAttribute<Integer> moisture = new RangeAttribute<>(100, 0, 100);

  private long lastMoistureDecay;

  public Pumpkin() {
    super("pumpkin");
  }

  public RangeAttribute<Integer> getMoisture() {
    return this.moisture;
  }

  @Override
  public void update() {
    if (this.isDead()) {
      return;
    }

    if (Game.time().since(this.lastMoistureDecay) > MOISTURE_DECAY) {
      this.getMoisture().modifyMaxBaseValue(new AttributeModifier<>(Modification.SUBSTRACT, 1));
      this.lastMoistureDecay = Game.loop().getTicks();
    }

    if (this.getMoisture().get() == 0) {
      this.die();
    }
  }

  @Override
  public void render(Graphics2D g) {
    Game.graphics().renderText(g, this.getMoisture().toString(), Game.world().camera().getViewportDimensionCenter(this));
  }
}
