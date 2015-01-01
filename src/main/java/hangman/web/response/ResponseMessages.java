package hangman.web.response;

import static hangman.web.response.ResponseMessage.Status;


public class ResponseMessages {
	
	public static final <T> ResponseMessage<T> newSuccessMessage(T data) {
		return new ResponseMessage<T>(Status.SUCCESS, null, data);
	}
	
	public static final <T> ResponseMessage<T> newFailureMessage(String errorCode) {
		return new ResponseMessage<T>(Status.FAILURE, errorCode, null);
	}
	
	private ResponseMessages() {}
}
