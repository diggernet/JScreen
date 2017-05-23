package net.digger.ui.screen.io;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.ArrayDeque;

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
 * Implements keyboard input.
 * @author walton
 */
public class JScreenKeyboard {
	// When true, dumps KeyEvent details to assist in troubleshooting.
	private static boolean KEY_DEBUG = false;

	private final JScreen screen;
	private boolean keyBufferEnabled = false;
	private ArrayDeque<KeyEvent> keyBuffer = new ArrayDeque<>();

	/**
	 * Constructor.  Sets up the key event buffer, and adds a KeyListener to the underlying component.
	 * @param screen
	 */
	public JScreenKeyboard(JScreen screen) {
		this.screen = screen;
		
		// key listener
		screen.getComponent().addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				addEvent(e);
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				// ignore KEY_RELEASED events
//				addEvent(e);
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				// only add if there won't be a KEY_TYPED event
				if (e.isActionKey()) {
					addKeyEvent(e);
				}
			}
			
			private void addEvent(KeyEvent e) {
				addKeyEvent(e);
			}
		});
	}

	// ##### Key buffer methods #####
	
	/**
	 * Turn the key event buffer on/off.
	 * This implements keyboard input where key events wait in a buffer until you read them,
	 * or you can do a blocking read to wait for a key event.
	 * Defaults to off, to avoid having the buffer grow indefinitely when not in use.  
	 * You must turn it on if you want to use keyboard input.
	 * @param enable
	 */
	public void enableKeyBuffer(boolean enable) {
		keyBufferEnabled = enable;
	}
	
	/**
	 * Check if the key buffer is enabled;
	 * @return
	 */
	public boolean isKeyBufferEnabled() {
		return keyBufferEnabled;
	}
	
	/**
	 * Discard any key events in the buffer.
	 */
	public void clearKeyBuffer() {
		keyBuffer.clear();
	}
	
	/**
	 * Check whether there are any key events waiting in the buffer.
	 * @return
	 */
	public boolean isKeyEvent() {
		return !keyBuffer.isEmpty();
	}
	
	/**
	 * Returns the first waiting key event, or null if the buffer is empty.
	 * @return
	 */
	public KeyEvent getKeyEvent() {
		return keyBuffer.poll();
	}
	
	/**
	 * Returns the first waiting key event, or waits for an event if the buffer is empty.
	 * @return
	 * @throws RuntimeException If called when the key buffer is not enabled (or else it'll block forever).
	 * @throws InterruptedException
	 */
	public KeyEvent awaitKeyEvent() throws InterruptedException {
		if (!keyBufferEnabled) {
			throw new RuntimeException("Key buffer is not enabled.");
		}
		synchronized(keyBuffer) {
			while (keyBuffer.isEmpty()) {
				keyBuffer.wait();
			}
			return keyBuffer.remove();
		}
	}
	
	/**
	 * Add a KeyEvent to the key buffer.
	 * @param event
	 */
	private void addKeyEvent(KeyEvent event) {
		if (keyBufferEnabled) {
			synchronized(keyBuffer) {
				keyBuffer.add(event);
				if (KEY_DEBUG) {
					dumpKey(keyBuffer.peekLast());
				}
				keyBuffer.notify();
			}
		}
	}

	/**
	 * Add KeyEvents replicating the given string to the key buffer.
	 * This can be used for pasting text, and it will be handled as though it was typed.
	 * @param text
	 */
	public void addKeyEvents(String text) {
		if (!keyBufferEnabled || (text == null)) {
			return;
		}
		for (int i=0; i<text.length(); i++) {
			int modifiers = 0;
			char ch = text.charAt(i);
			if (Character.isUpperCase(ch)) {
				modifiers |= KeyEvent.SHIFT_DOWN_MASK;
			}
			KeyEvent event = new KeyEvent(screen.getComponent(), KeyEvent.KEY_TYPED, System.currentTimeMillis(), modifiers, KeyEvent.VK_UNDEFINED, ch);
			addKeyEvent(event);
		}
	}
	
	/**
	 * Paste text from clipboard to keyboard buffer.
	 */
	public void pasteClipboard() {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		try {
			String text = null;
			Transferable contents = clipboard.getContents(null);
			if ((contents != null) && (contents.isDataFlavorSupported(DataFlavor.stringFlavor))) {
				text = (String)contents.getTransferData(DataFlavor.stringFlavor);
			}
			addKeyEvents(text);
		} catch (IllegalStateException | UnsupportedFlavorException | IOException ex) {
			// If something goes wrong, just don't paste.
		}
	}
	
	// ##### Line input methods #####
	
	/**
	 * Inputs a line at the current cursor position.
	 * @return Input string, when the user presses CR.
	 * @throws InterruptedException
	 */
	public String readLine() throws InterruptedException {
		return readLine(Integer.MAX_VALUE);
	}
	
	/**
	 * Inputs a line with the given max length at the current cursor position.
	 * @return Input string, when the user presses CR.
	 * @throws InterruptedException
	 */
	public String readLine(int maxLength) throws InterruptedException {
		String input = "";
		char ch = 0;
		do {
			KeyEvent event = awaitKeyEvent();
			if (event.getID() != KeyEvent.KEY_TYPED) {
				continue;
			}
			ch = event.getKeyChar();
			switch (ch) {
				case 8:		// BS
					if (input.length() > 0) {
						input = input.substring(0, input.length() - 1);
						screen.print(ch);
					}
					break;
				case 10:	// CR
					screen.println();
					break;
				case 27:	// ESC
					while (input.length() > 0) {
						input = input.substring(0, input.length() - 1);
						screen.print((char)8);
					}
					break;
				default:
					if (input.length() < maxLength) {
						input += ch;
						screen.print(ch);
					}
					break;
			}
		} while (ch != 10);
		return input;
	}
	
	/**
	 * Dumps details of the given KeyEvent to STDOUT.
	 * @param event
	 */
	private void dumpKey(KeyEvent event) {
		//You should only rely on the key char if the event
		//is a key typed event.
		int id = event.getID();
		String keyString;
		if (id == KeyEvent.KEY_TYPED) {
			char c = event.getKeyChar();
			keyString = "key character = '" + c + "' (" + (int)c + " = 0x" + Integer.toHexString(c) + ")";
		} else {
			int keyCode = event.getKeyCode();
			keyString = "key code = " + keyCode
					+ " ("
					+ KeyEvent.getKeyText(keyCode)
					+ ")";
		}

		int modifiersEx = event.getModifiersEx();
		String modString = "extended modifiers = " + modifiersEx;
		String tmpString = KeyEvent.getModifiersExText(modifiersEx);
		if (tmpString.length() > 0) {
			modString += " (" + tmpString + ")";
		} else {
			modString += " (no extended modifiers)";
		}

		String actionString = "action key? ";
		if (event.isActionKey()) {
			actionString += "YES";
		} else {
			actionString += "NO";
		}

		String locationString = "key location: ";
		int location = event.getKeyLocation();
		if (location == KeyEvent.KEY_LOCATION_STANDARD) {
			locationString += "standard";
		} else if (location == KeyEvent.KEY_LOCATION_LEFT) {
			locationString += "left";
		} else if (location == KeyEvent.KEY_LOCATION_RIGHT) {
			locationString += "right";
		} else if (location == KeyEvent.KEY_LOCATION_NUMPAD) {
			locationString += "numpad";
		} else { // (location == KeyEvent.KEY_LOCATION_UNKNOWN)
			locationString += "unknown";
		}
		
		String newline = System.getProperty("line.separator");
		switch (id) {
			case KeyEvent.KEY_PRESSED:
				System.out.println("KEY_PRESSED");
				break;
			case KeyEvent.KEY_RELEASED:
				System.out.println("KEY_RELEASED");
				break;
			case KeyEvent.KEY_TYPED:
				System.out.println("KEY_TYPED");
				break;
			default:
				System.out.println("Unknown event type");
				break;
		}
		System.out.println("    " + keyString + newline
				+ "    " + modString + newline
				+ "    " + actionString + newline
				+ "    " + locationString + newline);
	}
}
