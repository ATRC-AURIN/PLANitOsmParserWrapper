package org.goplanit.aurin.parser;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.locationtech.jts.geom.Envelope;
import org.goplanit.osm.converter.network.OsmNetworkReader;
import org.goplanit.osm.tags.OsmHighwayTags;
import org.goplanit.osm.tags.OsmRailModeTags;
import org.goplanit.osm.tags.OsmRoadModeTags;
import org.goplanit.utils.exceptions.PlanItException;
import org.goplanit.utils.locale.CountryNames;
import org.goplanit.utils.misc.CharacterUtils;
import org.goplanit.utils.misc.UrlUtils;
import org.goplanit.utils.resource.ResourceUtils;

/**
 * Helper methods to configure the OSM network reader based on user arguments provided for this wrapper.
 * 
 * @author markr
 *
 */
public class OsmNetworkReaderConfigurationHelper {
  
  //----------------------------------------------------
  //--------INPUT SOURCE -----------------------------------
  //----------------------------------------------------
  
  /** Key reflecting the location of the input file or URL */
  public static final String INPUT_SOURCE_KEY = "input";  
  
  //----------------------------------------------------
  //--------COUNTRY -----------------------------------
  //----------------------------------------------------
  
  /** Key reflecting the location of the input file or URL */
  public static final String COUNTRY_KEY = "country";    
  
  //----------------------------------------------------
  //--------ROAD MODES----------------------------------
  //----------------------------------------------------
  
  /**
   * The supported OSM road modes. These include:
   * 
   * <ul>
   * <li>motorcar</li>
   * </ul>
   */
  private static final List<String> OSM_ROAD_MODES = 
      Arrays.asList(OsmRoadModeTags.MOTOR_CAR);  
  
  //----------------------------------------------------
  //--------FIDELITY------------------------------------
  //----------------------------------------------------
  
  /** fine fidelity, i.e. high level of detail */
  private static final String FIDELITY_KEY = "fidelity";
  
  /** fine fidelity, i.e. high level of detail */
  private static final String FIDELITY_FINE = "fine";
  
  /** medium fidelity, i.e. average level of detail */
  private static final String FIDELITY_MEDIUM = "medium";
  
  /** coarse fidelity, i.e. low level of detail */
  private static final String FIDELITY_COARSE = "coarse";   
  
  /**
   * The supported OSM highway types for coarse fidelity networks. these mainly include roads
   * that connect on the national level:
   * 
   * <ul>
   * <li>motorway</li>
   * <li>motorway_link</li>
   * <li>trunk</li>
   * <li>trunk_link</li>
   * <li>primary</li>
   * <li>primary_link</li>
   * </ul>
   */
  private static final List<String> COARSE_OSM_HIGHWAY_TYPES = 
      Arrays.asList(
          OsmHighwayTags.MOTORWAY,
          OsmHighwayTags.MOTORWAY_LINK,
          OsmHighwayTags.TRUNK,
          OsmHighwayTags.TRUNK_LINK,
          OsmHighwayTags.PRIMARY,
          OsmHighwayTags.PRIMARY_LINK);
  
  /**
   * The supported OSM highway types for medium fidelity networks. These include the roads supported by the
   * coarse fidelity and in addition also include
   * 
   * <ul>
   * <li>secondary</li>
   * <li>secondary_link</li>
   * </ul>
   */
  private static final List<String> MEDIUM_OSM_HIGHWAY_TYPES =
      Stream.concat(COARSE_OSM_HIGHWAY_TYPES.stream(), 
          Arrays.asList(
            OsmHighwayTags.SECONDARY,
            OsmHighwayTags.SECONDARY_LINK).stream()).collect(Collectors.toList());
  
  /**
   * The supported OSM highway types for fine fidelity networks. These include the roads supported by the
   * coarse and medium fidelity and in addition also include
   * 
   * <ul>
   * <li>tertiary</li>
   * <li>tertiary_link</li>
   * <li>residential</li>
   * <li>living_street</li>
   * <li>service</li>
   * <li>unclassified</li>
   * <li>road</li>
   * </ul>
   */
  private static final List<String> FINE_OSM_HIGHWAY_TYPES =
      Stream.concat(MEDIUM_OSM_HIGHWAY_TYPES.stream(), 
          Arrays.asList(
            OsmHighwayTags.TERTIARY,
            OsmHighwayTags.TERTIARY_LINK,
            OsmHighwayTags.RESIDENTIAL,
            OsmHighwayTags.LIVING_STREET,
            OsmHighwayTags.SERVICE,
            OsmHighwayTags.UNCLASSIFIED,
            OsmHighwayTags.ROAD
            ).stream()).collect(Collectors.toList());  
  
  //----------------------------------------------------
  //--------RAIL ACTIVATION-----------------------------
  //----------------------------------------------------  
  
  /** configuration key to determine if rail infrastructure should be parsed or not */
  private static final String RAIL_PARSER_ACTIVATION_KEY = "rail";
  
  /** Activation value to parse rail infrastructure, e.g., "yes" */
  private static final String RAIL_PARSER_ACTIVATE = "yes";
  
  /** Deactivation value to not parse rail infrastructure, e.g., "no" */
  private static final String RAIL_PARSER_DEACTIVATE = "no";
  
  /**
   * The supported OSM rail modes. These include:
   * 
   * <ul>
   * <li>light_rail</li>
   * <li>train</li>
   * <li>tram</li>
   * </ul>
   */
  private static final List<String> OSM_RAIL_MODES = 
      Arrays.asList(
          OsmRailModeTags.LIGHT_RAIL,
          OsmRailModeTags.TRAIN,
          OsmRailModeTags.TRAM);  
  
  //----------------------------------------------------
  //--------BOUNDING BOX--------------------------------
  //----------------------------------------------------  
  
  /** configuration key to determine bounding box to parse, must be present */
  private static final String BOUNDING_BOX_KEY = "bbox";  
  
  //----------------------------------------------------
  //-------- CLEAN NETWORK -----------------------------
  //----------------------------------------------------
  
  /** Key reflecting the location of the input file or URL */
  public static final String CLEAN_NETWORK_KEY = "clean_network";    
  
  /** do clean the network */
  private static final String CLEAN_NETWORK_YES = "yes";
  
  /** do not clean the network */
  @SuppressWarnings("unused")
  private static final String CLEAN_NETWORK_NO = "no";  

  /** For the medium network fidelity we activate the following OSM highway types based on the mediumOsmHighwayTypes
   * member
   * 
   * @param osmNetworkReader to configure
   * @throws PlanItException thrown if null inputs
   */
  private static void configureMediumOsmNetworkFidelity(final OsmNetworkReader osmNetworkReader) throws PlanItException {
    PlanItException.throwIfNull(osmNetworkReader, "OSM network reader null");
    
    /* medium level detail */
    osmNetworkReader.getSettings().getHighwaySettings().deactivateAllOsmHighwayTypesExcept(MEDIUM_OSM_HIGHWAY_TYPES);
    
  }

  /** For the medium network fidelity we activate the following OSM highway types based on the coarseOsmHighwayTypes
   * member
   * 
   * @param osmNetworkReader to configure
   * @throws PlanItException thrown if null inputs
   */
  private static void configureCoarseOsmNetworkFidelity(final OsmNetworkReader osmNetworkReader) throws PlanItException {
    PlanItException.throwIfNull(osmNetworkReader, "OSM network reader null");
    
    /* coarse level detail */
    osmNetworkReader.getSettings().getHighwaySettings().deactivateAllOsmHighwayTypesExcept(COARSE_OSM_HIGHWAY_TYPES);
  }

  /** For the medium network fidelity we activate the following OSM highway types based on the fineOsmHighwayTypes
   * member
   * 
   * @param osmNetworkReader to configure
   * @throws PlanItException thrown if null inputs
   */  
  private static void configureFineOsmNetworkFidelity(final OsmNetworkReader osmNetworkReader) throws PlanItException {
    PlanItException.throwIfNull(osmNetworkReader, "OSM network reader null");
    
    /* fine level detail */
    osmNetworkReader.getSettings().getHighwaySettings().deactivateAllOsmHighwayTypesExcept(FINE_OSM_HIGHWAY_TYPES);
    
  }

  /** Based on the fidelity choice configure the readers level of detail. If no fidelity if provided, we assume medium
   * level of detail.
   * 
   * @param osmNetworkReader to configure
   * @param keyValueMap to extract fidelity configuration from
   * @throws PlanItException thrown if null inputs
   */
  public static void parseNetworkFidelity(final OsmNetworkReader osmNetworkReader, final Map<String, String> keyValueMap) throws PlanItException {
    PlanItException.throwIfNull(osmNetworkReader, "OSM network reader null");
    PlanItException.throwIfNull(keyValueMap, "Configuration information null");
    
    String fidelityValue = keyValueMap.getOrDefault(FIDELITY_KEY, FIDELITY_MEDIUM);
    switch (fidelityValue) {
      case FIDELITY_FINE:
        configureFineOsmNetworkFidelity(osmNetworkReader);
        break;
      case FIDELITY_MEDIUM:
        configureMediumOsmNetworkFidelity(osmNetworkReader);
        break;
      case FIDELITY_COARSE:
        configureCoarseOsmNetworkFidelity(osmNetworkReader);
        break;        
      default:
        throw new PlanItException(
            "Unkown fidelity chosen %s, choose from %s, %s, %s",fidelityValue, FIDELITY_FINE, FIDELITY_MEDIUM, FIDELITY_COARSE);
    }
  }
  
  /** Parse the country, if not present, global country is assumed 
   * 
   * @param keyValueMap to extract country from
   * @return extracted country name, global if not available from list or not present
   * @throws PlanItException thrown if error
   */  
  public static String getCountry(Map<String, String> keyValueMap) throws PlanItException {
    PlanItException.throwIfNull(keyValueMap, "Configuration information null");
    
    if(!keyValueMap.containsKey(COUNTRY_KEY)) {
      return CountryNames.GLOBAL;
    }  
    
    return keyValueMap.get(COUNTRY_KEY);
  }   

  /** Configure whether or not to activate the rail parse
   * 
   * @param osmNetworkReader to configure
   * @param keyValueMap to extract rail parser configuration from
   * @throws PlanItException thrown if null inputs
   */
  public static void parseRailActivation(final OsmNetworkReader osmNetworkReader, final Map<String, String> keyValueMap) throws PlanItException {
    PlanItException.throwIfNull(osmNetworkReader, "OSM network reader null");
    PlanItException.throwIfNull(keyValueMap, "Configuration information null");
    
    String railActicationValue = keyValueMap.getOrDefault(RAIL_PARSER_ACTIVATION_KEY, RAIL_PARSER_DEACTIVATE);
    switch (railActicationValue) {
      case RAIL_PARSER_ACTIVATE:
        osmNetworkReader.getSettings().activateRailwayParser(true);
        restrictToDefaultRailModes(osmNetworkReader);
        break;
      case RAIL_PARSER_DEACTIVATE:
        osmNetworkReader.getSettings().activateRailwayParser(false);
        break;
      default:
        throw new PlanItException(
            "Unkown rail activation value chosen %s, choose from %s, %s",railActicationValue, RAIL_PARSER_ACTIVATE, RAIL_PARSER_DEACTIVATE);
    }    
  }

  /** Configure the bounding box to use. Need not be present, if not present entire file/source is parsed
   * 
   * @param osmNetworkReader to configure
   * @param keyValueMap to extract bounding box configuration from
   * @throws PlanItException thrown if null inputs
   */
  public static void parseBoundingBox(final OsmNetworkReader osmNetworkReader, final Map<String, String> keyValueMap) throws PlanItException {
    PlanItException.throwIfNull(osmNetworkReader, "OSM network reader null");
    PlanItException.throwIfNull(keyValueMap, "Configuration information null");
    
    if(!keyValueMap.containsKey(BOUNDING_BOX_KEY)) {
      return;
    }
    
    String boundingBoxValue = keyValueMap.get(BOUNDING_BOX_KEY);
    String[] boundingBoxOrdinates = boundingBoxValue.split(CharacterUtils.COMMA.toString());
    if(boundingBoxOrdinates.length != 4) {
      throw new PlanItException("Bounding box is expected to have comma separated values but found %s", boundingBoxValue);
    }
    
    Envelope boundingBox = new Envelope(
        Double.parseDouble(boundingBoxOrdinates[0]),
        Double.parseDouble(boundingBoxOrdinates[1]),
        Double.parseDouble(boundingBoxOrdinates[2]), 
        Double.parseDouble(boundingBoxOrdinates[3]));
    
    osmNetworkReader.getSettings().setBoundingBox(boundingBox);
  }
  
  /** Parse the input source, this must be present, if not an exception is thrown 
   * 
   * @param osmNetworkReader to configure
   * @param keyValueMap to extract input source from
   * @throws PlanItException thrown if error
   */
  public static void parseInputsource(OsmNetworkReader osmNetworkReader, Map<String, String> keyValueMap) throws PlanItException {
    PlanItException.throwIfNull(osmNetworkReader, "OSM network reader null");
    PlanItException.throwIfNull(keyValueMap, "Configuration information null");
    
    if(!keyValueMap.containsKey(INPUT_SOURCE_KEY)) {
      throw new PlanItException("--input option missing, this is required");
    }  
    
    String inputSource = keyValueMap.get(INPUT_SOURCE_KEY);
    URL inputSourceAsResource = ResourceUtils.getResourceUrl(inputSource);
    if(inputSourceAsResource!=null) {      
      /* is local resource, use its absolute path instead of (possibly) relative path to avoid issues in OSM reader */
      inputSource = UrlUtils.asLocalPath(inputSourceAsResource).toString();
    }
    
    osmNetworkReader.getSettings().setInputSource(inputSource);
  }

  /** set flag indicating whether or not the newly created MATSim network should be passed through the MATSim NetworkCleaner
   * to ensure all links are reachable, e.g. ends of one way motorways at the edge of the network. Can be useful since MATSim
   * assigns trips to the nearest node and if this is a one way link near the edge it is otherwise possible no route into the
   * rest of the network can be created, causing MATSim to crash.
   * 
   * @param keyValueMap to extract from
   * @return parsed value or default (true)
   * @throws PlanItException thrown if error
   */
  public static boolean parseCleanNetwork(Map<String, String> keyValueMap) throws PlanItException {
    PlanItException.throwIfNull(keyValueMap, "Configuration information null");
    
    if(keyValueMap.containsKey(CLEAN_NETWORK_KEY)) {
      return CLEAN_NETWORK_YES.equals(keyValueMap.get(CLEAN_NETWORK_KEY)) ? true : false;
    }      
    /* default */
    return true;
  }

  /** Restrict allowed modes to car only, so roads that do not have car access will not be parsed even when activated
   * 
   * @param osmNetworkReader to configure
   * @throws PlanItException thrown if null inputs
   */
  public static void restrictToDefaultRoadModes(final OsmNetworkReader osmNetworkReader) throws PlanItException {
    PlanItException.throwIfNull(osmNetworkReader, "OSM network reader null");
    
    osmNetworkReader.getSettings().getHighwaySettings().deactivateAllRoadModesExcept(OSM_ROAD_MODES);
  }
  
  /** Restrict allowed modes to train, tram, lightrail only, so tracks that do not have such access will not be parsed even when activated
   * 
   * @param osmNetworkReader to configure
   * @throws PlanItException thrown if null inputs
   */
  public static void restrictToDefaultRailModes(final OsmNetworkReader osmNetworkReader) throws PlanItException {
    PlanItException.throwIfNull(osmNetworkReader, "OSM network reader null");
    
    osmNetworkReader.getSettings().getRailwaySettings().deactivateAllRailModesExcept(OSM_RAIL_MODES);
  }
  
}
