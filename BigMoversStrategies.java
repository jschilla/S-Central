/**
 *
 */
package stockcentral;

/**
 * @author Jack's Dell
 *
 */
public class BigMoversStrategies extends BacktestStrategies {

	private static final String[] STRATEGY_NAMES = {

		"Down 12%",
		"Down 10%",
		"Up 12%",
		"Up 10%"

	};

	/* (non-Javadoc)
	 * @see stockcentral.BacktestStrategies#getNumberOfStrategies()
	 */
	@Override
	public int getNumberOfStrategies() {
		// TODO Auto-generated method stub
		return STRATEGY_NAMES.length;
	}

	public String getStrategyName(int strategyId) {

		return STRATEGY_NAMES[strategyId];

	}

	/* (non-Javadoc)
	 * @see stockcentral.BacktestStrategies#isStrategyBullish(int)
	 */
	@Override
	public boolean isStrategyBullish(int strategyId) {
		// TODO Auto-generated method stub
		boolean toReturn = false;

		if (strategyId <= 1)
			toReturn = true;

		return toReturn;

	}

	/* (non-Javadoc)
	 * @see stockcentral.BacktestStrategies#testExitAtDay(stockcentral.StockData, int, int, float)
	 */
	@Override
	public boolean testExitAtDay(StockData sd, int strategyId, int lookBack,
			float closeAtMatch) {
		// TODO Auto-generated method stub
		return true;
	}

	/* (non-Javadoc)
	 * @see stockcentral.BacktestStrategies#testStrategyAtDay(stockcentral.StockData, int, int)
	 */
	@Override
	public boolean testStrategyAtDay(StockData sd, int strategyId, int lookBack) {
		// TODO Auto-generated method stub

		boolean toReturn = false;

		float[] closes = sd.getCloses();
		float[] lows = sd.getLows();
		float[] highs = sd.getHighs();

		try {

				// The most important numbers are here!
			double changeSinceYesterday = (closes[lookBack] / closes[lookBack + 1]) - 1;
			double bottomSinceYesterday = (lows[lookBack] / closes[lookBack + 1]) - 1;
			double topSinceYesterday = (highs[lookBack] / closes[lookBack + 1]) - 1;
			double ratioOfBottomToChange = changeSinceYesterday / bottomSinceYesterday;
			double ratioOfTopToChange = changeSinceYesterday / topSinceYesterday;

			if (strategyId == 0) {

				if ((changeSinceYesterday < -0.12) && (ratioOfBottomToChange > 0.8))
					toReturn = true;

			}
			else if (strategyId == 1) {

				if ((changeSinceYesterday < -0.10) && (ratioOfBottomToChange > 0.8))
					toReturn = true;

			}
			else if (strategyId == 2) {

				if ((changeSinceYesterday > 0.12) && (ratioOfTopToChange > 0.8))
					toReturn = true;

			}
			else if (strategyId == 3) {

				if ((changeSinceYesterday > 0.10) && (ratioOfTopToChange > 0.8))
					toReturn = true;

			}

		}	// try
		catch (ArrayIndexOutOfBoundsException e) {}

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
