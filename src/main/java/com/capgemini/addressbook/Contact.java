package com.capgemini.addressbook;

public class Contact {
	public Contact(String firstName, String lastName, String city, String state, int zip,
			long phoneNumber, String email) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.city = city;
		this.state = state;
		this.zip = zip;
		this.phoneNumber = phoneNumber;
		this.email = email;
	}
	public Contact(String firstName, String lastName, String city, String state, int zip,
			long phoneNumber, String email, int type) {
		this(firstName, lastName, city, state, zip, phoneNumber, email);
		this.type = type;
	}
	public Contact(int id, String firstName, String lastName, String city, String state, int zip,
			long phoneNumber, String email, int type) {
		this(firstName, lastName, city, state, zip, phoneNumber, email);
		this.type = type;
		this.id = id;
	}
	public int id;
	public String firstName;
	public String lastName;
	private String address;
	public String city;
	public String state;
	public int zip;
	public long phoneNumber;
	public String email;
	public int type;

	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public int getZip() {
		return zip;
	}
	public void setZip(int zip) {
		this.zip = zip;
	}
	public long getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(long phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * Usecase 7
	 * Check whether the two contacts are same or not on the basis of their names
	 *
	 */
	@Override
	public boolean equals(Object object) {
	    boolean result = false;
	    if(object == this) {
		return true;
	    }
	    Contact contact = (Contact)object;
	    if(contact.firstName.equals(this.firstName) && contact.lastName.equals(this.lastName)) {
		result = true;
	    }
	    return result;
	}
	@Override
	public String toString() {
		return this.getFirstName() + this.getLastName() + "," + this.getAddress() + "," + this.getState() + "," + this.getCity() + "," + this.getZip() + "," + this.getPhoneNumber() + "," + this.getEmail();
	}
}

