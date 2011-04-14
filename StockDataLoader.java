/**
 *
 */

package stockcentral;

import stockcentral.*;
import java.io.*;
import java.util.*;

/**
 * @author Jack Schillaci
 * @version Build 2/16/2010
 *
 */
public class StockDataLoader {

	public static final String DEFAULT_DATA = "default_data_array.dar";
	public static final String DATA_DIRECTORY = "stockdata";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		StockDataArray dataArray = new StockDataArray();

		StockDataGrabber sdg = new YahooStockDataGrabber();

		String tickerFileName, dataFileName;

			// First, we need to see if we have been passed input and output file names, and if not,
			// we need to invoke the defaults.
		if ((args != null) && (args.length > 1)) {

			tickerFileName = args[0];
			dataFileName = args[1];

		}	// if the user has passed file names.
		else {

			tickerFileName = stockcentral.StockTickerLoader.DEFAULT_FILENAME;
			dataFileName = DEFAULT_DATA;

		}	// if no file names have been passed.

			// Now, we need to load the tickers up.
		StockTickerArray tickerArray =
			(StockTickerArray)StockCentral.deserializeObject(tickerFileName, null);
		Iterator<String> tickers = tickerArray.iterator();

			// Now, we must cycle through and pull up the data for each ticker, save the StockData
			// object, and add it to the StockDataArray.
		System.out.println("Loading and saving data for " + tickerArray.size() + " stocks.");

		int numStocksInArray = 0;

		while (tickers.hasNext()){

			String ticker = (String)tickers.next();

//			StockDataLoaderThread thread = new StockDataLoaderThread(ticker, dataArray, sdg);

//			thread.run();

			try {

				System.out.println("Loading data for stock:  " + ticker);

				StockData sd = sdg.pullStockPriceHistoricalData(ticker);

					// we only care about stocks that have a volume of at least 25000.
				if (sd.getVolumes()[0] >= 25000) {

					// sd.calculateBellsAndWhistles();

					String sdFileName = generateDataFileName(ticker);

					StockCentral.serializeObject(sdFileName, DATA_DIRECTORY, sd);

					dataArray.add(sdFileName);

					numStocksInArray++;

				}	// if
				else
					System.out.println("Data for stock " + ticker + " skipped for insufficient volume");

			}	// try
			catch (FileNotFoundException e) {

				System.out.println("Couldn't get the data for a stock.");

			}	// catch


		}	// cycling through each ticker

		System.out.println("\n\nIncluded data for " + numStocksInArray + " stocks.");

		// THIS NEEDS TO STOP UNTIL THE DATA IS COMPLETELY LOADED, AND THEN WE CAN SAVE IT.

			// Finally, save the StockDataArray object.
		StockCentral.serializeObject(dataFileName, null, dataArray);

	}	// main()

	public static final String generateDataFileName(String ticker) {

		String toReturn = null;

		Calendar now = new GregorianCalendar();

		toReturn = ticker + "_" + now.get(Calendar.MONTH) + "_" + now.get(Calendar.DATE) + "_" +
			now.get(Calendar.YEAR) + "_" + now.get(Calendar.HOUR_OF_DAY) + "_" + now.get(Calendar.MINUTE) +
			".sdt";

		return toReturn;

	}	// generateDataFileName()

}

class StockDataLoaderThread extends Thread {

	private String m_ticker;

	private StockDataArray m_dataArray;

	private StockDataGrabber m_sdg;

	public StockDataLoaderThread(String ticker, StockDataArray dataArray, StockDataGrabber sdg) {

		m_ticker = ticker;
		m_dataArray = dataArray;
		m_sdg = sdg;

	}	// ctor

	public void run() {

		try {

			System.out.println("Loading data for stock:  " + m_ticker);

			StockData sd = m_sdg.pullStockPriceHistoricalData(m_ticker);

			// sd.calculateBellsAndWhistles();

			String sdFileName = generateDataFileName(m_ticker);

			StockCentral.serializeObject(sdFileName, StockDataLoader.DATA_DIRECTORY, sd);

			m_dataArray.add(sdFileName);

		}	// try
		catch (FileNotFoundException e) {

			System.out.println("Couldn't get the data for a stock.");

		}	// catch

	}	// run()

	private static final String generateDataFileName(String ticker) {

		String toReturn = null;

		Calendar now = new GregorianCalendar();

		toReturn = ticker + "_" + now.get(Calendar.MONTH) + "_" + now.get(Calendar.DATE) + "_" +
			now.get(Calendar.YEAR) + "_" + now.get(Calendar.HOUR_OF_DAY) + "_" + now.get(Calendar.MINUTE) +
			".sdt";

		return toReturn;

	}	// generateDataFileName()

}	// class StockDataLoader
