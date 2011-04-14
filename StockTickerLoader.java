/**
 *
 */
package stockcentral;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Serializable;
import java.util.*;
import java.io.*;
import java.net.URL;

/**
 * @author Jack Schillaci
 * @version Build 2/16/2010
 *
 */
public class StockTickerLoader implements Serializable {

	public static final String DEFAULT_FILENAME = "default_tickers.tkr";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String fileName;

			// First, we need to figure out if we're using the default tickers output file.
		if ((args != null) && (args.length >0))
			fileName = args[0];
		else
			fileName = DEFAULT_FILENAME;

/*			// Next, let's get the the tickers from a method.
		ArrayList<String> tickers = pullETFTickers();
*/
//		ArrayList<String> etfTickers = parseESDList(ETF_FILE_NAME);
		ArrayList<String> sp500Tickers = parseESDList(SP500_FILE_NAME);
//		ArrayList<String> russ3000Tickers = parseESDList(RUSSEL3000_FILE_NAME);

			// Third, we need to create a StockTickerArray and load up each and every stock ticker.
		StockTickerArray tickerArray = new StockTickerArray();

//		tickerArray.addAll(etfTickers);
		tickerArray.addAll(sp500Tickers);
//		tickerArray.addAll(russ3000Tickers);

			// Finally, we need to save this into a file.
		StockCentral.serializeObject(fileName, null, tickerArray);

	} // main

	private static ArrayList<String> getTickers() {

		ArrayList<String> toReturn = new ArrayList<String>();
		String[] tickers = StockCentralConstants.ETFS;

		for (int count = 0; count < tickers.length; count++)
			toReturn.add(tickers[count]);

		return toReturn;

	}	// getTickers

	private static final String ETF_TICKERS_URL =
	    "http://www.masterdata.com/HelpFiles/ETF_List_Downloads/AllETFs.csv";

	private static ArrayList<String> pullETFTickers() {

	    ArrayList<String> toReturn = new ArrayList<String>();

	    System.out.println("Pull ETF Tickers:");

        try {

            URL u = new URL(ETF_TICKERS_URL);

            InputStream in = u.openStream();
            LineNumberReader read = new LineNumberReader(new InputStreamReader(in));

		        // we need to skip the first line because it just contains
		        // the legend.
		    String legendLine = read.readLine();

		        // Next, we need to parse out each line.  The first element is the fund's
		        // name, the next is its symbol, then its type, and then maybe some
		        // other info.  We only care about the symbol, so that's what we're
		        // pulling out.

		    String nextLine;

		        // we have to cycle through this until we get a null (the end of the
		        // file).
		    while ((nextLine = read.readLine()) != null) {

		    		// we have to tokenize this and then skip the first token.
		    	StringTokenizer tokens = new StringTokenizer(nextLine, ",", false);

		    	tokens.nextToken();

		    	String nextTicker = tokens.nextToken();

		    	toReturn.add(nextTicker);

		    	System.out.print(nextTicker + ", ");

		    }
        }	// try
        catch (IOException e) { e.printStackTrace(); }

        return toReturn;

	}

	private static final String DEFAULT_FILE_NAME = "etfs.csv";
	private static final String ETF_FILE_NAME = "etfs.csv";
	private static final String SP500_FILE_NAME = "sp500.csv";
	private static final String RUSSEL3000_FILE_NAME = "rus3000.csv";

	/**
	*	This method parses a file listing ticker components of an index downloaded from easystockdata.com
	*/
	private static ArrayList<String> parseESDList(String fileName) {

		ArrayList<String> toReturn = new ArrayList<String>();

		if (fileName == null)
			fileName = DEFAULT_FILE_NAME;

		try {
				// first, let's open the file.
			LineNumberReader read =
				new LineNumberReader(new InputStreamReader(new FileInputStream(fileName)));


				// now that we have the file open, we have to skip the first three lines (which are just garbage).
			read.readLine();
			read.readLine();
			read.readLine();

				// next, we parse out the tickers.  conveniently, the first item in each CSV file is
				// the ticker, so we just pull that out.
			String nextLine;

			while ((nextLine = read.readLine()) != null) {

				StringTokenizer tokens = new StringTokenizer(nextLine, ",", false);

				String nextTicker = tokens.nextToken();

				toReturn.add(nextTicker);

				System.out.println(nextTicker);

			}	// while there are still lines left in this file.


		} // try

		catch (Exception e) {

			e.printStackTrace();


		}	// catch

		return toReturn;
	}

}	// class StockTickerLoader