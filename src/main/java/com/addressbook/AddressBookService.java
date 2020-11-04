package com.addressbook;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.addressbook.DatabaseException;

public class AddressBookService {
	private List<Contact> contactList = new ArrayList<Contact>();
	private AddressBookDBService addressBookDBService;
	public AddressBookService() {
		addressBookDBService = AddressBookDBService.getInstance();
	}
	public static void writeAddressBook(Map<String, AddressBook> map) {
		StringBuffer buffer = new StringBuffer("");
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
	public static void readAddressBookCSV() 
	{ 
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
	public void addContact(String firstName, String lastName, String city, String state, int zip, int bookid, String phonenumber, String email) throws DatabaseException, SQLException {
		addressBookDBService.addContact(firstName, lastName, city, state, zip, bookid, phonenumber, email);
	}
	public List<Contact> readContactDBData() throws DatabaseException {
			this.contactList = addressBookDBService.readData();
		return this.contactList;
	}
	public void updateContactData(String firstName, String lastName, long phone) throws DatabaseException, SQLException{
		int result = addressBookDBService.updateContactData(firstName, lastName, phone);
		if(result == 0) {
			return;
		}
		Contact contact = this.getContact(firstName, lastName);
		if(contact != null) {
			contact.phoneNumber = phone;
		}
	}
	private Contact getContact(String firstName, String lastName) {
		Contact contact = this.contactList.stream().filter(contactData -> contactData.firstName.equals(firstName) && contactData.lastName.equals(lastName))
				.findFirst().orElse(null);
		return contact;
	}
	public boolean checkContactDataSync(String firstName, String lastName) throws DatabaseException, SQLException {
		List<Contact> contactList = addressBookDBService.getContactData(firstName, lastName);
		return contactList.get(0).equals(getContact(firstName, lastName));

	}
	public List<Contact> getContactByDate(LocalDate start, LocalDate end) throws DatabaseException {
		// TODO Auto-generated method stub
		return addressBookDBService.readDataForGivenDateRange(start, end);
	}
	public Map<String, Integer> getContactByCity() throws DatabaseException{
		return addressBookDBService.getContactsByFunction("city");
	}
	public Map<String, Integer> getContactByState() throws DatabaseException{
		return addressBookDBService.getContactsByFunction("state");
	}
}
