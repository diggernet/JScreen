package net.digger.ui.screen.mode;

import net.digger.ui.screen.charmap.CP437CharMap;
import net.digger.ui.screen.charmap.JScreenCharMap;
import net.digger.ui.screen.color.CGAColor;
import net.digger.ui.screen.color.JScreenPalette;
import net.digger.ui.screen.color.GreenScreenColor;
import net.digger.ui.screen.cursor.HorizontalCursor;
import net.digger.ui.screen.cursor.JScreenCursor;
import net.digger.ui.screen.font.PCFont;

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
 * Defines standard PC text modes.
 * @author walton
 */
public class PCScreenMode extends JScreenMode {
	private static final JScreenCharMap charMap = new CP437CharMap();
	
	/**
	 * MDA 80x25 (720x350px with 9x14 font)
	 */
	public static final PCScreenMode MDA_80x25 = new PCScreenMode(80, 25, new HorizontalCursor(12, 13), PCFont.MDA_9x14, GreenScreenColor.PALETTE, charMap, false);

	/**
	 * CGA 40x25 (320x200px with 8x8 font)
	 */
	public static final PCScreenMode CGA_40x25 = new PCScreenMode(40, 25, new HorizontalCursor(7, 7), PCFont.CGA_8x8_wide, CGAColor.PALETTE, charMap, false);
	/**
	 * CGA 80x25 (640x200px with 8x8 font)
	 */
	public static final PCScreenMode CGA_80x25 = new PCScreenMode(80, 25, new HorizontalCursor(7, 7), PCFont.CGA_8x8, CGAColor.PALETTE, charMap, false);

	/**
	 * EGA 80x25 (640x350 with 8x14 font)
	 */
	public static final PCScreenMode EGA_80x25 = new PCScreenMode(80, 25, new HorizontalCursor(12, 13), PCFont.EGA_8x14, CGAColor.PALETTE, charMap, false);
	/**
	 * EGA 80x43 (640x344 with 8x8 font)
	 */
	public static final PCScreenMode EGA_80x43 = new PCScreenMode(80, 43, new HorizontalCursor(7, 7), PCFont.BIOS_8x8, CGAColor.PALETTE, charMap, false);

	/**
	 * VGA 80x25 (720x400 with 9x16 font)
	 */
	public static final PCScreenMode VGA_80x25 = new PCScreenMode(80, 25, new HorizontalCursor(14, 15), PCFont.VGA_9x16, CGAColor.PALETTE, charMap, false);
	/**
	 * VGA 40x25 (360x400 with 9x16 font)
	 */
	public static final PCScreenMode VGA_40x25 = new PCScreenMode(40, 25, new HorizontalCursor(14, 15), PCFont.VGA_9x16_wide, CGAColor.PALETTE, charMap, false);
	/**
	 * VGA 80x30 (640x480 with 8x16 font)
	 */
	public static final PCScreenMode VGA_80x30 = new PCScreenMode(80, 30, new HorizontalCursor(14, 15), PCFont.VGA_8x16, CGAColor.PALETTE, charMap, false);
	/**
	 * VGA 80x50 (640x400 with 8x8 font)
	 */
	public static final PCScreenMode VGA_80x50 = new PCScreenMode(80, 50, new HorizontalCursor(7, 7), PCFont.BIOS_8x8, CGAColor.PALETTE, charMap, false);
	/**
	 * VGA 80x60 (640x480 with 8x8 font)
	 */
	public static final PCScreenMode VGA_80x60 = new PCScreenMode(80, 60, new HorizontalCursor(7, 7), PCFont.BIOS_8x8, CGAColor.PALETTE, charMap, false);

	public static final PCScreenMode DEFAULT_MODE = VGA_80x25;
	
	/**
	 * Create a new screen mode.
	 * @param width Width of the screen in chars.
	 * @param height Height of the screen in chars.
	 * @param cursor Cursor renderer to use.
	 * @param font Font to display.
	 * @param palette List of available colors, and default FG and BG colors.
	 * @param charMap CharMap for mapping between character sets.
	 * @param scanLines Display black lines between scan lines (for scales {@literal >1}).
	 */
	public PCScreenMode(int width, int height, JScreenCursor cursor, PCFont font, JScreenPalette palette, JScreenCharMap charMap, boolean scanLines) {
		super(width, height, cursor, font, palette, charMap, scanLines);
	}
}
