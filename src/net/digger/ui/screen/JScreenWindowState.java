package net.digger.ui.screen;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.EnumSet;

import net.digger.ui.screen.color.Attr;

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
 * Class for saving and restoring current text window state.
 * @author walton
 */
public class JScreenWindowState {
	/**
	 * The saved text window.
	 */
	public Rectangle window;
	/**
	 * The saved window-relative cursor position.
	 */
	public Point cursor;
	/**
	 * The saved current text font index.
	 */
	public int font;
	/**
	 * The saved current foreground color.
	 */
	public int fgColor;
	/**
	 * The saved current background color.
	 */
	public int bgColor;
	/**
	 * The saved current text attributes.
	 */
	public EnumSet<Attr> attrs;
}

