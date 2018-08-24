package microservices.book.gamification.service;

import microservices.book.gamification.client.MultiplicationResultAttemptClient;
import microservices.book.gamification.client.dto.MultiplicationResultAttempt;
import microservices.book.gamification.domain.Badge;
import microservices.book.gamification.domain.BadgeCard;
import microservices.book.gamification.domain.GameStats;
import microservices.book.gamification.domain.ScoreCard;
import microservices.book.gamification.repository.BadgeCardRepository;
import microservices.book.gamification.repository.ScoreCardRepository;
import microservices.book.gamification.service.impl.GameServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

public class GameServiceTest {

    private static final int LUCKY_NUMBER = 44;

    private GameService gameService;

    @Mock
    private ScoreCardRepository scoreCardRepository;

    @Mock
    private BadgeCardRepository badgeCardRepository;

    @Mock
    private MultiplicationResultAttemptClient client;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        gameService = new GameServiceImpl(scoreCardRepository, badgeCardRepository, client);
    }

    @Test
    public void processFirstCorrectAttemptTest() {
//        Given
        Long userId = 1L;
        Long attemptId = 1L;
        int totalScore = 10;

        ScoreCard scoreCard = new ScoreCard(userId, attemptId);
        List<ScoreCard> scoreCardList = new ArrayList<>();
        scoreCardList.add(scoreCard);
        List<BadgeCard> badgeCardList = new ArrayList<>();
        badgeCardList.add(new BadgeCard(userId, Badge.FIRST_WON));
        MultiplicationResultAttempt mra = new MultiplicationResultAttempt("asd",1, 2, 2, true);

        GameStats gameStats = new GameStats(userId, totalScore, badgeCardList.stream().map(BadgeCard::getBadge).collect(Collectors.toList()));

        given(scoreCardRepository.getTotalScoreForUser(userId)).willReturn(totalScore);
        given(scoreCardRepository.findByUserIdOrderByScoreTimestampDesc(userId)).willReturn(scoreCardList);
        given(badgeCardRepository.findByUserIdOrderByBadgeTimestampDesc(userId)).willReturn(Collections.emptyList());
        given(client.retrieveMultiplicationResultAttemptById(any())).willReturn(mra);

//        When
        GameStats gameStatsService = gameService.newAttemptForUser(userId, attemptId, true);

//        Assert
        assertThat(gameStatsService.getScore()).isEqualTo(ScoreCard.DEFAULT_SCORE);
        assertThat(gameStatsService.getBadges()).containsOnly(Badge.FIRST_WON);
    }

    @Test
    public void processCorrectAttemptForBronzeBadgeTest() {
//        Given
        Long userId = 1L;
        Long attemptId = 1L;
        int totalScore = 100;
        BadgeCard firstWonBadge = new BadgeCard(userId, Badge.FIRST_WON);
        MultiplicationResultAttempt mra = new MultiplicationResultAttempt("asd",1, 2, 2, true);

        given(scoreCardRepository.getTotalScoreForUser(userId)).willReturn(totalScore);
        given(scoreCardRepository.findByUserIdOrderByScoreTimestampDesc(userId)).willReturn(createNScoreCards(10, userId));
        given(badgeCardRepository.findByUserIdOrderByBadgeTimestampDesc(userId)).willReturn(Collections.singletonList(firstWonBadge));
        given(client.retrieveMultiplicationResultAttemptById(any())).willReturn(mra);

//        When
        GameStats gameStats = gameService.newAttemptForUser(userId, attemptId, true);

//        Assert
        assertThat(gameStats.getScore()).isEqualTo(ScoreCard.DEFAULT_SCORE);
        assertThat(gameStats.getBadges()).containsOnly(Badge.BRONZE_MULTIPLICATOR);
    }

    @Test
    public void processCorrectAttemptForSilverBadgeTest() {
//        Given
        Long userId = 1L;
        Long attemptId = 1L;
        int totalScore = 500;
        BadgeCard firstWonBadge = new BadgeCard(userId, Badge.FIRST_WON);
        MultiplicationResultAttempt mra = new MultiplicationResultAttempt("asd",1, 2, 2, true);

        given(scoreCardRepository.getTotalScoreForUser(userId)).willReturn(totalScore);
        given(scoreCardRepository.findByUserIdOrderByScoreTimestampDesc(userId)).willReturn(createNScoreCards(50, userId));
        given(badgeCardRepository.findByUserIdOrderByBadgeTimestampDesc(userId)).willReturn(Collections.singletonList(firstWonBadge));
        given(client.retrieveMultiplicationResultAttemptById(any())).willReturn(mra);

//        When
        GameStats gameStats = gameService.newAttemptForUser(userId, attemptId, true);

//        Assert
        assertThat(gameStats.getScore()).isEqualTo(ScoreCard.DEFAULT_SCORE);
        assertThat(gameStats.getBadges()).contains(Badge.BRONZE_MULTIPLICATOR, Badge.SILVER_MULTIPLICATOR);
    }

    @Test
    public void processCorrectAttemptForGoldBadgeTest() {
//        Given
        Long userId = 1L;
        Long attemptId = 1L;
        int totalScore = 1000;
        BadgeCard firstWonBadge = new BadgeCard(userId, Badge.FIRST_WON);
        MultiplicationResultAttempt mra = new MultiplicationResultAttempt("asd",1, 2, 2, true);

        given(scoreCardRepository.getTotalScoreForUser(userId)).willReturn(totalScore);
        given(scoreCardRepository.findByUserIdOrderByScoreTimestampDesc(userId)).willReturn(createNScoreCards(100, userId));
        given(badgeCardRepository.findByUserIdOrderByBadgeTimestampDesc(userId)).willReturn(Collections.singletonList(firstWonBadge));
        given(client.retrieveMultiplicationResultAttemptById(any())).willReturn(mra);

//        When
        GameStats gameStats = gameService.newAttemptForUser(userId, attemptId, true);

//        Assert
        assertThat(gameStats.getScore()).isEqualTo(ScoreCard.DEFAULT_SCORE);
        assertThat(gameStats.getBadges()).contains(Badge.BRONZE_MULTIPLICATOR, Badge.SILVER_MULTIPLICATOR, Badge.GOLD_MULTIPLICATOR);
    }

    @Test
    public void processCorrectAttemptForLuckyNumberBadgeTest() {
//        Given
        Long userId = 1L;
        Long attemptId = 1L;
        int totalScore = 20;
        BadgeCard firstWonBadge = new BadgeCard(userId, Badge.FIRST_WON);
        MultiplicationResultAttempt mra = new MultiplicationResultAttempt("asd",LUCKY_NUMBER, 2, 2, true);

        given(scoreCardRepository.getTotalScoreForUser(userId)).willReturn(totalScore);
        given(scoreCardRepository.findByUserIdOrderByScoreTimestampDesc(userId)).willReturn(createNScoreCards(2, userId));
        given(badgeCardRepository.findByUserIdOrderByBadgeTimestampDesc(userId)).willReturn(Collections.singletonList(firstWonBadge));
        given(client.retrieveMultiplicationResultAttemptById(any())).willReturn(mra);

//        When
        GameStats gameStats = gameService.newAttemptForUser(userId, attemptId, true);

//        Assert
        assertThat(gameStats.getScore()).isEqualTo(ScoreCard.DEFAULT_SCORE);
        assertThat(gameStats.getBadges()).contains(Badge.LUCKY_NUMBER);
    }

    @Test
    public void processWrongAttemptTest() {
//        Given
        Long userId = 1L;
        Long attemptId = 1L;

//        When
        GameStats gameStats = gameService.newAttemptForUser(userId, attemptId, false);

//        Assert
        assertThat(gameStats.getScore()).isEqualTo(0);
        assertThat(gameStats.getBadges()).isEmpty();
    }

    private List<ScoreCard> createNScoreCards(int n, Long userId) {
        return IntStream.range(0, n)
                .mapToObj(i -> new ScoreCard(userId, (long)i))
                .collect(Collectors.toList());
    }

    @Test
    public void retrieveStatsForUserTest() {
//        Given
        Long userId = 1L;
        int totalScore = 10;

        given(scoreCardRepository.getTotalScoreForUser(userId)).willReturn(totalScore);
        given(badgeCardRepository.findByUserIdOrderByBadgeTimestampDesc(userId)).willReturn(Collections.emptyList());

//        When
        GameStats gameStats = gameService.retrieveStatsForUser(userId);

//        Assert
        assertThat(gameStats.getScore()).isEqualTo(totalScore);
        assertThat(gameStats.getBadges()).isEmpty();
    }
}
