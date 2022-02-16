package fr.leflodu62.textflowserver;

import java.util.Scanner;
import java.util.function.BiConsumer;

public class ConsoleEntriesThread extends Thread {
	
	private boolean running = false;

	private final Scanner scanner = new Scanner(System.in);
	
	private final TextFlowServer instance;
	private final BiConsumer<TextFlowServer, String> executor;
	
	public ConsoleEntriesThread(TextFlowServer instance, BiConsumer<TextFlowServer, String> executor) {
		this.instance = instance;
		this.executor = executor;
	}
	
	@Override
	public void run() {
		running = true;
		
		while(running) {
			if(scanner.hasNextLine()) {
				executor.accept(instance, scanner.nextLine());
			}
		}
		
		scanner.close();
	}
	
	public void setRunning(boolean running) {
		this.running = running;
	}

}
