/**
 *
 */
package stockcentral;

/**
 * @author Jack Schillaci
 *
 */
public class OscillatorPackage {

	private StockData m_data;

	private float[] m_rsi;

	public OscillatorPackage(StockData sd) {

		m_data = sd;

	}	// ctor (StockData)

	public float[] getRSIs() {

		if (m_rsi == null) {

			m_rsi = StockCentral.calculateRSI(m_data.getCloses());

		}	// if

		return m_rsi;

	}	// getRSIs

}
