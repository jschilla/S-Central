/**
 *
 */
package stockcentral;

/**
 *
 * @author Jack's Dell
 *
 */
public class MacDSignalCrossoverStrategies extends BacktestStrategies {

    private static final String[] STRATEGY_NAMES = {
    	"4-9-4 Bullish; Exit at Reversal",
    	"4-9-4 Near Bullish; Exit at Reversal",
    	"4-9-4 Bearish; Exit at Reversal",
    	"4-9-4 Near Bearish; Exit at Reversal",
    	"9-26-9 Bullish; Exit at Reversal",
    	"9-26-9 Near Bullish; Exit at Reversal",
    	"9-26-9 Bearish; Exit at Reversal",
    	"9-26-9 Near Bearish; Exit at Reversal"
    	};


    /* (non-Javadoc)
	 * @see stockcentral.BacktestStrategies#getNumberOfStrategies()
	 */
	public int getNumberOfStrategies() {
		// TODO Auto-generated method stub
		return STRATEGY_NAMES.length;
	}

	public String getStrategyName (int strategyId) {

		return STRATEGY_NAMES[strategyId];

	}	// getStrategyName

	/* (non-Javadoc)
	 * @see stockcentral.BacktestStrategies#isStrategyBullish(int)
	 */
	@Override
	public boolean isStrategyBullish(int strategyId) {
		// TODO Auto-generated method stub
		boolean toReturn = false;

		if ((strategyId <= 1) || ((strategyId >= 4) && (strategyId <= 5)))
			toReturn = true;

		return toReturn;
	}


	/* (non-Javadoc)
	 * This tests to see if we have an exit signal.  An exit signal will occur if the MacD either crosses over the center line
	 * or if it crosses back over the signal line (which is more of a stop loss than an actual exit signal).
	 */
	@Override
	public boolean testExitAtDay (StockData sd, int strategyId, int lookback, float closeAtMatch) {

		boolean toReturn = false;

		MacD macdData;

        if (strategyId >= 4) {

            macdData = sd.getMacD9269();

            strategyId -= 4;

        }
        else
            macdData = sd.getMacD494();

        float[] closes = sd.getCloses();
		double[] macd = macdData.getMacD();
		double[] signal = macdData.getSignal();
		double[] histogram = macdData.getHistogram();

		try {
				// If this is bullish strategy, in which case we are looking for the macd to cross up and over
				// center line or to cross back down the signal line (as a stop loss).  We use the
				// histogram to detect such a signal line crossover.
			if ((strategyId == 0) || (strategyId == 1)) {

				if (macd[lookback - 1] > macd[lookback])
					toReturn = true;

			}
				// This bullish strategy is just a slight modification -- it
				// also exits if the MacD turns around at all.
/*			else if (strategyId == 1) {

				if (((macd[lookback - 1] > 0) && (macd[lookback] < 0)) ||
						((histogram[lookback - 1] < 0) && (histogram[lookback] > 0)) ||
						(macd[lookback - 1] < macd[lookback]))
							toReturn = true;

			}
*/				// These two just exit if the MacD turns around.
			else if ((strategyId == 1) || (strategyId == 2) || (strategyId == 5) /*|| (strategyId == 4)*/) {

				if (macd[lookback - 1] < macd[lookback])
					toReturn = true;

			}
				// This exits if there is either a reversal of the MacD or the stock has moved up a
				// certain percent.
			else if (strategyId == 3) {

				float changeFromMatch = 1 - (closeAtMatch / closes[lookback]);

				if ((macd[lookback - 1] < macd[lookback]) ||
					(changeFromMatch > 0.04))
						toReturn = true;


			}
			else if (strategyId == 4) {

				if ((macd[lookback - 1] < macd[lookback]) ||
					(histogram[lookback - 1] < histogram[lookback]))
						toReturn = true;


			}

				// If this is bearish strategy, in which case we are looking for the macd to cross down and
				// under the center line or to cross back up the signal line (as a stop loss).
				// We use the histogram to detect such a signal line crossover.
			else if ((strategyId == 5) /*|| (strategyId == 8)*/) {

				if ((macd[lookback - 1] > macd[lookback]) ||
						(closes[lookback - 1] < closes[lookback]))
							toReturn = true;

			}
				// This bullish strategy is just a slight modification -- it
				// also exits if the MacD turns around at all.
			else if (strategyId == 6) {

				if (((macd[lookback - 1] < 0) && (macd[lookback] > 0)) ||
						((histogram[lookback - 1] > 0) && (histogram[lookback] < 0)) ||
						(macd[lookback - 1] > macd[lookback]))
							toReturn = true;
			}
			else if ((strategyId == 7) || (strategyId == 11)/*|| (strategyId == 9)*/) {

				if (macd[lookback - 1] > macd[lookback])
					toReturn = true;

			}
			// This exits if there is either a reversal of the MacD or the stock has moved down a
			// certain percent.
			else if (strategyId == 8) {

				float changeFromMatch = 1 - (closeAtMatch / closes[lookback]);

				if ((macd[lookback - 1] > macd[lookback]) ||
					(changeFromMatch < -0.04))
						toReturn = true;


			}
			else if (strategyId == 9) {

				if ((macd[lookback - 1] > macd[lookback]) ||
					(histogram[lookback - 1] < histogram[lookback]))
						toReturn = true;


			}

		}	// try
		catch (ArrayIndexOutOfBoundsException e) {

		}

		return toReturn;

	}	// testExitAtDay

	/* (non-Javadoc)
	 * This method checks to see if the strategy has been invoked on the day passed.  For the bullish strategy, it
	 * it looks to determine whether the macd just passed above the signal line (i.e., that the histogram just turned
	 * positive) and the MacD is in negative territory (below the center line).
	 * For the bearish strategy, it obviously does the opposite.
	 */
	@Override
	public boolean testStrategyAtDay(StockData sd, int strategyId, int lookBack) {

		boolean toReturn = false;

    	MacD macdData;

        if (strategyId >= 12) {

            macdData = sd.getMacD9269();

            strategyId -= 12;

        }
        else
            macdData = sd.getMacD494();

		double[] macd = macdData.getMacD();
		double[] histogram = macdData.getHistogram();
		double avgHistogram = macdData.getAverageHistogram();

		if (strategyId == 1) {

			if ((histogram[lookBack] > 0) && (histogram[lookBack + 1] > 0) &&
					(histogram[lookBack + 2] < 0) && (histogram[lookBack] > histogram[lookBack + 1]))
				toReturn = true;

		}
		else if (strategyId <= 4) {

			if ((histogram[lookBack] > 0) && (histogram[lookBack + 1] < 0) && (macd[lookBack] < 0))
				toReturn = true;

		}
		else if (strategyId == 5) {

			if ((lookBack + 2) < histogram.length) {

				if ((Math.abs(histogram[lookBack]) < (avgHistogram * 0.5)) && (macd[lookBack] > macd[lookBack + 1]) &&
						(macd[lookBack + 1] > macd[lookBack + 2]))
					toReturn = true;

			}

		}
/*		else if ((strategyId == 3) || (strategyId == 4)) {

			if ((histogram[lookBack] > 0) && (histogram[lookBack + 1] < 0) && (macd[lookBack] < 0))
				if (findBulgeInLastStretch (macdData, lookBack, true))
					toReturn = true;


		}
*/

		if (strategyId == 7) {

			if ((histogram[lookBack] < 0) && (histogram[lookBack + 1] < 0) &&
					(histogram[lookBack + 2] > 0) && (histogram[lookBack] < histogram[lookBack + 1]))
				toReturn = true;

		}
		else if ((strategyId >= 6) && (strategyId <= 10)) {

			if ((histogram[lookBack] < 0) && (histogram[lookBack + 1] > 0) && (macd[lookBack] > 0))
				toReturn = true;

		}
		else if (strategyId == 11) {

			if ((lookBack + 2) < histogram.length) {

				if ((Math.abs(histogram[lookBack]) < (avgHistogram * 0.5)) && (macd[lookBack] < macd[lookBack + 1]) &&
						(macd[lookBack + 1] < macd[lookBack + 2]))
					toReturn = true;

			}

		}
/*        else if (strategyId >= 8) {

    		if ((histogram[lookBack] < 0) && (histogram[lookBack + 1] > 0) && (macd[lookBack] > 0))
                if (findBulgeInLastStretch (macdData, lookBack, false))
                    toReturn = true;

        }
*/

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

	private static boolean findBulgeInLastStretch(MacD macdData, int lookback, boolean bullish) {

		boolean toReturn = false;

		double[] macd = macdData.getMacD();
		double[] signal = macdData.getSignal();
		double[] histogram = macdData.getMacD();
		double avgHistogram = macdData.getAverageHistogram();

			// This block of code looks for the most recent cross-over in the other direction.  It has
			// to catch an out of bounds index exception, in case we go beyond the length of the data.
			// It also finds the largest histogram size in the range, then compares it to the average.
			// If it's more than double of the average, then we return true because we've found a bulge.
		try {

			int lookingForCrossover = 1;

			double largestHistogramSizeInRange = 0.0;

				// This looks for the most recent reverse crossover of the macd and the signal.
			while (((bullish) && (macd[lookback + lookingForCrossover] < signal[lookback + lookingForCrossover])) ||
                    ((!bullish) && (macd[lookback + lookingForCrossover] > signal[lookback + lookingForCrossover]))) {

				if (Math.abs(histogram[lookback + lookingForCrossover]) > largestHistogramSizeInRange)
					largestHistogramSizeInRange = Math.abs(histogram[lookback + lookingForCrossover]);

				lookingForCrossover++;

			}

			if (largestHistogramSizeInRange > (avgHistogram * 2))
				toReturn = true;

		}
		catch (ArrayIndexOutOfBoundsException e) {

		}

		return toReturn;

	}	// findBulgeInLastStretch

}
