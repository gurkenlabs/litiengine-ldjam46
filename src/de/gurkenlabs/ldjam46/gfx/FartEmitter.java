package de.gurkenlabs.ldjam46.gfx;

import de.gurkenlabs.litiengine.Align;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.Valign;
import de.gurkenlabs.litiengine.entities.EmitterInfo;
import de.gurkenlabs.litiengine.entities.IEntity;
import de.gurkenlabs.litiengine.graphics.RenderType;
import de.gurkenlabs.litiengine.graphics.emitters.SpritesheetEntityEmitter;
import de.gurkenlabs.litiengine.graphics.emitters.particles.Particle;
import de.gurkenlabs.litiengine.graphics.emitters.particles.SpriteParticle;
import de.gurkenlabs.litiengine.resources.Resources;

@EmitterInfo(emitterTTL = 400, originAlign = Align.CENTER, originVAlign = Valign.MIDDLE, particleMinTTL = 100, particleMaxTTL = 400, spawnRate = 0, spawnAmount = 20, maxParticles = 20)
public class FartEmitter extends SpritesheetEntityEmitter {

  public FartEmitter(IEntity entity) {
    super(Resources.spritesheets().get("fart-clouds"), entity, true);
    this.setRenderType(RenderType.OVERLAY);
  }

  @Override
  protected Particle createNewParticle() {
    final float dx = Game.random().nextInt(-3, 3);
    final float dy = Game.random().nextInt(-3, 3);
    final float gravityX = 0;
    final float gravityY = 0;
    final float deltaWidth = Game.random().nextFloat(.1f);
    final float deltaHeight = Game.random().nextFloat(.1f);

    return new SpriteParticle(this.getRandomSprite(), this.getRandomParticleTTL()).setDeltaX(dx).setDeltaY(dy)
        .setDeltaIncX(gravityX).setDeltaIncY(gravityY).setDeltaWidth(deltaWidth).setDeltaHeight(deltaHeight)
        .setFade(true);
  }

}
