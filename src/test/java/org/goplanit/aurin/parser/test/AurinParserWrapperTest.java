package org.goplanit.aurin.parser.test;

import static org.junit.Assert.fail;

import org.goplanit.aurin.parser.PlanitAurinParserMain;
import org.junit.Test;


/**
 * Test the aurin Wrapper
 * 
 * @author markr
 *
 */
public class AurinParserWrapperTest {

  /**
   * Test with an URL streaming based input source
   */
  @Test
  public void osmNetworkReaderStreamingTest() {
    try {

      // Run with settings using stream, equivalent to: 
      // java -jar PLANitAurinParser.jar --input "https://api.openstreetmap.org/api/0.6/map?bbox=13.465661,52.504055,13.469817,52.506204" -- country Germany --fidelity fine
      //  --output ./output/Germany
      PlanitAurinParserMain.main(
          new String[]{
              "--input", 
              "https://api.openstreetmap.org/api/0.6/map?bbox=13.465661,52.504055,13.469817,52.506204",
              "--country",
              "Germany", 
              "--fidelity",
              "fine",
              "--output",
              "./output/Germany"});     
      
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error when testing Aurin Network Parser Wrapper");
    }
  }
  
  /**
   * Test with an URL streaming based input source where we do not clean the network based on the MATSim NetworkCleaner
   */
  @Test
  public void osmNetworkReaderStreamingTestNoCleaning() {
    try {

      // Run with settings using stream, equivalent to: 
      // java -jar PLANitAurinParser.jar --input "https://api.openstreetmap.org/api/0.6/map?bbox=13.465661,52.504055,13.469817,52.506204" -- country Germany --fidelity fine
      //  --clean_network no --output ./output/Germany
      PlanitAurinParserMain.main(
          new String[]{
              "--input", 
              "https://api.openstreetmap.org/api/0.6/map?bbox=13.465661,52.504055,13.469817,52.506204",
              "--country",
              "Germany", 
              "--fidelity",
              "fine",
              "--clean_network",
              "no",
              "--output",
              "./output/Germany"});     
      
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error when testing Aurin Network Parser Wrapper");
    }
  }  
  
  /**
   * Test with a local file as input source
   */
  @Test
  public void osmNetworkReaderLocalFileTest() {
    try {

      // Run with settings using stream, equivalent to: 
      // java -jar PLANitAurinParser.jar --input "./Melbourne/melbourne.osm.pbf" --country Australia --fidelity coarse --output "./output/Melbourne"
      PlanitAurinParserMain.main(
          new String[]{
              "--input", 
              "./Melbourne/melbourne.osm.pbf",
              "--country",
              "Australia", 
              "--fidelity",
              "coarse",
              "--output",
              "./output/Melbourne"});     
      
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error when testing Aurin Network Parser Wrapper");
    }
  }  

}

