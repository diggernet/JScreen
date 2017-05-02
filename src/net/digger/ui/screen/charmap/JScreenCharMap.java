package net.digger.ui.screen.charmap;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

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
 * Character translation map.
 * Used to map from Unicode chars to screen font chars, and back.
 * @author walton
 */
public class JScreenCharMap {
	protected final BidiMap<Character, Character> charMap = new DualHashBidiMap<>();

	/**
	 * Returns character to display for the given Unicode character.
	 * @param ch
	 * @return
	 */
	public Character mapChar(char ch) {
		Character chr = charMap.get(ch);
		return (chr == null) ? ch : chr;
	}
	
	/**
	 * Returns the Unicode character for the given display character.
	 * @param ch
	 * @return
	 */
	public Character unmapChar(char ch) {
		Character chr = charMap.getKey(ch);
		return (chr == null) ? ch : chr;
	}
}
