#!/bin/bash

# hong: 리팩토링 해놔서 아래 변수만 설정에 맞게  변경하셔서 쓰심 됨다

# 설정 변수
CONTAINER_NAME="spring-backend"  # 컨테이너 prefix
CONTAINER_SETUP_DELAY_SECOND=10  # 컨테이너 실행 지연 시간
MAX_RETRY_COUNT=15  # 서버 상태 확인 최대 시도 횟수
RETRY_DELAY_SECOND=2  # 서버 상태 확인 지연 시간(초)
BLUE_SERVER_URL="http://127.0.0.1:8081"  # blue 서버 URL
GREEN_SERVER_URL="http://127.0.0.1:8082"  # green 서버 URL
HEALTH_END_POINT="/health"  # 서버 health check 를 위한 엔드포인트 (200 응답만 오면 됩니당)
BLUE_DOCKER_COMPOSE_FILE_NAME="docker-compose.blue"  # blue 의 docker-compose 파일명 (ex. `docker-compose.blue.yml`)
GREEN_DOCKER_COMPOSE_FILE_NAME="docker-compose.green"  # green 의 docker-compose 파일명 (ex. `docker-compose.green.yml`)
NGINX_SERVICE_URL_FILE="/etc/nginx/conf.d/service-url.inc"  # NGINX 설정 파일 경로

# NGINX 재로드 함수
reload_nginx() {
    echo "NGINX 설정 변경 작업 시작"

    if nginx -t; then
        nginx -s reload
        echo "NGINX 설정 재로드 완료"
    else
        echo "NGINX 설정 오류 -> 롤백 수행"
        echo "set \$service_url $CURRENT_SERVICE_URL;" > $NGINX_SERVICE_URL_FILE
        nginx -s reload
        exit 1
    fi
}

# 헬스 체크 함수
health_check() {
    local REQUEST_URL=$1
    local RETRY_COUNT=0

    while [ $RETRY_COUNT -lt $MAX_RETRY_COUNT ]; do
        echo "상태 검사 ( $REQUEST_URL )  ...  $(( RETRY_COUNT + 1 ))"
        sleep $RETRY_DELAY_SECOND

        REQUEST=$(curl -o /dev/null -s -w "%{http_code}\n" $REQUEST_URL)
        if [ "$REQUEST" -eq 200 ]; then
            echo "상태 검사 성공"
            return 0
        fi

        RETRY_COUNT=$(( RETRY_COUNT + 1 ))
    done

    return 1
}

# 컨테이너 시작 함수
start_container() {
    local COLOR=$1
    local DOCKER_COMPOSE_FILE_NAME=$2
    local SERVER_URL=$3

    echo "$COLOR 컨테이너를 띄우는 중"
    docker-compose -p ${CONTAINER_NAME}-$COLOR -f ${DOCKER_COMPOSE_FILE_NAME}.yml up -d
    echo "${CONTAINER_SETUP_DELAY_SECOND}초 대기"
    sleep $CONTAINER_SETUP_DELAY_SECOND

    echo "$COLOR 서버 상태 확인 시작"
    if ! health_check "$SERVER_URL$HEALTH_END_POINT"; then
        echo "$COLOR 배포 실패"
        echo "$COLOR 컨테이너 정리"
        docker-compose -p ${CONTAINER_NAME}-$COLOR -f ${DOCKER_COMPOSE_FILE_NAME}.yml down
        exit 1
    else
        echo "$COLOR 배포 성공"
        echo "set \$service_url $SERVER_URL;" > $NGINX_SERVICE_URL_FILE
        reload_nginx
        echo "기존 ${OTHER_COLOR} 컨테이너 정리"
        docker-compose -p ${CONTAINER_NAME}-${OTHER_COLOR} -f ${OTHER_DOCKER_COMPOSE_FILE_NAME}.yml down
    fi
}

# 메인 스크립트 로직
if [ "$(docker ps -q -f name=${CONTAINER_NAME}-blue)" ]; then
    echo "blue >> green"
    OTHER_COLOR="blue"
    OTHER_DOCKER_COMPOSE_FILE_NAME=$BLUE_DOCKER_COMPOSE_FILE_NAME
    start_container "green" $GREEN_DOCKER_COMPOSE_FILE_NAME $GREEN_SERVER_URL
else
    echo "green >> blue"
    OTHER_COLOR="green"
    OTHER_DOCKER_COMPOSE_FILE_NAME=$GREEN_DOCKER_COMPOSE_FILE_NAME
    start_container "blue" $BLUE_DOCKER_COMPOSE_FILE_NAME $BLUE_SERVER_URL
fi

#------------------------------------------------------------------------------------
#RUNNING_CONTAINER=$(sudo docker ps)
#echo "실행중인 컨테이너 목록: ${RUNNING_CONTAINER}"
#
## 실행 중인 도커 컴포즈 확인
#EXIST_BLUE=$(docker-compose -f /home/ubuntu/docker-compose.blue.yml ps -q blue)
#
#echo "EXIST_BLUE 값: ${EXIST_BLUE}"
#
#if [ -z "${EXIST_BLUE}" ]; then
#  # blue가 실행 중이지 않으면 blue 컨테이너를 시작
#  docker-compose -f /home/ubuntu/docker-compose.blue.yml up -d blue
#  START_CONTAINER="blue"
#  TERMINATE_CONTAINER="green"
#  START_PORT=8081
#  TERMINATE_PORT=8082
#else
#  # blue가 실행 중이면 green 컨테이너를 시작
#  docker-compose -f /home/ubuntu/docker-compose.green.yml up -d green
#  START_CONTAINER="green"
#  TERMINATE_CONTAINER="blue"
#  START_PORT=8082
#  TERMINATE_PORT=8081
#fi
#
#echo "${START_CONTAINER} 컨테이너를 실행 중입니다."
#
## 컨테이너를 빌드하고 실행하는 명령어
#sudo docker-compose -f /home/ubuntu/docker-compose.${START_CONTAINER}.yml up -d --build
#
#RUNNING_CONTAINER=$(sudo docker ps)
#echo "실행중인 컨테이너 목록: ${RUNNING_CONTAINER}"
#
## 서버 상태 확인
#for cnt in {1..10}; do
#    echo "서버 상태 확인 중..."
#
#    UP=$(curl -s http://127.0.0.1:${START_PORT}/api/health | grep 'UP')
#    if [ -n "${UP}" ]; then
#        echo "서버가 성공적으로 시작되었습니다."
#        break
#    fi
#
#    echo "서버가 아직 시작되지 않았습니다. 10초 후 다시 확인합니다."
#    sleep 10
#done
#
#if [ $cnt -eq 10 ]; then
#    echo "배포 실패: 서버가 시작되지 않았습니다."
#    exit 1
#fi
#
## NGINX 포트 변경 및 재시작
#echo "NGINX 설정 변경 및 재시작..."
#sudo sed -i "s/${TERMINATE_PORT}/${START_PORT}/" /etc/nginx/conf.d/default
#sudo service nginx reload
#
## 기존에 실행 중이던 컨테이너 종료
#echo "${TERMINATE_CONTAINER} 컨테이너를 종료 중입니다."
#sudo docker-compose -f /home/ubuntu/docker-compose.${TERMINATE_CONTAINER}.yml down
#
#echo "배포가 성공적으로 완료되었습니다."


