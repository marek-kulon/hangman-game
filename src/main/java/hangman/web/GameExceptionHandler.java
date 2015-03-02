package hangman.web;


import hangman.core.state.GuessAlreadyMadeException;
import hangman.web.exception.GameNotFoundException;
import hangman.web.exception.IllegalGuessValueException;
import hangman.web.exception.IllegalAllowedIncorrectGuessesNumberException;
import hangman.web.exception.SecretCategoryNotSupportedException;
import org.springframework.hateoas.VndErrors.VndError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import static hangman.util.AccessMonitor.MonitorException;


@ControllerAdvice
public class GameExceptionHandler {

    @ExceptionHandler(SecretCategoryNotSupportedException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    VndError onSecretCategoryNotSupported(SecretCategoryNotSupportedException ex) {
        return new VndError("1", ex.getMessage());
    }

    @ExceptionHandler(IllegalAllowedIncorrectGuessesNumberException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    VndError onIllegalAllowedIncorrectGuessesNumber(IllegalAllowedIncorrectGuessesNumberException ex) {
        return new VndError("2", ex.getMessage());
    }

    @ExceptionHandler(GameNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    VndError onGameNotFound(GameNotFoundException ex) {
        return new VndError("3", ex.getMessage());
    }

    @ExceptionHandler(IllegalGuessValueException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    VndError onIllegalGuessValue(IllegalGuessValueException ex) {
        return new VndError("4", ex.getMessage());
    }

    @ExceptionHandler(GuessAlreadyMadeException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    @ResponseBody
    VndError onGuessAlreadyMade(GuessAlreadyMadeException ex) {
        return new VndError("5", ex.getMessage());
    }


    @ExceptionHandler(MonitorException.class)
    @ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
    @ResponseBody
    VndError onMonitorFailure(MonitorException ex) {
        return new VndError("6", "Service Temporarily Unavailable"); // should happen very rarely -> this message should be sufficient
    }

}