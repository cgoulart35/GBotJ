FROM amazoncorretto:21 AS stage
WORKDIR /GBotJ
COPY build/libs/GBotJ-1.0-SNAPSHOT.jar ./GBotJ.jar

FROM stage AS dev

CMD ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar", "GBotJ.jar"]

FROM stage AS prod

CMD ["java", "-jar", "GBotJ.jar"]