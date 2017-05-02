package net.digger.ui.screen.cursor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

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
 * Interface for a cursor renderer.
 * @author walton
 */
public interface JScreenCursor {
	/**
	 * Draws the cursor in the given text cell, in the given color, at the given scale.
	 * @param g Graphics context to draw to.
	 * @param cell Cell bounds in g, in pixels.
	 * @param color Color to draw cursor.
	 * @param scale Pixels in g for each scan line in the cell.
	 */
	public void drawCursor(Graphics g, Rectangle cell, Color color, int scale);
}
