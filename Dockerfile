FROM eclipse-temurin:21.0.5_11-jdk
ARG JAR_FILE
COPY ${JAR_FILE} uttu.jar
ENTRYPOINT ["java","--add-opens", "java.desktop/java.awt.font=ALL-UNNAMED", "--add-opens", "java.base/java.util=ALL-UNNAMED","--add-opens", "java.base/java.lang.reflect=ALL-UNNAMED","--add-opens", "java.base/java.lang=ALL-UNNAMED","--add-opens", "java.base/java.io=ALL-UNNAMED", "--add-opens", "java.base/java.text=ALL-UNNAMED","-jar","/uttu.jar"]