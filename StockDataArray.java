/**
 *
 */
package stockcentral;

import java.io.Serializable;
import java.io.*;
import java.util.*;

/**
 * @author Jack's Dell
 *
 */
public class StockDataArray extends ArrayList<String> implements Serializable {

	private static final String INDEX_TICKER = "^GSPC";

	Calendar m_creationDate;

	private StockData m_indexData = null;

	private Iterator<String> m_elements = null;

	public StockDataArray() {

		m_creationDate = new GregorianCalendar();

		StockDataGrabber sdg = new YahooStockDataGrabber();

		try {

			m_indexData = sdg.pullStockPriceHistoricalData(INDEX_TICKER);

		}	// try
		catch (FileNotFoundException e) {

			System.out.println("Couldn't pull index data.");

		}	// catch

	}	// ctor

	public final void restartArray() {

		m_elements = iterator();

	}// restartArray

	public final StockData getNextData() {

		StockData toReturn = null;

		String nextFileName = (String)m_elements.next();

		toReturn = (StockData)StockCentral.deserializeObject(nextFileName, StockDataLoader.DATA_DIRECTORY);

		return toReturn;

	}

	public final StockData getIndexData() {

		return m_indexData;

	}	// getIndexData


}
