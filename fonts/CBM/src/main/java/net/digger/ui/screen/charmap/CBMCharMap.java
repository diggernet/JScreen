package net.digger.ui.screen.charmap;

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
 * Character translation map for CBM fonts.
 * @author walton
 */
public class CBMCharMap extends JScreenCharMap {
	// For use with PetMe, PetMe2X, PetMe2Y
	public static final CBMCharMap PetCharMap = new CBMCharMap(0xE000);
	public static final CBMCharMap VIC20CharMap = new CBMCharMap(0xE200);
	public static final CBMCharMap CBM2CharMap = new CBMCharMap(0xE800);
	// For use with PetMe64, PetMe642Y, PetMe128, PetMe1282Y
	public static final CBMCharMap C64CharMap = PetCharMap;
	public static final CBMCharMap C128CharMap = VIC20CharMap;
	
	/**
	 * Create a new instance of the PETSCII translation map.
	 * @param base Unicode character to start incrementing from.
	 */
	public CBMCharMap(int base) {
		for (int i=0; i<256; i++) {
			charMap.put((char)i, (char)(base + i));
		}
	}
	
	/*
	 * https://damieng.com/blog/2011/02/20/typography-in-8-bits-system-fonts
	 * http://www.kreativekorp.com/software/fonts/c64.shtml
	 * 
	 * http://www.ascii-codes.com/
	 * https://en.wikipedia.org/wiki/ASCII
	 * https://en.wikipedia.org/wiki/PETSCII
	 */
}
