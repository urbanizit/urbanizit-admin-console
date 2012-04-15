package org.urbanizit.adminconsole;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;
import org.urbanizit.adminconsole.Main;

public class MainTest {
	
	/*@Test*/ public void runTest() {
		Path from = Paths.get("/home/nicolasger/Apps/urbanizit/import");
		new Main().run(from);
	}

}
