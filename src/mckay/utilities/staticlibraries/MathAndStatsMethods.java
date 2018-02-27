/*
 * MathAndStatsMethods.java
 * Version 4.1
 *
 * Last modified on January 22, 2018.
 * CIRMMT, Marianopolis College, McGill University and University of Waikato
 */

package mckay.utilities.staticlibraries;

import java.util.Arrays;

/**
 * A holder class for static methods relating to statistical and mathematical analysis.
 *
 * @author Cory McKay
 */
public class MathAndStatsMethods
{
	/**
	 * Returns a random integer from 0 to max - 1, based on the uniform distribution.
	 *
	 * @param	max	The non-inclusive ceiling for the random number.
	 * @return		The random number.
	 */
	public static int generateRandomNumber(int max)
	{
		int random_number = (int) (((double) Integer.MAX_VALUE) * Math.random());
		return (random_number % max);
	}

	
	/**
	 * Returns an array of size n. Each entry has a value between 0 and n-1, and no numbers are repeated.
	 * Ordering of numbers is random.
	 *
	 * @param	number_entries	The size of the returned array, and the non-inclusive ceiling for its values.
	 * @return					The randomly generated array.
	 */
	public static int[] getRandomOrdering(int number_entries)
	{
		// Generate an array of random numbers
		double[] random_values = new double[number_entries];
		for (int i = 0; i < random_values.length; i++)
			random_values[i] = Math.random();

		// Fill in the array to return and return it
		int[] scrambled_values = new int[number_entries];
		for (int i = 0; i < scrambled_values.length; i++)
		{
			int largest_index = getIndexOfLargest(random_values);
			scrambled_values[i] = largest_index;
			random_values[largest_index] = -1.0; // to avoid double counting
		}
		
		return scrambled_values;
	}
	
	
	/**
	 * Returns a copy of the given array, but with any duplicate entries removed (e.g. {1, 3, 5, 3, 4} would
	 * return {1, 3, 5, 4}).
	 *
	 * @param	to_check	The array to remove double entries from.
	 * @return				Returns the array with double entries removed.
	 */
	public static int[] removeRedundantEntries(int[] to_check)
	{
		boolean[] is_double = new boolean[to_check.length];
		for (int i = 0; i < is_double.length; i++)
			is_double[i] = false;

		int doubles_found = 0;
		for (int i = 0; i < to_check.length - 1; i++)
		{
			if (!is_double[i])
			{
				for (int j = i + 1; j < to_check.length; j++)
				{
					if (to_check[j] == to_check[i])
					{
						doubles_found++;
						is_double[j] = true;
					}
				}
			}
		}

		if (doubles_found > 0)
		{
			int[] to_return = new int[to_check.length - doubles_found];
			int current = 0;
			for (int i = 0; i < to_check.length; i++)
			{
				if (!is_double[i])
				{
					to_return[current] = to_check[i];
					current++;
				}
			}
			return to_return;
		}
		else return to_check;
	}


	/**
	 * Returns the sum of the contents of all of the entries of the given array.
	 *
	 * @param	to_sum	The array whose contents are to be summed.
	 * @return			The resultant sum.
	 */
	public static double getArraySum(double[] to_sum)
	{
		double sum = 0.0;
		for (int i = 0; i < to_sum.length; i++)
			sum += to_sum[i];
		return sum;
	}

	
	/**
	 * Return a normalized copy of the the given array. The original array is not altered. If the sum of all
	 * entries of to_normalize is zero, then all entries of the returned array are set to 0.
	 *
	 * @param	to_normalize	The array to normalize.
	 * @return					A new array representing a normalized version of to_normalize.
	 */
	public static double[] normalize(double[] to_normalize)
	{
		// Copy the to_normalize array
		double[] normalized = new double[to_normalize.length];
		for (int i = 0; i < normalized.length; i++)
			normalized[i] = to_normalize[i];

		// Perform the normalization
		double sum = getArraySum(normalized);
		for (int i = 0; i < normalized.length; i++)
		{
			if (sum == 0.0) normalized[i] = 0.0;
			else normalized[i] = normalized[i] / sum;
		}

		// Return the normalized results
		return normalized;
	}

	
	/**
	 * Return a normalized copy of the the given array. Normalization is performed by row (i.e. the sum of
	 * each row (first indice) is one after normalization). Each row is independent. The original array is not
	 * altered.
	 *
	 * @param	to_normalize	The 2-D array to normalize.
	 * @return					A normalized-by-row copy of to_normalize.
	 */
	public static double[][] normalize(double[][] to_normalize)
	{
		// Copy the to_normalize array
		double[][] normalized = new double[to_normalize.length][];
		for (int i = 0; i < normalized.length; i++)
		{
			normalized[i] = new double[to_normalize[i].length];
			for (int j = 0; j < normalized[i].length; j++)
				normalized[i][j] = to_normalize[i][j];
		}

		// Perform the normalization
		double[] totals = new double[normalized.length];
		for (int i = 0; i < normalized.length; i++)
		{
			totals[i] = 0.0;
			for (int j = 0; j < normalized[i].length; j++)
				totals[i] += normalized[i][j];
		}
		for (int i = 0; i < normalized.length; i++)
		{
			for (int j = 0; j < normalized[i].length; j++)
			{
				if (totals[i] == 0.0) normalized[i][j] = 0.0;
				else normalized[i][j] = normalized[i][j] / totals[i];
			}
		}

		// Return the normalized results
		return normalized;
	}

	
	/**
	 * Return a normalized copy of the the given array. Normalization is performed overall so that the sum of
	 * all entries is 1.0. The original array is not altered.
	 *
	 * @param	to_normalize	The 2-D array to normalize.
	 * @return					A normalized copy of to_normalize.
	 */
	public static double[][] normalizeEntirely(double[][] to_normalize)
	{
		// Find the sum of all entries
		double sum = 0.0;
		for (int i = 0; i < to_normalize.length; i++)
			for (int j = 0; j < to_normalize[i].length; j++)
				sum += to_normalize[i][j];

		// Make the normalized copy
		double[][] normalized = new double[to_normalize.length][];
		for (int i = 0; i < to_normalize.length; i++)
		{
			normalized[i] = new double[to_normalize[i].length];
			for (int j = 0; j < to_normalize[i].length; j++)
			{
				if (sum == 0.0) normalized[i][j] = 0.0;
				else normalized[i][j] = to_normalize[i][j] / sum;
			}
		}

		// Return the normalized results
		return normalized;
	}
	
	
	/**
	 * Returns the index of the entry of an array of ints with the smallest value. The first occurrence is
	 * returned in the case of a tie.
	 *
	 * @param	values	The array of values to search.
	 * @return			The index of the entry of values with the smallest value.
	 */
	public static int getIndexOfSmallest(int[] values)
	{
		int min_index = 0;
		for (int i = 0; i < values.length; i++)
			if (values[i] < values[min_index])
				min_index = i;
		return min_index;
	}

	
	/**
	 * Returns the index of the entry of an array of doubles with the smallest value. The first occurrence is
	 * returned in the case of a tie.
	 *
	 * @param	values	The array of values to search.
	 * @return			The index of the entry of values with the smallest value.
	 */
	public static int getIndexOfSmallest(double[] values)
	{
		int min_index = 0;
		for (int i = 0; i < values.length; i++)
			if (values[i] < values[min_index]) 
				min_index = i;
		return min_index;
	}

	
	/**
	 * Returns the index of the entry of an array of integers with the largest value. The first occurrence is
	 * returned in the case of a tie.
	 *
	 * @param	values	The array of values to search.
	 * @return			The index of the entry of values with the largest value.
	 */
	public static int getIndexOfLargest(int[] values)
	{
		int max_index = 0;
		for (int i = 0; i < values.length; i++)
			if (values[i] > values[max_index])
				max_index = i;
		return max_index;
	}

	
	/**
	 * Returns the index of the entry of an array of doubles with the largest value. The first occurrence is
	 * returned in the case of a tie.
	 *
	 * @param	values	The array of values to search.
	 * @return			The index of the entry of values with the largest value.
	 */
	public static int getIndexOfLargest(double[] values)
	{
		int max_index = 0;
		for (int i = 0; i < values.length; i++)
			if (values[i] > values[max_index])
				max_index = i;
		return max_index;
	}

	
	/**
	 * Returns the index of the entry of an array of floats with the largest value. The first occurrence is
	 * returned in the case of a tie.
	 *
	 * @param	values	The array of values to search.
	 * @return			The index of the entry of values with the largest value.
	 */
	public static int getIndexOfLargest(float[] values)
	{
		int max_index = 0;
		for (int i = 0; i < values.length; i++)
			if (values[i] > values[max_index])
				max_index = i;
		return max_index;
	}

	
	/**
	 * Returns the index of the entry of an array of integers with the second largest value. The second 
	 * occurrence of the largest value is returned in the case of a tie.
	 *
	 * @param	values	The array of values to search.
	 * @return			The index of the entry of values with the second largest value.
	 */
	public static int getIndexOfSecondLargest(double[] values)
	{
		int max_index = getIndexOfLargest(values);
				
		int second_max_index = 0;
		for (int i = 0; i < values.length; i++)
			if (i != max_index)
				if (values[i] > values[second_max_index])
					second_max_index = i;
		
		return second_max_index;
	}

	
	/**
	 * Given the values histogram, find the number of bins separating the the first bin with a frequency 
	 * greater than 0 and the last bin with a frequency greater than 0.
	 * 
	 * @param	values	A histogram, where each array entry corresponds to a different bin, and its value
	 *					corresponds to the frequency of that bin.
	 * @return			The range in number of bins separating the the first bin with a frequency greater than
	 *					0 and the last bin with a frequency greater than 0. If no bins or only one bin has a
	 *					frequency greater than 0, then 0 is returned.
	 */
	public static int getHistogramRangeInBins(double[] values)
	{
		// Find the lowest and highest non-zero entries
		int lowest = values.length;
		int highest = -1;
		for (int bin = 0; bin < values.length; bin++)
		{
			if (values[bin] > 0.0 && lowest == values.length)
				lowest = bin;
			if (values[bin] > 0.0)
				highest = bin;
		}

		// Calculate the number of bins separating the highest and lowest values
		if (lowest == values.length || highest == -1)
			return 0;
		else return highest - lowest;
	}
	
		
	/**
	 * Returns the index of the earliest entry in an array that holds the median value of all the values in
	 * the array. Returns -1 if a problem occurs.
	 *
	 * @param	values	The array of values to search.
	 * @return			The index of the earliest entry in values that holds the median value of the entries
	 *					in values. -1 if values consists of less than 1 entries or if a problem occurs.
	 */
	public static int getIndexOfMedian(double[] values)
	{
		if (values.length < 1)
			return -1;

		double[] copy = new double[values.length];
		for (int i = 0; i < values.length; i++)
			copy[i] = values[i];

		Arrays.sort(copy);

		int centre = copy.length / 2;

		double median = copy[centre];
		for (int i = 0; i < values.length; i++)
			if (values[i] == median)
				return i;

		return -1;
	}

	
	/**
	 * Returns the median value in the the given array.
	 * 
	 * @param	values	The data for which the median is to be found.
	 * @return			The value of the median. 0 if data consists of less than 1 entries. -1 if a problem
	 *					occurs. 
	 */
	public static double getMedianValue(double[] values)
	{
		if (values.length < 1)
			return 0;

		int index_of_median = getIndexOfMedian(values);
		
		if (index_of_median == -1)
			return -1;
		
		return values[index_of_median];
	}
	
	
	/**
	 * Returns the mode average of the given set of values (the value that occurs most frequently). If there
	 * is a tie between values that occur equally frequently, then the one that occurs first in the given
	 * values array is returned.
	 * 
	 * @param	values	The data for which the mode is to be found.
	 * @return			The most commonly occurring value found in the values array. In the case of a tie, the
	 *					value that occurs first in the values array is returned. 0 if data consists of less 
	 *					than 1 entries.
	 */
	public static double getModeAverageValue(double[] values)
	{
		if (values.length < 1)
			return 0;

		int max_count = 0;
		double max_value = 0.0;
		
		for (int i = 0; i < values.length; i++)
		{
			int this_count = 0;
			for (int j = 0; j < values.length; j++)
				if (values[j] == values[i])
					this_count++;
			if (this_count > max_count)
			{
				max_count = this_count;
				max_value = values[i];
			}
		}
		
		return max_value;
	}
	
	
	/**
	 * Returns the mode average of the given set of values (the value that occurs most frequently). If there
	 * is a tie between values that occur equally frequently, then the one that occurs first in the given
	 * values array is returned.
	 * 
	 * @param	values	The data for which the mode is to be found.
	 * @return			The most commonly occurring value found in the values array. In the case of a tie, the
	 *					value that occurs first in the values array is returned. 0 if data consists of less 
	 *					than 1 entries.
	 */
	public static int getModeAverageValue(int[] values)
	{
		if (values.length < 1)
			return 0;

		int max_count = 0;
		int max_value = 0;
		
		for (int i = 0; i < values.length; i++)
		{
			int this_count = 0;
			for (int j = 0; j < values.length; j++)
				if (values[j] == values[i])
					this_count++;
			if (this_count > max_count)
			{
				max_count = this_count;
				max_value = values[i];
			}
		}
		
		return max_value;
	}
	
	
	/**
	 * Returns the mean average of the entries of an array. Returns 0 if the length of the data is 0.
	 *
	 * @param	data	The data to be averaged.
	 * @return			The mean of the given data. 0 if data consists of less than 1 entries.
	 */
	public static double getAverage(int[] data)
	{
		if (data.length < 1)
			return 0.0;

		double sum = 0.0;
		for (int i = 0; i < data.length; i++)
			sum = sum + (double) data[i];
		return (sum / ((double) data.length));
	}

	
	/**
	 * Returns the mean average of the entries of an array. Returns 0 if the length of the data is 0.
	 *
	 * @param	data	The data to be averaged.
	 * @return			The mean of the given data. 0 if data consists of less than 1 entries.
	 */
	public static double getAverage(short[] data)
	{
		if (data.length < 1)
			return 0.0;

		double sum = 0.0;
		for (int i = 0; i < data.length; i++)
			sum = sum + (double) data[i];
		return (sum / ((double) data.length));
	}


	/**
	 * Returns the mean average of the entries of an array. Returns 0 if the length of the data is 0.
	 *
	 * @param	data	The data to be averaged.
	 * @return			The mean of the given data. 0 if data consists of less than 1 entries.
	 */
	public static double getAverage(double[] data)
	{
		if (data.length < 1)
			return 0.0;

		double sum = 0.0;
		for (int i = 0; i < data.length; i++)
			sum = sum + data[i];
		return (sum / ((double) data.length));
	}

	
	/**
	 * Returns the standard deviation of a set of ints. Returns 0 if there is only one piece of data.
	 *
	 * @param	data	The data for which the standard deviation is to be found.
	 * @return			The standard deviation of the given data. 0 if data consists of less than 2 entries.
	 */
	public static double getStandardDeviation(int[] data)
	{
		if (data.length < 2)
			return 0.0;
		
		double average = getAverage(data);
		
		double sum = 0.0;
		for (int i = 0; i < data.length; i++)
		{
			double diff = ((double) data[i]) - average;
			sum = sum + diff * diff;
		}
		
		return Math.sqrt(sum / ((double) (data.length - 1)));
	}

	
	/**
	 * Returns the standard deviation of a set of shorts. Returns 0 if there is only one piece of data.
	 *
	 * @param	data	The data for which the standard deviation is to be found.
	 * @return			The standard deviation of the given data. 0 if data consists of less than 2 entries.
	 */
	public static double getStandardDeviation(short[] data)
	{
		if (data.length < 2)
			return 0.0;
		
		double average = getAverage(data);
		
		double sum = 0.0;
		for (int i = 0; i < data.length; i++)
		{
			double diff = ((double) data[i]) - average;
			sum = sum + diff * diff;
		}
		
		return Math.sqrt(sum / ((double) (data.length - 1)));
	}

	
	/**
	 * Returns the standard deviation of a set of doubles. Returns 0 if there is only one piece of data.
	 *
	 * @param	data	The data for which the standard deviation is to be found.
	 * @return			The standard deviation of the given data. 0 if data consists of less than 2 entries.
	 */
	public static double getStandardDeviation(double[] data)
	{
		if (data.length < 2)
			return 0.0;
		
		double average = getAverage(data);
		
		double sum = 0.0;
		for (int i = 0; i < data.length; i++)
		{
			double diff = data[i] - average;
			sum = sum + diff * diff;
		}
		
		return Math.sqrt(sum / ((double) (data.length - 1)));
	}

	
	/**
	 * Calculates the median skewness of the given data. More specifically, this is Pearson's second skewness
	 * coefficient. A negative value indicates a left skew, and a positive value indicates a right skew.
	 *
	 * @param	data	The data for which the median skewness is to be found.
	 * @return			The median skewness of the provided data. 0 if data consists of less than 3 entries or 
	 *					if it has a standard deviation of 0.0.
	 */
	public static double getMedianSkewness(double[] data)
	{
		if (data.length < 3)
			return 0.0;

		double mean = getAverage(data);
		double median = data[getIndexOfMedian(data)];
		double standard_deviation = getStandardDeviation(data);

		if (standard_deviation == 0.0)
			return 0.0;

		return 3.0 * (mean - median) / standard_deviation;
	}

	
	/**
	 * Calculates the median skewness of the given data. More specifically, this is Pearson's second skewness
	 * coefficient. A negative value indicates a left skew, and a positive value indicates a right skew.
	 *
	 * @param	data	The data for which the median skewness is to be found.
	 * @return			The median skewness of the provided data. 0 if data consists of less than 3 entries or
	 *					if it has a standard deviation of 0.0.
	 */
	public static double getMedianSkewness(short[] data)
	{
		if (data.length < 3)
			return 0.0;

		double[] copy = new double[data.length];
		for (int i = 0; i < data.length; i++)
			copy[i] = (double) data[i];
		
		return getMedianSkewness(copy);
	}

	
	/**
	 * Calculates the sample excess kurtosis for the given data. This measures how peaked or flat the data is.
	 * The higher the kurtosis, the more the data is clustered near the mean and the fewer outliers there are.
	 *
	 * @param	data	the data for which the median kurtosis is to be found.
	 * @return			The sample excess kurtosis of the provided data. 0 if data consists of less than 4 
	 *					entries or if the standard deviation is 0.0.
	 */
	public static double getSampleExcessKurtosis(double[] data)
	{
		if (data.length < 4)
			return 0.0;

		double n = data.length;
		double mean = getAverage(data);
		double standard_deviation = getStandardDeviation(data);

		if (standard_deviation == 0.0)
			return 0.0;

		double coefficient = (n * (n + 1.0)) / ((n - 1.0) * (n - 2.0) * (n - 3.0));

		double numerator = 0.0;
		for (int i = 0; i < n; i++)
			numerator += Math.pow((data[i] - mean), 4);

		double denominator = Math.pow(standard_deviation, 4);

		double second_term = (3.0 * (n - 1.0) * (n - 1.0)) / ((n - 2.0) * (n - 3.0));

		return (coefficient * numerator / denominator) + second_term;
	}

	
	/**
	 * Calculates the sample excess kurtosis for the given data. This measures how peaked or flat the data is.
	 * The higher the kurtosis, the more the data is clustered near the mean and the fewer outliers there are.
	 *
	 * @param	data	The data for which the median kurtosis is to be found.
	 * @return			The sample excess kurtosis of the provided data. 0 if data consists of less than 4 
	 *					entries.
	 */
	public static double getSampleExcessKurtosis(short[] data)
	{
		if (data.length < 4)
			return 0.0;

		double[] copy = new double[data.length];
		for (int i = 0; i < data.length; i++)
			copy[i] = (double) data[i];
		
		return getSampleExcessKurtosis(copy);
	}


	/**
	 * Generate a table with rows (first index) corresponding to the bins of the given histogram. Table
	 * entries are set to the magnitude of the corresponding bin of histogram if the magnitude in that bin of
	 * histogram is high enough to meet the column of the table entry's threshold requirements (see below),
	 * and to 0 otherwise. Column 0 has a threshold that only allows histogram bins with a magnitude over 0.1
	 * to be counted, column 1 has a threshold of higher than 0.01, and column 2 only counts histogram bins
	 * that have a magnitude at least 30% as high as the magnitude of the highest histogram bin. This table is
	 * then processed so that only peaks are included, which is to say that entries corresponding to bins of
	 * histogram that are adjacent to histogram bins with higher values are set to 0.
	 * 
	 * @param histogram	A histogram where each entry contains the magnitude of a bin. This histogram should
	 *					typically be normalized.
	 * @return			The table generated by the thresholding and the peak finding.
	 */
	public static double[][] calculateTablesOfThresholdedPeaks(double[] histogram)
	{
		// Instantiate thresholded_table and set entries to 0
		double[][] thresholded_table = new double[histogram.length][3];
		for (int i = 0; i < thresholded_table.length; i++)
			for (int j = 0; j < thresholded_table[i].length; j++)
				thresholded_table[i][j] = 0.0;

		// Find the highest frequency in the histogram
		double highest_frequency = histogram[getIndexOfLargest(histogram)];

		// Fill out thresholded_table
		for (int i = 0; i < histogram.length; i++)
		{
			if (histogram[i] > 0.1)
				thresholded_table[i][0] = histogram[i];
			if (histogram[i] > 0.01)
				thresholded_table[i][1] = histogram[i];
			if (histogram[i] > (0.3 * highest_frequency))
				thresholded_table[i][2] = histogram[i];
		}

		// Make sure all values refer to peaks (are not adjacent to higher values in thresholded_table)
		for (int i = 1; i < thresholded_table.length; i++)
			for (int j = 0; j < thresholded_table[i].length; j++)
				if (thresholded_table[i][j] > 0.0 && thresholded_table[i - 1][j] > 0.0)
				{
					if (thresholded_table[i][j] > thresholded_table[i - 1][j])
						thresholded_table[i - 1][j] = 0.0;
					else
						thresholded_table[i][j] = 0.0;
				}
		
		// Return results
		return thresholded_table;
	}	
	
	
	/**
	 * Returns the percentage of the given total that the given value represents.
	 *
	 * @param	value	The numerator in the percentage calculation.
	 * @param	total	The denominator in the percentage calculation.
	 * @return			The percentage.
	 */
	public static double getPercentage(int value, int total)
	{
		return 100.0 * ((double) value) / ((double) total);
	}


	/**
	 * Calculates the factorial of the given value.
	 *
	 * @param	n			The number to find the factorial of.
	 * @return				The factorial.
	 * @throws	Exception	Throws an informative exception if n is below 0 or greater than 20.
	 */
	public static long getFactorial(long n)
		throws Exception
	{
		if (n < 0)
			throw new Exception("Factorial input of " + n + " invalid: Must be 0 or greater.");
		else if (n > 20)
			throw new Exception("Factorial input of " + n + " invalid: Must be 20 or less.");
		else if (n == 0)
			return 1;
		else
			return n * getFactorial(n - 1);
	}


	/**
	 * Returns the given a raised to the power of the given b. IMPORTANT: b must be greater than zero.
	 *
	 * @param	a	The base.
	 * @param	b	The exponent.
	 * @return		The resultant value.
	 */
	public static int pow(int a, int b)
	{
		int result = a;
		for (int i = 1; i < b; i++)
			result *= a;
		return result;
	}


	/**
	 * Returns the logarithm of the specified base of the given number. IMPORTANT: Both x and n must be 
	 * greater than zero.
	 *
	 * @param	x	The value to find the log of.
	 * @param	n	The base of the logarithm.
	 * @return		The resultant value.
	 */
	public static double logBaseN(double x, double n)
	{
		return (Math.log10(x) / Math.log10(n));
	}


	/**
	 * If the given x is a power of the given n, then x is returned. If not, then the next value above the
	 * given x that is a power of n is returned. IMPORTANT: Both x and n must be greater than zero.
	 *
	 * @param	x	The value to ensure is a power of n.
	 * @param	n	The power to base x's validation on.
	 * @return		The resultant value.
	 */
	public static int ensureIsPowerOfN(int x, int n)
	{
		double log_value = logBaseN((double) x, (double) n);
		int log_int = (int) log_value;
		int valid_size = pow(n, log_int);
		if (valid_size != x)
			valid_size = pow(n, log_int + 1);
		return valid_size;
	}


	/**
	 * Returns whether or not x is either a factor or a multiple of y. z denotes the possible multipliers to
	 * check for. True is returned if x is either a factor of a multiple of y (and vice versa), and false
	 * otherwise.
	 *
	 * @param	x	The value that may be a factor or multiple of y.
	 * @param	y	The value that x may be a factor or multiple of.
	 * @param	z	The possible multipliers to check.
	 * @return		Whether or not x is either a factor of a multiple of y (and vice versa).
	 */
	public static boolean isFactorOrMultiple(int x, int y, int[] z)
	{
		boolean is_factor_or_multiple = false;

		if (y > x)
		{
			for (int i = 0; i < z.length; i++)
			{
				if ((x * z[i]) == y)
				{
					is_factor_or_multiple = true;
					i = z.length + 1; // exit loop
				}
			}
		}
		else
		{
			for (int i = 0; i < z.length; i++)
			{
				if ((y * z[i]) == x)
				{
					is_factor_or_multiple = true;
					i = z.length + 1; // exit loop
				}
			}
		}

		return is_factor_or_multiple;
	}	
	
	
	/**
	 * Returns the Euclidian distance between x and y. Throws an exception if x and y have different sizes.
	 *
	 * @param	x			The first vector.
	 * @param	y			The second vector.
	 * @return				The Euclidian distance between x and y.
	 * @throws	Exception	Throws an exception if x and y are of different sizes.
	 */
	public static double calculateEuclideanDistance(double[] x, double[] y)
		throws Exception 
	{
		if (x.length != y.length)
			throw new Exception("The two given arrays have different sizes.");

		double total = 0.0;
		for (int dim = 0; dim < x.length; dim++)
			total += Math.pow((x[dim] - y[dim]), 2);
		return Math.sqrt(total);
	}
	

	/**
	 * Calculates the number of permutations of the given parameters without replacement.
	 *
	 * For example, if one wishes to find the number of ordered ways that the letters C, A and T can be
	 * combined into a set of size 2, then the set_size paramter would be 3 (because there are three letters
	 * in the alphabet being used) and the permutation_size parameter would be 2.
	 *
	 * @param	set_size			The number of entries in the alphabet that can be used to construct 
	 *								permutations.
	 * @param	permutation_size	The size of the permutation sets to considered.
	 * @return						The number of possible permutations
	 * @throws	Exception			An informative Exception is thrown if set_size is greater than 20, as this
	 *								would necessitate the calculation of an overly large factorial. An 
	 *								Exception is also thrown if permutation_size is greater than set_size.
	 */
	public static int getNumberPerumutations(int set_size, int permutation_size)
		throws Exception
	{
		if (permutation_size > set_size)
			throw new Exception("Permutation set of " + permutation_size + " is larger than set size of " + set_size + ". Replacement is not permitted.");

		long numerator = getFactorial((long) set_size);
		long denominator = getFactorial((long) (set_size - permutation_size));
		return (int) (numerator / denominator);
	}
}