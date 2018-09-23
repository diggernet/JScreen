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
 * Horizontal cursor renderer.
 * @author walton
 */
public class HorizontalCursor implements JScreenCursor {
	/**
	 * Cell scan line cursor starts on.
	 */
	public int top;
	/**
	 * Cell scan line cursor ends on.
	 */
	public int bottom;

	/**
	 * Create new instance of horizontal cursor renderer.
	 * @param top Cursor start line in character cell.
	 * @param bottom Cursor end line in character cell.
	 */
	public HorizontalCursor(int top, int bottom) {
		this.top = top;
		this.bottom = bottom;
	}
	
	@Override
	public void drawCursor(Graphics g, Rectangle cell, Color color, int scale) {
		int rightEdge = cell.x + cell.width;
		int bottomEdge = cell.y + cell.height;
		int start = cell.y + (top * scale);
		int end = cell.y + ((bottom + 1) * scale);
		for (int line = cell.y; line<bottomEdge; line++) {
			if ((line >= start) && (line < end)) {
				g.setColor(color);
				g.drawLine(cell.x, line, rightEdge - 1, line);
			}
		}
	}
}
