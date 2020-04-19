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
import de.gurkenlabs.litiengine.graphics.emitters.particles.RectangleFillParticle;
import de.gurkenlabs.litiengine.physics.Collision;
import de.gurkenlabs.litiengine.util.ColorHelper;

@EmitterInfo(emitterTTL = 1000, originAlign = Align.LEFT, originVAlign = Valign.MIDDLE, particleMinTTL = 200, particleMaxTTL = 500, spawnRate = 0, spawnAmount = 20, maxParticles = 80)
@EntityInfo(renderType = RenderType.OVERLAY)
public class WaterSplashEmitter extends EntityEmitter {

  public WaterSplashEmitter(IEntity entity) {
    super(entity,true);
    this.addParticleColor(ColorHelper.decode("#501054A7"));
    this.addParticleColor(ColorHelper.decode("#50163963"));
    this.addParticleColor(ColorHelper.decode("#500791BE"));
  }

  @Override
  protected Particle createNewParticle() {
    final float x = this.getRandomParticleX();
    final float y = Game.random().nextFloat(2, 7);
    final float width = 1;
    final float height = 1;
    final float dx = Game.random().nextFloat(-1, 1);
    final float dy = 0;
    final float gravityX = 0;
    final float gravityY = Game.random().nextFloat(-.1f, .2f);
    final float deltaWidth = Game.random().nextFloat(.2f);
    final float deltaHeight = Game.random().nextFloat(.2f);

    return new RectangleFillParticle(width, height, this.getRandomParticleColor(), this.getRandomParticleTTL())
        .setDeltaX(dx).setDeltaY(dy).setDeltaIncX(gravityX).setDeltaIncY(gravityY).setX(x).setY(y)
        .setDeltaWidth(deltaWidth).setDeltaHeight(deltaHeight).setCollisionType(Collision.STATIC);
  }

}
