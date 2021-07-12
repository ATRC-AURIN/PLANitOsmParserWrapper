package org.planit.aurin.parser;

import java.nio.file.Path;
import java.util.Map;

import org.planit.matsim.converter.PlanitMatsimNetworkWriter;
import org.planit.utils.exceptions.PlanItException;

/**
 * Helper methods to configure the MATSim network writer based on user arguments provided for this wrapper.
 * 
 * @author markr
 *
 */
public class MatsimNetworkWriterConfigurationHelper {
  
  //----------------------------------------------------
  //--------OUTPUT PATH --------------------------------
  //----------------------------------------------------
  
  /** fine fidelity, i.e. high level of detail */
  private static final String OUTPUT_PATH_KEY = "output";  
  
  /** Output path defaults to directory where this application was run from */
  public static final Path MATSIM_OUTPUT_PATH = PlanitAurinParserMain.CURRENT_PATH;  

  /** The output directory to use. If absent nothing is set and it is assumed the output directory is set upon creation of the writer
   * 
   * @param matsimNetworkWriter to configure
   * @param keyValueMap to extract information from
   * @throws PlanItException thrown if error
   */
  public static void parseOutputDirectory(PlanitMatsimNetworkWriter matsimNetworkWriter, Map<String, String> keyValueMap) throws PlanItException {
    PlanItException.throwIfNull(matsimNetworkWriter, "Matsim network writer null");
    PlanItException.throwIfNull(keyValueMap, "Configuration information null");
    
    if(keyValueMap.containsKey(OUTPUT_PATH_KEY)) {
      matsimNetworkWriter.getSettings().setOutputDirectory(keyValueMap.get(OUTPUT_PATH_KEY));  
    }    
    
  }

}
