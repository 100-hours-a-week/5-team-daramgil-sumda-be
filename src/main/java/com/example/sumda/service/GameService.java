package com.example.sumda.service;

import com.example.sumda.dto.game.request.GameResultRequestDto;
import com.example.sumda.dto.game.response.GameResultResponseDto;
import com.example.sumda.dto.game.response.HighestScoreResponseDto;
import com.example.sumda.entity.GameLog;
import com.example.sumda.entity.GameType;
import com.example.sumda.entity.User;
import com.example.sumda.exception.CustomException;
import com.example.sumda.exception.ErrorCode;
import com.example.sumda.repository.GameLogRepository;
import com.example.sumda.repository.GameTypeRepository;
import com.example.sumda.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameLogRepository gameLogRepository;
    private final GameTypeRepository gameTypeRepository;
    private final UserRepository userRepository;

    @Transactional
    public GameResultResponseDto gameResult(Long userId, GameResultRequestDto gameResult) {

        Long gameId = gameResult.getGameId(); // 게임 종류 id
        int score = gameResult.getScore(); // 게임 점수

        // 게임 전 유저가 보유하고 있는 도토리 수
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        int userAcorns = user.getUserAcorn();

        int getAcorns = score/10; // 적립될 도토리 개수 (10점 당 1개)


        // 적립 후 유저 도토리 수
        int afterUserAcorns = userAcorns + getAcorns;

        // GameType을 gameId로 조회
        GameType gameType = gameTypeRepository.findById(gameId)
                .orElseThrow(() -> new CustomException(ErrorCode.Game_NOT_FOUND));

        GameLog gameLog = new GameLog();
        gameLog.setUser(user);
        gameLog.setGameType(gameType);
        gameLog.setGameName(gameType.getGameName());
        gameLog.setScore(score);
        gameLog.setGetAcorns(getAcorns);
        gameLog.setStartTime(gameResult.getStartTime());
        gameLog.setEndTime(Timestamp.valueOf(LocalDateTime.now()));
        gameLogRepository.save(gameLog); // 게임 결과 저장

        if (getAcorns==0) {
            GameResultResponseDto zeroDto = new GameResultResponseDto();
            zeroDto.setGetAcorns(getAcorns);
            zeroDto.setUserAcorns(userAcorns);
            return zeroDto;
        } else {
            user.setUserAcorn(afterUserAcorns);
            userRepository.save(user); // 도토리 적립
        }

        GameResultResponseDto dto = new GameResultResponseDto();
        dto.setGetAcorns(getAcorns);
        dto.setUserAcorns(afterUserAcorns);

        return dto;
    }

    // 본인의 가장 높은 점수 반환
    public HighestScoreResponseDto highestScore(Long userId, Long gameTypeId) {

        List<GameLog> gameLogs = gameLogRepository.findTopByUserIdAndGameTypeIdOrderByScoreDesc(userId,gameTypeId);

        // 게임 이력이 없는 경우
        if (gameLogs.isEmpty()) {
            throw new CustomException(ErrorCode.Game_LOG_NOT_FOUND);
        }

        // 가장 높은 점수 가져오기
        GameLog highestLog = gameLogs.get(0);

        HighestScoreResponseDto userScore = new HighestScoreResponseDto();
        userScore.setGameTypeId(highestLog.getGameType().getId());
        userScore.setHighestScore(highestLog.getScore());

        return userScore;
    }
}
