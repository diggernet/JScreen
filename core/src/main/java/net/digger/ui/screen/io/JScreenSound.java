package net.digger.ui.screen.io;

import java.awt.Toolkit;

import net.digger.util.Pause;

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
 * Implements sound output.
 * At least, I hope it will someday...
 * @author walton
 */
public class JScreenSound {
	private boolean mute = false;
	
	public JScreenSound() {}
	
	/**
	 * Beeps the speaker.
	 */
	public void beep() {
		if (!mute) {
			// this is...unreliable, at best
			Toolkit.getDefaultToolkit().beep();
		}
	}
	
	/**
	 * Plays sound at the given frequency for the given milliseconds.
	 * @param hz
	 * @param ms
	 */
	public void play(int hz, int ms) {
		if (!mute) {
			play(hz);
			Pause.milli(ms);
			stop();
		}
	}

	/**
	 * Starts playing sound at the given frequency.
	 * @param hz
	 */
	public void play(int hz) {
		if (!mute) {
			//TODO
		}
	}

	/** 
	 * Stops playing sound.
	 */
	public void stop() {
		//TODO
	}
	
	/**
	 * Returns whether sound is currently muted.
	 * @return
	 */
	public boolean isMuted() {
		return mute;
	}
	
	/**
	 * Set mute on or off.
	 * @param mute
	 */
	public void setMute(boolean mute) {
		this.mute = mute;
	}
}
