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
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { Application.class })
public class GameStateRepositoryIntegrationTest {

	@Autowired
    private
    GameStateRepository gameStateRepository;
	
	private GameState gs1;
	private GameState gs2;
	
	@Before
	public void setUp () {
		Set<Guess> guesses = new HashSet<>();
		guesses.add(Guess.of('a'));

		gs1 = GameState.newGameState(2, Secret.of("dog", Category.ANIMALS), guesses);
		gs2 = GameState.newGameState(2, Secret.of("orange", Category.FRUITS), guesses);
	}

	@Test
	public void testSaveFind() {
		gameStateRepository.saveOrUpdate("1", gs1);
		Optional<GameState> found = gameStateRepository.find("1");
		assertEquals(gs1, found.get());
	}
	
	@Test
	public void testSaveUpdateFind() {
		gameStateRepository.saveOrUpdate("1", gs1);
		gameStateRepository.saveOrUpdate("1", gs2);
		assertEquals(gs2, gameStateRepository.find("1").get());
	}
	
	@Test
	public void testSaveRemove() {
		gameStateRepository.saveOrUpdate("1", gs1);
		gameStateRepository.remove("1");
		assertFalse(gameStateRepository.find("1").isPresent());
	}
}
