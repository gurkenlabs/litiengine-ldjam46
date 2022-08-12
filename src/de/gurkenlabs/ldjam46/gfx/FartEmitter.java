package de.gurkenlabs.ldjam46.gfx;

import de.gurkenlabs.litiengine.Align;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.Valign;
import de.gurkenlabs.litiengine.entities.EmitterInfo;
import de.gurkenlabs.litiengine.entities.IEntity;
import de.gurkenlabs.litiengine.graphics.RenderType;
import de.gurkenlabs.litiengine.graphics.emitters.EntityEmitter;
import de.gurkenlabs.litiengine.graphics.emitters.particles.Particle;
import de.gurkenlabs.litiengine.graphics.emitters.particles.SpriteParticle;
import de.gurkenlabs.litiengine.resources.Resources;

@EmitterInfo(duration = 400, originAlign = Align.CENTER, originValign = Valign.MIDDLE, particleMinTTL = 100, particleMaxTTL = 400, spawnRate = 0, maxParticles = 20)
public class FartEmitter extends EntityEmitter {

  public FartEmitter(IEntity entity) {
    super(entity, true);
    data().setSpritesheet(Resources.spritesheets().get("fart-clouds"));
    this.setRenderType(RenderType.OVERLAY);
  }
}
