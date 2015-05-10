package ua.cn.dkrivenko.horseracepark;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import ua.cn.dkrivenko.horseracepark.Horse.WinStatus;

/**
 *
 * @author Dmitry Krivenko <dmitrykrivenko at gmail.com>
 */
public class TellerMachineTest {

	private TellerMachine tellerMachine;

	@Before
	public void setUp() {
		tellerMachine = new TellerMachine();
	}

	@Test
	public void testInitialState() {
		assertNotNull(tellerMachine.getInventory());
		assertEquals(5, tellerMachine.getInventory().size());

		assertNotNull(tellerMachine.getHorses());
		assertEquals(7, tellerMachine.getHorses().size());
	}

	@Test
	public void isValidHorseNumber_ValidIfHorseExists() {
		assertTrue(tellerMachine.isValidHorseNumber(1));
		assertFalse(tellerMachine.isValidHorseNumber(10));
	}

	@Test
	public void winningHorse_ChangeWinStatusOfTwoHorses() {
		assertEquals(WinStatus.WON, tellerMachine.getHorse(1).getWinStatus());

		tellerMachine.setWinningHorse(2);

		assertEquals(WinStatus.LOST, tellerMachine.getHorse(1).getWinStatus());
		assertEquals(WinStatus.WON, tellerMachine.getHorse(2).getWinStatus());
	}

	@Test(expected = IllegalArgumentException.class)
	public void winningHorse_InvalidHorseNumber() {
		tellerMachine.setWinningHorse(10);
	}

	@Test
	public void makePayout_DecreaseInventory() {
		Payout payout = tellerMachine.makePayout(1, 111);

		assertTrue(payout.hasEnoughMoney());
		assertEquals(555, payout.getAmount());

		assertEquals(new Integer(0), payout.getDispensing().get(1));
		assertEquals(new Integer(1), payout.getDispensing().get(5));
		assertEquals(new Integer(1), payout.getDispensing().get(10));
		assertEquals(new Integer(2), payout.getDispensing().get(20));
		assertEquals(new Integer(5), payout.getDispensing().get(100));

		assertEquals(new Integer(5), tellerMachine.getInventory().get(100));
		assertEquals(new Integer(8), tellerMachine.getInventory().get(20));
		assertEquals(new Integer(9), tellerMachine.getInventory().get(10));
		assertEquals(new Integer(9), tellerMachine.getInventory().get(5));
		assertEquals(new Integer(10), tellerMachine.getInventory().get(1));
	}
	
	@Test
	public void makePayout_NotEnoughMoney() {
		Payout payout = tellerMachine.makePayout(1, 1000);
		
		assertFalse(payout.hasEnoughMoney());
		assertEquals(5000, payout.getAmount());
		assertTrue(payout.getDispensing().isEmpty());
	}

	@Test(expected = IllegalArgumentException.class)
	public void makePayout_InvalidHorseNumber() {
		tellerMachine.makePayout(10, 111);
	}

	@Test(expected = RuntimeException.class)
	public void makePayout_NoPayoutForLostHorse() {
		tellerMachine.makePayout(7, 111);
	}

}
