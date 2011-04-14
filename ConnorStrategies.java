/**
*
*/
package stockcentral;

public class ConnorStrategies extends BacktestStrategies {

	private static final String[] STRATEGY_NAMES = { "10-Day Lows (Bullish)",
		"10-Day Highs (Bearish)" };

	private static final String[] STRATEGY_FILE_NAMES = { "tendaylows","tendayhighs" };


	private static final boolean[] STRATEGY_BULLISH = { true, false };

	public String getStrategyName (int strategyId) { return STRATEGY_NAMES[strategyId]; }

	public String getStrategyFileNameComponent (int strategyId) { return STRATEGY_FILE_NAMES[strategyId]; }

	public int getNumberOfStrategies() { return STRATEGY_NAMES.length; }

	public boolean isStrategyBullish (int strategyId) { return STRATEGY_BULLISH[strategyId]; }

	public boolean waitsForExit(int strategyId) { return true; }

	public boolean testStrategyAtDay (StockData sd, int strategyId, int lookback) {

		boolean toReturn = false;

		float[] closes = sd.getCloses();

			// the first strategy looks to see if today's close is lower than the close for the
			// last ten days.
			// ADD VARIANT WITH 200-DAY SMA!!!
		if (strategyId == 0) {

			toReturn = true;

			for (int countBack = 1; countBack <= 10; countBack++)
				toReturn = toReturn && (closes[lookback] < closes[lookback + countBack]);

		}	// if

			// the second strategy looks to see if today's close is higher than the close for the
			// last ten days.
			// ADD VARIANT WITH 200-DAY SMA!!!
		else if (strategyId == 1) {

			toReturn = true;

			for (int countBack = 1; countBack <= 10; countBack++)
				toReturn = toReturn && (closes[lookback] > closes[lookback + countBack]);

		}

		return toReturn;

	}	// testStrategyAtDay

	public boolean testStrategyAtDayBefore (StockData sd, int strategyId, int lookback) {

		boolean toReturn = false;

		float[] closes = sd.getCloses();

			// the first strategy looks to see if today's close is lower than the close for the
			// last ten days.
			// ADD VARIANT WITH 200-DAY SMA!!!
		if (strategyId == 0) {

			toReturn = true;

			for (int countBack = 1; countBack <= 9; countBack++)
				toReturn = toReturn && (closes[lookback] < closes[lookback + countBack]);

		}	// if

			// the second strategy looks to see if today's close is higher than the close for the
			// last ten days.
			// ADD VARIANT WITH 200-DAY SMA!!!
		else if (strategyId == 1) {

			toReturn = true;

			for (int countBack = 1; countBack <= 9; countBack++)
				toReturn = toReturn && (closes[lookback] > closes[lookback + countBack]);

		}

		return toReturn;

	}	// testStrategyAtDay

	public boolean testExitAtDay (StockData sd, int strategyId, int lookback) {

		boolean toReturn = false;

		float[] closes = sd.getCloses();
		float[] tenDayMA = sd.get5DaySMA();

		if ((strategyId == 0) && (closes[lookback] > tenDayMA[lookback]))
			toReturn = true;
		else if ((strategyId == 1) && (closes[lookback] < tenDayMA[lookback]))
			toReturn = true;

		return toReturn;

	}	// testExitAtDay








}	// class ConnorStrategies