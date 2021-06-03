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
 * <li>--input with value which is either a local file location or a URL that we can stream
 * <li>--country with value that is used to initialise defaults (speed limits, projection etc.), if absent global defaults are used 
 * <li>--bbox with value as "long long lat lat" valid bounding box that restrict the input further (if at all)</li>
 * <li>--fidelity with value "coarse/medium/fine" if absent defaults to medium</li>
 * <li>--rail with value "yes/no" if absent defaults to no</li>
 * <li>--output with value {@code <path>} output directory, if absent defaults to directory this application was invoked from
 * <li>
 * </ul>
 * In addition for the OSM reader we limit ourselves to:
 * <ul>
 * <li> road modes: motor_car support only </li>
 * <li> rail modes: train, tram, light rail only (when rail is activated) </li>
 * </ul>
 * <p>
 * For the MATSim output we by default activate the detailed geometry in case the user would like to visualise the results using VIA
 * where it can be used to prettify the link shapes (instead of being restricted to start/end nodes only). Further, road modes are mapped to Matsim mode "car" whereas
 * all public transport modes are mapped to Matsim mode "pt".
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
  
  /** PAth from which application was invoked */
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
        LOGGER.info("help requested on running PLANit OSM parser, this is not yet implemented");

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
      }
    } catch (Exception e) {
      LOGGER.severe(e.getMessage());
      LOGGER.severe("Unable to execute parser, terminating");
    }

  }

}
