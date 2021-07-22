package org.planit.aurin.parser;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.planit.converter.network.NetworkConverterFactory;
import org.planit.logging.Logging;
import org.planit.matsim.converter.PlanitMatsimNetworkWriter;
import org.planit.matsim.converter.PlanitMatsimNetworkWriterFactory;
import org.planit.matsim.converter.PlanitMatsimWriter;
import org.planit.osm.converter.network.PlanitOsmNetworkReader;
import org.planit.osm.converter.network.PlanitOsmNetworkReaderFactory;
import org.planit.utils.args.ArgumentParser;
import org.planit.utils.args.ArgumentStyle;
import org.planit.utils.exceptions.PlanItException;

/**
 * Access point for running a PLANit network parser that converts an OSM file to a MATSim compatible network. for now
 * we are restricted to using the Oceania input file which is expected to be available in the directory from where
 * this application is executed and should be named exactly <b>australia-oceania-latest.osm.pbf</b>.
 * <p>
 * The following command line options are available which should be provided such that the key is preceded with a double hyphen and the value follows directly (if any) with any number of 
 * spaces in between (no hyphens), e.g., {@code --<key> <value>}:
 * <ul>
 * <li>--input {@code <path>} to input file. Either a local file or a URL that we can stream</li>
 * <li>--country format: Name of the country. Default: Global. Used to initialise defaults (speed limits, projection etc.)</li> 
 * <li>--bbox format: long1 long2 lat1 lat2. Bounding box that restrict the input further (if at all)</li>
 * <li>--fidelity Options: [coarse, medium, fine]. Default: medium. Indicates fidelity of generated MATSim network based on predefined settings</li>
 * <li>--rail Options: [yes, no]. Default: no</li>
 * <li>--output format {@code <path>} to output directory. Default: directory this application was invoked from</li>
 * <li>--clean_network Options: [true, false]. Default "true". Result is persisted as separate network with postfix "_cleaned" where potentially unreachable links and vertices are removed</li>
 * </ul>
 * In addition for the OSM reader we limit ourselves to:
 * <ul>
 * <li> road modes: motor_car support only </li>
 * <li> rail modes: train, tram, light rail only (when rail is activated) </li>
 * </ul>
 * <p>
 * For the MATSim output we by default activate the detailed geometry in case the user would like to visualise the results using VIA
 * where it can be used to prettify the link shapes (instead of being restricted to start/end nodes only). Further, road modes are mapped to MATSim mode "car" whereas
 * all public transport modes are mapped to MATSim mode "pt".
 * 
 * 
 * @author markr
 *
 */
public class PlanitAurinParserMain {

  /** logger to use */
  private static Logger LOGGER = null;

  /**
   * Create a key value map based on provided arguments. If a key does not require a value, then it receives an
   * empty string.
   * 
   * @param args to parse
   * @return arguments as key value pairs
   * @throws PlanItException thrown if error
   */
  private static Map<String, String> getKeyValueMap(String[] args) throws PlanItException {

    Map<String, String> keyValueMap = ArgumentParser.convertArgsToMap(args, ArgumentStyle.DOUBLEHYPHEN);
    Map<String, String> lowerCaseKeyValueMap = new HashMap<String, String>(keyValueMap.size());
    for (Entry<String, String> entry : keyValueMap.entrySet()) {
      lowerCaseKeyValueMap.put(entry.getKey().toLowerCase(), entry.getValue());
    }
    return lowerCaseKeyValueMap;

  }

  /**
   * Configure the reader based on provided user arguments
   * 
   * @param osmNetworkReader to configure
   * @param keyValueMap arguments containing configuration choices
   * @throws PlanItException thrown if error
   */
  private static void configureNetworkReader(PlanitOsmNetworkReader osmNetworkReader, Map<String, String> keyValueMap) throws PlanItException {
    PlanItException.throwIfNull(osmNetworkReader, "OSM network reader null");
    PlanItException.throwIfNull(keyValueMap, "Configuration information null");    

    /* fixed configuration option */
    OsmNetworkReaderConfigurationHelper.restrictToDefaultRoadModes(osmNetworkReader);

    /* user configuration options */
    OsmNetworkReaderConfigurationHelper.parseInputsource(osmNetworkReader, keyValueMap);    
    OsmNetworkReaderConfigurationHelper.parseBoundingBox(osmNetworkReader, keyValueMap);
    OsmNetworkReaderConfigurationHelper.parseRailActivation(osmNetworkReader, keyValueMap);
    OsmNetworkReaderConfigurationHelper.parseNetworkFidelity(osmNetworkReader, keyValueMap);
  }

  /**
   * Configure the writer based on provided user arguments
   * 
   * @param matsimNetworkWriter to configure
   * @param keyValueMap arguments containing configuration choices
   * @throws PlanItException thrown if null inputs
   */
  private static void configureNetworkWriter(PlanitMatsimNetworkWriter matsimNetworkWriter, Map<String, String> keyValueMap) throws PlanItException {
    PlanItException.throwIfNull(matsimNetworkWriter, "Matsim network writer null");
    PlanItException.throwIfNull(keyValueMap, "Configuration information null");

    /* fixed configuration option */
    matsimNetworkWriter.getSettings().setGenerateDetailedLinkGeometryFile(true);

    /* user configuration options */
    MatsimNetworkWriterConfigurationHelper.parseOutputDirectory(matsimNetworkWriter, keyValueMap);
  }
  
  /**
   * Let MATSim clean the created network and persist it under a separate name with "_cleaned" added to the file name
   * 
   * @param matsimNetworkWriter to extract location of current (uncleaned) MATSim network from
   */
  private static void createCleanedNetwork(PlanitMatsimNetworkWriter matsimNetworkWriter) {
    Path originalNetworkFilePath = 
        Path.of(
            matsimNetworkWriter.getSettings().getOutputDirectory(),
            matsimNetworkWriter.getSettings().getOutputFileName()+PlanitMatsimWriter.DEFAULT_XML_FILE_EXTENSION);
    Path cleanedNetworkFilePath = 
        Path.of(
            matsimNetworkWriter.getSettings().getOutputDirectory(),
            matsimNetworkWriter.getSettings().getOutputFileName()+"_cleaned"+PlanitMatsimWriter.DEFAULT_XML_FILE_EXTENSION);
    LOGGER.info(String.format("Cleaning MATSim network %s",originalNetworkFilePath.toString()));    
    org.matsim.run.NetworkCleaner.main(new String[] {originalNetworkFilePath.toString(), cleanedNetworkFilePath.toString()});
    LOGGER.info(String.format("Persisted cleaned MATSim network to %s",cleanedNetworkFilePath));
    
    /* do the same for detailed geometry, although it is not a problem we do not because cleaning only removes (potentially) unreachable links */
    if(matsimNetworkWriter.getSettings().isGenerateDetailedLinkGeometryFile()) {
      LOGGER.fine(String.format("Generated detailed geometry based on uncleaned MATSim network (contains more links than available) ",cleanedNetworkFilePath));
    }
  }

  /** Path from which application was invoked */
  public static final Path CURRENT_PATH = Path.of("");    

  /** Help key */
  public static final String ARGUMENT_HELP = "help";  

  /**
   * Access point
   * 
   * @param args arguments provided
   */
  public static void main(String[] args) {
    try {
      
      /* logger + default Logging properties based on logging.properties file */
      LOGGER = Logging.createLogger(PlanitAurinParserMain.class);

      /* arguments as key/value map */
      Map<String, String> keyValueMap = getKeyValueMap(args);

      /* when --help is present, print options */
      if (keyValueMap.containsKey(ARGUMENT_HELP)) {

        // TODO
        LOGGER.info("--help is not yet implemented, see Javadoc instead for available arguments");

      } else {

        
        String countryName = OsmNetworkReaderConfigurationHelper.getCountry(keyValueMap);
        /* osm network reader */
        PlanitOsmNetworkReader osmNetworkReader = PlanitOsmNetworkReaderFactory.create(countryName);
        configureNetworkReader(osmNetworkReader, keyValueMap);
        
        /* Matsim network writer */
        PlanitMatsimNetworkWriter matsimNetworkWriter = PlanitMatsimNetworkWriterFactory.create(
            MatsimNetworkWriterConfigurationHelper.MATSIM_OUTPUT_PATH.toAbsolutePath().toString(), countryName);
        configureNetworkWriter(matsimNetworkWriter, keyValueMap);

        /* perform conversion */
        NetworkConverterFactory.create(osmNetworkReader, matsimNetworkWriter).convert();
        
        /* when cleaned network is requested an additional cleaned network file is created */
        if(OsmNetworkReaderConfigurationHelper.parseCleanNetwork(keyValueMap)) {
          createCleanedNetwork(matsimNetworkWriter);
        }
      }
    } catch (Exception e) {
      LOGGER.severe(e.getMessage());
      LOGGER.severe("Unable to execute parser, terminating");
    }

  }

}
