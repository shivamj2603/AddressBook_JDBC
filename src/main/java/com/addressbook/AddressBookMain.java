package com.addressbook;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.Collections;
import com.google.*;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonStreamParser;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
public class AddressBookMain {

	public static Map<String, AddressBook> addressBookMap = new HashMap<>();
	public static Map<String, Map<String, AddressBook>> stateBookMap = new HashMap<>();
	public void addData(Scanner input) {
		String contactChoice, cityChoice, stateChoice;
		do {
			System.out.println("Enter the name of state");
			String stateForMap = input.nextLine();

			do {
				System.out.println("Enter the name of city");
				String cityForMap = input.nextLine();
				AddressBook addBook = new AddressBook(cityForMap);
				for (Map.Entry<String, AddressBook> entry : addressBookMap.entrySet()) {
					if (entry.getKey().equals(cityForMap)) {
						addBook = entry.getValue();
					}
				}
				addressBookMap.put(cityForMap, addBook);
				do {
					System.out.println("Enter the details of person");
					System.out.println("Enter the first name");
					String firstName = input.nextLine();
					System.out.println("Enter the last name");
					String lastName = input.nextLine();
					System.out.println("Enter the address");
					String address = input.nextLine();
					System.out.println("Enter the ZIP code");
					int zip = input.nextInt();
					System.out.println("Enter the phone number");
					long phoneNumber = input.nextLong();
					input.nextLine();
					System.out.println("Enter the email");
					String email = input.nextLine();
					Contact c = new Contact(firstName, lastName, address, cityForMap, stateForMap, zip, phoneNumber, email);
					for (Map.Entry<String, AddressBook> entry : addressBookMap.entrySet()) {
						if (entry.getKey().equalsIgnoreCase(cityForMap)) {
							entry.getValue().addContact(c);
							//write contact to CSV file
							writeAsJson(c);
						}
					}
					System.out.println("Do you want to add contact again?");
					contactChoice = input.nextLine();
				} while (contactChoice.equals("yes"));
				System.out.println("Do you want to add another city");
				cityChoice = input.nextLine();
			} while (cityChoice.equals("yes"));
			stateBookMap.put(stateForMap, addressBookMap);
			System.out.println("Do you want to add for another state");
			stateChoice = input.nextLine();
		} while (stateChoice.equals("yes"));
	}
	/**
	 * Usecase 7_Part1
	 * Function checks for the contact in a particular city using the contact's name across the entire address book
	 * @param name
	 * @param city
	 */
	public static void searchPersonByCity(String name, String city) {
		List<Contact> list = new ArrayList<Contact>();
		for(Map.Entry<String, AddressBook> entries : addressBookMap.entrySet()) {
			list = entries.getValue().getAddressBook().stream().filter(p -> p.getCity().equals(city)).filter(p ->(p.getFirstName() + p.getLastName()).equals(name)).collect(Collectors.toList());
		}
		for(Contact contact : list) {
			System.out.println(contact);
		}
	}
	/**
	 * UseCase7_Part2
	 * Function checks for the contact in a particular State using the contact's name across the entire address book
	 * @param name
	 * @param state
	 */
	public static void searchPersonByState(String name, String state) {
		List<Contact> list = new ArrayList<Contact>();
		for(Map.Entry<String, AddressBook> entries : addressBookMap.entrySet()) {
			list = entries.getValue().getAddressBook().stream().filter(p -> p.getState().equals(state)).filter(p ->(p.getFirstName() + p.getLastName()).equals(name)).collect(Collectors.toList());
		}
		for(Contact contact : list) {
			System.out.println(contact);
		}
	}
	/**
	 * UseCase8_Part1
	 * This function returns the list of people residing in a particular City
	 * @param city
	 */
	public static void viewPersonByCity(String city) {
		List<Contact> list = new ArrayList<Contact>();
		for(Map.Entry<String, AddressBook> entries : addressBookMap.entrySet()) {
			list = entries.getValue().getAddressBook().stream().filter(p -> p.getCity().equals(city)).collect(Collectors.toList());
		}
		for(Contact contact : list) {
			System.out.println(contact);
		}
	}
	/**
	 * UseCase8_Part2
	 * This function returns the list of people residing in a particular State
	 * @param state
	 */
	public static void viewPersonByState(String state) {
		List<Contact> list = new ArrayList<Contact>();
		for(Map.Entry<String,Map<String, AddressBook>> entries : stateBookMap.entrySet()) {
			if(entries.getKey().equals(state)) {
				for(Map.Entry<String,AddressBook> Entry : addressBookMap.entrySet()) {
					System.out.println("The List for city "+ Entry.getKey()+" is :");
					Entry.getValue().viewList();
				}
			}
		}
	}
	/**
	 * Usecase9
	 * Function displays the count of contacts in a particular city across the multiple address book
	 * @param city
	 */
	public static void countPersonByCity(String city) {
		long totalCount = 0;
		for(Map.Entry<String, AddressBook> entries : addressBookMap.entrySet()) {
			long count = entries.getValue().getAddressBook().stream().filter(p -> p.getCity().equals(city)).count();
			totalCount += count;
		}
		System.out.println(totalCount + " Contacts in " + city);
	}
	/**
	 * UseCase10
	 * Function displays the count of contacts in a particular State across the multiple address book
	 * @param State
	 */
	public static void countPersonByState(String State) {
		long totalCount = 0;
		for(Map.Entry<String, AddressBook> entries : addressBookMap.entrySet()) {
			long count = entries.getValue().getAddressBook().stream().filter(p -> p.getCity().equals(State)).count();
			totalCount += count;
		}
		System.out.println(totalCount + " Contacts in " + State);
	}
	/**
	 * Usecase 11
	 * The function sorts the contacts on the basis of their names in alphabetical order
	 * 
	 */
	public static void sortByName() {
		for(Map.Entry<String,AddressBook> entry : addressBookMap.entrySet()) {
			Collections.sort(entry.getValue().getAddressBook(),new SortEntryByName());
		}
	}
	/**
	 * UseCase 12
	 * Function sorts the contacts on the basis of zipcode
	 * 
	 */
	public static void sortByZip() {
		for(Map.Entry<String,AddressBook> entry : addressBookMap.entrySet()) {
			Collections.sort(entry.getValue().getAddressBook(),new SortEntryByZip());
		}
	}
	public static void viewContacts(Scanner input) {
		System.out.println("1.View Person By City\n2.View Person By State\n3.Count Person By City\n4.Count Person By State\5.View Contacts in City\n6.View Contacts in State\n7.Sort By Name\n8.Sort By Zipcode\n9.Exit");
		int option = input.nextInt();
		input.nextLine();
		while(option != 9) {
			switch(option) {
			case 1:
				System.out.println("Enter the city");
				String city = input.nextLine();
				viewPersonByCity(city);
				break;
			case 2:
				System.out.println("Enter the state");
				String state = input.nextLine();
				viewPersonByState(state);
				break;
			case 3: 
				System.out.println("Enter the city");
				String City = input.nextLine();
				countPersonByCity(City);
				break;
			case 4:
				System.out.println("Enter the state");
				String State = input.nextLine();
				viewPersonByState(State);
				break;
			case 5:
				System.out.println("Enter the city and the name");
				String Citie = input.nextLine();
				String name = input.next();
				input.nextLine();
				searchPersonByCity(name, Citie);
				break;
			case 6:
				System.out.println("Enter the city and the name");
				String States = input.nextLine();
				String names = input.next();
				input.nextLine();
				searchPersonByState(names, States);
				break;
			case 7:
				sortByName();
				break;
			case 8:
				sortByZip();
				break;
			case 9:
				break;
			}
		}
	}
	public static void readAddressBook() {
		try {
			Path path = Paths.get("./addressbook.txt");
			List <String> lines = Files.readAllLines(path);
			for(String line : lines) {
				System.out.println(line);
			}
		}
		catch (IOException exception) {

			exception.printStackTrace();
		}
		System.out.println("Data Read Successfully");
	}
	public static void writeAddressBook(Map<String, AddressBook> map) {
		StringBuffer buffer = new StringBuffer();
		for(String city : map.keySet()) {
			map.get(city).getAddressBook().forEach(c -> buffer.append(c.toString().concat("\n")));
		}
		try {
			Path path = Paths.get("./addressbook.txt");
			Files.write(path, buffer.toString().getBytes());
		} catch (IOException exception) {
			exception.printStackTrace();
		}
		System.out.println("Data Written Successfully");
	}
	/**
	 * Usecase 14
	 * Write as CSV file
	 * @param contact
	 */
	public static void writeContactAsCSV(Contact contact) 
	{ 
		Path path = Paths.get("addressBook.csv");
		try { 
			FileWriter outputfile = new FileWriter(path.toFile(), true); 
			CSVWriter writer = new CSVWriter(outputfile); 
			//add data to csv
			String[] data = contact.toString().split(",");
			writer.writeNext(data);
			// closing writer connection 
			writer.close(); 
		} 
		catch (IOException exception) { 
			exception.printStackTrace(); 
		} 
	} 
	/**
	 * Usecase 14
	 * Read as CSV file
	 * 
	 */
	public static void readAddressBookCSV(){ 
		try {  
			FileReader filereader = new FileReader(Paths.get("addressBook.csv").toFile()); 
			CSVReader csvReader = new CSVReaderBuilder(filereader).build();  
			List<String[]> contactData = csvReader.readAll(); 
			// print Data 
			for (String[] row : contactData) { 
				for (String cell : row) { 
					System.out.print(cell + "\t"); 
				} 
				System.out.println(); 
			} 
		} 
		catch (Exception exception) { 
			exception.printStackTrace(); 
		} 
	} 
	/**
	 * Usecase 15
	 * Write Contact as Json Object
	 * @param contact
	 */
	public static void writeAsJson(Contact contact) {
		Gson gson = new Gson();
		String json = gson.toJson(contact);
		try {
			FileWriter writer = new FileWriter(Paths.get("addressBook.json").toFile(), true);
			writer.write(json);
			writer.close();
		} catch (IOException exception) {
			exception.printStackTrace();
		}
		System.out.println(json);	     
	}
	/**
	 * Usecase 15
	 * Read Contact from Json File
	 */
	public static void readAsJson() {
		String nextLine = "";
		Gson gson = new Gson();
		BufferedReader br;
		try {
			br = new BufferedReader(
					new FileReader(Paths.get("addressBook.json").toFile()));
			JsonStreamParser parser = new JsonStreamParser(br);
			while(parser.hasNext())
			{
				JsonElement element = parser.next();
				if (element.isJsonObject()) {
					Contact contact = gson.fromJson(element, Contact.class);
					System.out.println(contact);
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	public static void main(String[] args) {
		System.out.println("Welcome to Address Book");
		Scanner input = new Scanner(System.in);
		do {
			System.out.println("1.Add a new Contact");
			System.out.println("2.Edit the contact details");
			System.out.println("3.Delete a Contact");
			int choice = input.nextInt();
			input.nextLine();
			switch(choice) {
			case 1:  
				new AddressBookMain().addData(input);//Add Contact Details
				break;
			case 2:
				System.out.println("Enter the name to edit contact");
				String editName = input.nextLine();
				System.out.println("Enter the city");
				String city2 = input.nextLine();
				for (Map.Entry<String,AddressBook> entry : addressBookMap.entrySet()) {   
					if(entry.getKey().equalsIgnoreCase(city2)) {
						entry.getValue().editContact(editName);
					}
					else {
						System.out.println("The addressbook does not exist.");
					}
				}
				break;
			case 3:
				System.out.println("Enter the name to delete");
				String deleteName = input.nextLine();
				System.out.println("Enter the city");
				String city1 = input.nextLine();
				for (Map.Entry<String,AddressBook> entry : addressBookMap.entrySet()) {   
					if(entry.getKey().equalsIgnoreCase(city1)) {
						entry.getValue().deleteContact(deleteName);
					}
					else {
						System.out.println("The addressbook does not exist.");
					}
				}
				break;
			default:
				break;
			} 
			System.out.println("Do you wish to continue(yes/no)?");
		}
		while(input.nextLine().equals("yes"));
		viewContacts(input);
		readAsJson();
		System.out.println("Thank You");
	}
}

