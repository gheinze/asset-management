FROM java:openjdk-8u45-jdk
MAINTAINER glenn@gheinze.com
EXPOSE 8080
CMD java -jar a4-asset-manager-1.0-SNAPSHOT.jar
ADD build/a4-asset-manager-1.0-SNAPSHOT.jar .
