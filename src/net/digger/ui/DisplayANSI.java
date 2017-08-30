package net.digger.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;

import net.digger.ui.screen.JScreen;
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
 * A simple ANSI art viewer, to show off JScreen.
 * @author walton
 */
public class DisplayANSI {
	@SuppressWarnings("serial")
	private static final Map<Integer, String> SPEEDS = new TreeMap<Integer, String>() {{
		put(110, "110 baud Bell 101");
		put(300, "300 baud v.21");
		put(1200, "1200bps v.22");
		put(2400, "2400bps v.22bis");
		put(4800, "4800bps v.27ter");
		put(9600, "9600bps v.32");
		put(14400, "14.4kbps v.32bis");
		put(19200, "19.2kbps v.32terbo");
		put(28800, "28.8kbps v.34");
		put(33600, "33.6kbps v.34");
		put(56000, "56kbps v.90, v.92");
		put(64000, "64kbps ISDN");
		put(112000, "112kbps v.92 bonded");
		put(128000, "128kbps ISDN bonded");
		put(1544000, "1.544mbps T1");
		put(Integer.MAX_VALUE, "Unlimited");
	}};
	private JScreen screen;
	private int bps = 9600;
	private Path file;
	private static boolean restart = true;
	
	public static void main(String[] args) throws IOException {
		if (args.length < 1) {
			System.out.println();
			System.out.println("DisplayANSI, a simple ANSI art viewer.");
			System.out.println("Usage:");
			System.out.println("\tjava -jar DisplayANSI.jar <filename>");
			System.out.println("Right-click the screen for options.");
			System.out.println();
			return;
		}
		
		DisplayANSI ansi = new DisplayANSI();
		ansi.setFile(args[0]);
		while (true) {
			if (restart) {
				ansi.display();
			}
			if (ansi.checkEscape()) {
				break;
			}
			Delay.milli(500);
		}
	}
	
	public DisplayANSI() {
		screen = JScreen.createJScreenWindow("DisplayANSI (right-click for options)", PCScreenMode.VGA_80x25);
		screen.setTextProtocol(new ANSI(screen));
		screen.hideCursor();
		screen.keyboard.enableKeyBuffer(true);
		JPopupMenu menu = screen.getContextMenu();
		JMenuItem reset = new JMenuItem("Restart");
		menu.add(reset);
		reset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				restart = true;
			}
		});
		JMenu speedMenu = new JMenu("Modem Speed");
		menu.add(speedMenu);
		ButtonGroup group = new ButtonGroup();
		for (int speed : SPEEDS.keySet()) {
			JRadioButtonMenuItem item = new JRadioButtonMenuItem(SPEEDS.get(speed));
			item.setSelected(bps == speed);
			item.setActionCommand(String.valueOf(speed));
			group.add(item);
			speedMenu.add(item);
			item.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					bps = Integer.parseInt(e.getActionCommand());
				}
			});
		}
	}
	
	public boolean checkEscape() {
		while (screen.keyboard.isKeyEvent()) {
			KeyEvent key = screen.keyboard.getKeyEvent();
			if (key.getKeyChar() == KeyEvent.VK_ESCAPE) {
				JFrame frame = (JFrame)SwingUtilities.getRoot(screen.getComponent());
				if (frame != null) {
					frame.dispose();
				}
				screen.close();
				return true;
			}
		}
		return false;
	}

	public void setFile(String file) {
		Path path = Paths.get(file);
		if (!Files.isRegularFile(path)) {
			throw new IllegalArgumentException();
		}
		this.file = path;
	}
	
	public void display() throws IOException {
		restart = false;
		int oldbps = bps;
		int wait = (int)(1000000 / (bps / 8.0));
		System.out.println("Wait: " + wait);
		for (byte b : Files.readAllBytes(file)) {
			char ch = (char)(b & 0xff);		// convert signed byte to unsigned char
			if (bps != oldbps) {
				oldbps = bps;
				wait = (int)(1000000 / (bps / 8.0));
				System.out.println("Wait: " + wait);
			}
			screen.print(ch);
			Delay.micro(wait);
			if (restart) {
				screen.printlnBPS(bps);
				return;
			}
		}
	}
}
