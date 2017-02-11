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

	public static final boolean DEBUG_OUTPUT = false;
	
	public abstract SCLearningModuleData learnOptimalTrade(StockData sd);
	
	public abstract boolean isOptimalTrade(StockData sd, SCLearningModuleData optimalAnalysis, int lookback);
	
	public abstract String getModuleName();
	
	public abstract boolean isBullish();
	
	public abstract String getModuleMnemonic();
	
}
