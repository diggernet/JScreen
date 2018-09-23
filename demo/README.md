# JScreen-Demo

A sampling of demo programs for [JScreen](../core).

The demos can be run from the Maven build artifact.  To see instructions, run:

	java -jar jscreen-demo-<version>.jar


## Hello World

Runs this minimal "Hello world" program:

	import net.digger.ui.screen.JScreen;
	
	public class HelloWorld {
		public static void main(String[] args) {
			JScreen screen = JScreen.createJScreenWindow();
			screen.print("Hello world.");
		}
	}


## JScreen Demo

Plays a short demo showing some of what JScreen can do.


## Display ANSI

A simple ANSI art viewer.


## License
JScreen-Demo is provided under the terms of the GNU Lesser General Public License v3.0 (LGPLv3).

