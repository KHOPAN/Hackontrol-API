package com.khopan.hackontrol.exception;

public class NoTextChannelException extends RuntimeException {
	private static final long serialVersionUID = -4616542325810633903L;

	public NoTextChannelException() {
		super();
	}

	public NoTextChannelException(String message) {
		super(message);
	}

	public NoTextChannelException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoTextChannelException(Throwable cause) {
		super(cause);
	}

	public NoTextChannelException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
