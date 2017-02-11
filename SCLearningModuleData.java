package stockcentral;

import java.io.*;

public class SCLearningModuleData implements Serializable {
	
	private double m_optimalLevel;
	private String m_ticker;
	private double m_returnSuccess, m_returnAll, m_frequency;
	private int m_horizon;
	private SCLearningModule m_module;
//	private double[] m_returnsBoC, m_returnsBoO;
	
	SCLearningModuleData(SCLearningModule module) {
		
		m_module = module;
		
	}
	
	SCLearningModuleData() {}
	
	public double getOptimalLevel() { return m_optimalLevel; }
	public void setOptimalLevel(double input) { m_optimalLevel = input; }
	
	public String getTicker() { return m_ticker; }
	public void setTicker(String ticker) { m_ticker = ticker; }
	
	public double getReturnSuccessfulDays() { return m_returnSuccess; }
	public void setReturnSuccessfulDays(double horizonReturn) { m_returnSuccess = horizonReturn; }
	
	public double getReturnAllDays() { return m_returnAll; }
	public void setReturnAllDays (double returnAll) { m_returnAll = returnAll; }
	
	public int getHorizon() { return m_horizon; }
	public void setHorizon(int horizon) { m_horizon = horizon; }
	
	public double getFrequency() { return m_frequency; }
	public void setFrequency(double frequency) { m_frequency = frequency; }
	
	public SCLearningModule getModule() { return m_module; }
	public void setModule(SCLearningModule module) { m_module = module; }
	
//	public double[] getBuyOnCloseReturns() { return m_returnsBoC; }
//	public double[] getBuyOnOpenReturns() { return m_returnsBoO; }

}
