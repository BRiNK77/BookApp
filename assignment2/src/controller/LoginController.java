package controller;

import javafx.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class LoginController implements Initializable, MyController {
	
	public int clearance;
	
	public LoginController() {
		
	}
	
	@FXML
	private Button logB;
	
	@FXML
	private TextField userN;
	
	@FXML
	private TextField pass;
	
	@FXML
	void login(ActionEvent event) {
		if(event.getSource() == logB) {
			if(userN.getText().equals("wilma") && pass.getText().equals("arugula")) {
				clearance = 3; // full
			} else if (userN.getText().equals("leeroy") && pass.getText().equals("wipeout")) {
				clearance = 2; // no delete
			} else if (userN.getText().equals("sasquatch") && pass.getText().equals("jerky")) {
				clearance = 1; // no changes, only existing book viewing
			} else {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Invalid Login");
				alert.setHeaderText(null);
				alert.setContentText("Credentials are invalid.");

				alert.showAndWait();
				return;
			}
		}
		AppController.getInstance(clearance).switchView(ViewType.VIEW11, null);
	}
	
	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		
	}
	
	
}