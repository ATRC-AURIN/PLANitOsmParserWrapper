# Base Alpine Linux based image with OpenJDK 11 only
FROM adoptopenjdk/openjdk11:jre-11.0.6_10-alpine
RUN mkdir /opt/jar
# copy application JAR (with libraries inside)
COPY target/planit-aurin-parser-*.jar /opt/jar
# specify default command
CMD ["java", "-jar", "/opt/jar/planit-aurin-parser-0.0.1a1.jar", "--input", "https://api.openstreetmap.org/api/0.6/map?bbox=13.465661,52.504055,13.469817,52.506204", "--country", "Germany", "--fidelity", "fine", "--pt-infra", "yes", "--clean_network", "no", "--output", "./output/Germany_pt"]

       