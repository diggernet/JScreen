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
 * Block cursor renderer.
 * @author walton
 */
public class BlockCursor implements JScreenCursor {
	@Override
	public void drawCursor(Graphics g, Rectangle cell, Color color, int scale) {
		g.setColor(color);
		g.fillRect(cell.x, cell.y, cell.width, cell.height);
	}
}
