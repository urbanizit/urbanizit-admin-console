package org.urbanizit.adminconsole;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.server.NeoServer;
import org.neo4j.server.WrappingNeoServerBootstrapper;
import org.neo4j.server.configuration.Configurator;
import org.neo4j.server.configuration.EmbeddedServerConfigurator;
import org.neo4j.test.ImpermanentGraphDatabase;
import org.urbanizit.adminconsole.core.Urbanizer;


public class MainTest  {



    Main m = null;

    //@Before
    public void setup() {
        m = new Main();
        //m.main("-initialize");
    }

    //@Test
    public void helpOption() {
        /*Path from = Paths.get("/home/nicolasger/Apps/urbanizit/import");
             new Main().run(from);*/
        m.main("-h");
    }

    //@Test
    public void addOption(){
        m.main("-h");
        m.main("-a", "test", "-t", "APPLICATION");

        m.main("-c", "test", "-t", "APPLICATION", "-i", "/home/nicolasger/Apps/tool/urbanizit/import");
        m.main("-l");
    }

    //@Test
    public void importOption() {
        m.main("-c", "test", "-t", "APPLICATION", "-i", "/home/nicolasger/Apps/tool/urbanizit/import");
    }

    //@Test
    public void listMapOption() {
        m.main("-l");
    }
}
