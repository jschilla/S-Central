/**
 * 
 */
package stockcentral;

import java.util.Calendar;
import java.io.PrintWriter;

/**
 * @author jschilla
 *
 */
public class Trade {

	private float m_windPrice, m_unwindPrice;
	private float m_tradePerformance;
	private String m_ticker, m_strategyName;
	private Calendar m_windDate, m_unwindDate;
	
	private float[] m_extraInfo = new float[0];
	private String[] m_extraInfoTitles = new String[0];
	
	public Trade(Calendar windDate, float windPrice, String ticker, String strategyName) {
		
		m_windDate = windDate;
		m_windPrice = windPrice;
		
		m_ticker = ticker;
		m_strategyName = strategyName;
		
	}	// ctor
	
	public Trade(Calendar a, float b, String c, String d, float[] extraInfo, String[] infoTitles) {
		
		this(a, b, c, d);
		
		m_extraInfo = extraInfo;
		m_extraInfoTitles = infoTitles;
		
	}	// ctor
	
	public void closeTrade(Calendar unwindDate, float unwindPrice) {
		
		m_unwindDate = unwindDate;
		unwindPrice = unwindPrice;
		
	}
	
	public void calculatePerformance (boolean bullish) {
		
		if (bullish)
			m_tradePerformance = (m_unwindPrice - m_windPrice) / m_windPrice;
		else
			m_tradePerformance = -((m_unwindPrice - m_windPrice) / m_windPrice);
	
	}
	
	public void printTrade (PrintWriter out) {
	
		out.print(m_ticker);
		out.print(',');
		out.print(m_strategyName);
		out.print(',');
		out.print(StockCentral.translateCalendarToString(m_unwindDate));
		out.print(',');
		out.print(m_unwindPrice);
		out.print(StockCentral.translateCalendarToString(m_windDate));
		out.print(m_windPrice);
		out.print(',');
		out.print(m_tradePerformance);
		out.println('%');
		
	}
	
}
