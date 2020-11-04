package com.addressbook;
import java.util.Comparator;

public class SortEntryByZip implements Comparator<Contact>{
	public int compare(Contact contact1, Contact contact2) {
		return (int)(contact1.getZip() - contact2.getZip());
	}
}