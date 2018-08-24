package microservices.book.gamification.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import microservices.book.gamification.domain.LeaderBoardRow;
import microservices.book.gamification.service.LeaderBoardService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringRunner.class)
@WebMvcTest(LeaderBoardController.class)
public class LeaderBoardControllerTest {

    @MockBean
    private LeaderBoardService leaderBoardService;

    @Autowired
    MockMvc mvc;

    private JacksonTester<List<LeaderBoardRow>> json;

    @Before
    public void setup() {
        JacksonTester.initFields(this, new ObjectMapper());
    }

    @Test
    public void getLeaderBoardTest() throws Exception {
//        Given
        LeaderBoardRow leaderBoardRow = new LeaderBoardRow(1L, 500L);
        LeaderBoardRow leaderBoardRow1 = new LeaderBoardRow(2L, 400L);
        List<LeaderBoardRow> leaderBoardRows = new ArrayList<>();
        Collections.addAll(leaderBoardRows, leaderBoardRow, leaderBoardRow1);
        given(leaderBoardService.getCurrentLeaderBoard()).willReturn(leaderBoardRows);

//        When
        MockHttpServletResponse response = mvc.perform(get("/leaders").accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

//        Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(json.write(leaderBoardRows).getJson());
    }
}
