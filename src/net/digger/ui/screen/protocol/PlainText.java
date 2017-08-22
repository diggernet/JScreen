package net.digger.ui.screen.protocol;

import net.digger.ui.screen.JScreen;

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
 * Extends JScreenTextProtocol to handle common control characters (CR, LF, BS, etc).
 * @author walton
 */
public class PlainText extends JScreenTextProtocol {
	/**
	 * Create instance of this protocol handler.
	 * @param screen JScreen for text display.
	 */
	public PlainText(JScreen screen) {
		super(screen);
	}

	/**
	 * Print a character to screen, processing it for certain control characters.
	 * @param ch Character to inspect and display.
	 */
	@Override
	public void print(char ch) {
		switch (ch) {
			case 0x00:		// ^@	NUL
				break;
			case 0x07:		// ^G	BEL
				screen.sound.beep();
				break;
			case 0x08:		// ^H	BS
				screen.backspace();
				break;
			//TODO: ^I	HT?
			case 0x0a:	// ^J	LF
				screen.lineFeed();
				break;
			case 0x0c:	// ^L	FF
				screen.clearWindow();
				break;
			case 0x0d:	// ^M	CR
				screen.carriageReturn();
				break;
			default:
				if (ch < 32) {
					System.out.println("PlainText: Unimplemented Control Character: 0x" + Integer.toHexString(ch));
				}
				super.print(ch);
				break;
		}
	}
}
