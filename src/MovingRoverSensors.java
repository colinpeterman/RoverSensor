import java.io.*;
import java.lang.*;
import java.util.*;

// Author: Colin Peterman //
// Date: 2/08/2021 //

/**
 * MovingRoverSensors: A class for reading visual and sample
 * perceptions from a file
 *
 * @author Eric Fosler-Lussier
 * @version 1.1
 *
 * allowed stdin version of constructor
 */

public class MovingRoverSensors {
    private SamplePercept[][] samps;
    private VisionPercept[][] vis;

    /**
     * Creates Sensors object from file
     * @param filename The file that data is read from
     */
    public MovingRoverSensors(String filename) {
	BufferedReader myFile=null;

	try {
	    myFile=new BufferedReader(new FileReader(filename));
	} catch (Exception e) {
	    System.err.println("Ooops!  I can't seem to load the file \""+filename+"\", do you have the file in the correct place?");
	    System.exit(1);
	}
	initialize(myFile);
    }

    /**
     * Creates Sensors object from standard input
     */
    public MovingRoverSensors() {
	BufferedReader myFile=null;

	try {
	    myFile=new BufferedReader(new InputStreamReader(System.in));
	} catch (Exception e) {
	    System.err.println("Ooops!  I can't seem to read the file from the standard input!");
	    System.exit(1);
	}
	initialize(myFile);
    }

    private void initialize(BufferedReader myFile) {

	int counter=0;
	LinkedList sp1=new LinkedList();
	LinkedList sp2=new LinkedList();
	LinkedList vp1=new LinkedList();
	LinkedList vp2=new LinkedList();
	String line;

	try {
	    while((line=myFile.readLine())!=null) {
		counter++;
		StringTokenizer st=new StringTokenizer(line,",");
		int row=Integer.parseInt(st.nextToken());
		
		if (row!=counter) {
		    throw new Exception("Malformatted file");
		}
		while(st.hasMoreTokens()) {
		    if (counter==1) {
			// next token: vision
			vp1.add(new VisionPercept(st.nextToken()));
			// next token: sample
			sp1.add(new SamplePercept(st.nextToken()));
		    } else if (counter==2) {
			// next token: vision
			vp2.add(new VisionPercept(st.nextToken()));
			// next token: sample
			sp2.add(new SamplePercept(st.nextToken()));
		    }			
		}
	    }
	} catch (Exception e) {
	    System.err.println("Ooops!  I had some problems reading in the file.");
	    System.exit(1);
	}

	try {
	    // now allocate array space for fast lookup
	    // this isn't really necessary, but it makes the lookup code
	    // very clean

	    Object[] x1=sp1.toArray();
	    Object[] x2=sp2.toArray();
	    Object[] x3=vp1.toArray();
	    Object[] x4=vp2.toArray();
	    samps=new SamplePercept[x1.length][2];
	    vis=new VisionPercept[x1.length][2];
	    
	    int i;
	    for(i=0;i<x1.length;i++) {
		samps[i][0]=(SamplePercept) x1[i];
		samps[i][1]=(SamplePercept) x2[i];
		vis[i][0]=(VisionPercept) x3[i];
		vis[i][1]=(VisionPercept) x4[i];
	    }
	    
	} catch (Exception e) {
	    System.err.println("Ooops!  I had some problems reading in the file.");
	    e.printStackTrace();
	    System.exit(1);
	}
    }

    /**
     * Gets a sample perception at <x,y>
     * @param x The x coordinate
     * @param y The y coordinate
     * @return SamplePercept A SamplePercept object containing the percept
     */
    public SamplePercept getSamplePercept(int x,int y) {
	try {
	    return samps[x-1][y-1];
	    
	} catch (Exception e) {
	}
	return null;
    }
    
    /**
     * Gets a vision perception at <x,y>
     * @param x The x coordinate
     * @param y The y coordinate
     * @return VisionPercept A VisionPercept object containing the percept
     */
    public VisionPercept getVisionPercept(int x,int y) {
	try {
	    return vis[x-1][y-1];
	    
	} catch (Exception e) {
	}
	return null;
    }
    
    // Required ReflexAgentWithState method //
    public static void ReflexAgentWithState(MovingRoverSensors mrs) {
    	int xCoordinate = 1;
    	int yCoordinate = 1;
    	boolean movePossible = true;
    	boolean secondMove = true;
    	int grabs = 0;
    	int moves = 0;
    	// Goes through a series of moves and then repeats to get through the 2x2 grid //
    	while(movePossible && xCoordinate < mrs.samps.length) {
    		// Checks to see if north is clear
    		if(yCoordinate == 1 && mrs.getVisionPercept(xCoordinate, yCoordinate + 1).isClear()) {
    			// If it is it goes north and then grabs the soil there //
    			printer(mrs,xCoordinate,yCoordinate,"NORTH","GONORTH");
    			yCoordinate += 1;
    			moves++;
    			printer(mrs, xCoordinate,yCoordinate,"NORTH","GRAB");
    			grabs++;
    			// Checks to see if the east is clear and if it does then it goes east and grabs it //
    			if(xCoordinate < mrs.samps.length && mrs.getVisionPercept(xCoordinate + 1, yCoordinate).isClear()) {
    				printer(mrs, xCoordinate,yCoordinate,"EAST","GOEAST");
    				moves++;
    				xCoordinate += 1;
    				printer(mrs, xCoordinate,yCoordinate,"EAST","GRAB");
    				grabs++;
    				// If the south is not clear it keeps going east and grabbing until the south is clear and then goes south //
    				while(secondMove) {
    					if(mrs.getVisionPercept(xCoordinate, yCoordinate - 1).isClear()) {
    						// increases moves and y coordinate while printing out information //
    						printer(mrs, xCoordinate,yCoordinate,"SOUTH","GOSOUTH");
    						moves++;
    						yCoordinate -= 1;
    						printer(mrs, xCoordinate,yCoordinate,"SOUTH","GRAB");
    						grabs++;
    						// checks to see if the east is clear and if it is it goes east //
    						if(xCoordinate < mrs.samps.length && mrs.getVisionPercept(xCoordinate + 1, yCoordinate).isClear()) {
    							printer(mrs, xCoordinate,yCoordinate,"EAST","GOEAST");
    							moves++;
    							xCoordinate++;
    							printer(mrs,xCoordinate,yCoordinate,"EAST","GRAB");
    							grabs++;
    						}
    						else if(xCoordinate < mrs.samps.length && !mrs.getVisionPercept(xCoordinate + 1, yCoordinate).isClear()){
    							printer(mrs, xCoordinate,yCoordinate,"EAST","LOOKNORTH");
    							printer(mrs, xCoordinate,yCoordinate,"NORTH","GONORTH");
    							yCoordinate++;
    							moves++;
    						}
    						secondMove = false;
    					}
    					// East is clear //
    					else if(xCoordinate < mrs.samps.length && mrs.getVisionPercept(xCoordinate + 1, yCoordinate).isClear()) {
    						printer(mrs,xCoordinate,yCoordinate,"SOUTH","LOOKEAST");
    						
    						printer(mrs,xCoordinate,yCoordinate,"EAST","GOEAST");
    						// increase moves, grabs, and x coordinate and then grabs the ground //
    						moves++;
    						xCoordinate += 1;
    						printer(mrs,xCoordinate,yCoordinate,"EAST","GRAB");
    						grabs++;
    					}
    					else {
    						// no moves possible so exits loop //
    						secondMove = false;
    						movePossible = false;
    					}
    				}
    				// east is not clear so you go back to the south and then go east //
    			} else {
    				// makes sure you are not at the end of the map //
    				if(xCoordinate == mrs.samps.length) {
    					movePossible = false;
    				}
    				else {
    					// go south and increases the moves and y coordinate //
    					printer(mrs,xCoordinate,yCoordinate,"EAST","LOOKSOUTH");
    				
    					printer(mrs,xCoordinate,yCoordinate,"SOUTH","GOSOUTH");
    					moves++;
    					yCoordinate -= 1;
    				// making sure that the east is clear //
    					if(xCoordinate < mrs.samps.length && mrs.getVisionPercept(xCoordinate + 1, yCoordinate).isClear()) {
    						
    						printer(mrs,xCoordinate,yCoordinate,"EAST","GOEAST");

    						moves++;
    						xCoordinate += 1;
    						printer(mrs,xCoordinate,yCoordinate,"EAST","GRAB");
    						grabs++;
    					}
    				}
    			}
    		}
    		// north is not clear to begin with so it goes east if it is clear and grabs it //
    		else if(xCoordinate < mrs.samps.length && mrs.getVisionPercept(xCoordinate + 1, yCoordinate).isClear()) {
    			printer(mrs,xCoordinate,yCoordinate,"NORTH","LOOKEAST");
    			
    			printer(mrs,xCoordinate,yCoordinate,"EAST","GOEAST");
    			
    			moves++;
    			xCoordinate += 1;
    			printer(mrs,xCoordinate,yCoordinate,"EAST","GRAB");
    			grabs++;
    			if(yCoordinate == 2 && mrs.getVisionPercept(xCoordinate, yCoordinate - 1).isClear()) {
        			printer(mrs,xCoordinate,yCoordinate,"EAST","LOOKSOUTH");
        			printer(mrs,xCoordinate,yCoordinate,"SOUTH","GOSOUTH");
        			moves++;
        			yCoordinate -= 1;
        			printer(mrs,xCoordinate,yCoordinate,"SOUTH","GRAB");
        			grabs++;
    			} 
    		}
    		// no possible moves so it exits the loop //
    		else {
    			movePossible = false;
    		}
    	}
    	printer(mrs,xCoordinate,yCoordinate,"EAST","NO MOVES POSSIBLE");
    	
    	// Prints out the total compunds collected and total moves //
    	
    	System.out.println("\n Total Compounds Collected:" + grabs + " Total Moves:" + moves);
    }
    
    // Prints out all the information to the screen and checks to see what the position it is looking at is 
    // clear, a boulder, or null //
    public static void printer(MovingRoverSensors mrs,int x, int y, String looking, String action ) {
    	String isClear = "CLEAR";
    	if(x < mrs.samps.length && looking.equals("EAST")) {
    		if(!mrs.getVisionPercept(x+1, y).isClear()) {
    			isClear = "BOULDER";
    		}
    	}
    	else if(y == 1 && looking.equals("NORTH")) {
    		if(!mrs.getVisionPercept(x, y+1).isClear()) {
    			isClear = "BOULDER";
    		}
    	}
    	else if(y == 2 && looking.equals("SOUTH")) {
    		if(!mrs.getVisionPercept(x, y-1).isClear()) {
    			isClear = "BOULDER";
    		}
    	}
    	else {
    		isClear = "NULL";
    	}
    	
    	System.out.print("Position: <" + x + "," + y + "> Looking: " + looking + " Perceived: <" );
		System.out.print(mrs.getSamplePercept(x,y).value() + ", " + isClear + "> ");
		System.out.println("Action: " + action);
		// ReflexAgentWithState(action); //
    }
	
    /**
     * Run a test of the reading routines, prints out all percepts of the file
     *
     * Usage: java MovingRoverSensors -file <filename>
     * or in eclipse: run as configuration, then arguments, then put -file hw1-data1.txt 
     */
    public static void main(String args[]) {
	if (args.length!=0 && 
	    (args.length != 2 || (! args[0].equals("-file")))) {
	    System.err.println("Usage: MovingRoverSensors -file <filename>");
	    System.exit(1);
	}
	
	MovingRoverSensors mrs=null;
	SamplePercept sp;
	VisionPercept vp;
	
	if (args.length==0) {
	    mrs=new MovingRoverSensors();
	} else {
	    mrs=new MovingRoverSensors(args[1]);
	}
	ReflexAgentWithState(mrs);
	

	
    }

}
