/*
 * ParsingXMLErrorHandler.java
 * Version 3.2
 *
 * Last modified on October 29, 2013.
 * Marianopolis College, McGill University and University of Waikato
 */

package mckay.utilities.xml;

import org.xml.sax.*;


/**
 * An implementation of the XML SAX ErrorHandler class. The methods of this
 * class are called by an instance of an XMLReader while it is parsing an XML
 * document.
 *
 * <p>This particular implementation simply throws unaltered exceptions of all
 * three standard types.
 *
 * @author Cory McKay
 */
public class ParsingXMLErrorHandler
     implements ErrorHandler
{
     public void warning(SAXParseException exception)
     throws SAXParseException
     {
          throw exception;
     }
     
     public void error(SAXParseException exception)
     throws SAXParseException
     {
          throw exception;
          
     }
     
     public void fatalError(SAXParseException exception)
     throws SAXParseException
     {
          throw exception;
     }
}
