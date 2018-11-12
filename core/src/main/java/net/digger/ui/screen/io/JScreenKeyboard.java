package net.digger.ui.screen.io;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

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
	 * @param screen Screen to listen for key events from.
	 */
	public JScreenKeyboard(JScreen screen) {
		this.screen = screen;
		
		/**
		 * Key listener.
		 * KEY_RELEASED events are ignored, and KEY_PRESSED events are ignored unless isActionKey().
		 * On Windows, some CTRL combination KEY_TYPED events are either reported wrong (^M -> ^J)
		 * or are not reported at all.  This listener will fake KEY_TYPED events for those combinations,
		 * and then if the events actually occur (such as on Linux) will suppress the duplicate
		 * real event.
		 */
		KeyListener listener = new KeyListener() {
			/**
			 * Map of KEY_PRESSED key code to KEY_TYPED key character to fake.
			 */
			@SuppressWarnings("serial")
			private final Map<Integer, Character> winCtrlKeyMap = new HashMap<Integer, Character>() {{
				put(77, (char)0x0d);	// ^M
				put(49, '1');			// ^1 (number row)
				put(50, '2');			// ^2 (number row)
				put(51, '3');			// ^3 (number row)
				put(52, '4');			// ^4 (number row)
				put(53, '5');			// ^5 (number row)
				put(54, '6');			// ^6 (number row)
				put(55, '7');			// ^7 (number row)
				put(56, '8');			// ^8 (number row)
				put(57, '9');			// ^9 (number row)
				put(48, '0');			// ^0 (number row)
				put(45, '-');			// ^- (number row)
				put(61, '=');			// ^= (number row)
				put(96, '0');			// ^0 (keypad)
				put(97, '1');			// ^1 (keypad)
				put(98, '2');			// ^2 (keypad)
				put(99, '3');			// ^3 (keypad)
				put(100, '4');			// ^4 (keypad)
				put(101, '5');			// ^5 (keypad)
				put(102, '6');			// ^6 (keypad)
				put(103, '7');			// ^7 (keypad)
				put(104, '8');			// ^8 (keypad)
				put(105, '9');			// ^9 (keypad)
				put(106, '*');			// * (keypad)
				put(107, '+');			// + (keypad)
				put(109, '-');			// - (keypad)
				put(110, '.');			// . (keypad)
				put(111, '/');			// / (keypad)
			}};
			/**
			 * Map of KEY_TYPED fake key character to SHIFTed fake key character.
			 */
			@SuppressWarnings("serial")
			private final Map<Character, Character> winShiftKeyMap = new HashMap<Character, Character>() {{
				put('1', '!');
				put('2', '@');
				put('3', '#');
				put('4', '$');
				put('5', '%');
				put('6', '^');
				put('7', '&');
				put('8', '*');
				put('9', '(');
				put('0', ')');
				put('-', (char)0x1f);
				put('=', '+');
			}};
			/**
			 * Faked KEY_TYPED key character.
			 */
			private Character faked = null;
			
			@Override
			public void keyTyped(KeyEvent e) {
				if (faked != null) {
					// if KEY_TYPED event was faked...
					if (e.getKeyChar() == faked) {
						// don't add duplicate real event
						if (KEY_DEBUG) {
							System.out.print("Suppressed: (faked=" + faked + ")");
						}
						faked = null;
						return;
					} else if ((e.getKeyChar() == (char)0x0a) && (faked == (char)0x0d)) {
						// don't add modified real event
						if (KEY_DEBUG) {
							System.out.print("Suppressed hack: (faked=" + faked + ")");
						}
						faked = null;
						return;
					}
					faked = null;
				}
				addKeyEvent(e);
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				// ignore KEY_RELEASED events
//				addKeyEvent(e);
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if (KEY_DEBUG) {
					dumpKey(e);
				}
				if (e.isActionKey()) {
					// add if there won't be a KEY_TYPED event
					addKeyEvent(e);
					return;
				}
				Character c = winCtrlKeyMap.get(e.getKeyCode());
				if (c != null) {
					int modifiers = e.getModifiersEx();
					boolean ctrl = ((modifiers & KeyEvent.CTRL_DOWN_MASK) != 0);
					boolean shift = ((modifiers & KeyEvent.SHIFT_DOWN_MASK) != 0);
					boolean numpad = (e.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD);
					if (ctrl) {
						// only fake an event if CTRL is pressed
						if (!shift || !numpad) {
							// don't fake an event if SHIFT is pressed on numpad
							if (shift) {
								// fake a different char if SHIFT is pressed
								Character c2 = winShiftKeyMap.get(c);
								if (c2 != null) {
									c = c2;
								}
							}
							// fake a key typed event
							KeyEvent e2 = new KeyEvent((Component)e.getSource(), KeyEvent.KEY_TYPED, e.getWhen(),
									e.getModifiers() | e.getModifiersEx(), 0, c, KeyEvent.KEY_LOCATION_UNKNOWN);
							if (KEY_DEBUG) {
								System.out.print("Faked:");
							}
							addKeyEvent(e2);
							faked = c;
						}
					}
				}
			}
		};
		screen.getComponent().addKeyListener(listener);
	}

	// ##### Key buffer methods #####
	
	/**
	 * Turn the key event buffer on/off.
	 * This implements keyboard input where key events wait in a buffer until you read them,
	 * or you can do a blocking read to wait for a key event.
	 * Defaults to off, to avoid having the buffer grow indefinitely when not in use.  
	 * You must turn it on if you want to use keyboard input.
	 * @param enable Turn key buffer on or off.
	 */
	public void enableKeyBuffer(boolean enable) {
		keyBufferEnabled = enable;
	}
	
	/**
	 * Check if the key buffer is enabled;
	 * @return State of key buffer.
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
	 * @return True if an event is in the buffer.
	 */
	public boolean isKeyEvent() {
		return !keyBuffer.isEmpty();
	}
	
	/**
	 * Returns the first waiting key event, or null if the buffer is empty.
	 * @return First event from buffer.
	 */
	public KeyEvent getKeyEvent() {
		return keyBuffer.poll();
	}
	
	/**
	 * Returns the first waiting key event, or waits for an event if the buffer is empty.
	 * @return First event from buffer.
	 * @throws RuntimeException If called when the key buffer is not enabled (or else it'll block forever).
	 * @throws InterruptedException If wait is interrupted.
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
	 * @param event Event to add to buffer.
	 */
	public void addKeyEvent(KeyEvent event) {
		if (!keyBufferEnabled) {
			return;
		}
		synchronized(keyBuffer) {
			keyBuffer.add(event);
			if (KEY_DEBUG) {
				dumpKey(keyBuffer.peekLast());
			}
			keyBuffer.notify();
		}
	}

	/**
	 * Add KeyEvents replicating the given string to the key buffer.
	 * This can be used for pasting text, and it will be handled as though it was typed.
	 * @param text Text to add to buffer as key events.
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
	 * @throws InterruptedException If wait is interrupted.
	 */
	public String readLine() throws InterruptedException {
		return readLine(Integer.MAX_VALUE);
	}
	
	/**
	 * Inputs a line with the given max length at the current cursor position.
	 * @param maxLength Maximum length of string to read.
	 * @return Input string, when the user presses CR.
	 * @throws InterruptedException If wait is interrupted.
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
	 * @param event Key event to display details of.
	 */
	private void dumpKey(KeyEvent event) {
		System.out.println();

		//You should only rely on the key char if the event
		//is a key typed event.
		int id = event.getID();
		switch (id) {
			case KeyEvent.KEY_PRESSED:
				System.out.println("KEY_PRESSED:");
				break;
			case KeyEvent.KEY_RELEASED:
				System.out.println("KEY_RELEASED:");
				break;
			case KeyEvent.KEY_TYPED:
				System.out.println("KEY_TYPED:");
				break;
			default:
				System.out.println("Unknown event type:");
				break;
		}

//		if (id == KeyEvent.KEY_TYPED) {
			char c = event.getKeyChar();
			System.out.printf("\tkey character = '%c' (%d = 0x%s)\n", c, (int)c, Integer.toHexString(c));
//		} else {
			int keyCode = event.getKeyCode();
			System.out.printf("\tkey code = %d (%s)\n", keyCode, KeyEvent.getKeyText(keyCode));
//		}

		int modifiersEx = event.getModifiersEx();
		String modString = KeyEvent.getModifiersExText(modifiersEx);
		if (StringUtils.isBlank(modString)) {
			modString = "no extended modifiers";
		}
		System.out.printf("\textended modifiers = %d (%s)\n", modifiersEx, modString);

		System.out.printf("\taction key? %s\n", event.isActionKey() ? "YES" : "NO");

		switch (event.getKeyLocation()) {
			case KeyEvent.KEY_LOCATION_STANDARD:
				System.out.println("\tkey location: standard");
				break;
			case KeyEvent.KEY_LOCATION_LEFT:
				System.out.println("\tkey location: left");
				break;
			case KeyEvent.KEY_LOCATION_RIGHT:
				System.out.println("\tkey location: right");
				break;
			case KeyEvent.KEY_LOCATION_NUMPAD:
				System.out.println("\tkey location: numpad");
				break;
			case KeyEvent.KEY_LOCATION_UNKNOWN:
			default:
				System.out.println("\tkey location: unknown");
				break;
		}
	}
}
