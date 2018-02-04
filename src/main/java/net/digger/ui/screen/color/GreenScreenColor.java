package net.digger.ui.screen.color;

import java.awt.Color;

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
 * Defines the standard colors available for a basic green screen.
 * @author walton
 */
public class GreenScreenColor extends JScreenPalette {
	public static final int BLACK = 0x0;
	public static final int GREEN = 0x1;
	
	public static final int DEFAULT_FG = GREEN;
	public static final int DEFAULT_BG = BLACK;

	/**
	 * The palette for a green screen.
	 */
	public static final GreenScreenColor PALETTE = new GreenScreenColor(DEFAULT_FG, DEFAULT_BG, Color.BLACK, Color.GREEN);

	private GreenScreenColor(int defaultFG, int defaultBG, Color... colors) {
		super(defaultFG, defaultBG, colors);
	}
}
