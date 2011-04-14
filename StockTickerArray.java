/**
 *
 */
package stockcentral;

import java.util.Vector;
import java.util.*;
import java.io.Serializable;

/**
 * @author Jack Schillaci
 * @version Build 2/16/2010
 *
 */
public class StockTickerArray extends ArrayList<String> implements Serializable {

	Calendar m_creationDate;

	public StockTickerArray() {

		m_creationDate = new GregorianCalendar();

	}
}
