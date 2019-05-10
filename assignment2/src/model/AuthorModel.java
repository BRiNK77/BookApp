package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import controller.AuthorBookGateway;
import controller.AuthorGateway;
import controller.GatewayException;
import controller.ValidationException;

public class AuthorModel {

	private int ID;
	private String first;
	private String last;
	private LocalDate dob;
	private String gender;
	private String website;
	
	public AuthorModel() {

		first = "First name here...";
		last = "Last name here...";
		gender = "Gender here...";
		dob = null;
		website = "Website here...";

	}
	
	public AuthorModel (String firstN, String lastN) {
		this();
		
		this.first = firstN;
		this.last = lastN;
	}

	public boolean isValidId(int id) {
		if(id < 0)
			return false;
		return true;
	}

	public boolean isValidFirst(String firstName) {
		if(firstName == null || firstName.length() < 1 || firstName.length() > 100)
			return false;
		return true;
	}

	public boolean isValidLast(String lastName) {
		if(lastName == null || lastName.length() < 1 || lastName.length() > 100)
			return false;
		return true;
	}
	
	public boolean isValidGender(String gender) {
		if(gender == null || (!gender.equals("Male") && !gender.equals("Female") && !gender.equals("Unknown")))
			return false;
		return true;
	}

	public boolean isValidWebSite(String webSite) {
		if(webSite == null)
			return true;
		if(webSite.length() > 100)
			return false;
		return true;
	}

	public boolean isValidDob(LocalDate dob) {
		if(dob == null || !dob.isBefore(LocalDate.now()))
			return false;
		return true;
	}
	public void save() throws GatewayException, ValidationException {
		if(!isValidFirst(getFirst())){
			throw new ValidationException("Invalid First: " + getFirst());
		}
		if(!isValidLast(getLast())) {
			throw new ValidationException("Invalid Last: " + getLast());
		}
		if(!isValidGender(getGender())) {
			throw new ValidationException("Invalid Gender: " + getGender());
		}
		if(!isValidWebSite(getWebsite())) {
			throw new ValidationException("Invalid Website: " + getWebsite());
		}
		if(!isValidDob(getDob())) {
			throw new ValidationException("Invalid Dob: " + getDob());
		}
		
		if(getID() == 0) {
			AuthorGateway.insertAuthor(this);
		} else {
			AuthorGateway.updateAuthor(this);
		}
	}
	
	
	public void delete() throws GatewayException{
		AuthorGateway.deleteAuthor(this);
	}
	
	public String toString() {
		return this.ID + "  " + first + " " + last;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getFirst() {
		return first;
	}

	public void setFirst(String first) {
		this.first = first;
	}

	public String getLast() {
		return last;
	}

	public void setLast(String last) {
		this.last = last;
	}

	public LocalDate getDob() {
		return dob;
	}

	public void setDob(LocalDate dob) {
		this.dob = dob;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	
}