package org.goplanit.aurin.parser;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.goplanit.osm.converter.network.OsmNetworkReaderSettings;
import org.goplanit.osm.tags.OsmHighwayTags;
import org.goplanit.osm.tags.OsmRailModeTags;
import org.goplanit.osm.tags.OsmRoadModeTags;
import org.goplanit.utils.exceptions.PlanItException;
import org.goplanit.utils.misc.StringUtils;
import org.locationtech.jts.geom.Envelope;

/**
 * Helper methods to configure the OSM network reader based on user arguments provided for this wrapper.
 * 
 * @author markr
 *
 */
public class OsmNetworkReaderConfigurationHelper {
  
  /** Logger to use */
  private static final Logger LOGGER = Logger.getLogger(OsmNetworkReaderConfigurationHelper.class.getCanonicalName());
    
  //----------------------------------------------------
  //--------ROAD MODES----------------------------------
  //----------------------------------------------------
  
  /** Key to signify explicit activation of (additional) OSM modes */
  private static final String ACTIVATE_MODE_KEY = "addmode";
  
  /** Key to signify explicit deactivation of OSM modes */
  private static final String DEACTIVATE_MODE_KEY = "rmmode";  
  
  /**
   * The supported OSM road modes. These include:
   * 
   * <ul>
   * <li>motorcar</li>
   * </ul>
   */
  public static final List<String> DEFAULT_OSM_ROAD_MODES = 
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
  
  /** configuration key to determine if rail tracks should be parsed or not */
  private static final String RAIL_PARSER_ACTIVATION_KEY = "rail";
  
  /** Activation value to parse rail tracks, e.g., "yes" */
  private static final String RAIL_PARSER_ACTIVATE = "yes";
  
  /** Deactivation value to not parse rail tracks, e.g., "no" */
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
  public static final List<String> DEFAULT_OSM_RAIL_MODES = 
      Arrays.asList(
          OsmRailModeTags.LIGHT_RAIL,
          OsmRailModeTags.TRAIN,
          OsmRailModeTags.TRAM);  
   
  //----------------------------------------------------
  //-------- CLEAN NETWORK -----------------------------
  //----------------------------------------------------
  
  /** Key reflecting the location of the input file or URL */
  public static final String CLEAN_NETWORK_KEY = "clean";    
  
  /** do clean the network */
  private static final String CLEAN_NETWORK_YES = "yes";
  
  /** do not clean the network */
  @SuppressWarnings("unused")
  private static final String CLEAN_NETWORK_NO = "no";  

  /** For the medium network fidelity we activate the following OSM highway types based on the mediumOsmHighwayTypes
   * member
   * 
   * @param settings to configure
   * @throws PlanItException thrown if null inputs
   */
  private static void configureMediumOsmNetworkFidelity(final OsmNetworkReaderSettings settings) throws PlanItException {
    PlanItException.throwIfNull(settings, "OSM network reader null");
    
    /* medium level detail */
    settings.getHighwaySettings().deactivateAllOsmHighwayTypesExcept(MEDIUM_OSM_HIGHWAY_TYPES);
    
  }

  /** For the medium network fidelity we activate the following OSM highway types based on the coarseOsmHighwayTypes
   * member
   * 
   * @param settings to configure
   * @throws PlanItException thrown if null inputs
   */
  private static void configureCoarseOsmNetworkFidelity(final OsmNetworkReaderSettings settings) throws PlanItException {
    PlanItException.throwIfNull(settings, "OSM network reader settings null");
    
    /* coarse level detail */
    settings.getHighwaySettings().deactivateAllOsmHighwayTypesExcept(COARSE_OSM_HIGHWAY_TYPES);
  }

  /** For the medium network fidelity we activate the following OSM highway types based on the fineOsmHighwayTypes
   * member
   * 
   * @param settings to configure
   * @throws PlanItException thrown if null inputs
   */  
  private static void configureFineOsmNetworkFidelity(final OsmNetworkReaderSettings settings) throws PlanItException {
    PlanItException.throwIfNull(settings, "OSM network reader settings null");
    
    /* fine level detail */
    settings.getHighwaySettings().deactivateAllOsmHighwayTypesExcept(FINE_OSM_HIGHWAY_TYPES);
    
  }
  
  /** Configure the bounding box to use. Need not be present, if not present entire file/source is parsed
   * 
   * @param settings to configure
   * @param keyValueMap to extract bounding box configuration from
   * @throws PlanItException thrown if null inputs
   */
  public static void parseBoundingBox(final OsmNetworkReaderSettings settings, final Map<String, String> keyValueMap) throws PlanItException {
    PlanItException.throwIfNull(settings, "OSM network reader null");
    
    Envelope boundingBox = OsmReaderConfigurationHelper.parseBoundingBox(keyValueMap);    
    if(boundingBox==null) {
      return;
    }    
    
    settings.setBoundingBox(boundingBox);
  }  

  /** Based on the fidelity choice configure the readers level of detail. If no fidelity if provided, we assume medium
   * level of detail.
   * 
   * @param settings to configure
   * @param keyValueMap to extract fidelity configuration from
   * @throws PlanItException thrown if null inputs
   */
  public static void parseNetworkFidelity(final OsmNetworkReaderSettings settings, final Map<String, String> keyValueMap) throws PlanItException {
    PlanItException.throwIfNull(settings, "OSM network reader null");
    PlanItException.throwIfNull(keyValueMap, "Configuration information null");
    
    String fidelityValue = keyValueMap.get(FIDELITY_KEY);
    if(StringUtils.isNullOrBlank(fidelityValue)) {
      fidelityValue = FIDELITY_MEDIUM;
    }
    switch (fidelityValue) {
      case FIDELITY_FINE:
        configureFineOsmNetworkFidelity(settings);
        break;
      case FIDELITY_MEDIUM:
        configureMediumOsmNetworkFidelity(settings);
        break;
      case FIDELITY_COARSE:
        configureCoarseOsmNetworkFidelity(settings);
        break;        
      default:
        throw new PlanItException(
            "Unkown fidelity chosen %s, choose from %s, %s, %s",fidelityValue, FIDELITY_FINE, FIDELITY_MEDIUM, FIDELITY_COARSE);
    }
  }

  /** Configure whether or not to activate the rail parser. Adopt the default when not explicitly set, meaning that it defaults to deactivation unless we are parsing public transport infrastructure
   * in which case the default is to activate the rail
   * 
   * @param settings to configure
   * @param keyValueMap to extract rail parser configuration from
   * @throws PlanItException thrown if null inputs
   */
  public static void parseRailActivation(final OsmNetworkReaderSettings settings, final Map<String, String> keyValueMap) throws PlanItException {
    PlanItException.throwIfNull(settings, "OSM network reader null");
    PlanItException.throwIfNull(keyValueMap, "Configuration information null");
    
    final String defaultRailActivation = OsmReaderConfigurationHelper.isParsePublicTransportInfrastructure(keyValueMap) ? RAIL_PARSER_ACTIVATE : RAIL_PARSER_DEACTIVATE; 
    String railActicationValue = keyValueMap.get(RAIL_PARSER_ACTIVATION_KEY);
    if(StringUtils.isNullOrBlank(railActicationValue)) {
      railActicationValue = defaultRailActivation;
    }
    switch (railActicationValue) {
      case RAIL_PARSER_ACTIVATE:
        settings.activateRailwayParser(true);
        restrictToDefaultRailModes(settings);
        break;
      case RAIL_PARSER_DEACTIVATE:
        settings.activateRailwayParser(false);
        break;
      default:
        throw new PlanItException(
            "Unkown rail activation value chosen %s, choose from %s, %s",railActicationValue, RAIL_PARSER_ACTIVATE, RAIL_PARSER_DEACTIVATE);
    }    
  }
 
  /** Parse the input source and set it on the network reader as its input source. 
   * This must be present, if not an exception is thrown 
   * 
   * @param settings to configure
   * @param keyValueMap to extract input source from
   * @throws PlanItException thrown if error
   */
  public static void parseInputsource(OsmNetworkReaderSettings settings, Map<String, String> keyValueMap) throws PlanItException {
    PlanItException.throwIfNull(settings, "OSM network reader settings null");   
    settings.setInputSource(OsmReaderConfigurationHelper.parseInputsource(keyValueMap));
  }

  /** Parse which modes are explicitly (de-)activated and modify the settings accordingly. Deactivation takes precedence (as in is
   * enforced) over activation.
   * 
   * @param settings to configure
   * @param keyValueMap to extract mode (de)activation from
   * @throws PlanItException thrown if error
   */
  public static void parseModes(OsmNetworkReaderSettings settings, Map<String, String> keyValueMap) throws PlanItException {
    PlanItException.throwIfNull(settings, "OSM network reader settings null");
    
    /* activate */
    if(keyValueMap.containsKey(ACTIVATE_MODE_KEY)) {
      List<String> activateOsmModes = List.of(keyValueMap.get(ACTIVATE_MODE_KEY).split(","));
      for(String activatedOsmMode : activateOsmModes) {
        if(StringUtils.isNullOrBlank(activatedOsmMode)) {
          continue;
        }
        if(OsmRoadModeTags.isRoadModeTag(activatedOsmMode)) {
          settings.getHighwaySettings().activateOsmRoadMode(activatedOsmMode);
        }else if(OsmRailModeTags.isRailModeTag(activatedOsmMode)) {
          settings.getRailwaySettings().activateOsmRailMode(activatedOsmMode);
        }else {
          LOGGER.warning(String.format("Unsupported OSM mode %s to activate encountered %s, ignored",activatedOsmMode));
        }
      }
    }   
    
    /* deactivate */
    if(keyValueMap.containsKey(DEACTIVATE_MODE_KEY)) {
      List<String> deactivateOsmModes = List.of(keyValueMap.get(DEACTIVATE_MODE_KEY).split(","));
      for(String deactivatedOsmMode : deactivateOsmModes) {
        if(StringUtils.isNullOrBlank(deactivatedOsmMode)) {
          continue;
        }
        
        if(OsmRoadModeTags.isRoadModeTag(deactivatedOsmMode)) {
          settings.getHighwaySettings().deactivateOsmRoadMode(deactivatedOsmMode);
        }else if(OsmRailModeTags.isRailModeTag(deactivatedOsmMode)) {
          settings.getRailwaySettings().deactivateOsmRailMode(deactivatedOsmMode);
        }else {
          LOGGER.warning(String.format("Unsupported OSM mode %s to deactivate encountered %s, ignored",deactivatedOsmMode));
        }        
      }
    }       
    
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
      String clean = keyValueMap.get(CLEAN_NETWORK_KEY);
      if(!StringUtils.isNullOrBlank(clean)) {
        return CLEAN_NETWORK_YES.equals(keyValueMap.get(CLEAN_NETWORK_KEY)) ? true : false;  
      }      
    }      
    
    /* default */
    return true;
  }

  /** Restrict allowed modes to car only, so roads that do not have car access will not be parsed even when activated
   * 
   * @param settings to configure
   * @throws PlanItException thrown if null inputs
   */
  public static void restrictToDefaultRoadModes(final OsmNetworkReaderSettings settings) throws PlanItException {
    PlanItException.throwIfNull(settings, "OSM network reader settings null");
    
    settings.getHighwaySettings().deactivateAllRoadModesExcept(DEFAULT_OSM_ROAD_MODES);
  }
  
  /** Restrict allowed modes to train, tram, lightrail only, so tracks that do not have such access will not be parsed even when activated
   * 
   * @param settings to configure
   * @throws PlanItException thrown if null inputs
   */
  public static void restrictToDefaultRailModes(final OsmNetworkReaderSettings settings) throws PlanItException {
    PlanItException.throwIfNull(settings, "OSM network reader settings null");
    
    settings.getRailwaySettings().deactivateAllRailModesExcept(DEFAULT_OSM_RAIL_MODES);
  }
  
}
