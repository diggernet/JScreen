package net.digger.ui.screen.color;

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
 * Attributes for characters on screen.
 * @author walton
 */
public enum Attr {
	/**
	 * Reverse FG and BG.
	 */
	REVERSE,
	/**
	 * Bold foreground.
	 */
	BOLD,
	/**
	 * Blinking text.
	 */
	BLINKING,
	/**
	 * Blinking character is currently not visible (blinked off).
	 * For internal use only.
	 */
	_IS_BLINKED,
	/**
	 * Character is currently selected.
	 * For internal use only.
	 */
	_IS_SELECTED,
	// Other attributes available for custom purposes
	USER_1, USER_2, USER_3, USER_4, USER_5, USER_6, USER_7, USER_8, 
}
