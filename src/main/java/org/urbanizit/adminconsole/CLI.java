package org.urbanizit.adminconsole;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.kohsuke.args4j.CmdLineParser;
import org.neo4j.graphdb.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.urbanizit.adminconsole.core.Urbanizer;
import org.urbanizit.adminconsole.modules.GraphModule;
import org.urbanizit.adminconsole.modules.UrbanizerModule;
import org.urbanizit.adminconsole.pojo.Map;

import java.nio.file.Paths;
import java.util.List;

/**
 * User: nicolasger
 * Date: 15/08/12
 * Time: 22:29
 */
public class CLI {
    private static final Logger logger = LoggerFactory.getLogger(CLI.class);
    private String[] arguments;
    private Options options;
    private CmdLineParser parser;
    private Urbanizer urbanizer;

    public CLI(String...args) {
        arguments = args;
        options = new Options();
        parser = new CmdLineParser(options);
        parser.setUsageWidth(120);
    }

    public void execute() {
        try {
            Injector injector = Guice.createInjector(new UrbanizerModule(), new GraphModule());
            urbanizer = injector.getInstance(Urbanizer.class);

            parser.parseArgument(arguments);

            if (options.isInitialize()) {
                urbanizer.initializeDb();
            } else if (options.isHelp()) {
                parser.printUsage(System.out);
            } else if (options.isListMaps()) {
                List<Map> maps = urbanizer.listMaps();
                logger.info("ID   TYPE               NAME");
                logger.info("---------------------------------------");
                if (!maps.isEmpty()) {
                    for (Map map : maps) {
                        StringBuilder sb = new StringBuilder()
                                .append(map.getId())
                                .append("    ")
                                .append(map.getMapType())
                                .append("        ")
                                .append(map.getName());
                        logger.info(sb.toString());
                    }
                }
             }
            else if (options.getMapToAdd() != null && options.getMapType() != null) {
                logger.info("Create a new map named {} and typed as {}", options.getMapToAdd(), options.getMapType());
                urbanizer.addMap(options.getMapToAdd(), options.getMapType());
            }
            else if (options.getMapToConnect() != null && options.getMapType()!=null) {
                Node map = urbanizer.getMap(options.getMapToConnect(), options.getMapType());
                if(map == null) {
                    throw new IllegalArgumentException("map doesn't exist");
                }
                if(options.getImportDirectory() != null) {
                    urbanizer.populate(Paths.get(options.getImportDirectory().getAbsolutePath()), map);
                }
            }
            else{
                parser.printUsage(System.out);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            parser.printUsage(System.out);
        }

    }
}