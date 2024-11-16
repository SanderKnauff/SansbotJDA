FROM eclipse-temurin:21-alpine

RUN mkdir /app
COPY target/sansbot-jda-1.0.0-SNAPSHOT.jar /app/sansbot.jar

WORKDIR /app
ENTRYPOINT ["java", "-XX:StartFlightRecording=maxsize=120m", "-Djava.util.concurrent.ForkJoinPool.common.parallelism=1", "-jar", "sansbot.jar"]
