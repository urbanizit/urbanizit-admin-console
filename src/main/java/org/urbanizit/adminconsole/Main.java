package org.urbanizit.adminconsole;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Nicolas Geraud
 */
public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);


    public static void main(String... args) {
        CLI cli = new CLI(args);
        cli.execute();

        /*
           *
           *
           *
           *
           *
           *
           * if (args.length != 1) { System.err.println("Usage : java -jar
           * urbanizit-adminconsole.jar path_to_directory_to_import");
           * System.exit(0); } logger.debug("run with parameter {}", args[0]);
           * Main main= new Main();
           *
           * main.run(Paths.get(args[0]));
           *
           * boolean quit = false; while (!quit) { System.out.print("Quit ? [y/n]
           * : ");
           *
           * String in = null; try(BufferedReader input= new BufferedReader(new
           * InputStreamReader(System.in))) { in = input.readLine(); } catch
           * (IOException err) { System.err.println(err.getMessage());
           * System.exit(0); } quit = "y".equals(in); } main.stop();
           */
    }
}