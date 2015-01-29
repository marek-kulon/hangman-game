package hangman.web;

import com.jayway.jsonpath.JsonPath;
import hangman.infrastructure.Application;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class GameControllerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    /**
     * new game test
     */

    @Test
    public void newGameInorrectCategoryParameter() throws Exception {
        mockMvc.perform(post("/game/new-game/xCategory/1").accept(APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    public void newGameInorrectMaxIncorrectGuessesNoParameter() throws Exception {
        mockMvc.perform(post("/game/new-game/animals/-1").accept(APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    public void newGameCorrectParameters() throws Exception {
        mockMvc.perform(post("/game/new-game/fruits/10").accept(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON))
                        // state
                .andExpect(jsonPath("$.state.category").value("fruits"))
                .andExpect(jsonPath("$.state.maxIncorrectGuessesNo").value(10))
                .andExpect(jsonPath("$.state.correctGuessesNo").value(0))
                .andExpect(jsonPath("$.state.incorrectGuessesNo").value(0))
                .andExpect(jsonPath("$.state.status").value("IN_PROGRESS"))
                        // guessed value
                .andExpect(jsonPath("$.guessedValue").value("___"))
                        // link
                .andExpect(jsonPath("$.links[0].href").exists());
    }


    /**
     * load
     */

    @Test
    public void loadGameIncorrectToken() throws Exception {
        mockMvc.perform(get("/game/load/xxxyyyzzz").accept(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }


    @Test
    public void loadCorrectToken() throws Exception {
        final String token = createNewGame("animals", 11);

        // load game
        mockMvc.perform(get("/game/load/" + token).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.state.category").value("animals"))
                .andExpect(jsonPath("$.state.maxIncorrectGuessesNo").value(11));
    }



    /**
     * guess
     */

    @Test
    public void
    guessGameIncorrectToken() throws Exception {
        mockMvc.perform(patch("/game/guess/xxxyyyzzz/a").accept(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void guessGameIllegalGuessValue() throws Exception {
        final String token = createNewGame("fruits", 10);

        mockMvc.perform(patch("/game/guess/" + token + "/-").accept(APPLICATION_JSON))
                .andExpect(status().isConflict());
    }


    @Test
    public void guessGameIncorrectGuessAndGameInProgress() throws Exception {
        final String token = createNewGame("fruits", 10);

        mockMvc.perform(patch("/game/guess/" + token + "/a").accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.guessedValue").value("___"))
                .andExpect(jsonPath("$.state.incorrectGuessesNo").value(1))
                .andExpect(jsonPath("$.state.status").value("IN_PROGRESS"));
    }


    @Test
    public void guessGameIncorrectGuessAndGameLost() throws Exception {
        final String token = createNewGame("fruits", 0);

        mockMvc.perform(patch("/game/guess/" + token + "/a").accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.guessedValue").value("Fig"))
                .andExpect(jsonPath("$.state.incorrectGuessesNo").value(1))
                .andExpect(jsonPath("$.state.status").value("LOST"));
    }


    @Test
    public void guessGameCorrectGuessesAndGameWon() throws Exception {
        final String token = createNewGame("fruits", 0);

        mockMvc.perform(patch("/game/guess/" + token + "/f").accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.guessedValue").value("F__"))
                .andExpect(jsonPath("$.state.correctGuessesNo").value(1))
                .andExpect(jsonPath("$.state.status").value("IN_PROGRESS"));

        mockMvc.perform(patch("/game/guess/" + token + "/i").accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.guessedValue").value("Fi_"))
                .andExpect(jsonPath("$.state.correctGuessesNo").value(2))
                .andExpect(jsonPath("$.state.status").value("IN_PROGRESS"));

        mockMvc.perform(patch("/game/guess/" + token + "/g").accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.guessedValue").value("Fig"))
                .andExpect(jsonPath("$.state.correctGuessesNo").value(3))
                .andExpect(jsonPath("$.state.status").value("WON"));
    }


    /**
     * Perform request generating new game and retrieve its token
     *
     * @return game token
     * @throws Exception
     */
    private String createNewGame(String category, int maxIncorrectGuessesNo) throws Exception {
        MvcResult result = mockMvc.perform(post("/game/new-game/" + category + "/" + maxIncorrectGuessesNo).accept(APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        System.out.println(content);
        return JsonPath.read(content, "$.links[0].href");
    }

}
