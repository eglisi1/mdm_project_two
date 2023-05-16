FROM openjdk:17

WORKDIR /usr/src/app
COPY . .

EXPOSE 8080

CMD ["java", "-jar", "/usr/src/app/target/mdm_project_two-0.0.1-SNAPSHOT.jar"]