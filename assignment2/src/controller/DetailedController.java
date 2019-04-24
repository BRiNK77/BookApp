package controller;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;
import javax.xml.bind.ValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import model.AuditTrailModel;
import model.AuthorBookModel;
import model.AuthorModel;
import model.BookModel;
import model.PublisherModel;

// controller for the detailed view
public class DetailedController implements Initializable, MyController {

	private static Logger logger = LogManager.getLogger(DetailedController.class);

	@FXML
	private TextField bookTitle, published, ISBN, time;
	@FXML
	private TextArea bookSum;
	@FXML
	private ComboBox<PublisherModel> listPub;
	@FXML
	private ListView<AuthorBookModel> listviewAuthors;
	
	private List<AuthorBookModel> listData;
	private BookModel aBook;
	private BookModel bookCopy;

	@FXML
	private Button saveB, auditB, addB, editB, deleteB;
	private LocalDateTime originalTime;
	private List<PublisherModel> listPubs;

	// sets values for the view apon declaration
	public DetailedController(BookModel book, List<PublisherModel> pubs) {
		this.aBook = book;
		this.bookCopy = book;
		this.listPubs = pubs;
		try {
			this.listData = aBook.getAuthors();
		} catch (GatewayException e) {
			e.printStackTrace();
		}
		this.originalTime = aBook.getLastModified();
	}

	// for blank/new books, require a blank controller
	public DetailedController() {

	}

	// handles audit button action
	@FXML
	void auditButtonPressed(ActionEvent event) {
		AppController.getInstance().switchView(ViewType.VIEW4, this.aBook);

	}

	// handles save button action
	@FXML
	void saveButtonPressed(ActionEvent event) {
		if (event.getSource() == saveB) {
			logger.info("Save button pressed.");

			LocalDateTime currentTime;
			try {
				currentTime = BookGateway.getBookLastModifiedById(aBook.getID());
				if (!currentTime.equals(originalTime)) {
					AlertHelper.showWarningMessage("Cannot save!", "Record has been changed since this view loaded",
							"Please refresh your view and try again.");

				}
			} catch (controller.ValidationException e) {
				e.printStackTrace();
			}
			if (save()) {
				logger.info("Changes fully saved.");
				AppController.getInstance().switchView(ViewType.VIEW1, null);
			} else {
				logger.info("Changes not saved.");
			}

		}
	}

	// save function that checks for updates and calls the save function in the
	// gateway to save values to database
	public boolean save() {

		Alert alert = new Alert(AlertType.INFORMATION);
		try {

			// checking for updates on info
			checkUpdate();

			// updates all the values for the book and its copy
			aBook.setTitle(this.bookCopy.getTitle());
			aBook.setSummary(this.bookCopy.getSummary());
			aBook.setYearPublished(this.bookCopy.getYearPublished());
			aBook.setPublisher(this.bookCopy.getPublisher());
			aBook.setISBN(this.bookCopy.getISBN());

			aBook.saveBook();
			// sets the new last modified time stamp
			originalTime = aBook.getLastModified();

			// alerts user of successful save
			alert.setTitle("Changes saved");
			alert.setHeaderText(null);
			alert.setContentText("Changes saved successfully!");

			alert.showAndWait();

		} catch (GatewayException | ValidationException e) {
			logger.error("Could not save: " + e.getMessage());
			alert.setTitle("Changes not saved");
			alert.setHeaderText(null);
			alert.setContentText(e.getMessage());
			return false;
		}
		return true;

	}

	@FXML 
	void addAuthor() {
		AuthorBookModel newAuthBook = new AuthorBookModel();
		if(aBook.getID() != 0) {
			AppController.getInstance().switchView(ViewType.VIEW6, newAuthBook);
			//AppController.getInstance().changeView(AppController.AUTHOR_BOOK_NEW, new AuthorBook(new Author(), book,0,true,conn));
		}else {
			AlertHelper.showWarningMessage("Error", "Unsaved Book", "Please save the book before adding authors");
		}
	}
	
	@FXML 
	void editAuthor() {
		if (listviewAuthors.getSelectionModel().getSelectedItem() != null) {
			AuthorBookModel selected = listviewAuthors.getSelectionModel().getSelectedItem();
			AppController.getInstance().switchView(ViewType.VIEW5, selected);
		}
	}
	
	@FXML 
	void deleteAuthor() {
		try {
			if (listviewAuthors.getSelectionModel().getSelectedItem() != null) {
			AuthorBookGateway.deleteAuthorBook(listviewAuthors.getSelectionModel().getSelectedItem());
			listData = aBook.getAuthors();
			this.listviewAuthors.setItems((ObservableList<AuthorBookModel>) listData);
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}
	
	// checks if the book has changed on screen via the publisher id's
	public boolean hasChanged() {
		if (aBook.getPublisher().getId() != listPub.getValue().getId()) {
			return true;
		}
		return false;
	}

	// checks each field and saves audits for any changes, returns true if changes
	// made, false otherwise
	public boolean checkUpdate() {

		AuditTrailModel audit;
		boolean marker = false;
		if (!bookTitle.getText().equals(this.bookCopy.getTitle())) {

			audit = new AuditTrailModel(this.aBook.getID(),
					"Changes made to title " + this.bookCopy.getTitle() + " to " + bookTitle.getText());
			logger.info("Changes made to title " + this.bookCopy.getTitle() + " to " + bookTitle.getText());
			BookGateway.insertAudit(audit);
			marker = true;

		}
		if (!bookSum.getText().equals(this.bookCopy.getSummary())) {

			audit = new AuditTrailModel(this.aBook.getID(),
					"Changes made to summary " + this.bookCopy.getSummary() + " to " + bookSum.getText());
			logger.info("Changes made to summary " + this.bookCopy.getSummary() + " to " + bookSum.getText());
			BookGateway.insertAudit(audit);
			marker = true;

		}
		if (Integer.parseInt(published.getText()) != this.bookCopy.getYearPublished()) {

			audit = new AuditTrailModel(this.aBook.getID(), "Changes made to publisher year "
					+ this.bookCopy.getYearPublished() + " to " + Integer.parseInt(published.getText()));
			logger.info("Changes made to publisher year " + this.bookCopy.getYearPublished() + " to "
					+ Integer.parseInt(published.getText()));
			BookGateway.insertAudit(audit);
			marker = true;
		}
		if (!ISBN.getText().equals(this.bookCopy.getISBN())) {

			audit = new AuditTrailModel(this.aBook.getID(),
					"Changes made to ISBN " + this.bookCopy.getISBN() + " to " + ISBN.getText());
			logger.info("Changes made to ISBN " + this.bookCopy.getISBN() + " to " + ISBN.getText());
			BookGateway.insertAudit(audit);
			marker = true;

		}
		if (!listPub.getValue().getPubName().equals(this.bookCopy.getPublisher().getPubName())) {

			audit = new AuditTrailModel(this.aBook.getID(), "Changes made to publisher "
					+ this.bookCopy.getPublisher().getId() + " to " + listPub.getValue().getId());
			logger.info("Changes made to publisher " + this.bookCopy.getPublisher().getId() + " to "
					+ listPub.getValue().getId());
			BookGateway.insertAudit(audit);
			marker = true;

		}

		if (marker == false) {
			logger.info("No changes made.");
			return false;
		}

		this.bookCopy.setTitle(bookTitle.getText());
		this.bookCopy.setSummary(bookSum.getText());
		this.bookCopy.setYearPublished(Integer.parseInt(published.getText()));
		this.bookCopy.setPublisher(listPub.getValue());
		this.bookCopy.setISBN(ISBN.getText());

		return true;

	}

	// sets up the view with data
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		listPub.getItems().addAll(listPubs);
		listPub.setValue(this.bookCopy.getPublisher());

		bookTitle.setText(this.bookCopy.getTitle());
		bookSum.setText(this.bookCopy.getSummary());
		published.setText("" + this.bookCopy.getYearPublished());
		ISBN.setText(this.bookCopy.getISBN());
		time.setText("" + this.bookCopy.getLastModified());
		
		ObservableList<AuthorBookModel> authors = listviewAuthors.getItems();
		for (AuthorBookModel author : listData) {
			//System.out.println(author);
			authors.add(author);
		}
	}
}
