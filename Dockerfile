FROM openjdk:8-alpine

COPY target/uberjar/ask-project.jar /ask-project/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/ask-project/app.jar"]
