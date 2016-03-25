/**
 * 
 */
package stockcentral;

import java.util.*;

/**
 * @author jschilla
 *
 */
public class SCRSI80Percent extends SCLearningModule {

	private final static int DEFAULT_INVESTMENT_HORIZON = 3;
	private final static int DEFAULT_RSI_PERIODS = 14;
	private static final double DEFAULT_CONFIDENCE_LEVEL = 0.8;
	
	private int m_investmentHorizon, m_rsiPeriods;
	private double m_confidenceLevel;
	
	public SCRSI80Percent() {
		
		m_investmentHorizon = DEFAULT_INVESTMENT_HORIZON;
		m_confidenceLevel = DEFAULT_CONFIDENCE_LEVEL;
		m_rsiPeriods = DEFAULT_RSI_PERIODS;
		
	}	// default ctor
	
	public SCRSI80Percent (int horizon, double confidence) {
		
		m_investmentHorizon = horizon;
		m_confidenceLevel = confidence;
		m_rsiPeriods = DEFAULT_RSI_PERIODS;
		
	}
	
	public SCRSI80Percent (int horizon, double confidence, int rsiPeriods) {
		
		this (horizon, confidence);
		m_rsiPeriods = rsiPeriods;
		
	}
	
		// What this learning module does is that it determines the RSI level at which 80% of the time there is
		// a positive return after two trading days.  This is accomplished as follows:
		// 1. 	The method runs through each day's RSI and measures the two-day return for each day.
		// 2.	It then runs the RSI numbers from 100 to 1, and determines how often RSIs at that level or lower
		// 		resulted in a positive return after two days.
	public SCLearningModuleData learnOptimalTrade(StockData sd) {
		 
		SCRSIBullishModuleData toReturn = new SCRSIBullishModuleData();
		toReturn.setModule(this);
		
			// First, run through each day's RSI and figure out the two-day return.
		float[] closes = sd.getCloses();
		float[] rsis = StockCentral.calculateRSI(closes, m_rsiPeriods);
		Calendar[] dates = sd.getDates();
		
		if (DEBUG_OUTPUT)
			System.out.println("Ticker:  " + sd.getTicker());
		
		double[] twoDayDeltas = new double[closes.length];
		double[] twoDayDeltaPercentages = new double[closes.length];
		
		for (int count = m_investmentHorizon; count < rsis.length; count++) {
			
			twoDayDeltas[count] = closes[count - m_investmentHorizon] - closes[count];
			twoDayDeltaPercentages[count] = twoDayDeltas[count] / closes[count];
			
			if (DEBUG_OUTPUT)
				System.out.println(StockCentral.translateCalendarToString(dates[count]) + " -- " + 
						twoDayDeltas[count] + " -- " + twoDayDeltaPercentages[count]);
			
		}
		
			// Next, we need to go through every number 1 - 100 and figure out how often that RSI results in a two-day
			// positive return.
			// This is accomplished by going through each number, and for each counting the number of times that there
			// are positive two-day returns.  The results of this analysis are put into a Hasthable.  
			// This could also be done by first sorting all of RSI/Two-Day Returns and then just going up the list.
		Hashtable<Integer, Double> rsiAnalysis = new Hashtable<Integer, Double>(), 
				averagePerformance = new Hashtable<Integer, Double>(), frequency = new Hashtable<Integer, Double>();
		
			// Outer loop:  go through each round whole RSI number
		for (int countRSIs = 1; countRSIs < 100; countRSIs++) {
			
				// First inner loop:  isolate all RSIs below countRSIs.
			Hashtable<Integer, Float> rsisBelowCount = new Hashtable<Integer, Float>();		// this should just be a stack.
			
			for (int countDays = m_investmentHorizon; countDays < rsis.length; countDays++) {
				
					// if this day's RSI is below the countRSI, then we put its index number in the hashtable.
				if (rsis[countDays] < countRSIs)
					rsisBelowCount.put(countDays, rsis[countDays]);
			}
			
				// Second inner loop:  calculate the average return across all of these RSIs.
			int numberOfMatches = rsisBelowCount.size();
			int numberOfSuccessfulMatches = 0;
			double percentageOfSuccessfulMatches = 0.0;
			
			Enumeration matchedIndexes = rsisBelowCount.keys();
			double totalChange = 0.0;			
			
			while (matchedIndexes.hasMoreElements()) {
				
				int nextMatch = (Integer)matchedIndexes.nextElement();
				
				if (twoDayDeltas[nextMatch] > 0) {
					
					numberOfSuccessfulMatches++;

					totalChange += twoDayDeltaPercentages[nextMatch];
				}
				
			}
			
			if (numberOfMatches > 0)
				percentageOfSuccessfulMatches = (double)numberOfSuccessfulMatches / (double)numberOfMatches;
			
			rsiAnalysis.put(countRSIs, percentageOfSuccessfulMatches);
			averagePerformance.put(countRSIs, (totalChange / numberOfMatches));
			frequency.put(countRSIs, (double)(numberOfMatches / (double)rsis.length));
			
			
		}
		
			// Second outer loop:  start at the top and figure out when's the first time we get a 
			// two-day return > 80%.
		int optimalRSI = 0;
		boolean keepScanning = true;
		
		for (int countRSIsBackwards = 99; (countRSIsBackwards > 0) && (keepScanning); countRSIsBackwards--) {
			
			double twoDaySuccessRate = rsiAnalysis.get(countRSIsBackwards);
			
			if (twoDaySuccessRate >= m_confidenceLevel) {
				
				keepScanning = false;
				optimalRSI = countRSIsBackwards;

			}

			if (DEBUG_OUTPUT)
				System.out.println("RSI:  " + countRSIsBackwards + ";  % Successful Matches:  " + twoDaySuccessRate +
						";  Average Performance:  " + averagePerformance.get(countRSIsBackwards) + 
						";  Frequency of RSI Level:  " + frequency.get(countRSIsBackwards));
			
		}
		
		if (optimalRSI != 0) {
			
			toReturn.setOptimalLevel(optimalRSI);
			toReturn.setHorizon(m_investmentHorizon);
			toReturn.setHorizonReturn(averagePerformance.get(optimalRSI));
			toReturn.setTicker(sd.getTicker());
			toReturn.setFrequency(frequency.get(optimalRSI));

		}
		else {

			toReturn.setOptimalLevel(0);
			toReturn.setHorizon(m_investmentHorizon);
			toReturn.setHorizonReturn(0);
			toReturn.setTicker(sd.getTicker());
			toReturn.setFrequency(0);
			
		}
		
		toReturn.setConfidenceLevel(m_confidenceLevel);
		toReturn.setRSIPeriods(m_rsiPeriods);

		return toReturn;
	
	}
	
	public boolean isOptimalTrade(StockData sd, SCLearningModuleData optimalAnalysis) { 
		
		boolean toReturn = false;

		SCRSIBullishModuleData data = (SCRSIBullishModuleData)optimalAnalysis;
		
		int rsiPeriods = data.getRSIPeriods();
		double optimalLevel = data.getOptimalLevel();
		float[] closes = sd.getCloses();
		
		float[] rsis = StockCentral.calculateRSI(closes, rsiPeriods);
		
		if (rsis[0] < optimalLevel)
			toReturn = true;
		
		return toReturn;
		
	}
	
	public String getModuleName() { 
		
		return m_rsiPeriods + "-day RSI level at which there is a " + (m_confidenceLevel * 100) + 
				"% chance of positive return after " + m_investmentHorizon + " trading days";
	
	}
	
}

class SCRSIBullishModuleData extends SCLearningModuleData {
	
	private int m_rsiPeriods;
	private double m_confidenceLevel;
	
	public void setRSIPeriods(int p) { m_rsiPeriods = p; }
	public int getRSIPeriods() { return m_rsiPeriods; }
	
	public void setConfidenceLevel(double c) { m_confidenceLevel = c; }
	public double getConfidenceLeve() { return m_confidenceLevel; }
	
}
