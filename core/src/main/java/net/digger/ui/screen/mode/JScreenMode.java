package net.digger.ui.screen.mode;

import net.digger.ui.screen.charmap.JScreenCharMap;
import net.digger.ui.screen.color.JScreenPalette;
import net.digger.ui.screen.color.GreenScreenColor;
import net.digger.ui.screen.cursor.BlockCursor;
import net.digger.ui.screen.cursor.JScreenCursor;
import net.digger.ui.screen.font.SystemFont;
import net.digger.ui.screen.font.JScreenFont;

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
 * Defines the parameters of a text mode.
 * @author walton
 */
public class JScreenMode {
	/**
	 * Basic predefined screen mode: 80x25 green screen.
	 */
	public static final JScreenMode DEFAULT_MODE = new JScreenMode(80, 25, new BlockCursor(), SystemFont.MONOSPACED, GreenScreenColor.PALETTE);
	
	/**
	 * Width of the screen (chars).
	 */
	public final int width;
	/**
	 * Height of the screen (lines).
	 */
	public final int height;
	/**
	 * Cursor renderer.
	 */
	public final JScreenCursor cursor;
	/**
	 * Display font.
	 */
	public final JScreenFont font;
	/**
	 * Text color palette.
	 */
	public final JScreenPalette palette;
	/**
	 * Display CRT scan lines?
	 */
	public final boolean scanLines;
	/**
	 * Character conversion map.
	 */
	public final JScreenCharMap charMap;
	
	/**
	 * Text blink rate.
	 * 	VGA blink rate is vertical sync rate / 32.  (1.875 blinks per second at 60hz).
	 */
	public double blinkRate = 2;
	
	/**
	 * Create a new screen mode.
	 * @param width Width of the screen in chars.
	 * @param height Height of the screen in lines.
	 * @param cursor Cursor renderer to use.
	 * @param font Font to display.
	 * @param palette List of available colors, and default FG and BG colors.
	 */
	public JScreenMode(int width, int height, JScreenCursor cursor, JScreenFont font, JScreenPalette palette) {
		this(width, height, cursor, font, palette, null, false);
	}
	
	/**
	 * Create a new screen mode.
	 * @param width Width of the screen in chars.
	 * @param height Height of the screen in chars.
	 * @param cursor Cursor renderer to use.
	 * @param font Font to display.
	 * @param palette List of available colors, and default FG and BG colors.
	 * @param charMap CharMap for mapping between character sets.
	 */
	public JScreenMode(int width, int height, JScreenCursor cursor, JScreenFont font, JScreenPalette palette, JScreenCharMap charMap) {
		this(width, height, cursor, font, palette, charMap, false);
	}
	
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
	public JScreenMode(int width, int height, JScreenCursor cursor, JScreenFont font, JScreenPalette palette, JScreenCharMap charMap, boolean scanLines) {
		this.width = width;
		this.height = height;
		this.cursor = cursor;
		this.font = font;
		this.palette = palette;
		this.charMap = charMap;
		this.scanLines = scanLines;
	}
}
