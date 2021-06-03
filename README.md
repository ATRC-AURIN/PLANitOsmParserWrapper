# PLANitOsmParserWrapper

Wrapper for Aurin services to access exposed functionality of PLANit network converters (OSM to MATSim)

## Maven parent

Projects need to be built from Maven before they can be run. The common maven configuration can be found in the PLANitParentPom project which acts as the parent for this project's pom.xml.

> Make sure you install the PLANitParentPom pom.xml before conducting a maven build (in Eclipse) on this project, otherwise it cannot find the references dependencies, plugins, and other resources.

## FAT Jar

To run the wrapper in a stand-alone fashion (not from IDE) all dependencies need to be made available within the runnable jar. To support this a separate pom.xml is provided in the project that builds such a jar.
