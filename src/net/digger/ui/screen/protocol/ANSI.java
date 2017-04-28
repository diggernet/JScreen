package net.digger.ui.screen.protocol;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.EnumUtils;

import net.digger.ui.screen.JScreen;
import net.digger.ui.screen.color.Attr;
import net.digger.ui.screen.color.CGAColor;

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
 * Extends PlainText protocol to implement ANSI escape sequences.
 * https://en.wikipedia.org/wiki/ANSI_escape_code
 * @author walton
 */
public class ANSI extends PlainText {
	// The escape character
	private static final char ESCAPE = 27;
	// Implemented ANSI command letters
	// not in ANSI.SYS:  E, F, G, S, T
	// not implemented:  i, n, l, h
	private enum Command { A, B, C, D, f, H, J, K, m, s, u }

	private String buffer = "";			// Potential ANSI escape sequence
	private String param = "";			// Potential ANSI parameter
	private List<Integer> params = new ArrayList<>();	// ANSI parameter list
	private boolean inEscape = false;	// Are we in an escape sequence?
	private boolean inControl = false;	// Are we in a control (ESC-[) sequence?
	private Point cursor = null;		// Stored cursor position

	/**
	 * Create instance of the ANSI protocol handler.
	 * @param screen JScreen for text display.
	 */
	public ANSI(JScreen screen) {
		super(screen);
	}
	
	/**
	 * Print a character to screen, processing it for ANSI escape sequences.
	 * @param ch Character to inspect and display.
	 */
	@Override
	public void print(char ch) {
		buffer += ch;
		
		// if we are not in an escape sequence...
		if (!inEscape) {
			if (ch == ESCAPE) {
				// start an escape sequence
				inEscape = true;
			} else {
				// print the buffer and reset
				printAndReset();
			}
			return;
		}
		// we are in an escape sequence.
		
		// if we get another escape...
		if (ch == ESCAPE) {
			// this is a bogus sequence, so print it all
			printAndReset();
			return;
		}
		
		// if we are not in a control sequence...
		if (!inControl) {
			if (ch == '[') {
				// start a control sequence
				inControl = true;
			} else {
				// non-control sequences are not implemented
				printAndReset();
			}
			return;
		}
		// we are in a control sequence.
		
		// if we get a number...
		if ((ch >= '0') && (ch <= '9')) {
			// start assembling a parameter
			param += ch;
			return;
		}
		
		// if we get a separator...
		if (ch == ';') {
			if (param.length() < 1) {
				// this parameter will be defaulted
				params.add(null);
			} else {
				// add parameter to the list
				params.add(Integer.parseInt(param));
				param = "";
			}
			return;
		}
		
		// if we get a command which is invalid or not implemented...
		Command cmd = EnumUtils.getEnum(Command.class, String.valueOf(ch));
		if (cmd == null) {
			printAndReset();
			return;
		}
		// we have a valid, implemented command.
		
		// catch that last parameter
		if (param.length() > 0) {
			params.add(Integer.parseInt(param));
			param = "";
		}
		
		doCommand(cmd);
		reset();
	}
	
	/**
	 * Returns the given parameter (1-based), or the given default value if not present.
	 * @param param
	 * @param defaultValue
	 * @return
	 */
	private int getParam(int param, int defaultValue) {
		if (params.isEmpty() || (params.size() < param)) {
			return defaultValue;
		}
		Integer value = params.get(param - 1);
		return (value == null) ? defaultValue : value;
	}
	
	/**
	 * Performs the given ANSI command.
	 * @param cmd
	 */
	private void doCommand(Command cmd) {
		Point coord;
		Integer param;
		Rectangle window;
		
		switch (cmd) {
			case A:		// CUU - CUrsor Up
				coord = screen.getCursor();
				coord.y = Math.max(0, coord.y - getParam(1, 1));
				screen.setCursor(coord);
				break;
			case B:		// CUD - CUrsor Down
				coord = screen.getCursor();
				window = screen.getWindow();
				coord.y = Math.min(window.height - 1, coord.y + getParam(1, 1));
				screen.setCursor(coord);
				break;
			case C:		// CUF - CUrsor Forward
				coord = screen.getCursor();
				window = screen.getWindow();
				coord.x = Math.min(window.width - 1, coord.x + getParam(1, 1));
				screen.setCursor(coord);
				break;
			case D:		// CUB - CUrsor Back
				coord = screen.getCursor();
				coord.x = Math.max(0, coord.x - getParam(1, 1));
				screen.setCursor(coord);
				break;
			case H:		// CUP - CUrsor Position
			case f:		// HVP - Horizontal and Vertical Position
				screen.setCursor(getParam(2, 1) - 1, getParam(1, 1) - 1);
				break;
			case J:		// ED - Erase in Display
				param = getParam(1, 0);
				switch (param) {
					case 0:		// Clear from cursor to end of screen
						screen.clearToBottom();
						break;
					case 1:		// Clear from cursor to beginning of screen
						screen.clearToTop();
						break;
					case 2:		// Clear screen and go to upper left corner
					case 3:		// Clear screen, go to upper left corner, and clear scrollback buffer
						screen.clearWindow();
						break;
					default:
						// ignore command for illegal parameter
						break;
				}
				break;
			case K:		// EL - Erase in Line
				param = getParam(1, 0);
				switch (param) {
					case 0:		// Clear from cursor to end of line
						screen.clearToEOL();
						break;
					case 1:		// Clear from cursor to beginning of line
						screen.clearToBOL();
						break;
					case 2:		// Clear entire line
						screen.clearLine();
						break;
					default:
						// ignore command for illegal parameter
						break;
				}
				break;
			case m:		// SGR - Select Graphic Rendition
				if (params.isEmpty()) {
					params.add(0);
				}
				for (int i=1; i<=params.size(); i++) {
					param = getParam(i, 0);
					switch (param) {
						case 0:		// Reset / Normal [all attributes off]
							screen.setTextColors(CGAColor.DEFAULT_FG, CGAColor.DEFAULT_BG);
							break;
						case 1:		// Bold or increased intensity
							screen.setTextAttr(Attr.BOLD, true);
							break;
//						case 2:		// Faint (decreased intensity) [Not widely supported.]
//						case 3:		// Italic: on [Not widely supported. Sometimes treated as inverse.]
//						case 4:		// Underline: Single
						case 5:		// Blink: Slow [less than 150 per minute]
							screen.setTextAttr(Attr.BLINKING, true);
							break;
//						case 6:		// Blink: Rapid [MS-DOS ANSI.SYS; 150+ per minute; not widely supported]
						case 7:		// Image: Negative [inverse or reverse; swap foreground and background]
							screen.setTextAttr(Attr.REVERSE, true);
							break;
//						case 8:		// Conceal [Not widely supported.]
//						case 9:		// Crossed-out [ legible, but marked for deletion. Not widely supported.]
//						case 10:	// Primary(default) font
//						case 11: case 12: case 13: case 14: case 15: case 16: case 17: case 18: case 19:
//							// n-th alternate font [14 being the fourth alternate font, up to 19 being the 9th alternate font]
//						case 20:	// Fraktur [hardly ever supported]
//						case 21:	// Bold: off or Underline: Double [Bold off not widely supported; double underline hardly ever supported.]
						case 22:	// Normal color or intensity [Neither bold nor faint]
							screen.setTextAttr(Attr.BOLD, false);
							break;
//						case 23:	// Not italic, not Fraktur
//						case 24:	// Underline: None [Not singly or doubly underlined]
						case 25:	// Blink: off
							screen.setTextAttr(Attr.BLINKING, false);
							break;
//						case 26:	// Reserved
						case 27:	// Image: Positive
							screen.setTextAttr(Attr.REVERSE, false);
							break;
//						case 28:	// Reveal [conceal off]
//						case 29:	// Not crossed out
						// Color table
						// Intensity	0		1		2		3		4		5			6		7
						// Normal		Black	Red		Green	Brown	Blue	Magenta		Cyan	LtGrey
						// Bright		DkGrey	LtRed	LtGreen	Yellow	LtBlue	LtMagenta	LtCyan	White
						case 30:	// Set text color (foreground) [30 + n, where n is from the color table]
							screen.setFGColor(CGAColor.BLACK);
							break;
						case 31:	// Set text color (foreground) [30 + n, where n is from the color table]
							screen.setFGColor(CGAColor.RED);
							break;
						case 32:	// Set text color (foreground) [30 + n, where n is from the color table]
							screen.setFGColor(CGAColor.GREEN);
							break;
						case 33:	// Set text color (foreground) [30 + n, where n is from the color table]
							screen.setFGColor(CGAColor.BROWN);
							break;
						case 34:	// Set text color (foreground) [30 + n, where n is from the color table]
							screen.setFGColor(CGAColor.BLUE);
							break;
						case 35:	// Set text color (foreground) [30 + n, where n is from the color table]
							screen.setFGColor(CGAColor.MAGENTA);
							break;
						case 36:	// Set text color (foreground) [30 + n, where n is from the color table]
							screen.setFGColor(CGAColor.CYAN);
							break;
						case 37:	// Set text color (foreground) [30 + n, where n is from the color table]
							screen.setFGColor(CGAColor.LIGHT_GREY);
							break;
//						case 38:	// Reserved for extended set foreground color
//							// [typical supported next arguments are 5;n where n is color index (0..255) or 2;r;g;b where r,g,b are red, green and blue color channels (out of 255)]
						case 39:	// Default text color (foreground) [implementation defined (according to standard)]
							screen.setFGColor(CGAColor.DEFAULT_FG);
							break;
						case 40:	// Set background color [40 + n, where n is from the color table]
							screen.setBGColor(CGAColor.BLACK);
							break;
						case 41:	// Set background color [40 + n, where n is from the color table]
							screen.setBGColor(CGAColor.RED);
							break;
						case 42:	// Set background color [40 + n, where n is from the color table]
							screen.setBGColor(CGAColor.GREEN);
							break;
						case 43:	// Set background color [40 + n, where n is from the color table]
							screen.setBGColor(CGAColor.BROWN);
							break;
						case 44:	// Set background color [40 + n, where n is from the color table]
							screen.setBGColor(CGAColor.BLUE);
							break;
						case 45:	// Set background color [40 + n, where n is from the color table]
							screen.setBGColor(CGAColor.MAGENTA);
							break;
						case 46:	// Set background color [40 + n, where n is from the color table]
							screen.setBGColor(CGAColor.CYAN);
							break;
						case 47:	// Set background color [40 + n, where n is from the color table]
							screen.setBGColor(CGAColor.LIGHT_GREY);
							break;
//						case 48:	// Reserved for extended set background color
//							// [typical supported next arguments are 5;n where n is color index (0..255) or 2;r;g;b where r,g,b are red, green and blue color channels (out of 255)]
						case 49:	// Default background color [implementation defined (according to standard)]
							screen.setBGColor(CGAColor.DEFAULT_BG);
							break;
//						case 50:	// Reserved
//						case 51:	// Framed
//						case 52:	// Encircled
//						case 53:	// Overlined
//						case 54:	// Not framed or encircled
//						case 55:	// Not overlined
//						case 56: case 57: case 58: case 59:	// Reserved
//						case 60:	// ideogram underline or right side line [hardly ever supported]
//						case 61:	// ideogram double underline or double line on the right side [hardly ever supported]
//						case 62:	// ideogram overline or left side line [hardly ever supported]
//						case 63:	// ideogram double overline or double line on the left side [hardly ever supported]
//						case 64:	// ideogram stress marking [hardly ever supported]
//						case 65:	// ideogram attributes off [hardly ever supported, reset the effects of all of 60–64]
//						case 90: case 91: case 92: case 93: case 94: case 95: case 96: case 97:
//							// Set foreground text color, high intensity [aixterm (not in standard)]
//						case 100: case 101: case 102: case 103: case 104: case 105: case 106: case 107:
//							// Set background color, high intensity [aixterm (not in standard)]
					}
				}
				break;
			case s:		// SCP - Save Cursor Position
				cursor = screen.getCursor();
				break;
			case u:		// RCP - Restore Cursor Position
				if (cursor != null) {
					screen.setCursor(cursor);
				}
				break;
		}
	}
	
	/**
	 * Resets the escape sequence state.
	 */
	private void reset() {
		inEscape = false;
		inControl = false;
		buffer = "";
		param = "";
		params.clear();
	}
	
	/**
	 * Prints buffer and resets the escape sequence state.
	 */
	private void printAndReset() {
		for (int i=0; i<buffer.length(); i++) {
			super.print(buffer.charAt(i));
		}
		reset();
	}
}
