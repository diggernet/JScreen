package net.digger.ui;

import java.io.IOException;

import org.apache.commons.lang3.ArrayUtils;

public class DemoRunner {
	public static void main(String[] args) throws IOException {
		if (args.length < 1) {
			usage();
			return;
		}

		String demo = args[0].toLowerCase();
		args = ArrayUtils.remove(args, 0);
		switch (demo) {
			case "demo":
				JScreenDemo.main(args);
				break;
			case "displayansi":
				DisplayANSI.main(args);
				break;
			case "helloworld":
				HelloWorld.main(args);
				break;
			default:
				usage();
				break;
		}
	}
	
	private static void usage() {
		System.out.println();
		System.out.println("Run the JScreen demo programs.");
		System.out.println("Usage:");
		System.out.println("\tjava -jar jscreen-demo.jar (Demo|DisplayANSI|HelloWorld)");
		System.out.println("\t\tDemo: Plays a short demo showing some of what JScreen can do.");
		System.out.println("\t\tDisplayANSI: A simple ANSI art viewer.");
		System.out.println("\t\tHelloWorld: Runs a minimal Hello World program.");
		System.out.println();
		System.out.println("\tIf a demo program needs additional arguments, running it without");
		System.out.println("\targuments will display usage details for that demo.");
		System.out.println();
	}
}
