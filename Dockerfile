FROM gradle:8.4.0-jdk17
WORKDIR /app
COPY . .
RUN gradle build -x test

RUN jarfile=$(find build/libs/ -maxdepth 1 -name "*.jar" ! -name "*plain.jar") && \
    echo "#!/bin/sh" > /run.sh && \
    echo "java -jar $jarfile" >> /run.sh && \
    chmod +x /run.sh

EXPOSE 8080
CMD ["/run.sh"]
