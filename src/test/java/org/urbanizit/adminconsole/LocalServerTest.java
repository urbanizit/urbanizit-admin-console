package org.urbanizit.adminconsole;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.server.NeoServer;
import org.neo4j.server.WrappingNeoServerBootstrapper;
import org.neo4j.server.configuration.Configurator;
import org.neo4j.server.configuration.EmbeddedServerConfigurator;
import org.neo4j.test.ImpermanentGraphDatabase;

import java.io.IOException;

/**
 * User: nicolasger
 * Date: 14/08/12
 * Time: 13:29
 */
public class LocalServerTest {
    protected static EmbeddedGraphDatabase graphDatabase;
    protected static NeoServer neoServer;

    @BeforeClass
    public static void start() throws IOException {
        graphDatabase = new ImpermanentGraphDatabase();
        EmbeddedServerConfigurator config = new EmbeddedServerConfigurator(graphDatabase);
        config.configuration().setProperty(Configurator.WEBSERVER_PORT_PROPERTY_KEY, 7575);
        WrappingNeoServerBootstrapper bootstrapper = new WrappingNeoServerBootstrapper(graphDatabase, config);
        bootstrapper.start();
        neoServer = bootstrapper.getServer();
        System.out.println("Server started !");
    }

    @AfterClass
    public static void stop() {
        graphDatabase.shutdown();
        neoServer.stop();

        System.out.println("Server stoped !");
    }

}
