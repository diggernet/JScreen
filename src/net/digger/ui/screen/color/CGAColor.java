package net.digger.ui.screen.color;

import java.awt.Color;
import java.util.EnumSet;

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
 * Defines the standard colors available for PC text.
 * @author walton
 */
public class CGAColor extends JScreenPalette {
	public static final int BLACK = 0x0;
	public static final int BLUE = 0x1;
	public static final int GREEN = 0x2;
	public static final int CYAN = 0x3;
	public static final int RED = 0x4;
	public static final int MAGENTA = 0x5;
	public static final int BROWN = 0x6;
	public static final int LIGHT_GREY = 0x7;
	
	public static final int BOLD = 0x8;
	
	public static final int DEFAULT_FG = LIGHT_GREY;
	public static final int DEFAULT_BG = BLACK;

	/**
	 * The palette for a PC text screen.
	 */
	public static final CGAColor PALETTE = new CGAColor(DEFAULT_FG, DEFAULT_BG,
		new Color(0x00, 0x00, 0x00),	// Black
		new Color(0x00, 0x00, 0xAA),	// Blue
		new Color(0x00, 0xAA, 0x00),	// Green
		new Color(0x00, 0xAA, 0xAA),	// Cyan
		new Color(0xAA, 0x00, 0x00),	// Red
		new Color(0xAA, 0x00, 0xAA),	// Magenta
		new Color(0xAA, 0x55, 0x00),	// Brown
		new Color(0xAA, 0xAA, 0xAA),	// Light Grey
		new Color(0x55, 0x55, 0x55),	// Dark Grey
		new Color(0x55, 0x55, 0xFF),	// Light Blue
		new Color(0x55, 0xFF, 0x55),	// Light Green
		new Color(0x55, 0xFF, 0xFF),	// Light Cyan
		new Color(0xFF, 0x55, 0x55),	// Light Red
		new Color(0xFF, 0x55, 0xFF),	// Light Magenta
		new Color(0xFF, 0xFF, 0x55),	// Yellow
		new Color(0xFF, 0xFF, 0xFF)		// White
	);

	private CGAColor(int defaultFG, int defaultBG, Color... colors) {
		super(defaultFG, defaultBG, colors);
	}
	
	@Override
	public Color getFG(int fgIndex, int bgIndex, EnumSet<Attr> attrs) {
		if (attrs == null) {
			return color[fgIndex & ~BOLD];
		}
		if (attrs.contains(Attr.REVERSE)) {
			return color[bgIndex & ~BOLD];
		}
		if (attrs.contains(Attr.BOLD)) {
			return color[fgIndex | BOLD];
		}
		return color[fgIndex & ~BOLD];
	}
	
	@Override
	public Color getBG(int fgIndex, int bgIndex, EnumSet<Attr> attrs) {
		if (attrs == null) {
			return color[bgIndex & ~BOLD];
		}
		if (attrs.contains(Attr.REVERSE)) {
			if (attrs.contains(Attr.BOLD)) {
				return color[fgIndex | BOLD];
			}
			return color[fgIndex & ~BOLD];
		}
		return color[bgIndex & ~BOLD];
	}
}
