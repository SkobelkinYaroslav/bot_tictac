FROM gradle:8.1.1 AS BUILD

WORKDIR /opt/app

COPY . /opt/app/build
WORKDIR /opt/app/build
RUN ls -l
RUN gradle :bootJar
RUN pwd

FROM openjdk:17.0.1-jdk-slim

#ARG JAR_FILE=./build/libs/bot_*.jar
#COPY --from=BUILD ${JAR_FILE} /opt/app/bottictac.jar
COPY . /opt/app/
WORKDIR /opt/app/build/libs/
EXPOSE 8082

ENTRYPOINT ["java","-jar","bot_tictac-0.0.1-SNAPSHOT.jar"]