package test;

import com.khopan.hackontrol.Hackontrol;
import com.khopan.hackontrol.Target;
import com.khopan.hackontrol.target.TargetListener;

public class Main {
	public static void main(String[] args) {
		Hackontrol hackontrol = new Hackontrol();
		hackontrol.setTargetListener(new Listener());
	}

	private static class Listener implements TargetListener {
		@Override
		public void onTargetConnected(Target target) {
			System.out.println("Connected: " + target.getMachineIdentifier());
		}

		@Override
		public void onTargetDisconnected(Target target) {
			System.out.println("Disconnected: " + target.getMachineIdentifier());
		}
	}
}
