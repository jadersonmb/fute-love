FROM openjdk:17-jdk
VOLUME /tmp
COPY target/fute-love-*.jar app.jar
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]