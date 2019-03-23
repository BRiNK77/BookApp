package controller;

import java.net.URL;
import java.util.ResourceBundle;

import javax.xml.bind.ValidationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import model.BookModel;

public class DetailedController implements Initializable, MyController {
	
	private static Logger logger = LogManager.getLogger(DetailedController.class);
	
	@FXML private TextField bookTitle, published, ISBN, pubID;
	@FXML private TextArea bookSum;
	
	private BookModel aBook;
	private BookModel bookCopy;
	
	@FXML private Button saveB;
	
	public DetailedController(BookModel book) {
		this.aBook = book;
		this.bookCopy = book;
		
	}
	
	public DetailedController() {
		
	}
	
	@FXML void saveButtonPressed(ActionEvent event) {
		if(event.getSource() == saveB) {
			logger.info("Save button pressed.");
			
			if(save()) {
				logger.info("Changes fully saved.");
			} else {
				logger.info("Changes ");
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
			aBook.setPublisherID(this.bookCopy.getPublisherID());
			aBook.setISBN(this.bookCopy.getISBN());
			
			
			aBook.saveBook();
			
			
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
	
	public void checkUpdate() {
		if(bookTitle.getText() == this.bookCopy.getTitle() && bookSum.getText() == this.bookCopy.getSummary() && Integer.parseInt(published.getText()) == this.bookCopy.getYearPublished() && ISBN.getText() == this.bookCopy.getISBN()) {
			logger.info("No changes made.");
		} else {
			logger.info("Changes made.");
		}
		
		this.bookCopy.setTitle(bookTitle.getText());
		this.bookCopy.setSummary(bookSum.getText());
		this.bookCopy.setYearPublished(Integer.parseInt(published.getText()));
		this.bookCopy.setPublisherID(Integer.parseInt(pubID.getText()));
		this.bookCopy.setISBN(ISBN.getText());
		
		
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		bookTitle.setText(this.bookCopy.getTitle());
		bookSum.setText(this.bookCopy.getSummary());
		published.setText("" + this.bookCopy.getYearPublished());
		pubID.setText("" + this.bookCopy.getPublisherID());
		ISBN.setText(this.bookCopy.getISBN());
	}
}
