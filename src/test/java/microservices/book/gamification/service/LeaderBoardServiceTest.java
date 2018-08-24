package microservices.book.gamification.service;

import microservices.book.gamification.domain.LeaderBoardRow;
import microservices.book.gamification.repository.ScoreCardRepository;
import microservices.book.gamification.service.impl.LeaderBoardServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

public class LeaderBoardServiceTest {

    private LeaderBoardService leaderBoardService;

    @Mock
    private ScoreCardRepository scoreCardRepository;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        leaderBoardService = new LeaderBoardServiceImpl(scoreCardRepository);
    }

    @Test
    public void getCurrentLeaderBoardTest() {

//        Given
        Long userId = 1L;
        Long totalScore = 10L;

        LeaderBoardRow leaderBoardRow = new LeaderBoardRow(userId, totalScore);
        List<LeaderBoardRow> leaderBoardRowList = Collections.singletonList(leaderBoardRow);

        given(scoreCardRepository.findFirst10()).willReturn(leaderBoardRowList);

//        When
        List<LeaderBoardRow> returned = leaderBoardService.getCurrentLeaderBoard();

//        Assert
        assertThat(returned).isEqualTo(leaderBoardRowList);
    }
}
