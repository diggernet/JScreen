package net.digger.ui;

import net.digger.ui.screen.JScreen;

public class HelloWorld {
	public static void main(String[] args) {
		JScreen screen = JScreen.createJScreenWindow();
		screen.print("Hello world.");
	}
}
