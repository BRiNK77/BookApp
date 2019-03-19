package controller;

import java.net.URL;
import java.util.ResourceBundle;

import javax.xml.bind.ValidationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
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
	
	// for part 5
	@FXML private TextField bookTitle, published, ISBN;
	@FXML private TextArea bookSum;
	
	private BookModel aBook;
	private BookModel bookCopy;
	
	@FXML private Button saveB;
	// Trying to implement step 5 here
	
	public DetailedController(BookModel book) {
		this.aBook = book;
		this.bookCopy = book;
		
	}
	
	public DetailedController() {
		
	}
	
	@FXML void saveButtonPressed(ActionEvent event) {
		if(event.getSource() == saveB) {
			logger.info("Save button pressed.");
			save();
		}
	}
	
	public boolean save() {
	
		try {
			checkUpdate();
			// System.out.println(aBook);
			// System.out.println(bookCopy);
			
			aBook.setTitle(this.bookCopy.getTitle());
			aBook.setSummary(this.bookCopy.getSummary());
			aBook.setYearPublished(this.bookCopy.getYearPublished());
			aBook.setISBN(this.bookCopy.getISBN());
			
			// System.out.println(aBook);
			aBook.saveBook();
			
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Changes saved");
			alert.setHeaderText(null);
			alert.setContentText("Changes saved successfully!");
			
			alert.showAndWait();
			
		} catch(GatewayException | ValidationException e) {
			logger.error("Could not save: " + e.getMessage());
			return false;
		}
		return true;
		
	}
	
	public void checkUpdate() {
		this.bookCopy.setTitle(bookTitle.getText());
		this.bookCopy.setSummary(bookSum.getText());
		this.bookCopy.setYearPublished(Integer.parseInt(published.getText()));
		this.bookCopy.setISBN(ISBN.getText());
		
		if(this.aBook.getTitle() == this.bookCopy.getTitle() && this.aBook.getSummary() == this.bookCopy.getSummary() && this.aBook.getYearPublished() == this.bookCopy.getYearPublished() && this.aBook.getISBN() == this.bookCopy.getISBN()) {
			logger.info("No changes made.");
		} else {
			logger.info("Changes made.");
		}
		
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		bookTitle.setText(this.bookCopy.getTitle());
		bookSum.setText(this.bookCopy.getSummary());
		published.setText("" + this.bookCopy.getYearPublished());
		ISBN.setText(this.bookCopy.getISBN());
	}
}
