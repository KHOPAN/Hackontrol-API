package com.khopan.hackontrol;

public class MachineId {
	private final String identifier;

	public MachineId(String identifier) {
		this.identifier = identifier;
	}

	public String getIdentifier() {
		return this.identifier;
	}

	@Override
	public String toString() {
		return this.identifier;
	}
}
