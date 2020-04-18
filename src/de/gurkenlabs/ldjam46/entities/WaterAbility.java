package de.gurkenlabs.ldjam46.entities;

import de.gurkenlabs.litiengine.abilities.Ability;
import de.gurkenlabs.litiengine.abilities.AbilityInfo;
import de.gurkenlabs.litiengine.abilities.effects.Effect;
import de.gurkenlabs.litiengine.abilities.effects.EffectTarget;
import de.gurkenlabs.litiengine.attributes.AttributeModifier;
import de.gurkenlabs.litiengine.attributes.Modification;
import de.gurkenlabs.litiengine.attributes.RangeAttribute;
import de.gurkenlabs.litiengine.entities.ICombatEntity;

@AbilityInfo(name = "WaterAbility", cooldown = 1000, range = 0, impact = 30, impactAngle = 90, value = 1, duration = 400)
public class WaterAbility extends Ability {

  private RangeAttribute<Integer> charges = new RangeAttribute<>(5, 0, 1);

  //TODO: refill 
  WaterAbility(Farmer farmer) {
    super(farmer);

    this.addEffect(new WaterEffect(this));
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
    }

    @Override
    protected void apply(ICombatEntity entity) {
      Pumpkin pumpkin = (Pumpkin) entity;
      pumpkin.getHitPoints().modifyBaseValue(new AttributeModifier<>(Modification.ADD, 25));

      System.out.println("pumpkin healed");
      WaterAbility.this.charges.modifyBaseValue(new AttributeModifier<>(Modification.SUBSTRACT, 1));
    }

    @Override
    protected boolean customTarget(ICombatEntity entity) {
      return entity instanceof Pumpkin && !entity.isDead();
    }
  }
}
