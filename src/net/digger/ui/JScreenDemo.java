package net.digger.ui;

import java.awt.Rectangle;
import java.io.IOException;

import net.digger.ui.screen.JScreen;
import net.digger.ui.screen.JScreenWindowState;
import net.digger.ui.screen.color.Attr;
import net.digger.ui.screen.color.CGAColor;
import net.digger.ui.screen.font.PCFont;
import net.digger.ui.screen.mode.PCScreenMode;
import net.digger.ui.screen.protocol.ANSI;
import net.digger.util.Delay;

/**
 * Copyright © 2017  David Walton
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * A demo of some of the capabilities of JScreen.
 * @author walton
 */
public class JScreenDemo {
	private JScreen screen;
	private static final Rectangle textWin = new Rectangle(10, 3, 60, 5);
	private static final Rectangle demoWin = new Rectangle(20, 10, 40, 13);
	private static final int BPS = 300;
	private JScreenWindowState textWinSave;
	private JScreenWindowState demoWinSave;

	public static void main(String[] args) throws IOException {
		new JScreenDemo().run();
	}

	public JScreenDemo() {
		screen = JScreen.createJScreenWindow("JScreenDemo", PCScreenMode.VGA_80x25);
		screen.setTextProtocol(new ANSI(screen));
		screen.keyboard.clearKeyBuffer();
		screen.keyboard.enableKeyBuffer(true);
	}
	
	public void run() {
		screen.clearScreen();
		screen.setCursor(0, 1);
		screen.putStrCentered("JScreen");
		screen.setWindow(textWin);
		textWinSave = screen.saveWindowState();
		screen.setWindow(demoWin);
		demoWinSave = screen.saveWindowState();
		
		writeTextWin(
			"JScreen is a Java Swing component which provides a text",
			"screen display.  It was conceived to enable porting old",
			"DOS programs to a modern cross-platform environment, while",
			"maintaining the original look and feel."
		);
		Delay.second(2);

		writeTextWin(
			"JScreen can create its own window, as we have done here.",
			"Or it can be embedded as a component in your UI, using",
			"whatever screen dimensions you wish."
		);
		Delay.second(2);

		writeTextWin(
			"With JScreen, you can create output windows in the screen,",
			"such as the one this text is in.  You can also frame those",
			"windows."
		);
		Delay.second(1);

		inDemoWin(() -> {
			screen.frameWindow(null, PCFont.DOUBLE_FRAME);
		});
		Delay.second(2);

		writeTextWin(
			"And put a title on the frame."
		);
		Delay.second(1);

		inDemoWin(() -> {
			screen.unframeWindow();
			screen.frameWindow(" This is a title ", PCFont.DOUBLE_FRAME);
		});
		Delay.second(2);

		writeTextWin(
			"JScreen comes with fonts and character mapping to imitate",
			"a PC screen, as you see in this demo.  But it was also",
			"designed to enable emulating non-PC text modes, given",
			"suitable fonts and character mapping."
		);
		Delay.second(1);

		inDemoWin(() -> {
			for (int i=0; i<8; i++) {
				for (int j=0; j<32; j++) {
					screen.putChar((char)(i*32+j));
				}
				screen.println();
			}
			screen.println();
		});
		Delay.second(2);

		writeTextWin(
			"Naturally, you can also use colors..."
		);
		Delay.second(1);

		inDemoWin(() -> {
			screen.setTextColors(CGAColor.BLUE, CGAColor.BLACK);
			screen.print("Blue ");
			screen.setTextColors(CGAColor.GREEN, CGAColor.BLACK);
			screen.print("Green ");
			screen.setTextColors(CGAColor.CYAN, CGAColor.BLACK);
			screen.print("Cyan ");
			screen.setTextColors(CGAColor.RED, CGAColor.BLACK);
			screen.print("Red ");
			screen.setTextColors(CGAColor.MAGENTA, CGAColor.BLACK);
			screen.print("Magenta ");
			screen.setTextColors(CGAColor.BROWN, CGAColor.BLACK);
			screen.print("Brown ");
			screen.println();
			screen.setTextColors(CGAColor.LIGHT_GREY, CGAColor.BLACK);
			screen.print("Light Grey ");
			screen.setTextColors(CGAColor.BLACK, CGAColor.BLACK, Attr.BOLD);
			screen.print("Dark Grey ");
			screen.setTextColors(CGAColor.BLUE, CGAColor.BLACK, Attr.BOLD);
			screen.print("Light Blue ");
			screen.println();
			screen.setTextColors(CGAColor.GREEN, CGAColor.BLACK, Attr.BOLD);
			screen.print("Light Green ");
			screen.setTextColors(CGAColor.CYAN, CGAColor.BLACK, Attr.BOLD);
			screen.print("Light Cyan ");
			screen.setTextColors(CGAColor.RED, CGAColor.BLACK, Attr.BOLD);
			screen.print("Light Red ");
			screen.println();
			screen.setTextColors(CGAColor.MAGENTA, CGAColor.BLACK, Attr.BOLD);
			screen.print("Light Magenta ");
			screen.setTextColors(CGAColor.BROWN, CGAColor.BLACK, Attr.BOLD);
			screen.print("Yellow ");
			screen.setTextColors(CGAColor.LIGHT_GREY, CGAColor.BLACK, Attr.BOLD);
			screen.print("White ");
			screen.println();
			screen.setTextColors(CGAColor.GREEN, CGAColor.BLUE, Attr.BOLD);
			screen.print("Yes, ");
			screen.setTextColors(CGAColor.GREEN, CGAColor.GREEN, Attr.BOLD);
			screen.print("you ");
			screen.setTextColors(CGAColor.GREEN, CGAColor.CYAN, Attr.BOLD);
			screen.print("get ");
			screen.setTextColors(CGAColor.GREEN, CGAColor.RED, Attr.BOLD);
			screen.print("back");
			screen.setTextColors(CGAColor.GREEN, CGAColor.MAGENTA, Attr.BOLD);
			screen.print("ground ");
			screen.setTextColors(CGAColor.GREEN, CGAColor.BROWN, Attr.BOLD);
			screen.print("colors ");
			screen.setTextColors(CGAColor.GREEN, CGAColor.LIGHT_GREY, Attr.BOLD);
			screen.print("too.");
			screen.resetTextColors();
			screen.println();
			screen.println();
		});
		Delay.second(2);

		String ansi = "\u001b[1;30mA\u001b[31mN\u001b[32mS\u001b[33mI\u001b[34m!\u001b[35m!\u001b[36m!\u001b[37m!";
		writeTextWin(
			"You can optionally turn on support for ANSI escape codes,",
			"to interpret strings like this one, as you can see below:"
		);
		screen.print("    ");
		screen.putStr(ansi);
		screen.println();
		textWinSave = screen.saveWindowState();
		Delay.second(1);

		inDemoWin(() -> {
			screen.resetTextColors();
			screen.println(ansi);
		});
		Delay.second(2);

		writeTextWin(
			"And, of course..."
		);
		screen.setTextAttr(Attr.BLINKING, true);
		screen.print("        BLINK");
		screen.setTextAttr(Attr.BLINKING, false);
		screen.println();
		Delay.second(2);

		writeTextWin(
			"JScreen implements blocking keyboard input, so you can",
			"wait for a key press..."
		);
		screen.print("<PRESS ANY KEY>");
		textWinSave = screen.saveWindowState();
		try {
			screen.keyboard.awaitKeyEvent();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writeTextWin(
			"",
			"Well Done!",
			"It also supports line input."
		);
		screen.printBPS(BPS, "Please enter your name: ");
		String name = "";
		try {
			name = screen.keyboard.readLine(20);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		textWinSave = screen.saveWindowState();
		writeTextWin(
			"Thank you, " + name + "."
		);
		Delay.second(2);
		
		writeTextWin(
			"JScreen allows you to fill the screen, a window, or any",
			"arbitrary region."
		);
		Delay.second(1);

		inDemoWin(() -> {
			screen.fillWindow('*', CGAColor.RED, CGAColor.BLUE);
			Delay.second(1);
			screen.fillRegion(new Rectangle(15, 5, 20, 5), '#', CGAColor.GREEN, CGAColor.BLACK);
		});
		Delay.second(2);

		writeTextWin(
			"And also scroll the screen, a window, or any",
			"arbitrary region."
		);
		Delay.second(1);

		inDemoWin(() -> {
			int delay = 250;
			Rectangle region = new Rectangle(5, 1, 20, 5);
			Delay.milli(delay);
			screen.scrollRegionUp(region);
			Delay.milli(delay);
			screen.scrollRegionUp(region);
			Delay.milli(delay);
			screen.scrollRegionUp(region);
			Delay.milli(delay);
			screen.scrollRegionUp(region);
			Delay.milli(delay);
		});
		Delay.second(2);

		writeTextWin(
			"But how fast is JScreen, you ask?",
			"Let's fill that window with random characters and colors,",
			"10,000 times."
		);
		Delay.second(1);

		inDemoWin(() -> {
			for (int i=0; i<10000; i++) {
				screen.setCursor(0, 0);
				for (int j=((demoWin.width - 2) * (demoWin.height - 2)); j>0; j--) {
					if (j == 1) {
						screen.putChar(demoWin.width - 3, demoWin.height - 3, (char)(Math.random() * 256), (int)(Math.random() * 8), (int)(Math.random() * 8));
					} else {
						screen.putChar((char)(Math.random() * 256), (int)(Math.random() * 8), (int)(Math.random() * 8));
					}
				}
			}
		});
		Delay.second(2);

		writeTextWin(
			"That concludes our demo for now, but there is more JScreen",
			"can do.  Feel free to have a closer look.  Maybe it's",
			"exactly what you've been looking for.",
			"Or maybe it isn't.  That's cool, too.",
			"Thanks for watching!"
		);
		Delay.second(2);

	}
	
	private void writeTextWin(String... text) {
		screen.restoreWindowState(textWinSave);
		screen.printlnBPS(BPS);
		for (String str : text) {
			screen.printlnBPS(BPS, str);
		}
		textWinSave = screen.saveWindowState();
	}
	
	private void inDemoWin(Runnable stuff) {
		screen.restoreWindowState(demoWinSave);
		stuff.run();
		demoWinSave = screen.saveWindowState();
	}
}
