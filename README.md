# UtilityClasses 4.1
by Cory McKay
Copyright (C) 2022 (GNU GPL)


### OVERVIEW

UtilityClasses is an open source multi-purpose Java class library that
implements commonly used functionality. As a library, it does not have any
executable components.

UtilityClasses was developed as part of the jMIR music classification
research software suite, and may be used either as part of this suite or
may be incorporated into other software projects independently.


### GETTING MORE INFORMATION

More information on jMIR is available at http://jmir.sourceforge.net.

Please contact Cory McKay (cory.mckay@mail.mcgill.ca) with any bug reports
or questions relating to the software. 


### LICENSING AND LIABILITY

This program is free software; you can redistribute it and/or modify it under
the terms of the GNU General Public License as published by the Free Software
Foundation.

This program is distributed in the hope that it will be useful, but WITHOUT 
ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You may obtain a copy of the GNU General Public License by writing to the Free
Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

The UtilityClasses software makes use of the Yahoo! SDK to submit queries to
the Yahoo! engine. This SDK comes with a BSD license. Queries submitted to
Yahoo's Web Services must comply with Yahoo!'s terms of service, available at
http://docs.yahoo.com/info/terms. 


### COMPATIBILITY

The UtilityClasses software is written in Java, which means that it can in
principle be run on any system that has the Java Runtime Environment (JRE)
installed on it. It is particularly recommended that this software be used
with Windows or Linus, as it was developed and tested primarily under these
operating systems. Although the software should still run perfectly well on
OS X, Solaris or any other operating system with the JRE installed on it, 
users should be advised that this software has not yet been fully tested on 
these platforms, so difficulties may be encountered.

The current version of UtilityClasses was developed and tested with version 17 of the
JDK (Java Development Kit), and because it uses certain features not necessarily
present in earlier versions of Java, it is suggested that users have Java 17 or
later installed on their systems in order to run jSymbolic properly.


### INSTALLING THE JAVA RUNTIME ENVIRONMENT

If your system already has the JRE installed, as will most typically be the
case, you may skip this section. If not, you will need to install the JRE in
order to run this project. The JRE can be downloaded for free from the Java web
site. The JDK typically includes the JRE, or the JRE can simply be installed
alone.

When the JRE download is complete, follow the installation instructions that
come with it in order to install it.


### INSTALLING UtilityClasses

The project can be accessed at http://jmir.sourceforge.net. This distribution
includes a pre-compiled Jar file, the source code and extensive documentation. 
All of this is delivered in a zipped file, from which the project files can be
extracted.

Two directories are contained in the distribution zip file:

- Third-Party-Jars: Contains the distributable third-party software used by 
the project. These need to be included in the UtilityClasses build (in the
NetBeans context, this means adding the jar files found here as libraries).
- UtilityClasses: The UtilityClasses project, presented in the form of a
NetBeans project. Contains the following directories and files:
	- dist: Contains a pre-compiled UtilityClasses.jar file (and associated
	libraries) for direct inclusion in other projects.
	- javadoc: Javadoc documentation for the project source code.
	- licenses: Licenses associated with the project and its dependencies.
	- nbproject: NetBeans project files. This is only relevant to those
	wishing to use the software in a NetBeans IDE context; it is certainly
	possible to use the software in other development environments as well.
	- src: The project's source code.
	- test: The project's unit testing code.
	- manifest.mf: The manifest used when building the project Jar file.
	- nbbuild.xml: NetBeans build instructions. Only relevant if using the
	NetBeans IDE.
	- README.md: Basic overall documentation of the project.


### UPDATES SINCE VERSION 1.0

UtilityClasses 4.1
- Added new static statistics methods to MathAndStatsMethods

UtilityClasses 4.0
- Various bug fixes
- Substantial new miscellaneous methods (no changes to existing functionality)
- Updates to code documentation

UtilityClasses 3.1:
- mckay.utilities.webservices.ProxyServerAccessor.ProxyServerAccessor
	- A new class for testing for and configuring settings needing to
	access the web using a proxy server
- mckay.utilities.staticlibraries.StringMethods
	- Added the new passwordBasedDecrypt and passwordBasedEncryptin 
	methods for encrypting and decrypting strings
- mckay.utilities.staticlibraries.MiscellaneousMethods
	- A new class for performing miscellaneous taks. Includes a static
	method for running as a subprocess a command in a specified 
	environment and for collecting any resulting output. Also includes
	a method for parsing command line arguments.
- mckay.utilities.staticlibraries.NetworkMethods
	- A new class for performing network-related tasks. Includes static
	methods for making HTTP GET and POST requests to servers using Java.

UtilityClasses 3.0:
- Various bug fixes
- Various new functionality