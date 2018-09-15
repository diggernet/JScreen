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
			default:
				usage();
				break;
		}
	}
	
	private static void usage() {
		System.out.println();
		System.out.println("Run the JScreen demo programs.");
		System.out.println("Usage:");
		System.out.println("\tjava -jar jscreen-demo.jar (Demo|DisplayANSI)");
		System.out.println();
	}
}
