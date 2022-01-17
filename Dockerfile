# Base Alpine Linux based image with OpenJDK 11 only
FROM adoptopenjdk/openjdk11:jre-11.0.6_10-alpine

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

RUN mkdir /opt/jar

# copy application JAR (with libraries inside)
COPY target/planit-aurin-parser-*.jar /opt/jar

# specify default command
CMD ["sh", "-c", "java -jar /opt/jar/planit-aurin-parser-${VERSION}.jar \ 
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

       