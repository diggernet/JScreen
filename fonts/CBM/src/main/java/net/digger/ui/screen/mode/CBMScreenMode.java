package net.digger.ui.screen.mode;

import net.digger.ui.screen.charmap.JScreenCharMap;
import net.digger.ui.screen.charmap.CBMCharMap;
import net.digger.ui.screen.color.JScreenPalette;
import net.digger.ui.screen.color.GreenScreenColor;
import net.digger.ui.screen.cursor.BlockCursor;
import net.digger.ui.screen.cursor.JScreenCursor;
import net.digger.ui.screen.font.CBMFont;

/**
 * Copyright © 2017  David Walton
 * 
 * This file is part of JScreen.
 * 
 * JScreen is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * Defines some standard CBM text modes.
 * @author walton
 */
public class CBMScreenMode extends JScreenMode {

	/**
	 * VIC-20 22x23 (176x184px with 8x8 font)
	 */
	public static final CBMScreenMode VIC20 = new CBMScreenMode(22, 23, new BlockCursor(), CBMFont.VIC20, GreenScreenColor.PALETTE, CBMCharMap.VIC20CharMap, true);

	/**
	 * Pet 40x25 (320x200px with 8x8 font)
	 */
	public static final CBMScreenMode Pet2K = new CBMScreenMode(40, 25, new BlockCursor(), CBMFont.Pet, GreenScreenColor.PALETTE, CBMCharMap.PetCharMap, true);

	/**
	 * Pet 80x25 (640x200px with 8x8 font)
	 */
	public static final CBMScreenMode Pet4K = new CBMScreenMode(80, 25, new BlockCursor(), CBMFont.CBM_II, GreenScreenColor.PALETTE, CBMCharMap.PetCharMap, true);

	/**
	 * CBM-II 40x25 (320x200px with 8x8 font)
	 */
	public static final CBMScreenMode CBM_IIP = new CBMScreenMode(40, 25, new BlockCursor(), CBMFont.Pet, GreenScreenColor.PALETTE, CBMCharMap.CBM2CharMap, true);

	/**
	 * CBM-II 80x25 (640x200px with 8x8 font)
	 */
	public static final CBMScreenMode CBM_IIB = new CBMScreenMode(80, 25, new BlockCursor(), CBMFont.CBM_II, GreenScreenColor.PALETTE, CBMCharMap.CBM2CharMap, true);

	/**
	 * C64 40x25 (320x200px with 8x8 font)
	 */
	public static final CBMScreenMode C64 = new CBMScreenMode(40, 25, new BlockCursor(), CBMFont.C64_40, GreenScreenColor.PALETTE, CBMCharMap.C64CharMap, true);

	/**
	 * C128 80x25 (640x200px with 8x8 font)
	 */
	public static final CBMScreenMode C128 = new CBMScreenMode(80, 25, new BlockCursor(), CBMFont.C128_80, GreenScreenColor.PALETTE, CBMCharMap.C128CharMap, true);

	public static final CBMScreenMode DEFAULT_MODE = Pet2K;
	
	/**
	 * Create a new screen mode.
	 * @param width Width of the screen in chars.
	 * @param height Height of the screen in chars.
	 * @param cursor Cursor renderer to use.
	 * @param font Font to display.
	 * @param palette List of available colors, and default FG and BG colors.
	 * @param charMap CharMap for mapping between character sets.
	 * @param scanLines Display black lines between scan lines (for scales >1).
	 */
	public CBMScreenMode(int width, int height, JScreenCursor cursor, CBMFont font, JScreenPalette palette, JScreenCharMap charMap, boolean scanLines) {
		super(width, height, cursor, font, palette, charMap, scanLines);
	}
}
