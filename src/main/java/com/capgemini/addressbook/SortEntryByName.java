package com.capgemini.addressbook;

import java.util.Comparator;

public class SortEntryByName implements Comparator<Contact> {
	public int compare(Contact contact1, Contact contact2) {
		return (contact1.getFirstName()+" "+contact1.getLastName()).compareTo(contact2.getFirstName()+" "+contact2.getLastName());
	}
}
