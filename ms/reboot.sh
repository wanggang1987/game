echo "kill -9 `ps -ef | grep zuyi | grep -v grep | awk '{print $2}'`"
kill -9 `ps -ef | grep zuyi | grep -v grep | awk '{print $2}'`
echo "git pull"
git pull
sleep 1
echo "nohup mvn clean spring-boot:run >> log &"
nohup mvn clean spring-boot:run >> log &
echo "tail -f log"
tail -f log
