/**
 *
 */
package stockcentral;

import java.util.*;
import java.io.*;

/**
 * @author Jack's Dell
 *
 */
public class StockSurveiller {

	private static final String SURVEILLANCE_FILE_ROOT = "Surveillance_";
	private static final String SURVEILLANCE_DIRECTORY = "surveillance";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Calendar today = Calendar.getInstance();

			// First, we load up the appropriate StockDataArray object, which we then
			// will use to load up individual StockData objects.
		String fileName;

		if ((args != null) && (args.length > 0))
			fileName = args[0];
		else
			fileName = StockDataLoader.DEFAULT_DATA;

		StockDataArray dataArray = (StockDataArray)StockCentral.deserializeObject(fileName, null);

		StockData indexData = dataArray.getIndexData();

		dataArray.restartArray();
		int numTradingDays = indexData.getCloses().length;

		StockCentral central = new StockCentral();

		System.out.println("Conducting stock surveillance for " + dataArray.size() + " stocks on " +
				StockCentral.generateDateString(today) + ".");

			// next, load up all the strategies.
		BacktestStrategies[] activeStrategies = ActiveBacktestStrategies.getStrategies();

			// now, we need to open a new CSV file for the surveillance.
		String outputFileName = SURVEILLANCE_FILE_ROOT + System.currentTimeMillis() + ".csv";

		PrintWriter surveillanceOut =
			StockCentral.createOutputFile(outputFileName, SURVEILLANCE_DIRECTORY);

		surveillanceOut.println("Ticker,Strategy,Strategy Backtest Results,Accuracy%,Frequency%");

			// Here's the heart of the bit -- we run each stock through each strategy.  if
			// we have a match, we make a record of it.  We also conduct a backtest and
			// output the results of that.
		for (int countStocks = 0; countStocks < dataArray.size(); countStocks++) {

			StockData sd = dataArray.getNextData();
			sd.calculateBellsAndWhistles();
			sd.spitOutData();

			System.out.println(sd.getTicker());

			surveillanceForOneStock(sd, activeStrategies, surveillanceOut, indexData);

/*
				// go through each strategy object.
			for (int countStrategyObjects = 0; countStrategyObjects < activeStrategies.length;
				countStrategyObjects++) {

					// and then go through each individual strategy.
				for (int countStrategies = 0;
					countStrategies < activeStrategies[countStrategyObjects].getNumberOfStrategies();
					countStrategies++) {

							// if we have a set up for this particular strategy for this stock, then
							// we conduct a backtest and then we output all the information into the
							// file.
						if (activeStrategies[countStrategyObjects].testStrategyAtDay(sd,
								countStrategies, 0)) {

							BacktestLog log = Backtester.backtestOneStockOneStrategy(sd, indexData,
									activeStrategies[countStrategyObjects],
									countStrategies, new Vector());

							surveillanceOut.print(sd.getTicker());
							surveillanceOut.print(',');
							surveillanceOut.print(activeStrategies[countStrategyObjects].getStrategyName(countStrategies));
							surveillanceOut.print(',');

							BacktestLogEntry entry = log.getAverages();

							surveillanceOut.print(entry.getMocToExit());
							surveillanceOut.print("% (");
							surveillanceOut.print(entry.getPercentAccurate());
							surveillanceOut.print("% / ");
							surveillanceOut.print(entry.getPercentMatches());
							surveillanceOut.print("% )");

							surveillanceOut.println();

						}

					}
			}
*/
		}	// for

		surveillanceOut.close();

	}	// main()

	public static void surveillanceForOneStock (StockData sd, BacktestStrategies[] activeStrategies,
			PrintWriter surveillanceOut, StockData indexData) {

				// go through each strategy object.
			for (int countStrategyObjects = 0; countStrategyObjects < activeStrategies.length;
				countStrategyObjects++) {

					// and then go through each individual strategy.
				for (int countStrategies = 0;
					countStrategies < activeStrategies[countStrategyObjects].getNumberOfStrategies();
					countStrategies++) {

							// if we have a set up for this particular strategy for this stock, then
							// we conduct a backtest and then we output all the information into the
							// file.
						if (activeStrategies[countStrategyObjects].testStrategyAtDay(sd,
								countStrategies, 0)) {

							BacktestLog log = Backtester.backtestOneStockOneStrategy(sd, indexData,
									activeStrategies[countStrategyObjects],
									countStrategies, new Vector());

							surveillanceOut.print(sd.getTicker());
							surveillanceOut.print(',');
							surveillanceOut.print(activeStrategies[countStrategyObjects].getStrategyName(countStrategies));
							surveillanceOut.print(',');

							BacktestLogEntry entry = log.getAverages();

							surveillanceOut.print(entry.getMocToExit());
							surveillanceOut.print("%,");
							surveillanceOut.print(entry.getPercentAccurate());
							surveillanceOut.print("%,");
							surveillanceOut.print(entry.getPercentMatches());

							surveillanceOut.println();

						}

					}
			}


	}	// surveillanceForOneStock

}	// class StockSurveiller
