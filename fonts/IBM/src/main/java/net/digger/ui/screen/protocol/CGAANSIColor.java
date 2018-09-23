/**
 * Copyright © 2018  David Walton
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
package net.digger.ui.screen.protocol;

import net.digger.ui.screen.color.CGAColor;

/**
 * Implementation of ANSIColor to use ANSI protocol with CGAColor colors.
 * 
 * @author walton
 */
public class CGAANSIColor implements ANSIColor {
	@Override
	public int getDefaultFG() {
		return CGAColor.DEFAULT_FG;
	}

	@Override
	public int getDefaultBG() {
		return CGAColor.DEFAULT_BG;
	}

	@Override
	public int getBlack() {
		return CGAColor.BLACK;
	}

	@Override
	public int getRed() {
		return CGAColor.RED;
	}

	@Override
	public int getGreen() {
		return CGAColor.GREEN;
	}

	@Override
	public int getBrown() {
		return CGAColor.BROWN;
	}

	@Override
	public int getBlue() {
		return CGAColor.BLUE;
	}

	@Override
	public int getMagenta() {
		return CGAColor.MAGENTA;
	}

	@Override
	public int getCyan() {
		return CGAColor.CYAN;
	}

	@Override
	public int getLightGrey() {
		return CGAColor.LIGHT_GREY;
	}

}
