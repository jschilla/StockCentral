/**
 *
 */
package stockcentral;

import java.io.PrintWriter;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * @author Jack's Dell
 *
 */
public class BacktestLogEntry implements BacktestLogStorable {

	float[] m_logData = new float[LOG_DATA_NAMES.length],
		m_accuracyAverages = new float[LOG_DATA_NAMES.length];

	private float m_percentMatches, m_percentAccurate;
	public float getPercentMatches() { return m_percentMatches; }
	public float getPercentAccurate() { return m_percentAccurate; }

	boolean[] m_accuracyLog = new boolean[LOG_DATA_NAMES.length];

	String m_ticker;
	public String getTicker() { return m_ticker; }

	Calendar m_datePositionOpened, m_datePositionClosed;
	public Calendar getPositionOpenDate() { return m_datePositionOpened; }
	public Calendar getPositionCloseDate() { return m_datePositionClosed; }

	private BacktestStrategies m_strategies;
	private int m_strategyId;

	public String getStockTicker() { return m_ticker; }

	public int getNumberOfEntries() { return 1; }

	public float[] getLogEntries() { return m_logData; }
	public float[] getAccuracyAverages() { return m_accuracyAverages; }

	public BacktestLogEntry getAverageOfEntries() { return this; }

	public BacktestLogEntry getTotalOfEntries() { return this; }

	public String getStrategyName() { return m_strategies.getStrategyName(m_strategyId); }

	public void outputTradeReport(PrintWriter out) {

		out.print(m_ticker);
		out.print(",");
		out.print(StockCentral.generateDateString(m_datePositionOpened));
		out.print(",");
		out.print(StockCentral.generateDateString(m_datePositionClosed));
		out.println();

	}	// outputTradesReport

	public static final String SUMMARY_FILE_LEGEND = "Strategy,,Percent Match,,MOC to Exit Signal,MOO to Exit Signal";

	public void outputSummaryDataLine(PrintWriter out) {

			/// THIS NEEDS TO BE FIXED!!!  I think this is it.  - JLS 4/27

		out.print(m_strategies.getStrategyName(m_strategyId));
		out.print(",,");
		out.print((m_percentMatches * 100) + "%");
		out.print(',');
		out.print(m_logData[MOC_TO_EXIT_SIGNAL] + "%");
/*		out.print(',');
		out.println(m_logData[MOO_TO_EXIT_SIGNAL] + "%");
*/
	}	// outputSummaryDataLine

	public BacktestLogEntry(StockData sd, StockData index, BacktestStrategies strategies,
			int strategyId, int lookback) {

		m_strategies = strategies;
		m_strategyId = strategyId;

		boolean isBullish = strategies.isStrategyBullish(strategyId);
		float trailingStop = strategies.getTrailingStop(strategyId);

		m_datePositionOpened = new GregorianCalendar();
		m_datePositionOpened.setTimeInMillis((sd.getDates()[lookback].getTimeInMillis()));
		m_datePositionClosed = new GregorianCalendar();

		m_ticker = sd.getTicker();

		float[] closes = sd.getCloses();
		float[] opens = sd.getOpens();
		float[] highs = sd.getHighs();
		float[] lows = sd.getLows();
		float[] mktCloses = index.getCloses();
		float[] mktOpens = index.getOpens();

			// First, we need to set everything to zero.  If there's an ArrayIndexOutOfBoundsError,
			// we want to make sure there's a zero set.
		for (int count = 0; count < LOG_DATA_NAMES.length; count++) {

			m_logData[count] = 0;
			m_accuracyLog[count] = false;

		}	// cycle through and set zeros.

			// the market on close one day = diff between MOC on the day of the signal and MOC
			// the day after.
			// this currently assumes a bullish pattern.

		try {

				// Next we need to figure out when the strategy instructs that it would be time to
				// exit the position.
				// To do this, we have to scan forward each day and ask the strategy whether it is
				// time to exit.  When the signal comes back true, then we need to exit the position
				// on the close and calculate the relevant delta from there.
			boolean exitedPosition = false;

			int countForwardToExit;

			for (countForwardToExit = 1; (!exitedPosition) && ((lookback - countForwardToExit) >= 0);
				countForwardToExit++) {

				if (strategies.testExitAtDay(sd, strategyId, lookback - countForwardToExit))
					exitedPosition = true;

			}	// while the position has not been exited.

			countForwardToExit--;

				// if we ran up until today's data without finding an exit signal, then we assume
				// that today is the exit (which is kind of inaccurate, but it's the best we can do).
			if (countForwardToExit > lookback)
				countForwardToExit = lookback;

				// we need to record the date the position then closed.
			m_datePositionClosed.setTimeInMillis((sd.getDates()[lookback - countForwardToExit].getTimeInMillis()));

				// Now that we figured out where we would exit the position, we need to calculate (1)
				// the MOC-close on exit signal delta and (2) the MOO-close on exit signal delta (No. 2 is taken out for now).
			m_logData[MOC_TO_EXIT_SIGNAL] =
				((closes[lookback - countForwardToExit] - closes[lookback]) / closes[lookback]) * 100;
				//(closes[lookback - countForwardToExit] / closes[lookback]) - 1;
/*			m_logData[MOO_TO_EXIT_SIGNAL] =
				calculateDelta(opens[lookback], closes[lookback - countForwardToExit]);
*/			m_logData[DAYS_TO_EXIT_SIGNAL] = countForwardToExit;

				// finally, we need to update the accuracy log -- it's true if the change was positive and this strategy is bullish
				// or negative and this strategy is bearish.
			if ((m_logData[MOC_TO_EXIT_SIGNAL] > 0) && (isBullish))
				m_accuracyLog[MOC_TO_EXIT_SIGNAL] = true;
			else if ((m_logData[MOC_TO_EXIT_SIGNAL] < 0) && (!isBullish))
				m_accuracyLog[MOC_TO_EXIT_SIGNAL] = true;
			else

				m_accuracyLog[MOC_TO_EXIT_SIGNAL] = false;

/*				// Now, we're going to look through each bit of data we've tracked.  If it
				// is positive and we have a bullish strategy (or vice versa), then we need to
				// mark it as that.
			for (int countAccuracyLog = 0; countAccuracyLog < LOG_DATA_NAMES.length; countAccuracyLog++) {

				if (((m_logData[countAccuracyLog] > 0) && (isBullish)) ||
						((m_logData[countAccuracyLog] < 0) && (!isBullish)))
					m_accuracyLog[countAccuracyLog] = true;
				else
					m_accuracyLog[countAccuracyLog] = false;

			}	// for
*/

/*

			// 	I HAVE COMMENTED ALL OF THIS OUT, AT LEAST TEMPORARILY, BECAUSE THE INFORMATION
			//	IT GATHERS IS REALLY QUITE USELESS AND I AM MOVING TOWARD A MODEL OF HAVING
			//	DEFINED EXIT SIGNALS -- JLS 8/18/2010
				// let's first calculate the day-over-day changes.
			if (lookback >= 1) {

				m_logData[MOC_ONE_DAY] = calculateDelta (closes[lookback], closes[lookback - 1]);
				m_logData[MOO_ONE_DAY] = calculateDelta (opens[lookback - 1], closes[lookback - 1]);

			}
			else {

				m_logData[MOC_ONE_DAY] = 0;
				m_logData[MOO_ONE_DAY] = 0;

			}

			if (lookback >= 2)
				m_logData[MOO_TWO_DAY] = calculateDelta (opens[lookback - 1], closes[lookback - 2]);
			else
				m_logData[MOO_TWO_DAY] = 0;


			if (lookback >= 3)
				m_logData[MOO_THREE_DAY] = calculateDelta (opens[lookback - 1], closes[lookback - 3]);
			else
				m_logData[MOO_THREE_DAY] = 0;

			if (lookback >=5)
				m_logData[MOO_ONE_WEEK] = calculateDelta (opens[lookback - 1], closes[lookback - 5]);
			else
				m_logData[MOO_ONE_WEEK] = 0;

				// next, we'll calculate the one-day changes if the position is held market neutral.
				// in other words, this is what would happen if, for a long position, the trade
				// was matched with a market-wide short.  For bullish positions, then we subtract.
				// For bearish positions, then we add.
			if (isBullish) {

				if (lookback >= 1) {

					m_logData[MOC_ONE_DAY_NEUTRAL] = (m_logData[MOC_ONE_DAY] -
							calculateDelta(mktCloses[lookback], mktCloses[lookback - 1]));
					m_logData[MOO_ONE_DAY_NEUTRAL] = (m_logData[MOO_ONE_DAY] -
							calculateDelta(mktOpens[lookback - 1], mktCloses[lookback - 1]));

				}
				else {

					m_logData[MOC_ONE_DAY_NEUTRAL] = 0;
					m_logData[MOO_ONE_DAY_NEUTRAL] = 0;

				}

				if (lookback >= 2)
					m_logData[MOO_TWO_DAY_NEUTRAL] = (m_logData[MOO_TWO_DAY] -
						calculateDelta(mktOpens[lookback - 1], mktCloses[lookback - 2]));
				else
					m_logData[MOO_TWO_DAY_NEUTRAL] = 0;

				if (lookback >= 3)
					m_logData[MOO_THREE_DAY_NEUTRAL] = (m_logData[MOO_THREE_DAY] -
						calculateDelta(mktOpens[lookback - 1], mktCloses[lookback - 3]));
				else
					m_logData[MOO_THREE_DAY_NEUTRAL] = 0;

				if (lookback >= 5)
					m_logData[MOO_ONE_WEEK_NEUTRAL] = (m_logData[MOO_ONE_WEEK] -
						calculateDelta(mktOpens[lookback - 1], mktCloses[lookback - 5]));
				else
					m_logData[MOO_ONE_WEEK_NEUTRAL] = 0;

			}	// if this is bullish
			else {

				if (lookback >= 1) {

					m_logData[MOC_ONE_DAY_NEUTRAL] = (m_logData[MOC_ONE_DAY] +
							calculateDelta(mktCloses[lookback], mktCloses[lookback - 1]));
					m_logData[MOO_ONE_DAY_NEUTRAL] = (m_logData[MOO_ONE_DAY] +
							calculateDelta(mktOpens[lookback - 1], mktCloses[lookback - 1]));

				}
				else {

					m_logData[MOC_ONE_DAY_NEUTRAL] = 0;
					m_logData[MOO_ONE_DAY_NEUTRAL] = 0;

				}

				if (lookback >= 2)
					m_logData[MOO_TWO_DAY_NEUTRAL] = (m_logData[MOO_TWO_DAY] +
						calculateDelta(mktOpens[lookback - 1], mktCloses[lookback - 2]));
				else
					m_logData[MOO_TWO_DAY_NEUTRAL] = 0;

				if (lookback >= 3)
					m_logData[MOO_THREE_DAY_NEUTRAL] = (m_logData[MOO_THREE_DAY] +
						calculateDelta(mktOpens[lookback - 1], mktCloses[lookback - 3]));
				else
					m_logData[MOO_THREE_DAY_NEUTRAL] = 0;

				if (lookback >= 5)
					m_logData[MOO_ONE_WEEK_NEUTRAL] = (m_logData[MOO_ONE_WEEK] +
						calculateDelta(mktOpens[lookback - 1], mktCloses[lookback - 5]));
				else
					m_logData[MOO_ONE_WEEK_NEUTRAL] = 0;

			}

				// Now, we need to calculate whether the signal was accurate in each individual time frame.
				// If the strategy is bullish, then we look for positive movement, if bearish, then
				// negative movement.
			if (((m_logData[MOC_ONE_DAY] > 0) && (isBullish)) || ((m_logData[MOC_ONE_DAY] < 0) && (!isBullish)))
				m_accuracyLog[MOC_ONE_DAY] = true;
			else
				m_accuracyLog[MOC_ONE_DAY] = false;

			if (((m_logData[MOO_ONE_DAY] > 0) && (isBullish)) || ((m_logData[MOO_ONE_DAY] < 0) && (!isBullish)))
				m_accuracyLog[MOO_ONE_DAY] = true;
			else
				m_accuracyLog[MOO_ONE_DAY] = false;

			if (((m_logData[MOC_ONE_DAY_NEUTRAL] > 0) && (isBullish)) ||
					((m_logData[MOC_ONE_DAY_NEUTRAL] < 0) && (!isBullish)))
				m_accuracyLog[MOC_ONE_DAY_NEUTRAL] = true;
			else
				m_accuracyLog[MOC_ONE_DAY_NEUTRAL] = false;

			if (((m_logData[MOO_ONE_DAY_NEUTRAL] > 0) && (isBullish)) ||
					((m_logData[MOO_ONE_DAY_NEUTRAL] < 0) && (!isBullish)))
				m_accuracyLog[MOO_ONE_DAY_NEUTRAL] = true;
			else
				m_accuracyLog[MOO_ONE_DAY_NEUTRAL] = false;

			if (((m_logData[MOO_TWO_DAY] > 0) && (isBullish)) || ((m_logData[MOO_TWO_DAY] < 0) && (!isBullish)))
				m_accuracyLog[MOO_TWO_DAY] = true;
			else
				m_accuracyLog[MOO_TWO_DAY] = false;

			if (((m_logData[MOO_TWO_DAY_NEUTRAL] > 0) && (isBullish)) ||
					((m_logData[MOO_TWO_DAY_NEUTRAL] < 0) && (!isBullish)))
				m_accuracyLog[MOO_TWO_DAY_NEUTRAL] = true;
			else
				m_accuracyLog[MOO_TWO_DAY_NEUTRAL] = false;

			if (((m_logData[MOO_THREE_DAY] > 0) && (isBullish)) || ((m_logData[MOO_THREE_DAY] < 0) && (!isBullish)))
				m_accuracyLog[MOO_THREE_DAY] = true;
			else
				m_accuracyLog[MOO_THREE_DAY] = false;

			if (((m_logData[MOO_THREE_DAY_NEUTRAL] > 0) && (isBullish)) ||
					((m_logData[MOO_THREE_DAY_NEUTRAL] < 0) && (!isBullish)))
				m_accuracyLog[MOO_THREE_DAY_NEUTRAL] = true;
			else
				m_accuracyLog[MOO_THREE_DAY_NEUTRAL] = false;

			if (((m_logData[MOO_ONE_WEEK] > 0) && (isBullish)) || ((m_logData[MOO_ONE_WEEK] < 0) && (!isBullish)))
				m_accuracyLog[MOO_ONE_WEEK] = true;
			else
				m_accuracyLog[MOO_ONE_WEEK] = false;

			if (((m_logData[MOO_ONE_WEEK_NEUTRAL] > 0) && (isBullish)) ||
					((m_logData[MOO_ONE_WEEK_NEUTRAL] < 0) && (!isBullish)))
				m_accuracyLog[MOO_ONE_WEEK_NEUTRAL] = true;
			else
				m_accuracyLog[MOO_ONE_WEEK_NEUTRAL] = false;

				// Next, we have to find the next peak.  What this algorithm does is look at the highs
				// (or lows) and finds the next such high/low without a retracement below/above a certain
				// percentage -- the idea is that it is basically like tracking the stock's movement
				// if it's traded with a trailing stop.
			if (isBullish) {

					// we need to go through each day and compare the low of the day with (1) the
					// previous day's close and (2) that day's open.  If the low is less than the trailing
					// stop, then we assume that the stop loss would have been tripped and the position would
					// have gone to cash.

					// we're also going to keep track of the high on the day on which the
					// stop loss is tripped.

					// we start out with the position opening at the open of the day after the signal.
				float openingPosition = opens[lookback - 1];
				float currentPosition = openingPosition;

				float peakAtStopLossTripped = 0, stopLossValue = 0;

				boolean stopLossTripped = false;

				int daysUntilStopped = 1;

					// Now, we'll keep going forward until the stop loss is tripped.
				while (!stopLossTripped) {

						// These are the two values we care about -- the first is the different between the previous close and today's low,
						// and the second is the difference between today's open and today's low.
					float deltaLowPreviousClose = calculateDelta(closes[lookback - (daysUntilStopped - 1)], lows[lookback - daysUntilStopped]);
					float deltaLowOpen = calculateDelta(opens[lookback - daysUntilStopped], lows[lookback - daysUntilStopped]);

						// if either of those numbers exceeds the trailing stop (e.g., a bigger move), then
						// we stop the scan.
					if ((((deltaLowPreviousClose < 0) && (Math.abs(deltaLowPreviousClose) > trailingStop))) ||
							((deltaLowOpen < 0) && (Math.abs(deltaLowOpen) > trailingStop))) {

						stopLossTripped = true;

							// we need to wrap up these values.
						stopLossValue = lows[lookback - daysUntilStopped];
						peakAtStopLossTripped = highs[lookback - daysUntilStopped];

					}

					else {

						daysUntilStopped++;
						currentPosition = opens[lookback - 1];

					}	// else

				}	// while

					// Finally, let's assign these numbers out to the array.
				m_logData[STOP_LOSS_RETRACEMENT] = calculateDelta(openingPosition, stopLossValue);
				m_logData[PEAK_AT_RETRACEMENT] = calculateDelta(openingPosition, peakAtStopLossTripped);
				m_logData[DAYS_UNTIL_RETRACEMENT] = daysUntilStopped / 100;

			}	// if this is a bullish pattern.
			else {

					// THIS NEEDS TO BE COMPLETED!!!!!!!

			}	// if this is a bearish pattern.
*/


		}	// try
		catch (ArrayIndexOutOfBoundsException e) {

			e.printStackTrace();

		}	// catch ArrayIndexOutofBoundsError

	}	// ctor

	public BacktestLogEntry() {

		m_ticker = "";

		m_datePositionOpened = new GregorianCalendar();
		m_datePositionClosed = new GregorianCalendar();

		for (int count = 0; count < LOG_DATA_NAMES.length; count++) {
			m_logData[count] = 0;
			m_accuracyAverages[count] = 0;
		}	//

	}	// ctor

	/**
	 * This method adds the values from another entry to this entry.  It does not change the passed
	 * entry.  It also adds to averages for accuracy average variable.
	 */
	public void addEntry (BacktestLogEntry additur) {

		for (int count = 0; count < LOG_DATA_NAMES.length; count++) {
			m_logData[count] += additur.m_logData[count];

			if (additur.m_accuracyLog[count]) {

				m_accuracyAverages[count]++;

			}
		}	//

	}	// addEntry

	public BacktestLogEntry averageEntry (int divisor, float percentMatches, float percentAccurate) {

		BacktestLogEntry toReturn = new BacktestLogEntry();

		toReturn.m_percentMatches = percentMatches * 100;
		toReturn.m_percentAccurate = percentAccurate * 100;

			// for now, at least, we only care about the MOC_TO_EXIT NUMBER, so that's all we're going
			// to average.
		toReturn.m_logData[MOC_TO_EXIT_SIGNAL] = m_logData[MOC_TO_EXIT_SIGNAL] / divisor;
		toReturn.m_accuracyAverages[MOC_TO_EXIT_SIGNAL] = m_accuracyAverages[MOC_TO_EXIT_SIGNAL];

/*		for (int count = 0; count < LOG_DATA_NAMES.length; count++) {

			toReturn.m_logData[count] = m_logData[count] / divisor;
			toReturn.m_accuracyAverages[count] = m_accuracyAverages[count] / divisor;

		}	// for
*/
		return toReturn;

	}	// averageEntry

	private static final float calculateDelta (float original, float changed) {

		float toReturn = 0;

		toReturn = (changed - original) / original;

		return toReturn;

	}	// calculateDelta

	public float getMocToExit() { return m_logData[MOC_TO_EXIT_SIGNAL]; }


}	// class BacktestLogEntry