/**
 * 
 */
package stockcentral;

import java.util.*;

/**
 * @author jschilla
 *
 */
public class LearnerStrategies extends BacktestStrategies {

	private SCLearningModule[] m_activeModules = SCStrategyLearner.ACTIVE_MODULES;
	private Hashtable<String, String> m_learnedDataFileList;
	
	
	public LearnerStrategies() {
	
		m_learnedDataFileList = (Hashtable<String,String>) StockCentral.deserializeObject(SCStrategyLearner.LEARNED_DATA_INDEX, 
				SCStrategyLearner.LEARNED_DATA_DIRECTORY);

		
	}
	
	/* (non-Javadoc)
	 * @see stockcentral.BacktestStrategies#getNumberOfStrategies()
	 */
	@Override
	public int getNumberOfStrategies() {
		// TODO Auto-generated method stub
		return m_activeModules.length;
	}
	
	public String getStrategyName(int strategyId) {
		
		return m_activeModules[strategyId].getModuleName();
		
	}
	
	public String getStrategyFileNameComponent (int strategyId) {

		return m_activeModules[strategyId].getModuleMnemonic();

	}	// getStrategyFileNameComponent

	/* (non-Javadoc)
	 * @see stockcentral.BacktestStrategies#isStrategyBullish(int)
	 */
	@Override
	public boolean isStrategyBullish(int strategyId) {
		// TODO Auto-generated method stub
		return m_activeModules[strategyId].isBullish();
	}

	/* (non-Javadoc)
	 * @see stockcentral.BacktestStrategies#testStrategyAtDay(stockcentral.StockData, int, int)
	 */
	@Override
	public boolean testStrategyAtDay(StockData sd, int strategyId, int lookBack) {
		// TODO Auto-generated method stub
		
		boolean toReturn = false;
		
			// First, let's load the module data for this particular strategy/ticker combination.
		String key = SCStrategyLearner.generateKey(sd.getTicker(), m_activeModules[strategyId].getModuleMnemonic());
		String fileNameOfDataModule = (String) m_learnedDataFileList.get(key);
		SCLearningModuleData moduleData = (SCLearningModuleData) StockCentral.deserializeObject(fileNameOfDataModule, 
				SCStrategyLearner.LEARNED_DATA_DIRECTORY);
		
			// Next, we pass the information to the learning module to do the actual analysis.
		toReturn = m_activeModules[strategyId].isOptimalTrade(sd, moduleData, lookBack);
		
		
		return toReturn;
	}

	/* (non-Javadoc)
	 * @see stockcentral.BacktestStrategies#testExitAtDay(stockcentral.StockData, int, int, float)
	 */
	@Override
	public boolean testExitAtDay(StockData sd, int strategyId, int lookBack,
			float closeAtMatch) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see stockcentral.BacktestStrategies#testStrategyAtDayBefore(stockcentral.StockData, int, int)
	 */
	@Override
	public boolean testStrategyAtDayBefore(StockData sd, int strategyId,
			int lookBack) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see stockcentral.BacktestStrategies#waitsForExit(int)
	 */
	@Override
	public boolean waitsForExit(int strategyId) {
		// TODO Auto-generated method stub
		return false;
	}

}
