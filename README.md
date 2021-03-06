# JScreen
JScreen is a Java Swing component which provides a text screen display.

It was conceived to enable porting old DOS programs to a modern cross-platform
environment, while retaining their original look and feel.  But it was also
designed to enable non-PC text modes, given suitable fonts and character mapping.


## Hello World
Getting started is this simple:

	import net.digger.ui.screen.JScreen;
	
	public class HelloWorld {
		public static void main(String[] args) {
			JScreen screen = JScreen.createJScreenWindow();
			screen.print("Hello world.");
		}
	}

The sample programs in [JScreen-Demo](demo) show off some more of what JScreen can do.
And, of course, the source is available to learn every nuance.


## Modules
JScreen has the following modules:

* [JScreen](core) The main JScreen library.
* [JScreen-Demo](demo) Demo programs for JScreen.
* [JScreen-Fonts-IBM](fonts/IBM) IBM PC font pack for JScreen.
* [JScreen-Fonts-CBM](fonts/CBM) Commodore font pack for JScreen.


## License
JScreen is provided under the terms of the GNU Lesser General Public License v3.0 (LGPLv3).

