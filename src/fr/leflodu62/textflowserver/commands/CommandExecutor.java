package fr.leflodu62.textflowserver.commands;

import fr.leflodu62.textflowserver.ClientConnection;

public abstract class CommandExecutor {
	
	public abstract void execute(ClientConnection client, String command, String[] args);

	/** Just a shorthand for <br>
	 * <code>client.getTextFlowServerInstance().sendPrivateServerMessage(client, message);</code> **/
	public void sendMessage(ClientConnection client, String message) {
		client.getTextFlowServerInstance().sendPrivateServerMessage(client, message);
	}
	
}
