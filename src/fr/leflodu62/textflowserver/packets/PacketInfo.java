package fr.leflodu62.textflowserver.packets;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PacketInfo {
	
	public int id();
	
	/** Should Packet be processed at server side ? **/
	public boolean serverSide();

}
