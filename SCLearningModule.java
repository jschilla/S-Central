/**
 * 
 */
package stockcentral;

import java.util.Hashtable;
import java.io.*;

/**
 * @author jschilla
 *
 */
public abstract class SCLearningModule implements Serializable {

	public final boolean DEBUG_OUTPUT = false;
	
	public abstract SCLearningModuleData learnOptimalTrade(StockData sd);
	
	public abstract boolean isOptimalTrade(StockData sd, SCLearningModuleData optimalAnalysis);
	
	public abstract String getModuleName();
	
}
