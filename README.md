# PLANitOsmParserWrapper

Wrapper for Aurin services to access exposed functionality of PLANit network converters (OSM to MATSim).

## Getting started

The simplest way to use this wrapper is to simply build this project via its pom.xml. Before performing a maven clean install, make sure that you update the git submodules of the repository with the recursive option switched on, otherwise the source code of the dependencies is not present. Once this is done, build the project. This will compile all dependent PLANit modules in the correct order as well as compiling the local PLANitAurinParser module that is part of this wrapper. The latter's pom.xml is configured to generate an executable jar in its target output dir. Hence, after successfully building this project the executable jar can be found under path/to/PLANitOsmParserWrapper/modules/PLANitAurinParser/target/PLANitAurinParser_version_.jar. It is this jar that can be run from the command line. Below you will find an example on how to conduct a simple conversion based on an OSM URL with a bounding box for a small area in Germany

```
java -jar PLANitAurinParser_version_.jar --input "https://api.openstreetmap.org/api/0.6/map?bbox=13.465661,52.504055,13.469817,52.506204" --country Germany --fidelity fine  --output ./output/Germany
```

Below a list of the available command line options that are currently exposed. The PLANit OSM parser has many more options than currently made available. If you wish to use those, then we suggest not using this wrapper but instead directly utilise the PLANit platform instead.

> Implementation of PLANitGTFS is partially funded by the University of Sydney and the Australian Transport Research Cloud ([ATRC](https://ardc.edu.au/project/australian-transport-research-cloud-atrc/)). ATRC is a project instigated by the Australian Research Data Cloud ([ARDC](www.ardc.edu.au)).

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

This wrapper is setup to be an umbrella parent project. Dependencies are gather via module definitions in both Maven and Git. The structure is such that all can be compiled in one go via the pom.xml file. The following important dependencies are defined:

* PLANitAurinParser
    * Contains the source code of the wrapper and is available as part of this repo 
* PLANitParentPom
    * contains the versions of PLANit and its dependencies (as modules) within the AURIN context. The included PLANit modules are:
* PLANitUtils
    * generic utilities 
* PLANit
    * core PLANit repo 
* PLANitOSM
    * code for parsing Open Street Map networks 
* PLANitMatsim
    * code for prsisting networks in MATSim format

The PLANitAurinParser is the only module that is directly part of this repo and is not a git module as well. PLANitParentPom is the PLANit umbrella module under which the required PLANit dependencies are defined via recursive modules, e.g.,

* pom.xml
    * PLANitParentPom pom.xml (git + maven module)
        * PLANitUtils (git + maven module) 
        * PLANit (git + maven module)
        * PLANitOSM (git + maven module)
        * PLANitMatsim (git + maven module)
    * PLANitAurinParser maven module 
        * MATSim core (maven versioned dependency)

