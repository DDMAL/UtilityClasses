/*
 * ParseFileHandler.java
 * Version 3.2
 *
 * Last modified on October 29, 2013.
 * Marianopolis College, McGill University and University of Waikato
 */

package mckay.utilities.xml;

import org.xml.sax.helpers.DefaultHandler;


/**
 * An extension of the Xerces XML DefaultHandler class that adds an array of
 * objects that can contain information derived from files during parsing.
 */
public class ParseFileHandler
     extends DefaultHandler
{
     /* FIELDS ****************************************************************/
     
     
     /**
      * Holds the data extracted from the XML file.
      */
     public Object[] parsed_file_contents;
     
     
     /**
      * Holds any comments extracted from the XML file.
      */
     public String comments;
}
