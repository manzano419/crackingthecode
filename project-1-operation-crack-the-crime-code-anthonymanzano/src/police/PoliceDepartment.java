package police;

import criminals.Member;
import criminals.Organization;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.DomainCombiner;
import java.util.*;

import javax.print.attribute.standard.Media;

import java.io.File;

import criminals.Organization;
import interfaces.List;
import lists.ArrayList;
import lists.DoublyLinkedList;
import lists.SinglyLinkedList;

public class PoliceDepartment {
	/**global variables that we will use to store information from the total of arrest 
	 * and the different organizations for easy access to the report
	 */
	String cap;
	Integer arrest = 0;
	
	SinglyLinkedList<Organization> organizationarray = new SinglyLinkedList<>();
	public static int leaderorg;
		
	/*Constructor of the police department object
	 * @Param  name of the captain in charge
	 */
	public PoliceDepartment(String captain) {
		
		this.cap = captain;
		
	}
	/*Returns a list with all of the criminal organizations in current case.
	 * 
	 */
	
	public List<Organization> getCriminalOrganizations() {
		//returns the global array with the organizations.
		return organizationarray;
	}

	/*This method iterates through the folder that contains the files with the the information of the organization and sets them up 
	 * calling the organization class and adds them to the array.
	 * @Param Case folder containing the organizations
	 * @throws IOExecption
	 */
		
	public void setUpOrganizations(String caseFolder) throws IOException{	
		//initialize a variable type file to where the organization info is
		File dir = new File(caseFolder + "/CriminalOrganizations");
		//iterate through each organization file with a file method
		 for (File file : dir.listFiles()) {
			 //check that we are not pointing to a null file
			 if(!file.equals(null)) {
				 //calling the organization constructor from the organization class and adding it to the array.
	            	Organization neworg = new Organization(file.getPath());
	            	organizationarray.add(neworg);
		 			}
	            }
	            
		 }

	/* Receives a Flyer to where the message that we want to decipher is located and returns the name
	 * of the leader in the operation.
	 * @param case file ( flyer )
	 * @return   name of the leader
	 * @throws IOExeption
	 */
	public String decipherMessage(String caseFolder) throws IOException {
		//Initializing variables
		String root = null;
		String name = "";
		int location;
		int key;
		String line = "";
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(caseFolder));
			//reading the first line that contains the digit root.
			root = br.readLine();
			//taking away the # from the number
			root = root.substring(1);
			//calling the digitroot method to calculate the position in the array
			location = getDigiroot(root);
			//assigning the position to a global variable to use later in arrest
			leaderorg = location-1;
			//fetching the key in which the name is located in the flyer
			key = getCriminalOrganizations().get(location-1).getLeaderKey();
			//skipping second line of flyer
			br.readLine();
			//assigning the first line we are going to analyze with the the key
			line = br.readLine();
			
			//checks each line of the flyer to find the ith word in the line and adds the first character to the variable nam., 
			//if the array is smaller than the key it adds a space.
 			while(!line.equals("--")) {
 				String[] words = line.split(" ");
 				if(words.length < key) {
 					name += " ";
 				}else {
				name+= words[key-1].charAt(0);
 				}
				line = br.readLine();
 				
 			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return name;
	}

	/* Method that calculates the digit root from the poem. It does this mathematically.
	 * @param number to calculate.
	 * @return the position of the organization.
	 * 
	 */
	public int getDigiroot(String numbers) {
		
		Integer digiroot;
		//converts the number from string to integer
		digiroot = Integer.parseInt(numbers);
		int root = 0;
		
		//condition to check whether we have finished
		while (digiroot > 0 || root > 9) {
			if( digiroot == 0) {
				digiroot = root;
				root = 0;
			}
			//the remainder gives us the last digit in the number and we add it then we divide to take away that digit from the number, and repeat.
			root += digiroot%10;
			digiroot /= 10;
		}

		return root;
		

	}
	
	/* Method to arrest the leader and the underlings. It will check which underlings have the most underlings and arrest them also.
	 * @param leader of the operation
	 * 
	 */
	
	public void arrest(String leader) {
		 int arrested = 0;
		 //fetching the organization stored in the global variable to which the leader is member
		 Organization org = getCriminalOrganizations().get(leaderorg);
		 //using a lambda function to find where the leader of the operation is located in the organization.
		 List<Member> mem = org.organizationTraversal( M -> M.getNickname().equalsIgnoreCase(leader));
		 Member leadnick = mem.first();
		 //arresting the leader.
		 leadnick.setArrested(true);
		 //adding to the arrested counter
		 arrested++;
		 
		 //checks if the leader has any underlings, if not the method is done
		if(leadnick.getUnderlings().size() != 0) {
			
			//assigning the underling with most underlings to the first so we can compare the rest.
			Member maxUnder = leadnick.getUnderlings().get(0);
			//keeps count of the most underlings
			int maxUnderSize = maxUnder.getUnderlings().size();
			
			//arresting each underling of the leader
			for( Member leadunder : leadnick.getUnderlings()) {
				//Checks whether he was already arrested previously.
				if( leadunder.isArrested() == true) {
					continue;
				}
				else {
					leadunder.setArrested(true);
					arrested++;
				}
				
				//compares the underling with the previous one to see who has the most
			if( leadunder.getUnderlings().size() > maxUnderSize) {
				maxUnderSize = leadunder.getUnderlings().size();
				maxUnder = leadunder;
			}
		}
		
			//arrest each underling of the member with the most underlings
		for( Member toarrest : maxUnder.getUnderlings()) {
			if(toarrest.isArrested() == true) {
				continue;
			}
			else {
			toarrest.setArrested(true);
			arrested++;
			}
			}
		}
		
		//adds the number of people arrested in this operation to the global variable containing the arrest of all other operations in the case.
		this.arrest += arrested;
	}

	
	/*
	 * Writes and creates the file containing the police report of the case with all the details.
	 * the police report may not appear in eclipse but will appear in the file explorer in the results folder of the project.
	 * @param location on where to create the file in the computer
	 */
	public void policeReport(String filePath) {
		
		try {
			//creating the file
			File file =  new File(filePath);
			//passing the file to the writer
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			//starts writing the case line per line.
			writer.write("CASE REPORT\n");
			writer.write("\nIn charge of Operation: " + this.cap + "\n");
			writer.write("\nTotal arrest made: " + this.arrest + "\n");
			writer.write("\nCurrent Status of Criminal Organizations:\n");
			
			//iterates and prints each organization in their current status.
			for( Organization org : getCriminalOrganizations()) {
				if(org.getBoss().isArrested() == true) {
					writer.write("\nDISSOLVED");
				}
				writer.write("\n" + org + "\n");
				writer.write("\n---\n");
			}
			//finishes the report and closes the writer.
			writer.write("\nEND OF REPORT");
			writer.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
