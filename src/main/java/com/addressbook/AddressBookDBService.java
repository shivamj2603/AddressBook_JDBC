package com.addressbook;
import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class AddressBookDBService {
	private static AddressBookDBService addressBookDBService;
	private AddressBookDBService() {
	}
	public static AddressBookDBService getInstance() {
		if(addressBookDBService == null) {
			addressBookDBService = new AddressBookDBService();
		}
		return addressBookDBService;
	}
	
	/**
	 * Function returns a connection
	 * @return
	 * @throws DatabaseException
	 */
	private Connection getConnection() throws DatabaseException {
		String jdbcurl = "jdbc:mysql://localhost:3306/addressbook_service?useSSL=false";
		String userName = "root";
		String password = "Shivam99@";
		Connection connection;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(jdbcurl, userName, password);
		}
		catch(Exception e) {
			throw new DatabaseException("Connection was unsuccessful");
		}
		return connection;
	}
	//retrieval query
	public List<Contact> readData() throws DatabaseException {
		String sql = "select * from contacts inner join address using(contactid) inner join bookmap using(contactid) inner join addressbook using(bookid) ; " ;
		return this.getContactData(sql);
	}
	/**
	 * Function retrieves the contact details and returns a list of contacts
	 * @param sql
	 * @return
	 * @throws DatabaseException
	 */
	private List<Contact> getContactData(String sql) throws DatabaseException {
		List<Contact> contactList = new ArrayList<>();
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				String firstname = resultSet.getString("firstname");
				String lastname = resultSet.getString("lastname");
				int zip = resultSet.getInt("zip");
				String city = resultSet.getString("city");
				String state = resultSet.getString("state");
				long phoneNumber = resultSet.getLong("phonenumber");
				String email = resultSet.getString("email");
				contactList.add(new Contact(firstname, lastname, city, state, zip, phoneNumber,email));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return contactList;			
	}
}
