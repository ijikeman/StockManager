# クラス単位で絞り込んで実行
# TEST="YahooProviderTest"
# ./test-exec-single.sh $TEST
sudo docker compose -f compose.yml -f compose.test.yml run --rm backend ./gradlew test --tests $1 --no-daemon

