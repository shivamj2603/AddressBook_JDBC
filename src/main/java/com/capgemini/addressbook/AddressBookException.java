package com.capgemini.addressbook;

public class AddressBookException extends Exception {
	public String message;
	public AddressBookException(String message) {
		super(message);
	}
}
