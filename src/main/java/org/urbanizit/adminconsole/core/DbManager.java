package org.urbanizit.adminconsole.core;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.rest.graphdb.RestGraphDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.urbanizit.adminconsole.Configuration;

/**
 *
 * @author nicolasger
 */
public class DbManager {
	private static final Logger logger = LoggerFactory.getLogger(DbManager.class);
	private static GraphDatabaseService graphDb;

	public static GraphDatabaseService getDB() {
		if (graphDb == null) {
            if(Configuration.isEmbedded()) {
                throw new RuntimeException("The neo4j database must be started !");
            } else {
                graphDb = new RestGraphDatabase(Configuration.getDatabaseURI());
                if (graphDb != null) {
                    logger.info("Connected to database ({})", Configuration.getDatabaseURI());
                } else {
                    throw new IllegalArgumentException("The neo4j database (" + Configuration.getDatabaseURI() + ") is not reachable");
                }
            }
		}
		return graphDb;
	}

    public static void start() {
        if(graphDb != null) {
            throw new RuntimeException("The neo4j database is already started");
        }

        if(Configuration.isEmbedded()) {
            graphDb = new EmbeddedGraphDatabase(Configuration.getDatabaseDirectory());
            logger.info("Database is started");
        } else {
            logger.warn("Urbanizit is configured to work with an external database");
        }
    }

    public static void shutdown() {
        if(graphDb == null) {
            throw new RuntimeException("The neo4j database is already stopped");
        }

        if(Configuration.isEmbedded()) {
            graphDb.shutdown();
            logger.info("Database is stopped");
        } else {
            logger.warn("Urbanizit is configured to work with an external database");
        }
    }

	private static void registerShutdownHook(final GraphDatabaseService gdb) {
		// Registers a shutdown hook for the Neo4j instance so that it
		// shuts down nicely when the VM exits (even if you "Ctrl-C" the
		// running example before it's completed)
		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {
				logger.info("shutdown database connection ...");
				gdb.shutdown();
			}
		});
	}
}
