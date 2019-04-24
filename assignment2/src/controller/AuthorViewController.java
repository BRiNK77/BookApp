package controller;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import model.AuthorBookModel;
import model.AuthorModel;

public class AuthorViewController implements Initializable, MyController {
	private static Logger logger = LogManager.getLogger(AuthorViewController.class);
	
    @FXML private TextField firstname;
    @FXML private TextField lastname;
    @FXML private TextField gender;
    @FXML private TextField website;
    @FXML private TextField dateofbirth;
    
    @FXML private Button saveB;
    private AuthorModel author;
    
    public AuthorViewController(AuthorModel author) {
    
    	this.author = author;
    }
    
    public AuthorViewController() {
    	
	}

	public AuthorViewController(AuthorBookModel arg) {
    	this.author = arg.getAuthor();
	}

	@FXML void saveAuthor(ActionEvent event) {
		logger.info(author.getID());
    	logger.info("Model's name is " + author.getFirst()); 	
    	if(!author.isValidFirstName(author.getFirst())) {
    		logger.error("Invalid author first name " + author.getFirst());
    		AlertHelper.showWarningMessage("Oh crap!", "Author name is invalid. Please input a first name with 0-100 characters", "Arcane error number plus description");
    		return;
    	}
    	if(!author.isValidLastName(author.getLast())) {
    		logger.error("Invalid author last name " + author.getLast());
    		AlertHelper.showWarningMessage("Oh crap!", "Author name is invalid. Please input a last name with 0-100 characters", "Arcane error number plus description");
    		return;
    	}
    	if(!author.isValidGender(author.getGender())) {
    		logger.error("Invalid author gender " + author.getGender());
    		AlertHelper.showWarningMessage("Oh crap!", "Author gender is invalid. Please enter \"Male\" or \"female\"", "Arcane error number plus description");
    		return;
    	}
    	if(!author.isValidWebSite(author.getWebsite())) {
    		logger.error("Invalid author website " + author.getWebsite());
    		AlertHelper.showWarningMessage("Oh crap!", "Author website is invalid. Please enter a website with 0-100 characters", "Arcane error number plus description");
    		return;
    	}
    	if(!author.isValidDateOfBirth(author.getDob())){
    		logger.error("Invalid author date of birth " + author.getDob());
    		AlertHelper.showWarningMessage("Oh crap!", "Author date of birth is invalid. please enter in MM/DD/YYYY", "Arcane error number plus description");
    		return;
    	}
    	if(author.getID() == 0) {
    		try {
				author.save();
			} catch (GatewayException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}else {
    		try {
				author.save();
			} catch (GatewayException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
	}
	@FXML void deleteAuthor(ActionEvent event) throws GatewayException {
		logger.info("Author " + author.getFirst() + " " + author.getLast() + " has been deleted");
		author.delete();
	}
	

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		
	}
}