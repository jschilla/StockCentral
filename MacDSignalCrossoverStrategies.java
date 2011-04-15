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
public class MacDSignalCrossoverStrategies extends BacktestStrategies {

    private static final String[] STRATEGY_NAMES = { "Bullish Signal Cross Over; Exit at Center Cross/Reverse Signal Cross",
    	"Bullish Signal Cross Over; Exit at Center Cross/Reverse Signal Cross/Reversal of MacD Trend",
    	"Bullish Signal Cross Over; Exit at Reversal of MacD Trend",
    	"Bearish Signal Cross Over; Exit at Center Cross/Reverse Signal Cross",
    	"Bearish Signal Cross Over; Exit at Center Cross/Reverse Signal Cross/Reversal of MacD Trend",
    	"Bearish Signal Cross Over; Exit at Reversal of MacD Trend" };


    /* (non-Javadoc)
	 * @see stockcentral.BacktestStrategies#getNumberOfStrategies()
	 */
	public int getNumberOfStrategies() {
		// TODO Auto-generated method stub
		return STRATEGY_NAMES.length;
	}

	public String getStrategyName (int strategyId) {

		return STRATEGY_NAMES[strategyId];

	}	// getStrategyName

	/* (non-Javadoc)
	 * @see stockcentral.BacktestStrategies#isStrategyBullish(int)
	 */
	@Override
	public boolean isStrategyBullish(int strategyId) {
		// TODO Auto-generated method stub
		boolean toReturn = true;

		if (strategyId >= 3)
		toReturn = false;

		return toReturn;
	}


	/* (non-Javadoc)
	 * This tests to see if we have an exit signal.  An exit signal will occur if the MacD either crosses over the center line
	 * or if it crosses back over the signal line (which is more of a stop loss than an actual exit signal).
	 */
	@Override
	public boolean testExitAtDay (StockData sd, int strategyId, int lookback) {

		boolean toReturn = false;

		MacD macdData = sd.getMacD494();

		double[] macd = macdData.getMacD();
		//double[] signal = macdData.getSignal();
		double[] histogram = macdData.getHistogram();

			// If this is bullish strategy, in which case we are looking for the macd to cross up and over
			// center line or to cross back down the signal line (as a stop loss).  We use the
			// histogram to detect such a signal line crossover.
		if (strategyId == 0) {

			if (((macd[lookback] > 0) && (macd[lookback + 1] < 0)) ||
			((histogram[lookback] < 0) && (histogram[lookback + 1] > 0)))
			toReturn = true;

		}
			// This bullish strategy is just a slight modification -- it
			// also exits if the MacD turns around at all.
		else if (strategyId == 1) {

			if (((macd[lookback] > 0) && (macd[lookback + 1] < 0)) ||
					((histogram[lookback] < 0) && (histogram[lookback + 1] > 0)) ||
					(macd[lookback] < macd[lookback + 1]))
						toReturn = true;

		}
		else if (strategyId == 2) {

			if (macd[lookback] < macd[lookback + 1])
				toReturn = true;

		}
			// If this is bearish strategy, in which case we are looking for the macd to cross down and
			// under the center line or to cross back up the signal line (as a stop loss).
			// We use the histogram to detect such a signal line crossover.
		else if (strategyId == 3) {

			if (((macd[lookback] < 0) && (macd[lookback + 1] > 0)) ||
					((histogram[lookback] > 0) && (histogram[lookback + 1] < 0)))
						toReturn = true;

		}
			// This bullish strategy is just a slight modification -- it
			// also exits if the MacD turns around at all.
		else if (strategyId == 4) {

			if (((macd[lookback] < 0) && (macd[lookback + 1] > 0)) ||
					((histogram[lookback] > 0) && (histogram[lookback + 1] < 0)) ||
					(macd[lookback] > macd[lookback + 1]))
						toReturn = true;
		}
		else if (strategyId == 5) {

			if (macd[lookback] > macd[lookback + 1])
				toReturn = true;

		}

		return toReturn;

	}	// testExitAtDay

	/* (non-Javadoc)
	 * This method checks to see if the strategy has been invoked on the day passed.  For the bullish strategy, it
	 * it looks to determine whether the macd just passed above the signal line (i.e., that the histogram just turned
	 * positive) and the MacD is in negative territory (below the center line).
	 * For the bearish strategy, it obviously does the opposite.
	 */
	@Override
	public boolean testStrategyAtDay(StockData sd, int strategyId, int lookBack) {

		boolean toReturn = false;

		MacD macdData = sd.getMacD494();

		double[] macd = macdData.getMacD();
		double[] histogram = macdData.getHistogram();

		if (strategyId <= 1) {

			if ((histogram[lookBack] > 0) && (histogram[lookBack + 1] < 0) && (macd[lookBack] < 0))
				toReturn = true;

		}
		else if (strategyId >= 2) {

			if ((histogram[lookBack] < 0) && (histogram[lookBack + 1] > 0) && (macd[lookBack] > 0))
				toReturn = true;

		}


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
