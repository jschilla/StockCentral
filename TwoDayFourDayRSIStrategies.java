/**
 *
 */
package stockcentral;

/**
 * @author Jack's Dell
 *
 */
public class TwoDayFourDayRSIStrategies extends BacktestStrategies {

		// We're creating a four-dimensional array in two dimensions, just to make this slightly less tedious.	The first
		// number of each block of four is the 2-day RSI entry signal, the second is the 4-day entry signal, the third is
		// is 2-day exit signal, and fourth is the 4-day exit signal.
	private static final int[] TWO_DAY_ENTRY = { 70, 95, 98, 99 };
	private static final int[] FOUR_DAY_ENTRY = { 70, 90, 95 };
	private static final int[] TWO_DAY_EXIT = { 60 };
	private static final int[] FOUR_DAY_EXIT = { 60 };

	private int[][] bullishArray, bearishArray;

	private void calculateArrays() {

		bullishArray = new int[TWO_DAY_ENTRY.length * FOUR_DAY_ENTRY.length * TWO_DAY_EXIT.length * FOUR_DAY_EXIT.length][4];
		bearishArray = new int[TWO_DAY_ENTRY.length * FOUR_DAY_ENTRY.length * TWO_DAY_EXIT.length * FOUR_DAY_EXIT.length][4];

		int arrayIndex = 0;

		for (int twoDayEntry = 0; twoDayEntry < TWO_DAY_ENTRY.length; twoDayEntry++)
			for (int fourDayEntry = 0; fourDayEntry < FOUR_DAY_ENTRY.length; fourDayEntry++)
				for (int twoDayExit = 0; twoDayExit < TWO_DAY_EXIT.length; twoDayExit++)
					for (int fourDayExit = 0; fourDayExit < FOUR_DAY_EXIT.length; fourDayExit++) {

						//bearishArray[arrayIndex] = new int[4];

						bearishArray[arrayIndex][0] = TWO_DAY_ENTRY[twoDayEntry];
						bearishArray[arrayIndex][1] = FOUR_DAY_ENTRY[fourDayEntry];
						bearishArray[arrayIndex][2] = TWO_DAY_EXIT[twoDayExit];
						bearishArray[arrayIndex][3] = FOUR_DAY_EXIT[fourDayExit];

						arrayIndex++;

					}

		arrayIndex = 0;

		for (int twoDayEntry = 0; twoDayEntry < TWO_DAY_ENTRY.length; twoDayEntry++)
			for (int fourDayEntry = 0; fourDayEntry < FOUR_DAY_ENTRY.length; fourDayEntry++)
				for (int twoDayExit = 0; twoDayExit < TWO_DAY_EXIT.length; twoDayExit++)
					for (int fourDayExit = 0; fourDayExit < FOUR_DAY_EXIT.length; fourDayExit++) {

						bullishArray[arrayIndex] = new int[4];

						bullishArray[arrayIndex][0] = 100 - TWO_DAY_ENTRY[twoDayEntry];
						bullishArray[arrayIndex][1] = 100 - FOUR_DAY_ENTRY[fourDayEntry];
						bullishArray[arrayIndex][2] = 100 - TWO_DAY_EXIT[twoDayExit];
						bullishArray[arrayIndex][3] = 100 - FOUR_DAY_EXIT[fourDayExit];

						arrayIndex++;

					}

	}	// calculateBullishArray

	public TwoDayFourDayRSIStrategies() {

		calculateArrays();

	}	// ctor

	private static final String STRATEGY_NAME_ROOT = "Combo 2- and 4-Day RSI -- ";

	private static final String[] STRATEGY_FILE_PARTS = { "fourtwo-oversold", "fourtwo-overbought" };

	/* (non-Javadoc)
	 * @see stockcentral.BacktestStrategies#getNumberOfStrategies()
	 */
	@Override
	public int getNumberOfStrategies() {

		return TWO_DAY_ENTRY.length * FOUR_DAY_ENTRY.length * TWO_DAY_EXIT.length * FOUR_DAY_EXIT.length * 2;

	}	// getNumberOfStrategies()

	/* (non-Javadoc)
	 * @see stockcentral.BacktestStrategies#isStrategyBullish(int)
	 */
	@Override
	public boolean isStrategyBullish(int strategyId) {

		boolean toReturn = false;

		if (strategyId >= TWO_DAY_ENTRY.length * FOUR_DAY_ENTRY.length * TWO_DAY_EXIT.length * FOUR_DAY_EXIT.length)
			toReturn = true;

		return toReturn;
	}	// isStrategyBullish()

	public String getStrategyName (int strategyId) {

		String toReturn = STRATEGY_NAME_ROOT;

		int numCombosEachSide = TWO_DAY_ENTRY.length * FOUR_DAY_ENTRY.length * TWO_DAY_EXIT.length * FOUR_DAY_EXIT.length;

		if (strategyId < numCombosEachSide)
			toReturn += "Bear (" + bearishArray[strategyId][0] + " - " + bearishArray[strategyId][1] + " - " +
				bearishArray[strategyId][2] + " - " + bearishArray[strategyId][3] + ")";
		else
			toReturn += "Bull (" + bullishArray[strategyId - numCombosEachSide][0] + " - " +
				bullishArray[strategyId - numCombosEachSide][1] + " - " +
				bullishArray[strategyId - numCombosEachSide][2] + " - " +
				bearishArray[strategyId - numCombosEachSide][3] + ")";

		return toReturn;

	}	// getStrategyName

	public String getStrategyFileNameComponent (int strategyId) {

		return STRATEGY_FILE_PARTS[strategyId];

	}	// getStrategyFileNameComponent

	/**
	 * This is where the magic happens.  This method tests the ETF strategies on the given day.
	 */
	public boolean testStrategyAtDay (StockData sd, int strategyId, int lookBack) {

		boolean toReturn = false;

		float[] fourDayRSI = sd.getFourDayRSI();
		float[] twoDayRSI = sd.getTwoDayRSI();
		float[] m_200DaySMA = sd.get200DaySMA();
		float[] m_closes = sd.getCloses();


		int numCombosEachSide = TWO_DAY_ENTRY.length * FOUR_DAY_ENTRY.length * TWO_DAY_EXIT.length * FOUR_DAY_EXIT.length;

		int twoDayEntry, fourDayEntry;

		if (strategyId < numCombosEachSide) {

			if ((twoDayRSI[lookBack] > bearishArray[strategyId][0]) &&
					(fourDayRSI[lookBack] > bearishArray[strategyId][1]) &&
					(m_closes[lookBack] < m_200DaySMA[lookBack]))
				toReturn = true;

		}	// if
		else {

			if ((twoDayRSI[lookBack] < bullishArray[strategyId - numCombosEachSide][0]) &&
				(fourDayRSI[lookBack] < bullishArray[strategyId - numCombosEachSide][1]) &&
				(m_closes[lookBack] > m_200DaySMA[lookBack]))
				toReturn = true;

		}	// else

		return toReturn;

	}	// testStrategyAtday

	/* (non-Javadoc)
	 * @see stockcentral.BacktestStrategies#waitsForExit(int)
	 */
	@Override
	public boolean waitsForExit(int strategyId) {

		return true;

	}	// waitsForExit

	public boolean testExitAtDay (StockData sd, int strategyId, int lookBack) {

		boolean toReturn = false;

		float[] fourDayRSI = sd.getFourDayRSI();
		float[] twoDayRSI = sd.getTwoDayRSI();

		int numCombosEachSide = TWO_DAY_ENTRY.length * FOUR_DAY_ENTRY.length * TWO_DAY_EXIT.length * FOUR_DAY_EXIT.length;

		int twoDayExit, fourDayExit;

		if (strategyId < numCombosEachSide) {

			if ((twoDayRSI[lookBack] < bearishArray[strategyId][2]) || (fourDayRSI[lookBack] < bearishArray[strategyId][3]))
				toReturn = true;

		}	// if
		else {

			if ((twoDayRSI[lookBack] < bullishArray[strategyId - numCombosEachSide][2]) &&
				(fourDayRSI[lookBack] < bullishArray[strategyId - numCombosEachSide][3]))
				toReturn = true;

		}	// else

		return toReturn;

	}	// testExitAtDay

	// This needs to get fixed up, obviously.
	public boolean testStrategyAtDayBefore (StockData sd, int strategyId, int lookBack) {

		return false;

	}	// testStrategyAtDayBefore


}
