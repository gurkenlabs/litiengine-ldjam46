package de.gurkenlabs.ldjam46.entities;

import de.gurkenlabs.ldjam46.gfx.WalkDustSpawner;
import de.gurkenlabs.litiengine.Align;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.Valign;
import de.gurkenlabs.litiengine.entities.CollisionInfo;
import de.gurkenlabs.litiengine.entities.Creature;
import de.gurkenlabs.litiengine.entities.EntityInfo;
import de.gurkenlabs.litiengine.entities.ICollisionEntity;
import de.gurkenlabs.litiengine.entities.MovementInfo;
import de.gurkenlabs.litiengine.entities.Spawnpoint;
import de.gurkenlabs.litiengine.environment.Environment;
import de.gurkenlabs.litiengine.physics.IMovementController;

@EntityInfo(width = 11, height = 20)
@CollisionInfo(collision = false, collisionBoxWidth = 4, collisionBoxHeight = 4, align = Align.CENTER, valign = Valign.DOWN)
@MovementInfo(velocity = 70)
public class EnemyFarmer extends Creature {
  private final Spawnpoint spawn;

  private final StabAbility stabAbility = new StabAbility(this);

  private boolean fartedOn;

  public EnemyFarmer(Spawnpoint spawn) {
    super(Game.random().choose("enemyfarmer1", "enemyfarmer2"));

    this.spawn = spawn;
    this.setTeam(2);
    this.onMoved(new WalkDustSpawner());
  }

  @Override
  public boolean canCollideWith(ICollisionEntity otherEntity) {
    return super.canCollideWith(otherEntity) && !(otherEntity instanceof EnemyFarmer);
  }

  @Override
  public void loaded(Environment environment) {
    super.loaded(environment);

    this.updateTarget();
  }

  public void updateTarget() {
    Pumpkin pumpkin = Game.random().choose(Game.world().environment().getEntities(Pumpkin.class, e -> !e.isDead()));
    this.setTarget(pumpkin);
  }

  @Override
  protected IMovementController createMovementController() {
    return new EnemyFarmerController(this);
  }

  public StabAbility getStabAbility() {
    return stabAbility;
  }

  public boolean isFartedOn() {
    return fartedOn;
  }

  public void fartOn() {
    this.fartedOn = true;
    this.setCollision(false);
  }

  public Spawnpoint getSpawn() {
    return spawn;
  }
}
