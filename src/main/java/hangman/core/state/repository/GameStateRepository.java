package hangman.core.state.repository;

import hangman.core.state.GameState;

import java.util.Optional;

public interface GameStateRepository {

    Optional<GameState> find(String gameId);
	
	void saveOrUpdate(String gameId, GameState value);
	
	void remove(String gameId);
}
