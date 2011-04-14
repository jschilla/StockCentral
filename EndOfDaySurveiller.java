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
public class EndOfDaySurveiller {

	private static final String SURVEILLANCE_FILE_ROOT = "EODSurveillance_";
	private static final String SURVEILLANCE_DIRECTORY = "surveillance";

	private static final float[] ZERO_ARRAY = { 0.0f };
	private static final long[] ZERO_LONG_ARRAY = { 0l };
	private static final Calendar[] ZERO_CALENDAR_ARRAY = { Calendar.getInstance() };

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

			// We may eventually want to add in a quick call to the StockDataUpdater as an initial step.

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

		StockDataGrabber sdg = new YahooStockDataGrabber();

		System.out.println("Conducting end of day stock surveillance for " + dataArray.size() + " stocks on " +
				StockCentral.generateDateString(today) + ".");

			// next, load up all the strategies.
		BacktestStrategies[] activeStrategies = ActiveBacktestStrategies.getStrategies();

			// now, we need to open a new CSV file for the surveillance.
		String outputFileName = SURVEILLANCE_FILE_ROOT + System.currentTimeMillis() + ".csv";

		PrintWriter surveillanceOut =
			StockCentral.createOutputFile(outputFileName, SURVEILLANCE_DIRECTORY);

		surveillanceOut.println("Ticker,Strategy,Strategy Backtest Results");

			// Here's the heart of the bit -- first, we update each stock with the current trade and then
			// we run each stock through each strategy.  if
			// we have a match, we make a record of it.  We also conduct a backtest and
			// output the results of that.
		for (int countStocks = 0; countStocks < dataArray.size(); countStocks++) {

			StockData sd = dataArray.getNextData();

				// next, we have to get the current stock price for this stock.
			float[] currentPrice = new float[1];
			currentPrice[0] = sdg.pullLastTradePrice(sd.getTicker());

				// then, we create a whole new StockData object with the current price as the most recent close.
			StockData newSD = new StockData();

				// note that we are using zeros for all of this right now because at the moment all of our
				// strategies really just rely on closing price.  this may need to change in the future.
			newSD.setCloses(StockCentral.mergeFloatArrays(currentPrice, sd.getCloses()));
			newSD.setHighs(StockCentral.mergeFloatArrays(ZERO_ARRAY, sd.getHighs()));
			newSD.setLows(StockCentral.mergeFloatArrays(ZERO_ARRAY, sd.getLows()));
			newSD.setOpens(StockCentral.mergeFloatArrays(ZERO_ARRAY, sd.getOpens()));
			newSD.setVolumes(StockCentral.mergeLongArrays(ZERO_LONG_ARRAY, sd.getVolumes()));
			newSD.setDates(StockCentral.mergeCalendarArrays(ZERO_CALENDAR_ARRAY, sd.getDates()));
			newSD.setTicker(sd.getTicker());


			newSD.calculateBellsAndWhistles();
			newSD.spitOutData();

			System.out.println(sd.getTicker());

				// WARNING:  THE INDEX DATA NEEDS TO BE UDPATED!!!!

			StockSurveiller.surveillanceForOneStock(newSD, activeStrategies, surveillanceOut, indexData);

		}	// for

		surveillanceOut.close();

	}	// main()


}	// class StockSurveiller
