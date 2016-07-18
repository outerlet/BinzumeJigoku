package jp.onetake.binzumejigoku.exception;

public class ContentsException extends Exception {
	public ContentsException(String message) {
		super(message);
	}

	public ContentsException(Throwable throwable) {
		super(throwable);
	}

	public ContentsException(String message, Throwable cause) {
		super(message, cause);
	}
}
