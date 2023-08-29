package police;

import java.io.File;
import java.io.IOException;

import criminals.Organization;

/**
 * Class whose main method will follow the steps needed for arresting 
 * members of criminal organizations. The step should be followed as established in the 
 * project's document.
 * @author Gretchen
 *
 */
public class ArrestOperation {
	
	/*
	 * Main method that executes the operation fully and creates the case report in order to see if we arrested everyone.
	 * the police report may not appear in eclipse but will appear in the file explorer in the results folder of the project.
	 */
	public static void main(String[] args) throws IOException {
		
		PoliceDepartment test = new PoliceDepartment("Captain Morgan");
		test.setUpOrganizations("inputFiles/case2");
		File codedmessage = new File("inputFiles/case2/Flyers");
			for (File file : codedmessage.listFiles()) {
			 if(!file.equals(null)) {
	            	String leader = test.decipherMessage(file.getPath());
	        		test.arrest(leader);
		 			}
	            }
			test.policeReport("results/case2_report.txt");
		 }	
	}
	


