package net.digger.ui.screen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import net.digger.ui.screen.charmap.JScreenCharMap;
import net.digger.ui.screen.color.Attr;
import net.digger.ui.screen.color.JScreenPalette;
import net.digger.ui.screen.cursor.JScreenCursor;
import net.digger.ui.screen.font.JScreenFont;
import net.digger.ui.screen.io.JScreenKeyboard;
import net.digger.ui.screen.io.JScreenSound;
import net.digger.ui.screen.mode.JScreenMode;
import net.digger.ui.screen.protocol.PlainText;
import net.digger.ui.screen.protocol.JScreenTextProtocol;
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
 * Java component to create a text screen interface.
 * @author walton
 */
public class JScreen implements Closeable {
	private static final String VERSION = "1.2.0";
	private static final String COPYRIGHT = "\u00A92017";
	// default values
	private static final String DEFAULT_WINDOW_TITLE = "JScreen";
	private static final JScreenMode DEFAULT_SCREEN_MODE = JScreenMode.DEFAULT_MODE;
	
	// ui component
	private final JScreenComponent screen;
	
	// context menu
	private JPopupMenu menu = null;
	private JMenu scaleMenu = null;
	private JMenu fontMenu = null;
	
	// screen backing array
	// this array is referenced by [y][x] to simplify scrolling
	private JScreenCell[][] cells;
	
	// screen dimensions (in chars)
	private Rectangle screenCells;
	// screen dimensions (in pixels)
	private Rectangle screenPixels;
	
	// cell dimensions (in pixels)
	private Dimension cellSize;
	
	// current screen colors and attributes
	private JScreenPalette palette;
	private int fgColor;
	private int bgColor;
	private EnumSet<Attr> attrs = EnumSet.noneOf(Attr.class);
	private ScrollFillMethod scrollFillMethod = ScrollFillMethod.DEFAULT;
	
	// screen font
	private JScreenFont[] fonts;
	private int font;
	private int fontScale;
	private int maxFontScale = 1;
	
	// current text window bounds (in chars)
	private Rectangle window;
	
	// cursor renderer
	private JScreenCursor cursorRenderer;
	// current cursor position (in chars, relative to full screen)
	private Point cursor = new Point(0, 0);
	// show/hide cursor
	private boolean cursorVisible = true;
	// is the cursor blinking?
	private boolean cursorBlink = true;
	private ScheduledFuture<?> cursorBlinker = null;
	
	// interprets text protocol (ANSI, AVATAR, etc)
	private JScreenTextProtocol protocol;
	// used to map between Unicode and other character sets
	private JScreenCharMap charMap;
	
	// screen effects
	private boolean scanLines = false;
	private ScheduledThreadPoolExecutor scheduler = null;
	private boolean blinkingChars = false;
	private boolean blinked = false;
	private Point selectionStarted = null;
	private Rectangle selection = null;
	
	// key event handler
	public final JScreenKeyboard keyboard;
	
	// sound handler
	public final JScreenSound sound;
	
	public enum ScrollFillMethod {
		/**
		 * Fill new cells with default colors.
		 */
		DEFAULT,
		/**
		 * Fill new cells with current colors.
		 */
		CURRENT,
	};
	
	// ##### Constructors #####

	/**
	 * Create a new JScreen instance using default screen mode.
	 */
	public JScreen() {
		this(null, null);
	}
	
	/**
	 * Create a new JScreen instance using the given screen mode.
	 * @param mode Screen mode to use.
	 */
	public JScreen(JScreenMode mode) {
		this(mode, null);
	}
	
	/**
	 * Create a new JScreen instance using the given screen mode, 
	 * and adding the given copyright message to the context menu.
	 * @param mode Screen mode to use.
	 * @param copyright Copyright notice to display.
	 */
	public JScreen(JScreenMode mode, String copyright) {
		mode = (mode == null) ? DEFAULT_SCREEN_MODE : mode;
		screen = new JScreenComponent(this::paintScreen);
		setScreenMode(mode);
		setTextProtocol(new PlainText(this));
		
		screen.setEnabled(true);
		screen.setFocusable(true);
		screen.setFocusTraversalKeysEnabled(false);
		
		keyboard = new JScreenKeyboard(this);
		sound = new JScreenSound();
		
		screen.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				switch (e.getClickCount()) {
					case 3:
						doTripleClick(e);
						break;
					case 2:
						doDoubleClick(e);
						break;
					default:
						doClick(e);
						break;
				}
			}
			
			private void doClick(MouseEvent e) {
				selectionStarted = null;
				clearSelection();
			}
			
			private void doDoubleClick(MouseEvent e) {
				Point clicked = findCell(e.getPoint());
				int startX = clicked.x;
				int endX = clicked.x;
				int y = clicked.y;
				boolean space = (getCellChar(startX, y) == ' ');
				do {
					startX--;
				} while ((startX >= 0) && checkChar(getCellChar(startX, y), space));
				startX++;
				do {
					endX++;
				} while ((endX < screenCells.width) && checkChar(getCellChar(endX, y), space));
				endX--;
				selectCells(new Rectangle(startX, y, (endX - startX) + 1, 1));
			}
			
			private void doTripleClick(MouseEvent e) {
				Point clicked = findCell(e.getPoint());
				int startX = 0;
				int endX = screenCells.width - 1;
				int y = clicked.y;
				selectCells(new Rectangle(startX, y, (endX - startX) + 1, 1));
			}
			
			private boolean checkChar(char ch, boolean space) {
				return space ? (ch == ' ') : (ch != ' ');
			}

			@Override
			public void mousePressed(MouseEvent e) {
				selectionStarted = findCell(e.getPoint());
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				selectionStarted = null;
			}
		});
		
		screen.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				if (selectionStarted != null) {
					Point cell = findCell(e.getPoint());
					selectCells(new Rectangle(
							Math.min(selectionStarted.x, cell.x),
							Math.min(selectionStarted.y, cell.y),
							Math.abs(cell.x - selectionStarted.x) + 1,
							Math.abs(cell.y - selectionStarted.y) + 1)
					);
				}
			}
		});
		
		// right-click context menu
		menu = new JPopupMenu();
		screen.setComponentPopupMenu(menu);
		if (copyright != null) {
			menu.add(new JMenuItem(copyright));
		}
		menu.add(new JMenuItem("JScreen v" + VERSION + " " + COPYRIGHT + " by David Walton"));
		
		JMenuItem copy = new JMenuItem("Copy selection text to clipboard");
		menu.add(copy);
		copy.addActionListener((ActionEvent e) -> {
			copySelectionToClipboard();
		});
		JMenuItem paste = new JMenuItem("Paste text to keyboard buffer");
		menu.add(paste);
		paste.addActionListener((ActionEvent e) -> {
			if (keyboard != null) {
				keyboard.pasteClipboard();
			}
		});
		
		fontMenu = new JMenu("Fonts");
		menu.add(fontMenu);
		addFontMenus();
		scaleMenu = new JMenu("Font Scale");
		menu.add(scaleMenu);
		addFontScaleMenus();
		
		// blink text
		startBlinker(mode.blinkRate);
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				close();
			}
		});
	}
	

	private void startBlinker(double blinkRate) {
		if (scheduler == null) {
			scheduler = new ScheduledThreadPoolExecutor(1);
			scheduler.setRemoveOnCancelPolicy(true);
		}
		if (cursorBlinker != null) {
			cursorBlinker.cancel(false);
		}
		cursorBlinker = scheduler.scheduleAtFixedRate(() -> {
			// toggle the state of the blink
			blinked = !blinked;
			if (blinkingChars) {
				boolean found = false;
				for (int y=0; y<screenCells.height; y++) {
					for (int x=0; x<screenCells.width; x++) {
						JScreenCell cell = cells[y][x];
						if (cell.attrs.contains(Attr.BLINKING)) {
							found = true;
							cell.setAttr(Attr._IS_BLINKED, blinked);
							screen.repaint(cellPixels(x, y));
						}
					}
				}
				if (!found) {
					// if no blinking chars found, stop checking
					// anywhere that updates a cell to blink needs to turn this on
					blinkingChars = false;
				}
			}
			if (cursorVisible && cursorBlink) {
				JScreenCell cell = cells[cursor.y][cursor.x];
				cell.setAttr(Attr._IS_BLINKED, blinked);
				screen.repaint(cellPixels(cursor));
			}
		}, 0, (int)(1000 / blinkRate), TimeUnit.MILLISECONDS);
	}
	
	/**
	 * Call when done with the JScreen component, to clean up resources.
	 */
	@Override
	public void close() {
		if ((scheduler != null) && !scheduler.isShutdown()) {
			scheduler.shutdownNow();
		}
	}


	// ##### Window creation methods #####
	
	/**
	 * Create a new UI window containing a JScreen instance.
	 * @return New JScreen instance.
	 */
	public static JScreen createJScreenWindow() {
		return createJScreenWindow(DEFAULT_WINDOW_TITLE, null, null);
	}
	
	/**
	 * Create a new UI window containing a JScreen instance,
	 * using the given window title.
	 * @param title Window title to use.
	 * @return New JScreen instance.
	 */
	public static JScreen createJScreenWindow(String title) {
		return createJScreenWindow(title, null, null);
	}

	/**
	 * Create a new UI window containing a JScreen instance,
	 * using the given window title and screen mode.
	 * @param title Window title to use.
	 * @param mode Screen mode to use.
	 * @return New JScreen instance.
	 */
	public static JScreen createJScreenWindow(String title, JScreenMode mode) {
		return createJScreenWindow(title, mode, null);
	}

	/**
	 * Create a new UI window containing a JScreen instance,
	 * using the given window title, screen mode and context menu copyright message.
	 * @param title Window title to use.
	 * @param mode Screen mode to use.
	 * @param copyright Copyright notice to display.
	 * @return New JScreen instance.
	 */
	public static JScreen createJScreenWindow(String title, JScreenMode mode, String copyright) {
		JScreen screen = new JScreen(mode, copyright);
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame(title);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setLayout(new BorderLayout());
			frame.setResizable(false);
			frame.add(screen.getComponent(), BorderLayout.CENTER);
			frame.pack();
			frame.setLocationRelativeTo(null);	// centers window on screen
			frame.setLocationByPlatform(true);	// for me, doesn't seem to do anything
			frame.setVisible(true);
		});
		return screen;
	}
	
	
	// ##### Text color methods #####
	
	/**
	 * Set foreground and background colors to default, and clear text attributes.
	 */
	public void resetTextColors() {
		setFGColor(palette.defaultFG);
		setBGColor(palette.defaultBG);
		this.attrs = createAttrSet((Attr[])null);
	}
	
	/**
	 * Set new foreground color, background color, and text attributes in one call.
	 * @param fg Foreground color palette index.
	 * @param bg Background color palette index.
	 * @param attrs Optional display attributes.
	 */
	public void setTextColors(int fg, int bg, Attr... attrs) {
		setFGColor(fg);
		setBGColor(bg);
		this.attrs = createAttrSet(attrs);
	}
	
	/** 
	 * Set new foreground color.
	 * @param fg Foreground color palette index.
	 */
	public void setFGColor(int fg) {
		this.fgColor = fg;
	}
	
	/**
	 * Set new background color.
	 * @param bg Background color palette index.
	 */
	public void setBGColor(int bg) {
		this.bgColor = bg;
	}
	
	/**
	 * Get current foreground and background colors.
	 * @return Array of colors: [fg, bg]
	 */
	public int[] getTextColors() {
		return new int[] { fgColor, bgColor };
	}
	
	/**
	 * Get current foreground color.
	 * @return Foreground color palette index.
	 */
	public int getTextFGColor() {
		return fgColor;
	}
	
	/**
	 * Get current background color.
	 * @return Background color palette index.
	 */
	public int getTextBGColor() {
		return bgColor;
	}

	/**
	 * Get foreground and background colors at point relative to current text window.
	 * @param coord Character cell position in current text window.
	 * @return Array of colors: [fg, bg]
	 */
	public int[] getCellColors(Point coord) {
		JScreenCell cell = getWindowCell(coord);
		return new int[] { cell.fg, cell.bg };
	}
	
	/**
	 * Get foreground and background colors at point relative to current text window.
	 * @param x X position in current text window.
	 * @param y Y position in current text window.
	 * @return Array of colors: [fg, bg]
	 */
	public int[] getCellColors(int x, int y) {
		JScreenCell cell = getWindowCell(x, y);
		return new int[] { cell.fg, cell.bg };
	}
	
	/**
	 * Get foreground color at point relative to current text window.
	 * @param coord Character cell position in current text window.
	 * @return Foreground color palette index.
	 */
	public int getCellFGColor(Point coord) {
		return getWindowCell(coord).fg;
	}

	/**
	 * Get foreground color at point relative to current text window.
	 * @param x X position in current text window.
	 * @param y Y position in current text window.
	 * @return Foreground color palette index.
	 */
	public int getCellFGColor(int x, int y) {
		return getWindowCell(x, y).fg;
	}

	/**
	 * Get background color at point relative to current text window.
	 * @param coord Character cell position in current text window.
	 * @return Background color palette index.
	 */
	public int getCellBGColor(Point coord) {
		return getWindowCell(coord).bg;
	}
	
	/**
	 * Get background color at point relative to current text window.
	 * @param x X position in current text window.
	 * @param y Y position in current text window.
	 * @return Background color palette index.
	 */
	public int getCellBGColor(int x, int y) {
		return getWindowCell(x, y).bg;
	}
	
	/**
	 * Set how new cells are filled when scrolling up or down.
	 * @param method Fill method to use.
	 */
	public void setScrollFillMethod(ScrollFillMethod method) {
		scrollFillMethod = method;
	}
	
	// ##### Text attribute methods #####
	
	/**
	 * Turn the given text attribute on or off.
	 * @param attr Display attribute to set.
	 * @param on Turn it on or off.
	 */
	public void setTextAttr(Attr attr, boolean on) {
		if (on) {
			attrs.add(attr);
		} else {
			attrs.remove(attr);
		}
	}
	
	/**
	 * Toggle the state of the given text attribute.
	 * @param attr Display attribute to toggle.
	 */
	public void toggleTextAttr(Attr attr) {
		if (attrs.contains(attr)) {
			attrs.remove(attr);
		} else {
			attrs.add(attr);
		}
	}
	
	/**
	 * Get the state of the given text attribute.
	 * @param attr Display attribute to check.
	 * @return Current state of attribute.
	 */
	public boolean getTextAttr(Attr attr) {
		return attrs.contains(attr);
	}
	
	/**
	 * Clears all text attributes.
	 */
	public void clearTextAttrs() {
		attrs.clear();
	}
	
	/**
	 * Turn the given text attribute at the given window-relative coordinate on or off.
	 * @param coord Character cell position in current text window.
	 * @param attr Display attribute to set.
	 * @param on Turn it on or off.
	 */
	public void setCellAttr(Point coord, Attr attr, boolean on) {
		setCellAttr(coord.x, coord.y, attr, on);
	}
	
	/**
	 * Turn the given text attribute at the given window-relative coordinate on or off.
	 * @param x X position in current text window.
	 * @param y Y position in current text window.
	 * @param attr Display attribute to set.
	 * @param on Turn it on or off.
	 */
	public void setCellAttr(int x, int y, Attr attr, boolean on) {
		JScreenCell cell = getWindowCell(x, y);
		cell.setAttr(attr, on);
		if ((attr == Attr.BLINKING) && on) {
			blinkingChars = true;
		}
		screen.repaint(cellPixels(x, y));
	}
	
	/**
	 * Toggles the state of the given text attribute at the given window-relative coordinate.
	 * @param coord Character cell position in current text window.
	 * @param attr Display attribute to toggle.
	 */
	public void toggleCellAttr(Point coord, Attr attr) {
		toggleCellAttr(coord.x, coord.y, attr);
	}
	
	/**
	 * Toggles the state of the given text attribute at the given window-relative coordinate.
	 * @param x X position in current text window.
	 * @param y Y position in current text window.
	 * @param attr Display attribute to toggle.
	 */
	public void toggleCellAttr(int x, int y, Attr attr) {
		JScreenCell cell = getWindowCell(x, y);
		cell.toggleAttr(attr);
		if (cell.attrs.contains(Attr.BLINKING)) {
			blinkingChars = true;
		}
		screen.repaint(cellPixels(x, y));
	}
	
	/**
	 * Gets the state of the given text attribute at the given window-relative coordinate.
	 * @param coord Character cell position in current text window.
	 * @param attr Display attribute to check.
	 * @return Current state of attribute.
	 */
	public boolean getCellAttr(Point coord, Attr attr) {
		return getWindowCell(coord).attrs.contains(attr);
	}

	/**
	 * Gets the state of the given text attribute at the given window-relative coordinate.
	 * @param x X position in current text window.
	 * @param y Y position in current text window.
	 * @param attr Display attribute to check.
	 * @return Current state of attribute.
	 */
	public boolean getCellAttr(int x, int y, Attr attr) {
		return getWindowCell(x, y).attrs.contains(attr);
	}

	/**
	 * Create an EnumSet of attributes from varargs or an array of Attrs.
	 * @param attrs Zero or more attributes to add to the set.
	 * @return EnumSet of attributes.
	 */
	private EnumSet<Attr> createAttrSet(Attr... attrs) {
		EnumSet<Attr> set = EnumSet.noneOf(Attr.class);
		if (attrs != null) {
			for (Attr attr : attrs) {
				if (attr != null) {
					set.add(attr);
				}
			}
		}
		return set;
	}

	// ##### Screen mode methods #####
	
	/**
	 * Set a new screen mode.
	 * @param mode Screen mode to use.
	 */
	public void setScreenMode(JScreenMode mode) {
		charMap = mode.charMap;
		scanLines = mode.scanLines;
		cursorRenderer = mode.cursor;
		palette = mode.palette;
		setTextColors(palette.defaultFG, palette.defaultBG);
		setTextFonts(mode.font);
		setTextScreenSize(new Dimension(mode.width, mode.height));
		startBlinker(mode.blinkRate);
	}
	
	// ##### Screen size methods #####
	
	/**
	 * Get current screen dimensions (in chars).
	 * @return Screen dimensions in characters.
	 */
	public Dimension getTextScreenSize() {
		return new Dimension(screenCells.getSize());
	}
	
	/**
	 * Get current screen dimensions (in pixels).
	 * @return Screen dimensions in pixels.
	 */
	public Dimension getTextScreenPixels() {
		return new Dimension(screenPixels.getSize());
	}
	
	/**
	 * Set new screen dimensions (in chars).
	 * @param size New screen dimensions in characters.
	 */
	public void setTextScreenSize(Dimension size) {
		screenCells = new Rectangle(size);
//		System.out.println("Screen size: " + screenCells.getSize());
		window = new Rectangle(screenCells);
		cells = JScreenRegion.createCellGrid(screenCells.getSize());
		setFontScale();
		clearScreen();
	}
	
	// ##### Text font methods #####

	/**
	 * Set fonts to be available for text.
	 * All fonts MUST have the same cell size!
	 * @param fonts Fonts to make available.
	 */
	public void setTextFonts(JScreenFont... fonts) {
		if (ArrayUtils.isEmpty(fonts)) {
			throw new IllegalArgumentException("Must provide at least one font!");
		}
		this.fonts = fonts;
		for (int i=0; i<fonts.length; i++) {
			System.out.println("Font " + i + " family: " + fonts[i].getFamily());
		}
		addFontMenus();
		setFontScale();
	}
	
	/**
	 * Selects the index of the font to use for future text.
	 * If there are no fonts loaded, or the index is out of range, does nothing.
	 * @param font Index of font to use.
	 */
	public void setTextFont(int font) {
		if (ArrayUtils.isNotEmpty(fonts) && (font >= 0) && (font < fonts.length)) {
			this.font = font;
		}
	}
	
	/**
	 * Get current text font index.
	 * @return Index of font in use.
	 */
	public int getTextFont() {
		return font;
	}

	/**
	 * Get font index at point relative to current text window.
	 * @param coord Character cell position in current text window.
	 * @return Index of font in use.
	 */
	public int getCellFont(Point coord) {
		JScreenCell cell = getWindowCell(coord);
		return cell.font;
	}

	/**
	 * Get font index at point relative to current text window.
	 * @param x X position in current text window.
	 * @param y Y position in current text window.
	 * @return Index of font in use.
	 */
	public int getCellFont(int x, int y) {
		JScreenCell cell = getWindowCell(x, y);
		return cell.font;
	}

	/**
	 * Set the largest font scaling factor to fit on screen.
	 */
	public void setFontScale() {
		if ((screenCells == null) || ArrayUtils.isEmpty(fonts)) {
			return;
		}
		setMaxFontScale();
		setFontScale(maxFontScale - 1);
	}

	/**
	 * Determines the largest font scale which will fit on screen, and sets maxFontScale to that value + 1;
	 */
	private void setMaxFontScale() {
		if ((screenCells == null) || ArrayUtils.isEmpty(fonts)) {
			return;
		}
		// Calculate font scale based on screen dimensions
		// or should this use Toolkit.getDefaultToolkit().getScreenSize()?
		Rectangle bounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		Dimension size = null;
		// Get the cell size for scale 1, and ensure it is the same for all fonts.
		for (JScreenFont font : fonts) {
			Dimension fsize = font.getCellSize(1);
			if (size == null) {
				size = fsize;
			} else {
				if (!size.equals(fsize)) {
					throw new IllegalArgumentException("All fonts must have the same cell size!");
				}
			}
		}
		int xScale = bounds.width / (screenCells.width * size.width);
		int yScale = bounds.height / (screenCells.height * size.height);
		maxFontScale = Math.max(1, Math.min(xScale, yScale)) + 1;
		addFontScaleMenus();
	}
	
	/**
	 * Set the font scaling factor to use.
	 * @param scale Font scale to use.
	 */
	public void setFontScale(int scale) {
		if ((screenCells == null) || ArrayUtils.isEmpty(fonts)) {
			return;
		}
		fontScale = scale;
		cellSize = fonts[0].getCellSize(fontScale);
		screenPixels = new Rectangle(screenCells.width * cellSize.width, screenCells.height * cellSize.height);
//		System.out.println("Screen pixels: " + screenPixels);
		addFontScaleMenus();
		setPreferredSize();
	}
	
	// ##### Cursor methods #####
	
	/**
	 * Set the cursor to the given coordinates relative to the current text window.
	 * @param x X position in current text window.
	 * @param y Y position in current text window.
	 */
	public void setCursor(int x, int y) {
		setAbsCursor(windowCoordToScreen(x, y));
	}

	/**
	 * Set the cursor to the given point relative to the current text window.
	 * @param coord Character cell position in current text window.
	 */
	public void setCursor(Point coord) {
		setAbsCursor(windowPointToScreen(coord));
	}
	
	/**
	 * Get the current cursor position, relative to the current text window.
	 * @return Current cursor position.
	 */
	public Point getCursor() {
		return new Point(cursor.x - window.x, cursor.y - window.y);
	}
	
	/**
	 * Make the cursor visible.
	 */
	public void showCursor() {
		cursorVisible = true;
	}
	
	/**
	 * Make the cursor hidden.
	 */
	public void hideCursor() {
		cursorVisible = false;
	}
	
	/**
	 * Set whether cursor blinks or not.
	 * @param blink Set blink on or off.
	 */
	public void blinkCursor(boolean blink) {
		cursorBlink = blink;
	}
	
	/**
	 * Advances the cursor in the current text window.
	 */
	private void advanceCursor() {
		int newX = cursor.x + 1;
		if (newX >= (window.x + window.width)) {
			carriageReturn();
			lineFeed();
		} else {
			setAbsCursor(newX, cursor.y);
		}
	}

	/**
	 * Perform a carriage return.
	 */
	public void carriageReturn() {
		setAbsCursor(window.x, cursor.y);
	}

	/**
	 * Perform a line feed.
	 */
	public void lineFeed() {
		int newY = cursor.y + 1;
		if (newY >= (window.y + window.height)) {
			scrollWindowUp();
			newY--;
		}
		setAbsCursor(cursor.x, newY);
	}
	
	/**
	 * Perform a backspace.
	 */
	public void backspace() {
		if (cursor.x == window.x) {
			// Already at the left edge of current text window.  Do nothing.
			return;
		}
		// Somewhere in a line.  Back up.
		int newX = cursor.x - 1;
		// Remove the backspaced-over char.
		cells[cursor.y][newX].ch = ' ';
		cells[cursor.y][newX].attrs.clear();
		setAbsCursor(newX, cursor.y);
	}

	/**
	 * Set the cursor to the given screen-relative point.
	 * Refreshes old and new cursor cells.
	 * Anything which moves the cursor should use this or setAbsCursor(x, y).
	 * @param cursor Character cell position in screen.
	 */
	private void setAbsCursor(Point cursor) {
		setAbsCursor(cursor.x, cursor.y);
	}
	
	/**
	 * Set the cursor to the given screen-relative coordinates.
	 * Refreshes old and new cursor cells.
	 * Anything which moves the cursor should use this or setAbsCursor(cursor).
	 * @param x X position in screen.
	 * @param y Y position in screen.
	 */
	private void setAbsCursor(int x, int y) {
		int oldX = cursor.x;
		int oldY = cursor.y;
		cursor.x = x;
		cursor.y = y;
		if (cursorVisible) {
			screen.repaint(cellPixels(oldX, oldY));
			screen.repaint(cellPixels(cursor));
		}
	}
	
	// ##### Text window methods #####
	
	/**
	 * Set bounds for the current text window, and moves cursor to 0, 0.
	 * @param left X position of left side of text window in screen.
	 * @param top Y position of top of text window in screen.
	 * @param width Width of text window in screen.
	 * @param height Height of text window in screen.
	 */
	public void setWindow(int left, int top, int width, int height) {
		setTextWindow(left, top, width, height);
		setCursor(0, 0);
	}

	/**
	 * Set bounds for the current text window, and moves cursor to 0, 0.
	 * @param region Bounds of text window in screen.
	 */
	public void setWindow(Rectangle region) {
		setTextWindow(region.x, region.y, region.width, region.height);
		setCursor(0, 0);
	}

	/**
	 * Set bounds for the current text window.
	 * @param left X position of left side of text window in screen.
	 * @param top Y position of top of text window in screen.
	 * @param width Width of text window in screen.
	 * @param height Height of text window in screen.
	 */
	private void setTextWindow(int left, int top, int width, int height) {
		if ((width < 1) || (height < 1)) {
			throw new IllegalArgumentException("Window must be at least 1x1.");
		}
		Rectangle region = new Rectangle(left, top, width, height);
		checkRegionInScreen(region);
		window = region;
	}
	
	/**
	 * Move and resize the current text window.
	 * This does not move the window contents!
	 * If the move or resize would result in the cursor being outside the window,
	 * the cursor is moved to 0,0 in the new window.
	 * Otherwise, cursor position relative to the whole screen stays the same.
	 * @param dx Columns to move left (negative) or right (positive).
	 * @param dy Rows to move up (negative) or down (positive).
	 * @param dw Columns to increase or decrease width.
	 * @param dh Rows to increase or decrease height.
	 */
	public void adjustWindow(int dx, int dy, int dw, int dh) {
		setTextWindow(window.x + dx, window.y + dy, window.width + dw, window.height + dh);
		if (!window.contains(cursor)) {
			setCursor(0, 0);
		}
	}
	
	/**
	 * Move the current text window on the screen.  
	 * Width and height stay the same.
	 * This does not move the window contents!
	 * If the move would result in the cursor being outside the window,
	 * the cursor is moved to 0,0 in the new window.
	 * Otherwise, cursor position relative to the whole screen stays the same.
	 * @param dx Columns to move left (negative) or right (positive).
	 * @param dy Rows to move up (negative) or down (positive).
	 */
	public void moveWindow(int dx, int dy) {
		adjustWindow(dx, dy, 0, 0);
	}
	
	/**
	 * Resize the current text window.  Upper left corner doesn't move.
	 * If the resize would result in the cursor being outside the window,
	 * the cursor is moved to 0,0 in the new window.
	 * Otherwise, cursor position relative to the whole screen stays the same.
	 * @param dw Columns to increase or decrease width.
	 * @param dh Rows to increase or decrease height.
	 */
	public void resizeWindow(int dw, int dh) {
		adjustWindow(0, 0, dw, dh);
	}
	
	/**
	 * Get the current text window.
	 * @return Bounds of current text window in screen.
	 */
	public Rectangle getWindow() {
		return new Rectangle(window);
	}
	
	/**
	 * Save and return the current text window state.
	 * @return Current text window state.
	 */
	public JScreenWindowState saveWindowState() {
		JScreenWindowState state = new JScreenWindowState();
		state.window = getWindow();
		state.cursor = getCursor();
		state.cursorVisible = cursorVisible;
		state.cursorBlink = cursorBlink;
		state.font = font;
		state.fgColor = fgColor;
		state.bgColor = bgColor;
		state.attrs = EnumSet.copyOf(attrs);
		return state;
	}
	
	/**
	 * Restore the given text window state.
	 * @param state Text window state to restore.
	 */
	public void restoreWindowState(JScreenWindowState state) {
		setWindow(state.window);
		setCursor(state.cursor);
		cursorVisible = state.cursorVisible;
		cursorBlink = state.cursorBlink;
		font = state.font;
		fgColor = state.fgColor;
		bgColor = state.bgColor;
		attrs = EnumSet.copyOf(state.attrs);
	}
	
	/**
	 * Draw a frame around the current text window, and shrink the window to fit in the frame.
	 * Title string and frame will be printed with current text colors and attributes, so set those
	 * before calling this method.
	 * If the framing would result in the cursor being outside the window,
	 * the cursor is moved to 0,0 in the new window.
	 * Otherwise, cursor position relative to the whole screen stays the same.
	 * Frame characters is an array of characters used to draw the frame, in this order:
	 * Upper left corner, top, upper right corner, left side, right side,
	 * lower left corner, bottom, lower right corner.
	 * The frame character array can optionally have two additional characters to bracket the title:
	 * Before title, after title.
	 * @param title Title string.
	 * @param frame Characters to use to draw the frame.
	 */
	public void frameWindow(String title, char[] frame) {
		frameWindow(title, frame, null, null, (Attr[])null);
	}
	
	/**
	 * Draw a frame around the current text window, and shrink the window to fit in the frame.
	 * Title string will be printed with current text colors and attributes, so set those
	 * before calling this method.  Frame will be drawn using the provided colors and attributes,
	 * or the current colors and attributes if none are given.
	 * If the framing would result in the cursor being outside the window,
	 * the cursor is moved to 0,0 in the new window.
	 * Otherwise, cursor position relative to the whole screen stays the same.
	 * Frame characters is an array of characters used to draw the frame, in this order:
	 * Upper left corner, top, upper right corner, left side, right side,
	 * lower left corner, bottom, lower right corner.
	 * The frame character array can optionally have two additional characters to bracket the title:
	 * Before title, after title.
	 * @param title Title string.  If null, no title is displayed.
	 * @param frame Characters to use to draw the frame.  If null, no frame is drawn.
	 * @param frameFG Window frame foreground color.
	 * @param frameBG Window frame background color.
	 * @param frameAttrs Window frame text attributes.
	 */
	public void frameWindow(String title, char[] frame, Integer frameFG, Integer frameBG, Attr... frameAttrs) {
		Point coord = new Point(0, 0);
		
		if (frame != null) {
			if (frameFG == null) {
				frameFG = fgColor;
			}
			if (frameBG == null) {
				frameBG = bgColor;
			}
			if (frameAttrs == null) {
				frameAttrs = attrs.toArray(new Attr[0]);
			}
			
			// upper left corner
			putChar(coord, frame[0], frameFG, frameBG, frameAttrs);
			// top line
			coord.x++;
			putChars(coord, frame[1], window.width - 2, frameFG, frameBG, frameAttrs);
			// upper right corner
			coord.x = window.width - 1;
			putChar(coord, frame[2], frameFG, frameBG, frameAttrs);
		}
		// title
		if (title != null) {
			if ((frame != null) && (frame.length > 9)) {
				title = StringUtils.substring(title, 0, window.width - 4);
				coord.x = (window.width - title.length()) / 2;
				putChar(coord.x - 1, coord.y, frame[8], frameFG, frameBG, frameAttrs);
				putStr(coord, title);
				putChar(coord.x + title.length(), coord.y, frame[9], frameFG, frameBG, frameAttrs);
			} else {
				title = StringUtils.substring(title, 0, window.width - 2);
				coord.x = (window.width - title.length()) / 2;
				putStr(coord, title);
			}
		}
		
		if (frame != null) {
			// left and right side lines
			for (int y=1; y<window.height; y++) {
				coord.y = y;
				coord.x = 0;
				putChar(coord, frame[3], frameFG, frameBG, frameAttrs);
				coord.x = window.width - 1;
				putChar(coord, frame[4], frameFG, frameBG, frameAttrs);
			}
			
			// lower left corner
			coord.y = window.height - 1;
			coord.x = 0;
			putChar(coord, frame[5], frameFG, frameBG, frameAttrs);
			// bottom line
			coord.x++;
			putChars(coord, frame[6], window.width - 2, frameFG, frameBG, frameAttrs);
			// lower right corner
			coord.x = window.width - 1;
			putChar(coord, frame[7], frameFG, frameBG, frameAttrs);
		}

		adjustWindow(1, 1, -2, -2);
	}
	
	/**
	 * Enlarge the current text window to remove the frame.
	 * The cursor position relative to the whole screen stays the same.
	 */
	public void unframeWindow() {
		adjustWindow(-1, -1, 2, 2);
	}

	/**
	 * Returns a new Point representing the given window-relative cell coordinates as absolute screen coordinates.
	 * @param coord Character cell position in current text window.
	 * @return Character cell position in screen.
	 */
	private Point windowPointToScreen(Point coord) {
		return windowCoordToScreen(coord.x, coord.y);
	}

	/**
	 * Returns a new Point representing the given window-relative cell coordinates as absolute screen coordinates.
	 * @param x X position in current text window.
	 * @param y Y position in current text window.
	 * @return Character cell position in screen.
	 */
	private Point windowCoordToScreen(int x, int y) {
		Point coord = new Point(window.x + x, window.y + y);
		checkCellInWindow(coord);
		return coord;
	}

	/**
	 * Returns a new Rectangle representing the given window-relative cell region as an absolute screen region.
	 * @param region Bounds of region in current text window.
	 * @return Bounds of region in screen.
	 */
	private Rectangle windowRegionToScreen(Rectangle region) {
		region = new Rectangle(region);
		region.translate(window.x, window.y);
		checkRegionInWindow(region);
		return region;
	}

	// ##### Text character methods #####
	
	/**
	 * Puts the given char at the given screen-relative coordinates.
	 * When repeating a char, printing stops at the right edge of the current text window.
	 * Does not move the cursor.
	 * @param coord Coordinates relative to whole screen.
	 * @param ch Character to place.
	 * @param count Number of times to print the character.
	 * @param fg Foreground color to use.
	 * @param bg Background color to use.
	 * @param attrs Character attributes to use.
	 */
	private void putCellChar(Point coord, char ch, int count, int fg, int bg, EnumSet<Attr> attrs) {
		if (charMap != null) {
			ch = charMap.mapChar(ch);
		}
		for (int i=0; i<count; i++) {
			int x = coord.x + i;
			if (x > (window.x + window.width - 1)) {
				break;
			}
			JScreenCell cell = cells[coord.y][x];
			cell.fg = fg;
			cell.bg = bg;
			cell.ch = ch;
			cell.font = font;
			cell.setAttrs(attrs);
			screen.repaint(cellPixels(x, coord.y));
		}
		if (attrs.contains(Attr.BLINKING)) {
			blinkingChars = true;
		}
	}
	
	/**
	 * Repeats the given char at the given window-relative coordinates.
	 * When repeating a char, printing stops at the right edge of the current text window.
	 * Does not move the cursor.
	 * @param coord Coordinates relative to current text window.
	 * @param ch Character to place.
	 * @param count Number of times to print the character.
	 * @param fg Foreground color to use.
	 * @param bg Background color to use.
	 * @param attrs Character attributes to use.
	 */
	public void putChars(Point coord, char ch, int count, int fg, int bg, Attr... attrs) {
		EnumSet<Attr> set = createAttrSet(attrs);
		putCellChar(windowPointToScreen(coord), ch, count, fg, bg, set);
	}
	
	/**
	 * Repeats the given char at the given window-relative coordinates.
	 * When repeating a char, printing stops at the right edge of the current text window.
	 * Does not move the cursor.
	 * @param x X coordinate relative to current text window.
	 * @param y Y coordinate relative to current text window.
	 * @param ch Character to place.
	 * @param count Number of times to print the character.
	 * @param fg Foreground color to use.
	 * @param bg Background color to use.
	 * @param attrs Character attributes to use.
	 */
	public void putChars(int x, int y, char ch, int count, int fg, int bg, Attr... attrs) {
		EnumSet<Attr> set = createAttrSet(attrs);
		putCellChar(windowCoordToScreen(x, y), ch, count, fg, bg, set);
	}
	
	/**
	 * Puts the given char at the given window-relative coordinates.
	 * Does not move the cursor.
	 * @param coord Coordinates relative to current text window.
	 * @param ch Character to place.
	 * @param fg Foreground color to use.
	 * @param bg Background color to use.
	 * @param attrs Character attributes to use.
	 */
	public void putChar(Point coord, char ch, int fg, int bg, Attr... attrs) {
		EnumSet<Attr> set = createAttrSet(attrs);
		putCellChar(windowPointToScreen(coord), ch, 1, fg, bg, set);
	}
	
	/**
	 * Puts the given char at the given window-relative coordinates.
	 * Does not move the cursor.
	 * @param x X coordinate relative to current text window.
	 * @param y Y coordinate relative to current text window.
	 * @param ch Character to place.
	 * @param fg Foreground color to use.
	 * @param bg Background color to use.
	 * @param attrs Character attributes to use.
	 */
	public void putChar(int x, int y, char ch, int fg, int bg, Attr... attrs) {
		EnumSet<Attr> set = createAttrSet(attrs);
		putCellChar(windowCoordToScreen(x, y), ch, 1, fg, bg, set);
	}
	
	/**
	 * Puts the given char at the given window-relative coordinates using current
	 * foreground color, background color, and attributes.
	 * Does not move the cursor.
	 * @param coord Coordinates relative to current text window.
	 * @param ch Character to place.
	 */
	public void putChar(Point coord, char ch) {
		putCellChar(windowPointToScreen(coord), ch, 1, fgColor, bgColor, attrs);
	}
	
	/**
	 * Puts the given char at the given window-relative coordinates using current
	 * foreground color, background color, and attributes.
	 * Does not move the cursor.
	 * @param x X coordinate relative to current text window.
	 * @param y Y coordinate relative to current text window.
	 * @param ch Character to place.
	 */
	public void putChar(int x, int y, char ch) {
		putCellChar(windowCoordToScreen(x, y), ch, 1, fgColor, bgColor, attrs);
	}
	
	/**
	 * Puts the given char at the current cursor position.
	 * Advances the cursor.
	 * @param ch Character to place.
	 * @param fg Foreground color to use.
	 * @param bg Background color to use.
	 * @param attrs Character attributes to use.
	 */
	public void putChar(char ch, int fg, int bg, Attr... attrs) {
		EnumSet<Attr> set = createAttrSet(attrs);
		putCellChar(cursor, ch, 1, fg, bg, set);
		advanceCursor();
	}
	
	/**
	 * Puts the given char at the current cursor position using current
	 * foreground color, background color, and attributes.
	 * Advances the cursor.
	 * @param ch Character to place.
	 */
	public void putChar(char ch) {
		putCellChar(cursor, ch, 1, fgColor, bgColor, attrs);
		advanceCursor();
	}
	
	/**
	 * Gets the char at the given window-relative coordinates.
	 * @param coord Coordinates relative to current text window.
	 * @return Character from cell.
	 */
	public char getWindowCellChar(Point coord) {
		return getWindowCellChar(coord.x, coord.y);
	}

	/**
	 * Gets the char at the given window-relative coordinates.
	 * @param x X coordinate relative to current text window.
	 * @param y Y coordinate relative to current text window.
	 * @return Character from cell.
	 */
	public char getWindowCellChar(int x, int y) {
		char ch = getWindowCell(x, y).ch;
		if (charMap != null) {
			ch = charMap.unmapChar(ch);
		}
		return ch;
	}
	
	/**
	 * Gets the char at the given screen-relative coordinates.
	 * @param coord Coordinates relative to whole screen.
	 * @return Character from cell.
	 */
	public char getCellChar(Point coord) {
		return getCellChar(coord.x, coord.y);
	}

	/**
	 * Gets the char at the given screen-relative coordinates.
	 * @param x X coordinate relative to whole screen.
	 * @param y Y coordinate relative to whole screen.
	 * @return Character from cell.
	 */
	public char getCellChar(int x, int y) {
		char ch = getCell(x, y).ch;
		if (charMap != null) {
			ch = charMap.unmapChar(ch);
		}
		return ch;
	}


	/**
	 * Puts the given string at the given window-relative coordinates.
	 * Printing stops at the right edge of the current text window.
	 * Does not move the cursor.
	 * @param coord Coordinates relative to current text window.
	 * @param str String to place.
	 * @param fg Foreground color to use.
	 * @param bg Background color to use.
	 * @param attrs Character attributes to use.
	 */
	public void putStr(Point coord, String str, int fg, int bg, Attr... attrs) {
		putStr(coord.x, coord.y, str, fg, bg, attrs);
	}
	
	/**
	 * Puts the given string centered in the current text window on the current line.
	 * Printing stops at the right edge of the text window.
	 * Does not move the cursor.
	 * @param str String to place.
	 * @param fg Foreground color to use.
	 * @param bg Background color to use.
	 * @param attrs Character attributes to use.
	 */
	public void putStrCentered(String str, int fg, int bg, Attr... attrs) {
		str = StringUtils.substring(str, 0, window.width);
		int x = (window.width - str.length()) / 2;
		putStr(x, cursor.y - window.y, str, fg, bg, attrs);
	}
	
	/**
	 * Puts the given string at the given window-relative coordinates.
	 * Printing stops at the right edge of the current text window.
	 * Does not move the cursor.
	 * @param x X coordinate relative to current text window.
	 * @param y Y coordinate relative to current text window.
	 * @param str String to place.
	 * @param fg Foreground color to use.
	 * @param bg Background color to use.
	 * @param attrs Character attributes to use.
	 */
	public void putStr(int x, int y, String str, int fg, int bg, Attr... attrs) {
		if (StringUtils.isEmpty(str)) {
			return;
		}
		Point coord = windowCoordToScreen(x, y);
		EnumSet<Attr> set = createAttrSet(attrs);
		for (int i=0; i<str.length(); i++) {
			putCellChar(coord, str.charAt(i), 1, fg, bg, set);
			coord.x++;
			if (coord.x > (window.x + window.width - 1)) {
				break;
			}
		}
	}
	
	/**
	 * Puts the given string at the given window-relative coordinates using current
	 * foreground color, background color, and attributes.
	 * Printing stops at the right edge of the current text window.
	 * Does not move the cursor.
	 * @param coord Coordinates relative to current text window.
	 * @param str String to place.
	 */
	public void putStr(Point coord, String str) {
		putStr(coord.x, coord.y, str);
	}
	
	/**
	 * Puts the given string centered in the current text window on the current line
	 * using current foreground color, background color, and attributes.
	 * Printing stops at the right edge of the text window.
	 * Does not move the cursor.
	 * @param str String to place.
	 */
	public void putStrCentered(String str) {
		putStrCentered(str, fgColor, bgColor, attrs.toArray(new Attr[0]));
	}
	
	/**
	 * Puts the given string at the given window-relative coordinates using current
	 * foreground color, background color, and attributes.
	 * Printing stops at the right edge of the current text window.
	 * Does not move the cursor.
	 * @param x X coordinate relative to current text window.
	 * @param y Y coordinate relative to current text window.
	 * @param str String to place.
	 */
	public void putStr(int x, int y, String str) {
		putStr(x, y, str, fgColor, bgColor, attrs.toArray(new Attr[0]));
	}
	
	/**
	 * Puts the given string at the current cursor position.
	 * Advances the cursor.
	 * @param str String to place.
	 * @param fg Foreground color to use.
	 * @param bg Background color to use.
	 * @param attrs Character attributes to use.
	 */
	public void putStr(String str, int fg, int bg, Attr... attrs) {
		if (StringUtils.isEmpty(str)) {
			return;
		}
		EnumSet<Attr> set = createAttrSet(attrs);
		for (int i=0; i<str.length(); i++) {
			putCellChar(cursor, str.charAt(i), 1, fg, bg, set);
			advanceCursor();
		}
	}
	
	/**
	 * Puts the given string at the current cursor position using current
	 * foreground color, background color, and attributes.
	 * Advances the cursor.
	 * @param str String to place.
	 */
	public void putStr(String str) {
		if (StringUtils.isEmpty(str)) {
			return;
		}
		for (int i=0; i<str.length(); i++) {
			putChar(str.charAt(i));
		}
	}
	
	/**
	 * Prints the given char at the current cursor position using current
	 * foreground color, background color, and attributes.  
	 * The text to be output is processed by the current text protocol handler.
	 * @param ch Character to print.
	 */
	public void print(char ch) {
		if (protocol == null) {
			putChar(ch);
		} else {
			protocol.print(ch);
		}
	}

	/**
	 * Prints the given objects at the current cursor position using current
	 * foreground color, background color, and attributes.
	 * The text to be output is processed by the current text protocol handler.
	 * @param params Objects to print.
	 */
	public void print(Object... params) {
		for (Object param : params) {
			if (param == null) {
				continue;
			}
			print(param.toString());
		}
	}
	
	/**
	 * Prints the given string at the current cursor position using current
	 * foreground color, background color, and attributes.
	 * The text to be output is processed by the current text protocol handler.
	 * @param str String to print.
	 */
	public void print(String str) {
		if (StringUtils.isEmpty(str)) {
			return;
		}
		for (int i=0; i<str.length(); i++) {
			print(str.charAt(i));
		}
	}
	
	/**
	 * Prints a formatted string using the specified format string and arguments.
	 * 
	 * @param format Format string to use.
	 * @param args Arguments for format string.
	 */
	public void printf(String format, Object... args) {
		print(String.format(format, args));
	}
	
	/**
	 * Prints the given objects at the current cursor position using current
	 * foreground color, background color, and attributes.
	 * The text to be output is processed by the current text protocol handler.
	 * The string is followed with CRLF.
	 * @param params Objects to print.
	 */
	public void println(Object... params) {
		for (Object param : params) {
			print(param.toString());
		}
		println();
	}
	
	/**
	 * Prints the given string at the current cursor position using current
	 * foreground color, background color, and attributes.
	 * The text to be output is processed by the current text protocol handler.
	 * The string is followed with CRLF.
	 * @param str String to print.
	 */
	public void println(String str) {
		print(str);
		println();
	}

	/**
	 * Prints CRLF at the current cursor position.
	 */
	public void println() {
		print("\r\n");
	}
	
	/**
	 * Prints the given objects at the given bps, at the current cursor position using current
	 * foreground color, background color, and attributes.
	 * The text to be output is processed by the current text protocol handler.
	 * @param bps BPS for printing.
	 * @param params Objects to print.
	 */
	public void printBPS(int bps, Object... params) {
		for (Object param : params) {
			if (param == null) {
				continue;
			}
			printBPS(bps, param.toString());
		}
	}
	
	/**
	 * Prints the given string at the given bps, at the current cursor position using current
	 * foreground color, background color, and attributes.
	 * The text to be output is processed by the current text protocol handler.
	 * @param bps BPS for printing.
	 * @param str String to print.
	 */
	public void printBPS(int bps, String str) {
		// pause between displaying chars to simulate modem speed
		// microseconds = 1,000,000us/s / cps
		// cps = bps / 8
		int wait = (int)(1000000 / (bps / 8.0));
		for (int i=0; i<str.length(); i++) {
			print(str.charAt(i));
			Pause.micro(wait);
		}
	}

	/**
	 * Prints the given objects at the given bps, at the current cursor position using current
	 * foreground color, background color, and attributes.
	 * The text to be output is processed by the current text protocol handler.
	 * The string is followed with CRLF.
	 * @param bps BPS for printing.
	 * @param params Objects to print.
	 */
	public void printlnBPS(int bps, Object... params) {
		for (Object param : params) {
			printBPS(bps, param.toString());
		}
		printlnBPS(bps);
	}
	
	/**
	 * Prints the given string at the given bps, at the current cursor position using current
	 * foreground color, background color, and attributes.
	 * The text to be output is processed by the current text protocol handler.
	 * The string is followed with CRLF.
	 * @param bps BPS for printing.
	 * @param str String to print.
	 */
	public void printlnBPS(int bps, String str) {
		printBPS(bps, str);
		printlnBPS(bps);
	}
	
	/**
	 * Prints CRLF at the given bps, at the current cursor position.
	 * @param bps BPS for printing.
	 */
	public void printlnBPS(int bps) {
		printBPS(bps, "\r\n");
	}
	
	// ##### Screen region filling methods #####
	
	/**
	 * Fill the screen.
	 * @param ch Character to fill with, or null to preserve existing characters.
	 * @param fg Foreground color to use, or null to preserve existing FG.
	 * @param bg Background color to use, or null to preserve existing BG.
	 * @param attrs Text attributes to use.  Omit to clear existing attributes.  Use (Attr[])null to preserve existing attributes.
	 */
	public void fillScreen(Character ch, Integer fg, Integer bg, Attr... attrs) {
		fillCells(screenCells, ch, fg, bg, attrs);
	}
	
	/**
	 * Fill the current text window.
	 * @param ch Character to fill with, or null to preserve existing characters.
	 * @param fg Foreground color to use, or null to preserve existing FG.
	 * @param bg Background color to use, or null to preserve existing BG.
	 * @param attrs Text attributes to use.  Omit to clear existing attributes.  Use (Attr[])null to preserve existing attributes.
	 */
	public void fillWindow(Character ch, Integer fg, Integer bg, Attr... attrs) {
		fillCells(window, ch, fg, bg, attrs);
	}
	
	/**
	 * Fill the given window-relative region.
	 * @param region Bounds of region in current text window.
	 * @param ch Character to fill with, or null to preserve existing characters.
	 * @param fg Foreground color to use, or null to preserve existing FG.
	 * @param bg Background color to use, or null to preserve existing BG.
	 * @param attrs Text attributes to use.  Omit to clear existing attributes.  Use (Attr[])null to preserve existing attributes.
	 */
	public void fillRegion(Rectangle region, Character ch, Integer fg, Integer bg, Attr... attrs) {
		fillCells(windowRegionToScreen(region), ch, fg, bg, attrs);
	}
	
	/**
	 * Fill the given screen-relative region.
	 * @param region Bounds of region in screen.
	 * @param ch Character to fill with, or null to leave existing characters.
	 * @param fg Foreground color to use, or null to leave existing FG.
	 * @param bg Background color to use, or null to leave existing BG.
	 * @param attrs Text attributes to use.
	 */
	private void fillCells(Rectangle region, Character ch, Integer fg, Integer bg, Attr... attrs) {
		if ((region.width < 1) || (region.height < 1)) {
			// nothing to do
			return;
		}
		for (int y=region.y; y<(region.y + region.height); y++) {
			for (int x=region.x; x<(region.x + region.width); x++) {
				JScreenCell cell = cells[y][x];
				if (ch != null) {
					cell.ch = ch;
					cell.font = font;
				}
				if (fg != null) {
					cell.fg = fg;
				}
				if (bg != null) {
					cell.bg = bg;
				}
				if (attrs != null) {
					cell.setAttrs(attrs);
				}
			}
		}
		if (ArrayUtils.contains(attrs, Attr.BLINKING)) {
			blinkingChars = true;
		}
		screen.repaint(regionPixels(region));
	}
	
	// ##### Screen region clearing methods #####

	/**
	 * Clear the screen, using current foreground and background colors.
	 * Also sets the current text window to the full screen and clears the current text attributes.
	 */
	public void clearScreen() {
		setWindow(screenCells);
		clearTextAttrs();
		clearWindow();
	}
	
	/**
	 * Clear the current text window, using current foreground and background colors.
	 * Sets cursor at upper left corner.
	 */
	public void clearWindow() {
		clearCells(window);
		setCursor(0, 0);
	}
	
	/**
	 * Clear the given window-relative region, using current foreground and background colors.
	 * @param region Bounds of region in current text window.
	 */
	public void clearRegion(Rectangle region) {
		clearCells(windowRegionToScreen(region));
	}
	
	/**
	 * Clear the current line, within the current text window, using current foreground and background colors.
	 */
	public void clearLine() {
		clearCells(new Rectangle(window.x, cursor.y, window.width, 1));
	}
	
	/**
	 * Clear the current line from the cursor to the end of the line, within the current text window, using current foreground and background colors.
	 */
	public void clearToEOL() {
		clearCells(new Rectangle(cursor.x, cursor.y, (window.x + window.width) - cursor.x, 1));
	}
	
	/**
	 * Clear the current line from the cursor to the beginning of the line, within the current text window, using current foreground and background colors.
	 */
	public void clearToBOL() {
		clearCells(new Rectangle(window.x, cursor.y, (cursor.x - window.x) + 1, 1));
	}
	
	/**
	 * Clear from cursor to beginning of current text window, using current foreground and background colors.
	 */
	public void clearToTop() {
		clearToBOL();
		clearCells(new Rectangle(window.x, window.y, window.width, cursor.y - window.y));
	}
	
	/**
	 * Clear from cursor to end of current text window, using current foreground and background colors.
	 */
	public void clearToBottom() {
		clearToEOL();
		clearCells(new Rectangle(window.x, cursor.y + 1, window.width, ((window.y + window.height) - 1) - cursor.y));
	}
	
	/**
	 * Clear the given screen-relative region, using current foreground and background colors.
	 * @param region Bounds of region in screen.
	 */
	private void clearCells(Rectangle region) {
		fillCells(region, ' ', fgColor, bgColor);
	}
	
	// ##### Screen region reading methods #####

	/**
	 * Return the data from all the character cells in the screen.
	 * @return Contents of screen.
	 */
	public JScreenRegion readScreen() {
		return readCells(screenCells);
	}
	
	/**
	 * Return the data from all the character cells in the current text window.
	 * @return Contents of current text window.
	 */
	public JScreenRegion readWindow() {
		return readCells(window);
	}
	
	/**
	 * Return the data from all the character cells in the given window-relative region.
	 * @param region Bounds of region in current text window.
	 * @return Contents of region in current text window.
	 */
	public JScreenRegion readRegion(Rectangle region) {
		return readCells(windowRegionToScreen(region));
	}
	
	/**
	 * Return the data from all the character cells in the given screen-relative region.
	 * @param region Bounds of region in screen.
	 * @return Contents of region in screen.
	 */
	private JScreenRegion readCells(Rectangle region) {
		JScreenRegion data = new JScreenRegion(region.getSize());
		if ((region.width < 1) || (region.height < 1)) {
			// nothing to do
			return data;
		}
		for (int sy=region.y; sy<(region.y + region.height); sy++) {
			for (int sx=region.x; sx<(region.x + region.width); sx++) {
				int dx = sx - region.x;
				int dy = sy - region.y;
				JScreenCell src = cells[sy][sx];
				JScreenCell dest = data.cells[dy][dx];
				dest.ch = src.ch;
				dest.font = src.font;
				dest.fg = src.fg;
				dest.bg = src.bg;
				dest.setAttrs(src.attrs);
			}
		}
		return data;
	}
	
	// ##### Screen region writing methods #####

	/**
	 * Write previously-read data to all the character cells in the screen.
	 * @param data Contents for screen.
	 */
	public void writeScreen(JScreenRegion data) {
		writeCells(screenCells, data);
	}
	
	/**
	 * Write previously-read data to all the character cells in the current text window.
	 * @param data Contents for current text window.
	 */
	public void writeWindow(JScreenRegion data) {
		writeCells(window, data);
	}
	
	/**
	 * Write previously-read data to all the character cells in given window-relative region.
	 * @param region Bounds of region in current text window.
	 * @param data Contents for region in current text window.
	 */
	public void writeRegion(Rectangle region, JScreenRegion data) {
		writeCells(windowRegionToScreen(region), data);
	}
	
	/**
	 * Write previously-read data to all the character cells in given screen-relative region.
	 * @param region Bounds of region in screen.
	 * @param data Contents for region in screen.
	 */
	private void writeCells(Rectangle region, JScreenRegion data) {
		int bottom = region.y + Math.min(region.height, data.size.height);
		int right = region.x + Math.min(region.width, data.size.width);
		for (int dy=region.y; dy<bottom; dy++) {
			for (int dx=region.x; dx<right; dx++) {
				int sx = dx - region.x;
				int sy = dy - region.y;
				JScreenCell src = data.cells[sy][sx];
				JScreenCell dest = cells[dy][dx];
				dest.ch = src.ch;
				dest.font = src.font;
				dest.fg = src.fg;
				dest.bg = src.bg;
				dest.setAttrs(src.attrs);
				if (dest.attrs.contains(Attr.BLINKING)) {
					blinkingChars = true;
				}
			}
		}
		screen.repaint(regionPixels(region));
	}
	
	// ##### Screen region scrolling methods #####
	
	/**
	 * Scroll the entire screen up one line.
	 */
	public void scrollScreenUp() {
		scrollCellsUp(screenCells);
	}
	
	/**
	 * Scroll the current text window up one line.
	 */
	public void scrollWindowUp() {
		scrollCellsUp(window);
	}
	
	/**
	 * Scroll the given window-relative region up one line.
	 * @param region Bounds of region in current text window to scroll.
	 */
	public void scrollRegionUp(Rectangle region) {
		scrollCellsUp(windowRegionToScreen(region));
	}
	
	/**
	 * Deletes the current cursor line within the current text window,
	 * and scrolls up the following window lines. 
	 */
	public void deleteLine() {
		scrollCellsUp(new Rectangle(window.x, cursor.y, window.width, window.height - (cursor.y - window.y)));
	}
	
	/**
	 * Scroll the given screen-relative region up one line.
	 * @param region Bounds of region in screen to scroll.
	 */
	private void scrollCellsUp(Rectangle region) {
		if (region.equals(screenCells)) {
			int bottom = screenCells.height - 1;
			for (int y=0; y<bottom; y++) {
				cells[y] = cells[y + 1];
			}
			cells[bottom] = JScreenRegion.createCellRow(screenCells.width);
			if (scrollFillMethod == ScrollFillMethod.CURRENT) {
				// Set new cells to current colors instead of default.
				clearCells(new Rectangle(0, bottom, screenCells.width, 1));
			}
		} else {
			if ((region.width < 1) || (region.height < 1)) {
				// nothing to do
				return;
			}
			int bottom = (region.y + region.height) - 1;
			int right = (region.x + region.width) - 1;
			for (int y=region.y; y<=bottom; y++) {
				JScreenCell[] newRow = new JScreenCell[0];
				if (region.x > 0) {
					newRow = ArrayUtils.addAll(newRow, ArrayUtils.subarray(cells[y], 0, region.x));
				}
				if (y == bottom) {
					newRow = ArrayUtils.addAll(newRow, JScreenRegion.createCellRow(region.width));
				} else {
					newRow = ArrayUtils.addAll(newRow, ArrayUtils.subarray(cells[y + 1], region.x, right + 1));
				}
				if (right < (screenCells.width - 1)) {
					newRow = ArrayUtils.addAll(newRow, ArrayUtils.subarray(cells[y], right + 1, screenCells.width));
				}
				cells[y] = newRow;
			}
			if (scrollFillMethod == ScrollFillMethod.CURRENT) {
				// Set new cells to current colors instead of default.
				clearCells(new Rectangle(region.x, bottom, region.width, 1));
			}
		}
		screen.repaint(regionPixels(region));
	}
	
	/**
	 * Scroll the entire screen down one line.
	 */
	public void scrollScreenDown() {
		scrollCellsDown(screenCells);
	}
	
	/**
	 * Scroll the current text window down one line.
	 */
	public void scrollWindowDown() {
		scrollCellsDown(window);
	}
	
	/**
	 * Scroll the given window-relative region down one line.
	 * @param region Bounds of region in current text window to scroll.
	 */
	public void scrollRegionDown(Rectangle region) {
		scrollCellsDown(windowRegionToScreen(region));
	}
	
	/**
	 * Inserts a new line at the current cursor line within the current text window,
	 * and scrolls down the following window lines. 
	 */
	public void insertLine() {
		scrollCellsDown(new Rectangle(window.x, cursor.y, window.width, window.height - (cursor.y - window.y)));
	}
	
	/**
	 * Scroll the given screen-relative region down one line.
	 * @param region Bounds of region in screen to scroll.
	 */
	private void scrollCellsDown(Rectangle region) {
		if (region.equals(screenCells)) {
			int bottom = screenCells.height - 1;
			for (int y=bottom; y>0; y--) {
				cells[y] = cells[y - 1];
			}
			cells[0] = JScreenRegion.createCellRow(screenCells.width);
			if (scrollFillMethod == ScrollFillMethod.CURRENT) {
				// Set new cells to current colors instead of default.
				clearCells(new Rectangle(0, 0, screenCells.width, 1));
			}
		} else {
			if ((region.width < 1) || (region.height < 1)) {
				// nothing to do
				return;
			}
			int bottom = (region.y + region.height) - 1;
			int right = (region.x + region.width) - 1;
			for (int y=bottom; y>=region.y; y--) {
				JScreenCell[] newRow = new JScreenCell[0];
				if (region.x > 0) {
					newRow = ArrayUtils.addAll(newRow, ArrayUtils.subarray(cells[y], 0, region.x));
				}
				if (y == region.y) {
					newRow = ArrayUtils.addAll(newRow, JScreenRegion.createCellRow(region.width));
				} else {
					newRow = ArrayUtils.addAll(newRow, ArrayUtils.subarray(cells[y + 1], region.x, right + 1));
				}
				if (right < (screenCells.width - 1)) {
					newRow = ArrayUtils.addAll(newRow, ArrayUtils.subarray(cells[y], right + 1, screenCells.width));
				}
				cells[y] = newRow;
			}
			if (scrollFillMethod == ScrollFillMethod.CURRENT) {
				// Set new cells to current colors instead of default.
				clearCells(new Rectangle(region.x, region.y, region.width, 1));
			}
		}
		screen.repaint(regionPixels(region));
	}
	
	// ##### Screen region selection methods #####
	
	/**
	 * Select the text in the whole screen.
	 */
	public void selectScreen() {
		selectCells(screenCells);
	}
	
	/**
	 * Select the text in the current text window.
	 */
	public void selectWindow() {
		selectCells(window);
	}
	
	/**
	 * Select the text in the given window-relative region.
	 * @param region Bounds of region in current text window to select.
	 */
	public void selectRegion(Rectangle region) {
		selectCells(windowRegionToScreen(region));
	}

	/**
	 * Select the text in the given screen-relative region.
	 * @param region Bounds of region in screen to select.
	 */
	public void selectCells(Rectangle region) {
		Point ul = new Point(Math.max(screenCells.x, region.x), Math.max(screenCells.y, region.y));
		Point lr = new Point(Math.min(screenCells.width, region.x + region.width),
				Math.min(screenCells.height, region.y + region.height));
		Rectangle oldSelection = selection;
		selection = new Rectangle(ul.x, ul.y, lr.x - ul.x, lr.y - ul.y);
		if (oldSelection != null) {
			screen.repaint(regionPixels(oldSelection));
		}
		screen.repaint(regionPixels(selection));
	}
	
	/**
	 * Copy the selected text to clipboard and remove the selection.
	 */
	public void copySelectionToClipboard() {
		if (selection != null) {
			List<String> text = new ArrayList<>();
			StringBuilder sb = new StringBuilder();
			for (int y=selection.y; y<(selection.y + selection.height); y++) {
				sb.setLength(0);
				for (int x=selection.x; x<(selection.x + selection.width); x++) {
					sb.append(cells[y][x].ch);
				}
				text.add(sb.toString());
			}
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(new StringSelection(StringUtils.join(text, '\n')), null);
			clearSelection();
		}
	}
	
	/**
	 * Remove text selection.
	 */
	public void clearSelection() {
		Rectangle oldSelection = selection;
		selection = null;
		if (oldSelection != null) {
			screen.repaint(regionPixels(oldSelection));
		}
	}

	// ##### Miscellaneous methods #####
	
	/**
	 * Sets the protocol used to display text (plain text, ANSI, AVATAR, etc).
	 * @param protocol Text protocol to use.
	 */
	public void setTextProtocol(JScreenTextProtocol protocol) {
		this.protocol = protocol;
	}
	
	// ##### Rendering methods #####
	
	/**
	 * Used by the wrapped JScreenComponent to render the screen to its canvas.
	 * @param g Graphics context to use for display.
	 */
	private void paintScreen(Graphics g) {
		// Use the clip bounds to determine what cells to render
		Rectangle bounds = g.getClipBounds();
		// make a copy of the location, because we'll be modifying it
		Point coords = new Point(bounds.getLocation());
		Point ulCell = findCell(coords);
		ulCell.x = Math.max(0, ulCell.x);
		ulCell.y = Math.max(0, ulCell.y);
		coords.translate(bounds.width - 1, bounds.height - 1);
		Point lrCell = findCell(coords);
		lrCell.x = Math.min(lrCell.x, screenCells.width - 1);
		lrCell.y = Math.min(lrCell.y, screenCells.height - 1);

		// paint the cells
		for (int y=ulCell.y; y<=lrCell.y; y++) {
			for (int x=ulCell.x; x<=lrCell.x; x++) {
				Rectangle cellBounds = cellPixels(x, y);
				int font = cells[y][x].font;
				// if there is a font available...
				if ((font >= 0) && (font < fonts.length)) {
					// render the cell
					if ((selection != null) && selection.contains(x, y)) {
						cells[y][x].setAttr(Attr._IS_SELECTED, true);
						fonts[font].drawChar(g, cellBounds, palette, cells[y][x], fontScale);
						cells[y][x].setAttr(Attr._IS_SELECTED, false);
					} else {
						fonts[font].drawChar(g, cellBounds, palette, cells[y][x], fontScale);
					}
				} else {
					// otherwise, paint it BG color
					Color bg = palette.getBG(cells[y][x]);
					g.setColor(bg);
					g.fillRect(cellBounds.x, cellBounds.y, cellBounds.width, cellBounds.height);
				}
				if ((cursorRenderer != null) && cursorVisible && (cursor.x == x) && (cursor.y == y) 
						&& (!cursorBlink || !cells[y][x].attrs.contains(Attr._IS_BLINKED))) {
					// draw the cursor, if it is enabled, in this cell, and not blinking or not currently blinked
					cursorRenderer.drawCursor(g, cellBounds, palette.getFG(cells[y][x]), fontScale);
				}
			}
		}
		
		// optionally, paint scan lines
		if (scanLines && (fontScale > 1)) {
			g.setColor(Color.BLACK);
			Point ulCorner = cellOrigin(ulCell);
			Point lrCorner = cellOrigin(lrCell);
			lrCorner.translate(cellSize.width - 1, cellSize.height - 1);
			for (int y=ulCorner.y; y<=lrCorner.y; y++) {
				if ((y % fontScale) >= (fontScale / 2.0)) {
					g.drawLine(ulCorner.x, y, lrCorner.x, y);
				}
			}
		}
		// refresh the display to ensure smooth updates (not updating in chunks)
		Toolkit.getDefaultToolkit().sync();
	}
	
	/**
	 * Update the preferred size of the wrapped JScreenComponent, and repack its frame.
	 */
	private void setPreferredSize() {
		Dimension d = screenPixels.getSize();
		screen.setMinimumSize(d);
		screen.setPreferredSize(d);
		screen.setMaximumSize(d);
		JFrame frame = (JFrame)SwingUtilities.getRoot(screen);
		if (frame != null) {
			frame.pack();
		}
	}
	
	// ##### Cell and pixel conversion methods #####
	
	/**
	 * Get the character cell at the given coordinates, relative to the current text window.
	 * @param coord Character cell position in current text window.
	 * @return Cell contents.
	 */
	private JScreenCell getWindowCell(Point coord) {
		return getWindowCell(coord.x, coord.y);
	}

	/**
	 * Get the character cell at the given coordinates, relative to the current text window.
	 * @param x X position in current text window.
	 * @param y Y position in current text window.
	 * @return Cell contents.
	 */
	private JScreenCell getWindowCell(int x, int y) {
		Point coord = windowCoordToScreen(x, y);
		return cells[coord.y][coord.x];
	}
	
	/**
	 * Get the character cell at the given coordinates, relative to the whole screen.
	 * @param coord Character cell position in screen.
	 * @return Cell contents.
	 */
	private JScreenCell getCell(Point coord) {
		checkCellInScreen(coord);
		return cells[coord.y][coord.x];
	}

	/**
	 * Get the character cell at the given coordinates, relative to the whole screen.
	 * @param x X position in screen.
	 * @param y Y position in screen.
	 * @return Cell contents.
	 */
	private JScreenCell getCell(int x, int y) {
		return getCell(new Point(x, y));
	}

	/**
	 * Returns the pixel coordinates of the upper left corner of the given character cell.
	 * @param coord Character cell position in screen.
	 * @return Pixel coordinates of upper left corner of cell.
	 */
	private Point cellOrigin(Point coord) {
		return cellOrigin(coord.x, coord.y);
	}
	
	/**
	 * Returns the pixel coordinates of the upper left corner of the given character cell.
	 * @param x X position in screen.
	 * @param y Y position in screen.
	 * @return Pixel coordinates of upper left corner of cell.
	 */
	private Point cellOrigin(int x, int y) {
		return new Point(x * cellSize.width, y * cellSize.height);
	}
	
	/**
	 * Returns the pixel region of the given character cell.
	 * @param coord Character cell position in screen.
	 * @return Pixel region of character cell.
	 */
	private Rectangle cellPixels(Point coord) {
		return cellPixels(coord.x, coord.y);
	}
	
	/**
	 * Returns the pixel region of the given character cell.
	 * @param x X position in screen.
	 * @param y Y position in screen.
	 * @return Pixel region of character cell.
	 */
	private Rectangle cellPixels(int x, int y) {
		return new Rectangle(cellOrigin(x, y), cellSize);
	}
	
	/**
	 * Returns the pixel region of the given character cell region.
	 * @param region Bounds of character cell region in screen.
	 * @return Pixel region of character cell region.
	 */
	private Rectangle regionPixels(Rectangle region) {
		return regionPixels(region.x, region.y, region.width, region.height);
	}
	
	/**
	 * Returns the pixel region of the given character cell region.
	 * @param left X position of left side of character cell region in screen.
	 * @param top Y position of top of character cell region in screen.
	 * @param width Width of character cell region in screen.
	 * @param height Height of character cell region in screen.
	 * @return Pixel region of character cell region.
	 */
	private Rectangle regionPixels(int left, int top, int width, int height) {
		Point start = cellOrigin(left, top);
		Point end = cellOrigin(left + width - 1, top + height - 1);
		end.translate(cellSize.width, cellSize.height);
		return new Rectangle(start.x, start.y, (end.x - start.x) + 1, (end.y - start.y) + 1);
	}
	
	/**
	 * Returns the character cell at the given pixel coordinates.
	 * @param pixel Pixel coordinates in screen.
	 * @return Character cell position in screen.
	 */
	private Point findCell(Point pixel) {
		return findCell(pixel.x, pixel.y);
	}
	
	/**
	 * Returns the character cell at the given pixel coordinates.
	 * @param x X position of pixel in screen.
	 * @param y Y position of pixel in screen.
	 * @return Character cell position in screen.
	 */
	private Point findCell(int x, int y) {
		Point coord = new Point();
		coord.x = x / cellSize.width;
		coord.y = y / cellSize.height;
		return coord;
	}
	
	// ##### Bounds checking methods #####
	// These should be used whenever coordinates are received through a public API.
	
	/**
	 * Throws exception if the given cell is off-screen.
	 * @param coord Character cell position relative to screen.
	 */
	private void checkCellInScreen(Point coord) {
		if (!screenCells.contains(coord)) {
			throw new IllegalArgumentException("Cell " + coord + " is off-screen (" + screenCells + ").");
		}
	}
	
	/**
	 * Throws exception if the given (screen-relative) cell is outside of current window.
	 * @param coord Character cell position relative to screen.
	 */
	private void checkCellInWindow(Point coord) {
		if (!window.contains(coord)) {
			throw new IllegalArgumentException("Cell " + coord + " is outside of current window (" + window + ").");
		}
	}
	
	/**
	 * Throws exception if the given cell region is off-screen.
	 * @param region Bounds of character cell region relative to screen.
	 */
	private void checkRegionInScreen(Rectangle region) {
		if (!screenCells.contains(region)) {
			throw new IllegalArgumentException("Cell region " + region + " is not on-screen (" + screenCells + ").");
		}
	}
	
	/**
	 * Throws exception if the given (screen-relative) cell region is outside of current window.
	 * @param region Bounds of character cell region relative to screen.
	 */
	private void checkRegionInWindow(Rectangle region) {
		if (!window.contains(region)) {
			throw new IllegalArgumentException("Cell region " + region + " is outside of current window (" + window + ").");
		}
	}
	
	/**
	 * Throws exception if the given pixel is off-screen.
	 * @param pixel Pixel coordinates relative to screen.
	 */
	private void checkPixelInScreen(Point pixel) {
		if (!screenPixels.contains(pixel)) {
			throw new IllegalArgumentException("Pixel " + pixel + " is not on-screen (" + screenPixels + ").");
		}
	}

	// ##### UI component methods #####

	/**
	 * Returns the JScreen context menu, for custom UI tweaks.
	 * @return Popup menu.
	 */
	public JPopupMenu getContextMenu() {
		return menu;
	}

	/**
	 * Adds font scale options to the context menu.
	 */
	private void addFontScaleMenus() {
		if (scaleMenu != null) {
			scaleMenu.removeAll();
			ButtonGroup group = new ButtonGroup();
			for (int i=1; i<=maxFontScale; i++) {
				JRadioButtonMenuItem item = new JRadioButtonMenuItem("" + i + 'x');
				item.setSelected(fontScale == i);
				item.setActionCommand(String.valueOf(i));
				group.add(item);
				scaleMenu.add(item);
				item.addActionListener((ActionEvent e) -> {
					setFontScale(Integer.parseInt(e.getActionCommand()));
				});
			}
		}
	}

	/**
	 * Adds font copyright messages to the context menu.
	 */
	private void addFontMenus() {
		if (fontMenu != null) {
			fontMenu.removeAll();
			for (JScreenFont font : fonts) {
				fontMenu.add(new JMenuItem(font.getAbout()));
			}
		}
	}
	
	/**
	 * Returns a reference to the wrapped JScreenComponent, so you can add it to your custom UI.
	 * @return Component for the screen.
	 */
	public JScreenComponent getComponent() {
		return screen;
	}
	
	/**
	 * Actual component class, hidden here to avoid inheriting the whole JComponent API into JScreen.
	 */
	public class JScreenComponent extends JComponent {
		private static final long serialVersionUID = 8329461748149292559L;
		private final Consumer<Graphics> painter;

		/**
		 * Constructor.
		 * @param painter Method reference this component uses to paint itself.
		 */
		public JScreenComponent(Consumer<Graphics> painter) {
			this.painter = painter;
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			painter.accept(g);
		}
	}
}
