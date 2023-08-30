FROM tomcat:10.1-jdk11-corretto

COPY ./target/WeatherApp.war /use/local/tomcat/webapps/WeatherApp.war

CMD ["catalina.sh", "run"]