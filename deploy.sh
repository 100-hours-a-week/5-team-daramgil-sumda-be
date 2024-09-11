#!/bin/bash

RUNNING_CONTAINER=$(sudo docker ps)
echo "실행중인 컨테이너 목록: ${RUNNING_CONTAINER}"

# 실행 중인 도커 컴포즈 확인
EXIST_BLUE=$(docker-compose -f /home/ubuntu/docker-compose.blue.yml ps -q spring-backend)

echo "EXIST_BLUE 값: ${EXIST_BLUE}"

if [ -z "${EXIST_BLUE}" ]; then
  # green이 실행 중이면 blue 컨테이너를 시작
  docker-compose -f /home/ubuntu/docker-compose.blue.yml up -d spring-backend
  START_CONTAINER="blue"
  TERMINATE_CONTAINER="green"
  START_PORT=8081
  TERMINATE_PORT=8082
else
  # blue가 실행 중이면 green 컨테이너를 시작
  docker-compose -f /home/ubuntu/docker-compose.green.yml up -d spring-backend
  START_CONTAINER="green"
  TERMINATE_CONTAINER="blue"
  START_PORT=8082
  TERMINATE_PORT=8081
fi

echo "${START_CONTAINER} 컨테이너를 실행 중입니다."

# 컨테이너를 빌드하고 실행하는 명령어
sudo docker-compose -f /home/ubuntu/docker-compose.${START_CONTAINER}.yml up -d --build

RUNNING_CONTAINER=$(sudo docker ps)
echo "실행중인 컨테이너 목록: ${RUNNING_CONTAINER}"

# 서버 상태 확인
for cnt in {1..20}; do
    echo "서버 상태 확인 중..."

    UP=$(curl -s http://127.0.0.1:${START_PORT}/api/health | grep 'UP')
    if [ -n "${UP}" ]; then
        echo "서버가 성공적으로 시작되었습니다."
        break
    fi

    echo "서버가 아직 시작되지 않았습니다. 10초 후 다시 확인합니다."
    sleep 10
done

if [ $cnt -eq 20 ]; then
    echo "배포 실패: 서버가 시작되지 않았습니다."
    exit 1
fi

# NGINX 포트 변경 및 재시작
echo "NGINX 설정 변경 및 재시작..."
sudo cp /etc/nginx/nginx.blue.conf /etc/nginx/nginx.green.conf
sudo service nginx reload

#이전 컨테이너 중지
docker-compose -f /home/ubuntu/docker-compose.${TERMINATE_CONTAINER}.yml down
echo "${TERMINATE_CONTAINER} 컨테이너를 중지했습니다."
echo "배포 완료"