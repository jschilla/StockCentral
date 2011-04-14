/**
 *
 */
package stockcentral;

import java.io.PrintWriter;
import java.util.Vector;

/**
 * @author Jack's Dell
 *
 */
public class StrategyHypothesisTester {

	private static final String HYPOTHESIS_TEST_FILENAME = "HypoTest_";
	private static final String HYPOTHESIS_TEST_DIRECTORY = "hypotests";

	private static final BacktestStrategies[] STRATEGIES = { new TwoDayFourDayRSIStrategies() };

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

	int numTradingDays = indexData.getCloses().length;

	System.out.println("Testing Hypotheses for " + STRATEGIES.length + " stocks.");

		// Next, for each strategy, we run through all of the back data, and collect statistics
		// on the number of matches and the success of each match.
		// We will collect the following data:  1-day MOC entry, 1-day following day MOO entry,
		// 2-, 3-, 4-, 5-, 10-, 20-, 50-day following day MOO entry, exit pattern detection, and
		// peak detection.
	BacktestStrategies[] activeStrategies = ActiveBacktestStrategies.getStrategies();

	String hypoFilename = HYPOTHESIS_TEST_FILENAME + System.currentTimeMillis() + ".csv";

	PrintWriter hypoOut = StockCentral.createOutputFile(hypoFilename, HYPOTHESIS_TEST_DIRECTORY);

	hypoOut.println("Strategy Name,p-value,Null Hypothesis?");

		// To do this, we have to load up each StockData object and then run each and every
		// backtest strategy on it.  When we have a match, we need to track a bunch of information.
	dataArray.restartArray();

	StockCentral central = new StockCentral();

		// This outer loop cycles through each strategy object.
	for (int strategyObjCount = 0; strategyObjCount < STRATEGIES.length; strategyObjCount++) {

			// This inner loop goes through each strategy within each object.
		for (int strategyInnerCount = 0;
			strategyInnerCount < STRATEGIES[strategyObjCount].getNumberOfStrategies();
			strategyInnerCount++) {

				Vector strategyMatches = new Vector();

				StockData indexData = dataArray.getIndexData();

					// This inner loop goes through each and every stock data object.
				for (int stockDataCount = 0; stockDataCount < dataArray.size(); stockDataCount++) {

					StockData sd = dataArray.getNextData();

					sd.calculateBellsAndWhistles();

					Backtester.backtestOneStockOneStrategy(sd, indexData, STRATEGIES[strategyObjCount],
							strategyInnerCount, strategyMatches);

				}

					// Now that we have all of the matches, we need to calculate our information
					// and spit out the data.

			}


	}	// cycle through each strategy object

	for (int countStockData = 0; (countStockData < dataArray.size() /*&& countStockData < 10 */);
		countStockData++) {

		StockData sd = dataArray.getNextData();

		sd.calculateBellsAndWhistles();

		sd.spitOutData();

		System.out.println(sd.getTicker());

		//float closes[] = sd.getCloses();

		Vector matches = new Vector();

			// This pulls all the results for this stock for all strategies.
		BacktestLog[][] stockLogs = backtestForOneStock(sd, indexData, activeStrategies, matches);

			// And then we add it to the hashtable.
		//stockMatrixLogs.put(sd.getTicker(), stockLogs);

			// Spit out this line of data.
		outputMatrixDataLine(matrixOut, stockLogs, sd.getTicker());

		outputTradeReportEntries(matches, tradeReportOut);

	}	// cycle each stock

		// go through each log and add it to the total.
	/*while (allLogs.hasMoreElements()) {

	}	//
*/
	//outputTradeReport(everyMatch);

	matrixOut.close();

	tradeReportOut.close();

	central.destructor();

	}

}
