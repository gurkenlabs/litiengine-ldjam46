package de.gurkenlabs.ldjam46.entities;

import de.gurkenlabs.ldjam46.gfx.WaterSplashEmitter;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.abilities.Ability;
import de.gurkenlabs.litiengine.abilities.AbilityInfo;
import de.gurkenlabs.litiengine.abilities.effects.Effect;
import de.gurkenlabs.litiengine.abilities.effects.EffectTarget;
import de.gurkenlabs.litiengine.attributes.AttributeModifier;
import de.gurkenlabs.litiengine.attributes.Modification;
import de.gurkenlabs.litiengine.attributes.RangeAttribute;
import de.gurkenlabs.litiengine.entities.ICombatEntity;
import de.gurkenlabs.litiengine.entities.IEntity;
import de.gurkenlabs.litiengine.entities.RelativeEntityComparator;

@AbilityInfo(name = "WaterAbility", cooldown = 1000, range = 0, impact = 30, impactAngle = 360, value = 1, duration = 400)
public class WaterAbility extends Ability {

  private RangeAttribute<Integer> charges = new RangeAttribute<>(5, 0, 2);

  // TODO: refill
  WaterAbility(Farmer farmer) {
    super(farmer);

    this.addEffect(new WaterEffect(this));
    this.addEffect(new WaterSplashEffect(this));
    // TODO animation effect
    // TODO sound effect
  }

  public RangeAttribute<Integer> getCharges() {
    return this.charges;
  }

  @Override
  public boolean canCast() {
    return super.canCast() && this.charges.get() > 0;
  }

  private class WaterEffect extends Effect {

    WaterEffect(Ability ability) {
      super(ability, EffectTarget.CUSTOM);
      this.setTargetPriorityComparator(new RelativeEntityComparator() {

        @Override
        public int compare(IEntity entity1, IEntity entity2) {
          if (entity1 instanceof Pumpkin && entity2 instanceof Pumpkin) {
            Pumpkin p1 = (Pumpkin) entity1;
            Pumpkin p2 = (Pumpkin) entity2;
            if (p1.getHitPoints().get() < p2.getHitPoints().get()) {
              return -1;
            }

            if (p1.getHitPoints().get() >= p2.getHitPoints().get()) {
              return 1;
            }

            return 0;
          }

          if (this.getRelativeEntity() == null) {
            return 0;
          }

          final double distance1 = entity1.getLocation().distance(this.getRelativeEntity().getLocation());
          final double distance2 = entity2.getLocation().distance(this.getRelativeEntity().getLocation());
          if (distance1 < distance2) {
            return -1;
          }
          if (distance1 > distance2) {
            return 1;
          }

          return 0;
        }
      });
    }

    @Override
    protected void apply(ICombatEntity entity) {
      Pumpkin pumpkin = (Pumpkin) entity;
      pumpkin.getHitPoints().modifyBaseValue(new AttributeModifier<>(Modification.ADD, 33));

      System.out.println("pumpkin healed");
      WaterAbility.this.charges.modifyBaseValue(new AttributeModifier<>(Modification.SUBSTRACT, 1));

      this.getAbility().getExecutor().setVelocity(0);
      Game.loop().perform(1000, () -> {
        this.getAbility().getExecutor().setVelocity(70);
      });
    }

    @Override
    protected boolean customTarget(ICombatEntity entity) {
      return entity instanceof Pumpkin && !entity.isDead();
    }
  }

  private static class WaterSplashEffect extends Effect {
    protected WaterSplashEffect(Ability ability) {
      super(ability, EffectTarget.EXECUTINGENTITY);
    }

    @Override
    protected void apply(ICombatEntity entity) {
      WaterSplashEmitter splash = new WaterSplashEmitter(entity);
      Game.world().environment().add(splash);
    }
  }
}
