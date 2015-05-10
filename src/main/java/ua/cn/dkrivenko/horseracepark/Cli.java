package ua.cn.dkrivenko.horseracepark;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *
 * @author Dmitry Krivenko <dmitrykrivenko at gmail.com>
 */
public class Cli {

	private static final String NEWLINE = System.getProperty("line.separator");

	private static final String COMMAND_TEMPLATE = "^Q|R|W (?<wonnum>\\d+)|(?<num>\\d+) (?<bet>.*)";
	private static final Pattern commandPattern = Pattern.compile(COMMAND_TEMPLATE);

	private final BufferedReader inReader;
	private final PrintStream outStream;

	private final TellerMachine tellerMachine = new TellerMachine();

	public Cli(InputStream inputStream, PrintStream outStream) {
		this.inReader = new BufferedReader(new InputStreamReader(inputStream));
		this.outStream = outStream;
	}

	private String getInput() {
		try {
			return inReader.readLine();
		} catch (IOException e) {
			throw new RuntimeException("Failed to read from input: ", e);
		}
	}

	private void writeOutput(String str) {
		this.outStream.println(str);
	}

	private void displayTellerMachineState() {
		writeOutput(getInventoryAsString());
		writeOutput(getHorsesAsString());
	}

	private String getInventoryAsString() {
		return String.format("Inventory:%s%s",
			NEWLINE, getDispensingAsString(tellerMachine.getInventory()));
	}

	private String getHorsesAsString() {
		String horsesAsString = tellerMachine.getHorses().entrySet().stream().map(entry -> {
			return String.format("%d,%s,%d,%s",
				entry.getValue().getNumber(),
				entry.getValue().getName(),
				entry.getValue().getOdds(),
				entry.getValue().getWinStatus());
		}).collect(Collectors.joining(NEWLINE));

		return String.format("Horses:%s%s",
			NEWLINE, horsesAsString);
	}

	private void displayPayout(String horseName, int payout, Map<Integer, Integer> dispensing) {
		writeOutput(getPayoutAsString(horseName, payout, dispensing));
	}

	private String getPayoutAsString(String horseName, int payout, Map<Integer, Integer> dispensing) {
		String dispensingAsString = String.format("Dispensing:%s%s", NEWLINE, getDispensingAsString(dispensing));
		return String.format("Payout: %s,%d%s%s",
			horseName, payout, NEWLINE, dispensingAsString);
	}

	private String getDispensingAsString(Map<Integer, Integer> dispensing) {
		return dispensing.entrySet().stream().map(entry -> {
			return String.format("$%d,%d", entry.getKey(), entry.getValue());
		}).collect(Collectors.joining(NEWLINE));
	}

	private void displayErrorMessage(String message) {
		writeOutput(message);
	}

	private void startEventLoop() {
		displayTellerMachineState();
		while (true) {
			String input = getInput();
			if (input == null) {
				break;
			}
			
			String command = input.toUpperCase().trim();
			Matcher matcher = commandPattern.matcher(command);
			if (!matcher.matches()) {
				displayErrorMessage(String.format("Invalid Command: %s", input));
			} else if (command.equals("Q")) { //'Q' or 'q' - quits the application
				System.exit(0);
			} else if (command.equals("R")) { //'R' or 'r' - restocks the cash inventory
				tellerMachine.restock();
			} else if (command.startsWith("W")) { //'W' or 'w' [<horse number>] - sets the winning horse number
				int horseNumber = Integer.parseInt(matcher.group("wonnum"));
				if (!tellerMachine.isValidHorseNumber(horseNumber)) {
					displayErrorMessage(String.format("Invalid Horse Number: %d", horseNumber));
				} else {
					tellerMachine.setWinningHorse(horseNumber);
				}
			} else { //[<horse number>] <amount> - specifies the horse wagered on and the amount of the bet
				int horseNumber = Integer.parseInt(matcher.group("num"));
				String bet = matcher.group("bet");
				if (!isInteger(bet)) {
					displayErrorMessage(String.format("Invalid Bet: %s", bet));
					continue;
				}
				int betAmount = Integer.parseInt(matcher.group("bet"));
				if (!tellerMachine.isValidHorseNumber(horseNumber)) {
					displayErrorMessage(String.format("Invalid Horse Number: %d", horseNumber));
				} else if (tellerMachine.getHorse(horseNumber).getWinStatus() != Horse.WinStatus.WON) {
					displayErrorMessage(String.format("No Payout: %s", tellerMachine.getHorse(horseNumber).getName()));
				} else {
					Payout payout = tellerMachine.makePayout(horseNumber, betAmount);
					if (!payout.hasEnoughMoney()) {
						displayErrorMessage(String.format("Insufficient Funds: %d", payout.getAmount()));
					} else {
						displayPayout(tellerMachine.getHorse(horseNumber).getName(), payout.getAmount(), payout.getDispensing());
					}
				}
			}
			displayTellerMachineState();
		}
	}

	private boolean isInteger(String input) {
		try {
			Integer.parseInt(input);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static void main(String[] args) {
		Cli cli = new Cli(System.in, System.out);
		cli.startEventLoop();
	}
}
