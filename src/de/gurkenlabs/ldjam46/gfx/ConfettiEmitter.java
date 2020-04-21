package de.gurkenlabs.ldjam46.gfx;

import java.awt.Color;
import java.awt.geom.Point2D;

import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.entities.EmitterInfo;
import de.gurkenlabs.litiengine.entities.EntityInfo;
import de.gurkenlabs.litiengine.graphics.RenderType;
import de.gurkenlabs.litiengine.graphics.emitters.Emitter;
import de.gurkenlabs.litiengine.graphics.emitters.particles.Particle;
import de.gurkenlabs.litiengine.graphics.emitters.particles.RectangleFillParticle;

@EmitterInfo(maxParticles = 5000, spawnAmount = 60, particleMinTTL = 500, particleMaxTTL = 8000, activateOnInit = true)
@EntityInfo(renderType = RenderType.OVERLAY)
public class ConfettiEmitter extends Emitter {

  public ConfettiEmitter() {
    super();
    this.setWidth(Game.window().getWidth() / Game.graphics().getBaseRenderScale());
    this.setHeight(Game.window().getHeight() / Game.graphics().getBaseRenderScale());

  }

  @Override
  protected Particle createNewParticle() {

    final float delta = Game.random().nextFloat(5);
    final float dx = -delta;
    final float dy = delta;
    final float gravityX = 0.01f;
    final float gravityY = 0.05f;
    final float size = Game.random().nextFloat(4) / Game.graphics().getBaseRenderScale();
    int r = Game.random().nextInt(255);
    int g = Game.random().nextInt(255);
    int b = Game.random().nextInt(255);
    int a = Game.random().nextInt(255);
    return new RectangleFillParticle(size, size, new Color(r, g, b, a), this.getRandomParticleTTL()).setDeltaX(dx).setDeltaY(dy).setDeltaIncX(gravityX).setDeltaIncY(gravityY).setX(this.getRandomParticleX()).setY(this.getRandomParticleY());
  }

}
