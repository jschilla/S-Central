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

public class MacDSignalData {

	public static final int OPEN_INDEX = 0;
	public static final int RSI_REVERSAL_INDEX = 1;
	public static final int MACD_REVERSE_CROSSOVER_INDEX = 2;
	public static final int MACD_REVERSE_INDEX = 3;
	public static final int CLOSE_INDEX = 4;

	private static final float RETREAT_VARIANCE = 0.02f;

	private float[] m_RSIs = new float[5], m_gapFromEMA = new float[5], m_price = new float[5], m_delta = new float[5];
	private Calendar[] m_dates = new Calendar[5];
	private float[] m_macds = new float[5];
	private int[] m_numDays = new int[5];
	
	public void assessBullishCrossover(StockData sd, int startIndex, MacD baseMacD) {
		
			// First, let's only proceed if there has been a bullish cross over of the MacD.
		MacD macds = sd.getMacD9269();
		double[] macd = baseMacD.getHistogram();
		double[] macdHigh = baseMacD.getMacD();
		float[] closes = sd.getCloses();
		float[] opens = sd.getOpens();
		float[] lows = sd.getLows();
		double[] emas = sd.get200DayEMA();
		float[] rsis = sd.get10DayRSI();
		Calendar[] dates = sd.getDates();
		
		if ((macd[startIndex] > 0) && (macd[startIndex + 1] < 0)) {
			
				// If there was a bullish crossover, we calculate the RSI reversal, the reverse crossover and the 
				// close positions.  But first, we calculate the info for the opening day.
			m_RSIs[OPEN_INDEX] = rsis[startIndex];
			m_gapFromEMA[OPEN_INDEX] = closes[startIndex] - (float)emas[startIndex];
			if (startIndex > 0)
				m_price[OPEN_INDEX] = opens[startIndex - 1];
			m_delta[OPEN_INDEX] = 0;
			m_dates[OPEN_INDEX] = dates[startIndex];
			m_macds[OPEN_INDEX] = (float)macd[startIndex];
			m_numDays[OPEN_INDEX] = 0;
			
			try {
					
					// OK, now let's figure out when the RSI goes into oversold territory.  We start on the day of the bullish
					// MacD crossover and continue forward.
				boolean rsiBullish = false;
				int rsiStepForward = 0;
				
				while (!rsiBullish) {
					
					if ((rsis[startIndex - rsiStepForward] > 70) || 
							(lows[startIndex - rsiStepForward] < (closes[startIndex] * (1 - RETREAT_VARIANCE))))
						rsiBullish = true;
					else
						rsiStepForward++;
					
					if ((startIndex - rsiStepForward) < 0) {
						
						rsiBullish = true;
						rsiStepForward--;
						
					}
					else if (rsiStepForward > 20)
						rsiBullish = true;
					
				}
				
				m_RSIs[RSI_REVERSAL_INDEX] = rsis[startIndex - rsiStepForward];
				m_gapFromEMA[RSI_REVERSAL_INDEX] = closes[startIndex - rsiStepForward] - 
						(float)emas[startIndex - rsiStepForward];
				m_price[RSI_REVERSAL_INDEX] = closes[startIndex - rsiStepForward];
				m_delta[RSI_REVERSAL_INDEX] = ((closes[startIndex - rsiStepForward] - closes[startIndex]) / 
						closes[startIndex]) * 100;
				m_dates[RSI_REVERSAL_INDEX] = dates[startIndex - rsiStepForward];
				m_macds[RSI_REVERSAL_INDEX] = (float)macd[startIndex - rsiStepForward];
				m_numDays[RSI_REVERSAL_INDEX] = rsiStepForward;
				
					// Now we figure out about the cross back over of the MacD.
				boolean macDCrossback = false;
				int macdStepForward = 1;
				
				while (!macDCrossback) {
					
					if ((startIndex - macdStepForward) < 0) {
						
						macDCrossback = true;
						macdStepForward--;

					}
					else if (macd[startIndex - macdStepForward] < 0)
						macDCrossback = true;
					else
						macdStepForward++;
					
				}
				
				m_RSIs[MACD_REVERSE_CROSSOVER_INDEX] = rsis[startIndex - macdStepForward];
				m_gapFromEMA[MACD_REVERSE_CROSSOVER_INDEX] = closes[startIndex - macdStepForward] - 
						(float)emas[startIndex - macdStepForward];
				m_price[MACD_REVERSE_CROSSOVER_INDEX] = closes[startIndex - macdStepForward];
				m_delta[MACD_REVERSE_CROSSOVER_INDEX] = ((closes[startIndex - macdStepForward] - closes[startIndex]) / 
						closes[startIndex]) * 100;
				m_dates[MACD_REVERSE_CROSSOVER_INDEX] = dates[startIndex - macdStepForward];
				m_macds[MACD_REVERSE_CROSSOVER_INDEX] = (float)macd[startIndex - macdStepForward];
				m_numDays[MACD_REVERSE_CROSSOVER_INDEX] = macdStepForward;
				
					// OK, now I'm adding one where the MacD histogram gets lower than the previous day.
				boolean histogramUp = true;
				int histogramStepForward = 1;
				
				while (histogramUp) {
					
					if (macd[startIndex - histogramStepForward] < macd[(startIndex - histogramStepForward) + 1])
						histogramUp = false;
					else 
						histogramStepForward++;
					
				}
	
				m_RSIs[MACD_REVERSE_INDEX] = rsis[startIndex - histogramStepForward];
				m_gapFromEMA[MACD_REVERSE_INDEX] = closes[startIndex - histogramStepForward] - 
						(float)emas[startIndex - histogramStepForward];
				m_price[MACD_REVERSE_INDEX] = closes[startIndex - histogramStepForward];
				m_delta[MACD_REVERSE_INDEX] = ((closes[startIndex - histogramStepForward] - closes[startIndex]) / 
						closes[startIndex]) * 100;
				m_dates[MACD_REVERSE_INDEX] = dates[startIndex - histogramStepForward];
				m_macds[MACD_REVERSE_INDEX] = (float)macd[startIndex - histogramStepForward];
				m_numDays[MACD_REVERSE_INDEX] = histogramStepForward;

				
					// Finally, do it for the reversal of the trend.  For now at least, this occurs when there's
					// a retread to less than 2% under a previous high close.
				boolean stillBullish = true;
				int bullishStepForward = 1;
				float previousHigh = closes[startIndex];
				
				while (stillBullish) {
					
					if (lows[startIndex - bullishStepForward] < (previousHigh * (1 - RETREAT_VARIANCE)))
						stillBullish = false;
					else {
						
						if (closes[startIndex - bullishStepForward] > previousHigh)
							previousHigh = closes[startIndex - bullishStepForward];
						
						bullishStepForward++;
						
						if ((startIndex - bullishStepForward) < 0) {
							
							stillBullish = false;
							bullishStepForward--;
							
						}

						
					}
					
				}
	
				m_RSIs[CLOSE_INDEX] = rsis[startIndex - bullishStepForward];
				m_gapFromEMA[CLOSE_INDEX] = closes[startIndex - bullishStepForward] - 
						(float)emas[startIndex - bullishStepForward];
				m_price[CLOSE_INDEX] = closes[startIndex - bullishStepForward];
				m_delta[CLOSE_INDEX] = ((closes[startIndex - bullishStepForward] - closes[startIndex]) / 
						closes[startIndex]) * 100;
				m_dates[CLOSE_INDEX] = dates[startIndex - bullishStepForward];
				m_macds[CLOSE_INDEX] = (float)macd[startIndex - bullishStepForward];
				m_numDays[CLOSE_INDEX] = bullishStepForward;

			}
			catch (ArrayIndexOutOfBoundsException e) {
				
				e.printStackTrace();
				
			}
			
			
		}	// if there's a bullish MacD crossover.
		
		
	}	// assessBullishCrossover
	
	private static final String CSV_FORMAT = "Open Date,Close Date,Open Price,Close Price,Delta,MacD,RSI,EMA Gap,# of Days";
	
	public static final String getHeaderForCSV() { return CSV_FORMAT; }
	
	public String outputDataInCSV(int index) {
		
		StringBuffer toReturn = new StringBuffer();
		
		if (m_dates[index] == null)
			System.out.println("WTF?");
		
		toReturn.append(StockCentral.translateCalendarToString(m_dates[OPEN_INDEX]));
		toReturn.append(',');
		toReturn.append(StockCentral.translateCalendarToString(m_dates[index]));
		toReturn.append(',');
		toReturn.append(m_price[OPEN_INDEX]);
		toReturn.append(',');
		toReturn.append(m_price[index]);
		toReturn.append(',');
		toReturn.append(m_delta[index]);
		toReturn.append(',');
		toReturn.append(m_macds[index]);
		toReturn.append(',');
		toReturn.append(m_RSIs[index]);
		toReturn.append(',');
		toReturn.append(m_gapFromEMA[index]);
		toReturn.append(',');
		toReturn.append(m_numDays[index]);
		
		return toReturn.toString();
		
	}
	
	private static final String CSV_FORMAT2 = 
			"Open Date,Open Price,RSI Date,RSI Close,RSI Delta,RSI,RSI #/Days,MacD Reversal Date,MacD Reversal Delta,MacD Crossback Date," +
			"MacD Crossback Delta,Price Reversal Date,Price Reversal Delta";
	
	public static final String getHeader2ForCSV() { return CSV_FORMAT2; }

	public String outputAllDataInCSV() {
		
		StringBuffer toReturn = new StringBuffer();
		
		toReturn.append(StockCentral.translateCalendarToString(m_dates[OPEN_INDEX]));
		toReturn.append(',');
		toReturn.append(m_price[OPEN_INDEX]);
		toReturn.append(',');
		toReturn.append(StockCentral.translateCalendarToString(m_dates[RSI_REVERSAL_INDEX]));
		toReturn.append(',');
		toReturn.append(m_price[RSI_REVERSAL_INDEX]);
		toReturn.append(',');
		toReturn.append(m_delta[RSI_REVERSAL_INDEX]);
		toReturn.append(',');
		toReturn.append(m_RSIs[RSI_REVERSAL_INDEX]);
		toReturn.append(',');
		toReturn.append(m_numDays[RSI_REVERSAL_INDEX]);
		toReturn.append(',');
		toReturn.append(StockCentral.translateCalendarToString(m_dates[MACD_REVERSE_INDEX]));
		toReturn.append(',');
		toReturn.append(m_delta[MACD_REVERSE_INDEX]);
		toReturn.append(',');
		toReturn.append(StockCentral.translateCalendarToString(m_dates[MACD_REVERSE_CROSSOVER_INDEX]));
		toReturn.append(',');
		toReturn.append(m_delta[MACD_REVERSE_CROSSOVER_INDEX]);
		toReturn.append(',');
		toReturn.append(StockCentral.translateCalendarToString(m_dates[CLOSE_INDEX]));
		toReturn.append(',');
		toReturn.append(m_delta[CLOSE_INDEX]);
		
		return toReturn.toString();
		
	}
	
	public void outputAllDayInCSV (PrintWriter out) {
		
		out.println(CSV_FORMAT);
		
		for (int count = 0; count < 5; count++) {
			
			out.println(outputDataInCSV(count));
			
		}
		
		out.println();
		out.println();
		
	}
	
}
