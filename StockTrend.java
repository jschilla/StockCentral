package stockcentral;

import java.io.*;

public class StockTrend implements Serializable, StockCentralConstants {

	private int m_trend = CONSOLIDATION;
	private float m_trendIntensity = 0.0f;
	private int m_trendLength = 0;
	
		// accessor methods
	public void setTrend(int t) { m_trend = t; }
	public int getTrend() { return m_trend; }
	public float getTrendIntensity() { return m_trendIntensity; }
	public void setTrendIntensity(float ti) { m_trendIntensity = ti; }
	public float getTrendLength() { return m_trendLength; }
	public void setTrendLength(int tl) { m_trendLength = tl; }
	
	public static String getTrendDescription(int test) {
		
		String toReturn = null;
		
		switch (test) {
		
		case BULLISH :	toReturn = "Bullish";
						break;
						
		case BEARISH :	toReturn = "Bearish";
						break;
						
		default		 :	toReturn = "Consolidation";
		
		}	// switch
		
		return toReturn;
		
	}	// getTrendDescription
	
}
