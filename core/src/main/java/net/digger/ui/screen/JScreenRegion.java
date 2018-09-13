package net.digger.ui.screen;

import java.awt.Dimension;

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
 * Defines a grid of character cells, to represent a region of the screen.
 * @author walton
 */
public class JScreenRegion {
	/**
	 * Size of the screen region.
	 */
	public final Dimension size;
	/**
	 * Region character cells.
	 * This array is referenced by [y][x] to simplify scrolling.
	 */
	public final JScreenCell[][] cells;

	/**
	 * Create a new screen region of the given size.
	 * @param size Dimensions of screen region.
	 */
	public JScreenRegion(Dimension size) {
		this.size = size;
		cells = createCellGrid(size);
	}

	/**
	 * Create a two-dimensional character cell array of the given size.
	 * @param size Dimensions of screen region.
	 * @return Array of screen cells.
	 */
	public static JScreenCell[][] createCellGrid(Dimension size) {
		JScreenCell[][] cells = new JScreenCell[size.height][size.width];
		for (int y=0; y<size.height; y++) {
			for (int x=0; x<size.width; x++) {
				cells[y][x] = new JScreenCell();
			}
		}
		return cells;
	}

	/**
	 * Create a character cell array of the given width.
	 * @param width Width of row.
	 * @return Array of screen cells.
	 */
	public static JScreenCell[] createCellRow(int width) {
		JScreenCell[] cells = new JScreenCell[width];
		for (int x=0; x<width; x++) {
			cells[x] = new JScreenCell();
		}
		return cells;
	}
}
