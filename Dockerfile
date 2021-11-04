FROM eclipse-temurin:17-alpine

RUN mkdir /app
COPY back-end/target/back-end-1.0-SNAPSHOT.jar /app/sansbot.jar
COPY resources/img-fonts /app/img-fonts
COPY resources/movies /app/movies

WORKDIR /app
ENTRYPOINT ["java", "-XX:StartFlightRecording=maxsize=120m", "-Djava.util.concurrent.ForkJoinPool.common.parallelism=1", "-jar", "sansbot.jar"]
