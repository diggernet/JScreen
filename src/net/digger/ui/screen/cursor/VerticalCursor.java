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
 * Vertical cursor renderer.
 * @author walton
 */
public class VerticalCursor implements JScreenCursor {
	/**
	 * Cell column cursor starts on.
	 */
	public int left;
	/**
	 * Cell column cursor ends on.
	 */
	public int right;

	/**
	 * Create new instance of vertical cursor renderer.
	 * @param left Cursor start column in character cell.
	 * @param right Cursor end column in character cell.
	 */
	public VerticalCursor(int left, int right) {
		this.left = left;
		this.right = right;
	}
	
	@Override
	public void drawCursor(Graphics g, Rectangle cell, Color color, int scale) {
		int rightEdge = cell.x + cell.width;
		int bottomEdge = cell.y + cell.height;
		int start = cell.x + (left * scale);
		int end = cell.x + ((right + 1) * scale);
		for (int column = cell.x; column<rightEdge; column++) {
			if ((column >= start) && (column < end)) {
				g.setColor(color);
				g.drawLine(column, cell.y, column, bottomEdge - 1);
			}
		}
	}
}
