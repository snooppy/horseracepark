package ua.cn.dkrivenko.horseracepark;

import static ua.cn.dkrivenko.horseracepark.Horse.WinStatus.LOST;
import static ua.cn.dkrivenko.horseracepark.Horse.WinStatus.WON;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author Dmitry Krivenko <dmitrykrivenko at gmail.com>
 */
public final class TellerMachine {

	//denomination, quantity in inventory
	private final Map<Integer, Integer> inventory = new TreeMap<>((o1, o2) -> o2.compareTo(o1));
	//horse number, horse
	private final Map<Integer, Horse> horses = new HashMap<>();
	private int winningHorseNumber;
	private int totalAmount;

	public TellerMachine() {
		initInventory();
		initHorses();
	}

	public Map<Integer, Integer> getInventory() {
		return inventory;
	}

	public Map<Integer, Horse> getHorses() {
		return horses;
	}

	public Horse getHorse(int number) {
		return horses.get(number);
	}

	public void restock() {
		initInventory();
	}

	public void setWinningHorse(int number) {
		validateHorseNumber(number);

		if (winningHorseNumber == number) {
			return;
		}
		horses.get(number).setWinStatus(WON);
		horses.get(winningHorseNumber).setWinStatus(LOST);
		winningHorseNumber = number;
	}

	public Payout makePayout(int horseNumber, int betAmount) {
		validateHorseNumber(horseNumber);

		Horse horse = horses.get(horseNumber);
		if (horse.getWinStatus() == LOST) {
			throw new RuntimeException(String.format("No Payout For Horse With Number: %d", horseNumber));
		}

		int amount = horse.getOdds() * betAmount;
		if (amount > totalAmount) {
			return new Payout(amount);
		}
		int currAmount = amount;

		Map<Integer, Integer> inventoryCopy = new TreeMap<>(inventory);

		//denomination, number of bills
		Map<Integer, Integer> dispensing = new TreeMap<>();
		for (Map.Entry<Integer, Integer> entry : inventory.entrySet()) {
			Integer denomination = entry.getKey();
			Integer quantity = entry.getValue();

			Integer numberOfBills = currAmount / denomination;
			if (numberOfBills > quantity) {
				numberOfBills = quantity;
			}
			dispensing.put(denomination, numberOfBills);
			entry.setValue(quantity - numberOfBills);
			currAmount -= denomination * numberOfBills;
		}

		if (currAmount > 0) {
			inventory.clear();
			inventory.putAll(inventoryCopy);

			return new Payout(amount);
		}

		totalAmount -= amount;
		return new Payout(amount, dispensing);
	}

	private void initInventory() {
		totalAmount = 0;
		int quantity = 10;

		inventory.put(1, quantity);
		inventory.put(5, quantity);
		inventory.put(10, quantity);
		inventory.put(20, quantity);
		inventory.put(100, quantity);

		inventory.entrySet().forEach(entry -> {
			totalAmount += entry.getKey() * entry.getValue();
		});
	}

	private void initHorses() {
		horses.put(1, new Horse(1, "That Darn Gray Cat", 5, WON));
		horses.put(2, new Horse(2, "Fort Utopia", 10, LOST));
		horses.put(3, new Horse(3, "Count Sheep", 9, LOST));
		horses.put(4, new Horse(4, "Ms Traitour", 4, LOST));
		horses.put(5, new Horse(5, "Real Princess", 3, LOST));
		horses.put(6, new Horse(6, "Pa Kettle", 5, LOST));
		horses.put(7, new Horse(7, "Gin Stinger", 6, LOST));

		winningHorseNumber = 1;
	}

	private void validateHorseNumber(int number) {
		if (!isValidHorseNumber(number)) {
			throw new IllegalArgumentException(String.format("Invalid Horse Number: %d", number));
		}
	}

	public boolean isValidHorseNumber(int number) {
		return horses.containsKey(number);
	}

}
