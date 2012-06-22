package org.urbanizit.adminconsole;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.urbanizit.adminconsole.core.Urbanizer;
import org.urbanizit.adminconsole.pojo.Map;

import java.util.List;

public class Main {

	private static final Logger logger = LoggerFactory.getLogger(Main.class);
	

	public static void main(String... args) {
		Options options = new Options();
		CmdLineParser parser = new CmdLineParser(options);
		parser.setUsageWidth(120);
		Main main = new Main();
		try {
			parser.parseArgument(args);

            if (options.isStart()) {
                Urbanizer.startDb();
            }
            else if (options.isStop()) {
                Urbanizer.stopDb();
            }
            else if (options.isInitialize()) {
                Urbanizer.initializeDb();
            }
			else if (options.isHelp()) {
				parser.printUsage(System.out);
			}
            else if (options.isListMaps()) {
				List<Map> maps = Urbanizer.listMaps();
				if(!maps.isEmpty()) {
					logger.info("ID		TYPE		NAME");
					logger.info("---------------------------------------");
					for(Map map : maps) {
						StringBuilder sb = new StringBuilder()
								.append(map.getId())
								.append("		")
								.append(map.getType())
								.append("		")
								.append(map.getName());
						logger.info(sb.toString());
					}
				}
				
			}
		} catch (CmdLineException e) {
			parser.printUsage(System.out);
		}




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