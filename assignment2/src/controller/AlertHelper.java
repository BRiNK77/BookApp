package controller;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class AlertHelper {
	
	public static void showWarningMessage(String title, String header, String context) {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(context);
		alert.showAndWait();
	}
	
	public static void showINforMessage(String title, String header, String content) {
		Alert alert = new Alert(AlertType.INFORMATION);
		if(title != null && title.length() > 0) {
			alert.setTitle(title);
		}
		if(header != null && header.length() > 0) {
			alert.setHeaderText(header);
		}
		if(content != null && content.length() > 0) {
			alert.setContentText(content);
		}
	}
}