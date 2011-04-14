/**
 *
 */
package stockcentral;

import java.util.Calendar;
import java.io.*;

/**
 * This interface represents a system that can load stock data from some source.
 *
 * @author Jack Schillaci
 * @version Build 1/14/2007
 *
 */
public interface StockDataGrabber {

	public abstract float pullLastTradePrice(String ticker);

	public abstract StockData pullStockPriceHistoricalData(String ticker) throws FileNotFoundException;

	public abstract String[] pullListOfAvailableStocks();

	public abstract StockData updateStockPriceData (String ticker, Calendar last, Calendar today,
			StockData sd) throws FileNotFoundException;

}
