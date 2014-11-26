package hangman.web.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public final class ResponseMessage<T> {
	
	private final Status status;
	private final String message;
	private final T data;
	
	public ResponseMessage(Status status, String message, T data) {
		this.status = status;
		this.message = message;
		this.data = data;
	}
	
	public Status getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}

	public T getData() {
		return data;
	}

	public enum Status {
		SUCCESS, FAILURE 
	}
}
