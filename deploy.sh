#!/bin/bash

## spring boot build
#echo -e "Spring Boot 빌드 중입니다..."
#./gradlew build
#
## 현재 backend 확인
#ACTIVE_SERVER_URL="<http://0.0.0.0/active>"
#RESPONSE=$(curl -s $ACTIVE_SERVER_URL)
#
## 서버 응답에서 <h1>blue</h1> 패턴 찾기
#echo -e "서버 응답: $RESPONSE"
#if echo "$RESPONSE" | grep -q "blue"; then
#  NOW_BACKEND="blue"
#  NEW_BACKEND="green"
#  echo -e "현재 활성화된 서버는 $NOW_BACKEND 입니다. 새로운 서버는 $NEW_BACKEND 입니다."
#elif echo "$RESPONSE" | grep -q "green"; then
#  NOW_BACKEND="green"
#  NEW_BACKEND="blue"
#  echo -e "현재 활성화된 서버는 $NOW_BACKEND 입니다. 새로운 서버는 $NEW_BACKEND 입니다."
#else
#  echo -e "응답을 확인할 수 없습니다."
#  NEW_BACKEND="blue"
#fi
#
## NEW_BACKEND 빌드
#echo -e "${NEW_BACKEND} 서버를 빌드 중입니다..."
#docker-compose build --no-cache $NEW_BACKEND
#
## NEW_BACKEND 시작
#echo -e "${NEW_BACKEND} 서버를 시작 중입니다..."
#docker-compose up -d $NEW_BACKEND
#
## 새로운 서버가 안정화되도록 대기 (60초)
#echo -e "새로운 서버가 안정화되도록 대기 중입니다 60초..."
#for i in {1..60}; do
#  echo -e "$i/60..."
#  sleep 1
#done
#
## 새로운 서버의 안정성 확인 (5초 간격으로 3번 시도)
#if [ "$NEW_BACKEND" = "blue" ]; then
#  NEW_BACKEND_URL="<http://0.0.0.0:8081>"
#else
#  NEW_BACKEND_URL="<http://0.0.0.0:8082>"
#fi
#
#SUCCESS=0
#for i in {1..3}; do
#  echo -e "${NEW_BACKEND} 서버에 요청 시도 중... ($i/3)"
#  RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" $NEW_BACKEND_URL/healthcheck)
#
#  echo -e "$RESPONSE"
#  if [ "$RESPONSE" -eq 200 ]; then
#    SUCCESS=1
#    break
#  fi
#  sleep 5
#done
#
#if [ "$SUCCESS" -ne 1 ]; then
#  echo -e "${NEW_BACKEND} 서버가 정상적으로 시작되지 않았습니다."
#  exit 1
#fi
#
## Nginx 설정 파일 업데이트
#NGINX_CONF_TEMPLATE="./nginx/nginx-template.conf"
#NGINX_CONF="./nginx/nginx.conf"
#
#sed "s/{{BACKEND}}/$NEW_BACKEND/" $NGINX_CONF_TEMPLATE > $NGINX_CONF
#
## Nginx 컨테이너 내부로 설정 파일 복사
#docker cp $NGINX_CONF nginx:/etc/nginx/nginx.conf
#
## Nginx 컨테이너에서 Nginx 재시작
#docker exec nginx nginx -s reload
#
#echo -e "Nginx 설정이 업데이트되었습니다. 현재 backend는 $NEW_BACKEND 입니다."
#
## 현재 운용되는 서버 한번 더 확인
#echo -e "새롭게 생성된 서버를 확인합니다."
#
## 서버 응답에서 <h1>blue</h1> 패턴 찾기
#RESPONSE=$(curl -s $ACTIVE_SERVER_URL)
#echo -e "서버 응답: $RESPONSE"
#
#echo -e "${NOW_BACKEND} 를 종료합니다..."
#docker-compose stop $NOW_BACKEND
#docker-compose rm -f $NOW_BACKEND
#
#docker container prune -f
#docker image prune -f

#------------------------------------------------------------------------------------

RUNNING_CONTAINER=$(sudo docker ps)
echo "실행중인 컨테이너 목록: ${RUNNING_CONTAINER}"

# 실행 중인 도커 컴포즈 확인
EXIST_BLUE=$(docker-compose -f /home/ubuntu/docker-compose.blue.yml ps -q be-blue)

echo "EXIST_BLUE 값: ${EXIST_BLUE}"

if [ -z "${EXIST_BLUE}" ] # -z는 문자열 길이가 0이면 true. blue가 실행 중이지 않다는 의미.
then
  docker-compose -f /home/ubuntu/docker-compose.blue.yml up -d be-blue
#        # green이 실행 중인 경우
#        START_CONTAINER=be-blue
#        TERMINATE_CONTAINER=be-green
#        START_PORT=8081
#        TERMINATE_PORT=8082
#        DOCKER_COMPOSE_FILE="/home/ubuntu/docker-compose.blue.yml"
else
  docker-compose -f /home/ubuntu/docker-compose.green.yml up -d be-green
#        # blue가 실행 중인 경우
#        START_CONTAINER=be-green
#        TERMINATE_CONTAINER=be-blue
#        START_PORT=8082
#        TERMINATE_PORT=8081
#        DOCKER_COMPOSE_FILE="/home/ubuntu/docker-compose.green.yml"
fi

sudo docker-compose -f /home/ubuntu/docker-compose.${START_CONTAINER}.yml up -d --build ${START_CONTAINER}

RUNNING_CONTAINER=$(sudo docker ps)
echo "실행중인 컨테이너 목록: ${RUNNING_CONTAINER}"

for cnt in {1..10} # 10번 실행
do
        echo "check server start.."

        # 스프링부트에 등록했던 actuator로 실행되었는지 확인
        UP=$(curl -s http://127.0.0.1:${START_PORT}/api/health | grep 'UP')
        if [ -z "${UP}" ] # 실행되었다면 break
        then
                echo "server not start.."
        else
                break
        fi

        echo "wait 10 seconds" # 10 초간 대기
        sleep 10
done

if [ $cnt -eq 10 ] # 10번동안 실행이 안되었으면 배포 실패, 강제 종료
then
        echo "deployment failed."
        exit 1
fi

echo "server start!"
echo "change nginx server port"

# 종료되는 포트를 새로 시작되는 포트로 값을 변경해줍니다.
sudo sed -i "s/${TERMINATE_PORT}/${START_PORT}/" /etc/nginx/conf.d/default

# 새로운 포트로 스프링부트가 구동 되고, nginx의 포트를 변경해주었다면, nginx 재시작해줍니다.
echo "nginx reload.."
sudo service nginx reload

# 기존에 실행 중이었던 docker-compose는 종료시켜줍니다.
echo "${TERMINATE_CONTAINER} down"
sudo docker-compose -f /home/ubuntu/docker-compose.${TERMINATE_CONTAINER}.yml down
echo "success deployment"

#------------------------------------------------------------------------------------

## 설정 변수
#CONTAINER_NAME="be"  # 컨테이너 prefix
#CONTAINER_SETUP_DELAY_SECOND=10  # 컨테이너 실행 지연 시간
#MAX_RETRY_COUNT=15  # 서버 상태 확인 최대 시도 횟수
#RETRY_DELAY_SECOND=2  # 서버 상태 확인 지연 시간(초)
#BLUE_SERVER_URL="http://127.0.0.1:8081"  # blue 서버 URL
#GREEN_SERVER_URL="http://127.0.0.1:8082"  # green 서버 URL
#HEALTH_END_POINT="/api/health"  # 서버 health check 를 위한 엔드포인트 (200 응답만 오면 됨)
#DOCKER_COMPOSE_FILE="/home/ubuntu/docker-compose.yml"  # docker-compose 파일 경로 지정
#NGINX_BLUE_CONF="/etc/nginx/nginx.blue.conf"  # NGINX의 blue 설정 파일 경로
#NGINX_GREEN_CONF="/etc/nginx/nginx.green.conf"  # NGINX의 green 설정 파일 경로
#NGINX_CONF_FILE="/etc/nginx/nginx.conf"  # 실제로 사용되는 NGINX 설정 파일 경로
#
## NGINX 재로드 함수
#reload_nginx() {
#    local NGINX_CONF=$1
#    echo "NGINX 설정 변경 작업 시작"
#
#    sudo cp $NGINX_CONF $NGINX_CONF_FILE
#
#    if nginx -t; then
#        sudo nginx -s reload
#        echo "NGINX 설정 재로드 완료"
#    else
#        echo "NGINX 설정 오류 -> 롤백 수행"
#        exit 1
#    fi
#}
#
## 헬스 체크 함수
#health_check() {
#    local REQUEST_URL=$1
#    local RETRY_COUNT=0
#
#    while [ $RETRY_COUNT -lt $MAX_RETRY_COUNT ]; do
#        echo "상태 검사 ( $REQUEST_URL )  ...  $(( RETRY_COUNT + 1 ))"
#        sleep $RETRY_DELAY_SECOND
#
#        REQUEST=$(curl -o /dev/null -s -w "%{http_code}\n" $REQUEST_URL)
#        if [ "$REQUEST" -eq 200 ]; then
#            echo "상태 검사 성공"
#            return 0
#        fi
#
#        RETRY_COUNT=$(( RETRY_COUNT + 1 ))
#    done
#
#    return 1
#}
#
## 컨테이너 시작 함수
#start_container() {
#    local COLOR=$1
#    local SERVER_URL=$2
#    local NGINX_CONF=$3
#
#    echo "$COLOR 컨테이너를 띄우는 중"
#    docker-compose --env-file -f $DOCKER_COMPOSE_FILE up -d ${CONTAINER_NAME}-${COLOR}
#    echo "${CONTAINER_SETUP_DELAY_SECOND}초 대기"
#    sleep $CONTAINER_SETUP_DELAY_SECOND
#
#    echo "$COLOR 서버 상태 확인 시작"
#    if ! health_check "$SERVER_URL$HEALTH_END_POINT"; then
#        echo "$COLOR 배포 실패"
#        echo "$COLOR 컨테이너 정리"
#        docker-compose -f $DOCKER_COMPOSE_FILE down ${CONTAINER_NAME}-${COLOR}
#        exit 1
#    else
#        echo "$COLOR 배포 성공"
#        reload_nginx $NGINX_CONF  # NGINX 설정을 변경 후 재로드
#        echo "기존 ${OTHER_COLOR} 컨테이너 정리"
#        docker-compose -f $DOCKER_COMPOSE_FILE down ${CONTAINER_NAME}-${OTHER_COLOR}
#    fi
#}
#
## 메인 스크립트 로직
#if [ "$(docker ps -q -f name=${CONTAINER_NAME}-blue)" ]; then
#    echo "blue >> green"
#    OTHER_COLOR="blue"
#    start_container "green" $GREEN_SERVER_URL $NGINX_GREEN_CONF
#else
#    echo "green >> blue"
#    OTHER_COLOR="green"
#    start_container "blue" $BLUE_SERVER_URL $NGINX_BLUE_CONF
#fi