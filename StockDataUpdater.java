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
public class StockDataUpdater {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Calendar today = Calendar.getInstance();

			// First, we load up the appropriate StockDataArray object, which we then
			// will use to load up individual StockData objects.
		String dataFileName;

		if ((args != null) && (args.length > 0))
			dataFileName = args[0];
		else
			dataFileName = StockDataLoader.DEFAULT_DATA;

		StockDataArray oldData = (StockDataArray)StockCentral.deserializeObject(dataFileName, null);

		StockData indexData = oldData.getIndexData();

		oldData.restartArray();

		StockCentral central = new StockCentral();

		StockDataGrabber sdg = new YahooStockDataGrabber();

			// this is where the updated StockData objects will go.
		StockDataArray newData = new StockDataArray();

			// next, we need to go through each stock and update the data
		System.out.println("Updating stock data for " + oldData.size() + " stocks (plus index data).");
		System.out.println("Today's Date:  " + StockCentral.translateCalendarToString(today));
		System.out.println("Date of last data:  " +
				StockCentral.translateCalendarToString(indexData.getDates()[0]));

			// go through each stock, update the data, and then save it and add it to the new array.
		for (int countStocks = 0; countStocks < oldData.size(); countStocks++) {

			try {

				StockData oldSD = oldData.getNextData();

				String ticker = oldSD.getTicker();

				System.out.println(ticker);

				StockData newSD = sdg.updateStockPriceData(ticker, oldSD.getDates()[0], today, oldSD);

					// FOR SOME REASON, THIS IS LOADING THE DATE OF THE PREVIOUS DAY UP ONE FOR SOME REASON
				newSD.spitOutData();

					// I may want to add a filter here somewhere.
				String newFileName = StockDataLoader.generateDataFileName(ticker);

				StockCentral.serializeObject(newFileName, StockDataLoader.DATA_DIRECTORY, newData);

				newData.add(newFileName);

			}	// try
			catch (FileNotFoundException e) {

				System.out.println("Couldn't update data for a stock.");

			} 	// catch

		}	// for countStocks

		StockCentral.serializeObject(dataFileName, null, newData);

	}	// main(String[])


}	// class StockDataUpdater