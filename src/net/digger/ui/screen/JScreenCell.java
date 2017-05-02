package net.digger.ui.screen;

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
 * Data structure for a screen character cell.
 * @author walton
 */
public class JScreenCell {
	/**
	 * Display character.
	 */
	public char ch;
	/**
	 * Index into current font list.
	 */
	public int font;
	/**
	 * Foreground color.
	 */
	public int fg;
	/**
	 * Background color.
	 */
	public int bg;
	/**
	 * Text attributes.
	 */
	public final EnumSet<Attr> attrs = EnumSet.noneOf(Attr.class);

	/**
	 * Create a new character cell.
	 */
	public JScreenCell() {}
	
	/**
	 * Create a new character cell with the given values.
	 * @param ch
	 * @param fg
	 * @param bg
	 * @param attrs
	 */
	public JScreenCell(char ch, int fg, int bg, Attr... attrs) {
		this.ch = ch;
		this.fg = fg;
		this.bg = bg;
		setAttrs(attrs);
	}
	
	/**
	 * Set the text attributes for this character cell, with an array of attributes.
	 * @param attrs
	 */
	public void setAttrs(Attr... attrs) {
		this.attrs.clear();
		if (attrs != null) {
			for (Attr attr : attrs) {
				if (attr != null) {
					this.attrs.add(attr);
				}
			}
		}
	}

	/**
	 * Set the text attributes for this character cell, with an EnumSet of attributes.
	 * @param attrs
	 */
	public void setAttrs(EnumSet<Attr> attrs) {
		this.attrs.clear();
		this.attrs.addAll(attrs);
	}
	
	/**
	 * Add or remove an attribute for this character cell.
	 * @param attr
	 * @param on
	 */
	public void setAttr(Attr attr, boolean on) {
		if (on) {
			attrs.add(attr);
		} else {
			attrs.remove(attr);
		}
	}
	
	/**
	 * Toggle an attribute for this character cell.
	 * @param attr
	 */
	public void toggleAttr(Attr attr) {
		if (attrs.contains(attr)) {
			attrs.remove(attr);
		} else {
			attrs.add(attr);
		}
	}
}
