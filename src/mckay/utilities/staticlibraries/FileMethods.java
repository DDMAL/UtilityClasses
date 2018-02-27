/*
 * FileMethods.java
 * Version 3.2
 *
 * Last modified on March 1, 2016.
 * Marianopolis College, McGill University and University of Waikato
 */

package mckay.utilities.staticlibraries;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.JOptionPane;


/**
 * A holder class for static methods relating to files.
 *
 * @author Cory McKay
 */
public class FileMethods
{
     /**
      * Gets a new File object to write to based on the given path. If the
      * can_erase parameter is false, then the user is given a warning message
      * through the GUI asking him/er if s/he wishes to overwrite the file.
      * Returns null if the choice is to not overwrite file. Attempts to write
      * an empty string and displays an error message if this cannot be done
      * (also returns null in this case).
      *
      * @param	path          The path to which the file is to be saved.
      * @param	can_erase     Whether or not the file should be automatically
      *                       overwritten if it already exists.
      * @return               Returns the requested file, or null if a fie
      *                       cannot be written to.
      */
     public static File getNewFileForWriting( String path,
          boolean can_erase )
     {
          // Check to see if should overwrite a file
          boolean go_ahead = true;
          File to_file = new File(path);
          if (to_file.exists() && can_erase == false)
          {
               int response = JOptionPane.showConfirmDialog(null, "A file " +
                    "with the path " + path + " already exists.\nDo you wish to overwrite it?", "Warning",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
               if (response != JOptionPane.YES_OPTION)
                    go_ahead = false;
          }

          // Check that can write to file and return appropriate value
          if (go_ahead == true)
          {
               try
               {
                    DataOutputStream writer = getDataOutputStream(to_file);
                    writer.writeBytes("");
                    writer.close();
                    return to_file;
               }
               catch (Exception e)
               {
                    JOptionPane.showMessageDialog(null, "Unable to write file.", "ERROR", JOptionPane.ERROR_MESSAGE);
                    // e.printStackTrace();
                    return null;
               }
          }

          // Return indication that can or should not write to file
          return null;
     }


     /**
      * Gets a new File object to write to based on the given path. Attempts to 
	  * write an empty string to the file as a test.
      *
      * @param	path          The path to which the file is to be saved.
      * @param	can_erase     Whether or not the file should be automatically
      *                       overwritten if it already exists.
	  * @throws Exception     Throws an informative Exception if a file
	  *						  already exists at the given location and can_erase
	  *						  is false, or if a problem occurs during test
	  *                       writing.
      * @return               Returns the requested file.
      */
     public static File getNewFileForWritingNoDialog( String path, boolean can_erase )
	     throws Exception
     {
          // Check to see if should overwrite a file
          File to_file = new File(path);
          if (to_file.exists() && can_erase == false)
			  throw new Exception("Could not save a writable file. A file already exists at " + path + ".");

          // Check that can write to file and return appropriate value
          try
          {
             DataOutputStream writer = getDataOutputStream(to_file);
             writer.writeBytes("");
             writer.close();
             return to_file;
          }
          catch (Exception e)
          {
             throw new Exception("Could not save a writable file at " + path + ": " + e.getMessage());
             // e.printStackTrace();
          }
     }


     /**
      * Prepares a DataOutputStream that can be used to write a file. The user
      * must remember to close this DataOutputStream after writing is complete.
      *
      * @param      file                     The File that will be written to.
      * @return                              A new DataOutputStream for the
      *                                      given file.
      * @throws     FileNotFoundException    This exception is thrown if the
      *                                      given file cannot be found.
      */
     public static DataOutputStream getDataOutputStream(File file)
     throws FileNotFoundException
     {
          FileOutputStream to = new FileOutputStream(file);
          return new DataOutputStream(to);
     }
	 
	 
	/**
	 * Verify that the given path is a legitimate path to a file or directory. The path may refer to an
	 * existing file or directory, or it may simply be a potential path. An informative exception is thrown if
	 * a problem is detected with the specified path. The passed path_to_check_string is left unaltered, and
	 * no manipulations are performed at the specified path (i.e. no actual files or directories are created,
	 * modified or deleted).
	 *
	 * <p>This method checks both the specific aspects specified by its arguments as well as additional 
	 * general legitimacy checks (e.g. the path refers to a location (parent directory) that actually exists,
	 * the path is not empty, there are no illegal characters in the path, etc.). Some of these general
	 * legitimacy checks depend to a certain extent on the current operating system, so this method should 
	 * only be called while running the JVM on the same operating system that the ultimate file access will be
	 * performed on if one wishes to ensure that there will not be a problem when the file access occurs.</p>
	 * 
	 * <p>Certain combinations of input arguments will always result in an exception being thrown (e.g. if 
	 * both the must_exist and must_not_exist arguments are true), so the user should be sure to only choose
	 * such combinations if specific kinds of exceptions are wanted.</p>
	 * 
	 * @param path_to_check_string	The path to a file or directory that is to be checked for legitimacy.
	 * @param must_be_regular_file	True if the path must refer to a regular file (e.g. not a directory).
	 * @param must_be_directory		True if the path must refer to a directory.
	 * @param must_be_readable		True if the path must refer to a readable file or directory. This argument
	 *								is ignored if the file does not in fact exist (no check is performed in
	 *								such a case).
	 * @param must_be_writable		True if the path must refer to a writable file or directory. This argument
	 *								is ignored if the file does not in fact exist (no check is performed in
	 *								such a case).
	 * @param must_exist			True if the path must refer to a file or directory that actually exists.
	 * @param must_not_exist		True if the path must refer to a file or directory that must not yet
	 *								actually exist.
	 * @throws Exception			An informative exception is thrown if the path is invalid for any general
	 *								reasons, or if it violates any requirements specified by the arguments are
	 *								not met.
	 */
	public static void verifyValidPath( String path_to_check_string,
	                                    boolean must_be_regular_file,
	                                    boolean must_be_directory,
	                                    boolean must_be_readable,
	                                    boolean must_be_writable,
	                                    boolean must_exist,
	                                    boolean must_not_exist )
		throws Exception
	{
		// Variables to hold various representations of the path
		File file_to_check = null;
		Path path_to_check = null;
		
		// Verify that a path to check is provided
		if (path_to_check_string == null)
			throw new Exception("No path is specified.");
		if (path_to_check_string.equals(""))
			throw new Exception("An empty path is specified.");

		// Verify that path_to_check is a properly formed path
		try
		{
			path_to_check = Paths.get(path_to_check_string);
			file_to_check = new File(path_to_check_string);
			file_to_check.getCanonicalFile();
		}
		catch (InvalidPathException e)
		{
			throw new Exception( "The specified path is malformed:\n" +
			                     "     " + path_to_check_string + "\n" +
			                     "Reason: " + e.getReason() );
		}
		catch (Exception e)
		{
			throw new Exception( "The specified path is malformed:\n" +
			                     "     " + path_to_check_string + "\n");
		}

		// Verify that the parent directory referred to exists
		try
		{
			if (!file_to_check.getParentFile().exists())
				throw new Exception();
		}
		catch (Exception e)
		{
			throw new Exception( "The specified path does not refer to an existing parent directory:\n" + 
			                     "     " + path_to_check_string);
		}
		
		// Deal with a situation where the specified path corresponds to a file that already exists
		if (file_to_check.exists())
		{
			if (must_not_exist)
				throw new Exception( "The specified path corresponds to the path of an already existing resource:\n" + 
									 "     " + path_to_check_string);			
			else if (must_be_readable && !Files.isReadable(path_to_check))
				throw new Exception( "The specified path is not readable:\n" + 
									 "     " + path_to_check_string);
			else if (must_be_writable && !Files.isWritable(path_to_check))
				throw new Exception( "The specified path is not writable:\n" + 
									 "     " + path_to_check_string);
			else if (must_be_regular_file && !Files.isRegularFile(path_to_check))
			{
				if (Files.isDirectory(path_to_check))
					throw new Exception( "The specified path refers to a directory rather than a file:\n" + 
										 "     " + path_to_check_string);
				else
					throw new Exception( "The specified path refers to a non-regular file:\n" + 
										 "     " + path_to_check_string);
			}
			else if (must_be_directory && !Files.isDirectory(path_to_check))
				throw new Exception( "The specified path refers to a file rather than a directory:\n" + 
									 "     " + path_to_check_string);		
		}
		
		// Deal with a situation wher the specified path does not correspond to any already existing files
		else
		{
			if (must_exist)
				throw new Exception( "The specified path does not correspond to an already existing resource:\n" + 
									 "     " + path_to_check_string);
			
			// Check if would be a directory
			boolean is_directory = false;
			char last_char = path_to_check_string.charAt(path_to_check_string.length() - 1);
			if ( last_char == '/' ||
			     last_char == '\\' ||
			     last_char == ':' )
				is_directory = true;

			if (must_be_regular_file && is_directory)
				throw new Exception( "The specified path is formulated as a directory path rather than a file path:\n" + 
									 "     " + path_to_check_string);
			else if (must_be_directory && !is_directory)
				throw new Exception( "The specified path is forumulated as a file path rather than a directory path:\n" + 
									 "     " + path_to_check_string);			
		}
	}
	 

     /**
      * Tests the given file to see if it is a valid file.
      *
      * @param file           The file to test.
      * @param need_read      If this is set to true, then an exception is
      *                       sent if the file cannot be read from.
      * @param need_write     If this is set to true, then an exception is
      *                       sent if the file cannot be written to.
      * @return               Value of true if no problems occurred during
      *                       file validation.
      * @throws Exception     An informative exception is thrown if there is
      *                       a problem with the file.
      */
     public static boolean validateFile(File file, boolean need_read,
          boolean need_write)
          throws Exception
     {
          if (file == null)
               throw new Exception("Empty file reference provided.");
          if (!file.exists())
               throw new Exception("File " + file.getPath() + " does not exist.");
          if (file.isDirectory())
               throw new Exception("Reference to a directory instead of a file: " + file.getPath() + ".");
          if (!file.isFile())
               throw new Exception("Reference to " + file.getPath() + "is not a valid file.");
          if (need_read && !file.canRead())
               throw new Exception("Cannot read from file " + file.getPath() + ".");
          if (need_write && !file.canWrite())
               throw new Exception("File " + file.getPath() + " cannot be written to.");

          return true;
     }


     /**
      * Tests the given File to see if it is a valid directory.
      *
      * @param file           The file to test.
      * @param need_read      If this is set to true, then an exception is
      *                       sent if the directory cannot be read from.
      * @param need_write     If this is set to true, then an exception is
      *                       sent if the directory cannot be written to.
      * @return               Value of true if no problems occured during
      *                       file validation.
      * @throws Exception     An informative exception is thrown if there is
      *                       a problem with the file.
      */
     public static boolean validateDirectory(File file, boolean need_read,
          boolean need_write)
          throws Exception
     {
          if (file == null)
               throw new Exception("Empty directory reference provided.");
          if (!file.exists())
               throw new Exception("Directory " + file.getPath() + " does not exist.");
          if (file.isFile())
               throw new Exception("Reference to a file instead of a directory: " + file.getPath() + ".");
          if (!file.isDirectory())
               throw new Exception("Reference to " + file.getPath() + "is not a valid directory.");
          if (need_read && !file.canRead())
               throw new Exception("Cannot read from directory " + file.getPath() + ".");
          if (need_write && !file.canWrite())
               throw new Exception("Directory " + file.getPath() + " cannot be written to.");

          return true;
     }


     /**
      * Returns all files meeting the requirements of the given filter in the
      * given directory and, if requested, its subdirectories.
      *
      * @param directory                The directory to explore.
      * @param explore_subdirectories   Whether or not the subdirectories of
      *                                 directory should be explored.
      * @param filter                   A filter controlling what files are
      *                                 elligible to be returned. A value of
      *                                 null means all files found will be
      *                                 returned. Note that this filter
	  *									could potentially cause directories
	  *									that do not meet its requirements to
	  *									not be returned.
      * @param results                  Used internally for recursive calls.
      *                                 Pass null when calling this method
      *                                 externally.
      * @return                         An array containing all files meeting
      *                                 the requirements of the other
      *                                 parameters. Null is returned if no
      *                                 files can be found or if the given
      *                                 directory is not a valid directory.
      */
     public static File[] getAllFilesInDirectory(File directory, boolean explore_subdirectories,
          FileFilter filter, Vector<File> results)
     {
          // Ensure that the passed parameter is a valid directory
          if (!directory.isDirectory())
               return null;

          // Initialize the vector that will store found files
          if (results == null)
               results = new Vector<File>();

          // Find elligible files recursively
          File[] in_this_directory = directory.listFiles(filter);
          for (int i = 0; i < in_this_directory.length; i++)
          {
               if (in_this_directory[i].isDirectory())
               {
                    if (explore_subdirectories)
                         getAllFilesInDirectory(in_this_directory[i], explore_subdirectories,
                              filter, results);
               }
               else
                    results.add(in_this_directory[i]);
          }

          // Convert the results to a file array
          File[] results_array = results.toArray(new File[1]);

          // Return null if no elligible files were found
          if (results_array[0] == null)
               return null;

          // Return the results
          return results_array;
     }


     /**
	  * Finds all files meeting the requirements of the given filter in the given directory and, if requested,
	  * its subdirectories.
	  * 
	  * @param directory				The directory to explore.
	  * @param explore_subdirectories	Whether or not the subdirectories of directory should be explored
	  *									recursively.
	  * @param filter					A filter controlling what files are eligible to be returned. A value
	  *									of null means all files found will be returned.
	  * @param results					A list of files to which new files found will be added.
	  * @throws Exception				Throws an informative exception if a non-directory is specified.
	  */
	 public static void addAllFilesInDirectory( File directory,
												boolean explore_subdirectories,
												FileFilter filter,
												ArrayList<File> results )
          throws Exception
     {
          // Ensure that the passed parameter is a valid directory
          if (!directory.isDirectory())
               throw new Exception("The specified directory " + directory.getPath() + " does not exist.");

          // Find elligible files recursively
          File[] in_this_directory = directory.listFiles(filter);
          for (int i = 0; i < in_this_directory.length; i++)
          {
               if (in_this_directory[i].isDirectory())
               {
                    if (explore_subdirectories)
                         addAllFilesInDirectory( in_this_directory[i],
								                 explore_subdirectories,
                                                 filter,
												 results );
               }
			   else results.add(in_this_directory[i]);
          }
     }
	 
	 
	 /**
	  * Return a report indicating which files are present in first_root_directory but not in 
	  * second_root_directory. This search includes sub-directories of both root directories if
	  * explore_subdirectories is set to true; in this case, files must be in the same sub-directory structure
	  * to be considered matching. There is the option to remove some number of characters from the ends of
	  * file names for the purpose of the comparison, if desired.
	  * 
	  * @param first_root_directory				The directory to look for files that should be present in the
	  *											second directory.
	  * @param second_root_directory			The directory to check to see if files are present from the
	  *											first directory.
	  * @param explore_subdirectories			Whether or not to include sub-directories from both root
	  *											directories in the comparison.
	  * @param first_num_characters_at_ends		The number of characters to discount at the ends of filenames
	  *											in first_root_directory, including the extension.
	  * @param second_num_characters_at_ends	The number of characters to discount at the ends of filenames
	  *											in second_root_directory, including the extension.
	  * @return									A report enumerating the files in first_root_directory that
	  *											could not be found in second_root_directory.
	  */
	 public static String verifyMatchingFilesExist( String first_root_directory,
			                                        String second_root_directory,
												    boolean explore_subdirectories,
												    int first_num_characters_at_ends,
													int second_num_characters_at_ends )
	 {
		// A report on all errors encountered (leave as null if none found)
		String error_report = null;
		
		// The files to compare
		File[] first_files = FileMethods.getAllFilesInDirectory(new File(first_root_directory), explore_subdirectories, null, null);
		String[] first_files_paths = new String[first_files.length];
		for (int i = 0; i < first_files.length; i++)
			first_files_paths[i] = first_files[i].getAbsolutePath();
		File[] second_files = FileMethods.getAllFilesInDirectory(new File(second_root_directory), explore_subdirectories, null, null);
		String[] second_files_paths = new String[second_files.length];
		for (int i = 0; i < second_files.length; i++)
			second_files_paths[i] = second_files[i].getAbsolutePath();
		
		// Strip array the root directories
		int chars_to_strip = (new File(first_root_directory)).getAbsolutePath().length();
		for (int i = 0; i < first_files_paths.length; i++)
			first_files_paths[i] = first_files_paths[i].substring(chars_to_strip);
		chars_to_strip = (new File(second_root_directory)).getAbsolutePath().length();
		for (int i = 0; i < second_files_paths.length; i++)
			second_files_paths[i] = second_files_paths[i].substring(chars_to_strip);
		
		// Strip array end characters
		if (first_num_characters_at_ends > 1)
			for (int i = 0; i < first_files_paths.length; i++)
				first_files_paths[i] = first_files_paths[i].substring(0, first_files_paths[i].length() - first_num_characters_at_ends);
		if (second_num_characters_at_ends > 1)
			for (int i = 0; i < second_files_paths.length; i++)
				second_files_paths[i] = second_files_paths[i].substring(0, second_files_paths[i].length() - second_num_characters_at_ends);

		// Find all files from first_root_directory that are not in second_root_directory
		ArrayList<String> not_there = new ArrayList<>();
		for (int i = 0; i < first_files_paths.length; i++)
		{
			boolean found = StringMethods.isStringInArray(first_files_paths[i], second_files_paths);
			if (!found)
				not_there.add(first_files[i].getAbsolutePath());
		}
		if (!not_there.isEmpty())
		{
			error_report = not_there.size() + " FILES ARE IN " + first_root_directory + " BUT NOT " + second_root_directory + ":\n";
			for (int i = 0; i < not_there.size(); i++)
				error_report += not_there.get(i) + "\n";
		}		
		
		// Return the results
		return error_report;		 
	 }
	 
	 
	 /**
 	  * Creates a directory if it does not already exist at the given path. If a
	  * non-directory file exists at the given path, then delete it. If a directory
	  * already exists at the given path, then do nothing.
	  *
	  * @param path	The path of the directory to create.
	  * @return		True if a directory exists at the given path now, false if it
	  *				does not.
	  */
	 public static boolean createDirectory(String path)
	 {
		 // Prepare the new directory pointer
		 File directory = new File(path);
		 
		 // Delete a non-directory file if it exists at the given path, do nothing
		 // if a directory already exists at the given path
		 if (directory.exists())
		 {
			 if (!directory.isDirectory())
			 {
				 boolean result = directory.delete();
				 if (!result) return false;
			 }
			 else return true;
		 }
		 
		 // Create a directory at the given path
		 return directory.mkdirs();
	 }
	 
	 
     /**
      * Creates a directory at the given path. If a file already exists at the
      * given path, it is deleted. All contents of a directory that already
      * exists at the given path are deleted.
      *
      * <p>If a deletion fails, then the method stops attempting to delete and
      * returns false.
      *
      * @param path      The path at which a directory is to be created.
      * @return          True if the directory was succesfully created. False
      *                   if the directory could not be created or a pre-existing
      *                   item could not be deleted.
      */
     public static boolean createEmptyDirectory(String path)
     {
          File directory = new File(path);

          if (directory.exists())
          {
               boolean success = deleteDirectoryRecursively(directory);
               if (!success)
                    return false;
          }

          return directory.mkdirs();
     }


     /**
      * Deletes all files and sub-directories in the given directory. If the
      * given directory is actually a file, then it is just deleted.
      *
      * <p>If a deletion fails, then the method stops attempting to delete and
      * returns false.
      *
      * @param directory The directory to delete.
      * @return          True if the directory and its contents were succesfully
      *                  deleted, false if a failure to delete occured.
      */
     public static boolean deleteDirectoryRecursively(File directory)
     {
          // Delete directory contents recursively
          if (directory.isDirectory())
          {
               String[] children = directory.list();
               for (int i=0; i<children.length; i++)
               {
                    boolean success = deleteDirectoryRecursively(new File(directory, children[i]));
                    if (!success)
                         return false;
               }
          }

          // Delete empty directory or file
          return directory.delete();
     }


     /**
      * Copies the contents of one file to another.
      * Throws an exception if the destination file already exists
      * or if the original file does not exist.
      *
      * @param	original	The name of the file to be copied.
      * @param	destination	The name of the file to be copied to.
      * @throws Exception       An exception is thrown if a problem occurs
      *                         during copying.
      */
     public static void copyFile(String original, String destination)
     throws Exception
     {
          File original_file = new File(original);
          File destination_file = new File(destination);
          if (!original_file.exists())
               throw new Exception("File with path " + original + " does not exist.");
          if (destination_file.exists())
               throw new Exception("File with path " + destination + " already exists.");
          FileReader in = new FileReader(original_file);
          FileWriter out = new FileWriter(destination_file);
          int c;
          while ((c = in.read()) != -1)
               out.write(c);
          in.close();
          out.close();
     }

	 
	/**
	 * Copy all of the files in the input_parent_directory (and its subdirectories, if the 
	 * explore_subdirectories argument is set to true) to the output_parent_directory root folder. Both
	 * output_parent_directory and any necessary sub-directories are created if they are not already present.
	 * The newly copied files are given new names, consisting of the specified new_base_name followed by a 
	 * six-digit integer indicating the order in which the file was copied. The original file extension is 
	 * kept. Finally, a mapping text file is saved, where each line corresponds to a file that was copied,
	 * and where each line consists of first the original file path, followed by the specified 
	 * mappings_separator, followed by the destination file path.
	 * 
	 * @param input_parent_directory	The root folder to look for files to copy.
	 * @param output_parent_directory	The root folder to copy files to (and create sub-directories in, if
	 *									appropriate.
	 * @param new_base_name				The base file name to use for all saved copies.
	 * @param explore_subdirectories	Whether or not to include the sub-directories of 
	 *									input_parent_directory in processing.
	 * @param mappings_file_save_path	Where to save the generated mappings file. No file is saved if this is
	 *									null.
	 * @param mappings_separator		The delimiter to use in the generated mappings file, separating source
	 *									and destination files.
	 * @throws Exception				Throws an informative exception if a problem occurs.
	 */
	public static void copyAndRenameFiles( String input_parent_directory,
	                                       String output_parent_directory,
                                           String new_base_name,
									       boolean explore_subdirectories,
										   String mappings_file_save_path,
										   String mappings_separator )
			throws Exception
	{
		// The files to copy
		File[] files_to_copy = FileMethods.getAllFilesInDirectory(new File(input_parent_directory), explore_subdirectories, null, null);
		
		// The paths to copy the files to
		String[] paths_to_copy_to = new String[files_to_copy.length];
		
		// Update the paths_to_copy_to so that they reflect the new path, and using the current file system's
		// format. Orignal file names are still kept for now, however.
		String formatted_output_parent_directory = (new File(output_parent_directory)).getAbsolutePath() + File.separator;
		for (int i = 0; i < paths_to_copy_to.length; i++)
		{
			paths_to_copy_to[i] = files_to_copy[i].getAbsolutePath().substring(input_parent_directory.length());
			paths_to_copy_to[i] = formatted_output_parent_directory + paths_to_copy_to[i];
		}

		// Replace the old file names with the new ones, keeping the old extensions.
		for (int i = 0; i < paths_to_copy_to.length; i++)
		{
			String formatted_number = String.format("%06d", (i+1));
			String extension = StringMethods.getExtension(files_to_copy[i].getAbsolutePath());
			paths_to_copy_to[i] = (new File(paths_to_copy_to[i])).getParent() + File.separator + new_base_name + formatted_number + extension;
		}
		
		// Copy the files one by one, creating new directories as necessary.
		for (int i = 0; i < files_to_copy.length; i++)
		{
			// Note progress
			System.out.println("COPYING " + (i+1) + " OF " + (files_to_copy.length+1) + ": " + files_to_copy[i].getAbsolutePath());

			// Create the destination directory for the file (if it does not already exist).
			FileMethods.createDirectory((new File(paths_to_copy_to[i])).getParent());

			// Copy the file
			try { Files.copy( files_to_copy[i].toPath(), (new File(paths_to_copy_to[i])).toPath() ); }
			catch (Exception e) {throw new Exception("Could not copy file " + files_to_copy[i].getAbsolutePath() + " to " + paths_to_copy_to[i] );}
		}
		
		// Save the mappings file
		if (mappings_file_save_path != null)
		{
			// Save the new combined feature values Weka-compatible CSV file
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(mappings_file_save_path)))
			{
				for (int i = 0; i < files_to_copy.length; i++)
				{
					writer.write(files_to_copy[i].getAbsolutePath());
					writer.write(mappings_separator);
					writer.write(paths_to_copy_to[i]);
					writer.newLine();
				}
				writer.close();
			}
			catch (Exception e)	{ throw new Exception("Error when trying to save mappings file to \"" + mappings_file_save_path + "\": " + e.getMessage()); }
		}
	}	
		

	 /**
	  * Parses the specified text file into a String. New line characters are
	  * changed into the current system's line separators. The text is otherwise
	  * left unchanged.
	  *
	  * @param to_parse		The file to parse.
	  * @return				The parsed contents of the file
	  * @throws Exception	An informative exception is thrown if a problem occurs.
	  */
	 public static String parseTextFile(File to_parse)
	 	throws Exception
	 {
         // Ensure that the file can be read
         validateFile(to_parse, true, false);

		 // Prepare to read the file
		 StringBuilder contents = new StringBuilder();
		 BufferedReader buffered_input =  new BufferedReader(new FileReader(to_parse));

		 // Read the file
		 try
		 {
			 String line = null; //not declared within while loop
			 while (( line = buffered_input.readLine()) != null)
			 {
				 contents.append(line);
				 contents.append(System.getProperty("line.separator"));
			 }
		 }
		 finally {buffered_input.close();}

		 // Return the parsed results
		 return contents.toString();
	 }


     /**
      * Parses the given text file. The parsed file is considered to comprise a
      * list. Each line is counted as a separate item in the list. Blank lines
      * are treated as an item in the list consisting of "". An array of strings
      * is returned with one entry for each item in the list (i.e. each line).
      * This array is not sorted or otherwise processed, but no entries may be
      * null. A descriptive exception is thrown if a problem occurs during
      * paring. Parses special characters.
      *
      * @param      to_parse  The file to parse.
      * @return               The parsed contents of the file.
      * @throws     Exception An informative description of any problem that
      *                       occurs during parsing.
      */
     public static String[] parseTextFileLinesWithSpecialCharacters(File to_parse)
     throws Exception
     {
          // Ensure that the file can be read
          validateFile(to_parse, true, false);

          // Prepare file reader
          InputStreamReader reader = new InputStreamReader(new FileInputStream(to_parse),"ISO-8859-1");

          // Prepare the file parser
          BufferedReader parser = new BufferedReader(reader);

          // Read lines one by one
          Vector<String> parsed_lines = new Vector<String>();
          String this_line = "";
          while ((this_line = parser.readLine()) != null)
               parsed_lines.add(this_line);

          // Return the parsed results
          return parsed_lines.toArray(new String[1]);
     }
	 
	 
	 /**
      * Parses the given text file. The parsed file is considered to comprise a
      * list. Each line is counted as a separate item in the list. Blank lines
      * are treated as an item in the list consisting of "". An array of strings
      * is returned with one entry for each item in the list (i.e. each line).
      * This array is not sorted or otherwise processed, but no entries may be
      * null. A descriptive exception is thrown if a problem occurs during
      * paring. WARNING: Does not parse special characters properly.
      *
      * @param      to_parse  The file to parse.
      * @return               The parsed contents of the file.
      * @throws     Exception An informative description of any problem that
      *                       occurs during parsing.
      */
     public static String[] parseTextFileLines(File to_parse)
     throws Exception
     {
          // Ensure that the file can be read
          validateFile(to_parse, true, false);

          // Prepare file reader
          FileReader reader = new FileReader(to_parse);

          // Prepare the file parser
          BufferedReader parser = new BufferedReader(reader);

          // Read lines one by one
          Vector<String> parsed_lines = new Vector<String>();
          String this_line = "";
          while ((this_line = parser.readLine()) != null)
               parsed_lines.add(this_line);

          // Return the parsed results
          return parsed_lines.toArray(new String[1]);
     }
	 

	 /**
      * Parses the given CSV (or similar delimited) text file, with the specified delimiter. Parses special 
	  * characters correctly.
	  * 
	  * @param to_parse		The file to parse.
	  * @param delimiter	The delimiter separating values on a line (such as a comma, or a comma surrounded
	  *						by spaces.
	  * @return				The parsed contents of the file, where the first index indicates line and the
	  *						second indicates comma-separated entry.
	  * @throws Exception	An informative description of any problem that occurs during parsing.
	  */
	 public static String[][] parseCsvFileWithSpecialCharacters(File to_parse, String delimiter)
	 throws Exception
	 {
		// Break up to_parse into individual lines
		String[] original_lines = parseTextFileLinesWithSpecialCharacters(to_parse);
		
		// To hold parsed file contents
		String[][] parsed_contents = new String[original_lines.length][];
		 
		// Break up each line based on a single comma delimiter
		for (int i = 0; i < parsed_contents.length; i++)
			parsed_contents[i] = original_lines[i].split(delimiter);
			
		// Return the parsed results
		return parsed_contents; 
	 }
}
