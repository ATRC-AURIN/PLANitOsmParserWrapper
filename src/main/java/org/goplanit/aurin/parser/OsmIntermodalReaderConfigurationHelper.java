package org.goplanit.aurin.parser;

import java.util.Arrays;
import java.util.List;

import org.goplanit.osm.converter.intermodal.OsmIntermodalReaderSettings;
import org.goplanit.osm.tags.OsmRoadModeTags;
import org.goplanit.utils.exceptions.PlanItException;

/**
 * Helper methods to configure the OSM intermodal reader based on user arguments provided for this wrapper.
 * 
 * @author markr
 *
 */
public class OsmIntermodalReaderConfigurationHelper {
  
  //----------------------------------------------------
  //--------ROAD MODES----------------------------------
  //----------------------------------------------------
  
  /**
   * The default supported OSM intermodal road modes. These include:
   * 
   * <ul>
   * <li>motorcar</li>
   * <li>bus</li>
   * </ul>
   */
  public static final List<String> DEFAULT_OSM_INTERMODAL_ROAD_MODES = 
      Arrays.asList(OsmRoadModeTags.MOTOR_CAR, OsmRoadModeTags.BUS);   

  /** Restrict allowed modes to defaulr road and rail modes; so roads/rail that do not have these modes will not be parsed
   * 
   * @param settings to configure
   * @throws PlanItException thrown if null inputs
   */
  public static void restrictToDefaultModes(final OsmIntermodalReaderSettings settings) throws PlanItException {
    PlanItException.throwIfNull(settings, "OSM intermodal reader settings null");
    
    settings.getNetworkSettings().activateHighwayParser(true);
    settings.getNetworkSettings().activateRailwayParser(true);
    settings.getNetworkSettings().getHighwaySettings().deactivateAllRoadModesExcept(DEFAULT_OSM_INTERMODAL_ROAD_MODES);
    settings.getNetworkSettings().getRailwaySettings().deactivateAllRailModesExcept(OsmNetworkReaderConfigurationHelper.DEFAULT_OSM_RAIL_MODES);
  }  
}
