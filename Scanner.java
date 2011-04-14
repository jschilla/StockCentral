/**
 *
 */
package stockcentral;

import java.util.ArrayList;

/**
 * @author Jack's Dell
 *
 *	This class scans the current day's data to determine if any of the current BacktestStrategies
 *	have generated a signal.  If one has, it then conducts a backtest for that strategy on that stock,
 *	and produces a report indicating each signal indicating the stock, the strategy, and the past
 *	success of that strategy on that stock.
 *
 */
public class Scanner {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

			// First, we load up the appropriate StockDataArray object, which we then
			// will use to load up individual StockData objects.
		String fileName;

		if ((args != null) && (args.length > 0))
			fileName = args[0];
		else
			fileName = StockDataLoader.DEFAULT_DATA;

		StockDataArray dataArray = (StockDataArray)StockCentral.deserializeObject(fileName, null);

		StockData indexData = dataArray.getIndexData();

			// Next, we load the back test strategies and create a ScannerLog.
		BacktestStrategies[] activeStrategies = ActiveBacktestStrategies.getStrategies();

		ScannerLog log = new ScannerLog();

			// Now, go through each stock and run each strategy.  If we have a signal from any
			// strategy, then we run a backtest on it for that stock and then we report all of that
			// info into a ScannerLogEntry and then add that to the ScannerLog.

		dataArray.restartArray();

		StockCentral central = new StockCentral();

			// This loop iterates through each stock.
		for (int countStockData = 0; countStockData < dataArray.size(); countStockData++) {

			StockData data = dataArray.getNextData();

			data.calculateBellsAndWhistles();

			data.spitOutData();

				// This loop counts each strategy object
			for (int countStrategyObjects = 0; countStrategyObjects < activeStrategies.length;
				countStrategyObjects++) {

					// This loop iterates through each strategy within a given strategy object.
				for (int countStrategyIds = 0;
					countStrategyIds < activeStrategies[countStrategyObjects].getNumberOfStrategies();
					countStrategyIds++) {

					boolean gotASignal = activeStrategies[countStrategyObjects].testStrategyAtDay(data,
							countStrategyIds, 0);

						// if we've got a signal, run a backtest on this stock for this strategy
						// and then spit out the results into a log entry.

						// NOTE:  We're not ready to do this just yet.  For now, just print out a bit
						// of data.
					if (gotASignal) {

						System.out.println("Ticker: " + data.getTicker() + "; Strategy: " +
								activeStrategies[countStrategyObjects].getStrategyName(countStrategyIds));

					}

				}

			}

		}

	}	// main()

}	// class Scanner

class ScannerLog extends ArrayList<ScannerLogEntry> {

}	// class ScannerLog

class ScannerLogEntry {

	private static final int NUMBER_OF_BACKTESTS = 2;

	private static final int MOC_TO_EXIT_SIGNAL = 0;
	private static final int MOO_TO_EXIT_SIGNAL = 1;

	String m_ticker, m_strategyMatch;

	float[] m_strategyAccuracy = new float[NUMBER_OF_BACKTESTS];

}	// class ScannerLogEntry