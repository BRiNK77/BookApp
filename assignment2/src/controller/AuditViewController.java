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
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import model.AuditTrailModel;
import model.BookModel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class AuditViewController implements Initializable, MyController {
	
	private static Logger logger = LogManager.getLogger(AuditViewController.class);
	
	
	@FXML private ListView<AuditTrailModel> listviewAudits;
	@FXML private Button backB;
	
	private List<AuditTrailModel> listData;
	
	private AuditViewController(List<AuditTrailModel> audits) {
		this.listData = audits;
	}
	
	@FXML void backButtonPressed(ActionEvent event) {
		if(event.getSource() == backB) {
			logger.info("Delete button pressed.");
			
			AppController.getInstance().switchView( ViewType.VIEW2, null); // needs more
			
	
		}
	}
	
	public void initialize(URL location, ResourceBundle resources) {
		ObservableList<AuditTrailModel> audits = listviewAudits.getItems();
		for(AuditTrailModel audit: listData) {
			audits.add(audit);
			
		}
		
		
	}
	
}