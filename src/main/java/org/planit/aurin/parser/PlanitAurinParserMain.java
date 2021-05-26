package org.planit.aurin.parser;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.planit.converter.network.NetworkConverterFactory;
import org.planit.matsim.converter.PlanitMatsimNetworkWriter;
import org.planit.matsim.converter.PlanitMatsimNetworkWriterFactory;
import org.planit.osm.converter.network.PlanitOsmNetworkReader;
import org.planit.osm.converter.network.PlanitOsmNetworkReaderFactory;
import org.planit.utils.args.ArgumentParser;
import org.planit.utils.args.ArgumentStyle;
import org.planit.utils.exceptions.PlanItException;
import org.planit.utils.locale.CountryNames;

/**
 * Access point for running a PLANit network parser that converts an OSM file to a MATSim compatible network. for now
 * we are restricted to using the Oceania input file which is expected to be available in the directory from where
 * this application is executed and should be named exactly <b>australia-oceania-latest.osm.pbf</b>.
 * <p>
 * The following options are available:
 * <ul>
 * <li>--bbox "long long lat lat" valid bounding box within Australia must be provided</li>
 * <li>--fidelity "coarse/medium/fine" if absent defaults to medium</li>
 * <li>--rail "yes/no" if absent defaults to no</li>
 * <li>--output "./output" output directory, if absent defaults to directory this application was invoked from
 * <li>
 * </ul>
 * </p>
 * 
 * 
 * @author markr
 *
 */
public class PlanitAurinParserMain {

  /** logger to use */
  private static final Logger LOGGER = Logger.getLogger(PlanitAurinParserMain.class.getCanonicalName());

  /** currently hard-coded oceania file expected in directory where this application was run from */
  private static final Path MATSIM_OUTPUT_PATH = Path.of("");

  /** currently hard-coded oceania file expected in directory where this application was run from */
  private static final Path OSM_FILE_PATH = Path.of("", "australia-oceania-latest.osm.pbf");

  /** help key */
  private static final String ARGUMENT_HELP = "help";

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
  private static void configureNetworkReader(PlanitOsmNetworkReader osmNetworkReader, Map<String, String> keyValueMap)
      throws PlanItException {

    /* fixed configuration option */
    OsmNetworkReaderConfigurationHelper.restrictToCarOnly(osmNetworkReader);

    /* user configuration options */
    OsmNetworkReaderConfigurationHelper.parseBoundingBox(osmNetworkReader, keyValueMap);
    OsmNetworkReaderConfigurationHelper.parseRailActivation(osmNetworkReader, keyValueMap);
    OsmNetworkReaderConfigurationHelper.parseNetworkFidelity(osmNetworkReader, keyValueMap);
  }

  /**
   * Configure the writer based on provided user arguments
   * 
   * @param matsimNetworkWriter to configure
   * @param keyValueMap arguments containing configuration choices
   */
  private static void configureNetworkWriter(PlanitMatsimNetworkWriter matsimNetworkWriter,
      Map<String, String> keyValueMap) {
    // TODO Auto-generated method stub

  }

  /**
   * Access point
   * 
   * @param args arguments provided
   */
  public static void main(String[] args) {
    try {

      Map<String, String> keyValueMap = getKeyValueMap(args);

      /* when --help is present, print options */
      if (keyValueMap.containsKey(ARGUMENT_HELP)) {

        // TODO
        LOGGER.info("help requested on running PLANit OSM parser, this is not yet implemented");

      } else {

        /* osm network reader for Oceania/Australia */
        PlanitOsmNetworkReader osmNetworkReader = PlanitOsmNetworkReaderFactory.create(
            OSM_FILE_PATH.toAbsolutePath().toString(), CountryNames.AUSTRALIA);

        /* Matsim network writer for Australia */
        PlanitMatsimNetworkWriter matsimNetworkWriter = PlanitMatsimNetworkWriterFactory.create(
            MATSIM_OUTPUT_PATH.toAbsolutePath().toString(), CountryNames.AUSTRALIA);

        /* perform configuration based on input arguments */
        configureNetworkReader(osmNetworkReader, keyValueMap);
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
