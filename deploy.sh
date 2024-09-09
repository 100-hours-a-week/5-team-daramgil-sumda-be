#!/bin/bash

# 설정 변수
CONTAINER_NAME="be"  # 컨테이너 prefix
CONTAINER_SETUP_DELAY_SECOND=10  # 컨테이너 실행 지연 시간
MAX_RETRY_COUNT=15  # 서버 상태 확인 최대 시도 횟수
RETRY_DELAY_SECOND=2  # 서버 상태 확인 지연 시간(초)
BLUE_SERVER_URL="http://127.0.0.1:8081"  # blue 서버 URL
GREEN_SERVER_URL="http://127.0.0.1:8082"  # green 서버 URL
HEALTH_END_POINT="/api/health"  # 서버 health check 를 위한 엔드포인트 (200 응답만 오면 됨)
DOCKER_COMPOSE_FILE="/home/5-team-daramgil-sumda-be/docker-compose.yml"  # docker-compose 파일 경로 지정
NGINX_BLUE_CONF="/etc/nginx/nginx.blue.conf"  # NGINX의 blue 설정 파일 경로
NGINX_GREEN_CONF="/etc/nginx/nginx.green.conf"  # NGINX의 green 설정 파일 경로
NGINX_CONF_FILE="/etc/nginx/nginx.conf"  # 실제로 사용되는 NGINX 설정 파일 경로

# NGINX 재로드 함수
reload_nginx() {
    local NGINX_CONF=$1
    echo "NGINX 설정 변경 작업 시작"

    sudo cp $NGINX_CONF $NGINX_CONF_FILE

    if nginx -t; then
        sudo nginx -s reload
        echo "NGINX 설정 재로드 완료"
    else
        echo "NGINX 설정 오류 -> 롤백 수행"
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
    local SERVER_URL=$2
    local NGINX_CONF=$3

    echo "$COLOR 컨테이너를 띄우는 중"
    docker-compose -f $DOCKER_COMPOSE_FILE up -d ${CONTAINER_NAME}-${COLOR}
    echo "${CONTAINER_SETUP_DELAY_SECOND}초 대기"
    sleep $CONTAINER_SETUP_DELAY_SECOND

    echo "$COLOR 서버 상태 확인 시작"
    if ! health_check "$SERVER_URL$HEALTH_END_POINT"; then
        echo "$COLOR 배포 실패"
        echo "$COLOR 컨테이너 정리"
        docker-compose -f $DOCKER_COMPOSE_FILE down ${CONTAINER_NAME}-${COLOR}
        exit 1
    else
        echo "$COLOR 배포 성공"
        reload_nginx $NGINX_CONF  # NGINX 설정을 변경 후 재로드
        echo "기존 ${OTHER_COLOR} 컨테이너 정리"
        docker-compose -f $DOCKER_COMPOSE_FILE down ${CONTAINER_NAME}-${OTHER_COLOR}
    fi
}

# 메인 스크립트 로직
if [ "$(docker ps -q -f name=${CONTAINER_NAME}-blue)" ]; then
    echo "blue >> green"
    OTHER_COLOR="blue"
    start_container "green" $GREEN_SERVER_URL $NGINX_GREEN_CONF
else
    echo "green >> blue"
    OTHER_COLOR="green"
    start_container "blue" $BLUE_SERVER_URL $NGINX_BLUE_CONF
fi