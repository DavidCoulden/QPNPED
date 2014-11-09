-----------------------------USAGE----------------------------------
AutoQPNPED is run using the runnable jar, AutoQPNPED.jar, inside the dist folder and accepts a variety of command line arguments. AutoQPNPED has two different 
modes: QPN generation and QPN simulation. By default it is in QPN generation mode. To run the jar use the command: java -jar AutoQPNPED.jar <command line arguments here>.


----QPN generation mode-----
This is the default mode of AutoQPNPED. It takes an input traffic specification file and outputs a QPN in QPME format that models
this traffic specification. The options available for this mode are as follows:
 ____________________________________________________________________________________________________________________________________________
|   Option  |     Long option     |    Arguments    |  Description                                                                           |
|___________|_____________________|_________________|________________________________________________________________________________________|           
|    -f     |   --specification   |      file       | The traffic specification file used as input. This is required for all QPN generation. |
|___________|_____________________|_________________|________________________________________________________________________________________|
|    -s     |--require-annotations|      none       | Sets the specification reader to expect every statement to be annotated with the table |
|           |                     |                 | accessed, the exclusivity of access and the execution time of the statement. As a      |
|           |                     |                 | consequence of this the atomization and inference step of AutoQPNPED is skipped.       |
|___________|_____________________|_________________|________________________________________________________________________________________|
|    -i     | --annotated-output  |    filename     | Outputs a new annotated version of the traffic specification to the given filename.    |
|           |                     |                 | The annotated specification will contain the atomized and annotated statements of the  |
|           |                     |                 | original input specification.                                                          |
|___________|_____________________|_________________|________________________________________________________________________________________|
|    -V     |  --viewable-qpnout  |    filename     | The output destination for the viewable QPN file. The generated QPN is in an XML format|
|           |                     |                 | viewable in QPME.                                                                      |
|___________|_____________________|_________________|________________________________________________________________________________________|
|    -o     |  --template-qpnout  |    filename     | The output destination for the template QPN file. The generated QPN is in an XML format|
|           |                     |                 | compatible with QPME however is in not viewable in the QPME editor as client numbers   |
|           |                     |                 | are replaced with placeholders. AutoQPNPED when in run mode can convert the template   |
|           |                     |                 | into valid QPME QPN files.                                                             |
|___________|_____________________|_________________|________________________________________________________________________________________|

----QPN simulation mode (runner mode)-----
This mode is used to simulate generated QPN models over a specified client range. The options available for this mode are as follows:
 ____________________________________________________________________________________________________________________________________________
|   Option  |     Long option     |    Arguments    |  Description                                                                           |
|___________|_____________________|_________________|________________________________________________________________________________________|
|    -r     |     --run-mode      |      file       | Causes AutoQPNPED to start in runner mode. This allows automated invocation of QPME    |
|           |                     |                 | simulations. The filename provided is the template QPN that is going to be simulated   |
|           |                     |                 | over varying client amounts.                                                           |
|___________|_____________________|_________________|________________________________________________________________________________________|
|    -c     |     --clients       | intial clients, | Specifies the range of clients amounts to run the simulations over. Allowing the start |
|           |                     | end clients,    | and end amounts to be specified as well as a step value to give client amounts         |
|           |                     | client step     | inbetween.                                                                             |
|___________|_____________________|_________________|________________________________________________________________________________________|
|    -n     |     --run-id        |   identifier    | Associates the value given with all the results of the simulation. Used to uniquely    |
|           |                     |                 | identify the results of the simulation by incoporating the value into filenames.       |
|___________|_____________________|_________________|________________________________________________________________________________________|
|    -d     |     --sim-dir       |    directory    | The output directory for the results of the simulations.                               |
|___________|_____________________|_________________|________________________________________________________________________________________|
|    -df    |   --sim-datafile    |    filename     | The output file that contains the mean response times for transactions. The times are  |
|           |                     |                 | taken from the simulation results.                                                     |
|___________|_____________________|_________________|________________________________________________________________________________________|

---------------------------CONFIGURATION----------------------------------
Inside the dist folder there are two configuration files: dbconn.properties and dbtool.properties. dbconn.properties allows the user to specify the
credentials needed to access the required database. The properties required are a jdbc connection string, a username and a password. For more details on
the jdbc connecton string see the JDBC documentation. The dbtool.properties file contains properties that adjust how the generator and runner parts of 
AutoQPNPED function. The most important property, which is required for the simulation runner to function is the qpme.runner.location property. 
This property must be set to the filepath of the directory that the users QPME installation is.

----------------------------SPECIFICATION FILES----------------------------
Traffic compositions are specified using a simple syntax. There are two types of specification that AutoQPNPED accepts: unannotated and annotated.
Unannotated specification contain a set of transaction each with a name and proportion of total traffic. Inside the transaction defintion there are several
SQL statements that must begin with a '-'. AutoQPNPED will infer the details such as runtime, table accessed and exclusivity of access. An example of such a specification
is found in normal_spec.txt. An annotated specifcation contains the same transaction definition syntax but each statement but be defined with the runtime of the statement
in milliseconds, the table the statement accesses and the exclusivity of access. These specifications are run with the option -s to make AutoQPNPED use the values specified
for each statement instead of inferring. An example of such a specification is found in detailed_spec.txt. Both specifications allow for IF statements to be defined which
must have a probability of entry associated with it and it can contain any number of SQL statements or further IF statements. An example of an IF statement can be found in
either normal_spec.txt or detailed_spec.txt.

---------------------------BUILDING THE PROJECT----------------------------
To build the project from source there is an ANT build file provided that will create an executable jar in the dist folder containing all the appropriate
source code. It will also copy the required library jars into the lib folder inside the dist folder. Finally it will copy the two property files into the dist
folder as well so they can be edited as appropriate.