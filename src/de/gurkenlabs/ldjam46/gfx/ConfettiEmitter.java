package de.gurkenlabs.ldjam46.gfx;

import java.awt.Color;

import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.entities.EmitterInfo;
import de.gurkenlabs.litiengine.entities.EntityInfo;
import de.gurkenlabs.litiengine.graphics.RenderType;
import de.gurkenlabs.litiengine.graphics.emitters.Emitter;
import de.gurkenlabs.litiengine.graphics.emitters.particles.Particle;
import de.gurkenlabs.litiengine.graphics.emitters.particles.RectangleParticle;

@EmitterInfo(maxParticles = 300, spawnAmount = 60, particleMinTTL = 500, particleMaxTTL = 1500, activateOnInit = true)
@EntityInfo(renderType = RenderType.OVERLAY)
public class ConfettiEmitter extends Emitter {

  public ConfettiEmitter() {
    super();
    this.setWidth(Game.world().environment().getMap().getSizeInPixels().getWidth());
    this.setHeight(Game.world().environment().getMap().getSizeInPixels().getHeight());

  }

  @Override
  protected Particle createNewParticle() {

    final float dx = 0;
    final float dy = Game.random().nextFloat(2);
    final float gravityX = 0;
    final float gravityY = Game.random().nextFloat(.2f);
    final float size = Game.random().nextFloat(3);
    int r = Game.random().nextInt(255);
    int g = Game.random().nextInt(255);
    int b = Game.random().nextInt(255);
    int a = Game.random().nextInt(255);
    return new RectangleParticle(size, size).setColor(new Color(r, g, b, a)).setTimeToLive((int) data().getParticleTTL().getRandomNumber())
        .setVelocityX(dx).setVelocityY(dy).setAccelerationX(gravityX)
        .setAccelerationY(gravityY).setX(data().getParticleOffsetX().getRandomNumber()).setY(data().getParticleOffsetY().getRandomNumber());
  }

}
