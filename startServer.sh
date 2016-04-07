##gradle build -x test -x findbugsMain -x findbugsTest -x checkStyleMain -x checkStyleTest
java -jar ./build/libs/group-04-1.0.jar --server 7777 -map ./NightlyTests/NormalGameTest/default.map -s 1 -p 2
