/**
 * 
 */
package stockcentral;

import java.io.*;

/**
 * This object just runs all of the components of the StockCentral environment.
 * 
 * @author jschilla
 *
 */
public class SCStrategyLearnerMacro {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String tickerChoice = "", timelineChoice = "";
		String[] tickerFileName = new String[1];
		char timeline;
		
		LineNumberReader in = new LineNumberReader(new InputStreamReader(System.in));
		
		System.out.println("Welcome to the SC Strategy Learner!\n");
		System.out.println("Please select your ticker list:  ");
		System.out.println("1 - S&P 500\n2 - Russell 3000\n3 - ETF List\n4 - Dow 30");
		
		try {
			tickerChoice = in.readLine();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		if (tickerChoice.equals("1"))
			tickerFileName[0] = StockTickerLoader.SP500_FILE_NAME;
		else if (tickerChoice.equals("2"))
			tickerFileName[0] = StockTickerLoader.RUSSEL3000_FILE_NAME;
		else if (tickerChoice.equals("3"))
			tickerFileName[0] = StockTickerLoader.ETF_FILE_NAME;
		else if (tickerChoice.equals("4"))
			tickerFileName[0] = StockTickerLoader.DOW_FILE_NAME;
		else {
			
			System.out.println("Proceeding with default.");
			tickerFileName[0] = StockTickerLoader.DEFAULT_FILE_NAME;
			
		}
		
		System.out.println("Please select your timeframe:\n1 - Weekly\n2 - Daily");
		
		try {
			timelineChoice = in.readLine();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		if (timelineChoice.equals("1"))
			timeline = 'w';
		else if (timelineChoice.equals("2"))
			timeline = 'd';
		else {
			
			System.out.println("Defaulting to daily.");
			timeline = 'd';
			
		}
		
		System.out.println();
		
		System.out.println();
		
		StockTickerLoader.main(tickerFileName);
		
		System.out.println();
		
		YahooStockDataGrabber.setTimeFrame(timeline);
		StockDataLoader.main(null);
		
		System.out.println();
		
		SCStrategyLearner.main(null);
		
		System.out.println();
		System.out.println("Now, scanning most recent daily data:\n");
		
		Scanner.main(null);
		
	}

}
