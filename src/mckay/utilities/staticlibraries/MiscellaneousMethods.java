/*
 * NetworkMethods.java
 * Version 3.2
 *
 * Last modified on October 29, 2013.
 * Marianopolis College, McGill University and University of Waikato
 */

package mckay.utilities.staticlibraries;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Vector;

/**
 * A holder class for static methods for performing miscellaneous tasks.
 *
 * @author Cory McKay
 */
public class MiscellaneousMethods
{
	/**
	 * Runs the specified command as a subprocess in the environment of the
	 * specified runtime. Collects any output that it generatesm.
	 * 
	 * @param	command					The command to run.
	 * @param	run_time				The runtime to run the command in.
	 * @param	error_stream_reader		An array of size 1. The value of element
	 *									0 of this array will be changed to a new
	 *									InputStreamReader connected to the
	 *									error stream output of the subprocess
	 *									If null or an array of size != 1 is
	 *									passed in then this	parameter will be
	 *									ignored and the error stream will
	 *									therefore not be stored.
	 * @param	exit_code				An array of size 1. The value of element
	 *									0 of this array will be changed to
	 *									reflect the exit value of the process.
	 *									By convention, a value of 0 indicates
	 *									normal termination. If null or an array
	 *									of size != 1 is passed in then this
	 *									parameter will be ignored and the exit
	 *									code will therefore not be stored.
	 * @return							The standard output of the subprocess.
	 *									Each line of output is stored in a
	 *									separate extra element of the array.
	 *									Null is returned if there is no output.
	 * @throws	Exception				An exception is thrown if a problem
	 *									occurss
	 */
	public static String[] runCommand( String command,
			Runtime run_time,
			InputStreamReader[] error_stream_reader,
			int[] exit_code )
			throws Exception
	{
		// Execute the command
		Process process = run_time.exec(command);

		// Access the error stream if appropriate
		if (error_stream_reader != null)
			if (error_stream_reader.length == 1)
				error_stream_reader[0] = new InputStreamReader(process.getErrorStream());

		// Access the standard out
		Vector<String> output = new Vector<String>();
		BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line = null;
		while ((line = input.readLine()) != null)
			output.add(line);

		// Store the exit code, if apporpriate
		int exit_value = process.waitFor();
		if (exit_code != null)
			if (exit_code.length == 1)
				exit_code[0] = exit_value;

		// Return the output
		if (output.isEmpty()) return null;
		else return output.toArray(new String[output.size()]);
	}


	/**
	 * Validates and parses the given command line arguments. This method
	 * assumes that all command line arguments consist of flag/value pairs, and
	 * that all flags start with the "-" character. If invalid command line
	 * arguments are provided, then an explanation of the errors are printed to
	 * print_stream. An explanation of the permitted inputs are also printed to
	 * print_stream if explanations is non-null. Execution is terminated if
	 * invalid command line parameters are found.
	 *
	 * <p>If only a lone command line argument of "-help" is specified, then
	 * the valid command line arguments are printed to standard out and
	 * execution is terminated.
	 *
	 * @param args				The command line arguments to parse.
	 * @param permitted_flags	The keys of this HashMap represent the
	 *							permissible flags. Each flag maps to a Boolean,
	 *							which is true if the flag is mandatory, and
	 *							false if it is required. Note that these flags
	 *							must start with the "-" character. This may
	 *							not be null.
	 * @param explanation_keys	Each of the flags. Note that these flags
	 *							must start with the "-" character. Flags must
	 *							occur in the same order as in the explanations
	 *							parameter, and must match those in the
	 *							permitted_flags parameter (although the order
	 *							may vary from the latter. This may be null, in
	 *							which case explanations will not be printed out.
	 * @param explanations		Explanations of each of the flags. This will be
	 *							printed to print_stream if invalid input is
	 *							provided. This may be null, in which case this
	 *							output will not be provided.
	 * @param  print_stream		Where to print error messages indicating and
	 *							explaining invalid inputs. Typically standard
	 *							error or standard out.
	 * @return					A mapping between the flags that were provided
	 *							and the values for each of them. Null if invalid
	 *							input was provided.
	 */
	public static HashMap<String, String> parseCommandLineParameters( String[] args,
			HashMap<String, Boolean> permitted_flags,
			String[] explanation_keys,
			String[] explanations,
			PrintStream print_stream )
	{
		try
		{
			// Print out valid command line arguments if only -help is specified
			if (args.length == 1)
			{
				if (args[0].equals("-help"))
				{
					print_stream = System.out;
					throw new Exception("");
				}
			}

			// Verfify that there are an even number of command line arguments
			if ((args.length % 2) != 0)
				throw new Exception("An odd number of command line parameters were provided. Only flag/value are pairs accepted.");

			// Validate the arguments and parse them into parsed_args
			HashMap<String, String> parsed_args = new HashMap<String, String>();
			String current_flag = null;
			for (int i = 0; i < args.length; i++)
			{
				// Deal with flags
				if (i % 2 == 0)
				{
					// Verify that this is a valid flag
					if (args[i].length() < 2)
						throw new Exception ("There must be at least one flag and one value in the command line arguments.");
					if (!args[i].startsWith("-"))
						throw new Exception ("The \"" + args[i] + "\" flag does not start with a \"-\".");
					if (!permitted_flags.containsKey(args[i])) 
						throw new Exception ("\"" + args[i] + "\" is not a recognized flag.");
					if (parsed_args.containsKey(args[i]))
						throw new Exception("The flag \"" + args[i] + "\" appears more than once.");

					// Note the flag
					current_flag = args[i];
				}

				// Deal with values
				else
				{
					parsed_args.put(current_flag, args[i]);
				}
			}

			// Verify that all of the required flags are present
			String[] flags_allowed = permitted_flags.keySet().toArray(new String[1]);
			for (int i = 0; i < flags_allowed.length; i++)
				if (permitted_flags.get(flags_allowed[i]).booleanValue() && !parsed_args.containsKey(flags_allowed[i]))
					throw new Exception ("The mandatory flag " + flags_allowed[i] + " is missing.");

			// Return the parsed command line arguments
			return parsed_args;
		}
		catch (Exception e)
		{
			// e.printStackTrace();

			if (print_stream != null)
			{
				// Print the error message
				print_stream.println(e.getMessage());

				if (explanations != null && explanation_keys != null)
				{
					// Find the number of characthers for the first row
					int first_row_width = 0;
					for (int i = 0; i < explanation_keys.length; i++)
						if (explanation_keys[i].length() > first_row_width)
							first_row_width = explanation_keys[i].length();
					first_row_width += 3;

					// Print the valid output
					print_stream.println("\nValid flags are:\n");
					for (int i = 0; i < explanation_keys.length; i++)
					{
						print_stream.print(explanation_keys[i]);

						int number_spaces = first_row_width - explanation_keys[i].length();
						for (int j = 0; j < number_spaces; j++)
							print_stream.print(" ");

						boolean required = permitted_flags.get(explanation_keys[i]).booleanValue();
						if (required) print_stream.print("Required");
						else print_stream.print("Optional");

						print_stream.print("   " + explanations[i] + "\n\n");
					}

					print_stream.print("\nThese flags must each be followed by their associated value.\n\n");
				}
			}

			// Terminate execution
			System.exit(0);
			return null;
		}
	}
	
	
	/**
	 * Returns an integer representing a diatonic interval measure corresponding to the specified specific
	 * interval. For the purposes of this method, a diatonic interval is understood to be a measure of the
	 * number of major or minor scale steps connecting two pitches, and the specific interval is, for these
	 * purposes, the number of semitones separating the two pitches. Diatonic intervals are numbered here such
	 * that 1 corresponds to a unison, 2 to a second, 3 to a third and so on. Note, for example, that the
	 * intrinsically enharmonic nature of MIDI encoding makes it impossible to distinguish between an
	 * augmented fourth and a diminished fifth, so by arbitrary convention a tritone is treated as a fourth by
	 * this method. In this particular case, a diatonic interval is understood to allow octave expansions,
	 * meaning that generic values larger than an octave are permitted. However, if the specific interval is
	 * greater than 21, then a value of 128 is returned.
	 *
	 * @param	specific_interval		The number of semitones that a pitch interval between two notes spans.
	 *									This value should be positive for a rising interval and negative for a 
	 *									falling interval.
	 * @return							The number representing the diatonic interval that corresponds to the 
	 *									given specific interval. Will be positive for a rising interval and 
	 *									negative for a falling interval.
	 */
	public static int semitonesToDiatonicInterval(int specific_interval)
	{
		int generic_interval;
		
		switch (Math.abs(specific_interval))
		{
			// Assign a value for the generic interval based on the given number of semitones
			case(0): generic_interval = 1; break;
			case(1): generic_interval = 2; break;
			case(2): generic_interval = 2; break;
			case(3): generic_interval = 3; break;
			case(4): generic_interval = 3; break;
			case(5): generic_interval = 4; break;
			case(6): generic_interval = 4; break;
			case(7): generic_interval = 5; break;
			case(8): generic_interval = 6; break;
			case(9): generic_interval = 6; break;
			case(10): generic_interval = 7; break;
			case(11): generic_interval = 7; break;
			case(12): generic_interval = 8; break;
			case(13): generic_interval = 9; break;
			case(14): generic_interval = 9; break;
			case(15): generic_interval = 10; break;
			case(16): generic_interval = 10; break;
			case(17): generic_interval = 11; break;
			case(18): generic_interval = 11; break;
			case(19): generic_interval = 12; break;
			case(20): generic_interval = 13; break;
			case(21): generic_interval = 13; break;
			
			// If the interval is greater than 21 semitones, then the method will return 128
			default: return 128;
		}
		
		if (specific_interval < 0) 
			generic_interval = generic_interval * -1;
		
		return generic_interval;
	}
}