package com.tcpping.main;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.validator.routines.InetAddressValidator;

import com.tcpping.catcher.Catcher;
import com.tcpping.pitcher.Pitcher;
import com.tcpping.tcpapp.TcpAppInterface;

public class TCPPing {
	private static InetAddressValidator validator = new InetAddressValidator();

	/**
	 * Print command help.
	 */
	public static void printHelp() {
		String helpMessage;

		helpMessage = String.format(""
				+ "Usage: TCPPing -c | -p\n"
				+ "               -c -bind <hostname | IP> -port <port>\n"
				+ "               -p -port <port> -mps <mps> [-size <size>] hostname\n"
				+ "\n"
				+ "Options:\n"
				+ "   -p          Pitcher mode.\n"
				+ "   -c          Catcher mode.\n"
				+ "   -bind       Bind catcher to a host name or IP address.\n"
				+ "   -port       Port number.\n"
				+ "   -mps        Messages per second.\n"
				+ "   -size       Message size.\n"
				+ "   hostname    Host name or IP address.\n");
		System.out.println(helpMessage);
	}

	/**
	 * Process catcher options
	 * @param commandLine   CommandLine object reference
	 * @return              Reference to a created catcher object
	 * @throws ParseException
	 * @throws NumberFormatException
	 */
	public static Catcher processOptions(CommandLine commandLine) throws ParseException, NumberFormatException {
		String ipAddress;
		int portValue;

		if (commandLine.hasOption("bind")) {
			ipAddress = commandLine.getOptionValue("bind");
			if (!validator.isValidInet4Address(ipAddress) && !validator.isValidInet6Address(ipAddress))
				throw new ParseException("Invalid IP address format: " + ipAddress); 
		} else
			throw new ParseException("Invalid parameter.");

		if (commandLine.hasOption("port")) {
			String port = commandLine.getOptionValue("port");
			portValue = Integer.parseInt(port);
			if (portValue <= 1024 || portValue > 65535)
				throw new ParseException("Invalid port number " + port);
		} else
			throw new ParseException("Invalid parameter.");

		return new Catcher(ipAddress, portValue);
	}

	/**
	 * Process command      line options.
	 * @param commandLine   CommandLine object reference
	 * @param args          Arguments passed from the command line
	 * @return              Reference to Pitcher object
	 * @throws ParseException
	 * @throws NumberFormatException
	 */
	public static Pitcher processOptions(CommandLine commandLine, String[] args) throws ParseException, NumberFormatException {
		int portValue;
		int mpsValue;
		int sizeValue;
		String hostName;

		if (commandLine.hasOption("port")) {
			String port = commandLine.getOptionValue("port");
			portValue = Integer.parseInt(port);
			if (portValue <= 1024 || portValue > 65535)
				throw new ParseException("Invalid port number " + port);
		} else
			throw new ParseException("Invalid parameter, should be port.");

		if (commandLine.hasOption("mps")) {
			String mps = commandLine.getOptionValue("mps");
			mpsValue = Integer.parseInt(mps);
			if (mpsValue < 1)
				throw new ParseException("Invalid mps number " + mps);
		} else
			throw new ParseException("Invalid parameter, should be mps.");

		if (commandLine.hasOption("size")) {
			String size = commandLine.getOptionValue("size");
			sizeValue = Integer.parseInt(size);
			if (sizeValue < 50 || sizeValue > 3000)
				throw new ParseException("Invalid size number " + size);
		} else
			sizeValue = 300;

		if (args.length == 8)
			hostName = args[7];
		else
			hostName = args[5];

		if (!validator.isValidInet4Address(hostName) && !validator.isValidInet6Address(hostName))
			throw new ParseException("Invalid IP address format: " + hostName);

		return new Pitcher(hostName, portValue, mpsValue, sizeValue);
	}

	public static void main(String[] args) {
		TcpAppInterface tcpApp;
		CommandLine commandLine;
		Options options = new Options();
		CommandLineParser parser = new DefaultParser();

		options.addOption("c", false, "");
		options.addOption("p", false, "");
		options.addOption("bind", true, "");
		options.addOption("port", true, "");
		options.addOption("mps", true, "");
		options.addOption("size", true, "");

		try {
			commandLine = parser.parse(options, args);

			if (commandLine.hasOption("c")) {
				if (args.length != 5)
					throw new ParseException("Invalid number of arguments: " + args.length);
				tcpApp = processOptions(commandLine);
			} else if (commandLine.hasOption("p")) {
				if (args.length != 8 && args.length != 7)
					throw new ParseException("Invalid number of arguments: " + args.length);
				tcpApp = processOptions(commandLine, args);
			} else
				throw new ParseException("Invalid option.");
			tcpApp.startTCPApp();
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			printHelp();
		} catch (NumberFormatException e) {
			System.out.println("Invalid paramter format.");
			System.out.println(e.getMessage());
			printHelp();
		}
	}
}