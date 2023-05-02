FROM openjdk:17-alpine
RUN addgroup --system spring && adduser --system spring spring
USER spring:spring
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["sh","-c","java -Daws.serviceEndpoint=${AWS_SERVICE_ENDPOINT} -Daws.region=${AWS_REGION} -Daws.accessKey=${AWS_ACCESS_KEY} -Daws.secretKey=${AWS_SECRET_KEY} -jar /app.jar"]
