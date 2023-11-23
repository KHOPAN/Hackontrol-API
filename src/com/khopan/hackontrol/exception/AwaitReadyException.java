package com.khopan.hackontrol.exception;

public class AwaitReadyException extends RuntimeException {
	private static final long serialVersionUID = 7544083666000770445L;

	public AwaitReadyException() {
		super();
	}

	public AwaitReadyException(String message) {
		super(message);
	}

	public AwaitReadyException(String message, Throwable cause) {
		super(message, cause);
	}

	public AwaitReadyException(Throwable cause) {
		super(cause);
	}

	public AwaitReadyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
