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
 * Defines the parameters of IBM fonts.
 * @author walton
 */
public class IBMFont extends JScreenFont {
	/**
	 * Characters to draw a single-line frame through the center of character cells.
	 */
	public static final char[] SINGLE_FRAME = new char[] {0xDA, 0xC4, 0xBF, 0xB3, 0xB3, 0xC0, 0xC4, 0xD9};	// ┌─┐││└─┘
	public static final char[] SINGLE_FRAME_SPACE = new char[] {0xDA, 0xC4, 0xBF, 0xB3, 0xB3, 0xC0, 0xC4, 0xD9, 0x20, 0x20};	// ┌─┐││└─┘  
	/**
	 * Characters to draw a double-line frame through the center of character cells.
	 */
	public static final char[] DOUBLE_FRAME = new char[] {0xC9, 0xCD, 0xBB, 0xBA, 0xBA, 0xC8, 0xCD, 0xBC};	// ╔═╗║║╚═╝
	public static final char[] DOUBLE_FRAME_SPACE = new char[] {0xC9, 0xCD, 0xBB, 0xBA, 0xBA, 0xC8, 0xCD, 0xBC, 0x20, 0x20};	// ╔═╗║║╚═╝  
	public static final char[] DOUBLE_FRAME_SINGLE_UP = new char[] {0xC9, 0xCD, 0xBB, 0xBA, 0xBA, 0xC8, 0xCD, 0xBC, 0xBE, 0xD4};	// ╔═╗║║╚═╝╛╘
	public static final char[] DOUBLE_FRAME_SINGLE_DOWN = new char[] {0xC9, 0xCD, 0xBB, 0xBA, 0xBA, 0xC8, 0xCD, 0xBC, 0xB8, 0xD5};	// ╔═╗║║╚═╝╕╒
	public static final char[] DOUBLE_FRAME_SINGLE_BAR = new char[] {0xC9, 0xCD, 0xBB, 0xBA, 0xBA, 0xC8, 0xCD, 0xBC, 0xB5, 0xC6};	// ╔═╗║║╚═╝╡╞

	private static final String copyright = "\u00A92016 by VileR (http://int10h.org)";


	/**
	 * IBM BIOS (& EGA/VGA) 8x8 font - 1:1 pixel ratio.
	 */
	public static final IBMFont BIOS_8x8 = new IBMFont("fonts/PC/IBM/Px437_IBM_BIOS.ttf", 8, copyright);
	/**
	 * IBM BIOS (& EGA/VGA) 8x8 font - 2:1 pixel ratio.
	 */
	public static final IBMFont BIOS_8x8_wide = new IBMFont("fonts/PC/IBM/Px437_IBM_BIOS-2x.ttf", 8, copyright);
	/**
	 * IBM BIOS (& EGA/VGA) 8x8 font - 1:2 pixel ratio.
	 */
	public static final IBMFont BIOS_8x8_tall = new IBMFont("fonts/PC/IBM/Px437_IBM_BIOS-2y.ttf", 16, copyright);

	/**
	 * IBM MDA - 9x14 font, 1:1 pixel ratio.
	 */
	public static final IBMFont MDA_9x14 = new IBMFont("fonts/PC/IBM/Px437_IBM_MDA.ttf", 16, copyright);

	/**
	 * IBM CGA (& PCjr) - default font, 1:1 pixel ratio (40 columns).
	 */
	public static final IBMFont CGA_8x8_wide = new IBMFont("fonts/PC/IBM/Px437_IBM_CGA.ttf", 8, copyright);
	/**
	 * IBM CGA (& PCjr) - default font, 1:2 pixel ratio (80 columns).
	 */
	public static final IBMFont CGA_8x8 = new IBMFont("fonts/PC/IBM/Px437_IBM_CGA-2y.ttf", 16, copyright);

	/**
	 * IBM EGA (& VGA) 8x14 font, 1:1 pixel ratio (80 columns).
	 */
	public static final IBMFont EGA_8x14 = new IBMFont("fonts/PC/IBM/Px437_IBM_EGA8.ttf", 16, copyright);
	/**
	 * IBM EGA (& VGA) 8x14 font, 2:1 pixel ratio (40 columns).
	 */
	public static final IBMFont EGA_8x14_wide = new IBMFont("fonts/PC/IBM/Px437_IBM_EGA8-2x.ttf", 16, copyright);
	/**
	 * IBM EGA (& VGA) 9x14 font, 1:1 pixel ratio (80 columns).
	 */
	public static final IBMFont EGA_9x14 = new IBMFont("fonts/PC/IBM/Px437_IBM_EGA9.ttf", 16, copyright);
	/**
	 * IBM EGA (& VGA) 9x14 font, 2:1 pixel ratio (40 columns).
	 */
	public static final IBMFont EGA_9x14_wide = new IBMFont("fonts/PC/IBM/Px437_IBM_EGA9-2x.ttf", 16, copyright);

	/**
	 * IBM VGA (& MCGA) 8x16 font, 1:1 pixel ratio (80 columns).
	 */
	public static final IBMFont VGA_8x16 = new IBMFont("fonts/PC/IBM/Px437_IBM_VGA8.ttf", 16, copyright);
	/**
	 * IBM VGA (& MCGA) 8x16 font, 2:1 pixel ratio (40 columns).
	 */
	public static final IBMFont VGA_8x16_wide = new IBMFont("fonts/PC/IBM/Px437_IBM_VGA8-2x.ttf", 16, copyright);
	/**
	 * IBM VGA (& MCGA) 9x16 font, 1:1 pixel ratio (80 columns).
	 */
	public static final IBMFont VGA_9x16 = new IBMFont("fonts/PC/IBM/Px437_IBM_VGA9.ttf", 16, copyright);
	/**
	 * IBM VGA (& MCGA) 9x16 font, 2:1 pixel ratio (40 columns).
	 */
	public static final IBMFont VGA_9x16_wide = new IBMFont("fonts/PC/IBM/Px437_IBM_VGA9-2x.ttf", 16, copyright);

	/**
	 * Default IBM font (VGA 9x16).
	 * http://int10h.org/oldschool-pc-fonts/
	 * The 9x fonts imitate the PC behavior (in those modes) of displaying an 8x character with a blank column to the right for spacing.
	 * Those fonts also repeat the 8th column in the 9th column, as the PC would do for characters 0xC0-0xDF (NOT 0xB0-0xDF, as some sources claim).
	 */
	public static final IBMFont DEFAULT_FONT = VGA_9x16;

	/**
	 * Create a new IBM font.
	 * @param file Path to the font file in the .jar file.
	 * @param pointSize Font base point size (where the font renders 1px as 1px).
	 * @param about Copyright string for font.
	 */
	public IBMFont(String file, int pointSize, String about) {
		super(registerFont(file), pointSize, about);
	}
}
