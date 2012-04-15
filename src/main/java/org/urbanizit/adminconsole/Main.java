package org.urbanizit.adminconsole;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.rest.graphdb.RestGraphDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.urbanizit.adminconsole.core.Urbanizer;


public class Main {
	private static final Logger logger = LoggerFactory.getLogger(Main.class);
	private GraphDatabaseService restGraphDb;
	private static final String HOSTNAME = "localhost";
	private static final int PORT = 7474;
	private static final String SERVER_ROOT = "http://" + HOSTNAME + ":" + PORT;
	private static final String SERVER_ROOT_URI = SERVER_ROOT + "/db/data/";

	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("Usage : java -jar urbanizit-adminconsole.jar path_to_directory_to_import");
			System.exit(0);
		}
		logger.info("run with parameter {}", args[0]);
		Main main= new Main();
		
		main.run(Paths.get(args[0]));

		boolean quit = false;
		while (!quit) {
			System.out.print("Quit ? [y/n] : ");

			String in = null;
			try {
				BufferedReader input = new BufferedReader(
						new InputStreamReader(System.in));
				in = input.readLine();
			} catch (IOException err) {
				System.err.println(err.getMessage());
				System.exit(0);
			}
			quit = "y".equals(in);
		}
		main.stop();
	}

	public void run(Path directoryToImport) {
		this.initDB();
		Urbanizer urb = new Urbanizer(getDB());
		try {
			urb.populate(directoryToImport);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	private void initDB()  {
		if(getDB()==null) {
			setDB(new RestGraphDatabase(SERVER_ROOT_URI));
			if(getDB() !=null) {
				logger.info("Connected to neo4j database ({})", SERVER_ROOT_URI);
			} else {
				throw new NullPointerException("The neo4j database ("+SERVER_ROOT_URI+") is not reachable");
			}
			registerShutdownHook(getDB());
		}
	}
	
	private GraphDatabaseService getDB() {
		return restGraphDb;
	}
	
	private void setDB(GraphDatabaseService db) {
		this.restGraphDb = db;
	}

	private void stop() {
		restGraphDb.shutdown();
	}

	private static void registerShutdownHook(final GraphDatabaseService gdb) {
		// Registers a shutdown hook for the Neo4j instance so that it
		// shuts down nicely when the VM exits (even if you "Ctrl-C" the
		// running example before it's completed)
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				gdb.shutdown();
			}
		});
	}
}
