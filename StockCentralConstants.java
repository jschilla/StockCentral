package stockcentral;


/**
 * This interface just contains a bunch of constants used by the StockCentral classes.
 * @author Jack Schillaci
 * @version Build 1/15/2007
 *
 */
public interface StockCentralConstants {

		// constants for the StockDataLoader class
	public static final float MINIMUM_LAST_PRICE = 5.0f;

	//	default constants for oscillators and such stuff.

	public static final int DEFAULT_RSI_PERIODS = 14;

	public static final int DEFAULT_AROON_PERIOD = 10;

	// constants from the StockCentral class

	public static final String DEBUG_FILENAME_PREFIX = "debug";
	public static final String DATA_MONITOR_FILENAME_PREFIX = "data";

	public static final boolean DEBUG_ON = true;
	public static final boolean DATA_MONITORING_ON = true;

	// constants from the StockChart class

	public static final String CHARTFILE_PREFIX = "chartfile_";
	public static final String CHARTFILE_SUFFIX = ".jpg";

	// constants from the StockData class

	public static final int TRADING_DAYS_IN_THREE_MONTHS = 60;

	// constants for serializing and deserializing the StockData class.

	public static final String SAVEFILE_PREFIX = "stockdata_";
	public static final String SAVEFILE_SUFFIX = ".sav";
	public static final String STOCK_CENTRAL_INFO_FILENAME = "stockcentralinfo.inf";
	public static final String STOCKDATA_DIRECTORY = "stockdata";

	public static final String STOCK_RESULTS_FILE = "stock_central_results_";
	public static final String STOCK_RESULTS_EXTENSION = ".csv";

	public static final String STOCK_MONITOR_FILE = "stock_monitor.sav";

	public static final String STOCK_MONITOR_RESULTS_FILE = "stock_monitor_results_";
	public static final String STOCK_MONITOR_RESULTS_EXTENSION = ".txt";

	// constants for saving the chart files.

	public static final String CHARTFILE_DIRECTORY = "charts";

	// constants for interpretting candlestick patterns.

	public static final float DOJI_SHARE_OF_VOLATILITY = 0.05f;
	public static final float DEFAULT_DOJI = 0.1f;
	public static final float SPINNING_TOP_MAX = 0.2f;
	public static final float HIGH_WAVE_SHARE_OF_VOLATILITY = 0.75f;
	public static final float HOLD_SMALL_SIDE_SHARE = 0.1f;
	public static final float HOLD_LARGE_SIDE_SHARE = 0.2f;
	public static final float HOLD_SHARE_OF_VOLATILITY = 0.75f;
	public static final float HAMMER_SHARE_OF_REAL_BODY = 2.0f;
	public static final float COUNTER_ATTACK_MAX = 0.1f;
	public static final float HARAMI_SHARE_OF_VOLATILITY = 1.0f;
	public static final float TWEEZERS_SHARE_OF_VOLATILITY = 0.05f;

		// these constants are used by the id methods.

	public static final int LONG_TERM_TREND_INDICATOR_LENGTH = 200;
		// 200 days -- about a year.
	public static final int SHORT_TERM_TREND_INDICATOR_LENGTH = 100;
		// 50 days -- about 3 months.
	public static final int RECENT_TREND_INDICATOR_LENGTH = 10;
		// 10 days -- 2 weeks.

	public static final float AROON_MINIMUM_FOR_LONG_TERM_TREND = 50.0f;
	public static final float AROON_MINIMUM_FOR_SHORT_TERM_TREND = 50.0f;
		// the short term aroon can't fall below 80, which basically means there can be no
		// more than one day of a temporary pullback before we call off the possibility
		// of a trend.
	public static final float AROON_MINIMUM_FOR_RECENT_TREND = 80.0f;

		// 100 days -- six months.
	public static final int MINIMUM_PERIOD_FOR_LONG_TERM_TREND = 200;
	public static final int MINIMUM_PERIOD_FOR_SHORT_TERM_TREND = 100;
	public static final int MINIMUM_PERIOD_FOR_RECENT_TREND = 5;

	// over a long term trend, the stock price must move at least 10%
	// for every six months.
	public static final float LONG_TERM_TREND_RATE_MINIMUM = 0.2f;
	public static final float SHORT_TERM_TREND_RATE_MINIMUM = 0.1f;
	public static final float RECENT_TREND_RATE_MINIMUM = 0.02f;

		// this constant is used to generate a URL for Yahoo! Charts

	public static final String YAHOO_CHARTS_URL =
		"http://finance.yahoo.com/charts#chart5:range=3m;indicator=sma+volume+macd+rsi;" +
		"charttype=candlestick;crosshair=on;logscale=on;source=undefined;symbol=";

		// this constant is the list of stocks that are pulled.

	public static final String[] STOCK_INDICES_AND_HOLDERS = { "BBH", "BDH", "BHH", "EKH", "HHH", "IAH",
		"IIH", "MKH", "OIH", "PPH", "RKH", "RTH", "SMH", "SWH", "TTH", "UTH", "WMH", "SPY" };

	public static final String[] ETFS = { "DIA", "IYM", "IYD",
		"IYC", "IYK", "IYE", "IYF", "IYG", "IYH", "IYJ",
		"IYV", "IYR", "IYW", "IYZ", "IYY", "IDU", "EWA",
		"EWO", "EWK", "EWZ", "EWC", "EWQ", "EZU", "EWG",
		"EWH", "EWI", "EWJ", "EWM", "EWW", "EWN", "EWS",
		"EWP", "EWY", "EWD", "EWL", "EWT", "EWU", "IWB",
		"IWF", "IWD", "IWM", "IWO", "IWN", "IWV", "IWZ",
		"IWW", "IVV", "IVW", "IVE", "IEV", "IJH", "IJK",
		"IJJ", "IJR", "IJT", "IJS", "IKC", "QQQ", "SPY",
		"XLB", "XLV", "XLP", "XLY", "XLE", "XLF", "XLI",
		"MDY", "XLK", "XLU", "BHH", "BBH", "BDH", "HHH",
		"IAH", "IIH", "PPH", "RKH", "SMH", "TBH", "TTH",
		"UTH", "VXX", "VXZ" };

	public static final String[] STOCK_LIST = { "GOOG", "GS", "C", "MSFT", "IBM", "QQQQ", "^DJI",
			"JNPR", "GOOG", "INTC", "GS", "C", "MFLX", "AAPL",
	        "ADBE", "AMZN", "DISH", "AMD", "YHOO", "BEBE", "BAC", "BK",
	        "PDLI", "DELL", "ACI", "CNX", "MEE", "BTU", "ELX", "QLGC",
	        "WDC", "CL", "IP", "MO", "PG", "BA", "LLL", "LMT", "NOC", "RTN", "ATVI", "IGT", "MGAM", "PENN",
	        "THQI", "AU", "ABX", "FCX", "NEM", "ABI", "AFFX", "ALKS", "AMGN", "BIIB", "CRA", "DNA", "ENZN", "GENZ",
	        "GILD", "HGSI", "ICOS", "MEDI", "MLNM", "QLTI", "SEPR", "SHPGY", "AGR", "AMCC", "BRCM", "CIEN", "CMVT",
	        "CNXT", "GLW", "JDSU", "MOT", "MSPD", "NT", "PMCS", "QCOM", "RFMD", "SCMR", "SWKS", "TLAB", "TUTS",
	        "AGIL", "ARBA", "CKFR", "ICGE", "VERT", "AEG", "ALA", "ARMHY", "ASMI", "AXA", "BKHM",
	        "BOBJ", "BP", "DCX", "DEO", "DOX", "ELN", "GSK", "IFX", "ING", "IONA", "MICC", "NOK",
	        "PHG", "QGEN", "REP", "RYAAY", "SAP", "SKIL", "SNY", "SPI", "SRA", "STM", "TEF", "TOT", "UBS", "UN",
	        "VOD", "WPPGY", "AMTD", "CMGI", "CNET", "EBAY", "ELNK", "ET", "MFE", "PCLN", "RNWK", "TWX",
	        "ADPT", "COMS", "CSCO", "EXTR", "FDRY", "GTW", "HPQ", "MCDTA", "NAPS",
	        "NTAP", "SUNW", "SYMC", "UIS", "AKAM", "INSP", "NAVI", "OPWV", "VIGN", "VITR",
	        "VRSN", "AIG", "AV", "AZN", "BLS", "BMY", "BT", "CBS", "CMCSA", "DT", "EMC", "FTE", "GE",
	        "HD", "IBM", "JNJ", "KO", "LLY", "LU", "MHS", "MRK", "MS", "NT", "NTT", "NVS", "ORCL", "PFE", "Q",
	        "SNE", "STA", "SYT", "TM", "TXN", "XOM", "BHI", "BJS", "CAM", "DO", "ESV", "GRP", "GSF",
	        "HAL", "HC", "NBR", "NE", "NOV", "RDC", "RIG", "SII", "SLB", "TDW", "WFT", "ABT", "ADRX", "AGN", "BVF",
	        "EYE", "FRX", "HSP", "KG", "MYL", "SGP", "WYE", "ZMH", "ASO", "BBT", "CMA", "FITB", "JPM",
	        "KEY", "MEL", "MI", "NCC", "NTRS", "PJC", "PNC", "SNV", "STT", "USB", "WB", "WFC", "ADI", "ALTR", "AMAT",
	        "ATML", "KLAC", "LLTC", "LSI", "MU", "MXIM", "NSM", "NVLS", "SNDK", "TER", "VTSS", "XLNX",
	        "BMC", "CA", "CHKP", "INTU", "NUAN", "SAP", "SAPE", "SYMC", "TIBX", "AT", "BCE", "CBB",
	        "CTL", "LVLT", "Q", "T", "TDS", "VZ", "AEP", "CNP", "D", "DYN", "DUK", "EIX", "EP", "ETR", "EXC",
	        "FE", "FPL", "PEG", "RRI", "SO", "WMB", "BBY", "COST", "CVS", "FD", "GPS", "KR", "KSS", "LOW", "LTD",
	        "RSH", "SWY", "TGT", "TJX", "WAG", "WMT", "^DJI", "QQQQ", "^FTSE", "^IXIC", "^GSPC", "^N225", "^HSI" };

		// these are constants for the StockTrend class (used to be an enum)
	public static final int CONSOLIDATION = 0;
	public static final int BEARISH = -1;
	public static final int BULLISH = 1;

		// these are constants for the StockMonitorList
	public static final String DJ_TICKER = "^DJI";
	public static final String NASDAQ_TICKER = "^IXIC";
	public static final String SP500_TICKER = "^GSPC";

}	// class StockCentralConstants
