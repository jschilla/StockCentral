/**
 *
 */
package stockcentral;

import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.text.*;

/**
 * This class interfaces with Yahoo! Finance and downloads stock data from there.
 *
 * @author Jack Schillaci
 * @version Build 1/14/2007
 *
 */
public class YahooStockDataGrabber implements StockDataGrabber, StockCentralConstants {

	// constants

    private static final String YAHOO_CURRENT_PRICE_URL =
    	"http://finance.yahoo.com/d/quotes.csv?f=sl1d1t1c1ohgv&e=.csv&s=";
    private static final String STOCK_DATA_OPEN = "http://ichart.finance.yahoo.com/table.csv?g=d&";
    private static final int YEAR_OFFSET = 10;
    private static final int MONTH_OFFSET = 0;
    private static final int DATE_OFFSET = 0;

    private static final String[] MONTH_NAMES = {"Ja", "Fe", "Mar", "Ap", "May", "Jun", "Jul",
    	"Au", "Se", "Oc", "No", "De"};

    // instance methods

	/* (non-Javadoc)
	 * @see stockcentral.StockDataGrabber#pullLastTradePrice(java.lang.String)
	 */
	public float pullLastTradePrice(String ticker) {

        float toReturn = 0;

        try {

            URL u = new URL(YAHOO_CURRENT_PRICE_URL + ticker);

            InputStream in = u.openStream();
            LineNumberReader read = new LineNumberReader(new InputStreamReader(in));

            String nextLine = read.readLine();

            StringTokenizer tokens = new StringTokenizer(nextLine, ",", false);

                // We're looking for the second token.

            tokens.nextToken();     // skip the first one.

            String currentPriceAsFloat = tokens.nextToken();


            toReturn = new Float(currentPriceAsFloat).floatValue();
        }   // try
        catch (Exception e) {
        	System.out.println();
        	System.out.println("Exception thrown in pulling price for stock " +
        			ticker + ".");
        	e.printStackTrace();
        }

        StockCentral.dataMonitorOutput("Last trade for stock " + ticker + " was " + toReturn);
        StockCentral.debugOutput("Pulled last trade price for stock " + ticker);

        return toReturn;

	}	// pullLastTradePrice(String)

	public StockData updateStockPriceData(String ticker, Calendar lastUpdate, Calendar today, StockData sd)
		throws FileNotFoundException {

		float[] newHighs, newLows, newOpens, newCloses;
		long[] newVolumes;
		Calendar[] newDates;

		Stack s = new Stack();

		int numNewTradingDates = 0;

/*		Calendar lastUpdate = Calendar.getInstance();
		lastUpdate.setTime(l);
		Calendar today = Calendar.getInstance();
		today.setTime(t);
*/
			// we have to increase this by one so that we don't accidently get a duplicate
			// of the most recent day.
		lastUpdate.set(Calendar.DAY_OF_YEAR, (lastUpdate.get(Calendar.DAY_OF_YEAR) + 1));

		String urlString = createStockDataUrl(ticker, lastUpdate, today);

			// now, we pull up the updating data and pull open the data, the same way we would
			// if we pulled the ENTIRE data.  hopefully, this is faster than loading all
			// the data.
		try {
        	URL u = new URL(urlString);

        	InputStream in = u.openStream();
        	LineNumberReader read = new LineNumberReader(new InputStreamReader(in));

        	String nextLine = read.readLine();
        	while (nextLine != null) {

        		s.push(nextLine);
        		numNewTradingDates++;

        		nextLine = read.readLine();

        	}   // while

        	numNewTradingDates--;   	// this must be deducted by one to avoid the line that just
        								// has titles.

        		// initialize the variable arrays that this information is going to be loaded into.
        	newOpens = new float[numNewTradingDates];
        	newCloses = new float[numNewTradingDates];
        	newHighs = new float[numNewTradingDates];
        	newLows = new float[numNewTradingDates];
        	newVolumes = new long[numNewTradingDates];
        	newDates = new Calendar[numNewTradingDates];

        		// this data has to be in most-recent-day-first order, so we create an
        		// Enumeration and pull each entry out and parse it out.

        	Enumeration e = s.elements();

        	int lineCount = 0;

        	while (e.hasMoreElements()) {

        		String line = (String)e.nextElement();

        		StringTokenizer tokens = new StringTokenizer(line, ",");

        		String dateString = tokens.nextToken();

                   // this will avoid the final line, which is just the dates.
        		if (!(dateString.startsWith("Date")))
        		{
        			try {

        			newDates[lineCount] = parseDateFromYahoo(dateString);

        			}	// try
        			catch (RuntimeException exception) {

        				exception.printStackTrace();
        				System.out.println("Failure was on stock " + ticker);
        				System.out.println(today);
        				System.exit(0);

        			}	// catch

        			newOpens[lineCount] = Float.parseFloat(tokens.nextToken()); // open price
        			newHighs[lineCount] = Float.parseFloat(tokens.nextToken()); // high price
        			newLows[lineCount] = Float.parseFloat(tokens.nextToken()); // low price
        			newCloses[lineCount] = Float.parseFloat(tokens.nextToken()); // get the close price
        			newVolumes[lineCount] = Long.parseLong(tokens.nextToken());    // get the volume

        			lineCount++;

        		}       // if

        	}   // while there are still lines to be processed.

        	float[] oldOpens = sd.getOpens();
        	float[] oldCloses = sd.getCloses();
        	float[] oldHighs = sd.getHighs();
        	float[] oldLows = sd.getLows();
        	long[] oldVolumes = sd.getVolumes();
        	Calendar[] oldDates = sd.getDates();

        		// ok, so now we have to combine the old ones and the new ones and then
        		// put it back in the stock data file.  we have to start at the back of the arrays,
        		// because the data has to be "front-loaded" (i.e., most recent dates first)
        		// when it gets into the array.

        	int numOldTradingDates = sd.getNumTradingDates();
        	int totalTradingDates = numOldTradingDates + numNewTradingDates;

        	float[] allOpens = new float[totalTradingDates], allCloses =
        		new float[totalTradingDates], allHighs = new float[totalTradingDates],
        		allLows = new float[totalTradingDates];
        	long[] allVolumes = new long[totalTradingDates];
        	Calendar[] allDates = new Calendar[totalTradingDates];

		allOpens = StockCentral.mergeFloatArrays(newOpens, oldOpens);
		allCloses = StockCentral.mergeFloatArrays(newCloses, oldCloses);
		allHighs = StockCentral.mergeFloatArrays(newHighs, oldHighs);
		allLows = StockCentral.mergeFloatArrays(newLows, oldLows);
		allVolumes = StockCentral.mergeLongArrays(newVolumes, oldVolumes);
		allDates = (Calendar[])StockCentral.mergeCalendarArrays(newDates, oldDates);
/*
        		// first, we add the new ones, then we add the old ones.
        	for (int i = 0; i < numNewTradingDates; i++) {

        		allOpens[i] = newOpens[i];
        		allCloses[i] = newCloses[i];
        		allHighs[i] = newHighs[i];
        		allLows[i] = newLows[i];
        		allVolumes[i] = newVolumes[i];
        		allDates[i] = newDates[i];

        	}	// for

        		// now, the OLD ONES!!!!!
        	for (int i = numNewTradingDates; i < totalTradingDates; i++) {

        		int oldIndex = i - numNewTradingDates;

        		allOpens[i] = oldOpens[oldIndex];
        		allCloses[i] = oldCloses[oldIndex];
        		allHighs[i] = oldHighs[oldIndex];
        		allLows[i] = oldLows[oldIndex];
        		allVolumes[i] = oldVolumes[oldIndex];
        		allDates[i] = oldDates[oldIndex];

        	}	// for
*/
        	sd.setOpens(allOpens);
        	sd.setCloses(allCloses);
        	sd.setHighs(allHighs);
        	sd.setLows(allLows);
        	sd.setVolumes(allVolumes);
        	sd.setDates(allDates);

        	sd.setNumTradingDates(totalTradingDates);

        }   // try
        catch (FileNotFoundException e) {
        	throw e;
        }
        catch (Exception e) {
        	System.out.println("Exception thrown in import of stock data.");
        	e.printStackTrace();

        	System.exit(0);
        }   // catch

		return sd;

	}	// updateStockPriceData

	/* (non-Javadoc)
	 * @see stockcentral.StockDataGrabber#pullStockPriceHistoricalData(java.lang.String)
	 */
	public StockData pullStockPriceHistoricalData(String ticker) throws FileNotFoundException {

		StockCentral.debugOutput("Starting Yahoo! historical price data for stock " + ticker);

		StockData sd = new StockData();

		float[] opens, closes, highs, lows;
		long[] volumes;
		Calendar[] dates;

        Stack s = new Stack();

        int numberOfTradingDates = 0;

        // Create the start and end dates for this string
        Calendar today = Calendar.getInstance();
        int date = today.get(Calendar.DATE);
        date -= 5;
        today.set(Calendar.DATE, date);

        Calendar startDate = Calendar.getInstance();
        startDate.set(Calendar.YEAR, (today.get(Calendar.YEAR) - YEAR_OFFSET));
        startDate.set(Calendar.MONTH, (today.get(Calendar.MONTH) - MONTH_OFFSET));
        startDate.set(Calendar.DAY_OF_MONTH, (today.get(Calendar.DAY_OF_MONTH) - DATE_OFFSET));

        String urlString = createStockDataUrl(ticker, startDate, today);

        //System.out.println(urlString);

        try {

        	URL u = new URL(urlString);

        	InputStream in = u.openStream();
        	LineNumberReader read = new LineNumberReader(new InputStreamReader(in));

        	String nextLine = read.readLine();
        	while (nextLine != null) {

        		s.push(nextLine);
        		numberOfTradingDates++;

        		nextLine = read.readLine();

        	}   // while

        	numberOfTradingDates--;   // this must be deducted by one to avoid the line that just has titles.

        		// initialize the variable arrays that this information is going to be loaded into.
        	opens = new float[numberOfTradingDates];
        	closes = new float[numberOfTradingDates];
        	highs = new float[numberOfTradingDates];
        	lows = new float[numberOfTradingDates];
        	volumes = new long[numberOfTradingDates];
        	dates = new Calendar[numberOfTradingDates];

        	int lineCount = 0;

        		// this data has to be in most-recent-day-first order, so we create an Enumeration and
        		// pull each entry out and parse it out.

        	Enumeration e = s.elements();

        	while (e.hasMoreElements()) {

        		String t = (String)e.nextElement();

        		StringTokenizer tokens = new StringTokenizer(t, ",");

        		String dateString = tokens.nextToken();

                   // this will avoid the final line, which is just the dates.
        		if (!(dateString.startsWith("Date")))
        		{
        			try {

        			dates[lineCount] = parseDateFromYahoo(dateString);

        			}	// try
        			catch (RuntimeException exception) {

        				exception.printStackTrace();
        				System.out.println("Failure was on stock " + ticker);
        				System.out.println(t);
        				System.exit(0);

        			}	// catch

        			opens[lineCount] = Float.parseFloat(tokens.nextToken()); // open price
        			highs[lineCount] = Float.parseFloat(tokens.nextToken()); // high price
        			lows[lineCount] = Float.parseFloat(tokens.nextToken()); // low price
        			closes[lineCount] = Float.parseFloat(tokens.nextToken()); // get the close price
        			volumes[lineCount] = Long.parseLong(tokens.nextToken());    // get the volume

        			lineCount++;
        		}       // if

        	}   // while there are still lines to be processed.

        	sd.setOpens(opens);
            sd.setCloses(closes);
            sd.setHighs(highs);
            sd.setLows(lows);
            sd.setVolumes(volumes);

            sd.setNumTradingDates(lineCount);
            sd.setTicker(ticker);
            sd.setDates(dates);


        }   // try
        catch (FileNotFoundException e) {
        	throw e;
        }
        catch (Exception e) {
        	System.out.println("Exception thrown in import of stock data.");
        	e.printStackTrace();

        	sd = null;

        }   // catch

        StockCentral.debugOutput("Yahoo! stock data pull for stock " + ticker + " complete!");

		return sd;

	}	// pullStockPriceHistoricalData(String)

	public String[] pullListOfAvailableStocks() {

		String[] toReturn = null;

		ArrayList<String> nyseTickers = pullNYSEStockTickers();

		ArrayList<String> allTickers = new ArrayList<String>(nyseTickers);

		for (int i = 0; i < STOCK_INDICES_AND_HOLDERS.length; i++)
			allTickers.add(STOCK_INDICES_AND_HOLDERS[i]);

		toReturn = new String[allTickers.size()];
		toReturn = allTickers.toArray(toReturn);

		return toReturn;

	}	// pullListOfAvailableStocks

	// static methods

	/**
	 * This method creates the stock data URL for a given stock and date range and then returns it.
	 *
	 * @param ticker
	 * @param startDate
	 * @param today
	 * @return
	 */
    private static String createStockDataUrl(String ticker, Calendar startDate, Calendar endDate)
    {

        StringBuffer toReturn = new StringBuffer(STOCK_DATA_OPEN);

        toReturn.append("s=" + ticker + "&a=" + (startDate.get(Calendar.MONTH)) + "&b=" +
        		startDate.get(Calendar.DAY_OF_MONTH) + "&c=" + (startDate.get(Calendar.YEAR)) +
        		"&d=" + (endDate.get(Calendar.MONTH) + 1) + "&e=" + endDate.get(Calendar.DAY_OF_MONTH) +
        		"&f=" + (endDate.get(Calendar.YEAR)) + "&ignore=.csv");

        StockCentral.debugOutput("Stock Data URL created:  " + toReturn.toString());

        return toReturn.toString();

    }   // createStockDataUrl

    /**
     * This is a static method that pulls the date from the Yahoo coding.
     */
    private static Calendar parseDateFromYahoo(String dateString)
    {
        Calendar toReturn = null;
        Calendar c = Calendar.getInstance();

        StringTokenizer tokens = new StringTokenizer (dateString, "-");

        	// sometimes, apparently, the date string starts with the year, and then the month number,
        	// and then the date.  this fixes that problem by adding a bit of flexibility.
        if (dateString.startsWith("20")) {

        	int year = Integer.parseInt(tokens.nextToken());
        	int month = Integer.parseInt(tokens.nextToken());
        	month--;
        	int date = Integer.parseInt(tokens.nextToken());

        	c.set(year, month, date);

        	toReturn = c;

        }	// if
        else {

        	int date = Integer.parseInt(tokens.nextToken());
        	int month = Integer.parseInt(tokens.nextToken());//parseMonthNumber(tokens.nextToken());
        	int year = Integer.parseInt(tokens.nextToken());
        	year += 2000;

        	c.set(year, month, date);

        	toReturn = c;

        }

        return toReturn;

    }   // parseDateFromYahoo

    /**
     * This is a static method that parses out the number of the month from the date.
     */
    private static byte parseMonthNumber(String dateString)
    {
        byte toReturn;

        for (toReturn = 0; !(dateString.startsWith(MONTH_NAMES[toReturn])); toReturn++);

        return toReturn;

    }   // parseMonthNumber

    private static final String NYSE_TICKERS_URL =
    	"http://www.nysedata.com/nysedata/asp/download.asp?s=txt&prod=Symbols";

    private static ArrayList<String> pullNYSEStockTickers() {

    	ArrayList<String> toReturn = new ArrayList<String>();

        try {
        	URL u = new URL(NYSE_TICKERS_URL);

        	InputStream in = u.openStream();
        	LineNumberReader read = new LineNumberReader(new InputStreamReader(in));

        	StringTokenizer tokensIn = null;
        	String nextTicker = null;
        	String nextLine = read.readLine();
        	while (nextLine != null) {

        		tokensIn = new StringTokenizer(nextLine, "|");

        		nextTicker = tokensIn.nextToken();

        		if (!nextTicker.contains(" "))
        			toReturn.add(nextTicker);

        		nextLine = read.readLine();
        	}	// while

        }   // try
        catch (IOException e) {
        	e.printStackTrace();
        }	// catch

    	return toReturn;

    }	// pullNYSEStockTickers

    private static final String AMEX_TICKERS_URL =
    	"http://www.amex.com/equities/dataDwn/EQUITY_EODLIST_01MAR2007.csv";

    private static ArrayList<String> pullAMEXStockTickers() {

    	ArrayList<String> toReturn = new ArrayList<String>();

        try {
        	URL u = new URL(AMEX_TICKERS_URL);

        	InputStream in = u.openStream();
        	LineNumberReader read = new LineNumberReader(new InputStreamReader(in));

        	StringTokenizer tokensIn = null;
        	String nextTicker = null;
        	String duds, nextLine = read.readLine();

        		// we have to skip the first line, which is just a header line.
        	nextLine = read.readLine();
        	while (nextLine != null) {

        		tokensIn = new StringTokenizer(nextLine, ",\"");

        			// what we want is the very last token.
        		while (tokensIn.hasMoreElements())
        			nextTicker = tokensIn.nextToken();

        		toReturn.add(nextTicker);

        		nextLine = read.readLine();
        	}	// while

        }   // try
        catch (IOException e) {
        	e.printStackTrace();
        }	// catch

    	return toReturn;


    }	// pullAMEXStockTickers

    private static final String NASDAQ_TICKERS_URL =
    	"http://download.finance.yahoo.com/d/quotes.csv?s=@%5EIXIC&f=sl1d1t1c1ohgv&e=.csv&h=";
    private static final int NUMBER_OF_NASDAQ_TICKERS = 3125;

    private static ArrayList<String> pullNasdaqStockTickers() {

   	ArrayList<String> toReturn = new ArrayList<String>();

        try {

        	URL u;
        	InputStream in;
        	LineNumberReader read;
        	StringTokenizer tokensIn;
        	String url, nextTicker, nextLine;

        		// we have to pull off the tickers for the NASDAQ fifty at a time.  and
        		// for reasons that i don't understand the spreadsheets has the first line
        		// that repeats and is ALWAYS the first ticker in ^IXIC.  so that doesn't
        		// work and has to be skipped.
        	for (int i = 0; i < NUMBER_OF_NASDAQ_TICKERS; i += 50) {

        		u = new URL(NASDAQ_TICKERS_URL + i);
            	in = u.openStream();
            	read = new LineNumberReader(new InputStreamReader(in));

            		// skip the first line.
            	read.readLine();
            	nextLine = read.readLine();

            	while (nextLine != null) {

            		tokensIn = new StringTokenizer(nextLine, ",\"");
            			// the ticker is the first element of each line, and we just throw
            			// the rest away.
            		if (tokensIn.hasMoreElements()) {
            			nextTicker = tokensIn.nextToken();

            			toReturn.add(nextTicker);

            			//System.out.print(nextTicker + ", ");

            			nextLine = read.readLine();
            		}	// if
            		else
            			nextLine = null;

            	}	// while

        	}	// for


        }   // try
        catch (IOException e) {
        	e.printStackTrace();
        }	// catch

    	return toReturn;


    }	// pullNasdaqStockTickers()

    public static final void main(String[] args) {

    	ArrayList<String> nyseTickers = pullNYSEStockTickers();

    	ArrayList<String> amexTickers = pullAMEXStockTickers();

    	ArrayList<String> nasdaqTickers = pullNasdaqStockTickers();

    	ArrayList<String> allTickers = nyseTickers;
    	allTickers.addAll(amexTickers);
    	allTickers.addAll(nasdaqTickers);

    	String[] tickers = new String[allTickers.size()];

    	tickers = allTickers.toArray(tickers);

    	for (int i = 0; i < tickers.length; i++)
    		System.out.print(tickers[i] + ", ");

    }	// main


    private static final String YAHOO_URL_PRE = "http://download.finance.yahoo.com/d/quotes.csv?s=";
    private static final String YAHOO_URL_POST = "&f=sl1d1t1c1ohgv&e=.csv";


    public StockData pullTodaysData (String ticker) {

    	StockData toReturn = new StockData();

    	float[] close = new float[1], open = new float[1], high = new float[1],
    		low = new float[1];
    	long[] volume = new long[1];
    	Calendar[] date = new Calendar[1];

    		// The first thing we must do is construct a URL and open it so that
    		// we can pull out our data.
    	String url = YAHOO_URL_PRE + ticker + YAHOO_URL_POST;

    	try {

    		URL u = new URL(url);

    		InputStream in = u.openStream();

    			// Now, we need to load the data up.  There will only be one
    			// line of data, so it won't take long.  The data is in the
    			// following order:  Ticker, Current Price (delayed), Date,
    			// Time, Change, Open, High, Low, Volume.
    		LineNumberReader read = new LineNumberReader(new InputStreamReader(in));

    		StringTokenizer tokens = new StringTokenizer(read.readLine(), ",", false);

    			// Skip the first token (the ticker, which we already know).
    		tokens.nextToken();

    			// Now, pull out the close and the date.
    		close[0] = Float.parseFloat(tokens.nextToken());

    		try {

    			date[0] = parseDateFromYahoo(tokens.nextToken());


    		}
    		catch (RuntimeException e) {

    			e.printStackTrace();

    		}

    			// Now, skip the change and pull out the open, high, low, and volume.
    		tokens.nextToken();
    		open[0] = Float.parseFloat(tokens.nextToken());
    		high[0] = Float.parseFloat(tokens.nextToken());
    		low[0] = Float.parseFloat(tokens.nextToken());
    		volume[0] = Long.parseLong(tokens.nextToken());


    	}	// try
    	catch (Exception e) {

    		System.out.println("Exception thrown when pulling data for stock " + ticker + ".");
    		e.printStackTrace();

    	}

    		// Now, we just need to add this data to the toReturn object
    		// and it will be ready to go.
    	toReturn.setOpens(open);
    	toReturn.setCloses(close);
    	toReturn.setHighs(high);
    	toReturn.setLows(low);
    	toReturn.setVolumes(volume);

    	toReturn.setDates(date);
    	toReturn.setTicker(ticker);
    	toReturn.setNumTradingDates(1);

    	return toReturn;

    }	// pullTodaysData

}	// class YahooStockDataGrabber


