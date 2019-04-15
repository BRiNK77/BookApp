package controller;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

import javax.xml.bind.ValidationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import model.AuditTrailModel;
import model.BookModel;
import model.PublisherModel;

public class DetailedController implements Initializable, MyController {
	
	private static Logger logger = LogManager.getLogger(DetailedController.class);
	
	@FXML private TextField bookTitle, published, ISBN, time;
	@FXML private TextArea bookSum;
	@FXML private ComboBox<PublisherModel> listPub;
	
	private BookModel aBook;
	private BookModel bookCopy;
	
	@FXML private Button saveB;
	private LocalDateTime originalTime;
	private List<PublisherModel> listPubs;
	
	public DetailedController(BookModel book, List<PublisherModel> pubs) {
		this.aBook = book;
		this.bookCopy = book;
		this.listPubs = pubs;
		this.originalTime = aBook.getLastModified();
	}
	
	public DetailedController() {
		
	}
	
	@FXML void saveButtonPressed(ActionEvent event) {
		if(event.getSource() == saveB) {
			logger.info("Save button pressed.");
			
			LocalDateTime currentTime;
			try {
				currentTime = BookGateway.getBookLastModifiedById(aBook.getID());
				if(!currentTime.equals(originalTime)) {
					AlertHelper.showWarningMessage("Cannot save!", "Record has been changed since this view loaded", "Please refresh your view and try again.");
					
				}
			} catch (controller.ValidationException e) {
				e.printStackTrace();
			}
			if(save()) {
				logger.info("Changes fully saved.");
				AppController.getInstance().switchView(ViewType.VIEW1, null);
			} else {
				logger.info("Changes not saved.");
			}
			
		}
	}
	
	public boolean save() {
		
		Alert alert = new Alert(AlertType.INFORMATION);
		try {
			
			checkUpdate();
			
			aBook.setTitle(this.bookCopy.getTitle());
			aBook.setSummary(this.bookCopy.getSummary());
			aBook.setYearPublished(this.bookCopy.getYearPublished());
			aBook.setPublisher(this.bookCopy.getPublisher());
			aBook.setISBN(this.bookCopy.getISBN());
			
			//System.out.println(aBook.getPublisher());
			
			aBook.saveBook();
			originalTime = aBook.getLastModified();
			
			alert.setTitle("Changes saved");
			alert.setHeaderText(null);
			alert.setContentText("Changes saved successfully!");
			
			alert.showAndWait();
			
		} catch(GatewayException | ValidationException e) {
			logger.error("Could not save: " + e.getMessage());
			alert.setTitle("Changes not saved");
			alert.setHeaderText(null);
			alert.setContentText(e.getMessage());
			return false;
		}
		return true;
		
	}
	public boolean hasChanged() {
		if(aBook.getPublisher().getId() != listPub.getValue().getId()) {
			return true;
		}
		return false;
	}
	public boolean checkUpdate() {
		
		AuditTrailModel audit;
		
		if(bookTitle.getText() != this.bookCopy.getTitle() ) {
			
			audit = new AuditTrailModel(this.aBook.getID(), "Changes made to title " + bookTitle.getText() + " to " + this.bookCopy.getTitle());
			logger.info("Changes made to title " + bookTitle.getText() + " to " + this.bookCopy.getTitle());
			
		} else if (bookSum.getText() != this.bookCopy.getSummary() ) {
			
			audit = new AuditTrailModel(this.aBook.getID(), "Changes made to summary " + bookSum.getText() + " to " + this.bookCopy.getSummary() );
			logger.info("Changes made to summary " + bookSum.getText() + " to " + this.bookCopy.getSummary());
			
		} else if (Integer.parseInt(published.getText()) != this.bookCopy.getYearPublished()) { 
			
			audit = new AuditTrailModel(this.aBook.getID(), "Changes made to publisher year " + Integer.parseInt(published.getText()) + " to " + this.bookCopy.getYearPublished() );
			logger.info("Changes made to publisher year " + Integer.parseInt(published.getText()) + " to " + this.bookCopy.getYearPublished());
			
		} else if (ISBN.getText() != this.bookCopy.getISBN() ) {
			
			audit = new AuditTrailModel(this.aBook.getID(), "Changes made to ISBN " + ISBN.getText() + " to " + this.bookCopy.getISBN() );
			logger.info("Changes made to ISBN " + ISBN.getText() + " to " + this.bookCopy.getISBN());
			
		} else if(listPub.getValue().getId() != this.bookCopy.getPublisher().getId() ) {
			
			audit = new AuditTrailModel(this.aBook.getID(), "Changes made to publisher " + listPub.getValue().getId() + " to " + this.bookCopy.getPublisher().getId() );
			logger.info("Changes made to publisher " + listPub.getValue().getId() + " to " + this.bookCopy.getPublisher().getId());
			
		} else {
			
			logger.info("No changes made.");
			return false;
			
		}
		
		BookGateway.insertAudit(audit);
		this.bookCopy.setTitle(bookTitle.getText());
		this.bookCopy.setSummary(bookSum.getText());
		this.bookCopy.setYearPublished(Integer.parseInt(published.getText()));
		this.bookCopy.setPublisher(listPub.getValue());
		this.bookCopy.setISBN(ISBN.getText());
		return true;
		
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		listPub.getItems().addAll(listPubs);
		listPub.setValue(this.bookCopy.getPublisher());
		
		bookTitle.setText(this.bookCopy.getTitle());
		bookSum.setText(this.bookCopy.getSummary());
		published.setText("" + this.bookCopy.getYearPublished());
		ISBN.setText(this.bookCopy.getISBN());
		time.setText("" + this.bookCopy.getLastModified());
	}
}
