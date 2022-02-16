package fr.leflodu62.textflowserver;

import java.util.function.BiConsumer;
import java.util.logging.Level;

import org.mindrot.jbcrypt.BCrypt;

public class ConsoleCommands {

	//end
	public static final BiConsumer<TextFlowServer, String[]> END_COMMAND = (instance, args) -> {
		instance.close();
		TextFlowServer.LOGGER.log(Level.INFO, "server closing.");
	};
	
	//add username password hashed
	public static final BiConsumer<TextFlowServer, String[]> ADD_COMMAND = (instance, args) -> {
		instance.getDBHandler().addUser(args[0], ((args.length == 3 && ("1".equals(args[2]) || "true".equals(args[2]))) ? args[1] : BCrypt.hashpw(args[1], BCrypt.gensalt())));
		TextFlowServer.LOGGER.log(Level.INFO, "user added.");
	};
	
	//remove usename
	public static final BiConsumer<TextFlowServer, String[]> REMOVE_COMMAND = (instance, args) -> {
		instance.getDBHandler().removeUser(args[0]);
		TextFlowServer.LOGGER.log(Level.INFO, "user removed.");
	};
	
	//accept usename
		public static final BiConsumer<TextFlowServer, String[]> ACCEPT_COMMAND = (instance, args) -> {
			instance.acceptPendingRegistration(args[0]);
			TextFlowServer.LOGGER.log(Level.INFO, "user registered.");
		};
	
	public static final BiConsumer<TextFlowServer, String> EXECUTOR = (instance, line) -> {
		final String command = line.split(" ")[0];
		final String[] args = line.replace(command + " ", "").split(" ");
		switch(command) {
			case "stop":
			case "end":
				END_COMMAND.accept(instance, args);
				break;
			case "add":
				ADD_COMMAND.accept(instance, args);
				break;
			case "remove":
				REMOVE_COMMAND.accept(instance, args);
				break;
			case "accept":
				ACCEPT_COMMAND.accept(instance, args);
				break;
		}
	};

}
