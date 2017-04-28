package net.digger.ui.screen.color;

import java.awt.Color;
import java.util.EnumSet;

import net.digger.ui.screen.JScreenCell;

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
 * Defines the palette of available colors for a screen.
 * @author walton
 */
public class JScreenPalette {
	/**
	 * Array of available colors.
	 */
	public final Color[] color;
	/**
	 * Default foreground color.
	 */
	public final int defaultFG;
	/**
	 * Default background color.
	 */
	public final int defaultBG;
	
	/**
	 * Create a new palette instance.
	 * @param defaultFG Default foreground color.
	 * @param defaultBG Default background color.
	 * @param colors Array of available colors.
	 */
	public JScreenPalette(int defaultFG, int defaultBG, Color... colors) {
		this.defaultFG = defaultFG;
		this.defaultBG = defaultBG;
		this.color = colors;
	}
	
	/**
	 * Returns the color at the given palette index.
	 * @param index
	 * @return
	 */
	public Color get(int index) {
		return color[index];
	}
	
	/**
	 * Returns the FG color to use for the given screen character cell.
	 * @param cell
	 * @return
	 */
	public Color getFG(JScreenCell cell) {
		return getFG(cell.fg, cell.bg, cell.attrs);
	}
	
	/**
	 * Determines the FG color to use based on the given parameters.
	 * @param fgIndex Palette index for current foreground color.
	 * @param bgIndex Palette index for current background color.
	 * @param attrs Current character attributes.
	 * @return
	 */
	public Color getFG(int fgIndex, int bgIndex, EnumSet<Attr> attrs) {
		if ((attrs != null) && attrs.contains(Attr.REVERSE)) {
			return color[bgIndex];
		} else {
			return color[fgIndex];
		}
	}
	
	/**
	 * Returns the BG color to use for the given screen character cell.
	 * @param cell
	 * @return
	 */
	public Color getBG(JScreenCell cell) {
		return getBG(cell.fg, cell.bg, cell.attrs);
	}
	
	/**
	 * Determines the BG color to use based on the given parameters.
	 * @param fgIndex Palette index for current foreground color.
	 * @param bgIndex Palette index for current background color.
	 * @param attrs Current character attributes.
	 * @return
	 */
	public Color getBG(int fgIndex, int bgIndex, EnumSet<Attr> attrs) {
		if ((attrs != null) && attrs.contains(Attr.REVERSE)) {
			return color[fgIndex];
		} else {
			return color[bgIndex];
		}
	}
}
