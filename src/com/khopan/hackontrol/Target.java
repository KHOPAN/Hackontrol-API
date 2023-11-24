package com.khopan.hackontrol;

public class Target {
	private final MachineId identifier;

	boolean connected;

	public Target(MachineId identifier) {
		this.identifier = identifier;
		this.connected = true;
	}

	public MachineId getMachineIdentifier() {
		return this.identifier;
	}
}
