FROM adoptopenjdk/openjdk11:jre-11.0.6_10-alpine
FROM maven:3.8.4-jdk-11

# PLANit version
ENV VERSION 0.0.1a1

# user specific environmental variables to override:
#---------------------------------------------------

# input URL: FILE NOT SUPPORTED YET
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

RUN mkdir -p /app/src
COPY ./src /app/src
COPY pom.xml /app
WORKDIR /app
RUN mvn clean install -Dmaven.test.skip=true

RUN mkdir -p /app/jar
RUN mkdir  /input
RUN mkdir  /output

# copy application JAR (with libraries inside) CHECK IF cp IS CORRECT NOT SURE
RUN cp /app/target/planit-aurin-parser-*.jar /app/jar
#COPY ./target/planit-aurin-parser-*.jar /app/jar

# specify default command
CMD ["sh", "-c", "java -jar /app/jar/planit-aurin-parser-${VERSION}.jar \ 
  --input ${INPUT} \
  --bbox ${BBOX} \
  --country ${COUNTRY} \
  --fidelity ${FIDELITY} \
  --rail ${RAIL} \
  --ptinfra ${PTINFRA} \
  --rmmode ${RMMODE} \
  --addmode ${ADDMODE} \
  --clean ${CLEAN} \
  --output ${OUTPUT}\
  "]

       