/**
 * 
 */
package stockcentral;

import java.io.PrintWriter;
import java.util.*;

/**
 * @author jschilla
 *
 */
public class SCRSIShort extends SCLearningModule {

	private final static int DEFAULT_INVESTMENT_HORIZON = 3;
	private final static int DEFAULT_RSI_PERIODS = 14;
	private static final double DEFAULT_CONFIDENCE_LEVEL = 0.8;
	
	private int m_investmentHorizon, m_rsiPeriods;
	private double m_confidenceLevel;
	
	//private static final boolean DEBUG_OUTPUT = false;
	private static final String DEBUG_FOLDER = "debug";
	
	public SCRSIShort() {
		
		m_investmentHorizon = DEFAULT_INVESTMENT_HORIZON;
		m_confidenceLevel = DEFAULT_CONFIDENCE_LEVEL;
		m_rsiPeriods = DEFAULT_RSI_PERIODS;
		
	}	// default ctor
	
	public SCRSIShort (int horizon, double confidence) {
		
		m_investmentHorizon = horizon;
		m_confidenceLevel = confidence;
		m_rsiPeriods = DEFAULT_RSI_PERIODS;
		
	}
	
	public SCRSIShort (int horizon, double confidence, int rsiPeriods) {
		
		this (horizon, confidence);
		m_rsiPeriods = rsiPeriods;
		
	}
	
	public String getModuleMnemonic() {
		
		String toReturn = "RSIBear" + Integer.toString(m_investmentHorizon) + Integer.toString(m_rsiPeriods);
		
		return toReturn;
		
	}	// getModuleMnemonic
	
	public boolean isBullish() { return false; }
	
		// What this learning module does is that it determines the RSI level at which 80% of the time there is
		// a positive return after two trading days.  This is accomplished as follows:
		// 1. 	The method runs through each day's RSI and measures the two-day return for each day.
		// 2.	It then runs the RSI numbers from 100 to 1, and determines how often RSIs at that level or lower
		// 		resulted in a positive return after two days.
	public SCLearningModuleData learnOptimalTrade(StockData sd) {
		 
		SCRSIBearishModuleData toReturn = new SCRSIBearishModuleData();
		toReturn.setModule(this);
		
			// First, run through each day's RSI and figure out the two-day return.
		float[] closes = sd.getCloses();
		float[] rsis = StockCentral.calculateRSI(closes, m_rsiPeriods);
		Calendar[] dates = sd.getDates();
		
		PrintWriter debugOut, tradeListOut;
		
		//if (DEBUG_OUTPUT)
			//System.out.println("Ticker:  " + sd.getTicker());
		
		if (DEBUG_OUTPUT) {
			
			String debugFileName = System.currentTimeMillis() + "_DebugForShort_" + m_investmentHorizon + "_" + m_confidenceLevel + 
					"_" + m_rsiPeriods + "_" + sd.getTicker() + ".csv";
			debugOut = StockCentral.createOutputFile(debugFileName, DEBUG_FOLDER);
			
			debugOut.println("Ticker:  " + sd.getTicker() + 
					",RSI, % Successful Matches (two days),Average Performance (on successful days)," +
					"Average Performance (on all match days),Frequency of that RSI Level");
			
			String tradeListFileName = System.currentTimeMillis() + "_TradesForShort_" + m_investmentHorizon + "_" + m_confidenceLevel + 
					"_" + m_rsiPeriods + "_" + sd.getTicker() + ".csv";
			tradeListOut = StockCentral.createOutputFile(tradeListFileName, DEBUG_FOLDER);
			
			tradeListOut.println("Ticker:  " + sd.getTicker() + ",Date,Close,RSI,Three-Day Return");
			
		}

		double[] twoDayDeltas = new double[closes.length];
		double[] twoDayDeltaPercentages = new double[closes.length];
		
		for (int count = m_investmentHorizon; count < rsis.length; count++) {
			
			twoDayDeltas[count] = closes[count - m_investmentHorizon] - closes[count];
			twoDayDeltaPercentages[count] = twoDayDeltas[count] / closes[count];
			
			if (DEBUG_OUTPUT) {
				
				tradeListOut.println("," + StockCentral.translateCalendarToString(dates[count]) + "," +
						closes[count] + "," + rsis[count] + "," + (twoDayDeltaPercentages[count] * 100) + "%");
				
			}
			
			/*if (DEBUG_OUTPUT)
				System.out.println(StockCentral.translateCalendarToString(dates[count]) + " -- " + 
						twoDayDeltas[count] + " -- " + twoDayDeltaPercentages[count]);*/
			
		}
		
			// Next, we need to go through every number 1 - 100 and figure out how often that RSI results in a two-day
			// positive return.
			// This is accomplished by going through each number, and for each counting the number of times that there
			// are positive two-day returns.  The results of this analysis are put into a Hasthable.  
			// This could also be done by first sorting all of RSI/Two-Day Returns and then just going up the list.
		Hashtable<Integer, Double> rsiAnalysis = new Hashtable<Integer, Double>(), 
				averagePerformanceOnSuccessfulDays = new Hashtable<Integer, Double>(), 
				averagePerformanceOnAllDays = new Hashtable<Integer, Double>(),
				frequency = new Hashtable<Integer, Double>();
		
			// Outer loop:  go through each round whole RSI number
		for (int countRSIs = 100; countRSIs > 0; countRSIs--) {
			
				// First inner loop:  isolate all RSIs below countRSIs.
			Hashtable<Integer, Float> rsisAboveCount = new Hashtable<Integer, Float>();		// this should just be a stack.
			
			for (int countDays = m_investmentHorizon; countDays < rsis.length; countDays++) {
				
					// if this day's RSI is below the countRSI, then we put its index number in the hashtable.
				if (rsis[countDays] > countRSIs)
					rsisAboveCount.put(countDays, rsis[countDays]);
			}
			
				// Second inner loop:  calculate the average return across all of these RSIs.
			int numberOfMatches = rsisAboveCount.size();
			int numberOfSuccessfulMatches = 0;
			double percentageOfSuccessfulMatches = 0.0;
			
			Enumeration matchedIndexes = rsisAboveCount.keys();
			double totalChangeOnSuccessfulDays = 0.0, totalChangeOnAllMatches = 0.0;

			
			while (matchedIndexes.hasMoreElements()) {
				
				int nextMatch = (Integer)matchedIndexes.nextElement();
				
				totalChangeOnAllMatches += twoDayDeltaPercentages[nextMatch];
				
				if (twoDayDeltas[nextMatch] < 0) {
					
					numberOfSuccessfulMatches++;

					totalChangeOnSuccessfulDays += twoDayDeltaPercentages[nextMatch];
				}
				
			}
			
			if (numberOfMatches > 0)
				percentageOfSuccessfulMatches = (double)numberOfSuccessfulMatches / (double)numberOfMatches;
			
			rsiAnalysis.put((countRSIs - 1), percentageOfSuccessfulMatches);
			averagePerformanceOnSuccessfulDays.put((countRSIs - 1), (totalChangeOnSuccessfulDays / numberOfSuccessfulMatches));
			averagePerformanceOnAllDays.put((countRSIs - 1), (totalChangeOnAllMatches / numberOfMatches));
			frequency.put((countRSIs - 1), (double)(numberOfMatches / (double)rsis.length));
			
			
		}
		
			// Second outer loop:  start at the bottom and figure out when's the first time we get a 
			// two-day return > 80%.
		int optimalRSI = 0;
		boolean keepScanning = true;
		
		for (int countRSIsBackwards = 0; (countRSIsBackwards < 100) && (keepScanning); countRSIsBackwards++) {
			
			double twoDaySuccessRate = rsiAnalysis.get(countRSIsBackwards);
			
			if (twoDaySuccessRate >= m_confidenceLevel) {
				
				keepScanning = false;
				optimalRSI = countRSIsBackwards;

			}

			if (DEBUG_OUTPUT)
				debugOut.println("," + countRSIsBackwards + "," + (twoDaySuccessRate * 100) + "%," + 
						(averagePerformanceOnSuccessfulDays.get(countRSIsBackwards) * 100) + "%," + 
						(averagePerformanceOnAllDays.get(countRSIsBackwards) * 100) + "%," +
						(frequency.get(countRSIsBackwards) * 100) + "%");
				
			
/*				System.out.println("RSI:  " + countRSIsBackwards + ";  % Successful Matches:  " + twoDaySuccessRate +
						";  Average Performance:  " + averagePerformance.get(countRSIsBackwards) + 
						";  Frequency of RSI Level:  " + frequency.get(countRSIsBackwards));
*/
			
		}
		
		if (optimalRSI != 0) {
			
			toReturn.setOptimalLevel(optimalRSI);
			toReturn.setHorizon(m_investmentHorizon);
			toReturn.setReturnSuccessfulDays(averagePerformanceOnSuccessfulDays.get(optimalRSI));
			toReturn.setReturnAllDays(averagePerformanceOnAllDays.get(optimalRSI));
			toReturn.setTicker(sd.getTicker());
			toReturn.setFrequency(frequency.get(optimalRSI));

		}
		else {

			toReturn.setOptimalLevel(100);
			toReturn.setHorizon(m_investmentHorizon);
			toReturn.setReturnSuccessfulDays(0);
			toReturn.setTicker(sd.getTicker());
			toReturn.setFrequency(0);
			
		}
		
		toReturn.setConfidenceLevel(m_confidenceLevel);
		toReturn.setRSIPeriods(m_rsiPeriods);
		
		if (DEBUG_OUTPUT) {
			
			debugOut.close();
			
			tradeListOut.close();

		}

		return toReturn;
	
	}
	
	public boolean isOptimalTrade(StockData sd, SCLearningModuleData optimalAnalysis, int lookback) { 
		
		boolean toReturn = false;

		SCRSIBearishModuleData data = (SCRSIBearishModuleData)optimalAnalysis;
		
		int rsiPeriods = data.getRSIPeriods();
		double optimalLevel = data.getOptimalLevel();
		float[] closes = sd.getCloses();
		
		float[] rsis = StockCentral.calculateRSI(closes, rsiPeriods);
		
		if (rsis[lookback] > optimalLevel)
			toReturn = true;
		
		return toReturn;
		
	}
	
	public String getModuleName() { 
		
		return m_rsiPeriods + "-day RSI level at which there is a " + (m_confidenceLevel * 100) + 
				"% chance of negative return after " + m_investmentHorizon + " trading days";
	
	}
	
}

class SCRSIBearishModuleData extends SCLearningModuleData {
	
	private int m_rsiPeriods;
	private double m_confidenceLevel;
	
	public void setRSIPeriods(int p) { m_rsiPeriods = p; }
	public int getRSIPeriods() { return m_rsiPeriods; }
	
	public void setConfidenceLevel(double c) { m_confidenceLevel = c; }
	public double getConfidenceLeve() { return m_confidenceLevel; }
	
}
