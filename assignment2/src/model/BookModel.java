package model;

import java.time.LocalDateTime;
import java.util.List;

import javax.xml.bind.ValidationException;

import controller.BookGateway;
import controller.GatewayException;


public class BookModel {
	
	private int ID;
	private String Title;
	private String Summary;
	private int yearPublished;
	private PublisherModel publisher;
	private String ISBN;
	private LocalDateTime lastModified;

	public BookModel(int id, String title, String summary, int yearPublished, int publisher, String isbn) {  //int id, String title, String summary, int yearPublished, String isbn
		this();
		
		this.ID = id;
		this.Title = title;
		this.Summary = summary;
		this.yearPublished = yearPublished;
		this.publisher = BookGateway.getPublisherbyId(publisher);
		this.ISBN = isbn;
		
	}
	
	public BookModel() {
		ID = 0;
		Title = "Title Here...";
		Summary = "Summary Here...";
		publisher = new PublisherModel();
		ISBN = "ISBN Here...";
		lastModified = null;
	}
	
	public String toString() {
		return this.ID + " : " + this.Title + "     Year: " + this.yearPublished + "   ISBN: " + this.ISBN;
	}
	
	public boolean titleCheck(String title) {
		boolean check = false;
		if(title == "Title Here...") {
			return check;
		}
		if(title.length() != 0 && title.length() <= 255) {
			check = true;
		}
		return check;
	}
	
	public boolean summaryCheck(String summary) {
		boolean check = false;
		if(summary == "Summary Here...") {
			return check;
		}
		if(summary.length() <= 65536) {
			check = true;
		}
		return check;
	}
	
	public List<AuditTrailModel> getAuditTrail(){
		List<AuditTrailModel> list = BookGateway.getAuditTrail(ID);
		return list;
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
		if(isbn == "ISBN Here...") {
			return check;
		}
		if(isbn.length() <= 13) {
			check = true;
		}
		return check;
	}

	public void saveBook() throws ValidationException, GatewayException {
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
		
		if(this.getID() == 0) {
			AuditTrailModel audit = new AuditTrailModel(this.getID(),"Book added.");
			BookGateway.insertBook(this);
			BookGateway.insertAudit(audit);
			
		} else {
			BookGateway.updateBook(this);
		
		}
	}
	
	public LocalDateTime getLastModified() {
		return lastModified;
	}

	public void setLastModified(LocalDateTime lastModified) {
		this.lastModified = lastModified;
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
	
	public PublisherModel getPublisher() {
		return publisher;
	}

	public void setPublisher(PublisherModel publisher) {
		this.publisher = publisher;
	}
	
	public String getISBN() {
		return ISBN;
	}

	public void setISBN(String iSBN) {
		this.ISBN = iSBN;
	}
	
}