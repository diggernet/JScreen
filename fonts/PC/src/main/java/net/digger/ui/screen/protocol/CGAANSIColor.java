package net.digger.ui.screen.protocol;

import net.digger.ui.screen.color.CGAColor;

public class CGAANSIColor implements ANSIColor {
	@Override
	public int getDefaultFG() {
		return CGAColor.DEFAULT_FG;
	}

	@Override
	public int getDefaultBG() {
		return CGAColor.DEFAULT_BG;
	}

	@Override
	public int getBlack() {
		return CGAColor.BLACK;
	}

	@Override
	public int getRed() {
		return CGAColor.RED;
	}

	@Override
	public int getGreen() {
		return CGAColor.GREEN;
	}

	@Override
	public int getBrown() {
		return CGAColor.BROWN;
	}

	@Override
	public int getBlue() {
		return CGAColor.BLUE;
	}

	@Override
	public int getMagenta() {
		return CGAColor.MAGENTA;
	}

	@Override
	public int getCyan() {
		return CGAColor.CYAN;
	}

	@Override
	public int getLightGrey() {
		return CGAColor.LIGHT_GREY;
	}

}
