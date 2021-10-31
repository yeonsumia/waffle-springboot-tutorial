#!/bin/bash

cd /
git pull
yeonsumia
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
cd waffle-rookies-19.5-springboot/seminar/
./gradlew bootJar
java -jar -Dspring.profiles.active=prod build/libs/seminar-0.0.1-SNAPSHOT.jar

exit 0
