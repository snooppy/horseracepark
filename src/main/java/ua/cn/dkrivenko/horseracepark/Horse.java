package ua.cn.dkrivenko.horseracepark;

/**
 *
 * @author Dmitry Krivenko <dmitrykrivenko at gmail.com>
 */
public class Horse {

	private final int number;
	private final String name;
	private final int odds;
	private WinStatus winStatus = WinStatus.LOST; //default

	public Horse(int number, String name, int odds, WinStatus winStatus) {
		this.number = number;
		this.name = name;
		this.odds = odds;
		this.winStatus = winStatus;
	}

	public int getNumber() {
		return number;
	}

	public String getName() {
		return name;
	}

	public int getOdds() {
		return odds;
	}

	public WinStatus getWinStatus() {
		return winStatus;
	}

	public void setWinStatus(WinStatus winStatus) {
		this.winStatus = winStatus;
	}

	public static enum WinStatus {

		WON("won"),
		LOST("lost");

		private final String value;

		private WinStatus(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return value;
		}
	}

}
