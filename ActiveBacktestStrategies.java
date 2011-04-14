/**
 *
 */
package stockcentral;

/**
 * @author Jack Schillaci
 * @version Build 2/16/2010
 */
public class ActiveBacktestStrategies {

	private static final BacktestStrategies[] ACTIVES = {

		new MacDSignalCrossoverStrategies()

		//new TwoDayFourDayRSIStrategies()

		/*new MultipleExtremeRSIStrategy(), new MultipleOverboughtRSIStrategies(),
		new CumulativeRSIStrategies(), new DoubleStrategies(),
		new DoubleOverboughtStrategies()//, new ExtremeOverboughtFourDayRSIStrategies()
*/
	};

	public static final BacktestStrategies[] getStrategies() { return ACTIVES; }

}
