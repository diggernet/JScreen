package net.digger.ui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.apache.commons.lang3.ArrayUtils;

import net.digger.ui.screen.JScreen;
import net.digger.ui.screen.color.GreenScreenColor;
import net.digger.ui.screen.cursor.BlockCursor;
import net.digger.ui.screen.font.IBMFont;
import net.digger.ui.screen.mode.JScreenMode;

public class KeyEventTest {
	private JScreen screen;
	
	public static void main(String[] args) throws InterruptedException {
		KeyEventTest test = new KeyEventTest();
		test.run();
	}
	
	public void run() throws InterruptedException {
		JScreenMode mode = new JScreenMode(140, 50, new BlockCursor(), IBMFont.VGA_9x16, GreenScreenColor.PALETTE);
		screen = JScreen.createJScreenWindow("KeyEventTest", mode);
		KeyListener listener = new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				dumpKey(e);
			}

			@Override
			public void keyPressed(KeyEvent e) {
				dumpKey(e);
			}

			@Override
			public void keyReleased(KeyEvent e) {
				dumpKey(e);
			}
		};
		screen.getComponent().addKeyListener(listener);
		println("KeyEventTest.  Prints details of all key events.");
		println();
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

	private void dumpKey(KeyEvent event) {
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
		if ((ch >= 0x20) && (ch != 0x7f) && (ch < 0xffff)) {
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
