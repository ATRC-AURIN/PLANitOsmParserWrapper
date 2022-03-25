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

# old
#RUN cp /app/target/planit-aurin-parser-*.jar /app/jar

# install bash and dos2unix
RUN apk add --no-cache bash dos2unix

# copy built jar from previous stage - intermediate results are discarded in image
RUN mkdir -p /app/jar
COPY --from=build /app/target/planit-aurin-parser-*.jar /app/jar/ 

COPY atrc/run_workflow_from_params.sh .

# run dos2unix on the entrypoint script to prevent incorrect
# EOL characters being added if building image on windows
RUN dos2unix /run_workflow_from_params.sh

# specify default command
CMD ["sh", "./run_workflow_from_params.sh"]

