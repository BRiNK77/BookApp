package controller;

import java.net.URL;
import java.time.LocalDate;
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
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import model.AuditTrailModel;
import model.AuthorBookModel;
import model.AuthorModel;
import model.BookModel;
import model.PublisherModel;

public class AuthorController implements Initializable, MyController {

	private static Logger logger = LogManager.getLogger(AuthorController.class);
	
	@FXML
	private TextField first, last, dob, gender, website;
	@FXML
	private Button saveB, backB;
	
	private AuthorModel author;
	
	public AuthorController(AuthorModel auth) {
		this.author = auth;

	}
	
	public AuthorController() {
		
	}
	@FXML
	void back() {
		AppController.getInstance().switchView(ViewType.VIEW8, null);	
		
	}
	@FXML
	void saveButtonPressed(ActionEvent event) {
		if(event.getSource() == saveB) {
			logger.info("Save button pressed.");
			
			if(save()) {
				logger.info("Changes fully saved.");
				AppController.getInstance().switchView(ViewType.VIEW8, null);			
			} else {
				logger.info("Changes not saved.");
			}
		}
	}

	public boolean save() {
		Alert alert = new Alert(AlertType.INFORMATION);
		
		CheckUpdate();
		
		try {
			author.save();
			
			alert.setTitle("Changes saved");
			alert.setHeaderText(null);
			alert.setContentText("Changes saved successfully!");

			alert.showAndWait();
		} catch (GatewayException | controller.ValidationException e) {
			logger.error("Could not save: " + e.getMessage());
			alert.setTitle("Changes not saved");
			alert.setHeaderText(null);
			alert.setContentText(e.getMessage());
			return false;
		}
		return true;
		
	}
	
	public boolean CheckUpdate() {
		boolean marker = false;
		
		if(!first.getText().equals(this.author.getFirst())) {
			logger.info("Changes made to first name " + this.author.getFirst() + " to " + first.getText());
		marker = true;
		}
		
		if(!last.getText().equals(this.author.getLast())) {
			logger.info("Changes made to last name " + this.author.getLast() + " to " + last.getText());
		marker = true;
		}
		
		if(!LocalDate.parse(dob.getText()).equals(this.author.getDob())) {
			logger.info("Changes made to DOB " + this.author.getDob() + " to " + dob.getText());
		marker = true;
		}
		
		if(!gender.getText().equals(this.author.getGender())) {
			logger.info("Changes made to gender " + this.author.getGender() + " to " + gender.getText());
		marker = true;
		}
		
		if(!website.getText().equals(this.author.getWebsite())) {
			logger.info("Changes made to website " + this.author.getWebsite() + " to " + website.getText());
		marker = true;
		}
		
		if( marker == false) {
			logger.info("No changes made.");
			return marker;
		}
		// first, last, dob, gender, website, time
		this.author.setFirst(first.getText());
		this.author.setLast(last.getText());
		this.author.setDob(LocalDate.parse(dob.getText()));
		this.author.setGender(gender.getText());
		this.author.setWebsite(website.getText());
		
		return true;
	}
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		first.setText(this.author.getFirst());
		last.setText(this.author.getLast());
		dob.setText("" + this.author.getDob());
		gender.setText(this.author.getGender());
		website.setText(this.author.getWebsite());
	}
	
}