package hangman.core.state.repository.file.mapdb;

import hangman.core.guess.Guess;
import hangman.core.secret.Secret;
import hangman.core.secret.Secret.Category;
import hangman.core.state.GameState;
import hangman.core.state.repository.GameStateRepository;
import hangman.infrastructure.Application;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { Application.class })
public class GameStateRepositoryIntegrationTest {

	@Autowired
	GameStateRepository gameStateRepository;
	
	private GameState gs1;
	private GameState gs2;
	
	@Before
	public void setUp () {
		Set<Guess> guesses = new HashSet<>();
		guesses.add(Guess.newGuess('a'));

		gs1 = GameState.newGameState(2, Secret.newSecret("dog", Category.ANIMALS), guesses);
		gs2 = GameState.newGameState(2, Secret.newSecret("orange", Category.FRUITS), guesses);
	}

	@Test
	public void testSaveFind() {
		gameStateRepository.saveOrUpdate("1", gs1);
		GameState found = gameStateRepository.find("1");
		assertEquals(gs1, found);
	}
	
	@Test
	public void testSaveUpdateFind() {
		gameStateRepository.saveOrUpdate("1", gs1);
		gameStateRepository.saveOrUpdate("1", gs2);
		assertEquals(gs2, gameStateRepository.find("1"));
	}
	
	@Test
	public void testSaveRemove() {
		gameStateRepository.saveOrUpdate("1", gs1);
		gameStateRepository.remove("1");
		assertTrue(gameStateRepository.find("1")==null);
	}
}
