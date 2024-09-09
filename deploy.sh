#!/bin/bash

# Docker Compose 설치 (만약 설치되어 있지 않다면)
if ! [ -x "$(command -v docker-compose)" ]; then
  echo 'docker-compose가 설치되어 있지 않으므로 설치합니다.' >&2
  sudo curl -L "https://github.com/docker/compose/releases/download/v2.20.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
  sudo chmod +x /usr/local/bin/docker-compose
fi

IS_GREEN=$(docker ps | grep be-green) # 현재 실행중인 App이 blue인지 확인합니다.
DEFAULT_CONF="/etc/nginx/nginx.conf"

if [ -z "$IS_GREEN" ]; then # blue가 실행중이라면
  echo "### BLUE => GREEN ###"

  echo "1. get green image"
  docker-compose pull ghcr.io/kwongiyeon/5-team-daramgil-sumda-be:latest # green 이미지를 가져옵니다.

  echo "2. green container up"
  docker-compose up -d be-green # green 컨테이너를 실행합니다.

  while [ 1 = 1 ]; do
    echo "3. green health check..."
    sleep 3

    REQUEST=$(curl --silent --fail http://127.0.0.1:8082) # green으로 request
    if [ $? -eq 0 ]; then # 서비스 가능하면 health check 중지
      echo "health check success"
      break ;
    fi
  done;

  echo "4. reload nginx"
  sudo cp /etc/nginx/nginx.green.conf /etc/nginx/nginx.conf
  sudo nginx -s reload

  echo "5. blue container down"
  docker-compose stop be-blue
else
  echo "### GREEN => BLUE ###"

  echo "1. get blue image"
  docker-compose pull ghcr.io/kwongiyeon/5-team-daramgil-sumda-be:latest

  echo "2. blue container up"
  docker-compose up -d be-blue

  while [ 1 = 1 ]; do
    echo "3. blue health check..."
    sleep 3
    REQUEST=$(curl --silent --fail http://127.0.0.1:8081) # blue로 request
    if [ $? -eq 0 ]; then # 서비스 가능하면 health check 중지
      echo "health check success"
      break ;
    fi
  done;

  echo "4. reload nginx"
  sudo cp /etc/nginx/nginx.blue.conf /etc/nginx/nginx.conf
  sudo nginx -s reload

  echo "5. green container down"
  docker-compose stop be-green
fi