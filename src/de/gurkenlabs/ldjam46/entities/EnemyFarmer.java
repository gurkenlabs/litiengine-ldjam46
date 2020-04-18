package de.gurkenlabs.ldjam46.entities;

import de.gurkenlabs.litiengine.Align;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.Valign;
import de.gurkenlabs.litiengine.entities.CollisionInfo;
import de.gurkenlabs.litiengine.entities.Creature;
import de.gurkenlabs.litiengine.entities.EntityInfo;
import de.gurkenlabs.litiengine.entities.ICollisionEntity;
import de.gurkenlabs.litiengine.entities.MovementInfo;
import de.gurkenlabs.litiengine.environment.Environment;
import de.gurkenlabs.litiengine.physics.IMovementController;

@EntityInfo(width = 11, height = 20)
@CollisionInfo(collision = true, collisionBoxWidth = 4, collisionBoxHeight = 4, align = Align.CENTER, valign = Valign.DOWN)
@MovementInfo(velocity = 70)
public class EnemyFarmer extends Creature {

  public EnemyFarmer() {
    super(Game.random().choose("enemyfarmer1", "enemyfarmer2"));
  }

  @Override
  public boolean canCollideWith(ICollisionEntity otherEntity) {
    return super.canCollideWith(otherEntity) && !(otherEntity instanceof EnemyFarmer);
  }

  @Override
  public void loaded(Environment environment) {
    super.loaded(environment);

    Pumpkin pumpkin = Game.random().choose(Game.world().environment().getEntities(Pumpkin.class));
    this.setTarget(pumpkin);
  }

  @Override
  protected IMovementController createMovementController() {
    return new EnemyFarmerController(this);
  }
}
