package net.digger.ui.screen.protocol;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import net.digger.ui.screen.JScreen;
import net.digger.ui.screen.color.Attr;
import net.digger.ui.screen.color.CGAColor;
import net.digger.util.VTParser;

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
 * http://www.inwap.com/pdp10/ansicode.txt
 * http://vt100.net/docs/
 * http://vt100.net/emu/dec_ansi_parser
 * @author walton
 */
public class ANSI extends PlainText {
	// The escape character
	private static final char ESCAPE = 27;
	// Implemented ANSI escape sequence letters
	private enum EscapeSequence { D, E, M };
	private static final String CSI = ESCAPE + "[";
	// Implemented ANSI control sequence letters
	private enum ControlSequence { A, B, C, D, f, H, J, K, m, n, r, s, u }
	// not in ANSI.SYS:  E, F, G, S, T
	// not implemented:  i, l, h

	private VTParser parser;
	private Consumer<String> dsrCallback = null;	// Optional callback for DSR (ESC[6n) support.
	private Point cursor = null;		// Stored cursor position
	private int topMargin = 0;			// Top of scrolling region
	private int bottomMargin = 0;		// Bottom of scrolling region

	/**
	 * Create instance of the ANSI protocol handler.
	 * @param screen JScreen for text display.
	 */
	public ANSI(JScreen screen) {
		this(screen, null);
	}
	
	/**
	 * Create instance of the ANSI protocol handler, with DSR callback.
	 * If DSR callback is provided, and Device Status Report (ESC[#n) is received,
	 * the callback will be called with CPR ("ESC[<row>;<col>R") or DSR ("ESC[0n"),
	 * so that the application can send that response.
	 * @param screen JScreen for text display.
	 * @param dsrCallback.
	 */
	public ANSI(JScreen screen, Consumer<String> dsrCallback) {
		super(screen);
		this.dsrCallback = dsrCallback;
		parser = new VTParser((action, ch, intermediateChars, params) -> {
			switch (action) {
				case CSI_DISPATCH:
					doCSI(ch, intermediateChars, params);
					break;
				case ESC_DISPATCH:
					doEscape(ch, intermediateChars, params);
					break;
				case EXECUTE:
					doExecute(ch);
					break;
				case PRINT:
					doPrint(ch);
					break;
				case DCS_HOOK:
				case DCS_PUT:
				case DCS_UNHOOK:
				case OSC_END:
				case OSC_PUT:
				case OSC_START:
				case ERROR:
				default:
					System.out.printf("ANSI: Unimplemented Parser Action: %s", action);
					if (ch != 0) {
						System.out.printf(", Char: 0x%02x ('%c')\n", (int)ch, ch);
					}
					break;
			}
		});
	}
	
	/**
	 * Print a character to screen, processing it for ANSI escape sequences.
	 * @param ch Character to inspect and display.
	 */
	@Override
	public void print(char ch) {
		parser.parse(ch);
	}
	
	private void doCSI(char ch, List<Character> intermediateChars, List<Integer> params) {
		ControlSequence ctrlseq = EnumUtils.getEnum(ControlSequence.class, String.valueOf(ch));
		if ((ctrlseq == null) || (intermediateChars.size() > 0)) {
			System.out.println("ANSI: Unimplemented Control Sequence: Esc[" + StringUtils.join(intermediateChars, null) + StringUtils.join(params, ';') + ch);
			return;
		}
		switch (ctrlseq) {
			case A:		// CUU - CUrsor Up
				doCUU(intermediateChars, params);
				break;
			case B:		// CUD - CUrsor Down
				doCUD(intermediateChars, params);
				break;
			case C:		// CUF - CUrsor Forward
				doCUF(intermediateChars, params);
				break;
			case D:		// CUB - CUrsor Back
				doCUB(intermediateChars, params);
				break;
			case f:		// HVP - Horizontal and Vertical Position
			case H:		// CUP - CUrsor Position
				doHVPCUP(intermediateChars, params);
				break;
			case J:		// ED - Erase in Display
				doED(intermediateChars, params);
				break;
			case K:		// EL - Erase in Line
				doEL(intermediateChars, params);
				break;
			case m:		// SGR - Select Graphic Rendition
				doSGR(intermediateChars, params);
				break;
			case n:		// DSR - Device Status Report
				doDSR(intermediateChars, params);
				break;
			case r:		// DECSTBM - DEC Set Top and Bottom Margins (scrolling region)
				doDECSTBM(intermediateChars, params);
				break;
			case s:		// SCP - Save Cursor Position
				doSCP(intermediateChars, params);
				break;
			case u:		// RCP - Restore Cursor Position
				doRCP(intermediateChars, params);
				break;
		}
	}

	private void doCUU(List<Character> intermediateChars, List<Integer> params) {
		Point coord = screen.getCursor();
		// move cursor, stopping at top margin
		coord.y = Math.max(0, coord.y - nextParam(params, 1));
		if (topMargin > 0) {
			coord.y = Math.max(coord.y, topMargin - 1);
		}
		screen.setCursor(coord);
	}
	
	private void doCUD(List<Character> intermediateChars, List<Integer> params) {
		Point coord = screen.getCursor();
		Rectangle window = screen.getWindow();
		// move cursor, stopping at bottom margin
		coord.y = Math.min(window.height - 1, coord.y + nextParam(params, 1));
		if (bottomMargin > 0) {
			coord.y = Math.min(coord.y, bottomMargin - 1);
		}
		screen.setCursor(coord);
	}
	
	private void doCUF(List<Character> intermediateChars, List<Integer> params) {
		Point coord = screen.getCursor();
		Rectangle window = screen.getWindow();
		// move cursor, stopping at right margin
		coord.x = Math.min(window.width - 1, coord.x + nextParam(params, 1));
		screen.setCursor(coord);
	}
	
	private void doCUB(List<Character> intermediateChars, List<Integer> params) {
		Point coord = screen.getCursor();
		// move cursor, stopping at left margin
		coord.x = Math.max(0, coord.x - nextParam(params, 1));
		screen.setCursor(coord);
	}
	
	private void doHVPCUP(List<Character> intermediateChars, List<Integer> params) {
		Rectangle window;
		int y = nextParam(params, 1);
		int x = nextParam(params, 1);
		window = screen.getWindow();
		// move cursor, stopping at edge of screen
		x = Math.max(1, Math.min(window.width, x));
		y = Math.max(1, Math.min(window.height, y));
		screen.setCursor(x - 1, y - 1);
	}
	
	private void doED(List<Character> intermediateChars, List<Integer> params) {
		int param = nextParam(params, 0);
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
				System.out.printf("ANSI: Unimplemented ED parameter: %d\n", param);
				break;
		}
	}

	private void doEL(List<Character> intermediateChars, List<Integer> params) {
		int param = nextParam(params, 0);
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
				System.out.printf("ANSI: Unimplemented EL parameter: %d\n", param);
				break;
		}
	}

	private void doSGR(List<Character> intermediateChars, List<Integer> params) {
		if (params.isEmpty()) {
			// make sure we have at least one (default) param
			params.add(null);
		}
		while (!params.isEmpty()) {
			int param = nextParam(params, 0);
			switch (param) {
				case 0:		// Reset / Normal [all attributes off]
					screen.setTextColors(CGAColor.DEFAULT_FG, CGAColor.DEFAULT_BG);
					break;
				case 1:		// Bold or increased intensity
					screen.setTextAttr(Attr.BOLD, true);
					break;
//				case 2:		// Faint (decreased intensity) [Not widely supported.]
//				case 3:		// Italic: on [Not widely supported. Sometimes treated as inverse.]
//				case 4:		// Underline: Single
				case 5:		// Blink: Slow [less than 150 per minute]
					screen.setTextAttr(Attr.BLINKING, true);
					break;
//				case 6:		// Blink: Rapid [MS-DOS ANSI.SYS; 150+ per minute; not widely supported]
				case 7:		// Image: Negative [inverse or reverse; swap foreground and background]
					screen.setTextAttr(Attr.REVERSE, true);
					break;
//				case 8:		// Conceal [Not widely supported.]
//				case 9:		// Crossed-out [ legible, but marked for deletion. Not widely supported.]
//				case 10:	// Primary(default) font
//				case 11: case 12: case 13: case 14: case 15: case 16: case 17: case 18: case 19:
//					// n-th alternate font [14 being the fourth alternate font, up to 19 being the 9th alternate font]
//				case 20:	// Fraktur [hardly ever supported]
//				case 21:	// Bold: off or Underline: Double [Bold off not widely supported; double underline hardly ever supported.]
				case 22:	// Normal color or intensity [Neither bold nor faint]
					screen.setTextAttr(Attr.BOLD, false);
					break;
//				case 23:	// Not italic, not Fraktur
//				case 24:	// Underline: None [Not singly or doubly underlined]
				case 25:	// Blink: off
					screen.setTextAttr(Attr.BLINKING, false);
					break;
//				case 26:	// Reserved
				case 27:	// Image: Positive
					screen.setTextAttr(Attr.REVERSE, false);
					break;
//				case 28:	// Reveal [conceal off]
//				case 29:	// Not crossed out
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
//				case 38:	// Reserved for extended set foreground color
//					// [typical supported next arguments are 5;n where n is color index (0..255) or 2;r;g;b where r,g,b are red, green and blue color channels (out of 255)]
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
//				case 48:	// Reserved for extended set background color
//					// [typical supported next arguments are 5;n where n is color index (0..255) or 2;r;g;b where r,g,b are red, green and blue color channels (out of 255)]
				case 49:	// Default background color [implementation defined (according to standard)]
					screen.setBGColor(CGAColor.DEFAULT_BG);
					break;
//				case 50:	// Reserved
//				case 51:	// Framed
//				case 52:	// Encircled
//				case 53:	// Overlined
//				case 54:	// Not framed or encircled
//				case 55:	// Not overlined
//				case 56: case 57: case 58: case 59:	// Reserved
//				case 60:	// ideogram underline or right side line [hardly ever supported]
//				case 61:	// ideogram double underline or double line on the right side [hardly ever supported]
//				case 62:	// ideogram overline or left side line [hardly ever supported]
//				case 63:	// ideogram double overline or double line on the left side [hardly ever supported]
//				case 64:	// ideogram stress marking [hardly ever supported]
//				case 65:	// ideogram attributes off [hardly ever supported, reset the effects of all of 60–64]
//				case 90: case 91: case 92: case 93: case 94: case 95: case 96: case 97:
//					// Set foreground text color, high intensity [aixterm (not in standard)]
//				case 100: case 101: case 102: case 103: case 104: case 105: case 106: case 107:
//					// Set background color, high intensity [aixterm (not in standard)]
				default:
					System.out.printf("ANSI: Unimplemented SGR parameter: %d\n", param);
					break;
			}
		}
	}
	
	private void doDSR(List<Character> intermediateChars, List<Integer> params) {
		String response;
		int param = nextParam(params, 0);
		switch (param) {
			case 5:		// Send DSR (0=ready, 3=malfunction)
				if (dsrCallback != null) {
					response = CSI + "0n";
					dsrCallback.accept(response);
				}
				break;
			case 6:		// Send CPR - Cursor Position Report
				if (dsrCallback != null) {
					Point cursor = screen.getCursor();
					response = CSI + cursor.y + ';' + cursor.x + 'R';
					dsrCallback.accept(response);
				}
				break;
			default:
				System.out.printf("ANSI: Unimplemented DSR parameter: %d\n", param);
				break;
		}
	}

	private void doDECSTBM(List<Character> intermediateChars, List<Integer> params) {
		Rectangle window = screen.getWindow();
		int top = nextParam(params, 1);
		int bottom = nextParam(params, window.height);
		if ((top == 1) && (bottom == window.height)) {
			topMargin = 0;
			bottomMargin = 0;
//System.out.printf("Top: %d, Bottom: %d\n", topMargin, bottomMargin);
		} else if ((top > 0) && (bottom > 0) && (bottom > top)) {
			topMargin = top;
			bottomMargin = bottom;
//System.out.printf("Top: %d, Bottom: %d\n", topMargin, bottomMargin);
		}
	}
	
	private void doSCP(List<Character> intermediateChars, List<Integer> params) {
		cursor = screen.getCursor();
	}
	
	private void doRCP(List<Character> intermediateChars, List<Integer> params) {
		if (cursor != null) {
			screen.setCursor(cursor);
		}
	}
	
	private void doEscape(char ch, List<Character> intermediateChars, List<Integer> params) {
		EscapeSequence escseq = EnumUtils.getEnum(EscapeSequence.class, String.valueOf(ch));
		if ((escseq == null) || (intermediateChars.size() > 0)) {
			System.out.println("ANSI: Unimplemented Escape Sequence: Esc" + StringUtils.join(intermediateChars, null) + ch);
			return;
		}
		switch (escseq) {
			case D:		// IND - Index
				doIND(intermediateChars, params);
				break;
			case E:		// NEL - NExt Line
				doNEL(intermediateChars, params);
				break;
			case M:		// RI - Reverse Index
				doRI(intermediateChars, params);
				break;
		}
	}
	
	private void doIND(List<Character> intermediateChars, List<Integer> params) {
		insideMargin(() -> {
			Point coord = screen.getCursor();
			Rectangle window = screen.getWindow();
			if (coord.y < (window.height - 1)) {
				screen.setCursor(coord.x, coord.y + 1);
			} else {
				screen.scrollWindowUp();
			}
		});
	}

	private void doNEL(List<Character> intermediateChars, List<Integer> params) {
		insideMargin(() -> {
			super.print('\r');
			super.print('\n');
		});
	}

	private void doRI(List<Character> intermediateChars, List<Integer> params) {
		insideMargin(() -> {
			Point coord = screen.getCursor();
			if (coord.y > 0) {
				screen.setCursor(coord.x, coord.y - 1);
			} else {
				screen.scrollWindowDown();
			}
		});
	}

	private void doExecute(char ch) {
		// Unless we have some reason to specially handle a control character here,
		// just pass it through to the base class.
		super.print(ch);
	}

	private void doPrint(char ch) {
		insideMargin(() -> {
			super.print(ch);
		});
	}


	/**
	 * Removes the next parameter from the given list and returns it.
	 * If the list is null or empty, or the parameter is null or 0, returns the given default value.
	 * @param params
	 * @param defaultValue
	 * @return
	 */
	private int nextParam(List<Integer> params, int defaultValue) {
		if (params == null) {
			return defaultValue;
		}
		if (params.isEmpty()) {
			return defaultValue;
		}
		Integer param = params.remove(0);
		if ((param == null) || (param == 0)) {
			return defaultValue;
		}
		return param;
	}


	private void insideMargin(Runnable callback) {
		int dy = 0;
		int dh = 0;
		// if scrolling region has been set, adjust current window
		if ((topMargin > 0) && (bottomMargin > 0)) {
			Point cursor = screen.getCursor();
			Rectangle window = screen.getWindow();
			int bottom = Math.min(bottomMargin, window.height);
			// only adjust if cursor is inside scrolling region
			if (((cursor.y + 1) >= topMargin) && ((cursor.y + 1) <= bottom)) {
				dy = topMargin - 1;
				int height = (bottom - topMargin) + 1;
				dh = height - window.height;
				if ((dy != 0) || (dh != 0)) {
//System.out.printf("DY: %d, DH: %d\n", dy, dh);
					screen.adjustWindow(0, dy, 0, dh);
				}
			}
		}
		// call the callback
		callback.run();
		// if current window was adjusted, undo adjustment
		if ((dy != 0) || (dh != 0)) {
			screen.adjustWindow(0, -dy, 0, -dh);
		}
	}
}
