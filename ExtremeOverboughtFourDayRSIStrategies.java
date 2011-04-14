/**
 *
 */
package stockcentral;

/**
 * @author Jack's Dell
 *
 */
public class ExtremeOverboughtFourDayRSIStrategies extends BacktestStrategies {

	private static final String[] STRATEGY_NAMES = { "4-Day RSI Below 10",
	"4-Day RSI Above 90" };

	private static final String[] STRATEGY_FILE_PARTS = { "fourrsibelow10", "fourrsiabove90" };

	/* (non-Javadoc)
	 * @see stockcentral.BacktestStrategies#getNumberOfStrategies()
	 */
	@Override
	public int getNumberOfStrategies() {
		// TODO Auto-generated method stub
		return STRATEGY_NAMES.length;
	}	// getNumberOfStrategies()

	/* (non-Javadoc)
	 * @see stockcentral.BacktestStrategies#isStrategyBullish(int)
	 */
	@Override
	public boolean isStrategyBullish(int strategyId) {
		// TODO Auto-generated method stub
		boolean toReturn = false;

		if (strategyId == 0)
			toReturn = true;

		if (strategyId == 1)
			toReturn = false;

		return toReturn;
	}	// isStrategyBullish()

	public String getStrategyName (int strategyId) {

		return STRATEGY_NAMES[strategyId];

	}	// getStrategyName

	public String getStrategyFileNameComponent (int strategyId) {

		return STRATEGY_FILE_PARTS[strategyId];

	}	// getStrategyFileNameComponent

	/**
	 * This is where the magic happens.  This method tests the ETF strategies on the given day.
	 */
	public boolean testStrategyAtDay (StockData sd, int strategyId, int lookBack) {

		boolean toReturn = false;

		float[] closes = sd.getCloses();
		float[] fourDayRSI = sd.getFourDayRSI();
		float[] sma200Day = sd.get200DaySMA();

		if (strategyId == 0) {

			if ((fourDayRSI[lookBack] < 10) && (closes[lookBack] < sma200Day[lookBack]))
					toReturn = true;

		}	// if it's the first strategy ID.
		else if (strategyId == 1) {

			if ((fourDayRSI[lookBack] > 90) && (closes[lookBack] > sma200Day[lookBack]))
				toReturn = true;

		}

		return toReturn;

	}	// testStrategyAtday

	/* (non-Javadoc)
	 * @see stockcentral.BacktestStrategies#waitsForExit(int)
	 */
	@Override
	public boolean waitsForExit(int strategyId) {

		return true;

	}	// waitsForExit

	public boolean testExitAtDay (StockData sd, int strategyId, int lookback) {

		boolean toReturn = false;

		float[] fourDayRSI = sd.getFourDayRSI();

			// The first strategy should be exited when the four-day RSI rises above 55
		if (strategyId == 0) {

			if (fourDayRSI[lookback] >= 50)
				toReturn = true;

		}	// if
		else if (strategyId == 1) {

			if (fourDayRSI[lookback] <= 50)
				toReturn = true;

		}	// else if

		return toReturn;

	}	// testExitAtDay

	// This needs to get fixed up, obviously.
	public boolean testStrategyAtDayBefore (StockData sd, int strategyId, int lookBack) {

		return false;

	}	// testStrategyAtDayBefore


}
