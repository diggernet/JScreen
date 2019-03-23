package net.digger.ui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import net.digger.ui.screen.JScreen;
import net.digger.ui.screen.color.GreenScreenColor;
import net.digger.ui.screen.cursor.BlockCursor;
import net.digger.ui.screen.font.IBMFont;
import net.digger.ui.screen.mode.JScreenMode;

public class KeyboardTest {
	private JScreen screen;
	
	public static void main(String[] args) throws InterruptedException {
		KeyboardTest test = new KeyboardTest();
		test.run();
	}
	
	public void run() throws InterruptedException {
		JScreenMode mode = new JScreenMode(140, 50, new BlockCursor(), IBMFont.VGA_9x16, GreenScreenColor.PALETTE);
		screen = JScreen.createJScreenWindow("KeyboardTest", mode);
		screen.keyboard.enableKeyBuffer(true);
		for (KeyListener listener : screen.getComponent().getKeyListeners()) {
			screen.getComponent().removeKeyListener(listener);
		}
		KeyListener listener = new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				screen.keyboard.addKeyEvent(e);
			}

			@Override
			public void keyPressed(KeyEvent e) {
				screen.keyboard.addKeyEvent(e);
			}

			@Override
			public void keyReleased(KeyEvent e) {
				screen.keyboard.addKeyEvent(e);
			}
		};
		screen.getComponent().addKeyListener(listener);
		println("KeyboardTest.  Prints details of keystrokes.");
		println();
		KeyEvent event = null;
		while ((event = screen.keyboard.awaitKeyEvent()) != null) {
			dumpKey(event);
		}
	}
	
	private void print(Object... texts) {
		if (ArrayUtils.isNotEmpty(texts)) {
			for (Object text : texts) {
				screen.print(text.toString());
				System.out.print(text.toString());
			}
		}
	}
	
	private void println(Object... texts) {
		print(texts);
		print("\r\n");
	}

	int[] modifiers = new int[] {
			KeyEvent.VK_SHIFT, KeyEvent.VK_CONTROL, KeyEvent.VK_ALT, KeyEvent.VK_META, KeyEvent.VK_ALT_GRAPH
	};

	int[] fkeys = new int[] {
			KeyEvent.VK_F1, KeyEvent.VK_F2, KeyEvent.VK_F3, KeyEvent.VK_F4,
			KeyEvent.VK_F5, KeyEvent.VK_F6, KeyEvent.VK_F7, KeyEvent.VK_F8,
			KeyEvent.VK_F9, KeyEvent.VK_F10, KeyEvent.VK_F11, KeyEvent.VK_F12,
			KeyEvent.VK_F13, KeyEvent.VK_F14, KeyEvent.VK_F15, KeyEvent.VK_F16,
			KeyEvent.VK_F17, KeyEvent.VK_F18, KeyEvent.VK_F19, KeyEvent.VK_F20,
			KeyEvent.VK_F21, KeyEvent.VK_F22, KeyEvent.VK_F23, KeyEvent.VK_F24
	};

	int[] cursor = new int[] {
			KeyEvent.VK_INSERT, KeyEvent.VK_BEGIN,
			KeyEvent.VK_HOME, KeyEvent.VK_END, KeyEvent.VK_PAGE_UP, KeyEvent.VK_PAGE_DOWN,
			KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT,
			KeyEvent.VK_KP_UP, KeyEvent.VK_KP_DOWN, KeyEvent.VK_KP_LEFT, KeyEvent.VK_KP_RIGHT
	};
	
	int[] numpadNum = new int[] {
			KeyEvent.VK_NUMPAD0, KeyEvent.VK_NUMPAD1, KeyEvent.VK_NUMPAD2, KeyEvent.VK_NUMPAD3, KeyEvent.VK_NUMPAD4,
			KeyEvent.VK_NUMPAD5, KeyEvent.VK_NUMPAD6, KeyEvent.VK_NUMPAD7, KeyEvent.VK_NUMPAD8, KeyEvent.VK_NUMPAD9
	};

	int[] numpadMath = new int[] {
			KeyEvent.VK_ADD, KeyEvent.VK_SUBTRACT, KeyEvent.VK_MULTIPLY, KeyEvent.VK_DIVIDE, KeyEvent.VK_DECIMAL
	};

	int[] special = new int[] {
			KeyEvent.VK_CAPS_LOCK, KeyEvent.VK_NUM_LOCK, KeyEvent.VK_SCROLL_LOCK,
			KeyEvent.VK_PAUSE, KeyEvent.VK_PRINTSCREEN,
			KeyEvent.VK_WINDOWS, KeyEvent.VK_CONTEXT_MENU
	};
	
	int[] control = new int[] {
			KeyEvent.VK_BACK_SPACE, KeyEvent.VK_TAB, KeyEvent.VK_ENTER, KeyEvent.VK_ESCAPE, KeyEvent.VK_DELETE, KeyEvent.VK_CANCEL
	};

	private void keyTyped(char ch, boolean numpad) {
		print("Typed '" + ch + "'");
		if (numpad) {
			print(" (NumPad)");
		}
		println();
	}
	
	private boolean keyPressed(int mod, int code, boolean numpad) {
		StringBuilder sb = new StringBuilder();
		sb.append("Pressed ");
		if (mod > 0) {
//			sb.append(KeyEvent.getModifiersExText(mod));
			Set<String> mods = new HashSet<>();
			if ((mod & KeyEvent.SHIFT_DOWN_MASK) > 0) {
				mods.add("Shift");
			}
			if ((mod & KeyEvent.CTRL_DOWN_MASK) > 0) {
				mods.add("Control");
			}
			if ((mod & KeyEvent.META_DOWN_MASK) > 0) {
				mods.add("Meta/Command");
			}
			if ((mod & KeyEvent.ALT_DOWN_MASK) > 0) {
				mods.add("Alt/Option");
			}
			if ((mod & KeyEvent.ALT_GRAPH_DOWN_MASK) > 0) {
				mods.add("AltGraph");
			}
			sb.append(StringUtils.join(mods, " + ")).append(" + ");
		}
//		sb.append(KeyEvent.getKeyText(code)).append("\n");
		switch (code) {
			case KeyEvent.VK_F1:
				sb.append("F1");
				break;
			case KeyEvent.VK_F2:
				sb.append("F2");
				break;
			case KeyEvent.VK_F3:
				sb.append("F3");
				break;
			case KeyEvent.VK_F4:
				sb.append("F4");
				break;
			case KeyEvent.VK_F5:
				sb.append("F5");
				break;
			case KeyEvent.VK_F6:
				sb.append("F6");
				break;
			case KeyEvent.VK_F7:
				sb.append("F7");
				break;
			case KeyEvent.VK_F8:
				sb.append("F8");
				break;
			case KeyEvent.VK_F9:
				sb.append("F9");
				break;
			case KeyEvent.VK_F10:
				sb.append("F10");
				break;
			case KeyEvent.VK_F11:
				sb.append("F11");
				break;
			case KeyEvent.VK_F12:
				sb.append("F12");
				break;
			case KeyEvent.VK_F13:
				sb.append("F13");
				break;
			case KeyEvent.VK_F14:
				sb.append("F14");
				break;
			case KeyEvent.VK_F15:
				sb.append("F15");
				break;
			case KeyEvent.VK_F16:
				sb.append("F16");
				break;
			case KeyEvent.VK_F17:
				sb.append("F17");
				break;
			case KeyEvent.VK_F18:
				sb.append("F18");
				break;
			case KeyEvent.VK_F19:
				sb.append("F19");
				break;
			case KeyEvent.VK_F20:
				sb.append("F20");
				break;
			case KeyEvent.VK_F21:
				sb.append("F21");
				break;
			case KeyEvent.VK_F22:
				sb.append("F22");
				break;
			case KeyEvent.VK_F23:
				sb.append("F23");
				break;
			case KeyEvent.VK_F24:
				sb.append("F24");
				break;
			case KeyEvent.VK_INSERT:
				sb.append("Insert");
				break;
			case KeyEvent.VK_BEGIN:
				// Keypad 5 with numlock off on Linux
				/*
PRESSED: Char:0xffff, Code:0xff58 (Begin), ExtCode:0xff58 (Begin), Mod:0x0, ModEx:0x0, Action:Y, Loc:Num
				 */
				sb.append("Begin");
				break;
			case KeyEvent.VK_CLEAR:
				// Keypad 5 with numlock off on Windows
				/*
WIN:PRESSED: Char:0xffff, Code:0xc (Clear), ExtCode:0xc (Clear), Mod:0x0, ModEx:0x0, Action:N, Loc:Num
				 */
				sb.append("Clear");
				break;
			case KeyEvent.VK_HOME:
				sb.append("Home");
				break;
			case KeyEvent.VK_END:
				sb.append("End");
				break;
			case KeyEvent.VK_PAGE_UP:
				sb.append("Page Up");
				break;
			case KeyEvent.VK_PAGE_DOWN:
				sb.append("Page Down");
				break;
			case KeyEvent.VK_UP:
			case KeyEvent.VK_KP_UP:
				sb.append("Up");
				break;
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_KP_DOWN:
				sb.append("Down");
				break;
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_KP_LEFT:
				sb.append("Left");
				break;
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_KP_RIGHT:
				sb.append("Right");
				break;
			case KeyEvent.VK_CAPS_LOCK:
				sb.append("Caps Lock");
				break;
			case KeyEvent.VK_NUM_LOCK:
				sb.append("Num Lock");
				break;
			case KeyEvent.VK_SCROLL_LOCK:
				sb.append("Scroll Lock");
				break;
			case KeyEvent.VK_PAUSE:
				sb.append("Pause");
				break;
			case KeyEvent.VK_PRINTSCREEN:
				sb.append("Print Screen");
				break;
			case KeyEvent.VK_WINDOWS:
				sb.append("Windows");
				break;
			case KeyEvent.VK_CONTEXT_MENU:
				sb.append("Context Menu");
				break;
			case KeyEvent.VK_NUMPAD0:
				sb.append("'0'");
				break;
			case KeyEvent.VK_NUMPAD1:
				sb.append("'1'");
				break;
			case KeyEvent.VK_NUMPAD2:
				sb.append("'2'");
				break;
			case KeyEvent.VK_NUMPAD3:
				sb.append("'3'");
				break;
			case KeyEvent.VK_NUMPAD4:
				sb.append("'4'");
				break;
			case KeyEvent.VK_NUMPAD5:
				sb.append("'5'");
				break;
			case KeyEvent.VK_NUMPAD6:
				sb.append("'6'");
				break;
			case KeyEvent.VK_NUMPAD7:
				sb.append("'7'");
				break;
			case KeyEvent.VK_NUMPAD8:
				sb.append("'8'");
				break;
			case KeyEvent.VK_NUMPAD9:
				sb.append("'9'");
				break;
			case KeyEvent.VK_ADD:
				sb.append("'+'");
				break;
			case KeyEvent.VK_SUBTRACT:
				sb.append("'-'");
				break;
			case KeyEvent.VK_MULTIPLY:
				sb.append("'*'");
				break;
			case KeyEvent.VK_DIVIDE:
				sb.append("'/'");
				break;
			case KeyEvent.VK_DECIMAL:
				sb.append("'.'");
				break;
			case KeyEvent.VK_DELETE:
				sb.append("Delete");
				break;
			case KeyEvent.VK_ENTER:
				sb.append("Enter");
				break;
			case KeyEvent.VK_BACK_SPACE:
				sb.append("Backspace");
				break;
			case KeyEvent.VK_TAB:
				sb.append("Tab");
				break;
			case KeyEvent.VK_ESCAPE:
				sb.append("Escape");
				break;
			case KeyEvent.VK_CANCEL:
				sb.append("Cancel");
				break;
			case KeyEvent.VK_A:
			case KeyEvent.VK_B:
			case KeyEvent.VK_C:
			case KeyEvent.VK_D:
			case KeyEvent.VK_E:
			case KeyEvent.VK_F:
			case KeyEvent.VK_G:
			case KeyEvent.VK_H:
			case KeyEvent.VK_I:
			case KeyEvent.VK_J:
			case KeyEvent.VK_K:
			case KeyEvent.VK_L:
			case KeyEvent.VK_M:
			case KeyEvent.VK_N:
			case KeyEvent.VK_O:
			case KeyEvent.VK_P:
			case KeyEvent.VK_Q:
			case KeyEvent.VK_R:
			case KeyEvent.VK_S:
			case KeyEvent.VK_T:
			case KeyEvent.VK_U:
			case KeyEvent.VK_V:
			case KeyEvent.VK_W:
			case KeyEvent.VK_X:
			case KeyEvent.VK_Y:
			case KeyEvent.VK_Z:
			case KeyEvent.VK_0:
			case KeyEvent.VK_1:
			case KeyEvent.VK_2:
			case KeyEvent.VK_3:
			case KeyEvent.VK_4:
			case KeyEvent.VK_5:
			case KeyEvent.VK_6:
			case KeyEvent.VK_7:
			case KeyEvent.VK_8:
			case KeyEvent.VK_9:
			case KeyEvent.VK_MINUS:
			case KeyEvent.VK_EQUALS:
			case KeyEvent.VK_OPEN_BRACKET:
			case KeyEvent.VK_CLOSE_BRACKET:
			case KeyEvent.VK_BACK_SLASH:
			case KeyEvent.VK_SEMICOLON:
			case KeyEvent.VK_COMMA:
			case KeyEvent.VK_PERIOD:
			case KeyEvent.VK_SLASH:
			case KeyEvent.VK_SPACE:
				sb.append("'").append((char)code).append("'");
				break;
			case KeyEvent.VK_QUOTE:
				sb.append("'''");
				break;
			case KeyEvent.VK_BACK_QUOTE:
				sb.append("'`'");
				break;
			default:
				return false;
		}
		if (numpad) {
			sb.append(" (NumPad)");
		}
		println(sb.toString());
		return true;
	}
	
	/*
	 * Process the given event, and return true if the event was consumed.
	 */
	private boolean processEvent(KeyEvent event) {
		// ignore TYPED and RELEASED events
		if ((event.getID() == KeyEvent.KEY_TYPED) || (event.getID() == KeyEvent.KEY_RELEASED)) {
			return true;
		}
		char ch = event.getKeyChar();
		int code = event.getExtendedKeyCode();
		if (code == KeyEvent.VK_UNDEFINED) {
			code = event.getKeyCode();
		}
		int mod = event.getModifiersEx();
		boolean numpad = (event.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD);

		if (event.isActionKey()) {
			// process F-keys
			/*
ID:PRESSED,Char:0xffff,Code:0x74 (F5),ExtCode:0x74,Mod:0x0 (),ModEx:0x0 (),Action:Y,Loc:Std
ID:PRESSED,Char:0xffff,Code:0x74 (F5),ExtCode:0x74,Mod:0x1 (Shift),ModEx:0x40 (Shift),Action:Y,Loc:Std
ID:PRESSED,Char:0xffff,Code:0x74 (F5),ExtCode:0x74,Mod:0x2 (Ctrl),ModEx:0x80 (Ctrl),Action:Y,Loc:Std
ID:PRESSED,Char:0xffff,Code:0x74 (F5),ExtCode:0x74,Mod:0x8 (Alt),ModEx:0x200 (Alt),Action:Y,Loc:Std
			 */
			if (ArrayUtils.contains(fkeys, code)) {
				return keyPressed(mod, code, numpad);
			}
			// process cursor keys (neither delete is an action key)
			/*
ID:PRESSED,Char:0xffff,Code:0x9b (Insert),ExtCode:0x9b,Mod:0x0 (),ModEx:0x0 (),Action:Y,Loc:Std
ID:PRESSED,Char:0xffff,Code:0x24 (Home),ExtCode:0x24,Mod:0x0 (),ModEx:0x0 (),Action:Y,Loc:Std
ID:PRESSED,Char:0xffff,Code:0x23 (End),ExtCode:0x23,Mod:0x0 (),ModEx:0x0 (),Action:Y,Loc:Std
ID:PRESSED,Char:0xffff,Code:0x21 (Page Up),ExtCode:0x21,Mod:0x0 (),ModEx:0x0 (),Action:Y,Loc:Std
ID:PRESSED,Char:0xffff,Code:0x22 (Page Down),ExtCode:0x22,Mod:0x0 (),ModEx:0x0 (),Action:Y,Loc:Std
ID:PRESSED,Char:0xffff,Code:0x26 (Up),ExtCode:0x26,Mod:0x0 (),ModEx:0x0 (),Action:Y,Loc:Std
ID:PRESSED,Char:0xffff,Code:0x28 (Down),ExtCode:0x28,Mod:0x0 (),ModEx:0x0 (),Action:Y,Loc:Std
ID:PRESSED,Char:0xffff,Code:0x25 (Left),ExtCode:0x25,Mod:0x0 (),ModEx:0x0 (),Action:Y,Loc:Std
ID:PRESSED,Char:0xffff,Code:0x27 (Right),ExtCode:0x27,Mod:0x0 (),ModEx:0x0 (),Action:Y,Loc:Std

ID:PRESSED,Char:0xffff,Code:0x9b (Insert),ExtCode:0x9b,Mod:0x0 (),ModEx:0x0 (),Action:Y,Loc:Num
ID:PRESSED,Char:0xffff,Code:0xff58 (Begin),ExtCode:0xff58,Mod:0x0 (),ModEx:0x0 (),Action:Y,Loc:Num
ID:PRESSED,Char:0xffff,Code:0x24 (Home),ExtCode:0x24,Mod:0x0 (),ModEx:0x0 (),Action:Y,Loc:Num
ID:PRESSED,Char:0xffff,Code:0x23 (End),ExtCode:0x23,Mod:0x0 (),ModEx:0x0 (),Action:Y,Loc:Num
ID:PRESSED,Char:0xffff,Code:0x21 (Page Up),ExtCode:0x21,Mod:0x0 (),ModEx:0x0 (),Action:Y,Loc:Num
ID:PRESSED,Char:0xffff,Code:0x22 (Page Down),ExtCode:0x22,Mod:0x0 (),ModEx:0x0 (),Action:Y,Loc:Num
ID:PRESSED,Char:0xffff,Code:0xe0 (Up),ExtCode:0xe0,Mod:0x0 (),ModEx:0x0 (),Action:Y,Loc:Num
ID:PRESSED,Char:0xffff,Code:0xe1 (Down),ExtCode:0xe1,Mod:0x0 (),ModEx:0x0 (),Action:Y,Loc:Num
ID:PRESSED,Char:0xffff,Code:0xe2 (Left),ExtCode:0xe2,Mod:0x0 (),ModEx:0x0 (),Action:Y,Loc:Num
ID:PRESSED,Char:0xffff,Code:0xe3 (Right),ExtCode:0xe3,Mod:0x0 (),ModEx:0x0 (),Action:Y,Loc:Num
			 */
			if (ArrayUtils.contains(cursor, code)) {
				return keyPressed(mod, code, numpad);
			}
			// process special keys
			/*
ID:PRESSED,Char:0xffff,Code:0x14 (Caps Lock),ExtCode:0x14,Mod:0x0 (),ModEx:0x0 (),Action:Y,Loc:Std
ID:PRESSED,Char:0xffff,Code:0x90 (Num Lock),ExtCode:0x90,Mod:0x0 (),ModEx:0x0 (),Action:Y,Loc:Num
ID:PRESSED,Char:0xffff,Code:0x91 (Scroll Lock),ExtCode:0x91,Mod:0x0 (),ModEx:0x0 (),Action:Y,Loc:Std
ID:PRESSED,Char:0xffff,Code:0x13 (Pause),ExtCode:0x13,Mod:0x0 (),ModEx:0x0 (),Action:Y,Loc:Std
ID:PRESSED,Char:0xffff,Code:0x9a (Print Screen),ExtCode:0x9a,Mod:0x0 (),ModEx:0x0 (),Action:Y,Loc:Std
ID:PRESSED,Char:0xffff,Code:0x20c (Windows),ExtCode:0x20c,Mod:0x0 (),ModEx:0x0 (),Action:Y,Loc:Std
ID:PRESSED,Char:0xffff,Code:0x20d (Context Menu),ExtCode:0x20d,Mod:0x0 (),ModEx:0x0 (),Action:Y,Loc:Std
			 */
			if (ArrayUtils.contains(special, code)) {
				return keyPressed(mod, code, numpad);
			}
			// Ctrl-Pause is extra special:  ExtCode=0x0
			/*
ID:PRESSED,Char:0xffff,Code:0x13 (Pause),ExtCode:0x13,Mod:0x0 (),ModEx:0x0 (),Action:Y,Loc:Std
ID:PRESSED,Char:0xffff,Code:0x13 (Pause),ExtCode:0x13,Mod:0x1 (Shift),ModEx:0x40 (Shift),Action:Y,Loc:Std
ID:PRESSED,Char:0xffff,Code:0x13 (Pause),ExtCode:0x0,Mod:0x2 (Ctrl),ModEx:0x80 (Ctrl),Action:Y,Loc:Unk
ID:PRESSED,Char:0xffff,Code:0x13 (Pause),ExtCode:0x13,Mod:0x8 (Alt),ModEx:0x200 (Alt),Action:Y,Loc:Std
			 */
			if ((event.getKeyCode() == KeyEvent.VK_PAUSE) && ((mod & KeyEvent.CTRL_DOWN_MASK) > 0)) {
				return keyPressed(mod, event.getKeyCode(), numpad);
			}
			return false;
		}

		if (numpad) {
			// process numpad-#
			/*
ID:PRESSED,Char:0x30,Code:0x60 (NumPad-0),ExtCode:0x60,Mod:0x0 (),ModEx:0x0 (),Action:N,Loc:Num
ID:PRESSED,Char:0xffff,Code:0x60 (NumPad-0),ExtCode:0x60,Mod:0x1 (Shift),ModEx:0x40 (Shift),Action:N,Loc:Num
ID:PRESSED,Char:0x30,Code:0x60 (NumPad-0),ExtCode:0x60,Mod:0x2 (Ctrl),ModEx:0x80 (Ctrl),Action:N,Loc:Num
ID:PRESSED,Char:0x30,Code:0x60 (NumPad-0),ExtCode:0x60,Mod:0x8 (Alt),ModEx:0x200 (Alt),Action:N,Loc:Num
				 */
			if (ArrayUtils.contains(numpadNum, code)) {
				if (mod == 0) {
					keyTyped(ch, numpad);
				} else {
					return keyPressed(mod, code, numpad);
				}
				return true;
			}
			// process numpad-math
			/*
ID:PRESSED,Char:0x2b,Code:0x6b (NumPad +),ExtCode:0x6b,Mod:0x0 (),ModEx:0x0 (),Action:N,Loc:Num
ID:PRESSED,Char:0x2d,Code:0x6d (NumPad -),ExtCode:0x6d,Mod:0x0 (),ModEx:0x0 (),Action:N,Loc:Num
ID:PRESSED,Char:0x2a,Code:0x6a (NumPad *),ExtCode:0x6a,Mod:0x0 (),ModEx:0x0 (),Action:N,Loc:Num
ID:PRESSED,Char:0x2f,Code:0x6f (NumPad /),ExtCode:0x6f,Mod:0x0 (),ModEx:0x0 (),Action:N,Loc:Num
ID:PRESSED,Char:0x2e,Code:0x6e (NumPad .),ExtCode:0x6e,Mod:0x0 (),ModEx:0x0 (),Action:N,Loc:Num
			 */
			if (ArrayUtils.contains(numpadMath, code)) {
				if (mod == 0) {
					keyTyped(ch, numpad);
					return true;
				} else {
					return keyPressed(mod, code, numpad);
				}
			}
			// special case for numpad delete and enter, because otherwise we return false
			/*
ID:PRESSED,Char:0x7f,Code:0x7f (Delete),ExtCode:0x7f,Mod:0x0 (),ModEx:0x0 (),Action:N,Loc:Std
ID:PRESSED,Char:0x7f,Code:0x7f (Delete),ExtCode:0x7f,Mod:0x0 (),ModEx:0x0 (),Action:N,Loc:Num
ID:PRESSED,Char:0xa,Code:0xa (Enter),ExtCode:0xa,Mod:0x0 (),ModEx:0x0 (),Action:N,Loc:Std
ID:PRESSED,Char:0xa,Code:0xa (Enter),ExtCode:0xa,Mod:0x0 (),ModEx:0x0 (),Action:N,Loc:Num
			 */
			if ((code == KeyEvent.VK_DELETE) || (code == KeyEvent.VK_ENTER)) {
				return keyPressed(mod, code, numpad);
			}
			if (code == KeyEvent.VK_CLEAR) {
				return keyPressed(mod, code, numpad);
			}
			return false;
		}

		if (event.getKeyChar() == KeyEvent.CHAR_UNDEFINED) {
			// ignore modifier PRESSED events
			/*
ID:PRESSED,Char:0xffff,Code:0x10 (Shift),ExtCode:0x10,Mod:0x1 (Shift),ModEx:0x40 (Shift),Action:N,Loc:Left
ID:PRESSED,Char:0xffff,Code:0x10 (Shift),ExtCode:0x10,Mod:0x1 (Shift),ModEx:0x40 (Shift),Action:N,Loc:Right
ID:PRESSED,Char:0xffff,Code:0x11 (Ctrl),ExtCode:0x11,Mod:0x2 (Ctrl),ModEx:0x80 (Ctrl),Action:N,Loc:Left
ID:PRESSED,Char:0xffff,Code:0x11 (Ctrl),ExtCode:0x11,Mod:0x2 (Ctrl),ModEx:0x80 (Ctrl),Action:N,Loc:Right
ID:PRESSED,Char:0xffff,Code:0x12 (Alt),ExtCode:0x12,Mod:0x8 (Alt),ModEx:0x200 (Alt),Action:N,Loc:Left
ID:PRESSED,Char:0xffff,Code:0x12 (Alt),ExtCode:0x12,Mod:0x8 (Alt),ModEx:0x200 (Alt),Action:N,Loc:Right
MAC:ID:PRESSED,Char:0xffff,Code:0x9d (Meta),ExtCode:0x9d,Mod:0x4 (Meta),ModEx:0x100 (Meta),Action:N,Loc:Left
MAC:ID:PRESSED,Char:0xffff,Code:0xff7e (Alt Graph),ExtCode:0xff7e,Mod:0x20 (Alt Graph),ModEx:0x2000 (Alt Graph),Action:N,Loc:Left
			 */
			if (ArrayUtils.contains(modifiers, code)) {
				return true;
			}
			// Ctrl-6 is special on windows: Char=0xffff
			/*
PRESSED: Char:0x36 (6), Code:0x36 (6), ExtCode:0x36 (6), Mod:0x2 (Ctrl), ModEx:0x80 (Ctrl), Action:N, Loc:Std
WIN:PRESSED: Char:0xffff, Code:0x36 (6), ExtCode:0x36 (6), Mod:0x2 (Ctrl), ModEx:0x80 (Ctrl), Action:N, Loc:Std
			 */
			if ((code == KeyEvent.VK_6) && ((mod & KeyEvent.CTRL_DOWN_MASK) > 0)) {
				return keyPressed(mod, code, numpad);
			}
			// Ctrl-- is special on windows: Char=0xffff
			/*
PRESSED: Char:0x2d (-), Code:0x2d (Minus), ExtCode:0x2d (Minus), Mod:0x2 (Ctrl), ModEx:0x80 (Ctrl), Action:N, Loc:Std
PRESSED: Char:0xffff, Code:0x2d (Minus), ExtCode:0x2d (Minus), Mod:0x2 (Ctrl), ModEx:0x80 (Ctrl), Action:N, Loc:Std
			 */
			if ((code == KeyEvent.VK_MINUS) && ((mod & KeyEvent.CTRL_DOWN_MASK) > 0)) {
				return keyPressed(mod, code, numpad);
			}
			return false;
		}

		if ((ch < 0x20) || (ch == 0x7f)) {
			if (ch == code) {
				// process control-key keys
				// Backspace, Tab, Enter, Escape, Delete, etc
				/*
ID:PRESSED,Char:0x8,Code:0x8 (Backspace),ExtCode:0x8,Mod:0x0 (),ModEx:0x0 (),Action:N,Loc:Std
ID:PRESSED,Char:0x9,Code:0x9 (Tab),ExtCode:0x9,Mod:0x0 (),ModEx:0x0 (),Action:N,Loc:Std
ID:PRESSED,Char:0xa,Code:0xa (Enter),ExtCode:0xa,Mod:0x0 (),ModEx:0x0 (),Action:N,Loc:Std
ID:PRESSED,Char:0x1b,Code:0x1b (Escape),ExtCode:0x1b,Mod:0x0 (),ModEx:0x0 (),Action:N,Loc:Std
ID:PRESSED,Char:0x7f,Code:0x7f (Delete),ExtCode:0x7f,Mod:0x0 (),ModEx:0x0 (),Action:N,Loc:Std
// On Windows, both Ctrl-ScrollLock and Ctrl-Pause do this:
WIN:PRESSED: Char:0x3, Code:0x3 (Cancel), ExtCode:0x3 (Cancel), Mod:0x2 (Ctrl), ModEx:0x80 (Ctrl), Action:N, Loc:Std
				 */
				if (ArrayUtils.contains(control, code)) {
					return keyPressed(mod, code, numpad);
				}
				return false;
			} else if ((mod & KeyEvent.CTRL_DOWN_MASK) > 0) {
				// process control keys
				/*
ID:PRESSED,Char:0x1,Code:0x41 (A),ExtCode:0x41,Mod:0x2 (Ctrl),ModEx:0x80 (Ctrl),Action:N,Loc:Std
ID:PRESSED,Char:0x8,Code:0x48 (H),ExtCode:0x48,Mod:0x2 (Ctrl),ModEx:0x80 (Ctrl),Action:N,Loc:Std
ID:PRESSED,Char:0x9,Code:0x49 (I),ExtCode:0x49,Mod:0x2 (Ctrl),ModEx:0x80 (Ctrl),Action:N,Loc:Std
ID:PRESSED,Char:0xa,Code:0x4a (J),ExtCode:0x4a,Mod:0x2 (Ctrl),ModEx:0x80 (Ctrl),Action:N,Loc:Std
ID:PRESSED,Char:0xd,Code:0x4d (M),ExtCode:0x4d,Mod:0x2 (Ctrl),ModEx:0x80 (Ctrl),Action:N,Loc:Std
ID:PRESSED,Char:0x1b,Code:0x5b (Open Bracket),ExtCode:0x5b,Mod:0x2 (Ctrl),ModEx:0x80 (Ctrl),Action:N,Loc:Std
				 */
				return keyPressed(mod, code, numpad);
			}
		} else {
			if ((mod & (KeyEvent.ALT_DOWN_MASK | KeyEvent.ALT_GRAPH_DOWN_MASK | KeyEvent.CTRL_DOWN_MASK | KeyEvent.META_DOWN_MASK)) > 0) {
				// process ctrl/alt/meta/altgraph keys
				return keyPressed(mod, code, numpad);
			} else {
				if ((mod & KeyEvent.SHIFT_DOWN_MASK) >0) {
					// special case for shift-space
					if (ch == KeyEvent.VK_SPACE) {
						return keyPressed(mod, code, numpad);
					}
				}
				// process typing keys
				keyTyped(ch, numpad);
				return true;
			}
		}

		return false;
	}

	private void dumpKey(KeyEvent event) {
		if (processEvent(event)) {
			return;
		}

		StringBuilder sb = new StringBuilder();
		
		int id = event.getID();
		switch (id) {
			case KeyEvent.KEY_PRESSED:
				sb.append("PRESSED");
				break;
			case KeyEvent.KEY_RELEASED:
				sb.append("RELEASED");
				break;
			case KeyEvent.KEY_TYPED:
				sb.append("TYPED");
				break;
			default:
				sb.append("ID 0x").append(Integer.toHexString(id));
				break;
		}
		sb.append(": ");
		
		char ch = event.getKeyChar();
		sb.append(String.format("Char:0x%s", Integer.toHexString(ch)));
		if ((ch >= 0x20) && (ch < 0xffff)) {
			sb.append(String.format(" (%c)", ch));
		}

		sb.append(", ");
		int code = event.getKeyCode();
		sb.append(String.format("Code:0x%s", Integer.toHexString(code)));
		if (code > 0) {
			sb.append(String.format(" (%s)", KeyEvent.getKeyText(code)));
		}

		sb.append(", ");
		code = event.getExtendedKeyCode();
		sb.append(String.format("ExtCode:0x%s", Integer.toHexString(code)));
		if (code > 0) {
			sb.append(String.format(" (%s)", KeyEvent.getKeyText(code)));
		}
		
		sb.append(", ");
		int mod = event.getModifiers();
		sb.append(String.format("Mod:0x%s", Integer.toHexString(mod)));
		if (mod > 0) {
			sb.append(String.format(" (%s)", KeyEvent.getKeyModifiersText(mod)));
		}
		
		sb.append(", ");
		mod = event.getModifiersEx();
		sb.append(String.format("ModEx:0x%s", Integer.toHexString(mod)));
		if (mod > 0) {
			sb.append(String.format(" (%s)", KeyEvent.getModifiersExText(mod)));
		}

		sb.append(", ");
		sb.append("Action:").append(event.isActionKey() ? "Y" : "N");

		sb.append(", ");
		sb.append("Loc:");
		int loc = event.getKeyLocation();
		switch (loc) {
			case KeyEvent.KEY_LOCATION_STANDARD:
				sb.append("Std");
				break;
			case KeyEvent.KEY_LOCATION_LEFT:
				sb.append("Left");
				break;
			case KeyEvent.KEY_LOCATION_RIGHT:
				sb.append("Right");
				break;
			case KeyEvent.KEY_LOCATION_NUMPAD:
				sb.append("Num");
				break;
			case KeyEvent.KEY_LOCATION_UNKNOWN:
				sb.append("Unk");
				break;
			default:
				sb.append("0x").append(Integer.toHexString(loc));
				break;
		}
		
		println(sb.toString());
	}
}
