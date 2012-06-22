package org.urbanizit.adminconsole;

import org.junit.Test;

public class MainTest {

    @Test public void startAndStop() {
        Main m = new Main();
        m.main("-start");
        m.main("-initialize");
        m.main("-stop");
    }


    @Test public void startOption() {
        new Main().main("-start");
    }

    @Test public void initializeOption() {
   		new Main().main("-initialize");
   	}
    @Test public void helpOption() {
   		/*Path from = Paths.get("/home/nicolasger/Apps/urbanizit/import");
   		new Main().run(from);*/
   		new Main().main("-h");
   	}
	
	@Test public void listMapOption() {
		new Main().main("-l");
	}

    @Test public void stopOption() {
        new Main().main("-stop");
    }
}
