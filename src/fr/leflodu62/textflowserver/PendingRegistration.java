package fr.leflodu62.textflowserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class PendingRegistration {
	
	private static final Timer TIMER = new Timer();
	
	private final TextFlowServer instance;
	
	private final TimerTask task;
	
	private final String username;
	private final List<String> accepted = new ArrayList<>();
	private final Consumer<Boolean> c;
	
	public PendingRegistration(TextFlowServer instance, String username, Consumer<Boolean> c) {
		this.instance = instance;
		this.username = username;
		this.c = c;
		
		task = new TimerTask() {
			@Override
			public void run() {
				instance.broadcast("§a" + username + " Ne sera pas parmi vous.");
				refuse();
			}
		};
		TIMER.schedule(task, 5*60*1000);
	}
	
	public String getUsername() {
		return username;
	}
	
	public boolean accept(String username) {
		if(!accepted.contains(username)) {
			accepted.add(username);
			if(accepted.size() == instance.getLoggedUserCount()) {
				accept();
			}
			return true;
		}
		return false;
	}
	
	public void accept() {
		task.cancel();
		c.accept(true);
	}
	
	private void refuse() {
		c.accept(false);
	}

}
