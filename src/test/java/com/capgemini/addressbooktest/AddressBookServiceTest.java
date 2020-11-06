package com.capgemini.addressbooktest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

import com.capgemini.addressbook.AddressBookService;
import com.capgemini.addressbook.Contact;
import com.capgemini.addressbook.DatabaseException;

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
}
