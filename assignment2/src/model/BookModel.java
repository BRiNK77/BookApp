package model;

import javax.xml.bind.ValidationException;

import controller.BookGateway;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class BookModel {
	
	private int ID;
	private String Title;
	private String Summary;
	private int yearPublished;
	private int publisherID;
	private String ISBN;
	
	private BookGateway gateway;

	public BookModel(int id, String title, String summary, int yearPublished, int publisherid, String isbn) {  //int id, String title, String summary, int yearPublished, String isbn
		this();
		
		this.ID = id;
		this.Title = title;
		this.Summary = summary;
		this.yearPublished = yearPublished;
		this.publisherID = publisherid;
		this.ISBN = isbn;
		
	}
	
	public BookModel() {
		
	}
	
	public String toString() {
		return this.ID + " : " + this.Title + "     Year: " + this.yearPublished + "   ISBN: " + this.ISBN;
	}
	
	// step 6 business rules checks 
	public boolean titleCheck(String title) {
		boolean check = false;
		if(title.length() != 0 && title.length() <= 255) {
			check = true;
		}
		return check;
	}
	
	public boolean summaryCheck(String summary) {
		boolean check = false;
		if(summary.length() <= 65536) {
			check = true;
		}
		return check;
	}
	
	public boolean yearPubCheck(int year) {
		boolean check = false;
		if(year >= 1455 && year <= 2019 ) {
			check = true;
		}
		return check;
	}
	
	public boolean isbnCheck(String isbn) {
		boolean check = false;
		if(isbn.length() <= 13) {
			check = true;
		}
		return check;
	}

	// part 2 of step 6
	public void saveBook() throws ValidationException {
		// TODO: call checks on all data in view, throw exceptions here and in gui
		if(!titleCheck(getTitle())) {
			throw new ValidationException("Invalid Title: " + getTitle());
		}
		if(!summaryCheck(getSummary())) {
			throw new ValidationException("Invalid Summary: " + getSummary());
		}
		if(!yearPubCheck(getYearPublished())) {
			throw new ValidationException("Invalid Year: " + getYearPublished());
		}
		if(!isbnCheck(getISBN())) {
			throw new ValidationException("Invalid ISBN: " + getISBN());
		}
		//TODO: save any changes made to book copy onto original book passed in
		
		//TODO: updateBook method from BookGateway to update that book in database
		gateway.updateBook(this);
	}
	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getTitle() {
		return Title;
	}

	public void setTitle(String title) {
		this.Title = title;
	}

	public String getSummary() {
		return Summary;
	}

	public void setSummary(String summary) {
		this.Summary = summary;
	}

	public int getYearPublished() {
		return yearPublished;
	}

	public void setYearPublished(int yearPublished) {
		this.yearPublished = yearPublished;
	}
	public int getPublisherID() {
		return publisherID;
	}

	public void setPublisherID(int publisherID) {
		this.publisherID = publisherID;
	}
	public String getISBN() {
		return ISBN;
	}

	public void setISBN(String iSBN) {
		this.ISBN = iSBN;
	}
	/*
	public SimpleStringProperty bookTitleProp() {
		return Title;
	}
	
	public SimpleStringProperty bookSumProp() {
		return Summary;
	}
	
	public SimpleIntegerProperty bookPublishProp() {
		return yearPublished;
	}
	
	public SimpleIntegerProperty bookPubIDProp() {
		return publisherID;
	}
	
	public SimpleStringProperty bookISBNProp() {
		return ISBN;
	}
	*/
}