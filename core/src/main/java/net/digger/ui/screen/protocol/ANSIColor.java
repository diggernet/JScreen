/**
 * Copyright © 2018  David Walton
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
package net.digger.ui.screen.protocol;

/**
 * Interface used by ANSI protocol implementation to look up palette indexes for colors.
 * 
 * @author walton
 */
public interface ANSIColor {
	public int getDefaultFG();
	public int getDefaultBG();
	public int getBlack();
	public int getRed();
	public int getGreen();
	public int getBrown();
	public int getBlue();
	public int getMagenta();
	public int getCyan();
	public int getLightGrey();
}
