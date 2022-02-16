package fr.leflodu62.textflowserver.commands;

import java.nio.channels.AlreadyBoundException;
import java.util.HashMap;

import fr.leflodu62.textflowserver.ClientConnection;

public final class CommandRegistry {
	
	private final HashMap<String, CommandExecutor> COMMAND_REGISTRY = new HashMap<>();
	
	public void register() {
		registerCommand("accept", new AcceptCommand());
	}

	private void registerCommand(String command, CommandExecutor executor) {
		if(COMMAND_REGISTRY.containsKey(command)) {
			throw new AlreadyBoundException();
		}
		
		COMMAND_REGISTRY.put(command, executor);
	}
	
	public void processCommand(ClientConnection client, String plainMessage) {
		final String[] words = plainMessage.replaceFirst("/", "").split(" ");
		if(COMMAND_REGISTRY.containsKey(words[0])) {
			final String[] args = new String[words.length-1];
			System.arraycopy(words, 1, args, 0, args.length);
			COMMAND_REGISTRY.get(words[0]).execute(client, plainMessage, args);
		} else {
			client.getTextFlowServerInstance().sendPrivateServerMessage(client, "§cCommande inconnue.");
		}
	}

}
