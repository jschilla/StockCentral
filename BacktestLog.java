/**
 *
 */
package stockcentral;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author Jack's Dell
 *
 */
public class BacktestLog extends ArrayList<BacktestLogStorable> implements BacktestLogStorable {

	public static final String REPORTS_DIRECTORY = "reports";
	static final String TRADE_REPORTS_DIRECTORY = "reports/trades";

	BacktestLogEntry m_totals, m_averages;
	public BacktestLogEntry getAverages() { return m_averages; };

	private BacktestStrategies m_strategy;
	private int m_strategyId;

	private int m_totalTradingDays = 0;
	public void setTotalTradingDays(int n) { m_totalTradingDays = n; }

	private float m_percentMatches, m_percentAccurate;
	private int m_numMatchEntries = 0;


	public float getPercentMatches() { return m_percentMatches; }
	public float getPercentAccurate() { return m_percentAccurate; }

	public BacktestLog(BacktestStrategies strategy, int strategyId) {

		m_strategy = strategy;
		m_strategyId = strategyId;

		m_totals = new BacktestLogEntry();
		m_averages = new BacktestLogEntry();

	}	// ctor

	public void calculateAverages() {

		calculatePercentMatches();

		m_averages = getAverageOfEntries();

	}

	public void calculatePercentMatches() {

		m_percentMatches = (float)size() / m_totalTradingDays;

	}	// calculatePercentMatches

	public void outputBacktestReports() {

			// First, we'll print out a report summarizing the performance of this strategy.
		String summaryFileName = m_strategy.getStrategyFileNameComponent(m_strategyId) + "_summary_" +
			System.currentTimeMillis() + ".csv";

		PrintWriter summaryOut = StockCentral.createOutputFile(summaryFileName, REPORTS_DIRECTORY);

		summaryOut.println("Summary of backtest for strategy " + m_strategy.getStrategyName(m_strategyId));
		summaryOut.println("Matches:  " + (m_percentMatches * 100) + "%");
		summaryOut.println("Matches are " + size() + " out of " + m_totalTradingDays + " trading days.");
		summaryOut.println();

		summaryOut.println("Time Frame,Percent Successful,Net Movement");
		summaryOut.println();

			// Now, we go through each and every tiem frame and output the summary.
		for (int count = (BacktestLogEntry.LOG_DATA_NAMES.length- 1); count >= 0; count--) {

			summaryOut.print(BacktestLogEntry.LOG_DATA_NAMES[count]);
			summaryOut.print(",");
			summaryOut.print((m_averages.m_accuracyAverages[count] * 100) + "%");
			summaryOut.print(",");
			summaryOut.println((m_averages.m_logData[count] * 100) + "%");

			summaryOut.println();

		}	// cycle through each time frame in the log

		summaryOut.close();

			// Next, we're going to print out the report of each and every trade that matches
			// this.
		String tradesFileName = m_strategy.getStrategyFileNameComponent(m_strategyId) + "_trades_" +
		System.currentTimeMillis() + ".csv";

		PrintWriter tradesOut = StockCentral.createOutputFile(tradesFileName, TRADE_REPORTS_DIRECTORY);

		tradesOut.println("Trades Report for strategy " + m_strategy.getStrategyName(m_strategyId));
		tradesOut.println();

		tradesOut.println("Ticker,Date");

		Iterator<BacktestLogStorable> entries = iterator();

		while (entries.hasNext()) {

			BacktestLogStorable entry = entries.next();

			entry.outputTradeReport(tradesOut);

		}	// cycle through each entry and print it out

		tradesOut.close();

	}	// outputBacktestReport

	public void outputSummaryDataLine(PrintWriter out) {

	}	// outputSummaryDataLine

	public void outputTradeReport(PrintWriter out) {

	}	// outputTradeReport

	public int getNumberOfEntries() { return this.size(); }

	/**
	 * This method first totals and then averages all of the entries and returns it as
	 * a BactestLogEntry object.
	 */
	public BacktestLogEntry getAverageOfEntries() {

		BacktestLogEntry toReturn = new BacktestLogEntry();

			// The difference between this and the totaling is that we have to call the averaging function so that we are getting
			// the right number returned to us.  If we didn't do this, the averaging below would end up screwed up.  For instance,
			// if you had three entries in this array list, one of which was another BacktestLog with three entries, if you added the totals,
			// you'd end up with the total for that log, but would only be dividing it by 3, rather then five.
		Iterator<BacktestLogStorable> entries = iterator();

		int numAccurate = 0;

		while (entries.hasNext()) {

			BacktestLogStorable nextEntry = entries.next();

			toReturn.addEntry(nextEntry.getAverageOfEntries());

			if (nextEntry.getAverageOfEntries().getMocToExit() > 0)
				numAccurate++;

			m_numMatchEntries++;

		}	// while

		if (m_numMatchEntries != 0)
			m_percentAccurate = (float)numAccurate / (float)m_numMatchEntries;
		else
			m_percentAccurate = 0.0f;

		toReturn = toReturn.averageEntry(m_numMatchEntries, m_percentMatches, m_percentAccurate);

		return toReturn;

	}	// getAverageOfEntries

	/**
	 * This method totals all of the entries and returns it as a BacktestLogEntry object.  This ends up recursing all the way
	 * down using the BacktestLogStorable interface.
	 */
	public BacktestLogEntry getTotalOfEntries() {

		BacktestLogEntry toReturn = new BacktestLogEntry();

		Iterator<BacktestLogStorable> entries = iterator();

		while (entries.hasNext()) {

			toReturn.addEntry(entries.next().getTotalOfEntries());

		}	// while

		return toReturn;

	}	// getTotalOfEntries

	public String getStockTicker() { return null; }

	public String getStrategyName() { return null; }

}	// class BacktestLog

