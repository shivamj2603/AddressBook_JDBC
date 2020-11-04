package addressbooktest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import com.addressbook.AddressBookService;
import com.addressbook.Contact;
import com.addressbook.DatabaseException;

public class AddressBookTest {
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

}
