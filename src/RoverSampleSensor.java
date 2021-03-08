import java.io.*;
import java.lang.*;

//Author: Colin Peterman //
//Date: 2/08/2021 //

/**
 * RovingSampleSensors: A class for reading sample perceptions from a file
 * and presenting them one at a time
 *
 * @author Eric Fosler-Lussier
 * @version 1.1
 *
 * allowed stdin version of contstructor
 */

public class RoverSampleSensor {
    // File
    private BufferedReader myFile;

    
    /**
     * Creates Sensors object from file
     * @param filename The file that data is read from
     */
    public RoverSampleSensor(String filename) {
	try {
	    myFile=new BufferedReader(new FileReader(filename));
	} catch (Exception e) {
	    System.err.println("Ooops!  I can't seem to load the file \""+filename+"\", do you have the file in the correct place?");
	    System.exit(1);
	}
    }

    /**
     * Creates Sensors object from standard input
     */
    public RoverSampleSensor() {
	try {
	    myFile=new BufferedReader(new InputStreamReader(System.in));
	} catch (Exception e) {
	    System.err.println("Ooops!  I can't seem to read from the standard input!");
	    System.exit(1);
	}
    }

    /**
     * Gets the next sample perception
     * @return SamplePercept A SamplePercept object containing the percept
     */

    public SamplePercept getPercept() {
	String line;

	try {
	    line=myFile.readLine();
	    if (myFile==null) {
		return null;
	    } else if (line==null) {
		try {
		    myFile.close();
		} catch (Exception e) {
		}

		myFile=null;
		return null;
	    } else {
		return new SamplePercept(line);
	    }
	} catch (Exception e) {
	    System.err.println("Ooops!  I seem to have gotten an i/o error reading the file.");
	    System.exit(1);
	}
	return null;
    }
    
    // If the soil is evenly divisible by 5 then it assigns answer a G for grab and if not an N for noop and returns it //
    public static char SimpleReflexAgent(int percept) {
    	char answer = 'N';
    	if(percept % 5 == 0) {
    		answer = 'G';
    	}
    	return answer;
    }

    /**
     * Run a test of the reading routines, prints out all percepts of the file
     *
     * Usage: java RoverSampleSensor -file <filename>
     * or in eclipse: run as configuration, then arguments, then put -file hw1-data1.txt 
     */
    
    public static void main(String args[]) {
	if (args.length!= 0 && 
	    (args.length != 2 ||   (! args[0].equals("-file")))) {
	    System.err.println("Usage: RoverSampleSensor -file <filename>");
	    System.exit(1);
	}
	RoverSampleSensor rss=null;
	SamplePercept sp;

	if (args.length==0) {
	    rss=new RoverSampleSensor();
	} else {
	    rss=new RoverSampleSensor(args[1]);
	}
	while((sp=rss.getPercept())!=null) {
		// Prints out what is perceived and if you should grab it or noop it // 
	    System.out.print("Perceived: "+sp.value());
	    char answer = SimpleReflexAgent(sp.value());
	    System.out.println(" Action: " + answer);
	    }
    }

}
