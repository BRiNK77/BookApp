package controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import model.AuditTrailModel;
import model.BookModel;
import javafx.event.ActionEvent;


// controller for the audit view
public class AuditViewController implements Initializable, MyController {
	
	private static Logger logger = LogManager.getLogger(AuditViewController.class);
	
	
	@FXML private ListView<AuditTrailModel> listviewAudits;
	@FXML private Button backB;
	@FXML private Label title;
	private List<AuditTrailModel> listData;
	private BookModel theBook;
	
	public AuditViewController(List<AuditTrailModel> audits, BookModel book) {
		this.listData = audits;
		this.theBook =  book;
	}
	
	// handles back button actions
	@FXML void backButtonPressed(ActionEvent event) {
		if(event.getSource() == backB) {
			logger.info("Back button pressed.");
			AppController.getInstance().switchView( ViewType.VIEW2, this.theBook); // needs more
			
		}
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		title.setText("Audit Trail for " + this.theBook.getTitle());
		
		ObservableList<AuditTrailModel> audits = listviewAudits.getItems();
		for(AuditTrailModel audit: listData) {
			audits.add(audit);
			
		}
		
		
	}
	
}