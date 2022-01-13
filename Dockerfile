# Base Alpine Linux based image with OpenJDK 11 only
FROM adoptopenjdk/openjdk11:jre-11.0.6_10-alpine

# PLANit version
ENV VERSION 0.0.1a1
# input URL or file, FILE NOT SUPPORTED YET
ENV INPUT https://api.openstreetmap.org/api/0.6/map?bbox=13.465661,52.504055,13.469817,52.506204
ENV BBOX ""
# remove Germany once it works!!!!!!!!!!!!
ENV COUNTRY Germany
# remove fine once it works!!!!!!!!!!!!
ENV FIDELITY fine
ENV RAIL ""
# remove yes once it works!!!!!!!!!!!!
ENV PTINFRA yes
ENV RMMODE ""
ENV ADDMODE ""
ENV CLEAN no
# remove Germany once it works!!!!!!!!!!!!
ENV OUTPUT ./output/Germany_pt

RUN mkdir /opt/jar

# copy application JAR (with libraries inside)
COPY target/planit-aurin-parser-*.jar /opt/jar

# specify default command
CMD ["sh", "-c", "java -jar /opt/jar/planit-aurin-parser-${VERSION}.jar --input ${INPUT} --bbox ${BBOX} --country ${COUNTRY} --fidelity ${FIDELITY} --rail ${RAIL} --ptinfra ${PTINFRA} --rmmode ${RMMODE} --addmode ${ADDMODE} --clean ${CLEAN} --output ${OUTPUT}"]

       