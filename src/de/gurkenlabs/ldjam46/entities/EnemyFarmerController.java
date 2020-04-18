package de.gurkenlabs.ldjam46.entities;

import de.gurkenlabs.ldjam46.GameManager;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.entities.behavior.AStarGrid;
import de.gurkenlabs.litiengine.entities.behavior.AStarPathFinder;
import de.gurkenlabs.litiengine.entities.behavior.EntityNavigator;
import de.gurkenlabs.litiengine.physics.MovementController;

public class EnemyFarmerController extends MovementController<EnemyFarmer> {

  private static final int NAVIGATE_DELAY = 500;
  private final int pumpkinDist;

  private EntityNavigator navi;
  private long lastNavigate;

  public EnemyFarmerController(EnemyFarmer mobileEntity) {
    super(mobileEntity);

    this.pumpkinDist = Game.random().nextInt(10, 20);
    AStarGrid grid = GameManager.getCurrentGrid();

    this.navi = new EntityNavigator(mobileEntity, new AStarPathFinder(grid));
  }

  @Override
  public void update() {
    super.update();

    if (this.getEntity().getTarget() == null) {
      return;
    }

    if (this.getEntity().getTarget().isDead()) {
      this.getEntity().updateTarget();

      if (this.getEntity().getTarget() == null) {
        return;
      }
    }

    if (Game.time().since(this.lastNavigate) < NAVIGATE_DELAY) {
      return;
    }

    double dist = this.getEntity().getTarget().getCenter().distance(this.getEntity().getCenter());
    if (dist > pumpkinDist && !this.navi.isNavigating()) {

      this.navi.navigate(this.getEntity().getTarget().getCenter());
    } else {
      if (this.navi.isNavigating()) {
        this.navi.stop();
      }

      if (this.getEntity().getStabAbility().canCast()) {
        this.getEntity().getStabAbility().cast();
      }
    }
  }
}
