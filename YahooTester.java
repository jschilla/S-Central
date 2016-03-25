/**
 * 
 */
package stockcentral;

import yahoofinance.*;
import yahoofinance.histquotes.*;

import java.io.IOException;
import java.util.*;

/**
 * @author jschilla
 *
 */
public class YahooTester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		//YahooFinance finance = new YahooFinance();
		
        Calendar today = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        startDate.set(Calendar.YEAR, (today.get(Calendar.YEAR)));
        startDate.set(Calendar.MONTH, (today.get(Calendar.MONTH) - 6));
        startDate.set(Calendar.DAY_OF_MONTH, (today.get(Calendar.DAY_OF_MONTH)));

		try {
			Stock google = YahooFinance.get("GOOG", startDate, Interval.DAILY);
			
			List googleData = google.getHistory();
			
			for (int count = 0; count < googleData.size(); count++) {
				
				HistoricalQuote quote = (HistoricalQuote) googleData.get(count);
				
				double close = quote.getClose().doubleValue();
				Calendar date = quote.getDate();
				
				System.out.println("GOOG on " + StockCentral.translateCalendarToString(date) + ":  " + close);
				
			}
			
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

}
