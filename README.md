# PLANitOsmParserWrapper

Wrapper for Aurin services to access exposed functionality of PLANit network converters (OSM to MATSim).

## Getting started

The simplest way to use this wrapper is to simply build this project via its pom.xml (mvn clean install). This will collect all (PLANit) dependencies as well as compile the local wrapper code base. The pom.xml is configured to generate an executable jar in its target output dir. After successfully building this project the executable jar can be found under path/to/PLANitOsmParserWrapper/target/planit-aurin-parser_version_.jar. It is this jar that can be run from the command line. Below you will find an example on how to conduct a simple conversion based on an OSM URL with a bounding box for a small area in Germany

```
java -jar planit-aurin-parser-version.jar --input "https://api.openstreetmap.org/api/0.6/map?bbox=13.465661,52.504055,13.469817,52.506204" --country Germany --fidelity fine  --output ./output/Germany
```

Below a list of the available command line options that are currently exposed. The PLANit OSM parser has many more options than currently made available. If you wish to use those, then we suggest not using this wrapper but instead directly utilise the PLANit platform instead.

> Implementation of this wrapper is partially funded by the University of Sydney and the Australian Transport Research Cloud ([ATRC](https://ardc.edu.au/project/australian-transport-research-cloud-atrc/)). ATRC is a project instigated by the Australian Research Data Cloud ([ARDC](www.ardc.edu.au)).

## Command line options

The following command line options are available which should be provided such that the key is preceded with a double hyphen (--) and the value follows directly (if any) with any number of spaces in between (no hyphens), e.g., --<key> <value>:
 
 * **--input**    *Format: <path to input file>.* Either a local file or a URL that can be streamed
 * **--country**  *Format: Name of the country.* Default: Global. Used to initialise defaults (speed limits, projection etc.) 
 * **--bbox**     *Format: long1 long2 lat1 lat2.* Bounding box that restricts the geospatial area of the original input further (if at all)
 * **--fidelity** *Format: options [coarse, medium, fine].* Default: medium. Indicates fidelity of generated MATSim network based on predefined settings
 * **--rail** *Format: options: [yes, no].* Default: no
 * **--output** *Format <path to output directory>.* Default: working directory this application was invoked from
 * **--clean_network** *Format: options: [true, false].* Default true. Result is persisted as separate network with postfix "_cleaned" where potentially unreachable links and vertices are removed
 
 In addition we currently limit the used modes to:
 
 * road modes: *motor_car* support only mapped to MATSim mode *car*
 * rail modes: *train, tram, light rail* only (when rail is activated) --> mapped to MATSim mode *pt*

## Detailed MATSim geometry

For the MATSim output we by default activate the detailed geometry in case the user would like to visualise the results using VIA
where it can be used to prettify the link shapes (instead of being restricted to start/end nodes only). Within MATSim this detailed geometry file is of no use.

## General Maven build information 

The following PLANit dependencies are present:

* PLANitParentPom
    * parent pom of all PLANit dependencies used
* PLANitUtils
    * generic utilities 
* PLANit
    * core PLANit repo 
* PLANitOSM
    * code for parsing Open Street Map networks 
* PLANitMatsim
    * code for prsisting networks in MATSim format
