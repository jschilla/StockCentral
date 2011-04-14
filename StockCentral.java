
package stockcentral;

import java.io.*;
import java.util.*;
import webcab.lib.finance.trading.indicators.*;

/**
 * This class provides the backbone for the stock analysis program.  It starts the system going and
 * provides a number of utility methods that can be used.  Each class in this package is passed an instance of
 * the StockCentral class so that it can invoke these utility methods.  A reference to the StockCentral
 * class can also be obtained by calling the StockCentral.getStockCentral() method, which is static.
 *
 * @author Jack Schillaci
 * @version Build 1/15/2007
 *
 */
public class StockCentral implements StockCentralConstants {

	// static class variables.

		// the main method creates this variable, and then it can be accessed in the accessor method.
	private static StockCentral m_stockCentral;

	private static PrintWriter m_debugOutput;
	private static PrintWriter m_dataOutput;

		// these static variables are used to invoke the stock oscillator methods.
	private static Runtime m_runtime;
	private static Oscillators m_oscillators;
	private static MovingAverage m_movingAverages;
	private static MarketStrength m_marketStrength;
	private static Aroon m_aroon;
	private static Momentum m_momentum;

	// static accessor methods

	public static StockCentral getStockCentral() { return m_stockCentral; }
	public static Aroon getAroon() { return m_aroon; }
	public static MovingAverage getMovingAverage() { return m_movingAverages; }

	// instance variables

	private transient StockDataGrabber m_stockDataGrabber;

	private transient StockData[] m_stockData;

	private String[] m_tickers = null;

	// instance accessor methods

	// constructors

	/**
	 * This constructor creates all of the debug output streams and readies all of the
	 * oscillator data objects.
	 */
	public StockCentral() {

			// first, instantiate the debug and data output streams.
		if (DEBUG_ON) {

			String debugFileName = DEBUG_FILENAME_PREFIX + System.currentTimeMillis() + ".txt";

			m_debugOutput = StockCentral.createOutputFile(debugFileName, "logfiles");

		}	// if the debug is on

		if (DATA_MONITORING_ON) {

			String dataFileName = DATA_MONITOR_FILENAME_PREFIX + System.currentTimeMillis() + ".txt";

			m_dataOutput = StockCentral.createOutputFile(dataFileName, "logfiles");

		}

		debugOutput("Debug Output File for " + System.currentTimeMillis());
		dataMonitorOutput("Data Monitor Output File for " + System.currentTimeMillis());

		m_oscillators = new Oscillators();
		m_movingAverages = new MovingAverage();
		m_marketStrength = new MarketStrength();
		m_aroon = new Aroon();
		m_momentum = new Momentum();

		systemStatus();

	}	// ctor()

	private char[] WORKING_SYMBOLS = { '\\', '|', '/', '-' };

	/**
	 * This constructor loads all the data up from the files and creates and saves charts and also saves the
	 * data out into a file.
	 *
	 * @param s
	 */
/*	public StockCentral(String s) {

		this();

		System.out.println("Loading stock central info and stock data.");

		StockCentralInfo info = null;
		StockMonitorList list = new StockMonitorList();
		File dir = null;

		try {

			dir = new File(STOCKDATA_DIRECTORY);

			if ((dir.exists()) && (dir.isDirectory())) {

				ObjectInputStream in = new ObjectInputStream (new FileInputStream (new File(dir,
						STOCK_CENTRAL_INFO_FILENAME)));

				info = (StockCentralInfo)in.readObject();
				m_tickers = info.getGoodStockTickers();

			}	// if

		}	// try
		catch (IOException e) {

			System.out.println("Error loading stock file.");
			System.exit(0);

		}	// catch
		catch (ClassNotFoundException e) {

			System.out.println("Can't find a class.");
			e.printStackTrace();
			System.exit(0);

		}	// catch ClassNotFoundException

			// then cycle through and load the stock files that have been deemed "good"
			// and print out a but of information into the debug file.
		try {

			//File chartDirectory = new File(CHARTFILE_DIRECTORY);

			File resultsFile = new File(STOCK_RESULTS_FILE + System.currentTimeMillis() +
					STOCK_RESULTS_EXTENSION);

			PrintWriter resultsOut = new PrintWriter(resultsFile);
			resultsOut.println("Stock Ticker,Last Close,Last Candlestick Pattern,Prediction,Yahoo URL");

			for (String ticker : m_tickers) {

				System.out.print("Loading stock data for stock ");
				System.out.print(ticker);

				StringBuffer stockFileName = new StringBuffer(SAVEFILE_PREFIX);
				stockFileName.append(ticker);
				stockFileName.append(SAVEFILE_SUFFIX);

				System.out.print(".");

				File stockFile = new File(dir, stockFileName.toString());

					// make sure the file is actually there, then load it up.
				if (stockFile.exists()) {

					ObjectInputStream in = new ObjectInputStream (new FileInputStream
							(new File (dir, stockFileName.toString())));

					StockData sd = (StockData)in.readObject();

					sd.dataMonitor();

					list.addStock(new StockToMonitor(sd.getTicker(), sd.getLastClose(),
							sd.getCandlestickPattern(),
							sd.getRecentPrediction().getTrend()));

					StringBuffer sb = new StringBuffer();
					sb.append(sd.getTicker());
					sb.append(',');
					sb.append(sd.getLastClose());
					sb.append(',');
					CandlestickPattern csp = sd.getCandlestickPattern();
					sb.append(CandlestickPattern.getPatternDescription(csp));
					sb.append(',');
					sb.append(StockTrend.getTrendDescription(sd.getRecentPrediction().getTrend()));
					sb.append(',');
					sb.append(sd.generateYahooChartUrl(sd.getTicker()));

					resultsOut.println(sb.toString());

					System.out.println("Done!");

				}	// if
				else {

					System.out.println("No data file!");

				}	// else

			}	// for

			resultsOut.close();

			File monitorListFile = new File(STOCK_MONITOR_FILE);
			ObjectOutputStream monitorListOut = new ObjectOutputStream(
					new FileOutputStream(monitorListFile));
			monitorListOut.writeObject(list);
			monitorListOut.close();

			list.dataMonitor();

		}	// try
		catch (Exception e) {

			e.printStackTrace();
			System.exit(0);

		}

		System.out.println("Stock load complete!");

		systemStatus();

		destructor();

	}	// ctor(String)

	// finalize() and destructor() methods

	protected void finalize() {
		destructor();
	}	// finalize
*/

	/**
	 * This method just closes the open data streams and does any other final clean up that needs to be done.
	 *
	 */
	public void destructor() {

		m_debugOutput.flush();
		m_debugOutput.close();
		m_debugOutput = null;

		m_dataOutput.flush();
		m_dataOutput.close();
		m_dataOutput = null;

	}	// destructor

	// static main method.

	/**
	 * @param args Whatever arguments are passed when main() is invoked.  This should usually be nothing.
	 */
	public static void main(String[] args) {

		//m_stockCentral = new StockCentral("Shit!");

	}

	// static utility methods

	/**
	 * This method writes the designated text out to the debug output stream, but only if we're tracking debug info.
	 */
	public static void debugOutput(String debugText) {
		if ((m_debugOutput != null) && (DEBUG_ON))
			m_debugOutput.println(debugText);
	}	// debugOutput(String)

	/**
	 * This method writes the designated text out to the data output stream, but only if we're actually tracking this.
	 */
	public static void dataMonitorOutput(String dataText) {

		if ((m_dataOutput != null) && (DATA_MONITORING_ON))
			m_dataOutput.println(dataText);

	}	// dataMonitorOutput

	/**
	 * This method sends information about the total VM memory and the currently available
	 * memory out to the debug output stream.
	 */
	public static void systemStatus() {

		if (m_runtime == null)
			m_runtime = Runtime.getRuntime();

		debugOutput("Total VM Memory:  " + m_runtime.totalMemory());
		debugOutput("Currently Available VM Memory:  " + m_runtime.freeMemory());

	}	// systemStatus

	public static float calculateAvgDailyVolatility (float[] opens, float[] closes) {

		float toReturn;
		double totalDifference = 0;

		for (int i = 0; i < opens.length; i++)
			totalDifference += (Math.abs(opens[i] - closes[i]));

		toReturn = (float)(totalDifference / opens.length);

		return toReturn;

	}	// calculateAvgDailyVolatility

	/**
	 * This method calls the m_oscillator to calculate the relative strength index for the set of close prices
	 * that it is passed.
	 */
	public static float[] calculateRSI(float[] closes) {

		return calculateRSI(closes, DEFAULT_RSI_PERIODS);

	}	// calculateRSI

	/**
	 * This method calls the m_oscillator to calculate the relative strength index for the set of close prices
	 * that it is passed.
	 * Because I couldn't find a good commercially available RSI calculator, I had to do it myself.
	 */
	public static float[] calculateRSI(float[] closes, int numPeriods) {

		float[] rsiData = new float[closes.length];

		float avgGains = 0, avgLosses = 0;

		float rs = 0, rsi = 0;

		float totalInitialGains = 0, totalInitialLosses = 0;

		rsiData[closes.length - 1] = 50;

			// First, we need to determine the initial average losses and average gains during
			// the number of periods.
		for (int countInitial = 2; countInitial <= numPeriods; countInitial++) {

			rsiData[rsiData.length - countInitial] = 50;	// 50 is our default RSI number

				// first, we gotta calculate the difference between the current day and the day before.
			//float oneDayDelta = closes[countInitial] - closes[countInitial + 1];

				// if today's close is higher than yesterday's, then we add the difference to the
				// totalInitialGains
			if (closes[countInitial] > closes[countInitial + 1]) {

				totalInitialGains += (closes[countInitial] - closes[countInitial + 1]);

			}	// if

				// if today's close is lower, we add the difference (expressed as a positive number
				// to the totalInitialLosses.
			else {

				totalInitialLosses += (closes[countInitial + 1] - closes[countInitial]);

			}

		}	// cycle through the first periods.

			// Now that we have the total gains and losses during the initial period, we can calculate
			// the first averages.
		if (totalInitialGains != 0)
			avgGains = totalInitialGains / numPeriods;
		else
			avgGains = 0;

		if (totalInitialLosses != 0)
			avgLosses = totalInitialLosses / numPeriods;
		else
			avgLosses = 0;


			// Now, we have to calculate the first RS and RSI
		if (avgLosses != 0) {
			rs = avgGains / avgLosses;
			rsi = 100 - (100 / (1 + rs));
		}
		else
			rsi = 100;

		rsiData[rsiData.length - (numPeriods + 1)] = rsi;
										// this is plus 1 because it needs to take into account the
										// first empty day and the (n-1) number of the days in the initial
										// period.

			// Now, we need to cycle through each day that remains and calculate the RS and the RSI
			// along with the averages.
		for (int countRemaining = numPeriods + 2; countRemaining <= rsiData.length; countRemaining++) {

			//if (countRemaining == rsiData.length)
				//System.out.println("Got here");

				// First, we'll calculate the new averages.
			float oldAvgGains = avgGains;
			float oldAvgLosses = avgLosses;

			float dailyGain = 0, dailyLoss = 0;
			//float dailyDelta = closes[rsiData.length - countRemaining] -
				//closes[rsiData.length - (countRemaining - 1)];

				// if this day's close is higher than yesterday's, then we have a gain.
			if (closes[rsiData.length - countRemaining] > closes[rsiData.length - (countRemaining - 1)]) {

				dailyGain = closes[rsiData.length - countRemaining] -
					closes[rsiData.length - (countRemaining - 1)];

				//avgGains = ((oldAvgGains * (numPeriods - 1)) + dailyDelta) / numPeriods;

			}
				// if this day's close is lower, then we have a loss.
			else {

				dailyLoss = closes[rsiData.length - (countRemaining - 1)] -
					closes[rsiData.length - countRemaining];

				//avgLosses = ((oldAvgLosses * (numPeriods - 1)) + Math.abs(dailyDelta)) / numPeriods;
			}

				// Next, we need to recalculate the averages.
			avgGains = ((oldAvgGains * (numPeriods - 1)) + dailyGain) / numPeriods;
			avgLosses = ((oldAvgLosses * (numPeriods - 1)) + dailyLoss) / numPeriods;

			if (avgLosses != 0) {
				rs = avgGains / avgLosses;
				rsi = 100 - (100 / (1 + rs));
			}
			else
				rsi = 100;

			rsiData[rsiData.length - countRemaining] = rsi;

		}	// cycle through everything else.

		return rsiData;
/*
		double[] toReturn = null;

		try {

			double[] flippedAroundCloses = switchArrayAround(arrayConvertFloatToDouble(closes));

			double[] rsisWithoutLeadingZeros =
				m_oscillators.relativeStrengthIndex(flippedAroundCloses, numPeriods);

			double[] flippedAroundRSIs = rsisWithoutLeadingZeros;

			toReturn = fillInBlankArrays(flippedAroundRSIs, closes.length, 50.0);

		}	// try
		catch (IllegalArgumentException e) {

			debugOutput("Illegal argument in attempting to calculate RSI.");

		}	// catch

		debugOutput("Calculated " + toReturn.length + " RSI elements from " + closes.length + " close prices.");

		return arrayConvertDoubleToFloat(toReturn);
*/
	}	// calculateRSI (float[], int)

	/**
	 * This method invokes the moving averages class to calculate an exponential moving average for the
	 * passed closing prices.  NOTE:  THE METHOD THIS INVOKES SEEM TO GET THE NUMBERS WRONG OR SOMETHING.
	 * FURTHER INVESTIGATION IS WARRANTED INTO THE tTH THING.
	 * @param closes
	 * @param period
	 * @return
	 */
/*	public static float[] calculateEMA(float[] closes, int period) {

		double smoothingFactor = 2.0f / (1.0f + period);

		smoothingFactor = 1 - smoothingFactor;

		return calculateEMA(closes, smoothingFactor, period);

	}	// calculateEMA(float[], int)

	public static float[] calculateEMA(float[] closes, double smoothingFactor, int period) {

		float[] toReturn = null;
		double[] toReturnDubs = null;

		try {

			double[] emaWithoutLeadingZeros =
				m_movingAverages.exponentiallyWeightedMovingAverage(arrayConvertFloatToDouble(closes),
					smoothingFactor, period);

			toReturnDubs = fillInBlankArrays(emaWithoutLeadingZeros, closes.length, 0.0);

			debugOutput("Calculated " + toReturnDubs.length + " EMA elements from " +
					closes.length + " close prices.");

			toReturn = arrayConvertDoubleToFloat(toReturnDubs);

		}
		catch (IllegalArgumentException e) {

			debugOutput("Illegal argument in attempting to calculate EMA.");

			toReturn = new float[closes.length];

		}	// catch

		return toReturn;

	}	// calculateEMA(float[], double, int)
*/
	/**
	 * This method calls the MovingAverage oscillator method to calculate a simple moving
	 * average for the passed float array for passed period.
	 * @param closes
	 * @param period
	 * @return
	 */
	public static float[] calculateSMA(float[] closes, int period) {

		double[] toReturn = null;

		try {

			double[] emaWithoutLeadingZeros = m_movingAverages.simpleMovingAverage(arrayConvertFloatToDouble(closes),
					period);

			toReturn = fillInBlankArrays(emaWithoutLeadingZeros, closes.length, 0.0);

		}
		catch (IllegalArgumentException e) {

			debugOutput("Illegal argument in attempting to calculate SMA.  Fewer closes than the length of the SMA.");

			toReturn = new double[closes.length];

		}	// catch

		debugOutput("Calculated " + toReturn.length + " SMA elements from " +
				closes.length + " close prices.");

		return arrayConvertDoubleToFloat(toReturn);

	}	// calculateSMA(double[], int)

	/**
	 * This method calculates an array of balance of power calculations, which tells you whether the
	 * bulls or the bears are in control of each day's session.
	 * @param opens An array of floats representing the open prices for the stock.
	 * @param closes An array of floats representing the close prices for the stock.
	 * @param highs An array of floats representing the highs for the stock.
	 * @param lows An array of floats representing th elows of the stock.
	 * @return
	 */
	public static float[] calculateBalanceOfPowerOnDay(float[] opens, float[] closes,
			float[] highs, float[] lows) {

		double[] toReturn = new double[opens.length];

		try {

			toReturn = m_marketStrength.balanceOfPowerOverPeriod(arrayConvertFloatToDouble(opens),
					arrayConvertFloatToDouble(closes),
					arrayConvertFloatToDouble(highs),
					arrayConvertFloatToDouble(lows));

		}	// try
		catch (IllegalArgumentException e) {

			debugOutput("Illegal argument in attempting to calculate balance of power.");

		}	// catch

		return arrayConvertDoubleToFloat(toReturn);

	}

	/**
	 * This method calculates the Aroon(up) line, which measures the number of days since the last high in the range.
	 * @param closes
	 * @return
	 */
	public static float[] calculateAroonUp (float[] highs) {

		double[] toReturn = null;

		try {

			toReturn = m_aroon.aroonUpOverPeriod(arrayConvertFloatToDouble(highs), DEFAULT_AROON_PERIOD);

		}	// try
		catch (IllegalArgumentException e) {

			debugOutput("Illegal argument in attempting to calculate Aroon(up).");

		}	// catch

		return arrayConvertDoubleToFloat(toReturn);


	}

	/**
	 * This method calculates the Aroon(up) line, which measures the number of days since the last high in the range.
	 * @param closes
	 * @return
	 */
	public static float[] calculateAroonDown (float[] lows) {

		double[] toReturn = null;

		try {

			toReturn = m_aroon.aroonDownOverPeriod(arrayConvertFloatToDouble(lows), DEFAULT_AROON_PERIOD);

		}	// try
		catch (IllegalArgumentException e) {

			debugOutput("Illegal argument in attempting to calculate Aroon(up).");

		}	// catch

		return arrayConvertDoubleToFloat(toReturn);

	}	// calculateAroonDown(double[])

	/**
	 * This method returns the lowest of the passed array.
	 * @param lows
	 * @return
	 */
	public static float getLowest(float[] lows) {

		return (float)m_momentum.lowest(arrayConvertFloatToDouble(lows), lows.length);

	}	// getLowest (double[])

	/**
	 * This method returns the highest of the passed array.
	 * @param highs
	 * @return
	 */
	public static float getHighest(float[] highs) {

		return (float)m_momentum.highest(arrayConvertFloatToDouble(highs), highs.length);

	}	// getHighest (double[])

	/**
	 * This method converts an array of doubles into an array of float by down-casting it.
	 * @param dbl
	 * @return
	 */
	public static float[] arrayConvertDoubleToFloat(double[] dbl) {

		float[] toReturn = new float[dbl.length];

		for (int i = 0; i < dbl.length; i++) {

			toReturn[i] = (float) dbl[i];

		}	// for

		return toReturn;

	}	// arrayConvertDoubleToFloat(double[])

	/**
	 * Thid method converts an array of floats into an array of doubles by up-casting it.
	 * @param fl
	 * @return
	 */
	public static double[] arrayConvertFloatToDouble(float[] fl) {

		double[] toReturn = new double[fl.length];

		for (int i = 0; i < fl.length; i++) {

			toReturn[i] = fl[i];

		}	// for

		return toReturn;

	}	// arrayConvertFloatToDouble (float[])

	public static int findLastFromArrayBelow(float[] array, float below) {

		int toReturn = 0;
		float valFromArray = 0.0f;

		try {
			do {

				valFromArray = array[toReturn];

				toReturn++;

			}	// do
			while (valFromArray >= below);
		}	// try
		catch (ArrayIndexOutOfBoundsException e) {}

		return toReturn;

	}	// calculateLastFromArrayBelow(float[], float)

	public static double[] switchArrayAround(double[] toSwitch) {

		double[]toReturn = new double[toSwitch.length];

		for (int i =0; i < toSwitch.length; i++)
			toReturn[i] = toSwitch[toSwitch.length - (i + 1)];

		return toReturn;

	}	// switchArrayAround (double[])

	/**
	 * This method subsets the passed array from the startIndex to the sizeToSubset, and
	 * returns the result.
	 * @param startIndex
	 * @param sizeToSubset
	 * @param array
	 * @return
	 */
	public static float[] subsetArray(int startIndex, int sizeToSubset, float[] array) {

		float[] toReturn = new float[sizeToSubset];

		for (int i = 0; i < sizeToSubset; i++) {

			toReturn[i] = array[(i + startIndex)];

		}	// for

		return toReturn;

	}	// subsetArray (int, int, double[])

	public static float[] subsetArray(int sizeToSubset, float[] array) {

		return subsetArray(0, sizeToSubset, array);

	}	// subsetArray(int, double[])

	public static long[] subsetArray(int sizeToSubset, long[] array) {

		return subsetArray(0, sizeToSubset, array);

	}	// subsetArray(int, long[])

	public static long[] subsetArray(int startIndex, int sizeToSubset, long[] array) {

		long[] toReturn = new long[sizeToSubset];

		for (int i = 0; i < sizeToSubset; i++) {

			toReturn[i] = array[(i + startIndex)];

		}	// for

		return toReturn;

	}	// subsetArray (int, int, long[])

	public static Date[] subsetArray(int sizeToSubset, Date[] array) {

		return subsetArray(0, sizeToSubset, array);

	}	// subsetArray(int, MiniDate[])

	public static Date[] subsetArray(int startIndex, int sizeToSubset, Date[] array) {

		Date[] toReturn = new Date[sizeToSubset];

		for (int i = 0; i < sizeToSubset; i++) {

			toReturn[i] = array[(i + startIndex)];

		}	// for

		return toReturn;

	}	// subsetArray (int, int, double[])

	public static float calculateDelta(float start, float end) {

		float toReturn = (end - start) / start;

		return toReturn;

	}	// calculateDelta

	public static Calendar getCalendarForNow() {

		Calendar toReturn = new GregorianCalendar();

		return toReturn;

	}	// getCalendarForNow

	public static void serializeObject (String fileName, String directory, Serializable object) {

		try {

			File objectOutputFile;

				// if a directory name was passed, then we have to make sure that the directory either
				// exists or we will create it, then we create a File object in that directory.
			if (directory != null){

				File objectOutputDirectory = new File (directory);

				if (!objectOutputDirectory.exists())
					objectOutputDirectory.mkdir();

				objectOutputFile = new File(objectOutputDirectory, fileName);

			}	// if there is a directory

				// if there is no directory passed, then we just open the file in the current directory.
			else {

				objectOutputFile = new File(fileName);

			}	// if there is no directory.

				// Next, we open an Object Output Stream with this file name.
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(objectOutputFile));

			out.writeObject(object);

			out.close();

		}	// try
		catch (IOException e) {

			System.out.println("Attempt to save " + fileName + " failed on " +
					new GregorianCalendar().toString() + ".");

		}

	}	// serializeObject

	public static Object deserializeObject (String fileName, String directory) {

		Object toReturn = null;

		try {

			File objectInputFile;

				// if a directory name was passed, then we have to make sure that the directory either
				// exists or we will create it, then we create a File object in that directory.
			if (directory != null){

				File objectInputDirectory = new File (directory);

				if (!objectInputDirectory.exists())
					objectInputDirectory.mkdir();

				objectInputFile = new File(objectInputDirectory, fileName);

			}	// if there is a directory

				// if there is no directory passed, then we just open the file in the current directory.
			else {

				objectInputFile = new File(fileName);

			}	// if there is no directory.

				// Next, we open an Object Output Stream with this file name.
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(objectInputFile));

			toReturn = in.readObject();

			in.close();

		}	// try
		catch (Exception e) {

			System.out.println("Attempt to load " + fileName + " failed on " +
					Calendar.getInstance().toString() + ".");
			e.printStackTrace();

		}

		return toReturn;

	}	// deserializeObject

	public static final PrintWriter createOutputFile (String fileName, String directory) {

		PrintWriter toReturn = null;

		try {

			File outputFile;

				// if a directory name was passed, then we have to make sure that the directory either
				// exists or we will create it, then we create a File object in that directory.
			if (directory != null){

				File outputDirectory = new File (directory);

				if (!outputDirectory.exists())
					outputDirectory.mkdir();

				outputFile = new File(outputDirectory, fileName);

			}	// if there is a directory

				// if there is no directory passed, then we just open the file in the current directory.
			else {

				outputFile = new File(fileName);

			}	// if there is no directory.

			toReturn = new PrintWriter(outputFile);

		}	// try
		catch (IOException e) {

			System.out.println("Unable to create output file " + fileName + ".");

		}

		return toReturn;

	}	// createOutputFile

	public static double[] fillInBlankArrays(double[] data, int lengthToComplete, double defaultData) {

			// We have to add zeros to the very end of the array.
		double[] toReturn = new double[lengthToComplete];

			// we need to copy the RSI numbers over, and then ...
		for (int count = 0; count < data.length; count++) {

			toReturn[count] = data[count];

		}	// transfer all RSI numbers

		// ... we have to put default numbers in the remaining spots.
	for (int count = data.length; count < toReturn.length; count++) {

		toReturn[count] = defaultData;

	}	// put default data in

	return toReturn;

	}	// fillInBlankArrays

	public static String generateDateString(Calendar date) {

		String toReturn = Integer.toString((date.get(Calendar.MONTH)) + 1) + "/" +
			Integer.toString(date.get(Calendar.DATE)) + "/" +
			Integer.toString(date.get(Calendar.YEAR));

		return toReturn;

	}

	public static String translateCalendarToString(Calendar date) {

		String toReturn = (date.get(Calendar.MONTH) + 1) + " / " + date.get(Calendar.DATE) + " / " +
			date.get(Calendar.YEAR);

		return toReturn;

	}	// translateCalendarToString

	public static float[] unwrapFloats(Float[] floatsToUnwrap) {

		float[] toReturn = new float[floatsToUnwrap.length];

		for (int count = 0; count < floatsToUnwrap.length; count++)
			toReturn[count] = floatsToUnwrap[count].floatValue();

		return toReturn;

	}	// unwrapFloats

	public static float[] mergeFloatArrays (float[] first, float[] second) {

		float[] toReturn = new float[first.length + second.length];

		for (int countFirst = 0; countFirst < first.length; countFirst++)
			toReturn[countFirst] = first[countFirst];

		for (int countSecond = 0; countSecond < second.length; countSecond++)
			toReturn[countSecond + first.length] = second[countSecond];

		return toReturn;

	}	// mergeFloatArrays

	public static double[] mergeDoubleArrays (double[] first, double[] second) {

		double[] toReturn = new double[first.length + second.length];

		for (int countFirst = 0; countFirst < first.length; countFirst++)
			toReturn[countFirst] = first[countFirst];

		for (int countSecond = 0; countSecond < second.length; countSecond++)
			toReturn[countSecond + first.length] = second[countSecond];

		return toReturn;

	}	// mergeLongArrays

	public static long[] mergeLongArrays (long[] first, long[] second) {

		long[] toReturn = new long[first.length + second.length];

		for (int countFirst = 0; countFirst < first.length; countFirst++)
			toReturn[countFirst] = first[countFirst];

		for (int countSecond = 0; countSecond < second.length; countSecond++)
			toReturn[countSecond + first.length] = second[countSecond];

		return toReturn;

	}	// mergeLongArrays

	public static Calendar[] mergeCalendarArrays (Calendar[] first, Calendar[] second) {

		Calendar[] toReturn = new Calendar[first.length + second.length];

		for (int countFirst = 0; countFirst < first.length; countFirst++)
			toReturn[countFirst] = first[countFirst];

		for (int countSecond = 0; countSecond < second.length; countSecond++)
			toReturn[countSecond + first.length] = second[countSecond];

		return toReturn;

	}	// mergeObjectArrays

	public static double[] calculateEMA(float[] closes, int period) {

		double[] toReturn = new double[closes.length];

			// first, calculate the multiplier.
		double multiplier = (2.0f / (1.0f + period));

		// next, let's fill in the zeros at the beginning of the array.
		for (int countZeros = 1; countZeros < period; countZeros++)
			toReturn[toReturn.length - countZeros] = 0;

			// now, we need to get the first EMA of the array, which will occur in the array at (period - 1).  This is just the same
			// as the SMA.
		double firstEMA = 0;
		for (int countAdditurs = 1; countAdditurs < period + 1; countAdditurs++)
			firstEMA += closes[toReturn.length - countAdditurs];

		firstEMA = firstEMA / period;

		toReturn[closes.length - period] = firstEMA;

			// Now, we need to fill up the rest of the EMAs using the formula
			// EMA = (Close today * multiplier)
			// + (EMA of yesterday * (1 - multiplier))

		for (int countEMAs = closes.length - (period + 1); countEMAs >= 0; countEMAs--) {

			toReturn[countEMAs] = (closes[countEMAs] * multiplier) +
				(toReturn[(countEMAs + 1)] * (1 - multiplier));

				// the alternate code for method 1 is
				// ((closes[countEMAs] - toReturn[(countEMAs - 1)]) * multiplier) + toReturn[(countEMAs - 1)];
				// see http://www.decisionpoint.com/tacourse/MovingAve.html
		}	// for

		return toReturn;

	}

	public static double[] calculateEMA(double[] closes, int period) {

		double[] toReturn = new double[closes.length];

			// first, calculate the multiplier.
		double multiplier = (2.0f / (1.0f + period));

			// next, let's fill in the zeros at the beginning of the array.
		for (int countZeros = 1; countZeros < period; countZeros++)
			toReturn[toReturn.length - countZeros] = 0;

			// now, we need to get the first EMA of the array, which will occur in the array at (period - 1).  This is just the same
			// as the SMA.
		double firstEMA = 0;
		for (int countAdditurs = 1; countAdditurs < period + 1; countAdditurs++)
			firstEMA += closes[toReturn.length - countAdditurs];

		firstEMA = firstEMA / period;

		toReturn[closes.length - period] = firstEMA;

			// Now, we need to fill up the rest of the EMAs using the formula
			// EMA = (Close today * multiplier)
			// + (EMA of yesterday * (1 - multiplier))

		for (int countEMAs = closes.length - (period + 1); countEMAs >= 0; countEMAs--) {

			toReturn[countEMAs] = (closes[countEMAs] * multiplier) +
				(toReturn[(countEMAs + 1)] * (1 - multiplier));

				// the alternate code for method 1 is
				// ((closes[countEMAs] - toReturn[(countEMAs - 1)]) * multiplier) + toReturn[(countEMAs - 1)];
				// see http://www.decisionpoint.com/tacourse/MovingAve.html
		}	// for

		return toReturn;

	}	// calculateEMA

}
