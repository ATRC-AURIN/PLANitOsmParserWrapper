# Docker information

In this file we keep track how to use docker with this Java repository. It is a basic dockerfile that uses environment variables to pass in the command line arguments that otherwise would have been fed to the executable.

# Creating the docker image for OSM Parser Wrapper

* We use the Dockerfile in the root dir to create the image.  
* We use Alpine as the Linux distro since it is small
* There is no official jdk-11 and Alpine Docker image, so we use the one from adoptopenjdk, i.e., adoptopenjdk/openjdk11:jre-11.0.6_10-alpine

Restrictions to date:

* The jar is not built as part of the docker image creation, it needs to be build before the docker image can be created. We then copy the locally created fat jar (from the maven clean install on pom.xml) as the only file added to the openjdk-alpine image.

# Environmental variables

The docker image has the exact same environmental variables as the Java wrapper has (see README.md), except that all variables are capatilised, i.e., *--input* translates to *INPUT*, which in turn requires the *-e* switch to signal to docker it is an environmental variable.

In addition one more variable is present:

* *VERSION*, with default to the latest released version of this repository, so normally there is no need to use this variable
 

# Building a Docker image of this repo this repo:

From root dir of repo, build will use the Dockerfile file to perform the build

```
docker build -t osmparserwrapper:latest .
```

# Inspecting file structure of built image**

To inspect the file structure of an image (not container), export it to a tar file and then inspect the tar. An image cannot be
inspected without running it otherwise.

```
docker image save osmparserwrapper:latest > ./image.tar
```

# Running the image **

## Test run without volumes

Below an example of running the image with some of the command line options set, analogous to the small streaming example in PLANitOSM. It uses some test resources (files) directly included in the image, purely to test if the pipeline works independent of mounting any volumes (external I/O). If this works as expected, you are reader to instead pass in files and collect output via volumes

```
docker run -e INPUT=https://api.openstreetmap.org/api/0.6/map?bbox=13.465661,52.504055,13.469817,52.506204 -e COUNTRY=Germany -e FIDELITY=fine -e PTINFRA=yes -e CLEAN=no -e OUTPUT=./output/Germany_pt osmparserwrapper:latest
```

## Test run script with volumes

When using volumes we conform to the following convention and assumptions:

* Your environment has two fixed persistent directories: VM_INPUT and VM_OUTPUT where  the container reads and outputs data. Often Docker would be run from a virtual machine (VM) hence this naming convention. 
* It is expected that these two directories are available in the working directory from which the run command is invoked. If not this script needs to be altered to reflect these changes
* Before Docker Container runs, the input files should be present in the VM_INPUT directory.
* The docker container runs and creates the output files in VM_OUTPUT directory

Below an example script of how to run while using volumes (on Windows), with only the output volume requires as we are using a streaming input. 

```
docker rm  osmparserwrapper
docker run --name osmparserwrapper  -e INPUT=https://api.openstreetmap.org/api/0.6/map?bbox=13.465661,52.504055,13.469817,52.506204 -e COUNTRY=Germany -e FIDELITY=fine -e PTINFRA=yes -e CLEAN=no -e OUTPUT=/output -v ${PWD}/VM_INPUT:/input/:rw  -v ${PWD}/VM_OUTPUT:/output/:rw  osmparserwrapper:latest
```

And one where it is assumed the Melbourne test resource is copied to the input volume directory and readily available

```
docker rm  osmparserwrapper
docker run --name osmparserwrapper  -e INPUT=/input/melbourne.osm.pbf -e COUNTRY=Australia -e FIDELITY=coarse -e CLEAN=yes -e OUTPUT=/output -v ${PWD}/VM_INPUT:/input/:rw  -v ${PWD}/VM_OUTPUT:/output/:rw  osmparserwrapper:latest
```

# Resources

A comprehensive yet easy to comprehend tutorial regarding creating docker images and containers can be found on

* https://www.youtube.com/watch?v=3c-iBn73dDE and the blogpost on 
* https://codefresh.io/docker-tutorial/java_docker_pipeline/

Something specific to the openjdk 11 and alpine image can be found on this post (mainly regarding the setup of the virtual file system)

* https://stackoverflow.com/questions/53669151/java-11-application-as-lightweight-docker-image