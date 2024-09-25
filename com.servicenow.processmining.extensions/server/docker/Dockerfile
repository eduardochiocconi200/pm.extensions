FROM eclipse-temurin:17-jdk
RUN mkdir -p /usr/local/pm
COPY target/com.servicenow.processmining.extensions.server.api-0.0.1-SNAPSHOT.jar /usr/local/pm/pm-backend.jar
ENTRYPOINT ["java", "-jar", "/usr/local/pm/pm-backend.jar"]
