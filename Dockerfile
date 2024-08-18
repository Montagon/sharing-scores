FROM azul/zulu-openjdk-alpine:17.0.11-17.50-jre-headless AS build
WORKDIR /workspace

COPY .git /workspace/.git
COPY build.gradle.kts /workspace
COPY gradle.properties /workspace
COPY gradlew /workspace
COPY settings.gradle.kts /workspace
COPY src /workspace/src
COPY gradle /workspace/gradle
RUN echo $(ls -la)

RUN ./gradlew build --parallel --no-daemon
RUN echo $(ls build/distributions)

RUN tar -xf build/distributions/sharing-scores-*.tar && mv sharing-scores-*/ app/
RUN rm -f sharing-scores.tar

EXPOSE 8080:8080
ENTRYPOINT ["java", "-cp", "app/lib/*", "org.jcma.sharingscores.SharingScoresServer"]
