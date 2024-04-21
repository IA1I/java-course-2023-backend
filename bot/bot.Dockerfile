FROM openjdk:21

COPY target/bot.jar bot.jar

EXPOSE 8090

ENTRYPOINT ["java", "-jar", "-Dapp.token=${TOKEN}", "/bot.jar"]
