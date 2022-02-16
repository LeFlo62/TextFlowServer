package fr.leflodu62.textflowserver.packets;

import fr.leflodu62.textflowserver.ClientConnection;

public abstract class Packet {

	protected final Object[] data;
	
	public Packet(Object[] data) {
		this.data = data;
	}
	
	public Object[] getData() {
		return data;
	}
	
	public void processData(ClientConnection clientConnection, Packet packet) {}

}
