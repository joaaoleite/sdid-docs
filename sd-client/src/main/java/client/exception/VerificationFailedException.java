package client.exception;

public class VerificationFailedException extends Exception{

	private String _errorMessage;

	public VerificationFailedException(String errorMessage){
		_errorMessage = errorMessage;
	}

	public String getMessage(){
		return _errorMessage;
	}
}