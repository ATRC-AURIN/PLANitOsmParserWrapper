# -----------------------------------------------------
# -----------------------------------------------------
# STAGE 1 - BUILD
# Maven build
FROM maven:3.8.4-jdk-11 as build

RUN mkdir -p /app/src
COPY ./src /app/src
COPY pom.xml /app
WORKDIR /app
RUN mvn clean install -Dmaven.test.skip=true


# -----------------------------------------------------
# -----------------------------------------------------
# STAGE 2 - BUILD
# EXECUTABLE JAR + Java environment
FROM adoptopenjdk/openjdk11:jre-11.0.6_10-alpine

# PLANit version
ENV VERSION 0.0.1a1

# Environmental variables to override during run if desired:
#---------------------------------------------------
ENV INPUT ""
ENV BBOX ""
ENV COUNTRY ""
ENV FIDELITY ""
ENV RAIL ""
ENV PTINFRA ""
ENV RMMODE ""
ENV ADDMODE ""
ENV CLEAN ""
ENV OUTPUT ""
#---------------------------------------------------

RUN mkdir -p /app/jar
RUN mkdir  /input
RUN mkdir  /output

# old
#RUN cp /app/target/planit-aurin-parser-*.jar /app/jar

# copy built jar from previous stage - intermediate results are discarded in image
RUN mkdir -p /app/jar
COPY --from=build /app/target/planit-aurin-parser-*.jar /app/jar/ 

COPY run_workflow_from_params.sh .

# specify default command
CMD ["sh", "./run_workflow_from_params.sh"]

