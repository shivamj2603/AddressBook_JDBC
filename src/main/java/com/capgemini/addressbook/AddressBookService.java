package com.capgemini.addressbook;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.capgemini.addressbook.DatabaseException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;

public class AddressBookService {
	public enum IOService {
		CONSOLE_IO, FILE_IO, DB_IO, JSON_IO, CSV_IO, REST_IO
	};
	private List<Contact> contactList = new ArrayList<Contact>();
	private AddressBookDBService addressBookDBService;
	public AddressBookService() {
		addressBookDBService = AddressBookDBService.getInstance();
	}
	public AddressBookService(List<Contact> contactList){
		this.contactList = contactList;
	}
	public static void writeAddressBook(Map<String, AddressBook> map, IOService ioService) throws AddressBookException {
		if(ioService.equals(IOService.FILE_IO)) {
			StringBuffer buffer = new StringBuffer("");
			for(String city : map.keySet()) {
				map.get(city).getAddressBook().forEach(c -> buffer.append(c.toString().concat("\n")));
			}
			try {
				Path path = Paths.get("./addressbook.txt");
				Files.write(path, buffer.toString().getBytes());
			} catch (IOException exception) {
				throw new AddressBookException("Unable to register contact");
			}
		}
	}
	public static void writeContact(Contact contact,IOService ioService) throws AddressBookException {
		if(ioService.equals(IOService.CSV_IO)) {
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
				throw new AddressBookException("Unable to add contact to CSV file");
			} 
		}
		if(ioService.equals(IOService.JSON_IO)) {
			Gson gson = new Gson();
			String json = gson.toJson(contact);
			try {
				FileWriter writer = new FileWriter(Paths.get("addressBook.json").toFile(), true);
				writer.write(json);
				writer.close();
			} catch (IOException exception) {
				throw new AddressBookException("Unable to write contact to JSON file");
			}
		}
	} 
	public static void readAddressBook(IOService ioService) throws AddressBookException {
		if(ioService.equals(IOService.CSV_IO)) {
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
	    	throw new AddressBookException("Unable to read contact from CSV file");
	    } 
		}
		if(ioService.equals(IOService.JSON_IO)) {
			Gson gson = new Gson();
			BufferedReader br;
			try {
				br = new BufferedReader(
						new FileReader(Paths.get("contact.json").toFile()));
				JsonStreamParser parser = new JsonStreamParser(br);
				while(parser.hasNext())
				{
					JsonElement element = parser.next();
					if (element.isJsonObject()) {
						Contact contact = gson.fromJson(element, Contact.class);
					}
				}
			} catch (Exception exception) {
				throw new AddressBookException("Unable to read contact from JSON file");
			}
		}
	} 
	public int getCount() {
		return this.contactList.size();
	}
	/**
	 * Usecase 20:
	 * Add Contact to the database
	 * @param firstName
	 * @param lastName
	 * @param city
	 * @param state
	 * @param zip
	 * @param bookid
	 * @param phonenumber
	 * @param email
	 * @throws DatabaseException
	 * @throws SQLException
	 */
	public void addContact(String firstName, String lastName, String city, String state, int zip, String phonenumber, String email, int type) throws DatabaseException{
		try {
			addressBookDBService.addContact(firstName, lastName, city, state, zip, phonenumber, email, type);
		} catch (DatabaseException|SQLException exception) {
			throw new DatabaseException("Unable to add contact");
		}
	}
	public void addContact(Contact contact) {
		this.contactList.add(contact);
	}
	
	/**
	 * Usecase 21:
	 * Add Contacts using threads
	 * @param contactList
	 */
	public void addContactToDB(List<Contact> contactList) {
		contactList.forEach(contact -> {
			Runnable task = () -> {
				System.out.println("Contact Being Added: " + Thread.currentThread().getName());
				try {
					this.addContact(contact.firstName, contact.lastName, contact.city,
							contact.state, contact.zip, String.valueOf(contact.phoneNumber), contact.email, contact.type);
				} catch (DatabaseException exception) {
					exception.getMessage();
				}
				System.out.println("Contact Added: " + Thread.currentThread().getName());
			};
			Thread thread = new Thread(task, contact.firstName);
			thread.start();
			try {
				thread.join();
			} catch (InterruptedException exception) {
				exception.printStackTrace();
			}
		});
	}
	/**
	 * Usecase 16:
	 * Retreive contacts
	 * @return
	 * @throws DatabaseException
	 */
	public List<Contact> readContactDBData() throws DatabaseException {
		this.contactList = addressBookDBService.readData();
		return this.contactList;
	}
	public void updateContactData(String firstName, String lastName, long phone) throws DatabaseException{
		int result;
		try {
			result = addressBookDBService.updateContactData(firstName, lastName, phone);
		} catch (DatabaseException|SQLException exception) {
			throw new DatabaseException("Unable to update contact");
		}
		if(result == 0) {
			return;
		}
		Contact contact = this.getContact(firstName, lastName);
		if(contact != null) {
			contact.phoneNumber = phone;
		}
	}
	/**
	 * fetch contact for a given name
	 * @param firstName
	 * @param lastName
	 * @return
	 */
	public Contact getContact(String firstName, String lastName) {
		Contact contact = this.contactList.stream().filter(contactData -> contactData.firstName.equals(firstName) && contactData.lastName.equals(lastName))
				         .findFirst().orElse(null);
		return contact;
	}
	/**
	 * Check whether the contact list is in sync with the database
	 * @param firstName
	 * @param lastName
	 * @return
	 * @throws DatabaseException
	 * @throws SQLException
	 */
	public boolean checkContactDataSync(String firstName, String lastName) throws DatabaseException{
		List<Contact> contactList = addressBookDBService.getContactData(firstName, lastName);
		return contactList.get(0).equals(getContact(firstName, lastName));
	}
	/**
	 * Function to get list of contacts added in a given date range
	 * @param start
	 * @param end
	 * @return
	 * @throws DatabaseException
	 */
	public List<Contact> getContactByDate(LocalDate start, LocalDate end) throws DatabaseException {
		return addressBookDBService.readDataForGivenDateRange(start, end);
	}
	/**
	 * Function to get number of contacts by city
	 * @return
	 * @throws DatabaseException
	 */
	public Map<String, Integer> getContactByCity() throws DatabaseException{
		return addressBookDBService.getContactsByFunction("city");
	}
	/**
	 * Function to get number of contacts by state
	 * @return
	 * @throws DatabaseException
	 */
	public Map<String, Integer> getContactByState() throws DatabaseException{
		return addressBookDBService.getContactsByFunction("state");
	}
	/**
	 * Delete contact from the contactList
	 * @param firstName
	 * @param lastName
	 */
	public void deleteContact(String firstName, String lastName) {
		Contact contact = this.getContact(firstName,lastName);
		this.contactList.remove(contact);
	}	
}
