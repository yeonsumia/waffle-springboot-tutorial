#!/bin/bash

cd waffle-rookies-19.5-springboot
git pull
cd seminar
./gradlew bootJar
nohup java -jar -Dspring.profiles.active=prod build/libs/seminar-0.0.1-SNAPSHOT.jar & > /dev/null
exit 0
