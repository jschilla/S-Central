/**
 *
 */
package stockcentral;

/**
 * @author Jack's Dell
 *
 */
public class SmallRSIExtremesStrategies extends BacktestStrategies {

	private static final String[] STRATEGY_NAMES = {

		"Two-Day RSI < 5", "Four-Day RSI < 5", "Two-Day RSI > 95", "Four-Day RSI > 95"

	};

	public String getStrategyName (int strategyId) {

		return STRATEGY_NAMES[strategyId];

	}	// getStrategyName

	/* (non-Javadoc)
	 * @see stockcentral.BacktestStrategies#getNumberOfStrategies()
	 */
	@Override
	public int getNumberOfStrategies() {
		// TODO Auto-generated method stub
		return 4;
	}

	/* (non-Javadoc)
	 * @see stockcentral.BacktestStrategies#isStrategyBullish(int)
	 */
	@Override
	public boolean isStrategyBullish(int strategyId) {
		// TODO Auto-generated method stub
		boolean toReturn = true;

		if (strategyId >= 2)
			toReturn = false;

		return toReturn;
	}

	/* (non-Javadoc)
	 * @see stockcentral.BacktestStrategies#testExitAtDay(stockcentral.StockData, int, int, float)
	 */
	@Override
	public boolean testExitAtDay(StockData sd, int strategyId, int lookBack,
			float closeAtMatch) {
		// TODO Auto-generated method stub
		boolean toReturn = false;

		float[] twoDayRSIs = sd.getTwoDayRSI();
		float[] fourDayRSIs = sd.getFourDayRSI();

		// The first strategy looks to whether the 2-day RSI is below 5.
		if (strategyId == 0) {

			if (twoDayRSIs[lookBack] < twoDayRSIs[lookBack + 1])
				toReturn = true;

		}
			// The second looks at whether the 4-day RSI is below 5.
		else if (strategyId == 1) {

			if (fourDayRSIs[lookBack] < fourDayRSIs[lookBack + 1])
				toReturn = true;

		}
			// Third -- 2-day RSI above 95.
		else if (strategyId == 2) {

			if (twoDayRSIs[lookBack] > twoDayRSIs[lookBack + 1])
				toReturn = true;

		}
		else if (strategyId == 3) {

			if (fourDayRSIs[lookBack] > fourDayRSIs[lookBack + 1])
				toReturn = true;

		}



		return toReturn;
	}

	/* (non-Javadoc)
	 * @see stockcentral.BacktestStrategies#testStrategyAtDay(stockcentral.StockData, int, int)
	 */
	@Override
	public boolean testStrategyAtDay(StockData sd, int strategyId, int lookBack) {
		// TODO Auto-generated method stub
		boolean toReturn = false;

		float[] twoDayRSIs = sd.getTwoDayRSI();
		float[] fourDayRSIs = sd.getFourDayRSI();

			// The first strategy looks to whether the 2-day RSI is below 5.
		if (strategyId == 0) {

			if (twoDayRSIs[lookBack] < 5)
				toReturn = true;

		}
			// The second looks at whether the 4-day RSI is below 5.
		else if (strategyId == 1) {

			if (fourDayRSIs[lookBack] < 5)
				toReturn = true;

		}
			// Third -- 2-day RSI above 95.
		else if (strategyId == 2) {

			if (twoDayRSIs[lookBack] > 95)
				toReturn = true;

		}
		else if (strategyId == 3) {

			if (fourDayRSIs[lookBack] > 95)
				toReturn = true;

		}

		return toReturn;
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
		return true;
	}

}
