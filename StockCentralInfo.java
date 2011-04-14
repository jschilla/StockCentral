/**
 * 
 */
package stockcentral;

import java.io.Serializable;
import java.util.*;

/**
 * @author Jack Schillaci
 *
 */
public class StockCentralInfo implements Serializable, StockCentralConstants {

		// instance variables
	private ArrayList<String> m_allTickers = new ArrayList<String>();
	private ArrayList<String> m_goodStockTickers = new ArrayList<String>();
	
	private Date m_lastStockDataPullDate;
	
		// accessor methods
	public String[] getAllStockTickers() { 
		
		String[] toReturn = new String[m_allTickers.size()];
		
		toReturn = m_allTickers.toArray(toReturn);
		
		return toReturn; 
	
	}
	
	public int getNumStocks() { return m_allTickers.size(); }
	
	public String[] getGoodStockTickers() { 
	
		String[] toReturn = new String[m_goodStockTickers.size()];
		
		toReturn = m_goodStockTickers.toArray(toReturn);
		
		return toReturn; 
	
	}
	public int getNumGoodStocks() { return m_goodStockTickers.size(); }
	
	public Date getLastDataPullDate() { return m_lastStockDataPullDate; }
	public void setLastDataPullDate(Date d) { m_lastStockDataPullDate = d; }
	
		// instance methods
	
	public void addGoodStock(String ticker) {
	
		m_goodStockTickers.add(ticker);
		
	}	// addGoodStock
	
	public void addStock(String ticker) {
		
		m_allTickers.add(ticker);
		
	}	// addStock
	
	public void clearGoodStockList() { m_goodStockTickers.clear(); }
}
