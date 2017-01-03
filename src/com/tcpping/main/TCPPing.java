package com.tcpping.main;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.validator.routines.InetAddressValidator;

import com.tcpping.catcher.Catcher;
import com.tcpping.pitcher.Pitcher;

public class TCPPing {

	public static Catcher processCatcherOptions(CommandLine commandLine) throws ParseException {
		String ipAddress = null;
		int portValue = 0;

		if (commandLine.hasOption("bind")) {
			ipAddress = commandLine.getOptionValue("bind");
			InetAddressValidator validator = new InetAddressValidator();
			if (!validator.isValidInet4Address(ipAddress))
				throw new ParseException("Invalid IP address format: " + ipAddress); 
		} else {
			throw new ParseException("Invalid parameter.");
		}

		if (commandLine.hasOption("port")) {
			String port = commandLine.getOptionValue("port");
			portValue = Integer.parseInt(port);
			if (portValue < 1024 || portValue > 65535)
				throw new ParseException("Invalid port number " + port);
		} else {
			throw new ParseException("Invalid parameter.");
		}

		return new Catcher(ipAddress, portValue);
	}

	public static Pitcher processPitcherOptions(CommandLine commandLine, String[] args) throws ParseException {
		int portValue = 0;
		int mpsValue = 0;
		int sizeValue = 0;
		String hostName = null;

		if (commandLine.hasOption("port")) {
			String port = commandLine.getOptionValue("port");
			portValue = Integer.parseInt(port);
			if (portValue < 1024 || portValue > 65535)
				throw new ParseException("Invalid port number " + port);
		} else {
			throw new ParseException("Invalid parameter.");
		}

		if (commandLine.hasOption("mps")) {
			String mps = commandLine.getOptionValue("mps");
			mpsValue = Integer.parseInt(mps);
			if (mpsValue < 1)
				throw new ParseException("Invalid mps number " + mps);
		} else {
			throw new ParseException("Invalid parameter.");
		}

		if (commandLine.hasOption("size")) {
			String size = commandLine.getOptionValue("size");
			sizeValue = Integer.parseInt(size);
			if (sizeValue < 50 || sizeValue > 3000)
				throw new ParseException("Invalid size number " + size);
		} else {
			sizeValue = 300;
		}

		if (args.length == 8)
			hostName = args[7];
		else
			hostName = args[5];

		return new Pitcher(hostName, portValue, mpsValue, sizeValue);
	}
	
	public static void main(String[] args) {

		Options options = new Options();
		CommandLine commandLine;
		CommandLineParser parser = new DefaultParser();

		// TODO: kreirati helper metodu
		options.addOption("c", false, "Role catcher.");
		options.addOption("p", false, "Role pitcher");
		options.addOption("bind", true, "Bind an IP addresss");
		options.addOption("port", true, "Port number");
		options.addOption("mps", true, "Number of messages per second");
		options.addOption("size", true, "Message size");
		Catcher catcher = null;
		Pitcher pitcher = null;

		try {
			commandLine = parser.parse(options, args);

			if (commandLine.hasOption("c")) {
				catcher = processCatcherOptions(commandLine);
				// TODO: staviti pitcher i catcher u interface
				catcher.startTCPApp();
			} else if (commandLine.hasOption("p")) {
				pitcher = processPitcherOptions(commandLine, args);
				pitcher.startTCPApp();
			} else {
				System.out.println("Invalid option");
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
