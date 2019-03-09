package controller;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import model.BookModel;

public class DetailedController implements Initializable, MyController {
	
	private static Logger logger = LogManager.getLogger(DetailedController.class);
	
	// for part 5
	@FXML private TextField bookTitle;
	@FXML private TextField bookSum;
	@FXML private TextField published;
	@FXML private TextField ISBN;
	
	private BookModel aBook;
	private BookModel bookCopy;
	
	@FXML
	private Button saveB;
	// Trying to implement step 5 here
	
	public DetailedController(BookModel book) {
		this.aBook = book;
		this.bookCopy = book;
		
	}
	
	@FXML
	void saveButtonPressed(ActionEvent event) {
		if(event.getSource() == saveB) {
			logger.info("Save button pressed.");
		}
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		bookTitle.textProperty().bindBidirectional(bookCopy.bookTitleProp());
	}
}