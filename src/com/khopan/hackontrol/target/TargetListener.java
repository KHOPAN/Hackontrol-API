package com.khopan.hackontrol.target;

import com.khopan.hackontrol.Target;

public interface TargetListener {
	void onTargetConnected(Target target);
	void onTargetDisconnected(Target target);
}
