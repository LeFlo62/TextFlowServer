package fr.leflodu62.textflowserver;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

public class Main {
	
	public static void main(String[] args) {
		final OptionParser optionparser = new OptionParser();
		final OptionSpec<Integer> portOption = optionparser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(19842);
		final OptionSpec<Integer> maxUsersOption = optionparser.accepts("maxPlayer").withRequiredArg().ofType(Integer.class).defaultsTo(-1);
		final OptionSet optionset = optionparser.parse(args);
		
		new TextFlowServer(portOption.value(optionset), maxUsersOption.value(optionset)).start();
	}

}
