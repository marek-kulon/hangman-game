package hangman.web;


import hangman.core.state.GuessAlreadyMadeException;
import hangman.web.exception.GameNotFoundException;
import hangman.web.exception.IllegalGuessValueException;
import hangman.web.exception.IllegalMaxIncorrectGuessesNumberException;
import hangman.web.exception.SecretCategoryNotSupportedException;

import org.springframework.hateoas.VndErrors.VndError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


@ControllerAdvice
public class GameExceptionHandler {

	@ExceptionHandler(SecretCategoryNotSupportedException.class)
	@ResponseStatus(value = HttpStatus.CONFLICT)
	@ResponseBody VndError onSecretCategoryNotSupported(SecretCategoryNotSupportedException ex) {
		return new VndError("1", ex.getMessage());
	}
	
	@ExceptionHandler(IllegalMaxIncorrectGuessesNumberException.class)
	@ResponseStatus(value = HttpStatus.CONFLICT)
	@ResponseBody VndError onIllegalMaxIncorrectGuessesNumber(IllegalMaxIncorrectGuessesNumberException ex) {
		return new VndError("2", ex.getMessage());
	}
	
	@ExceptionHandler(GameNotFoundException.class)
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	@ResponseBody VndError onGameNotFound(GameNotFoundException ex) {
		return new VndError("3", ex.getMessage());
	}
	
	@ExceptionHandler(IllegalGuessValueException.class)
	@ResponseStatus(value = HttpStatus.CONFLICT)
	@ResponseBody VndError onIllegalGuessValue(IllegalGuessValueException ex) {
		return new VndError("4", ex.getMessage());
	}
	
	@ExceptionHandler(GuessAlreadyMadeException.class)
	@ResponseStatus(value = HttpStatus.CONFLICT)
	@ResponseBody VndError onGuessAlreadyMade(GuessAlreadyMadeException ex) {
		return new VndError("5", ex.getMessage());
	}
	
}