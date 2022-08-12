package de.gurkenlabs.ldjam46.entities;

import java.awt.geom.Point2D;

import de.gurkenlabs.litiengine.Direction;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.abilities.Ability;
import de.gurkenlabs.litiengine.abilities.AbilityInfo;
import de.gurkenlabs.litiengine.abilities.effects.Effect;
import de.gurkenlabs.litiengine.abilities.effects.EffectTarget;
import de.gurkenlabs.litiengine.abilities.effects.SoundEffect;
import de.gurkenlabs.litiengine.entities.Creature;
import de.gurkenlabs.litiengine.entities.EmitterInfo;
import de.gurkenlabs.litiengine.entities.ICombatEntity;
import de.gurkenlabs.litiengine.graphics.RenderType;
import de.gurkenlabs.litiengine.graphics.Spritesheet;
import de.gurkenlabs.litiengine.graphics.emitters.Emitter;
import de.gurkenlabs.litiengine.resources.Resources;
import de.gurkenlabs.litiengine.util.geom.GeometricUtilities;
import java.awt.geom.Point2D.Double;

@AbilityInfo(name = "StabAbility", cooldown = 5000, impact = 30, impactAngle = 90, value = 10, duration = 400)
public class StabAbility extends Ability {

  protected StabAbility(Creature executor) {
    super(executor);

    this.addEffect(new SoundEffect(this, "pumpkinhit1.wav", "pumpkinhit2.wav", "pumpkinhit3.wav"));
    this.addEffect(new StabEffect(this));
  }

  private static class StabEffect extends Effect {

    protected StabEffect(Ability ability) {
      super(ability, EffectTarget.CUSTOM);
    }

    @Override
    protected void apply(ICombatEntity entity) {
      super.apply(entity);

      final int damage = this.getAbility().getAttributes().value().get();
      entity.hit(damage, this.getAbility());

      RenderType renderType = RenderType.OVERLAY;
      String sprite = "hit";
      double x = this.getAbility().getExecutor().getCenter().getX();
      double y = this.getAbility().getExecutor().getCenter().getY();

      // face toward the closest enemy
      Pumpkin closestInRange = (Pumpkin) entity;

      double angle = GeometricUtilities.calcRotationAngleInDegrees(this.getAbility().getPivot().getPoint(), closestInRange.getCenter());

      switch (Direction.fromAngle(angle)) {
        case RIGHT -> {
          x -= 1;
          y -= 4;
          sprite = "hit-right";
        }
        case LEFT -> {
          x -= 11;
          y -= 4;
          sprite = "hit-left";
        }
        case UP -> {
          x -= 9;
          y -= 9;
          sprite = "hit-top";
          renderType = RenderType.SURFACE;
        }
        default -> {
          x -= 9;
          y += 1;
        }
      }

      Emitter emitter = new StrikeEmitter(Resources.spritesheets().get(sprite), new Double(x, y));
      emitter.setRenderType(renderType);
      Game.world().environment().add(emitter);
    }

    @Override
    protected boolean customTarget(ICombatEntity entity) {
      return entity.equals(this.getAbility().getExecutor().getTarget());
    }

    @EmitterInfo(particleMinTTL = 100, particleMaxTTL = 100, duration = 150, maxParticles = 1)
    public static class StrikeEmitter extends Emitter {

      public StrikeEmitter(Spritesheet spriteSheet, Point2D origin) {
        super(origin);
        data().setSpritesheet(spriteSheet);
      }
    }

  }
}
