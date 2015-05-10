package ua.cn.dkrivenko.horseracepark;

import java.util.Collections;
import java.util.Map;

/**
 *
 * @author Dmitry Krivenko <dmitrykrivenko at gmail.com>
 */
public class Payout {

	private final int amount;
	private final Map<Integer, Integer> dispensing;
	private boolean enoughMoney = true;

	/**
	 * In case if there is no enough money to make the complete payout.
	 * @param amount the complete payout
	 * @param dispensing dispensing of each bill
	 */
	public Payout(int amount, Map<Integer, Integer> dispensing) {
		this.amount = amount;
		this.dispensing = dispensing;
	}

	/**
	 * In case if there is no enough money to make the complete payout.
	 *
	 * @param amount the complete payout that can't be made
	 */
	public Payout(int amount) {
		this(amount, Collections.emptyMap());
		enoughMoney = false;
	}

	public int getAmount() {
		return amount;
	}

	public Map<Integer, Integer> getDispensing() {
		return dispensing;
	}

	public boolean hasEnoughMoney() {
		return enoughMoney;
	}

}
