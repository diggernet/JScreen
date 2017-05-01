
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

The JScreenDemo and DisplayANSI sample programs show off some more of what JScreen can do.
And, of course, the source is available to learn every nuance.


## Dependencies
* Java 8
* [net.digger.util.Delay](https://gitlab.com/digger.net/Java/Utils)

## License
JScreen is provided under the terms of the GNU LGPLv3.
