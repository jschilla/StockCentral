/**
 *
 */
package stockcentral;

/**
 * This is going to create 18 different strategies -- a 3x3x2 matrix where the third dimension is
 * whether it is with or without the 200-day SMA.  I also need to create one of these for bearish
 * indications and one for the four-day RSI.
 *
 * @author Jack's Dell
 *
 */
public class MultipleOverboughtRSIStrategies extends BacktestStrategies {

	private static final int[] RSI_MAXES = { 98, 95, 90 };

	private static final int[] NUM_DAYS = { 1, 2, 3 };

	/* (non-Javadoc)
	 * @see stockcentral.BacktestStrategies#getNumberOfStrategies()
	 */
	@Override
	public int getNumberOfStrategies() {
		// TODO Auto-generated method stub
		return RSI_MAXES.length * NUM_DAYS.length * 2;
	}

	/**	This method translates a strategyId into a two-dimensional array.  Since we're dealing with two dimensions,
	*	we have to be able to do this.
	*/
	private int[] calculateArrayIndicesFromId (int strategyId) {

		int[] toReturn = new int[2];

		toReturn[0] = strategyId / RSI_MAXES.length;

		toReturn[1] = strategyId % RSI_MAXES.length;

		return toReturn;

	}	// calculateArrayIndicesFromId

	public String getStrategyName (int strategyId) {

			// now we need to figure out which cumulative maximum and accumulation periods we are going
			// to be using
		int rsiMax, numberOfDays;

		int[] indices;

			// since the first of these strategies are going to be those WITH the 200-day SMA check,
		if (strategyId < (RSI_MAXES.length * NUM_DAYS.length))
			indices	= calculateArrayIndicesFromId(strategyId);
		else
			indices = calculateArrayIndicesFromId((strategyId - (RSI_MAXES.length * NUM_DAYS.length)));

		rsiMax = RSI_MAXES[indices[0]];
		numberOfDays = NUM_DAYS[indices[1]];

		String toReturn = "RSI above " + rsiMax + " for " + numberOfDays + " days";

		if (strategyId < (RSI_MAXES.length * NUM_DAYS.length))
			toReturn += " (over 200-Day SMA)";

		return toReturn;

}	// getStrategyName

	/* (non-Javadoc)
	 * @see stockcentral.BacktestStrategies#isStrategyBullish(int)
	 */
	@Override
	public boolean isStrategyBullish(int strategyId) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see stockcentral.BacktestStrategies#testExitAtDay(stockcentral.StockData, int, int)
	 */
	@Override
	public boolean testExitAtDay (StockData sd, int strategyId, int lookback) {

		boolean toReturn = false;

		float[] twoDayRSI = sd.getTwoDayRSI();

		if (twoDayRSI[lookback] < 25)
			toReturn = true;

		return toReturn;

	}	// testExitAtDay

	/* (non-Javadoc)
	 * @see stockcentral.BacktestStrategies#testStrategyAtDay(stockcentral.StockData, int, int)
	 */
	@Override
	public boolean testStrategyAtDay(StockData sd, int strategyId, int lookBack) {

		boolean toReturn = false;

		try {

			float[] closes = sd.getCloses();
			float[] twoDayRSI = sd.getTwoDayRSI();
			float[] ma200 = sd.get200DaySMA();

				// now we need to figure out which cumulative maximum and accumulation periods we are going
				// to be using
			int rsiMax, numberOfDays;
			boolean using200DaySMA;
			int[] indices;

			if (strategyId < (RSI_MAXES.length * NUM_DAYS.length)) {
				using200DaySMA = true;

				indices = calculateArrayIndicesFromId(strategyId);
			}	// if
			else {
				using200DaySMA = false;

				indices = calculateArrayIndicesFromId((strategyId - (RSI_MAXES.length * NUM_DAYS.length)));
			}	// else

			rsiMax = RSI_MAXES[indices[0]];
			numberOfDays = NUM_DAYS[indices[1]];

				// If we are using the 200-day SMA to test for bullish/bearish as a pre-req (i.e., this
				// is in the first half of the strategies), then we need to check for the 200-day SMA.
				// So, to move forward with the test, we either have to be ignoring the 200-day SMA or
				// we have to be above the 200-day SMA.
			if ((!using200DaySMA) || (closes[lookBack] < ma200[lookBack])) {

					// we assume that this is true until it's disproven (which it usually will be).
				toReturn = true;

					// Now that we've gotten past the SMA gatekeeper, we need to check to see if the 2-day
					// RSI has been below whatever limit we're working with for the numberOfDays variable.
				for (int count = 0; ((toReturn) && (count < numberOfDays)); count++) {

					if (twoDayRSI[lookBack + count] < rsiMax)
						toReturn = false;

				}	// for count

			}

		}	// try
		catch (ArrayIndexOutOfBoundsException e) {

		}	// catch
		// TODO Auto-generated method stub
		return toReturn;
	}

	/* (non-Javadoc)
	 * @see stockcentral.BacktestStrategies#testStrategyAtDayBefore(stockcentral.StockData, int, int)
	 */
	@Override
	public boolean testStrategyAtDayBefore(StockData sd, int strategyId,
			int lookBack) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see stockcentral.BacktestStrategies#waitsForExit(int)
	 */
	@Override
	public boolean waitsForExit(int strategyId) {
		// TODO Auto-generated method stub
		return true;
	}

}
