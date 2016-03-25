/**
 * 
 */
package stockcentral;

import java.util.*;
import java.io.PrintWriter;

/**
 * @author jschilla
 *
 */
public class TradeStatisticsPackage {

	Vector m_trades = new Vector();
	
	public void addTrade(Trade trade) {
		
		m_trades.add(trade);
		
	}
	
	public void outputTrades(PrintWriter out) {
		
		Enumeration trades = m_trades.elements();
		
		out.println("Ticker,Strategy,Wind Date,Wind Price,Unwind Date,Unwind Price,Delta");
		
		while (trades.hasMoreElements()) {
			
			Trade nextTrade = (Trade)trades.nextElement();
			nextTrade.printTrade(out);
			
		}
		
	}	// outputTrades
	
}
