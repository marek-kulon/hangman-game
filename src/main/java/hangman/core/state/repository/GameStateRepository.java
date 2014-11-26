package hangman.core.state.repository;

import hangman.core.state.GameState;

public interface GameStateRepository {

	GameState find(String token);
	
	void saveOrUpdate(String token, GameState value);
	
	void remove(String token);
}
