/**
 *
 */
package stockcentral;

import java.util.*;
import java.io.*;

/**
 * @author Jack Schillaci
 * @version Build 2/16/2010
 */
public class Backtester {

	private final static String STOCK_STRATEGY_MATRIX_FILENAME = "StockMatrixResults_";

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

		System.out.println("Conducting Backtest for " + dataArray.size() + " stocks.");

			// Next, for each strategy, we run through all of the back data, and collect statistics
			// on the number of matches and the success of each match.
			// We will collect the following data:  1-day MOC entry, 1-day following day MOO entry,
			// 2-, 3-, 4-, 5-, 10-, 20-, 50-day following day MOO entry, exit pattern detection, and
			// peak detection.
		BacktestStrategies[] activeStrategies = ActiveBacktestStrategies.getStrategies();

			// we have to create a two-dimensional array of BacktestLog objects.  We need one
			// BacktestLog object for each strategy that we're testing.  This won't be a "square"
			// 2-D array so we don't create each number of columns until we get to the individual
			// BacktestStrategies object.
		// BacktestLog[][] logs = new BacktestLog[activeStrategies.length][];

			// we need a corresponding 2D array representing whether there is presently an
			// open position in a given stock and strategy.  This will be true if there is
			// a signal and will be turned back false when there is an exit signal.
			// This is necessary because otherwise, if there are multiple, repeated
			// signals, the backtest engine returns data for each of them rather than just
			// the first.
		//boolean[][] positionsOpen = new boolean[activeStrategies.length][];

		//Hashtable stockMatrixLogs = new Hashtable();

		//Vector everyMatch = new Vector();

		String matrixFilename = STOCK_STRATEGY_MATRIX_FILENAME + System.currentTimeMillis() + ".csv";

		PrintWriter matrixOut = StockCentral.createOutputFile(matrixFilename,
				BacktestLog.REPORTS_DIRECTORY);

		outputMatrixHeaderLine(matrixOut, activeStrategies);

		PrintWriter tradeReportOut = createTradeWriter();

		outputTradeReportHeaderLine(tradeReportOut);

			// To do this, we have to load up each StockData object and then run each and every
			// backtest strategy on it.  When we have a match, we need to track a bunch of information.
		dataArray.restartArray();

		StockCentral central = new StockCentral();

			// Finally, we going to compile data for ALL stocks scanned and output that on the final line
			// of the matrix output.
		BacktestLog[][] sumLog = createStockLogArray(activeStrategies, true);

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

	}	// main()

	private static final BacktestLog[][] backtestForOneStock(StockData sd, StockData indexData,
			BacktestStrategies[] activeStrategies, Vector everyMatch) {

		BacktestLog[][] logs = createStockLogArray(activeStrategies, false);

			// we have to look at each BacktestStrategies object to find out how many strategies
			// each has so we can shape the 2-D array.
		for (int countStratObjs = 0; countStratObjs < activeStrategies.length; countStratObjs++) {

				// next, we need to initialize logs for each part of the array.
			for (int countStrats = 0;
				countStrats < activeStrategies[countStratObjs].getNumberOfStrategies(); countStrats++) {

				logs[countStratObjs][countStrats] =
					new BacktestLog(activeStrategies[countStratObjs], countStrats);

			}	// cycle through each and every strategy.

		}	// cycle through BacktestStrategy objects.

		float[] closes = sd.getCloses();

				// We have to pick through each BacktestStrategies object.
			for (int countStrategyObjects = 0; countStrategyObjects < activeStrategies.length;
				countStrategyObjects++) {

					// finally, let's look through each individual strategy.
				for (int countStrategyIds = 0;
					countStrategyIds < activeStrategies[countStrategyObjects].getNumberOfStrategies();
					countStrategyIds++) {

					logs[countStrategyObjects][countStrategyIds] =
						backtestOneStockOneStrategy(sd, indexData,
						activeStrategies[countStrategyObjects], countStrategyIds, everyMatch);

				}	// cycle through individual strategies.

			}	// cycle through BacktestStrategies objects.

		return logs;

	}	// backtestForOneStock

	public static final BacktestLog backtestOneStockOneStrategy(StockData sd, StockData indexData,
			BacktestStrategies strategies, int strategyId, Vector everyMatch) {

		BacktestLog log = new BacktestLog(strategies, strategyId);

		float[] closes = sd.getCloses();

			// we need to set the backtest log so that it knows how many days there are.  this will become
			// necessary when we do averaging later.
		log.setTotalTradingDays(closes.length);

			// we need a boolean representing whether there is presently an
			// open position in a given stock and strategy.  This will be true if there is
			// a signal and will be turned back false when there is an exit signal.
			// This is necessary because otherwise, if there are multiple, repeated
			// signals, the backtest engine returns data for each of them rather than just
			// the first.
		boolean positionOpen = false;

		// This looks through each day's data and checks each of the backtest strategies.
		for (int dayCount = closes.length - 1; dayCount >= 0; dayCount--) {

				// if we have a match and there isn't already a position open,
				// we need to make a record of it and flag the position open boolean.
			if ((strategies.testStrategyAtDay(sd,
					strategyId, dayCount)) &&
					(!positionOpen)) {

				BacktestLogEntry entry =
					new BacktestLogEntry(sd, indexData, strategies, strategyId, dayCount);

				log.add(entry);

				everyMatch.add(entry);

				if (strategies.waitsForExit(strategyId))
					positionOpen = true;

			}	// if we have a match.

				// If we already have a match and a position open, then we should check
				// to see if the position now needs to be closed.
			else if (positionOpen) {

				if (strategies.testExitAtDay(sd, strategyId, dayCount))
					positionOpen = false;

			}

		}	// cycle through each day.

			// Now, we have to calculate the averages so that the data that comes back is averaged,
			// not a total.
		log.calculateAverages();

		return log;

	}	// backtestOneStockOneStrategy

	private static final void outputMatrixHeaderLine(PrintWriter out, BacktestStrategies[] strategies) {

		out.print(",,");

			// we need to iterate through both dimensions of the strategies object and spit out
			// the descriptions of each strategy.
		for (int countStrategyObjects = 0; countStrategyObjects < strategies.length; countStrategyObjects++) {

			for (int countStrategyIds = 0;
				countStrategyIds < strategies[countStrategyObjects].getNumberOfStrategies();
				countStrategyIds++) {

				out.print(strategies[countStrategyObjects].getStrategyName(countStrategyIds));

				out.print(",");

			}	// each strategy ID

		}	// each strategy object

		out.println();

	}	// displayMatrixHeaderLine

	private static final void outputMatrixDataLine(PrintWriter out, BacktestLog[][] logs, String ticker) {

		out.print(ticker);
		out.print(",,");

			// we need to iterate through each level of the 2D array and print out two bits of data --
			// the average change from inception of a position until close and the percentage of time
			// such a position is accurate.
		for (int countOuterArrays = 0; countOuterArrays < logs.length; countOuterArrays++) {

			for (int countInnerArrays = 0; countInnerArrays < logs[countOuterArrays].length; countInnerArrays++) {

				BacktestLogEntry entry = logs[countOuterArrays][countInnerArrays].getAverages();

				out.print(entry.getMocToExit());
				out.print("% (");
				out.print(entry.getPercentAccurate());
				out.print("% / ");
				out.print(entry.getPercentMatches());
				out.print("% ),");

			}

		}

		out.println();

	}	// outputMatrixDataLine

	private static final String TRADE_REPORT_ROOT = "trades_";
	static final String TRADE_REPORTS_DIRECTORY = "reports/trades";

	/**
	*	This method outputs data on every trade.  It is passed a hash table of <ticker, BacktestLogEntry>
	*/
	private static final void outputTradeReport(Vector tradeHash) {

		String tradesFileName = TRADE_REPORT_ROOT + System.currentTimeMillis() + ".csv";

		PrintWriter tradesOut = StockCentral.createOutputFile(tradesFileName, TRADE_REPORTS_DIRECTORY);

		tradesOut.println("Ticker,Strategy,Opening Date,Closing Date,Delta");
		tradesOut.println();

		Enumeration trades = tradeHash.elements();

		while (trades.hasMoreElements()) {

			BacktestLogEntry trade = (BacktestLogEntry)trades.nextElement();

			tradesOut.println(trade.getTicker() + "," + trade.getStrategyName() + "," +
				StockCentral.translateCalendarToString(trade.getPositionOpenDate()) + "," +
				StockCentral.translateCalendarToString(trade.getPositionCloseDate()) + "," +
				trade.getMocToExit());

		}

		tradesOut.close();

	}	// outputTradeReport

	private static PrintWriter createTradeWriter() {

		String tradesFileName = TRADE_REPORT_ROOT + System.currentTimeMillis() + ".csv";

		PrintWriter toReturn= StockCentral.createOutputFile(tradesFileName, TRADE_REPORTS_DIRECTORY);

		return toReturn;

	}	// createTradeWriter

	private static void outputTradeReportHeaderLine(PrintWriter tradesOut) {

		tradesOut.println("Ticker,Strategy,Opening Date,Closing Date,Delta");
		tradesOut.println();

	}	// outputTradeReportHeaderLine

	private static void outputTradeReportEntries(Vector tradeHash, PrintWriter tradesOut) {

		Enumeration trades = tradeHash.elements();

		while (trades.hasMoreElements()) {

			BacktestLogEntry trade = (BacktestLogEntry)trades.nextElement();

			tradesOut.println(trade.getTicker() + "," + trade.getStrategyName() + "," +
				StockCentral.translateCalendarToString(trade.getPositionOpenDate()) + "," +
				StockCentral.translateCalendarToString(trade.getPositionCloseDate()) + "," +
				trade.getMocToExit());

		}

	}	// outputTradeReportEntries

	private static BacktestLog[][] createStockLogArray(BacktestStrategies[] activeStrategies, boolean createLogs) {

		BacktestLog[][] toReturn = new BacktestLog[activeStrategies.length][];

			// we have to look at each BacktestStrategies object to find out how many strategies
			// each has so we can shape the 2-D array.
		for (int countStratObjs = 0; countStratObjs < activeStrategies.length; countStratObjs++) {

			toReturn[countStratObjs] =
				new BacktestLog[activeStrategies[countStratObjs].getNumberOfStrategies()];

				// if we have been asked to create blank objects, we do that here.
			if (createLogs) {

				for (int countStrategies = 0;
					countStrategies < activeStrategies[countStratObjs].getNumberOfStrategies();
					countStrategies++)

						toReturn[countStratObjs][countStrategies] =
							new BacktestLog(activeStrategies[countStratObjs], countStrategies);
			}	// if

		}	// for

		return toReturn;

	}	// createStockLogArray

}	// class Backtester

/*
			// we have to look at each BacktestStrategies object to find out how many strategies
			// each has so we can shape the 2-D array.
		for (int countStratObjs = 0; countStratObjs < activeStrategies.length; countStratObjs++) {

			logs[countStratObjs] = new BacktestLog[activeStrategies[countStratObjs].getNumberOfStrategies()];

			positionsOpen[countStratObjs] =
				new boolean[activeStrategies[countStratObjs].getNumberOfStrategies()];

				// next, we need to initialize logs for each part of the array.
			for (int countStrats = 0;
				countStrats < activeStrategies[countStratObjs].getNumberOfStrategies(); countStrats++) {

				logs[countStratObjs][countStrats] =
					new BacktestLog(activeStrategies[countStratObjs], countStrats);

			}	// cycle through each and every strategy.

		}	// cycle through BacktestStrategy objects.

//			EVERYTHING BELOW NEEDS TO BE COMMENTED OUT, AT LEAST FOR NOW
/*
			//backtestForOneStock(sd, indexData, activeStrategies, logs);

				// This looks through each day's data and checks each of the backtest strategies.
			for (int dayCount = closes.length - 1; dayCount >= 0; dayCount--) {

					// We have to pick through each BacktestStrategies object.
				for (int countStrategyObjects = 0; countStrategyObjects < activeStrategies.length;
					countStrategyObjects++) {

						// finally, let's look through each individual strategy.
					for (int countStrategyIds = 0;
						countStrategyIds < activeStrategies[countStrategyObjects].getNumberOfStrategies();
						countStrategyIds++) {

							// if we have a match and there isn't already a position open,
							// we need to make a record of it and flag the position open boolean.
						if ((activeStrategies[countStrategyObjects].testStrategyAtDay(sd,
								countStrategyIds, dayCount)) &&
								(!positionsOpen[countStrategyObjects][countStrategyIds])) {

							BacktestLogEntry entry =
								new BacktestLogEntry(sd, indexData, activeStrategies[countStrategyObjects],
										countStrategyIds, dayCount);

							logs[countStrategyObjects][countStrategyIds].add(entry);

							if (activeStrategies[countStrategyObjects].waitsForExit(countStrategyIds))
								positionsOpen[countStrategyObjects][countStrategyIds] = true;

						}	// if we have a match.

							// If we already have a match and a position open, then we should check
							// to see if the position now needs to be closed.
						else if (positionsOpen[countStrategyObjects][countStrategyIds]) {

							if (activeStrategies[countStrategyObjects].testExitAtDay(sd,
									countStrategyIds, dayCount))
								positionsOpen[countStrategyObjects][countStrategyIds] = false;

						}

					}	// cycle through individual strategies.

				}	// cycle through BacktestStrategies objects.

			}	// cycle through each day.


		}	// cycle through each StockData object.
*/
//		NOTE:  I DECIDED TO COMMENT THIS OUT FOR RIGHT NOW BECAUSE I THINK THE INFO
//		IN THE STOCK/STRATEGY MATRIX WILL BE MORE USEFUL.

/*			// Next, we need to compile the numbers for each strategy.
		for (int countStrategyObjects = 0; countStrategyObjects < logs.length; countStrategyObjects++) {

			for (int countStrategies = 0; countStrategies < logs[countStrategyObjects].length;
				countStrategies++) {

				Iterator<BacktestLogStorable> entries = logs[countStrategyObjects][countStrategies].iterator();

				while (entries.hasNext()) {

					logs[countStrategyObjects][countStrategies].m_totals.addEntry(entries.next());

				}	// cycle through every match

				logs[countStrategyObjects][countStrategies].
					calculatePercentMatches(numTradingDays * dataArray.size());

				logs[countStrategyObjects][countStrategies].m_averages =
					logs[countStrategyObjects][countStrategies].
					m_totals.
					averageEntry(logs[countStrategyObjects][countStrategies].
							size(), 0);

			}	// cycle though each strategy.

		}	// cycle through the first level of the logs array.

			// Next, we need to pick the best strategy for each time frame.
			// NOTE:  I will add this later.
*/

			// Finally, we need to output the info for each and every strategy (by having each log
			// print out both its summary and individual trade information.  And then we're going to
			// have it print out summary information for ALL of the strategies.
/*		for (int countStrategyObjects = 0; countStrategyObjects < logs.length; countStrategyObjects++) {

			for (int countStrategies = 0; countStrategies < logs[countStrategyObjects].length;
				countStrategies++) {

				logs[countStrategyObjects][countStrategies].outputBacktestReports();

			}	// cycle though each strategy.

		}	// cycle through the first level of the logs array.
*/

