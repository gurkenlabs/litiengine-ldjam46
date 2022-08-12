package de.gurkenlabs.ldjam46.gfx;

import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.entities.EmitterInfo;
import de.gurkenlabs.litiengine.entities.IEntity;
import de.gurkenlabs.litiengine.graphics.emitters.EntityEmitter;
import de.gurkenlabs.litiengine.graphics.emitters.particles.Particle;
import de.gurkenlabs.litiengine.graphics.emitters.particles.RectangleParticle;

@EmitterInfo(duration = 5000, particleMinTTL = 250, particleMaxTTL = 600)
public class SmellEmitter extends EntityEmitter {

  public SmellEmitter(IEntity entity) {
    super(entity, true);
    data().getColors().add("#aa0d661a");
    data().getColors().add("#aa57d11b");
    data().getColors().add("#aa2f6325");
  }

  @Override
  protected Particle createNewParticle() {
    final float x = data().getParticleOffsetX().getRandomNumber();
    final float y = data().getParticleOffsetY().getRandomNumber();
    final float width = Game.random().nextFloat(1, 2);
    final float height = Game.random().nextFloat(1, 2);
    final float dx = Game.random().nextFloat(-1, 1);
    final float dy = Game.random().nextFloat(-2, -.1f);
    final float gravityX = 0;
    final float gravityY = 0;
    final float deltaWidth = Game.random().nextFloat(.1f);
    final float deltaHeight = Game.random().nextFloat(.1f);

    return new RectangleParticle(width, height).init(data())
        .setVelocityX(dx).setVelocityY(dy).setAccelerationX(gravityX).setAccelerationY(gravityY).setX(x).setY(y)
        .setDeltaWidth(deltaWidth).setDeltaHeight(deltaHeight);
  }

}
