import java.util.Scanner;
import java.io.File;
//this program is used to check to see if Pricer is working correctly
public class Compare{
	
	File checkFile;
	Scanner scanCheck;
	
	public Compare(String checkFileName){
		try
		{
			checkFile = new File(checkFileName);
			scanCheck = new Scanner(checkFile);
		} 
		catch (Exception ex) 
		{
            ex.printStackTrace();
        }
	}
	
	public String checkNext(){ //returns the next line from the check file
		if(scanCheck.hasNext()) 
		    return scanCheck.nextLine();
		return "";
	}
	
}