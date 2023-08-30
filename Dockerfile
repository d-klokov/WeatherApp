FROM maven:3.9-amazoncorretto-11 as build
COPY . .
RUN mvn package -DskipTests

FROM tomcat:10.1-jdk11-corretto
COPY --from=build /target/WeatherApp.war /usr/local/tomcat/webapps
CMD ["catalina.sh", "run"]