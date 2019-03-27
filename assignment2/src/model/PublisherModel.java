package model;

public class PublisherModel {
	private int id;
	private String pubName;
	
	public PublisherModel(int ID, String publisher) {
		this.id = ID;
		this.pubName = publisher;
	}
	public PublisherModel() {
		id = 1;
	}
	
	@Override
	public String toString() {
		return pubName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPubName() {
		return pubName;
	}

	public void setPubName(String pubName) {
		this.pubName = pubName;
	}
	
}