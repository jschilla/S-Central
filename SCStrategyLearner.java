/**
 * 
 */
package stockcentral;

import java.io.*;
import java.util.*;

/**
 * @author jschilla
 *
 */
public class SCStrategyLearner {

	private static final String LEARNING_REPORT_PREFIX = "LearningReport_";
	public static final String LEARNING_REPORT_FOLDER = "learning";
	public static final String LEARNED_DATA_INDEX = "LearnedDataList.dat";
	public static final String LEARNED_DATAFILE_PREFIX = "Data_";
	public static final String LEARNED_DATA_DIRECTORY = "learningdata";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Vector learnedDataFileList = new Vector();
		
			// First, let's create a report file into which we'll report each of the
		String learningReportFileName = LEARNING_REPORT_PREFIX + System.currentTimeMillis() + ".csv";
		
		PrintWriter reportOut = StockCentral.createOutputFile(learningReportFileName, LEARNING_REPORT_FOLDER);
		reportOut.println("Strategy,Ticker,Optimal Level,Performance,Frequency of Optimal Level");
		
			// Next, let'sload up all of the stock data.
		String fileName;

		if ((args != null) && (args.length > 0))
			fileName = args[0];
		else
			fileName = StockDataLoader.DEFAULT_DATA;

		StockDataArray dataArray = (StockDataArray)StockCentral.deserializeObject(fileName, null);
		
		StockCentral central = new StockCentral();		// I'm not sure if this is necessary.

			// Now, let's get all of the modules going.
		SCLearningModule[] activeModules = {
				
				new SCRSI80Percent()//, new SCRSI80Percent(3,0.8,28), new SCRSI80Percent(3,0.75,4)
				
		};	// activeModules variable

		for (int count = 0; count < activeModules.length; count++) {
		
			dataArray.restartArray();
			
			System.out.print("Running " + activeModules[count].getModuleName() + ":  ");
			
			for (int countSDs = 0; countSDs < dataArray.size(); countSDs++) {
				
				StockData sd = dataArray.getNextData();
				sd.calculateBellsAndWhistles();
				
				System.out.print(sd.getTicker() + ", ");
				
				SCLearningModuleData knowledge = activeModules[count].learnOptimalTrade(sd);
				
				reportOut.println(activeModules[count].getModuleName() + "," + knowledge.getTicker() + "," + 
						knowledge.getOptimalLevel() + "," + (knowledge.getHorizonReturn() * 100) + 
						"%," + (knowledge.getFrequency() * 100) + "%");
				
				String dataFileName = LEARNED_DATAFILE_PREFIX + knowledge.getTicker() + "_" + System.currentTimeMillis() + 
						".dat";
				StockCentral.serializeObject(dataFileName, LEARNED_DATA_DIRECTORY, knowledge);
				
				learnedDataFileList.add(dataFileName);

			}
			
			reportOut.println("*************");
			
			System.out.println();
			
		}
		
		reportOut.close();
		
		StockCentral.serializeObject(LEARNED_DATA_INDEX, LEARNED_DATA_DIRECTORY, learnedDataFileList);

	}

}
