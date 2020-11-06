package com.capgemini.addressbooktest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.capgemini.addressbook.AddressBookService;
import com.capgemini.addressbook.Contact;
import com.capgemini.addressbook.DatabaseException;
import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class AddressBookServiceTest {
	@Test
	public void givenContactDataInDB_WhenRetrieved_ShouldMatchContactCount() throws DatabaseException {
		AddressBookService addressBookService = new AddressBookService();
		List<Contact> contactData = addressBookService.readContactDBData();
		assertEquals(4, contactData.size());
	}
	@Test
	public void givenNewDataForContact_WhenUpdated_ShouldBeInSync() throws DatabaseException, SQLException {
		AddressBookService addressBookService = new AddressBookService();
		List<Contact> contactData = addressBookService.readContactDBData();
		addressBookService.updateContactData("Shivam", "Jaiswal", Long.parseLong("7748454018"));
		addressBookService.readContactDBData();
		boolean result = addressBookService.checkContactDataSync("Shivam", "Jaiswal");
		assertEquals(true, result);
	}
	@Test
	public void givenDateRangeForContactsAddedInDateRange_ShouldMatchCount() throws DatabaseException, SQLException {
		AddressBookService addressBookService = new AddressBookService();
		LocalDate start = LocalDate.of(2017, 01, 01);
		LocalDate end = LocalDate.now();
		List<Contact> contactList = addressBookService.getContactByDate(start, end);
		assertEquals(3, contactList.size());
	}
	@Test
	public void givenContacts_WhenRetrievedByCity_ShouldMatchCount() throws DatabaseException {
		AddressBookService addressBookService = new AddressBookService();
		assertTrue(addressBookService.getContactByCity().get("Dombivili").equals(1));
	}
	@Test
	public void givenContacts_WhenRetrievedByState_ShouldMatchCount() throws DatabaseException {
		AddressBookService addressBookService = new AddressBookService();
		assertTrue(addressBookService.getContactByState().get("Maharashtra").equals(2));
	}
	@Test
	public void givenContact_WhenAdded_ShouldMatchCount() throws DatabaseException, SQLException {
		AddressBookService addressBookService = new AddressBookService();
		addressBookService.addContact("Ramesh", "Powar", "Malad", "Kerala", 421201, "20324843854", "rameshpowar@gmail.com", 2);
		List<Contact> contactList = addressBookService.readContactDBData();
		assertEquals(contactList.size(), 5);
	}
	@Test
	public void givenMultipleContacts_WhenAddedToDB_ShouldMatchContactEntries() throws DatabaseException {
		Contact[] contactArray = { new Contact("Locky","Feguson", "Malad","Maharashtra",412055, 2324235324L,"abcd@gmail.com",2),
				new Contact("Locky","Feguson", "Malad","Maharashtra",412055, 2324235324L,"abcd@gmail.com",1)};
		AddressBookService addressBookService = new AddressBookService();
		addressBookService.readContactDBData();
		Instant start = Instant.now();
		addressBookService.addContactToDB(Arrays.asList(contactArray));
		Instant threadEnd = Instant.now();
		System.out.println("Duration with Thread: " + Duration.between(start, threadEnd));
		long result = addressBookService.readContactDBData().size();
		assertEquals(7, result);
	}
	@BeforeEach
	public void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 3000;
	}
	private Contact[] getContactList() {
		Response response = RestAssured.get("/contact");
		System.out.println("Contact entries in JSONServer:\n"+response.asString());
		Contact[] arrayOfContact = new Gson().fromJson(response.asString(),Contact[].class);
		return arrayOfContact;
	}
	private Response addContactToJsonServer(Contact contact) {
		String contactJson = new Gson().toJson(contact);
		RequestSpecification request = RestAssured.given();
		request.header("Content-Type", "application/json");
		request.body(contactJson);
		return request.post("/contact");
	}
	@Test
	public void givenContactInJSONServer_WhenRetrieved_ShouldMatchTheCount() {
		Contact[] arrayOfContact = getContactList();
		AddressBookService addressBookService = new AddressBookService(Arrays.asList(arrayOfContact));
		long entries = addressBookService.getCount();
		assertEquals(1,entries);	
	}
	@Test
	public void givenListOfNewContacts_WhenAdded_ShouldMatch201ResponseAndCount() {
		Contact[] arrayOfContact = getContactList();
		AddressBookService addService = new AddressBookService(Arrays.asList(arrayOfContact));
		Contact[] contacts = {new Contact("Hardik","Pandya","Ahmedabad", "Maharashtra", 444001, 8850273350L,"abcd@gmail.com",2),
				new Contact("Sachin","Tendulkar","Mumbai", "Maharashtra", 444001, 7887483853L,"abcd@gmail.com",2)};
		List<Contact> contactList = Arrays.asList(contacts);
		contactList.forEach(contact -> {
			Runnable task = () -> {
				Response response = addContactToJsonServer(contact);
				int statusCode = response.getStatusCode();
				assertEquals(201, statusCode);
				Contact newContact = new Gson().fromJson(response.asString(), Contact.class);
				addService.addContact(newContact);
			};
			Thread thread = new Thread(task, contact.firstName);
			thread.start();
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		int count = addService.getCount();
		assertEquals(3, count);
	}
	@Test 
	public void givenNewPhoneForContact_WhenUpdated_ShouldMatch200Request() throws DatabaseException, SQLException {
		Contact[] arrayOfContact = getContactList();
		AddressBookService addService = new AddressBookService(Arrays.asList(arrayOfContact));
		addService.updateContactData("Sachin", "Tendulkar", 7887483853L);
		Contact contact = addService.getContact("Sachin", "Tendulkar");
		String contactJson = new Gson().toJson(contact);
		RequestSpecification request = RestAssured.given();
		request.header("Content-Type","application/json");
		request.body(contactJson);
		Response response = request.put("/contact/"+contact.id);
		int statusCode = response.getStatusCode();
		assertEquals(200,statusCode);			
	}
	@Test 
	public void givenContactToDelete_WhenDeleted_ShouldMatch200ResponseAndCount() throws DatabaseException, SQLException {
		Contact[] arrayOfContact = getContactList();
		AddressBookService addService = new AddressBookService(new LinkedList<Contact>(Arrays.asList(arrayOfContact)));
		Contact contact = addService.getContact("Sachin", "Tendulkar");
		RequestSpecification request = RestAssured.given();
		request.header("Content-Type","application/json");
		Response response = request.delete("/contact/"+contact.id);
		int statusCode = response.getStatusCode();
		assertEquals(200,statusCode);
		addService.deleteContact(contact.firstName, contact.lastName);
		assertEquals(2,getContactList().length);
	}
}
