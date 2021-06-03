package org.planit.aurin.test;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.planit.aurin.parser.PlanitAurinParserMain;


/**
 * Test the aurin Wrapper
 * 
 * @author markr
 *
 */
public class AurinWrapperTest {

  /**
   * Test with an URL streaming based input source
   */
  @Test
  public void osmNetworkReaderStreamingTest() {
    try {

      // Run with settings using stream, equivalent to: 
      // java -jar PLANitAurinParser.jar --input "https://api.openstreetmap.org/api/0.6/map?bbox=13.465661,52.504055,13.469817,52.506204" -- country Germany --fidelity fine
      PlanitAurinParserMain.main(
          new String[]{
              "--input", 
              "https://api.openstreetmap.org/api/0.6/map?bbox=13.465661,52.504055,13.469817,52.506204",
              "--country",
              "Germany", 
              "--fidelity",
              "fine"});     
      
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error when testing Aurin Wrapper with max defaults ");
    }
  }

}

