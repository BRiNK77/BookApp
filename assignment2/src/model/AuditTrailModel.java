package model;

import java.time.LocalDateTime;

public class AuditTrailModel {
	private int id;
	private LocalDateTime dateAdded;
	private String message;

	public AuditTrailModel(int ID, LocalDateTime date, String messa) {
		this.id = ID;
		this.dateAdded = date;
		this.message = messa;

	}

	public AuditTrailModel(int ID, String messa) {
		this.id = ID;
		this.message = messa;
	}

	public String toString() {
		return this.dateAdded + " : " + this.message;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public LocalDateTime getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(LocalDateTime dateAdded) {
		this.dateAdded = dateAdded;
	}

}