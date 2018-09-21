package net.digger.ui.screen.font;

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
 * Defines the parameters of CBM fonts.
 * @author walton
 */
public class CBMFont extends JScreenFont {
	/**
	 * Characters to draw a frame around the outside of character cells.
	 */
	public static final char[] OUTER_FRAME = new char[] {0x4F, 0xE3, 0x50, 0x65, 0x67, 0x4C, 0x64, 0x7A};
	/**
	 * Characters to draw a frame through the center of character cells.
	 */
	public static final char[] MIDDLE_FRAME = new char[] {0x70, 0x40, 0x6E, 0x5D, 0x5D, 0x6D, 0x40, 0x7D};
	/**
	 * Characters to draw a frame through the center of character cells, with rounded corners.
	 */
	public static final char[] ROUNDED_FRAME = new char[] {0x55, 0x40, 0x49, 0x5D, 0x5D, 0x4A, 0x40, 0x4B};

	private static final String copyright = "\u00A92011 by Kreative Software (http://www.kreativekorp.com)";
	// From: http://www.kreativekorp.com/software/fonts/c64.shtml
	// AppleII, TRS-80, and more also available:
	// http://www.kreativekorp.com/software/fonts/index.shtml


	/**
	 * Commodore VIC-20 font.
	 */
	public static final CBMFont VIC20 = new CBMFont("fonts/CBM/Pet/PetMe2X.ttf", 8, copyright);

	/**
	 * Commodore PET font.
	 */
	public static final CBMFont Pet = new CBMFont("fonts/CBM/Pet/PetMe.ttf", 8, copyright);

	/**
	 * Commodore CBM-II font.
	 */
	public static final CBMFont CBM_II = new CBMFont("fonts/CBM/Pet/PetMe2Y.ttf", 16, copyright);

	/**
	 * Commodore 64 40-column font.
	 */
	public static final CBMFont C64_40 = new CBMFont("fonts/CBM/Pet/PetMe64.ttf", 8, copyright);

	/**
	 * Commodore 64 80-column font.
	 */
	public static final CBMFont C64_80 = new CBMFont("fonts/CBM/Pet/PetMe642Y.ttf", 8, copyright);

	/**
	 * Commodore 128 40-column font.
	 */
	public static final CBMFont C128_40 = new CBMFont("fonts/CBM/Pet/PetMe128.ttf", 8, copyright);

	/**
	 * Commodore 128 80-column font.
	 */
	public static final CBMFont C128_80 = new CBMFont("fonts/CBM/Pet/PetMe1282Y.ttf", 8, copyright);
	
	/**
	 * Create a new CBM font.
	 * @param file Path to the font file in the .jar file.
	 * @param pointSize Font base point size (where the font renders 1px as 1px).
	 * @param about Copyright string for font.
	 */
	public CBMFont(String file, int pointSize, String about) {
		super(registerFont(file), pointSize, about, false);
	}
}
