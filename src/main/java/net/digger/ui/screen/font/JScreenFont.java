package net.digger.ui.screen.font;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import net.digger.ui.screen.JScreenCell;
import net.digger.ui.screen.color.Attr;
import net.digger.ui.screen.color.JScreenPalette;

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
 * Defines the parameters of a font, and performs rendering.
 * @author walton
 */
public class JScreenFont {
	// Fonts for various old systems can be found at:
	// https://damieng.com/blog/2011/02/20/typography-in-8-bits-system-fonts

	// When true, draws boxes around the character cells to assist in troubleshooting a font.
	private static boolean FONT_DEBUG = false;
	
	private final String family;
	private final int size;
	private final String about;
	private final boolean antiAlias;

	// Set of fonts at various size multiples.
	private final Map<Integer, Font> fonts = new HashMap<>();
	// Set of baseline offsets at various size multiples.
	private final Map<Integer, Integer> offsets = new HashMap<>();
	// Set of character cell dimensions at various size multiples.
	private final Map<Integer, Dimension> sizes = new HashMap<>();

	/**
	 * Create a new display font, not anti-aliased.
	 * @param family Font family.
	 * @param pointSize Font base point size (where the font renders 1px as 1px).
	 * @param about Copyright string for font.
	 */
	public JScreenFont(String family, int pointSize, String about) {
		this(family, pointSize, about, false);
	}
	
	/**
	 * Create a new display font.
	 * @param family Font family.
	 * @param pointSize Font base point size (where the font renders 1px as 1px).
	 * @param about Copyright string for font.
	 * @param antiAlias Render the font anti-aliased.
	 */
	public JScreenFont(String family, int pointSize, String about, boolean antiAlias) {
		this.family = family;
		this.size = pointSize;
		this.about = about;
		this.antiAlias = antiAlias;
		if (!isMonospaced()) {
			throw new IllegalArgumentException(family + " is not monospaced.");
		}
	}
	
	/**
	 * Get font family.
	 * @return Family name of this font.
	 */
	public String getFamily() {
		return family;
	}

	/**
	 * Get base point size.
	 * @return Base point size of this font.
	 */
	public int getPointSize() {
		return size;
	}
	
	/**
	 * Get copyright string.
	 * @return Copyright string for this font.
	 */
	public String getAbout() {
		return "'" + family + "' " + ((about == null) ? "" : about);
	}

	/**
	 * @return Is this font monospaced?
	 */
	private boolean isMonospaced() {
		Font font = getFont(1);
		AffineTransform atrans = null;
		if (font.isTransformed()) {
			atrans = font.getTransform();
		}
		FontRenderContext frc = new FontRenderContext(atrans, true, true);
		Rectangle2D b1 = font.getStringBounds("|", frc);
		Rectangle2D b2 = font.getStringBounds("W", frc);
		return (b1.getWidth() == b2.getWidth());
	}
	
	/**
	 * Fetch or calculate character cell size at the given size multiple.
	 * @param scale Multiplier of base point size.
	 * @return Pixel dimensions of character cell at the requested size.
	 */
	public Dimension getCellSize(int scale) {
		Dimension cellSize = sizes.get(scale);
		if (cellSize == null) {
			Rectangle2D cell = getCell(scale);
//			System.out.println("Cell: " + cell);
			cellSize = new Dimension();
			cellSize.width = (int)Math.round(cell.getWidth() + 0.5);
			cellSize.height = (int)Math.round(cell.getHeight() + 0.5);
			sizes.put(scale, cellSize);
//			System.out.println("Cell size: " + cellSize + " (y offset " + getYOffset(scale) + ")");
		}
		return cellSize;
	}
	
	/**
	 * Calculate the character cell size at the given size multiple.
	 * @param scale Multiplier of base point size.
	 * @return
	 */
	private Rectangle2D getCell(int scale) {
		Font font = getFont(scale);
		AffineTransform atrans = null;
		if (font.isTransformed()) {
			atrans = font.getTransform();
		}
		FontRenderContext frc = new FontRenderContext(atrans, true, true);
		return font.getMaxCharBounds(frc);
	}
	
	/**
	 * Load a font from a file in the .jar file and register it for later use.
	 * @param file Path to the font file in the .jar file.
	 * @return Font family name.
	 */
	protected static String registerFont(String file) {
		try {
			// access .ttf file in .jar
			InputStream is = JScreenFont.class.getClassLoader().getResourceAsStream(file);
			// create 1pt font
			Font font = Font.createFont(Font.TRUETYPE_FONT, is);
			// register font
			GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
			return font.getFamily();
		} catch (FontFormatException | IOException e) {
			throw new RuntimeException("Unable to load font " + file);
		}
	}
	
	/**
	 * Fetch or create a font at the given size multiple.
	 * @param scale Multiplier of base point size.
	 * @return Font at the requested size.
	 */
	private Font getFont(int scale) {
		Font font = fonts.get(scale);
		if (font == null) {
			font = new Font(family, Font.PLAIN, size * scale);
			fonts.put(scale, font);
		}
		return font;
	}
	
	/**
	 * Fetch or calculate baseline offset at the given size multiple.
	 * @param scale Multiplier of base point size.
	 * @return Offset for the requested size.
	 */
	private int getYOffset(int scale) {
		Integer offset = offsets.get(scale);
		if (offset == null) {
			Rectangle2D cell = getCell(scale);
			// Offset between the bottom of the cell and the font baseline is cell.h + cell.y.
			// For the PC BIOS font at scale 2, that is 15.991211 + (-13.99231) = 1.998901 (or 2).
			offset = (int)Math.round(cell.getHeight() + cell.getY());
			offsets.put(scale, offset);
		}
		return offset;
	}
	
	/**
	 * Render a character on screen.
	 * @param g Graphics object to render to.
	 * @param bounds Bounds of character cell in g.
	 * @param palette List of available colors, and default FG and BG colors.
	 * @param cell Data for character cell to render.
	 * @param scale Multiplier of base point size.
	 */
	public void drawChar(Graphics g, Rectangle bounds, JScreenPalette palette, JScreenCell cell, int scale) {
		Color fg = palette.getFG(cell);
		Color bg = palette.getBG(cell);
		if ((cell.attrs != null) && cell.attrs.contains(Attr._IS_SELECTED)) {
			Color tmp = fg;
			fg = bg;
			bg = tmp;
		}
		g.setColor(bg);
		g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
		if (FONT_DEBUG) {
			// draw a box around the cell for testing
			Color boxColor = (((bounds.x/bounds.width + bounds.y/bounds.height) % 2) == 0) ? Color.BLUE : Color.GRAY;
			g.setColor(boxColor);
			g.drawRect(bounds.x, bounds.y, bounds.width - 1, bounds.height - 1);
		}
		// We only draw the character if it's not blinking, or it is blinking and is not currently blinked.
		if (!cell.attrs.contains(Attr.BLINKING) || !cell.attrs.contains(Attr._IS_BLINKED)) {
			// Also, only if the foreground color is different from the background
			if (!fg.equals(bg)) {
				g.setFont(getFont(scale));
				g.setColor(fg);
				if (antiAlias) {
					((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				}
				// Because drawChars references the font baseline, and we are getting the coords
				// of the top left of the cell, we need to add to get the bottom, then subtract 
				// the difference between the bottom and the base.
				g.drawChars(new char[] {cell.ch}, 0, 1, bounds.x, bounds.y + bounds.height - getYOffset(scale));
			}
		}
	}
}
