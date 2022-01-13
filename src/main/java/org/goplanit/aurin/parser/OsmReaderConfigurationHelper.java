package org.goplanit.aurin.parser;

import java.net.URL;
import java.util.Map;
import java.util.logging.Logger;

import org.locationtech.jts.geom.Envelope;
import org.goplanit.utils.exceptions.PlanItException;
import org.goplanit.utils.locale.CountryNames;
import org.goplanit.utils.misc.CharacterUtils;
import org.goplanit.utils.misc.StringUtils;
import org.goplanit.utils.misc.UrlUtils;
import org.goplanit.utils.resource.ResourceUtils;

/**
 * Helper methods to configure the OSM network reader based on user arguments provided for this wrapper.
 * 
 * @author markr
 *
 */
public class OsmReaderConfigurationHelper {
  
  /** logger to use */
  private static final Logger LOGGER = Logger.getLogger(OsmReaderConfigurationHelper.class.getCanonicalName());
  
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
  //--------PT-INFRASTRUCTURE ACTIVATION----------------
  //----------------------------------------------------
  
  /** configuration key to determine if public transport (non-road) infrastructure should be parsed or not */
  private static final String PUBLIC_TRANSPORT_INFRASTRUCTURE_KEY = "ptinfra";
  
  /** Activation value to parse public transport (non-road), e.g., "yes" */
  private static final String PUBLIC_TRANSPORT_INFRASTRUCTURE_ACTIVATE = "yes";
  
  /** Deactivation value to not parse public transport (non-road), e.g., "no" */
  private static final String PUBLIC_TRANSPORT_INFRASTRUCTURE_DEACTIVATE = "no";  
    
  //----------------------------------------------------
  //--------BOUNDING BOX--------------------------------
  //----------------------------------------------------  
  
  /** configuration key to determine bounding box to parse, must be present */
  private static final String BOUNDING_BOX_KEY = "bbox";  
  
  /** Verify if we are supposed to parse public transport infrastructure
   * 
   * @param keyValueMap to extract information from
   * @return true when parsing false otherwise
   * @throws PlanItException thrown if error
   */
  public static boolean isParsePublicTransportInfrastructure(final Map<String, String> keyValueMap) throws PlanItException {   
    PlanItException.throwIfNull(keyValueMap, "Configuration information null");
    
    if(!keyValueMap.containsKey(PUBLIC_TRANSPORT_INFRASTRUCTURE_KEY)) {
      return false;
    }  
    
    String ptInfraValue = keyValueMap.get(PUBLIC_TRANSPORT_INFRASTRUCTURE_KEY);
    if(StringUtils.isNullOrBlank(ptInfraValue)) {
      ptInfraValue = PUBLIC_TRANSPORT_INFRASTRUCTURE_DEACTIVATE;
    }
    
    
    if(ptInfraValue.equals(PUBLIC_TRANSPORT_INFRASTRUCTURE_ACTIVATE)) {
      return true;
    }else if(ptInfraValue.equals(PUBLIC_TRANSPORT_INFRASTRUCTURE_DEACTIVATE)) {
      return false;  
    }else {
      LOGGER.warning(String.format("Unsupported value %s encountered for key %s",ptInfraValue, PUBLIC_TRANSPORT_INFRASTRUCTURE_KEY));
    }
    return false;
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

  /** Configure the bounding box to use. Need not be present, if not present null is returned
   * 
   * @param keyValueMap to extract bounding box configuration from
   * @return parsed bounding box, null if not set
   * @throws PlanItException thrown if null inputs
   */
  public static Envelope parseBoundingBox(final Map<String, String> keyValueMap) throws PlanItException {
    PlanItException.throwIfNull(keyValueMap, "Configuration information null");
    
    if(!keyValueMap.containsKey(BOUNDING_BOX_KEY)) {
      return null;
    }
    
    String boundingBoxValue = keyValueMap.get(BOUNDING_BOX_KEY);
    if(StringUtils.isNullOrBlank(boundingBoxValue)){
      return null;
    }
    String[] boundingBoxOrdinates = boundingBoxValue.split(CharacterUtils.COMMA.toString());
    if(boundingBoxOrdinates.length != 4) {
      throw new PlanItException("Bounding box is expected to have comma separated values but found %s", boundingBoxValue);
    }
    
    Envelope boundingBox = new Envelope(
        Double.parseDouble(boundingBoxOrdinates[0]),
        Double.parseDouble(boundingBoxOrdinates[1]),
        Double.parseDouble(boundingBoxOrdinates[2]), 
        Double.parseDouble(boundingBoxOrdinates[3]));
    
    return boundingBox;
  }
  
  /** Parse the input source, this must be present, if not an exception is thrown 
   * 
   * @param keyValueMap to extract input source from
   * @return inputsource as either an absolute local path or an streamable external location in string form
   * @throws PlanItException throws when error
   */
  public static String parseInputsource(Map<String, String> keyValueMap) throws PlanItException {
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
    
    return inputSource;
  }

 
}
