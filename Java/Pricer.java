import java.util.Scanner;

public class Pricer
{
	static Book book;
	static double target_size;
	static boolean DEBUG = true; //if debug is true, program will halt on an incorrect output statement and display the error!
	static String inputLine;
	static Compare compare;

    // first argument: target-size
    // second argument (optional): name of a checkfile to see if the output of our software is correct
	public static void main(String args[])
	{
		
	    target_size = Double.parseDouble(args[0]);		//sets the target-size for our program
		
	
		book = new Book(target_size);					//create a new empty book, which contains our ask and bid lists
		
		
		if(args.length>1&&DEBUG)						//load our comparison program if DEBUG is true
		compare = new Compare(args[1]);							
		
		Scanner scanner = new Scanner(System.in);		//reads in data from stdin line-by-line
		while (scanner.hasNext()) {
			inputLine = scanner.nextLine();
		    processLog(inputLine);
		    
		}
	}
	
	//each line of our input log is split and parsed as follows:
	//Add to order book: timestamp "A" order_id side price size
	//Reduce order:      timestamp "R" order_id size
	//"A" and "R" designate either an "Add" order or "Reduce" order
	// order_id is a unique string identifying the order in our book
	// side is either "B" for a Bid or "A" for an Ask order
	public static void processLog(String line)
	{	
		String[] parsed = line.split(" ");						//parses our command line
	
	    if(parsed[1].equals("R")) 								//the command is to Reduce an order
	    {
	    	book.reduceOrder(parsed);
	    } else													//the command is to Add an order
	    {
	  		book.addOrder(parsed);
	    }
	}
	
	public static void write(String line)
	{
		System.out.println(line);								//writes our output to stdout
			
		if(DEBUG)
		{														//if DEBUG is true, compare our output to the output
			String compareLine = compare.checkNext();			//from our checkfile
			if(!compareLine.equals(line))
			{
			    System.out.println();
			    System.out.println("Compare Line: "+compareLine);
			    System.out.println("My Line     :"+line);
			    System.out.println("Input Line: "+inputLine);
			    System.exit(0);
		    }
		}
	}
}

