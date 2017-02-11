/**
 * 
 */
package stockcentral;

import java.io.*;
import java.util.*;

/**
 * This class runs all of the strategy learner to identify the thresholds for each strategy and each stock.
 * 
 * @author jschilla
 *
 */
public class SCStrategyLearner {

	private static final String LEARNING_REPORT_PREFIX = "LearningReport_";
	public static final String LEARNING_REPORT_FOLDER = "learning";
	public static final String LEARNED_DATA_INDEX = "LearnedDataList.dat";
	public static final String LEARNED_DATAFILE_PREFIX = "Data_";
	public static final String LEARNED_DATA_DIRECTORY = "learningdata";
	
	public static final SCLearningModule[] ACTIVE_MODULES= {
		
		new SCRSI80Percent(1, 0.9, 14), new SCRSIShort(1, 0.9, 14), new SCRSI80Percent (3, 0.9, 14), 
		new SCRSIShort(3, 0.9, 14), new SCRSI80Percent(5, 0.9, 14), new SCRSIShort(5, 0.9, 14)
		
	};
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Hashtable<String, String> learnedDataFileList = new Hashtable();
		
			// First, let's create a report file into which we'll report each of the
		String learningReportFileName = LEARNING_REPORT_PREFIX + System.currentTimeMillis() + ".csv";
		
		PrintWriter reportOut = StockCentral.createOutputFile(learningReportFileName, LEARNING_REPORT_FOLDER);
		reportOut.println("Strategy,Ticker,Optimal Level,Avg. Performance (successful matches)," + 
				"Avg. Performance (all matches),Frequency of Optimal Level");
		
			// Next, let'sload up all of the stock data.
		String fileName;

		if ((args != null) && (args.length > 0))
			fileName = args[0];
		else
			fileName = StockDataLoader.DEFAULT_DATA;

		StockDataArray dataArray = (StockDataArray)StockCentral.deserializeObject(fileName, null);
		
		StockCentral central = new StockCentral();		// I'm not sure if this is necessary.

			// Now, let's get all of the modules going.
		SCLearningModule[] activeModules = ACTIVE_MODULES;

		// Cycle through each active learner module.	
		for (int countModules = 0; countModules < activeModules.length; countModules++) {
		
			dataArray.restartArray();
			
			System.out.print("Running " + activeModules[countModules].getModuleName() + ":  ");
			
			// Cycle through each stock for this module.			
			for (int countSDs = 0; countSDs < dataArray.size(); countSDs++) {
				
				
				StockData sd = dataArray.getNextData();
				
				try {
					
					sd.calculateBellsAndWhistles();

				}
				catch (ArrayIndexOutOfBoundsException e) {
					
					e.printStackTrace();
					System.exit(0);

				}
				
				System.out.print(sd.getTicker() + ", ");
				
				SCLearningModuleData knowledge = activeModules[countModules].learnOptimalTrade(sd);
				
				reportOut.println(activeModules[countModules].getModuleName() + "," + knowledge.getTicker() + "," + 
						knowledge.getOptimalLevel() + "," + (knowledge.getReturnSuccessfulDays() * 100) + 
						"%," + (knowledge.getReturnAllDays() * 100) + "%," + (knowledge.getFrequency() * 100) + "%");
				
				String dataFileName = LEARNED_DATAFILE_PREFIX + knowledge.getTicker() + "_" + System.currentTimeMillis() + 
						".dat";
				StockCentral.serializeObject(dataFileName, LEARNED_DATA_DIRECTORY, knowledge);
				
				learnedDataFileList.put(generateKey(sd.getTicker(), activeModules[countModules].getModuleMnemonic()), 
						dataFileName);
				
			}
			
			reportOut.println("*************");
						
			System.out.println();
			
		}
		
		reportOut.close();
		
		StockCentral.serializeObject(LEARNED_DATA_INDEX, LEARNED_DATA_DIRECTORY, learnedDataFileList);
		
		if (SCLearningModule.DEBUG_OUTPUT) {
			
			String keyReportFileName = "KeyReport_" + System.currentTimeMillis() + ".csv";
			PrintWriter keyReportOut = StockCentral.createOutputFile(keyReportFileName, LEARNED_DATA_DIRECTORY);
			keyReportOut.println("Key,File name\n");
			Enumeration keys = learnedDataFileList.keys();
			while (keys.hasMoreElements()) {
				
				String element = (String)keys.nextElement();
				
				keyReportOut.println(element + "," + learnedDataFileList.get(element));

			}
			keyReportOut.close();
			
		}

		System.out.println("Analysis complete!!!");

	}
	
	public static String generateKey(String ticker, String moduleMnemonic) {
		
		String toReturn = ticker + ":" + moduleMnemonic;
		
		return toReturn;
		
	}

}
