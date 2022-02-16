package fr.leflodu62.textflowserver;

import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.security.KeyPair;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import fr.leflodu62.textflowserver.ClientConnection.State;
import fr.leflodu62.textflowserver.commands.CommandRegistry;
import fr.leflodu62.textflowserver.packets.PacketRegistry;
import fr.leflodu62.textflowserver.packets.message.MessagePacket;
import fr.leflodu62.textflowserver.packets.message.ServerMessagePacket;
import fr.leflodu62.textflowserver.secure.RSAHelper;
import fr.leflodu62.textflowserver.secure.Token;

public final class TextFlowServer {

	public static Logger LOGGER = setupLogger("TextFlowServer");
	private KeyPair encryptionStarter = null;
	private ServerSocket server = null;

	private CommandRegistry commandRegistry;
	
	private DBHandler dbHandler;
	
	private final List<ClientConnection> connections = new ArrayList<>();
	
	private final int maxUsers;
	
	private boolean isRunning = true;
	private final HashMap<String, PendingRegistration> pendingRegstrations = new HashMap<>();

	public TextFlowServer(int port, int maxUsers) {
		this.maxUsers = maxUsers;
		try {
			LOGGER.log(Level.INFO, "Generating RSA Key pair");
			encryptionStarter = RSAHelper.genKeyPair();
			
			LOGGER.log(Level.INFO, "Initializing Database");
			dbHandler = new DBHandler();
			dbHandler.createTableIfDoesntExist();
			
			LOGGER.log(Level.INFO, "Initializing Tokens");
			Token.init();
			
			LOGGER.log(Level.INFO, "Registering Packets");
			PacketRegistry.registerPackets();
			
			LOGGER.log(Level.INFO, "Registering Commands");
			commandRegistry = new CommandRegistry();
			commandRegistry.register();
			
			LOGGER.log(Level.INFO, "Listening for console commands");
			new ConsoleEntriesThread(this, ConsoleCommands.EXECUTOR).start();
			
			LOGGER.log(Level.INFO, "Starting server on port " + port + "...");
			server = new ServerSocket(port);
			
			Runtime.getRuntime().addShutdownHook(new Thread(){@Override
			public void run(){
		        close();
			}});
		} catch (final BindException e) {
			LOGGER.log(Level.SEVERE, "FAILED TO BIND TO PORT.");
			System.exit(1);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public void start() {
		final Thread main = new Thread(() -> {
			while (isRunning && !server.isClosed()) {
				try {
					final Socket client = server.accept();
					
					final ClientConnection connection = new ClientConnection(this, client);
					connections.add(connection);
					
					final Thread clientThread = new Thread(connection);
					clientThread.start();
				} catch (final SocketException e) {
					if(isRunning) {
						e.printStackTrace();
					}
				} catch (final Exception e) {
					e.printStackTrace();
					System.exit(2);
				}
				try {
					Thread.sleep(10);
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		});
		main.start();
	}
	
	public boolean isConnected(String username) {
		return !connections.isEmpty() && connections.stream().filter(c -> c.getState().equals(State.LOGGED)).map(ClientConnection::getUsername).anyMatch(u -> u.equals(username));
	}
	
	public void broadcast(String message, ClientConnection... except) {
		final List<ClientConnection> exception = Arrays.asList(except);
		connections.stream().filter(c -> c.getState().equals(State.LOGGED) && !exception.contains(c)).forEach(c -> sendPrivateServerMessage(c, message));
	}
	
	public void sendPrivateServerMessage(ClientConnection client, String message) {
		client.sendPacket(new ServerMessagePacket(message));
	}
	
	public void broadcastMessage(final ClientConnection origin, String username, String message) {
		connections.stream().filter(c -> !c.equals(origin)).forEach(c -> {c.sendPacket(new MessagePacket(username, message)); System.out.println("???");});
	}
	
	public boolean stillConnected(ClientConnection connection) {
		return connections.contains(connection);
	}
	
	public void closeConnection(ClientConnection connection) {
		try {
			connection.close();
		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			connections.remove(connection);
		}
	}

	public void close() {
		isRunning = false;
		connections.forEach(c -> c.disconnect("Arrêt du serveur", true));
		new Timer().scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if(connections.size() == 0) {
					try {
						server.close();
						LOGGER.log(Level.INFO, "server closed.");
						System.exit(-1);
						cancel();
					} catch (final Exception e) {
						e.printStackTrace();
					}
				}
			}
		}, 0, 10);
	}
	
	public boolean isServerFull() {
		return getMaxUsers() != -1 && connections.size() >= getMaxUsers();
	}
	
	public int getMaxUsers() {
		return maxUsers;
	}
	
	private static Logger setupLogger(final String name) {
		final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
		logger.setUseParentHandlers(false);
		
		logger.addHandler(new ConsoleHandler());
		
		for(final Handler h : logger.getHandlers()) {
			h.setFormatter(new Formatter() {
				@Override
				public String format(LogRecord record) {
					
					String content = record.getMessage();
					
					if(record.getThrown() instanceof Exception) {
						if(record.getThrown().getCause() != null) {
							content += " -> " +  record.getThrown().getCause().toString() + " Caused by:" + System.lineSeparator();
							for(final StackTraceElement e : record.getThrown().getCause().getStackTrace()) {
								content += "    " + e.toString() + System.lineSeparator();
							}
						}
					}
					
					return "[" + getCurrentTime(false, true) + "] ["+ (record.getThrown() != null ? record.getLevel().getLocalizedName() : name) +"] [" + record.getSourceClassName() + "]: " + content + "\n";
				}
			});
		}
		
		return logger;
	}
	
	private static String getCurrentTime(boolean date, boolean hours) {
		if(!date && !hours) return null;
		final SimpleDateFormat format = new SimpleDateFormat((date ? "dd-MM-YY" : "") + (date && hours ? " " : "") + (hours ? "HH:mm:ss" : ""));
		final Calendar calendar = Calendar.getInstance();
		return format.format(calendar.getTime());
	}
	
	public KeyPair getEncryptionStarter() {
		return encryptionStarter;
	}
	
	public DBHandler getDBHandler() {
		return dbHandler;
	}

	public CommandRegistry getCommandRegistry() {
		return commandRegistry;
	}

	public boolean isPendingRegistrationPreset() {
		return pendingRegstrations.size() > 0;
	}
	
	public void acceptPendingRegistration(String username) {
		pendingRegstrations.get(username).accept();
	}
	
	public boolean acceptPendingRegistration(String username, String accepted) {
		return pendingRegstrations.containsKey(accepted) && pendingRegstrations.get(accepted).accept(username);
	}
	
	public void addPendingRegistration(PendingRegistration registration) {
		pendingRegstrations.put(registration.getUsername(), registration);
	}

	public long getLoggedUserCount() {
		return connections.stream().filter(c -> c.getState().equals(State.LOGGED)).count();
	}
}
