package at.iag.romboe.doppelte;

import java.io.IOException;

public class Main {

	public static void main(String[] args) {
		try {
			if (args.length != 2) {
				System.err.println("Usage: java -jar doppelte.jar [source-directory] [backup-directory]");
			}
			Processor p = new Processor(args[0], args[1]);
			p.go();
		}
		catch(IOException ioe) {
			System.err.println(ioe.getMessage());
		}
	}
}
