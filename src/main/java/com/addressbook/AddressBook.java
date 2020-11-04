package com.addressbook;
import java.util.*;

public class AddressBook {
	public String name;
	public AddressBook(String name) {
		this.name = name;
	}

	public ArrayList<Contact> addressBook = new ArrayList<Contact>();
	public ArrayList<Contact> getAddressBook() {
		return addressBook;
	}
	public void setAddressBook(ArrayList<Contact> addressbook) {
		this.addressBook = addressBook;
	}
	public void addContact(Contact contact) {
		for(int iteration = 0; iteration < addressBook.size(); iteration++) {	
			if(addressBook.get(iteration).equals(contact)) {
				System.out.println("The person already exists!!!");
				return;
			}
		}
		addressBook.add(contact);
	}
	public void editContact(String name) {
		Scanner sc = new Scanner(System.in);
		String editName = "";
		for(Contact contact : addressBook) {
			editName = contact.getFirstName() + contact.getLastName();
			if(name.equalsIgnoreCase(editName)) {
				System.out.println("1.Change the address");
				System.out.println("2.Change the ZIP code");
				System.out.println("3.Change the phone number");
				System.out.println("4.Change the Email id");
				int choice = sc.nextInt();
				sc.nextLine();
				switch(choice) {
				case 1 : System.out.println("Enter new address");
				String address = sc.nextLine();
				contact.setAddress(address);
				break;
				case 2 : System.out.println("Enter new ZIP code");
				int zip = sc.nextInt();
				contact.setZip(zip);
				sc.nextLine();
				break;
				case 3 : System.out.println("Enter new phone number");
				long phone = sc.nextLong();
				sc.nextLine();
				contact.setPhoneNumber(phone);
				break;
				case 4 : System.out.println("Enter new Email id");
				String email = sc.nextLine();
				contact.setEmail(email);
				break;
				}	
			}
			else {
				System.out.println("No such contact exist");
			}
		}
	}
	public void deleteContact(String name) {
		String deleteName = "";
		for(Contact contact : addressBook) {
			deleteName = contact.getFirstName() + contact.getLastName();
			if(name.equalsIgnoreCase(deleteName)) {
				addressBook.remove(contact);
			}
		}
	}
	public void viewList() {
		for(Contact contact : addressBook) {
			System.out.println(contact);
		}
	}
}




