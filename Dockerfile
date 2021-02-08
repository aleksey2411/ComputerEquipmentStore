FROM openjdk:14
ENV DB_USER=postgres
ENV DB_PASSWORD=qwerty
ENV DB_URL=jdbc:postgresql://localhost:5432/computershop
COPY /target/computer-shop.jar computer-shop.jar
ENTRYPOINT ["java","-jar","computer-shop.jar"]
EXPOSE 8080

