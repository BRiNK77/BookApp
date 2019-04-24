package model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import controller.AuthorBookGateway;
import controller.AuthorGateway;
import controller.BookGateway;
import controller.GatewayException;

public class AuthorBookModel {

	private BookModel book;
	private AuthorModel author;
	private int royalty;
	private boolean newRecord;

	public AuthorBookModel() {
		author = new AuthorModel();
		book = new BookModel();
		royalty = 0;
		newRecord = true;

	}

	public AuthorBookModel(AuthorModel anAuthor, BookModel abook, int roy) {
		author = anAuthor;
		book = abook;
		royalty = roy;
		newRecord = false;

	}
	
	public AuthorBookModel(int auth_id, int book_id, int roy) throws GatewayException {
		author = AuthorGateway.getAuthorById(auth_id);
		book = BookGateway.getBookById(book_id);
		royalty = roy;
		newRecord = false;
	}

	public void saveAuthorBook() {
		if (getNewRecord()) {
			AuditTrailModel audit = new AuditTrailModel(this.getBook().getID(), "New Author Book added.");
			AuthorBookGateway.insertAuthorBook(this);
			BookGateway.insertAudit(audit);
		} else {
			try {
				AuthorBookGateway.updateAuthorBook(this);
			} catch (GatewayException e) {
	
				e.printStackTrace();
			}
		}
	}
	public boolean isValidAuthor(AuthorModel author) {
		if(author.getID() == 0)
			return false;
		return true;
	}
	
	public boolean isValidBook(BookModel book) {
		if(book.getID() == 0)
			return false;
		return true;
	}
	
	public boolean isValidRoyalty(int royalty) {
		if (royalty < 0f || royalty > 100f)
			return false;
		return true;
	}
	public String toString() {
		return this.author.getFirst() + " " + this.author.getLast() + "     " + this.royalty/1000 + "%";
	}

	public boolean getNewRecord() {
		return this.newRecord;
	}

	public BookModel getBook() {
		return book;
	}

	public void setBook(BookModel book) {
		this.book = book;
	}

	public AuthorModel getAuthor() {
		return author;
	}

	public void setAuthor(AuthorModel author) {
		this.author = author;
	}

	public int getRoyalty() {
		return royalty/1000;
	}

	public void setRoyalty(int royalty) {
		this.royalty = royalty;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

}