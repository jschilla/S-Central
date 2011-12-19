/**
 *
 */
package stockcentral;

/**
 * @author Jack's Dell
 *
 */
public class MacDStrategyTesterizer extends BacktestStrategies {

	private static final String[] STRATEGY_NAMES = {
		"Regular cross-over",
		"Cross-over on high volume",
		"Cross over in positive territory",
		"Cross over in negative territory",
		"Entry at reversal of MacD (for two days)",
		"Cross-over after Bulge",
		"Cross-over after profitable contra-turn",
		"Cross-over after flatlining",
		"Cross-over without dramatic price movement"
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
		return STRATEGY_NAMES.length;
	}

	/* (non-Javadoc)
	 * @see stockcentral.BacktestStrategies#isStrategyBullish(int)
	 */
	@Override
	public boolean isStrategyBullish(int strategyId) {
		// TODO Auto-generated method stub
		return true;
	}

	/* (non-Javadoc)
	 * @see stockcentral.BacktestStrategies#testExitAtDay(stockcentral.StockData, int, int, float)
	 */
	@Override
	public boolean testExitAtDay(StockData sd, int strategyId, int lookback,
			float closeAtMatch) {
		// TODO Auto-generated method stub

		boolean toReturn = false;

        MacD macdData = sd.getMacD494();

		float[] closes = sd.getCloses();
		double[] macd = macdData.getMacD();
		double[] histogram = macdData.getHistogram();

		if (strategyId == 4) {
			if (((histogram[lookback] > 0) && (histogram[lookback + 1] < 0)) ||
				(macd[lookback] < macd[lookback + 1]))
					toReturn = true;
		}
		else if (macd[lookback] < macd[lookback + 1])
			toReturn = true;

		return toReturn;
	}

	/* (non-Javadoc)
	 * @see stockcentral.BacktestStrategies#testStrategyAtDay(stockcentral.StockData, int, int)
	 */
	@Override
	public boolean testStrategyAtDay(StockData sd, int strategyId, int lookback) {
		// TODO Auto-generated method stub
		boolean toReturn = false;

        MacD macdData = sd.getMacD494();

		float[] closes = sd.getCloses();
		double[] macd = macdData.getMacD();
		double[] signal = macdData.getSignal();
		double[] histogram = macdData.getHistogram();

		try {

			boolean crossover = ((histogram[lookback] > 0) && (histogram[lookback + 1] < 0));

				// If there's been a crossover, then we need to see if the other conditions are met.
			if (crossover) {

				boolean highVolume = (sd.getVolumes()[lookback] > (sd.getAverageVolume() * 2));

					// The first strategy is just a generic cross-over.
				if (strategyId == 0)
					toReturn = true;

					// The second strategy will be satisfied if the cross over occurs on high volume
					// i.e., the move was on average volume x 2
				else if ((strategyId == 1) && (highVolume))
					toReturn = true;

					// The third and fourth strategies are satisfied if the cross-over occurs when the
					// MacD is positive and negative, respectively.
				else if ((strategyId == 2) && (macd[lookback] > 0))
					toReturn = true;
				else if ((strategyId == 3) && (macd[lookback] < 0))
					toReturn = true;

			}
				// The fifth strategy is satisfied is there was a bullish reversal in the MacD for two days.
			else if (strategyId == 4) {

				boolean macdBullish = (macd[lookback] > macd[lookback + 1]);
				boolean macdBullishPlusOne = (macd[lookback + 1] > macd[lookback + 2]);
				boolean macdReversalPlusTwo = (macd[lookback + 2] < macd[lookback + 3]);

				if ((macdBullish) && (macdBullishPlusOne) && (macdReversalPlusTwo))
					toReturn = true;

			}
		}
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
