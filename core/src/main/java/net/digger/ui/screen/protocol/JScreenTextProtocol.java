package net.digger.ui.screen.protocol;

import net.digger.ui.screen.JScreen;

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
 * A basic text protocol handler which does nothing, just passes the characters through to the screen.
 * @author walton
 */
public class JScreenTextProtocol {
	/**
	 * JScreen used to display text.
	 */
	protected final JScreen screen;

	/**
	 * Create instance of this protocol handler.
	 * @param screen JScreen for text display.
	 */
	public JScreenTextProtocol(JScreen screen) {
		this.screen = screen;
	}
	
	/**
	 * Print a character to screen, processing it according to the implemented text protocol.
	 * @param ch Character to inspect and display.
	 */
	public void print(char ch) {
		screen.putChar(ch);
	}
}
