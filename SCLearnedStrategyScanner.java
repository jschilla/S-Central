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
public class SCLearnedStrategyScanner {

	private static final String OUTPUT_FILE_PREFIX = "LearnedStrategyAnalysis_";
	private static final String OUTPUT_FILE_DIRECTORY = "learnedanalysis";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String outputFileName = OUTPUT_FILE_PREFIX + System.currentTimeMillis() + ".csv";
		
		PrintWriter reportOut = StockCentral.createOutputFile(outputFileName, OUTPUT_FILE_DIRECTORY);
		
		reportOut.println("List of matches.");
		
			// First, let's load up all of the StockData.
			// I need to add the ability to load today's closes.
		String sdFileName;
	
		if ((args != null) && (args.length > 0))
			sdFileName = args[0];
		else
			sdFileName = StockDataLoader.DEFAULT_DATA;
	
		StockDataArray dataArray = (StockDataArray)StockCentral.deserializeObject(sdFileName, null);
		
		StockCentral central = new StockCentral();		// I'm not sure if this is necessary.
		
			// We need to sort the StockData by ticker into a Hashtable.
		Hashtable<String, StockData> sds = new Hashtable<String, StockData>();
		
		dataArray.restartArray();
		
		for (int countSDs = 0; countSDs < dataArray.size(); countSDs++) {
						
			StockData sd = dataArray.getNextData();
			
			System.out.println(sd.getTicker());
			sds.put(sd.getTicker(), sd);
			
		}

			// Next, let's load up all of the learned data.
		Vector knowledgeFileNames = (Vector)StockCentral.deserializeObject(SCStrategyLearner.LEARNED_DATA_INDEX, 
				SCStrategyLearner.LEARNED_DATA_DIRECTORY);
		Vector knowledges = new Vector();
		
		Enumeration kFNs = knowledgeFileNames.elements();

		while (kFNs.hasMoreElements()) {
			
			SCLearningModuleData knowledge = (SCLearningModuleData)StockCentral.deserializeObject((String)kFNs.nextElement(), 
					SCStrategyLearner.LEARNED_DATA_DIRECTORY);
			
			knowledges.add(knowledge);
			
		}
		
			// Next, run through each learned data, pull up the StockData, and check to see if there's a match today.
		Enumeration knowledgeList = knowledges.elements();
		
		while (knowledgeList.hasMoreElements()) {
			
			SCLearningModuleData knowledge = (SCLearningModuleData) knowledgeList.nextElement();
			
			SCLearningModule module = knowledge.getModule();
			
			StockData sd = sds.get(knowledge.getTicker());
			
			if (module.isOptimalTrade(sd, knowledge, 0))
				reportOut.println(module.getModuleName() + "," + knowledge.getTicker());
			
		}
		
		reportOut.close();

	}

}
