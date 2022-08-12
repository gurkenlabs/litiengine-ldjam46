package de.gurkenlabs.ldjam46.gfx;

import de.gurkenlabs.litiengine.Align;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.Valign;
import de.gurkenlabs.litiengine.entities.EmitterInfo;
import de.gurkenlabs.litiengine.entities.EntityInfo;
import de.gurkenlabs.litiengine.entities.IEntity;
import de.gurkenlabs.litiengine.graphics.RenderType;
import de.gurkenlabs.litiengine.graphics.emitters.EntityEmitter;
import de.gurkenlabs.litiengine.graphics.emitters.particles.Particle;
import de.gurkenlabs.litiengine.graphics.emitters.particles.RectangleParticle;
import de.gurkenlabs.litiengine.physics.Collision;

@EmitterInfo(duration = 1000, originAlign = Align.LEFT, originValign = Valign.MIDDLE, particleMinTTL = 200, particleMaxTTL = 500, spawnRate = 0, spawnAmount = 20, maxParticles = 80)
@EntityInfo(renderType = RenderType.OVERLAY)
public class WaterSplashEmitter extends EntityEmitter {

  public WaterSplashEmitter(IEntity entity) {
    super(entity, true);
    data().getColors().add("#801054A7");
    data().getColors().add("#80163963");
    data().getColors().add("#800791BE");
  }

  @Override
  protected Particle createNewParticle() {
    final float x = data().getParticleOffsetX().getRandomNumber();
    final float y = Game.random().nextFloat(2, 7);
    final float width = 1;
    final float height = 1;
    final float dx = Game.random().nextFloat(-1, 1);
    final float dy = 0;
    final float gravityX = 0;
    final float gravityY = Game.random().nextFloat(-.1f, .2f);
    final float deltaWidth = Game.random().nextFloat(.2f);
    final float deltaHeight = Game.random().nextFloat(.2f);

    return new RectangleParticle(width, height).init(data())
        .setVelocityX(dx).setVelocityY(dy).setAccelerationX(gravityX).setAccelerationY(gravityY).setX(x).setY(y)
        .setDeltaWidth(deltaWidth).setDeltaHeight(deltaHeight).setCollisionType(Collision.STATIC);
  }

}
