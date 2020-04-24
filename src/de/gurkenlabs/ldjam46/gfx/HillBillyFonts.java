package de.gurkenlabs.ldjam46.gfx;

import java.awt.Font;

import de.gurkenlabs.litiengine.resources.Resources;

public class HillBillyFonts {
  public static final Font MENU_FONT = Resources.fonts().get("JANGLYWALK.TTF").deriveFont(48f);
  public static final Font UI_FONT1 = Resources.fonts().get("WoodCabin.ttf").deriveFont(32f);
  public static final Font PIXEL_UI_FONT = Resources.fonts().get("fsex300.ttf").deriveFont(10f);
  public static final Font SPEECHBUBBLE_FONT = PIXEL_UI_FONT.deriveFont(6f);
}
