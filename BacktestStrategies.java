/**
 *
 */
package stockcentral;

/**
 * @author Jack's Dell
 *
 */
public abstract class BacktestStrategies {

	public abstract int getNumberOfStrategies();

	public abstract boolean isStrategyBullish(int strategyId);

	public abstract boolean testStrategyAtDay (StockData sd, int strategyId, int lookBack);

	public abstract boolean testExitAtDay (StockData sd, int strategyId, int lookBack);

		// This method doesn't check for a completed signal, but instead checks for the day before
		// a completed signal.  This is being added so that we can detect possible setups before
		// they come through and then scan them near the close the following day so that we
		// can enter MOC orders.
	public abstract boolean testStrategyAtDayBefore (StockData sd, int strategyId, int lookBack);

		// This method returns true if this strategy is not the sort that "doubles down" on
		// repeat signals.  In other words, it returns true if a trader would not enter
		// into a second position on a subsequent signal until there is an exit signal.
	public abstract boolean waitsForExit(int strategyId);

	public String getStrategyName (int strategyId) {

		return "Default Name";

	}	// getStrategyName

	public String getStrategyFileNameComponent (int strategyId) {

		return "default";

	}	// getStrategyFileNameComponent

	public float getTrailingStop(int strategyId) {

		return (float)0.005;

	}

}	// abstract class BacktestStrategies
