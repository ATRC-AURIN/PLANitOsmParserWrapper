package org.planit.aurin.parser;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.planit.osm.converter.network.PlanitOsmNetworkReader;
import org.planit.osm.tags.OsmHighwayTags;
import org.planit.osm.tags.OsmRoadModeTags;
import org.planit.utils.exceptions.PlanItException;

/**
 * Helper methods to configure the OSM network reader based on user arguments provided
 * for this wrapper
 * 
 * @author markr
 *
 */
public class OsmNetworkReaderConfigurationHelper {
  
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
  private static final List<String> coarseOsmHighwayTypes = 
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
  private static final List<String> mediumOsmHighwayTypes =
      Stream.concat(coarseOsmHighwayTypes.stream(), 
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
  private static final List<String> fineOsmHighwayTypes =
      Stream.concat(mediumOsmHighwayTypes.stream(), 
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
  
  //----------------------------------------------------
  //--------BOUNDING BOX--------------------------------
  //----------------------------------------------------  
  
  /** configuration key to determine bounding box to parse, must be present */
  private static final String BOUNDING_BOX_KEY = "bbox";  

  /** For the medium network fidelity we activate the following OSM highway types based on the mediumOsmHighwayTypes
   * member
   * 
   * @param osmNetworkReader to configure
   */
  private static void configureMediumOsmNetworkFidelity(PlanitOsmNetworkReader osmNetworkReader) {
    
    /* medium level detail */
    osmNetworkReader.getSettings().getHighwaySettings().deactivateAllOsmHighwayTypesExcept(mediumOsmHighwayTypes);
    
  }

  /** For the medium network fidelity we activate the following OSM highway types based on the coarseOsmHighwayTypes
   * member
   * 
   * @param osmNetworkReader to configure
   */
  private static void configureCoarseOsmNetworkFidelity(PlanitOsmNetworkReader osmNetworkReader) {
    
    /* coarse level detail */
    osmNetworkReader.getSettings().getHighwaySettings().deactivateAllOsmHighwayTypesExcept(coarseOsmHighwayTypes);
  }

  /** For the medium network fidelity we activate the following OSM highway types based on the fineOsmHighwayTypes
   * member
   * 
   * @param osmNetworkReader to configure
   */  
  private static void configureFineOsmNetworkFidelity(PlanitOsmNetworkReader osmNetworkReader) {
    
    /* fine level detail */
    osmNetworkReader.getSettings().getHighwaySettings().deactivateAllOsmHighwayTypesExcept(fineOsmHighwayTypes);
    
  }

  /** Based on the fidelity choice configrue the readers level of detail. If no fidelity if provided, we assume medium
   * level of detail.
   * 
   * @param osmNetworkReader to configure
   * @param keyValueMap to extract fidelity configuration from
   * @throws PlanItException 
   */
  public static void parseNetworkFidelity(PlanitOsmNetworkReader osmNetworkReader, Map<String, String> keyValueMap) throws PlanItException {
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

  /** Configure whether or not to activate the rail parse
   * 
   * @param osmNetworkReader to configure
   * @param keyValueMap to extract rail parser configuration from
   * @throws PlanItException thrown if error
   */
  public static void parseRailActivation(PlanitOsmNetworkReader osmNetworkReader, Map<String, String> keyValueMap) throws PlanItException {
    String railActicationValue = keyValueMap.getOrDefault(RAIL_PARSER_ACTIVATION_KEY, RAIL_PARSER_DEACTIVATE);
    switch (railActicationValue) {
      case RAIL_PARSER_ACTIVATE:
        osmNetworkReader.getSettings().activateRailwayParser(true);
        break;
      case RAIL_PARSER_DEACTIVATE:
        osmNetworkReader.getSettings().activateRailwayParser(false);
        break;
      default:
        throw new PlanItException(
            "Unkown rail activation value chosen %s, choose from %s, %s",railActicationValue, RAIL_PARSER_ACTIVATE, RAIL_PARSER_DEACTIVATE);
    }    
  }

  /** Configure the bounding box to use. Must be present, so if not present throw an exception
   * 
   * @param osmNetworkReader to configure
   * @param keyValueMap to extract bounding box configuration from
   * @throws PlanItException thrown if error
   */
  public static void parseBoundingBox(PlanitOsmNetworkReader osmNetworkReader, Map<String, String> keyValueMap) throws PlanItException {
    if(!keyValueMap.containsKey(BOUNDING_BOX_KEY)) {
      throw new PlanItException("--bbox configuration option missing, this is required");
    }
    
    //TODO
  }

  /** Restrict allowed modes to car only, so roads that do not have car access will not be parsed even when activated
   * 
   * @param osmNetworkReader to configure
   */
  public static void restrictToCarOnly(PlanitOsmNetworkReader osmNetworkReader) {
    osmNetworkReader.getSettings().getHighwaySettings().deactivateAllRoadModesExcept(OsmRoadModeTags.MOTOR_CAR);
  }
}
