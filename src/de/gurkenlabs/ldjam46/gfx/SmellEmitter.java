package de.gurkenlabs.ldjam46.gfx;

import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.entities.EmitterInfo;
import de.gurkenlabs.litiengine.entities.IEntity;
import de.gurkenlabs.litiengine.graphics.emitters.EntityEmitter;
import de.gurkenlabs.litiengine.graphics.emitters.particles.Particle;
import de.gurkenlabs.litiengine.graphics.emitters.particles.RectangleFillParticle;
import de.gurkenlabs.litiengine.util.ColorHelper;

@EmitterInfo(emitterTTL = 5000, particleMinTTL = 250, particleMaxTTL = 600)
public class SmellEmitter extends EntityEmitter {

  public SmellEmitter(IEntity entity) {
    super(entity, true);
    this.addParticleColor(ColorHelper.decode("#aa0d661a"));
    this.addParticleColor(ColorHelper.decode("#aa57d11b"));
    this.addParticleColor(ColorHelper.decode("#aa2f6325"));
  }

  @Override
  protected Particle createNewParticle() {
    final float x = this.getRandomParticleX();
    final float y = this.getRandomParticleY();
    final float width = Game.random().nextFloat(1, 2);
    final float height = Game.random().nextFloat(1, 2);
    final float dx = Game.random().nextFloat(-1, 1);
    final float dy = Game.random().nextFloat(-2, -.1f);
    final float gravityX = 0;
    final float gravityY = 0;
    final float deltaWidth = Game.random().nextFloat(.1f);
    final float deltaHeight = Game.random().nextFloat(.1f);

    return new RectangleFillParticle(width, height, this.getRandomParticleColor(), this.getRandomParticleTTL())
        .setDeltaX(dx).setDeltaY(dy).setDeltaIncX(gravityX).setDeltaIncY(gravityY).setX(x).setY(y)
        .setDeltaWidth(deltaWidth).setDeltaHeight(deltaHeight);
  }

}
