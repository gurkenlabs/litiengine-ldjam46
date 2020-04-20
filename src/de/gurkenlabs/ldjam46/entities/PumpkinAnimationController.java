package de.gurkenlabs.ldjam46.entities;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Optional;

import de.gurkenlabs.ldjam46.GameManager;
import de.gurkenlabs.ldjam46.GameManager.GameState;
import de.gurkenlabs.litiengine.graphics.animation.PropAnimationController;
import de.gurkenlabs.litiengine.resources.Resources;
import de.gurkenlabs.litiengine.util.Imaging;
import de.gurkenlabs.litiengine.util.geom.GeometricUtilities;

public class PumpkinAnimationController extends PropAnimationController<Pumpkin> {

  public PumpkinAnimationController(Pumpkin prop) {
    super(prop);
  }

  @Override
  public BufferedImage getCurrentImage() {
    BufferedImage spriteImage = super.getCurrentImage();
    if (spriteImage == null) {
      return null;
    }

    boolean highlight = GameManager.getState() == GameState.INGAME
        && !this.getEntity().isDead()
        && Farmer.instance().getWaterAbility().canCast()
        && Farmer.instance().hasCan()
        && GeometricUtilities.shapeIntersects(this.getEntity().getHitBox(), Farmer.instance().getWaterAbility().calculateImpactArea());

    String cacheKey = this.buildCurrentCacheKey();
    cacheKey += "_" + this.getEntity().isAddShadow();
    cacheKey += "_" + this.getEntity().getState();
    cacheKey += "_" + this.getEntity().getSpriteRotation();
    cacheKey += "_" + this.getEntity().flipHorizontally();
    cacheKey += "_" + this.getEntity().flipVertically();
    cacheKey += "_shadow";
    cacheKey += "_" + highlight;

    Optional<BufferedImage> opt = Resources.images().tryGet(cacheKey);
    if (opt.isPresent()) {
      return opt.get();
    }

    BufferedImage scaled = Imaging.scale(spriteImage, (int) this.getEntity().getWidth(), (int) this.getEntity().getHeight());
    if (highlight) {
      BufferedImage border = Imaging.borderAlpha(scaled, new Color(16, 84, 167, 200), false);

      final int ShadowYOffset = border.getHeight();
      final BufferedImage shadow = Imaging.addShadow(border, 0, ShadowYOffset);
      Resources.images().add(cacheKey, shadow);

      return border;
    }

    final int ShadowYOffset = scaled.getHeight();
    final BufferedImage shadow = Imaging.addShadow(scaled, 0, ShadowYOffset);

    Resources.images().add(cacheKey, shadow);
    return spriteImage;
  }
}
