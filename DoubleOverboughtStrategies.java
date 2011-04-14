/**
 *
 */
package stockcentral;

/**
 * @author Jack's Dell
 *
 */
public class DoubleOverboughtStrategies extends BacktestStrategies {

	public String getStrategyName (int strategyId) { return "Double High " + (strategyId + 5) + "s Strategy"; }

	public String getStrategyFileNameComponent (int strategyId) { return "doublehigh" + (strategyId + 5) + ".csv"; }

	public int getNumberOfStrategies() { return 6; }

	public boolean isStrategyBullish (int strategyId) { return false; }

	public boolean waitsForExit(int strategyId) { return true; }

	public boolean testStrategyAtDay (StockData sd, int strategyId, int lookback) {

		boolean toReturn = false;

		try {

			float[] closes = sd.getCloses();
			float[] twoHundredDaySMA = sd.get200DaySMA();

			int lowPeriodCount = strategyId + 5;

				// the first step is to make sure that the close is below the 200-day MA.  If not, we don't move on.
			if (closes[lookback] < twoHundredDaySMA[lookback])
				toReturn = true;

				// Next, we look through each day to see if this day's close is the low.
			for (int count = 1; (count < lowPeriodCount) && (toReturn); count++) {

					// We assume that this is an entry signal, but if any of the last X days'
					// closes have been higher than today's, we switch it to false and return that.
				if (closes[lookback] < closes[lookback + count])
					toReturn = false;

			}

		}	// try
		catch (ArrayIndexOutOfBoundsException e) {

		}	// catch

		return toReturn;

	}	// testStrategyAtDay

	public boolean testStrategyAtDayBefore (StockData sd, int strategyId, int lookback) {

		boolean toReturn = false;

		float[] closes = sd.getCloses();
		float[] twoHundredDaySMA = sd.get200DaySMA();

		int lowPeriodCount = strategyId + 4;

			// the first step is to make sure that the close is above the 200-day MA.  If not, we don't move on.
		if (closes[lookback] < twoHundredDaySMA[lookback])
			toReturn = true;

			// Next, we look through each day to see if this is the low.
		for (int count = 1; (count < lowPeriodCount) && (toReturn); count++) {

				// We assume that this is an entry signal, but if any of the last X days'
				// closes have been higher than today's, we switch it to false and return that.
			if (closes[lookback] < closes[lookback + count])
				toReturn = false;

		}

		return toReturn;

	}	// testStrategyAtDay

	public boolean testExitAtDay (StockData sd, int strategyId, int lookback) {

		boolean toReturn = true;

		try {

			float[] closes = sd.getCloses();

			int highPeriodCount = strategyId + 5;

				// We exit this position if we are now at an X-day high.
			for (int count = 0; (count < highPeriodCount) && (toReturn); count++) {

					// we default to assume that this is an exit signal.  If any of the last X days'
					// closes have been higher than today's, however, then this is NOT an exit signal.
				if (closes[lookback] > closes[lookback + highPeriodCount])
					toReturn = false;

			}

		}	// try
		catch (ArrayIndexOutOfBoundsException e) {

		}	// catch

		return toReturn;

	}	// testExitAtDay

}	// class DoubleStrategies