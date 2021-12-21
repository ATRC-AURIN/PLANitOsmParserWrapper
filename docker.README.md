# Docker information

In this file we keep track how to use docker with this Java repository

# Creating the docker image for OSM Parser Wrapper

* We use the Dockerfile in the root dir to create the image.  
* We use Alpine as the Linux distro since it is small
* There is no official jdk-11 and Alpine Docker image, so we use the one from adoptopenjdk, i.e., adoptopenjdk/openjdk11:jre-11.0.6_10-alpine

Restrictions to date:
  
* We only copy the locally created fat jar (from the maven clean install on pom.xml) as the only file added to the openjdk-alpine image.
* There is a hard coded call to an example command line configuration that is being run. This is to be extended to a configurable run via environment parameters.

# Resources

My initial steps to create docker images and containers are based on the tutorials on 

* https://www.youtube.com/watch?v=3c-iBn73dDE and the blogpost on 
* https://codefresh.io/docker-tutorial/java_docker_pipeline/

Something specific to the openjdk 11 and alpine image can be found on this post (mainly regarding the setup of the virtual file system)

* https://stackoverflow.com/questions/53669151/java-11-application-as-lightweight-docker-image


# Cheatsheet of Docker commands used:

**build the docker image**

From root dir of repo, build will use the Dockerfile file to perform the build

```
docker build -t osmparserwrapper:latest
```

**inspect file structure of built image**

To inspect the file structure of an image (not container), export it to a tar file and then isnpect the tar.

```
docker image save osmparserwrapper:latest > ./image.tar
```

**running the created image**

```
docker run osmparserwrapper:latest
```
