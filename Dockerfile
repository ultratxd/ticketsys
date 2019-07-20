FROM openjdk:8u181-jdk
VOLUME /tmp:/tmp
ADD target/ticketsys-0.0.1-SNAPSHOT.jar app.jar
RUN /bin/sh -c 'touch /app.jar'
ENV SERVICE_TAGS traefik.tags=cj.com,traefik.frontend.rule=PathPrefix:/ota/v1,traefik.enable=true,traefik.frontend.entryPoints=http,traefik.frontend.passHostHeader=true
ENV SERVICE_8080_CHECK_HTTP /actuator/health
ENV SERVICE_8080_NAME cj-ticketsys
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]