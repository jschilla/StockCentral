/**
 *
 */
package stockcentral;

import java.io.PrintWriter;

/**
 * @author Jack's Dell
 *
 */
public interface BacktestLogStorable {

/*	public static final int MOC_ONE_DAY = 0;
	public static final int MOO_ONE_DAY = 1;
	public static final int MOC_ONE_DAY_NEUTRAL = 2;
	public static final int MOO_ONE_DAY_NEUTRAL = 3;
	public static final int MOO_TWO_DAY = 4;
	public static final int MOO_TWO_DAY_NEUTRAL = 5;
	public static final int MOO_THREE_DAY = 6;
	public static final int MOO_THREE_DAY_NEUTRAL = 7;
	public static final int MOO_ONE_WEEK = 8;
	public static final int MOO_ONE_WEEK_NEUTRAL = 9;
	public static final int STOP_LOSS_RETRACEMENT = 10;
	public static final int PEAK_AT_RETRACEMENT = 11;
	public static final int DAYS_UNTIL_RETRACEMENT = 12;
	public static final int PEAK_UNTIL_RETRACEMENT_TO_SIGNAL = 13;
*/	public static final int MOC_TO_EXIT_SIGNAL = 0;
//	public static final int MOO_TO_EXIT_SIGNAL = 15;
	public static final int DAYS_TO_EXIT_SIGNAL = 1;

	public static final String[] LOG_DATA_NAMES = { /*"MOC One Day", "MOO One Day", "MOC One Day-Neutral",
		"MOO One Day-Neutral", "MOO Two Day", "MOO Two Day-Neutral", "MOO Three Day",
		"MOO Three Day-Neutral", "MOO One Week", "MOO One Week-Neutral", "Next Retracement to Stop Loss",
		"Next Peak at Retracement to Stop Loss", "Number of Days Until Stop Loss",
		"Peak Until Retracement to Signal Price",*/ "MOC to Exit Signal", /*"MOO to Exit Signal",*/
		"Days Until Exit"
	};

	public void outputSummaryDataLine(PrintWriter out);

	public void outputTradeReport(PrintWriter out);

	public int getNumberOfEntries();

	public BacktestLogEntry getAverageOfEntries();

	public BacktestLogEntry getTotalOfEntries();

	public String getStockTicker();

	public String getStrategyName();

}
