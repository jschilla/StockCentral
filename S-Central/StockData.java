/**
 *
 */
package stockcentral;

import java.io.*;
import java.text.DateFormat;
import java.util.*;
import org.jfree.data.xy.*;
import webcab.lib.finance.trading.indicators.*;

/**
 * This class represents all of the data for a given stock.
 *
 * @author Jack Schillaci
 * @version Build 1/14/2007
 *
 */
public class StockData implements Monitorable, StockCentralConstants,
    	Serializable {

	// constants

	private static final String FILENAME_SUFFIX = "_StockData.dat";

	// instance variables

	private float[] m_opens, m_closes, m_highs, m_lows;

	private long[] m_volumes;

	private Calendar[] m_dates;

	private float m_avgVolatility;

	// private CandlestickPattern m_lastPattern = CandlestickPattern.NADA;

	private StockTrend m_recentTrend = null, m_shortTermTrend = null,
			m_longTermTrend = null, m_recentPrediction = null;

	private int m_numTradingDates;

	private String m_ticker;

	private float[] m_twoDayRSI, m_fourDayRSI;

	private float[] m_200DayEMA, m_200DaySMA, m_5DaySMA;

	private MacD m_macd_9_26_9, m_macd_5_35_5, m_macd_4_26_9, m_macd_4_9_4;

	// private StockInfo m_stockInfo;

	// instance variable accessor methods

	public MacD getMacD9269() { return m_macd_9_26_9; }
	public MacD getMacD5355() { return m_macd_5_35_5; }
	public MacD getMacD4269() { return m_macd_4_26_9; }
	public MacD getMacD494() { return m_macd_4_9_4; }

	public void setOpens(float[] s) {
		m_opens = s;
	}

	public void setCloses(float[] c) {
		m_closes = c;
	}

	public void setHighs(float[] h) {
		m_highs = h;
	}

	public void setLows(float[] l) {
		m_lows = l;
	}

	public void setVolumes(long[] v) {
		m_volumes = v;
	}

	public void setDates(Calendar[] d) {
		m_dates = d;
	}

	public void setNumTradingDates(int n) {
		m_numTradingDates = n;
	}

	public void setTicker(String s) {
		m_ticker = s;
	}

	public float[] getOpens() {
		return m_opens;
	}

	public float[] getCloses() {
		return m_closes;
	}

	public float getLastClose() {
		return m_closes[0];
	}

	public float[] getHighs() {
		return m_highs;
	}

	public float[] getLows() {
		return m_lows;
	}

	public long[] getVolumes() {
		return m_volumes;
	}

	public Calendar[] getDates() {
		return m_dates;
	}

	public int getNumTradingDates() {
		return m_numTradingDates;
	}

	public String getTicker() {
		return m_ticker;
	}

	// public CandlestickPattern getCandlestickPattern() { return m_lastPattern;
	// }
	public StockTrend getRecentPrediction() {
		return m_recentPrediction;
	}

	public float[] getTwoDayRSI() {
		return m_twoDayRSI;
	}

	public float[] getFourDayRSI() {
		return m_fourDayRSI;
	}

	public float[] get200DayEMA() {
		return m_200DayEMA;
	}

	public float[] get200DaySMA() { return m_200DaySMA; }

	public float[] get5DaySMA() { return m_5DaySMA; }

	public boolean hasRecentTrend() {
		return (m_recentTrend.getTrend() != CONSOLIDATION);
	}

	public boolean hasShortTermTrend() {

		return (m_shortTermTrend.getTrend() != CONSOLIDATION);

	} // hasShortTermTrend

	public boolean hasLongTermTrend() {

		return (m_longTermTrend.getTrend() != CONSOLIDATION);

	} // hasShortTermTrend

	// instance methods

	/*
	 * public boolean analyzeThis() {
	 *
	 * m_avgVolatility = StockCentral.calculateAvgDailyVolatility(m_opens,
	 * m_closes);
	 *
	 * boolean toReturn = false;
	 *
	 * m_recentPrediction = new StockTrend(); m_recentTrend = new StockTrend();
	 * m_lastPattern = CandlestickPattern.NADA;
	 *
	 * try {
	 *
	 * //idLongTermTrend(); //idShortTermTrend(); idRecentTrend();
	 *
	 * idLastCandlestickPattern(m_recentTrend.getTrend());
	 *
	 * if (hasRecentTrend() && (m_lastPattern != CandlestickPattern.NADA))
	 * toReturn = true;
	 *
	 * m_recentPrediction.setTrend(CandlestickPattern.getPatternPrediction(m_lastPattern,
	 * m_recentTrend.getTrend()));
	 *  } // try catch (ArrayIndexOutOfBoundsException e) {
	 *
	 * System.out.println("Array Index Exceeded for Stock " + m_ticker + ".");
	 *  } // catch
	 *
	 * return toReturn;
	 *  } // analyzeThis
	 */
	private void findAreasOfSupportAndResistance() {

	} // findAreasOfSupportAndResistance

	/**
	 * This is where a LOT of the action is. This method takes as arguments the
	 * last three days' worth of highs, lows, opens and closes. It looks at them
	 * to determine if there is a candlestick pattern emerging in the past
	 * couple days of trading.
	 *
	 */
	/*
	 * private void idLastCandlestickPattern (int lastTrend) throws
	 * ArrayIndexOutOfBoundsException {
	 *
	 * boolean todayPositive = (m_closes[0] > m_opens[0]);
	 *
	 * float todaysChange = Math.abs(m_opens[0] - m_closes[0]); float
	 * todaysTradingRange = m_highs[0] - m_lows[0]; float todaysUpperShadow,
	 * todaysLowerShadow;
	 *
	 * if (todayPositive) {
	 *
	 * todaysUpperShadow = m_highs[0] - m_closes[0]; todaysLowerShadow =
	 * m_opens[0] - m_lows[0];
	 *  } // if else {
	 *
	 * todaysUpperShadow = m_highs[0] - m_opens[0]; todaysLowerShadow =
	 * m_closes[0] - m_lows[0];
	 *  }
	 *  // first, let's look for a doji.
	 *  // this will test to see if the real body is a Doji. if ((todaysChange <
	 * (m_avgVolatility * DOJI_SHARE_OF_VOLATILITY)) || (todaysChange <
	 * DEFAULT_DOJI)) {
	 *
	 * m_lastPattern = CandlestickPattern.DOJI;
	 *
	 *  } // if
	 *  // next, let's look to see if this is a spinning top. a spinning top has
	 * a slightly // larger real body than does a Doji. it can be either
	 * positive or negative. if (todaysChange < (m_avgVolatility *
	 * SPINNING_TOP_MAX)) m_lastPattern = CandlestickPattern.SPINNING_TOP;
	 *  // next, let's look for a bullish belt hold. a bullish belt hold has a
	 * fairly long // positive real body, opens very close to its lows and
	 * closes fairly close to its open. // this is only a meaningful candle if
	 * it comes at the end of a bearish trend. if ((todayPositive) &&
	 * (todaysChange > (m_avgVolatility * HOLD_SHARE_OF_VOLATILITY)) &&
	 * (todaysLowerShadow < (todaysChange * HOLD_SMALL_SIDE_SHARE)) &&
	 * (todaysUpperShadow < (todaysChange * HOLD_LARGE_SIDE_SHARE)) &&
	 * (lastTrend == BEARISH)) m_lastPattern =
	 * CandlestickPattern.BULLISH_BEAR_HOLD;
	 *  // next, let's look for a bearish belt hold. a bearish belt hold has a
	 * fairly long // negative real body, opens very close to its high and
	 * closes fairly close to its // low. this is only a meaningful if it comes
	 * at the end of a bullish trend. else if ((!todayPositive) && (todaysChange >
	 * (m_avgVolatility * HOLD_SHARE_OF_VOLATILITY)) && (todaysLowerShadow <
	 * (todaysChange * HOLD_LARGE_SIDE_SHARE)) && (todaysUpperShadow <
	 * (todaysChange * HOLD_LARGE_SIDE_SHARE)) && (lastTrend == BULLISH))
	 * m_lastPattern = CandlestickPattern.BEARISH_BEAR_HOLD;
	 *  // next, we'll look for a hammer. a hammer is either positive or
	 * negative, and a // a lower shadow that is at least twice as large as the
	 * real body. this is bullish, // but only meaningful if it comes at the end
	 * of a bearish trend. else if (todaysLowerShadow >= (todaysChange *
	 * HAMMER_SHARE_OF_REAL_BODY) && (lastTrend == BEARISH)) m_lastPattern =
	 * CandlestickPattern.HAMMER;
	 *  // next, we'll look for a hanging man. a hanging man is either positive //
	 * or negative, and a lower shadow that is at least twice as large as the //
	 * real body. this is bearish, but only meaningful if it comes at the // end
	 * of a bullish trend. else if ((todaysLowerShadow >= (todaysChange *
	 * HAMMER_SHARE_OF_REAL_BODY)) && (lastTrend == BULLISH)) m_lastPattern =
	 * CandlestickPattern.HANGING_MAN;
	 *  // next, we'll look for a shooting star. a shooting star is either
	 * positive // or negative, and has an upper shadow at least twice as large
	 * as the readl body. // this is bearish, but only meaningful if it comes at
	 * the end of a bullish // trend. else if ((todaysUpperShadow <
	 * (todaysChange * HAMMER_SHARE_OF_REAL_BODY)) && (lastTrend == BULLISH))
	 * m_lastPattern = CandlestickPattern.SHOOTING_STAR;
	 *  // this checks to see if a doji or spinning top is a high-wave candle.
	 * if (((m_lastPattern == CandlestickPattern.DOJI) || (m_lastPattern ==
	 * CandlestickPattern.SPINNING_TOP)) && (todaysUpperShadow >
	 * (m_avgVolatility * HIGH_WAVE_SHARE_OF_VOLATILITY)) && (todaysLowerShadow >
	 * (m_avgVolatility * HIGH_WAVE_SHARE_OF_VOLATILITY))) m_lastPattern =
	 * CandlestickPattern.HIGH_WAVE_CANDLE;
	 *
	 *  // now we're going to look for two-candle patterns. first, we have to
	 * set up a // few variables representing yesterday's trading activity.
	 * boolean yesterdayPositive = (m_closes[1] > m_opens[1]);
	 *
	 * float yesterdaysChange = Math.abs(m_opens[1] - m_closes[1]); float
	 * yesterdaysTradingRange = m_highs[1] - m_lows[1]; float
	 * yesterdaysUpperShadow, yesterdaysLowerShadow, yesterdaysMedian;
	 *
	 * if (yesterdayPositive) {
	 *
	 * yesterdaysUpperShadow = m_highs[1] - m_closes[1]; yesterdaysLowerShadow =
	 * m_opens[1] - m_lows[1]; yesterdaysMedian = m_opens[1] - (yesterdaysChange /
	 * 2);
	 *  } // if else {
	 *
	 * yesterdaysUpperShadow = m_highs[1] - m_opens[1]; yesterdaysLowerShadow =
	 * m_closes[1] - m_lows[1]; yesterdaysMedian = m_closes[1] -
	 * (yesterdaysChange / 2);
	 *  } // else
	 *  // first, let's check for a bullish piercing pattern. a bullish piercing
	 * pattern // is a negative candle followed by a positive candle. the second
	 * day gaps down, // but "pierces" the prior day's price action by closing
	 * above the previous day's // halfway up the candle. this is a bullish
	 * pattern, but is only meaningful // if it comes at the end of a bearish
	 * trend. if ((!yesterdayPositive) && (todayPositive) && (m_opens[0] <
	 * m_closes[1]) && (m_closes[0] > yesterdaysMedian) && (lastTrend ==
	 * BEARISH)) m_lastPattern = CandlestickPattern.BULLISH_PIERCING;
	 *  // next, let's check for dark cloud cover. cloud cover is a positive
	 * candle // followed by a negative, and signals the end of a bullish trend.
	 * the second // day gaps up but pierces at least halfway through the
	 * previous day's candle. only // meaningful when it comes at the end of a
	 * bullish trend. else if ((yesterdayPositive) && (!todayPositive) &&
	 * (m_opens[0] > m_closes[1]) && (m_closes[0] < yesterdaysMedian) &&
	 * (lastTrend == BULLISH)) m_lastPattern =
	 * CandlestickPattern.DARK_CLOUD_COVER;
	 *  // next, we'll check for a bullish counter-attack. a bullish
	 * counter-attack // is a negative candle followed by a positive candle that
	 * gaps down on the // open and closes at or near the negative candle's
	 * close. it comes at the // end of a bearish trend. it signals the end of a
	 * bearish trend. else if ((!yesterdayPositive) && (todayPositive) &&
	 * (m_opens[0] < m_closes[1]) && ((Math.abs(m_closes[0] - m_closes[1]) <
	 * (todaysTradingRange * COUNTER_ATTACK_MAX))) && (lastTrend == BEARISH))
	 * m_lastPattern = CandlestickPattern.BULLISH_COUNTER_ATTACK;
	 *  // guess what comes next? let's check for a bearish counter-attack. a
	 * bearish // counter-attack is a positive candle followed by a negative
	 * candle that gaps // up on the open and closes at or near the positive
	 * candle's close. it comes at // the end of a bullish trend, and signals
	 * the end of that trend. else if ((yesterdayPositive) && (!todayPositive) &&
	 * (m_opens[0] > m_closes[1]) && ((Math.abs(m_closes[0] - m_closes[1]) <
	 * (todaysTradingRange * COUNTER_ATTACK_MAX))) && (lastTrend == BULLISH))
	 * m_lastPattern = CandlestickPattern.BEARISH_COUNTER_ATTACK;
	 *  // next is the bullish engulfing pattern. a bullish engulfing pattern is
	 * a // negative candle followed by a positive candle that gaps down and
	 * closes above // the previous day's open. it comes at the end of a bearish
	 * trend and represents // a reversal of that trend. else if
	 * ((!yesterdayPositive) && (todayPositive) && (m_opens[0] < m_closes[1]) &&
	 * (m_closes[0] > m_opens[1]) && (lastTrend == BEARISH)) m_lastPattern =
	 * CandlestickPattern.BULLISH_ENGULFING;
	 *  // now we'll look for a bearish engulfing pattern. a bearish engulfing
	 * pattern // is a positive candle followed by a negative candle that gaps
	 * up on the open // and closes below the previous days open. it comes at
	 * the end of a bullish trend // and signals the end of that trend. else if
	 * ((yesterdayPositive) && (!todayPositive) && (m_opens[0] > m_closes[1]) &&
	 * (m_closes[0] < m_opens[1]) && (lastTrend == BULLISH)) m_lastPattern =
	 * CandlestickPattern.BEARISH_ENGULFING;
	 *  // next up is a bearish harami pattern. a bearish harami is actually a
	 * bullish // signal and comes at the end of a bearish trend. a bearish
	 * harami is a large // negative candle followed by either a doji or a
	 * spinning top. else if ((!yesterdayPositive) && (yesterdaysChange >=
	 * m_avgVolatility * HARAMI_SHARE_OF_VOLATILITY) && ((m_lastPattern ==
	 * CandlestickPattern.DOJI) || (m_lastPattern ==
	 * CandlestickPattern.SPINNING_TOP) || (m_lastPattern ==
	 * CandlestickPattern.HIGH_WAVE_CANDLE)) && (lastTrend == BEARISH))
	 * m_lastPattern = CandlestickPattern.BEARISH_HARAMI;
	 *  // and now for the bullish harami pattern. this is a bearish signal and
	 * comes // at the end of a bullish trend. it is a large positive candle
	 * followed by // a doji or spinning top. else if ((yesterdayPositive) &&
	 * (yesterdaysChange >= m_avgVolatility * HARAMI_SHARE_OF_VOLATILITY) &&
	 * ((m_lastPattern == CandlestickPattern.DOJI) || (m_lastPattern ==
	 * CandlestickPattern.SPINNING_TOP) || (m_lastPattern ==
	 * CandlestickPattern.HIGH_WAVE_CANDLE)) && (lastTrend == BULLISH))
	 * m_lastPattern = CandlestickPattern.BULLISH_HARAMI;
	 *  // this next block checks for any type of tweezer. a tweezer consists of //
	 * two or more days has a matching (or very, very close) high or low. it is //
	 * usually considered more reliable when the first real body is longer than //
	 * the second one. if (((Math.abs(m_highs[1] - m_highs[0]) <
	 * (m_avgVolatility * TWEEZERS_SHARE_OF_VOLATILITY)) || (Math.abs(m_lows[1] -
	 * m_lows[0]) < (m_avgVolatility * TWEEZERS_SHARE_OF_VOLATILITY))) &&
	 * (yesterdaysChange > todaysChange)) {
	 *  // so, we know this is some sort of tweezer, now we just need to figure
	 * out // what kind. if this is coming at the top of a bullish trend, then
	 * it's // a tweezers top. if the opposite, a tweezers bottom. if (lastTrend ==
	 * BULLISH) m_lastPattern = CandlestickPattern.TWEEZERS_TOP; else if
	 * (lastTrend == BEARISH) m_lastPattern =
	 * CandlestickPattern.TWEEZERS_BOTTOM;
	 *  } // if
	 *  // finally, we're going to check for three-day candlestick patterns.
	 *
	 * boolean dayBeforePositive = (m_closes[2] > m_opens[2]);
	 *  // the first three-day to look for is the morning star. a morning star
	 * takes // place in a downtrend and represents a reversal. a morning star
	 * consists of three // candles. the first is a negative candle. the second
	 * is either a spinning // top or a doji that gaps down from the first
	 * candle. the final candle is a // positive candle that gaps up from the
	 * second candle. if ((todayPositive) && (!dayBeforePositive) && (m_opens[1] <
	 * m_closes[2]) && (m_opens[0] > m_closes[1]) && (m_opens[0] > m_opens[1]) &&
	 * (yesterdaysChange < (m_avgVolatility * SPINNING_TOP_MAX)) && (lastTrend ==
	 * BEARISH)) m_lastPattern = CandlestickPattern.MORNING_STAR;
	 *  // the next thing to look for is the evening star. an evening star takes //
	 * place in an uptrend and represents a reversal. a morning star consists of
	 * three // candles. the first is a positive candle. the second is either a
	 * spinning // top or a doji that gaps up from the first candle. the final
	 * candle is a // negative candle that gaps down from the second candle.
	 * else if ((!todayPositive) && (dayBeforePositive) && (m_opens[1] >
	 * m_closes[2]) && (m_opens[0] < m_closes[1]) && (m_opens[0] < m_opens[1]) &&
	 * (yesterdaysChange < (m_avgVolatility * SPINNING_TOP_MAX)) && (lastTrend ==
	 * BULLISH)) m_lastPattern = CandlestickPattern.EVENING_STAR;
	 *  // we also need to figure out if a morning star is a morning doji star.
	 * if ((m_lastPattern == CandlestickPattern.MORNING_STAR) &&
	 * ((yesterdaysChange < (m_avgVolatility * DOJI_SHARE_OF_VOLATILITY)) ||
	 * (yesterdaysChange < DEFAULT_DOJI))) m_lastPattern =
	 * CandlestickPattern.MORNING_DOJI_STAR;
	 *  // this checks to see if an evening star is an evening doji star. if
	 * ((m_lastPattern == CandlestickPattern.EVENING_STAR) && ((yesterdaysChange <
	 * (m_avgVolatility * DOJI_SHARE_OF_VOLATILITY)) || (yesterdaysChange <
	 * DEFAULT_DOJI))) m_lastPattern = CandlestickPattern.EVENING_DOJI_STAR;
	 *  } // idLastCandlestickPattern
	 */
	/**
	 * This method tries to determine the most recent trend. It looks to
	 * short-term moving averages and the aroon indicator to detect a recent
	 * trend in the past week or two.
	 *
	 * @return the stock trend for the last few days.
	 */
	/*
	 * public void idLongTermTrend() {
	 *
	 * m_longTermTrend = new StockTrend();
	 *  // we obviously only want to run this if we have more data than is the
	 * length // of the long term trend indicators. if (m_closes.length >
	 * LONG_TERM_TREND_INDICATOR_LENGTH) {
	 *
	 * Aroon aroon = StockCentral.getAroon(); MovingAverage ma =
	 * StockCentral.getMovingAverage();
	 *
	 * float[] aroonUp, aroonDown, movingAvg; int lengthOfUp, lengthOfDown,
	 * lengthOfMA;
	 *
	 * aroonUp = StockCentral.arrayConvertDoubleToFloat(aroon.aroonUpOverPeriod(
	 * StockCentral.arrayConvertFloatToDouble(m_closes),
	 * LONG_TERM_TREND_INDICATOR_LENGTH));
	 *
	 * lengthOfUp = StockCentral.findLastFromArrayBelow(aroonUp,
	 * AROON_MINIMUM_FOR_LONG_TERM_TREND);
	 *  // if the length of the trend exceeds the minimum for a long-term trend, //
	 * then we will confirm that this is in fact a trend by looking at the
	 * recent // trend in the long-term moving average. if this confirms the
	 * trend, then // we record the length of the trend and the intensity of the
	 * trend. if ((lengthOfUp > MINIMUM_PERIOD_FOR_LONG_TERM_TREND)) {
	 *
	 * m_longTermTrend.setTrend(BULLISH);
	 *
	 * int trendLength = lengthOfUp;
	 * m_longTermTrend.setTrendLength(trendLength);
	 *  // this will calculate the trend intensity by finding the average change //
	 * per day over the course of the trend. float trendIntensity;
	 * trendIntensity = (m_closes[0] - m_closes[trendLength]) / trendLength;
	 * m_longTermTrend.setTrendIntensity(trendIntensity);
	 *  // finally, we need to figure out if this trend is moving enough to
	 * justify // calling it a trend as opposed to simply being a consolidation
	 * pattern. float trendIntensityThreeMonths = trendIntensity *
	 * MINIMUM_PERIOD_FOR_LONG_TERM_TREND; float intensityAsPortionOfPrice =
	 * trendIntensityThreeMonths / m_closes[0]; if (intensityAsPortionOfPrice <
	 * LONG_TERM_TREND_RATE_MINIMUM) m_longTermTrend.setTrend(CONSOLIDATION);
	 *  } // if
	 *  // if this isn't ID'd as an uptrend, let's look to see if it's a
	 * downtrend. else {
	 *
	 * aroonDown =
	 * StockCentral.arrayConvertDoubleToFloat(aroon.aroonDownOverPeriod(
	 * StockCentral.arrayConvertFloatToDouble(m_closes),
	 * LONG_TERM_TREND_INDICATOR_LENGTH));
	 *
	 * lengthOfDown = StockCentral.findLastFromArrayBelow(aroonDown,
	 * AROON_MINIMUM_FOR_LONG_TERM_TREND);
	 *  // if the length of the trend exceeds the minimum for a long-term trend, //
	 * then we will confirm that this is in fact a trend by looking at the
	 * recent // trend in the long-term moving average. if this confirms the
	 * trend, then // we record the length of the trend and the intensity of the
	 * trend. if ((lengthOfDown > MINIMUM_PERIOD_FOR_LONG_TERM_TREND)) {
	 *
	 * m_longTermTrend.setTrend(BEARISH);
	 *
	 * int trendLength = lengthOfDown;
	 * m_longTermTrend.setTrendLength(trendLength);
	 *  // this will calculate the trend intensity by finding the average change //
	 * per day over the course of the trend. float trendIntensity;
	 * trendIntensity = (m_closes[trendLength] - m_closes[0]) / trendLength;
	 * m_longTermTrend.setTrendIntensity(trendIntensity);
	 *  // finally, we need to figure out if this trend is moving enough to
	 * justify // calling it a trend as opposed to simply being a consolidation
	 * pattern. float trendIntensityThreeMonths = trendIntensity *
	 * MINIMUM_PERIOD_FOR_LONG_TERM_TREND; float intensityAsPortionOfPrice =
	 * trendIntensityThreeMonths / m_closes[0]; if (intensityAsPortionOfPrice <
	 * LONG_TERM_TREND_RATE_MINIMUM) m_longTermTrend.setTrend(CONSOLIDATION);
	 *  } // if
	 *  } // else
	 *  } // if
	 *  } // idLongTermTrend
	 */
	/**
	 * This method tries to determine the most recent short term trend. It looks
	 * to medium-term moving averages and the aroon indicator to detect a recent
	 * trend in the past three months.
	 *
	 * @return the stock trend for the last few months.
	 */
	/*
	 * public void idShortTermTrend() {
	 *
	 * m_shortTermTrend = new StockTrend();
	 *  // we only run the analysis if we have enough data. if (m_closes.length >
	 * SHORT_TERM_TREND_INDICATOR_LENGTH) {
	 *
	 * Aroon aroon = StockCentral.getAroon(); MovingAverage ma =
	 * StockCentral.getMovingAverage();
	 *
	 * float[] aroonUp, aroonDown, movingAvg; int lengthOfUp, lengthOfDown,
	 * lengthOfMA;
	 *
	 * aroonUp = StockCentral.arrayConvertDoubleToFloat(aroon.aroonUpOverPeriod(
	 * StockCentral.arrayConvertFloatToDouble(m_closes),
	 * SHORT_TERM_TREND_INDICATOR_LENGTH));
	 *
	 * lengthOfUp = StockCentral.findLastFromArrayBelow(aroonUp,
	 * AROON_MINIMUM_FOR_SHORT_TERM_TREND);
	 *  // if the length of the trend exceeds the minimum for a long-term trend, //
	 * then we will confirm that this is in fact a trend by looking at the
	 * recent // trend in the long-term moving average. if this confirms the
	 * trend, then // we record the length of the trend and the intensity of the
	 * trend. if ((lengthOfUp > MINIMUM_PERIOD_FOR_SHORT_TERM_TREND)) {
	 *
	 * m_shortTermTrend.setTrend(BULLISH);
	 *
	 * int trendLength = lengthOfUp;
	 * m_shortTermTrend.setTrendLength(trendLength);
	 *  // this will calculate the trend intensity by finding the average change //
	 * per day over the course of the trend. float trendIntensity;
	 * trendIntensity = (m_closes[0] - m_closes[trendLength]) / trendLength;
	 * m_shortTermTrend.setTrendIntensity(trendIntensity);
	 *  // finally, we need to figure out if this trend is moving enough to
	 * justify // calling it a trend as opposed to simply being a consolidation
	 * pattern. float trendIntensityThreeMonths = trendIntensity *
	 * MINIMUM_PERIOD_FOR_SHORT_TERM_TREND; float intensityAsPortionOfPrice =
	 * trendIntensityThreeMonths / m_closes[0]; if (intensityAsPortionOfPrice <
	 * SHORT_TERM_TREND_RATE_MINIMUM) m_shortTermTrend.setTrend(CONSOLIDATION);
	 *  } // if
	 *  // if this isn't ID'd as an uptrend, let's look to see if it's a
	 * downtrend. else {
	 *
	 * aroonDown =
	 * StockCentral.arrayConvertDoubleToFloat(aroon.aroonDownOverPeriod(
	 * StockCentral.arrayConvertFloatToDouble(m_closes),
	 * SHORT_TERM_TREND_INDICATOR_LENGTH));
	 *
	 * lengthOfDown = StockCentral.findLastFromArrayBelow(aroonDown,
	 * AROON_MINIMUM_FOR_SHORT_TERM_TREND);
	 *  // if the length of the trend exceeds the minimum for a long-term trend, //
	 * then we will confirm that this is in fact a trend by looking at the
	 * recent // trend in the long-term moving average. if this confirms the
	 * trend, then // we record the length of the trend and the intensity of the
	 * trend. if ((lengthOfDown > MINIMUM_PERIOD_FOR_SHORT_TERM_TREND)) {
	 *
	 * m_shortTermTrend.setTrend(BEARISH);
	 *
	 * int trendLength = lengthOfDown;
	 * m_shortTermTrend.setTrendLength(trendLength);
	 *  // this will calculate the trend intensity by finding the average change //
	 * per day over the course of the trend. float trendIntensity;
	 * trendIntensity = (m_closes[trendLength] - m_closes[0]) / trendLength;
	 * m_shortTermTrend.setTrendIntensity(trendIntensity);
	 *  // finally, we need to figure out if this trend is moving enough to
	 * justify // calling it a trend as opposed to simply being a consolidation
	 * pattern. float trendIntensityThreeMonths = trendIntensity *
	 * MINIMUM_PERIOD_FOR_SHORT_TERM_TREND; float intensityAsPortionOfPrice =
	 * trendIntensityThreeMonths / m_closes[0]; if (intensityAsPortionOfPrice <
	 * SHORT_TERM_TREND_RATE_MINIMUM) m_shortTermTrend.setTrend(CONSOLIDATION);
	 *  } // if
	 *  } // else
	 *  } // if
	 *  } // idShortTermTrend
	 */
	/**
	 * This method tries to determine the last long-term trend. It looks to
	 * long-term moving averages and the aroon indicator to detect a recent
	 * trend in the past year.
	 *
	 * @return the stock trend for the last year.
	 */
	/*
	 * public void idRecentTrend() {
	 *
	 * m_recentTrend = new StockTrend();
	 *  // we only run the analysis if we have enough data. if (m_closes.length >
	 * SHORT_TERM_TREND_INDICATOR_LENGTH) {
	 *
	 * Aroon aroon = StockCentral.getAroon(); MovingAverage ma =
	 * StockCentral.getMovingAverage();
	 *
	 * float[] aroonUp, aroonDown, movingAvg; int lengthOfUp, lengthOfDown,
	 * lengthOfMA;
	 *
	 * aroonUp = StockCentral.arrayConvertDoubleToFloat(aroon.aroonUpOverPeriod(
	 * StockCentral.arrayConvertFloatToDouble(m_closes),
	 * RECENT_TREND_INDICATOR_LENGTH));
	 *
	 * lengthOfUp = StockCentral.findLastFromArrayBelow(aroonUp,
	 * AROON_MINIMUM_FOR_RECENT_TREND);
	 *  // if the length of the trend exceeds the minimum for a long-term trend, //
	 * then we will confirm that this is in fact a trend by looking at the
	 * recent // trend in the long-term moving average. if this confirms the
	 * trend, then // we record the length of the trend and the intensity of the
	 * trend. if ((lengthOfUp > MINIMUM_PERIOD_FOR_RECENT_TREND)) {
	 *
	 * m_recentTrend.setTrend(BULLISH);
	 *
	 * int trendLength = lengthOfUp; m_recentTrend.setTrendLength(trendLength);
	 *  // this will calculate the trend intensity by finding the average change //
	 * per day over the course of the trend. float trendIntensity;
	 * trendIntensity = (m_closes[0] - m_closes[trendLength]) / trendLength;
	 * m_recentTrend.setTrendIntensity(trendIntensity);
	 *  // finally, we need to figure out if this trend is moving enough to
	 * justify // calling it a trend as opposed to simply being a consolidation
	 * pattern. float trendIntensityThreeMonths = trendIntensity *
	 * MINIMUM_PERIOD_FOR_RECENT_TREND; float intensityAsPortionOfPrice =
	 * trendIntensityThreeMonths / m_closes[0]; if (intensityAsPortionOfPrice <
	 * RECENT_TREND_RATE_MINIMUM) m_recentTrend.setTrend(CONSOLIDATION);
	 *  } // if
	 *  // if this isn't ID'd as an uptrend, let's look to see if it's a
	 * downtrend. else {
	 *
	 * aroonDown =
	 * StockCentral.arrayConvertDoubleToFloat(aroon.aroonDownOverPeriod(
	 * StockCentral.arrayConvertFloatToDouble(m_closes),
	 * RECENT_TREND_INDICATOR_LENGTH));
	 *
	 * lengthOfDown = StockCentral.findLastFromArrayBelow(aroonDown,
	 * AROON_MINIMUM_FOR_RECENT_TREND);
	 *  // if the length of the trend exceeds the minimum for a long-term trend, //
	 * then we will confirm that this is in fact a trend by looking at the
	 * recent // trend in the long-term moving average. if this confirms the
	 * trend, then // we record the length of the trend and the intensity of the
	 * trend. if ((lengthOfDown > MINIMUM_PERIOD_FOR_RECENT_TREND)) {
	 *
	 * m_recentTrend.setTrend(BEARISH);
	 *
	 * int trendLength = lengthOfDown;
	 * m_recentTrend.setTrendLength(trendLength);
	 *  // this will calculate the trend intensity by finding the average change //
	 * per day over the course of the trend. float trendIntensity;
	 * trendIntensity = (m_closes[trendLength] - m_closes[0]) / trendLength;
	 * m_recentTrend.setTrendIntensity(trendIntensity);
	 *  // finally, we need to figure out if this trend is moving enough to
	 * justify // calling it a trend as opposed to simply being a consolidation
	 * pattern. float trendIntensityThreeMonths = trendIntensity *
	 * MINIMUM_PERIOD_FOR_RECENT_TREND; float intensityAsPortionOfPrice =
	 * trendIntensityThreeMonths / m_closes[0]; if (intensityAsPortionOfPrice <
	 * RECENT_TREND_RATE_MINIMUM) m_recentTrend.setTrend(CONSOLIDATION);
	 *  } // if
	 *  } // else
	 *  } // if
	 *  } // idRecentTermTrend
	 */
	public void dataMonitor() {

		// let's put this data into the data output stream.
		StringBuffer sb = new StringBuffer();

		sb.append("Historical price data for stock ");
		sb.append(m_ticker);
		sb.append("\r\n");

		sb.append("Date\t\t\t\tOpen\tClose\tHigh\tLow\tVolume\r\n");
		sb
				.append("------------------------------------------------------------------------------\r\n");

		try {

			for (int i = 0; i < 10; i++) {

				sb.append(m_dates[i].toString());
				sb.append("\t");
				sb.append(m_opens[i]);
				sb.append('\t');
				sb.append(m_closes[i]);
				sb.append('\t');
				sb.append(m_highs[i]);
				sb.append('\t');
				sb.append(m_lows[i]);
				sb.append('\t');
				sb.append(m_volumes[i]);
				sb.append("\t\t");
				sb.append("\r\n");

			} // for

		} // try
		catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("\nArrayIndexOutOfBoundsError in DataMonitor");
		}

		/*
		 * sb.append("Data Analysis for Stock " + m_ticker); sb.append("\r\n");
		 * /*sb.append("Long Term Trend: " +
		 * StockTrend.getTrendDescription(m_longTermTrend) + "; Trend Length: " +
		 * m_longTermTrend.getTrendLength() + "; Trend Intensity: " +
		 * m_longTermTrend.getTrendIntensity()); sb.append("\r\n");
		 * sb.append("Short Term Trend: " +
		 * StockTrend.getTrendDescription(m_shortTermTrend) + "; Trend Length: " +
		 * m_shortTermTrend.getTrendLength() + "; Trend Intensity: " +
		 * m_shortTermTrend.getTrendIntensity()); sb.append("\r\n");
		 * sb.append("Recent Trend: " +
		 * StockTrend.getTrendDescription(m_recentTrend.getTrend()) + "; Trend
		 * Length: " + m_recentTrend.getTrendLength() + "; Trend Intensity: " +
		 * m_recentTrend.getTrendIntensity()); sb.append("\r\n");
		 * sb.append("Candlestick Pattern: " +
		 * CandlestickPattern.getPatternDescription(m_lastPattern));
		 * sb.append("Yahoo! Stock URL: " + generateYahooChartUrl(m_ticker));
		 * sb.append("\r\n");
		 */

		StockCentral.dataMonitorOutput(sb.toString());

	} // dataMonitor()

	public static String generateYahooChartUrl(String ticker) {

		return YAHOO_CHARTS_URL + ticker;

	} // generateYahooChartUrl

	/**
	 * This method calculates (or has other classes calculate) all of the
	 * various oscillators and bells and whistles used by the StockData class.
	 *
	 */
	public void calculateBellsAndWhistles() {

		// StockCentral central = new StockCentral();

		m_twoDayRSI = StockCentral.calculateRSI(m_closes, 2);

		m_fourDayRSI = StockCentral.calculateRSI(m_closes, 4);

		m_200DayEMA = StockCentral.calculateEMA(m_closes, 200);

		m_200DaySMA = StockCentral.calculateSMA(m_closes, 200);

		m_5DaySMA = StockCentral.calculateSMA(m_closes, 5);

		m_macd_9_26_9 = new MacD(9, 26, 9, m_closes);
		m_macd_5_35_5 = new MacD(5, 35, 5, m_closes);
		m_macd_4_26_9 = new MacD(4, 26, 9, m_closes);
		m_macd_4_9_4 = new MacD(4, 9, 4, m_closes);

		/*
		 * m_rsi = StockCentral.calculateRSI(m_closes);
		 *
		 * m_10DayEMA = StockCentral.calculateEMA(m_closes, 10); m_20DayEMA =
		 * StockCentral.calculateEMA(m_closes, 20); m_50DayEMA =
		 * StockCentral.calculateEMA(m_closes, 50); m_120DayEMA =
		 * StockCentral.calculateEMA(m_closes, 120); m_200DayEMA =
		 * StockCentral.calculateEMA(m_closes, 200);
		 *
		 * m_10DaySMA = StockCentral.calculateSMA(m_closes, 10); m_20DaySMA =
		 * StockCentral.calculateSMA(m_closes, 20); m_50DaySMA =
		 * StockCentral.calculateSMA(m_closes, 50); m_120DaySMA =
		 * StockCentral.calculateSMA(m_closes, 120); m_200DaySMA =
		 * StockCentral.calculateSMA(m_closes, 200);
		 *
		 * m_BOP = StockCentral.calculateBalanceOfPowerOnDay(m_opens, m_closes,
		 * m_highs, m_lows); m_BopSMA = StockCentral.calculateSMA(m_BOP, 5);
		 *
		 * m_aroonUp = StockCentral.calculateAroonUp(m_highs); m_aroonDown =
		 * StockCentral.calculateAroonDown(m_lows);
		 */

		/*
		 * idLongTermTrend(); idShortTermTrend(); idRecentTrend();
		 */

	} // calculateBellsAndWhistles

	/*
	 * public void createStockChart() {
	 *
	 * m_stockChart = new
	 * StockChart(StockCentral.subsetArray(TRADING_DAYS_IN_THREE_MONTHS,
	 * m_dates), StockCentral.subsetArray(TRADING_DAYS_IN_THREE_MONTHS,
	 * m_opens), StockCentral.subsetArray(TRADING_DAYS_IN_THREE_MONTHS,
	 * m_closes), StockCentral.subsetArray(TRADING_DAYS_IN_THREE_MONTHS,
	 * m_highs), StockCentral.subsetArray(TRADING_DAYS_IN_THREE_MONTHS, m_lows),
	 * StockCentral.subsetArray(TRADING_DAYS_IN_THREE_MONTHS, m_volumes),
	 * m_ticker);
	 *
	 * m_stockChart.setupRsiChart(StockCentral.subsetArray(TRADING_DAYS_IN_THREE_MONTHS,
	 * m_rsi));
	 *  } // createStockChart
	 */

	/**
	 * This method sends the data of this file into a file.
	 *
	 */
	/*
	 * public void save() {
	 *
	 * StockCentral.debugOutput("Saving stock data for stock " + m_ticker);
	 *
	 * DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
	 *
	 * CSVWriter out = new CSVWriter(FILENAME_PREFIX + m_ticker +
	 * FILENAME_SUFFIX, 21);
	 *
	 * out.nextEntry("Date"); out.nextEntry("Open"); out.nextEntry("Close");
	 * out.nextEntry("High"); out.nextEntry("Low"); out.nextEntry("Volume");
	 * out.nextEntry("RSI Value"); out.nextEntry("10-Day EMA");
	 * out.nextEntry("20-Day EMA"); out.nextEntry("50-Day EMA");
	 * out.nextEntry("120-Day EMA"); out.nextEntry("200-Day EMA");
	 * out.nextEntry("10-Day SMA"); out.nextEntry("20-Day SMA");
	 * out.nextEntry("50-Day SMA"); out.nextEntry("120-Day SMA");
	 * out.nextEntry("200-Day SMA"); out.nextEntry("Balance of Power");
	 * out.nextEntry("BoP MA"); out.nextEntry("Aroon Up"); out.nextEntry("Aroon
	 * Down");
	 *
	 * out.nextEntry(m_numTradingDates); out.finishLine();
	 *  // cycle through and save all of the data. for (int i = 0; i <
	 * m_numTradingDates; i++) {
	 *
	 * out.nextEntry(df.format(m_dates[i]));
	 *
	 * out.nextEntry(m_opens[i]); out.nextEntry(m_closes[i]);
	 * out.nextEntry(m_highs[i]); out.nextEntry(m_lows[i]);
	 *
	 * out.nextEntry(m_volumes[i]);
	 *
	 * if (i < m_rsi.length) out.nextEntry(m_rsi[i]); else out.nextEntry(0);
	 *
	 * if (i < m_10DayEMA.length) out.nextEntry(m_10DayEMA[i]); else
	 * out.nextEntry(0);
	 *
	 * if (i < m_20DayEMA.length) out.nextEntry(m_20DayEMA[i]); else
	 * out.nextEntry(0);
	 *
	 * if (i < m_50DayEMA.length) out.nextEntry(m_50DayEMA[i]); else
	 * out.nextEntry(0);
	 *
	 * if (i < m_120DayEMA.length) out.nextEntry(m_120DayEMA[i]); else
	 * out.nextEntry(0);
	 *
	 * if (i < m_200DayEMA.length) out.nextEntry(m_200DayEMA[i]); else
	 * out.nextEntry(0);
	 *
	 * if (i < m_10DaySMA.length) out.nextEntry(m_10DaySMA[i]); else
	 * out.nextEntry(0);
	 *
	 * if (i < m_20DaySMA.length) out.nextEntry(m_20DaySMA[i]); else
	 * out.nextEntry(0);
	 *
	 * if (i < m_50DaySMA.length) out.nextEntry(m_50DaySMA[i]); else
	 * out.nextEntry(0);
	 *
	 * if (i < m_120DaySMA.length) out.nextEntry(m_120DaySMA[i]); else
	 * out.nextEntry(0);
	 *
	 * if (i < m_200DaySMA.length) out.nextEntry(m_200DaySMA[i]); else
	 * out.nextEntry(0);
	 *
	 * out.nextEntry(m_BOP[i]);
	 *
	 * if (i < m_BopSMA.length) out.nextEntry(m_BopSMA[i]); else
	 * out.nextEntry(0);
	 *
	 * if (i < m_aroonUp.length) out.nextEntry(m_aroonUp[i]); else
	 * out.nextEntry(0);
	 *
	 * if (i < m_aroonDown.length) out.nextEntry(m_aroonDown[i]); else
	 * out.nextEntry(0);
	 *  } // for
	 *
	 * out.close();
	 *  } // save
	 *
	 * public void saveChart (File dir) {
	 *
	 * m_stockChart.saveChart(dir);
	 *  } // saveChart
	 */

	/***************************************************************************
	 * / This method creates a new stock data object and then loads the data
	 * into it. @return
	 *
	 * public static StockData load(String ticker) {
	 *
	 * StockCentral.debugOutput("Loading data for stock" + ticker);
	 *
	 * StockData toReturn = new StockData();
	 *
	 * float[] opens, closes, highs, lows; long[] volumes; int numTradingDays;
	 * Date[] dates;
	 *
	 * try {
	 *
	 * FileReader fr = new FileReader(FILENAME_PREFIX + ticker +
	 * FILENAME_SUFFIX); LineNumberReader in = new LineNumberReader(fr);
	 *
	 * in.readLine(); // the first line is a dud. it just has headers on it.
	 *
	 * numTradingDays = Integer.parseInt(in.readLine());
	 *
	 * StringTokenizer tokens;
	 *
	 * opens = new float[numTradingDays]; closes = new float[numTradingDays];
	 * highs = new float[numTradingDays]; lows = new float[numTradingDays];
	 * volumes = new long[numTradingDays]; dates = new Date[numTradingDays];
	 *
	 * String s;
	 *
	 * System.out.println(numTradingDays);
	 *  // cycle through and load all the data out of a file. for (int i = 0; i <
	 * numTradingDays; i++) {
	 *
	 * s = in.readLine();
	 *
	 * //System.out.println(s);
	 *
	 * if (s!=null) {
	 *
	 *
	 * tokens = new StringTokenizer(s, ",");
	 *
	 * dates[i] = new MiniDate(Byte.parseByte(tokens.nextToken()),
	 * Byte.parseByte(tokens.nextToken()),
	 * Short.parseShort(tokens.nextToken()));
	 *
	 * opens[i] = Float.parseFloat(tokens.nextToken()); closes[i] =
	 * Float.parseFloat(tokens.nextToken()); highs[i] =
	 * Float.parseFloat(tokens.nextToken()); lows[i] =
	 * Float.parseFloat(tokens.nextToken());
	 *
	 * volumes[i] = Long.parseLong(tokens.nextToken());
	 *
	 *  // System.out.println(dates[i].toString() + "," + opens[i] + "," +
	 * closes[i] + // "," + highs[i] + lows[i] + "," + volumes[i]);
	 *  } // if } // for
	 *
	 *
	 * toReturn.setDates(dates); toReturn.setOpens(opens);
	 * toReturn.setCloses(closes); toReturn.setHighs(highs);
	 * toReturn.setLows(lows); toReturn.setVolumes(volumes);
	 *
	 * System.out.println("Look where we ended up!");
	 *  } // try catch (IOException e) {
	 *
	 * System.out.println("There's no file found."); e.printStackTrace();
	 * System.exit(0); } // catch
	 *
	 * return toReturn;
	 *  } // load
	 */

	public void spitOutData() {

		StockCentral.dataMonitorOutput("Data for stock " + m_ticker);
		StockCentral.dataMonitorOutput("   Date   -   Close");

		for (int count = 0; count < m_closes.length; count++) {

			StockCentral.dataMonitorOutput(StockCentral.generateDateString(	m_dates[count]) +
					" - " + m_closes[count]);

		}	// cycle through each date

	}	// spitOutData


	/**
	*	This method appends the data from a separate stock data object to this current one,
	*	assuming that the data in the passed object is more recent and thus putting
	*	its data and the beginning of the relevant arrays.
	*/
	public void appendData(StockData sd) {

			// the way we do this is by creating List objects for each of the
			// data points that we need to archive.  Then, we add the new data to
			// the 0 index of each List.  Then we switch them BACK to arrays
			// and set them as the new objects.

			// HAVE TO ADD EXCEPTION HANDLING!!!

			// I THINK THIS IS WRONG -- IT'S creating LISTS OF ARRAYS.

			// now we add to create List objects for all of the variables from
			// the object we're about to add.
		float[] newOpens = sd.getOpens();
		float[] newCloses = sd.getCloses();
		float[] newHighs = sd.getHighs();
		float[] newLows = sd.getLows();
		Calendar[] newDates = sd.getDates();
		long[] newVolumes = sd.getVolumes();

			// Update the number of trading dates variable.
		setNumTradingDates(m_numTradingDates + sd.getNumTradingDates());

			// Now, we just merge the two lists for each variable, and then put
			// it back into this StockData object as an array.

		float[] combinedOpens = new float[m_opens.length + newOpens.length];
		float[] combinedCloses = new float[m_closes.length + newCloses.length];
		float[] combinedHighs = new float[m_highs.length + newHighs.length];
		float[] combinedLows = new float[m_lows.length + newLows.length];
		Calendar[] combinedDates = new Calendar[m_dates.length + newDates.length];
		long[] combinedVolumes = new long[m_volumes.length + newVolumes.length];

		System.arraycopy(newOpens, 0, combinedOpens, 0, newOpens.length);
		System.arraycopy(m_opens, 0, combinedOpens, newOpens.length, m_opens.length);

		System.arraycopy(newCloses, 0, combinedCloses, 0, newCloses.length);
		System.arraycopy(m_closes, 0, combinedCloses, newCloses.length, m_closes.length);

		System.arraycopy(newOpens, 0, combinedHighs, 0, newHighs.length);
		System.arraycopy(m_highs, 0, combinedHighs, newHighs.length, m_highs.length);

		System.arraycopy(newLows, 0, combinedLows, 0, newLows.length);
		System.arraycopy(m_lows, 0, combinedLows, newLows.length, m_lows.length);

		System.arraycopy(newDates, 0, combinedDates, 0, newDates.length);
		System.arraycopy(m_dates, 0, combinedDates, newDates.length, m_dates.length);

		System.arraycopy(newVolumes, 0, combinedVolumes, 0, newVolumes.length);
		System.arraycopy(m_volumes, 0, combinedVolumes, newVolumes.length, m_volumes.length);

		setOpens(combinedOpens);
		setCloses(combinedCloses);
		setHighs(combinedHighs);
		setLows(combinedLows);
		setVolumes(combinedVolumes);
		setDates(combinedDates);

			// We should spit out the data, just to make sure it's in the right order.
		spitOutData();

	}	// appendData

}	// class StockData
