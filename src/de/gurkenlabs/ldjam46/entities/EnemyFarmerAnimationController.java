package de.gurkenlabs.ldjam46.entities;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Optional;

import de.gurkenlabs.ldjam46.GameManager;
import de.gurkenlabs.ldjam46.GameManager.GameState;
import de.gurkenlabs.litiengine.graphics.CreatureShadowImageEffect;
import de.gurkenlabs.litiengine.graphics.animation.CreatureAnimationController;
import de.gurkenlabs.litiengine.resources.Resources;
import de.gurkenlabs.litiengine.util.Imaging;
import de.gurkenlabs.litiengine.util.geom.GeometricUtilities;

public class EnemyFarmerAnimationController extends CreatureAnimationController<EnemyFarmer> {

  private final CreatureShadowImageEffect effect;

  public EnemyFarmerAnimationController(EnemyFarmer farmer) {
    super(farmer, true);
    effect = new CreatureShadowImageEffect(farmer, new Color(24, 30, 28, 100));
    effect.setOffsetY(1);
  }

  @Override
  public BufferedImage getCurrentImage() {
    BufferedImage spriteImage = super.getCurrentImage();
    if (spriteImage == null) {
      return null;
    }

    boolean highlight = GameManager.getState() == GameState.INGAME
        && !this.getEntity().isFartedOn()
        && Farmer.instance().getFartAbility().isEnabled()
        && Farmer.instance().getFartAbility().canCast()
        && GeometricUtilities.shapeIntersects(this.getEntity().getHitBox(), Farmer.instance().getFartAbility().calculateImpactArea());

    String cacheKey = this.buildCurrentCacheKey();
    cacheKey += "_shadow";
    cacheKey += "_" + highlight;

    Optional<BufferedImage> opt = Resources.images().tryGet(cacheKey);
    if (opt.isPresent()) {
      return opt.get();
    }

    if (highlight) {
      BufferedImage border = Imaging.borderAlpha(spriteImage, new Color(174, 237, 0, 200), false);

      spriteImage = this.effect.apply(border);
      Resources.images().add(cacheKey, spriteImage);

      return border;
    }

    spriteImage = this.effect.apply(spriteImage);

    Resources.images().add(cacheKey, spriteImage);
    return spriteImage;
  }

}
