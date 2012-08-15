package org.urbanizit.adminconsole;


import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;
import org.urbanizit.adminconsole.core.types.MapType;

import java.io.File;
import java.util.List;

/**
 * Options of the CLI
 * -h                    list commands
 * -l                    list all root nodes (ie list all maps)
 * -d <ROOT NAME>      delete the map with name <ROOT NAME>
 * -a <ROOT NAME>      add a new ROOT NAME
 * -t <ROOT TYPE>      declare types of the <ROOT ELEMENT> to create
 * -c <ROOT NAME>      connect to the map with name <ROOT NAME>
 * -i <SRC DIRECTORY> [use with -c option] import all components from <SRC DIRECTORY>
 */
public class Options {

    @Option(name = "-h", usage = "display help")
    private boolean help;

    @Option(name = "-l", usage = "list all maps")
    private boolean listMaps;

    @Option(name = "-d", metaVar = "NAME_OF_THE_MAP", usage = "delete a named map")
    private String mapToDelete;

    @Option(name = "-a", metaVar = "NAME_OF_THE_MAP", usage = "add a named map (must be typed)")
    private String mapToAdd;

    @Option(name = "-t", usage = "types of the map to add")
    private MapType mapType;

    @Option(name = "-c", metaVar = "NAME_OF_THE_MAP", usage = "connect to a map identified by a name")
    private String mapToConnect;

    @Option(name = "-i", metaVar = "PATH_TO_DATAS", usage = "import directory to a map (must be connected)")
    private File importDirectory;

    @Option(name = "-initialize", usage = "Initialize the graphDB. !!WARNING!! All data will be erased")
    private boolean initialize;

   /* @Option(name = "-start", usage = "Start database if urbanizit is configured in embedded mode")
    private boolean start;

    @Option(name = "-stop", usage = "Stop database if urbanizit is configured in embedded mode")
    private boolean stop;
     */
    @Argument
    private List<String> argument;

    public boolean isHelp() {
        return help;
    }

    public void setHelp(boolean help) {
        this.help = help;
    }

    public boolean isListMaps() {
        return listMaps;
    }

    public void setListMaps(boolean listMaps) {
        this.listMaps = listMaps;
    }

    public String getMapToDelete() {
        return mapToDelete;
    }

    public void setMapToDelete(String mapToDelete) {
        this.mapToDelete = mapToDelete;
    }

    public String getMapToAdd() {
        return mapToAdd;
    }

    public void setMapToAdd(String mapToAdd) {
        this.mapToAdd = mapToAdd;
    }

    public MapType getMapType() {
        return mapType;
    }

    public void setMapType(MapType mapType) {
        this.mapType = mapType;
    }

    public String getMapToConnect() {
        return mapToConnect;
    }

    public void setMapToConnect(String mapToConnect) {
        this.mapToConnect = mapToConnect;
    }

    public File getImportDirectory() {
        return importDirectory;
    }

    public void setImportDirectory(File importDirectory) {
        this.importDirectory = importDirectory;
    }

    public boolean isInitialize() {
        return initialize;
    }

    public void setInitialize(boolean initialize) {
        this.initialize = initialize;
    }

    /*public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    } */

    public List<String> getArgument() {
        return argument;
    }

    public void setArgument(List<String> argument) {
        this.argument = argument;
    }
}