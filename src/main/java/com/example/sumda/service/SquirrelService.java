package com.example.sumda.service;

import com.example.sumda.dto.squirrel.response.FeedSquirrelResponseDto;
import com.example.sumda.dto.squirrel.response.UserSquirrelResponseDto;
import com.example.sumda.entity.SquirrelType;
import com.example.sumda.entity.User;
import com.example.sumda.entity.UserSquirrel;
import com.example.sumda.repository.SquirrelTypeRepository;
import com.example.sumda.repository.UserRepository;
import com.example.sumda.repository.UserSquirrelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SquirrelService {

    private final UserRepository userRepository;
    private final UserSquirrelRepository userSquirrelRepository;
    private final SquirrelTypeRepository squirrelTypeRepository;


    // 유저가 현재 키우고 있는 다람쥐 정보 불러오기
    public UserSquirrelResponseDto getUserSquirrel(long userId) {
        Optional<UserSquirrel> userSquirrel = userSquirrelRepository.findByUserIdAndEndDateIsNull(userId);
        Optional<User> user = userRepository.findById(userId);

        // 다람쥐 종류
        SquirrelType squTypeId = userSquirrel.get().getSquTypeId();
        Optional<SquirrelType> squirrelType = squirrelTypeRepository.findById(squTypeId.getId());

        UserSquirrelResponseDto dto = new UserSquirrelResponseDto();
        dto.setSquirrelId(userSquirrel.get().getId());
        dto.setType(squirrelType.get().getSqrType());
        dto.setLevel(userSquirrel.get().getLevel());
        dto.setFeed(userSquirrel.get().getFeed());
        dto.setUserAcorns(user.get().getUserAcorn());

        return dto;
    }

    // 다람쥐한테 도토리 주기 (개수 유저가 지정)
    public FeedSquirrelResponseDto feedSquirrelOnAcorns(Long userId, int acorns) {
        Optional<UserSquirrel> optionalUserSquirrel = userSquirrelRepository.findByUserIdAndEndDateIsNull(userId);

        if (!optionalUserSquirrel.isPresent()) {
            throw new IllegalArgumentException("해당 유저의 다람쥐를 찾을 수 없습니다."); // 예외 처리
        }

        Optional<User> optionalUser = userRepository.findById(userId);
        if (!optionalUser.isPresent()) {
            throw new IllegalArgumentException("유저를 찾을 수 없습니다."); // 예외 처리
        }

        UserSquirrel userSquirrel = optionalUserSquirrel.get();
        User user = optionalUser.get();

        // 다람쥐 종류
        SquirrelType squTypeId = userSquirrel.getSquTypeId();
        int level = userSquirrel.getLevel(); // 다람쥐 레벨
        Optional<SquirrelType> squirrelType = squirrelTypeRepository.findById(squTypeId.getId());
        int totalFeed = userSquirrel.getFeed() + acorns;

        // 유저 도토리 수 - 다람쥐한테 먹인 도토리 수
        int userAcorns = user.getUserAcorn() - acorns;
        if (userAcorns < 0) {
            throw new IllegalArgumentException("유저의 도토리가 부족합니다."); // 도토리가 부족한 경우 예외 처리
        }

        // 다람쥐가 도토리를 먹어서 도토리 개수가 특정 값 이상이 되면 레벨 업 해줘야 함
        // 10 / 20 / 30 / 40 -> 0-9 / 10-29 / 30-59 / 60-100
        if (totalFeed >= 0 && totalFeed <= 9) {
            if (level != 1) {
                userSquirrel.setLevel(1);
            }
        } else if (totalFeed >= 10 && totalFeed <= 29) {
            if(level != 2) {
                userSquirrel.setLevel(2); // 다람쥐 레벨 업
            }
        } else if (totalFeed >= 30 && totalFeed <= 59) {
            if (level != 3) {
                userSquirrel.setLevel(3); // 다람쥐 레벨 업
            }
        } else if (totalFeed >= 60 && totalFeed <= 100) {
            if (level !=4) {
                userSquirrel.setLevel(4); // 다람쥐 레벨 업
            }
        }

        // 변경된 값을 엔티티에 설정
        userSquirrel.setFeed(totalFeed); // 다람쥐의 feed 값 업데이트
        user.setUserAcorn(userAcorns); // 유저의 도토리 값 업데이트

        // 변경된 엔티티를 저장
        userSquirrelRepository.save(userSquirrel);
        userRepository.save(user);

        FeedSquirrelResponseDto feedSquirrelDto = new FeedSquirrelResponseDto();
        feedSquirrelDto.setUserAcorns(user.getUserAcorn());
        feedSquirrelDto.setType(squirrelType.get().getSqrType());
        feedSquirrelDto.setLevel(userSquirrel.getLevel());
        feedSquirrelDto.setAteAcorns(userSquirrel.getFeed());

        return feedSquirrelDto;
    }


    // 새로운 다람쥐 분양 받기

    // 다람쥐 컬렉션
}
