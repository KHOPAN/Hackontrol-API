package com.khopan.hackontrol.exception;

public class GuildNotFoundException extends RuntimeException {
	private static final long serialVersionUID = -1336762075843195228L;

	public GuildNotFoundException() {
		super();
	}

	public GuildNotFoundException(String message) {
		super(message);
	}

	public GuildNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public GuildNotFoundException(Throwable cause) {
		super(cause);
	}

	public GuildNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
