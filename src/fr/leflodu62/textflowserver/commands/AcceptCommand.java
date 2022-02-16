package fr.leflodu62.textflowserver.commands;

import fr.leflodu62.textflowserver.ClientConnection;

public class AcceptCommand extends CommandExecutor {

	@Override
	public void execute(ClientConnection client, String command, String[] args) {
		if(client.getTextFlowServerInstance().isPendingRegistrationPreset()) {
			if(args.length == 1) {
				final boolean accepted = client.getTextFlowServerInstance().acceptPendingRegistration(client.getUsername(), args[0]);
				if(accepted) {
					sendMessage(client, "§aVous avez accepté " + args[0] + ".");
				} else {
					sendMessage(client, "§cVous ne pouvez pas accepter.");
				}
			} else {
				sendMessage(client, "§c/accept <pseudo>");
			}
		} else {
			sendMessage(client, "§cPersonne ne souhaite s'enregistrer sur le serveur.");
		}
	}

}
