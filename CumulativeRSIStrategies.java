/**
 *
 */
package stockcentral;

/**
 * @author Jack's Dell
 *
 */
public class CumulativeRSIStrategies extends BacktestStrategies {


	private static final int[] RSI_CUMULATIVE_MAXIMA = { 30, 35, 40, 45, 50, 55, 60 };

	private static final int[] RSI_ACCUMULATION_PERIODS = { 2, 3, 4, 5 };

	public String getStrategyName (int strategyId) {

			// now we need to figure out which cumulative maximum and accumulation periods we are going
			// to be using
		int cumulativeMaximum, accumulationPeriods;

		int[] indices = calculateArrayIndicesFromId(strategyId);

		cumulativeMaximum = RSI_CUMULATIVE_MAXIMA[indices[0]];
		accumulationPeriods = RSI_ACCUMULATION_PERIODS[indices[1]];

		return "Cum. RSI for " + accumulationPeriods + " days under " + cumulativeMaximum + ".";

	}	// getStrategyName

	public String getStrategyFileNameComponent (int strategyId) {

			// now we need to figure out which cumulative maximum and accumulation periods we are going
			// to be using
		int cumulativeMaximum, accumulationPeriods;

		int[] indices = calculateArrayIndicesFromId(strategyId);

		cumulativeMaximum = RSI_CUMULATIVE_MAXIMA[indices[0]];
		accumulationPeriods = RSI_ACCUMULATION_PERIODS[indices[1]];

		return "CumRSIPeriods" + accumulationPeriods + "Max" + cumulativeMaximum + ".csv";

	}

	public int getNumberOfStrategies() { return RSI_CUMULATIVE_MAXIMA.length *
		RSI_ACCUMULATION_PERIODS.length; }

		// These are all bullish, at least for now.
	public boolean isStrategyBullish (int strategyId) { return true; }

	public boolean waitsForExit(int strategyId) { return true; }

	public boolean testStrategyAtDay (StockData sd, int strategyId, int lookback) {

		boolean toReturn = false;

		try {

			float[] closes = sd.getCloses();
			float[] twoDayRSI = sd.getTwoDayRSI();
			float[] ma200 = sd.get200DaySMA();

				// now we need to figure out which cumulative maximum and accumulation periods we are going
				// to be using
			int cumulativeMaximum, accumulationPeriods;

			int[] indices = calculateArrayIndicesFromId(strategyId);

			cumulativeMaximum = RSI_CUMULATIVE_MAXIMA[indices[0]];
			accumulationPeriods = RSI_ACCUMULATION_PERIODS[indices[1]];

			float cumulativeRSITotal = 0f;

			for (int count = 0; count < accumulationPeriods; count++)
				cumulativeRSITotal += twoDayRSI[lookback+count];

			if ((closes[lookback] > ma200[lookback]) && (cumulativeRSITotal < cumulativeMaximum))
				toReturn = true;

		}	// try
		catch (ArrayIndexOutOfBoundsException e) {

		}	// catch

		return toReturn;

	}	// testStrategyAtDay

	public boolean testStrategyAtDayBefore (StockData sd, int strategyId, int lookback) {

			// NOT SURE WHAT TO DO HERE

		boolean toReturn = false;

		float[] closes = sd.getCloses();

			// the first strategy looks to see if today's close is lower than the close for the
			// last ten days.
			// ADD VARIANT WITH 200-DAY SMA!!!
		if ((strategyId == 0) || (strategyId == 2)) {

			toReturn = true;

			for (int countBack = 1; countBack <= 9; countBack++)
				toReturn = toReturn && (closes[lookback] < closes[lookback + countBack]);

		}	// if

			// the second strategy looks to see if today's close is higher than the close for the
			// last ten days.
			// ADD VARIANT WITH 200-DAY SMA!!!
		else if ((strategyId == 1) || (strategyId == 3)) {

			toReturn = true;

			for (int countBack = 1; countBack <= 9; countBack++)
				toReturn = toReturn && (closes[lookback] > closes[lookback + countBack]);

		}

		return toReturn;

	}	// testStrategyAtDay

	public boolean testExitAtDay (StockData sd, int strategyId, int lookback) {

		boolean toReturn = false;

		float[] twoDayRSI = sd.getTwoDayRSI();

		if (twoDayRSI[lookback] > 75)
			toReturn = true;

		return toReturn;

	}	// testExitAtDay

	/**	This method translates a strategyId into a two-dimensional array.  Since we're dealing with two dimensions,
	*	we have to be able to do this.
	*/
	private int[] calculateArrayIndicesFromId (int strategyId) {

		int[] toReturn = new int[2];

		toReturn[0] = strategyId / RSI_ACCUMULATION_PERIODS.length;

		toReturn[1] = strategyId % RSI_ACCUMULATION_PERIODS.length;

		return toReturn;

	}	// calculateArrayIndicesFromId

}	// class CumulativeRSIStrategies
