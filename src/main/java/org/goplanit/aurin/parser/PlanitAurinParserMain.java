package org.goplanit.aurin.parser;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.goplanit.converter.intermodal.IntermodalConverterFactory;
import org.goplanit.converter.network.NetworkConverterFactory;
import org.goplanit.logging.Logging;
import org.goplanit.matsim.converter.MatsimIntermodalWriterFactory;
import org.goplanit.matsim.converter.MatsimNetworkWriterFactory;
import org.goplanit.matsim.converter.MatsimNetworkWriterSettings;
import org.goplanit.matsim.converter.MatsimWriter;
import org.goplanit.osm.converter.intermodal.OsmIntermodalReaderFactory;
import org.goplanit.osm.converter.network.OsmNetworkReaderFactory;
import org.goplanit.osm.converter.network.OsmNetworkReaderSettings;
import org.goplanit.utils.args.ArgumentParser;
import org.goplanit.utils.args.ArgumentStyle;
import org.goplanit.utils.exceptions.PlanItException;

/**
 * Access point for running a PLANit network parser that converts an OSM file to a MATSim compatible network. for now
 * we are restricted to using the Oceania input file which is expected to be available in the directory from where
 * this application is executed and should be named exactly <b>australia-oceania-latest.osm.pbf</b>.
 * <p>
 * The following command line options are available which should be provided such that the key is preceded with a double hyphen and the value follows directly (if any) with any number of 
 * spaces in between (no hyphens), e.g., {@code --<key> <value>}:
 * <ul>
 * <li>--input {@code <path>} to input file. Either a local file or a URL that we can stream</li>
 * <li>--country Format: Name of the country. Default: Global. Used to initialise defaults (speed limits, projection etc.)</li> 
 * <li>--bbox Format: long1 long2 lat1 lat2. Bounding box that restrict the input further (if at all)</li>
 * <li>--fidelity Options: [coarse, medium, fine]. Default: medium. Indicates fidelity of generated MATSim network based on predefined settings</li>
 * <li>--output Format {@code <path>} to output directory. Default: directory this application was invoked from</li>
 * <li>--clean Options: [yes, no]. Default yes. Result is persisted as separate network with postfix "_cleaned" where potentially unreachable links and vertices are removed</li>
 * <li>--rail Options: [yes, no]. Default: no. Parse rail tracks when set to <i>yes</i>, in which case modes <i>train, tram, light_rail</i> are automatically activated </li>
 * <li>--ptinfra Options: [yes, no]. Default: no. Parse pt infrastructure when set to <i>yes</i>, i.e., bus stops, (train) stations, and platforms. By default activates <i>bus, train, tram, light_rail</i> as well as setting --rail to yes</li>
 * <li>--rmmode Format: Comma separated list of names of the OSM modes. Default: N/A. Explicitly exclude mode(s) from being parsed</li>
 * <li>--addmode Format: Comma separated list of names of the OSM modes. Default: motor_car. Explicitly activate mode(s) for parsing</li>
 * </ul>
 * 
 * When {@code ptinfra yes} or {@code rail yes}, this will implicitly activates the mentioned modes because it is assumed one would only activate these options when these modes are present and required. If one or more
 * of these modes are not to be parsed, they can be explicitly disabled via {@code deactivate-mode theMode}. Conversely, OSM modes can be manually activated via {@code activate-mode theMode}.
 * <p>
 * OSM mode names are expected to be based on @see <a href="https://wiki.openstreetmap.org/wiki/Key:access">OSM wiki: access</a>. When a mode is both activated and deactivated, the deactivation takes precedence.
 * <p>
 * To better support visualisation for the MATSim output we by default activate the detailed geometry in case the user would like to visualise the results using VIA
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
   * @param settings to configure
   * @param keyValueMap arguments containing configuration choices
   * @throws PlanItException thrown if error
   */
  private static void configureReaderSettings(OsmNetworkReaderSettings settings, Map<String, String> keyValueMap) throws PlanItException {
    PlanItException.throwIfNull(settings, "OSM network reader settings null");
    PlanItException.throwIfNull(keyValueMap, "Configuration information null");    
    
    /* user configuration options */
    OsmNetworkReaderConfigurationHelper.parseInputsource(settings, keyValueMap);
    OsmNetworkReaderConfigurationHelper.parseBoundingBox(settings, keyValueMap);    
    OsmNetworkReaderConfigurationHelper.parseRailActivation(settings, keyValueMap);
    OsmNetworkReaderConfigurationHelper.parseNetworkFidelity(settings, keyValueMap);
    OsmNetworkReaderConfigurationHelper.parseModes(settings, keyValueMap);
  }

  /**
   * Configure the writer based on provided user arguments
   * 
   * @param settings to configure
   * @param keyValueMap arguments containing configuration choices
   * @throws PlanItException thrown if null inputs
   */
  private static void configureWriterSettings(MatsimNetworkWriterSettings settings, Map<String, String> keyValueMap) throws PlanItException {
    PlanItException.throwIfNull(settings, "Matsim network writer settings null");
    PlanItException.throwIfNull(keyValueMap, "Configuration information null");

    /* fixed configuration option */
    settings.setGenerateDetailedLinkGeometryFile(true);

    /* user configuration options */
    MatsimWriterConfigurationHelper.parseOutputDirectory(settings, keyValueMap);
  }
  
  /**
   * Let MATSim clean the created network and persist it under a separate name with "_cleaned" added to the file name
   * 
   * @param settings to extract location of current (uncleaned) MATSim network from
   */
  private static void createCleanedNetwork(MatsimNetworkWriterSettings settings) {
    Path originalNetworkFilePath = 
        Path.of(
            settings.getOutputDirectory(),
            settings.getOutputFileName()+MatsimWriter.DEFAULT_FILE_NAME_EXTENSION);
    Path cleanedNetworkFilePath = 
        Path.of(
            settings.getOutputDirectory(),
            settings.getOutputFileName()+"_cleaned"+MatsimWriter.DEFAULT_FILE_NAME_EXTENSION);
    LOGGER.info(String.format("Cleaning MATSim network %s",originalNetworkFilePath.toString()));    
    org.matsim.run.NetworkCleaner.main(new String[] {originalNetworkFilePath.toString(), cleanedNetworkFilePath.toString()});
    LOGGER.info(String.format("Persisted cleaned MATSim network to %s",cleanedNetworkFilePath));
    
    /* do the same for detailed geometry, although it is not a problem we do not because cleaning only removes (potentially) unreachable links */
    if(settings.isGenerateDetailedLinkGeometryFile()) {
      LOGGER.fine(String.format("Generated detailed geometry based on uncleaned MATSim network (contains more links than available) ",cleanedNetworkFilePath));
    }
  }

  /** Perform a network conversion based on the provided command line configuration
   * 
   * @param keyValueMap command line configuration information
   * @throws PlanItException thrown when error
   */
  private static void executeNetworkConversion(Map<String, String> keyValueMap) throws PlanItException {
    
    String countryName = OsmReaderConfigurationHelper.getCountry(keyValueMap);
    
    /* osm network reader */
    var osmNetworkReader = OsmNetworkReaderFactory.create(countryName);
    /* Matsim network writer */
    var matsimNetworkWriter = MatsimNetworkWriterFactory.create(
        MatsimWriterConfigurationHelper.MATSIM_OUTPUT_PATH.toAbsolutePath().toString(), countryName);     
    
    /* default modes for network conversion */
    OsmNetworkReaderConfigurationHelper.restrictToDefaultRoadModes(osmNetworkReader.getSettings());
    
    /* configure */    
    configureReaderSettings(osmNetworkReader.getSettings(), keyValueMap);    
    configureWriterSettings(matsimNetworkWriter.getSettings(), keyValueMap);

    /* perform conversion */
    NetworkConverterFactory.create(osmNetworkReader, matsimNetworkWriter).convert();
    
    /* when cleaned network is requested an additional cleaned network file is created */
    if(OsmNetworkReaderConfigurationHelper.parseCleanNetwork(keyValueMap)) {
      createCleanedNetwork(matsimNetworkWriter.getSettings());
    }
  }

  /** Perform a network and public transport infrastructure combined conversion based on the provided command line configuration
   * 
   * @param keyValueMap command line configuration information
   * @throws PlanItException thrown when error
   */
  private static void executeIntermodalNetworkConversion(Map<String, String> keyValueMap) throws PlanItException {
    
    String countryName = OsmReaderConfigurationHelper.getCountry(keyValueMap);
    
    /* osm intermodal reader (network + zoning with PT infrastructure as transfer zones) */
    var osmIntermodalReader = OsmIntermodalReaderFactory.create(countryName);        
    /* Matsim intermodal writer */
    var matsimIntermodalWriter = MatsimIntermodalWriterFactory.create(
        MatsimWriterConfigurationHelper.MATSIM_OUTPUT_PATH.toAbsolutePath().toString(), countryName);
    
    /* default modes for network conversion */
    OsmIntermodalReaderConfigurationHelper.restrictToDefaultModes(osmIntermodalReader.getSettings());    
            
    /* configure */
    configureReaderSettings(osmIntermodalReader.getSettings().getNetworkSettings(), keyValueMap);    
    configureWriterSettings(matsimIntermodalWriter.getSettings().getNetworkSettings(), keyValueMap);

    /* perform conversion */
    IntermodalConverterFactory.create(osmIntermodalReader, matsimIntermodalWriter).convert();
    
    /* when cleaned network is requested an additional cleaned network file is created */
    if(OsmNetworkReaderConfigurationHelper.parseCleanNetwork(keyValueMap)) {
      createCleanedNetwork(matsimIntermodalWriter.getSettings().getNetworkSettings());
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
      var keyValueMap = getKeyValueMap(args);

      /* when --help is present, print options */
      if (keyValueMap.containsKey(ARGUMENT_HELP)) {

        // TODO
        LOGGER.info("--help is not yet implemented, see Javadoc instead for available arguments");

      } else {

        if(OsmReaderConfigurationHelper.isParsePublicTransportInfrastructure(keyValueMap)) {
          /* intermodal conversion */
          executeIntermodalNetworkConversion(keyValueMap);
        }else {
          /* regular network-only conversion */
          executeNetworkConversion(keyValueMap);
        }        
      }
    } catch (Exception e) {
      LOGGER.severe(e.getMessage());      
      e.printStackTrace();
      LOGGER.severe("Unable to execute parser, terminating");      
    }

  }

}
