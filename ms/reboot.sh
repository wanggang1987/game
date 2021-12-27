echo "kill -9 `ps -ef | grep ms | grep -v grep | awk '{print $2}'`"
kill -9 `ps -ef | grep ms | grep -v grep | awk '{print $2}'`
echo "git pull"
git pull
sleep 1
echo "------> rm -rf ./src/main/resources/static/*"
rm -rf ./src/main/resources/static/*
echo "------> cp -rf ../cocos2.4.7/build/web-mobile/*  ./src/main/resources/static/."
cp -rf ../cocos2.4.7/build/web-mobile/*  ./src/main/resources/static/.
echo "nohup mvn clean spring-boot:run >> log &"
nohup mvn clean spring-boot:run >> log &
echo "tail -f log"
tail -f log
