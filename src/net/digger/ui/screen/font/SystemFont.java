package net.digger.ui.screen.font;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
 * Defines the parameters of installed monospaced system fonts.
 * @author walton
 */
public class SystemFont extends JScreenFont {
	/**
	 * Default Java monospaced logical font.  The physical font this maps to varies by system.
	 */
	public static final SystemFont MONOSPACED = new SystemFont(Font.MONOSPACED, 8);

	private static final Map<String, SystemFont> fonts = new HashMap<>();
	
	/**
	 * Create a new installed font.
	 * @param family Font family.
	 * @param pointSize Font base point size (ideally, where the font renders 1px as 1px).
	 */
	SystemFont(String family, int pointSize) {
		super(family, pointSize, null, true);
	}

	/**
	 * Returns the SystemFont instance of the named font family, or null if not present.
	 * Must call getFonts() first.
	 * @param family Font family.
	 * @return SystemFont instance of requested family.
	 */
	public static SystemFont getFont(String family) {
		return fonts.get(family);
	}
	
	/**
	 * Get a list of available monospaced font families.
	 * @return Array of font family names.
	 */
	public static String[] getFonts() {
		fonts.clear();
		Set<String> names = new HashSet<>();
		String[] families = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		for (String family : families) {
			try {
				SystemFont font = new SystemFont(family, 8);
				fonts.put(family, font);
				names.add(family);
//				System.out.println("Family: " + family);
			} catch (IllegalArgumentException e) {}
		}
		return names.toArray(new String[0]);
	}
}
