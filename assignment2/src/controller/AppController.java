package controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import controller.BookGateway;
import controller.AuthorListController;
import model.AuditTrailModel;
import model.AuthorBookModel;
import model.AuthorModel;
import model.BookModel;
import model.PublisherModel;

// the overall controller for the application, handles the connection to database, and changing of views
public class AppController implements Initializable, MyController {

	private static Logger logger = LogManager.getLogger(AppController.class);

	private static AppController instance = null;
	private static DetailedController currentCon;
	public static int clearance;
	
	
	
	private AppController(int num) {
		AppController.clearance = num;
		//switchView(ViewType.VIEW10, null);
	}
	private AppController() {
		
	}

	// creates single instance for the application
	public static AppController getInstance(int number) {
		if (instance == null) {
			instance = new AppController();
			currentCon = null;
			
		}
		AppController.setClearance(number);
		return instance;
	}
	// must declare fxml objects with tag

	private BorderPane borderPane;

	@FXML
	private MenuItem mClose;
	@FXML
	private MenuItem mList;
	@FXML
	private MenuItem mAdd;
	@FXML
	private MenuItem mAddA;
	@FXML
	private MenuItem mlistA;
	@FXML
	private MenuItem mlogout;

	// method to handle view switching on single screen
	public boolean switchView(ViewType viewType, Object data) {
		if (currentCon != null) {
			if (currentCon.hasChanged()) {

				Alert alert = new Alert(AlertType.CONFIRMATION);

				alert.getButtonTypes().clear();
				ButtonType buttonTypeOne = new ButtonType("Yes");
				ButtonType buttonTypeTwo = new ButtonType("No");
				ButtonType buttonTypeThree = new ButtonType("Cancel");
				alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeThree);

				alert.setTitle("Save Changes?");
				alert.setHeaderText("The current view has unsaved changes.");
				alert.setContentText("Do you wish to save them before switching to a different view?");

				Optional<ButtonType> result = alert.showAndWait();
				if (result.get().getText().equalsIgnoreCase("Yes")) {
					currentCon.save();
					logger.info("*** saving the view");
				} else if (result.get().getText().equalsIgnoreCase("Cancel")) {
					return false;
				}

			}

		}

		String viewString = ""; // used to store fxml file names
		MyController controller = null; // need a blank controller to populate

		// switch to determine which view to set, also sets controller
		switch (viewType) {

		case VIEW1:
			List<BookModel> list = null;
			if(data == null) {
			try {
				list = BookGateway.getBooks(1,50);
			} catch (GatewayException e2) {
				e2.printStackTrace();
				}
			} else {
				list = (List<BookModel>) data;
			}
			viewString = "/View/BookListView.fxml";
			controller = new BookListController(list);
			break;

		case VIEW2:
			List<PublisherModel> pubs = BookGateway.getPublishers();
			viewString = "/View/DetailedView.fxml";
			currentCon = new DetailedController((BookModel) data, pubs);
			break;

		case VIEW3:
			BookModel book = new BookModel();
			List<PublisherModel> publishers = BookGateway.getPublishers();
			viewString = "/View/DetailedView.fxml";
			currentCon = new DetailedController(book, publishers);
			break;

		case VIEW4:
			int id = ((BookModel) data).getID();
			List<AuditTrailModel> audits = BookGateway.getAuditTrail(id);
			viewString = "/View/AuditView.fxml";
			controller = new AuditViewController(audits, (BookModel) data);
			break;
			
		case VIEW5:
			viewString = "/View/AuthorBookView.fxml";
			controller = new AuthorBookController((AuthorBookModel) data);
			break;
			
		case VIEW6:
			viewString = "/View/NewAuthorBook.fxml";
			controller = new NewAuthorBookController((AuthorBookModel) data);
			break;
			
		case VIEW7:
			AuthorModel author = new AuthorModel();
			viewString = "/View/AuthorView.fxml";
			controller = new AuthorController(author);
			break;
			
		case VIEW8:
			List<AuthorModel> authors = null;
			try {
				authors = BookGateway.getAllAuthors();
			} catch (GatewayException e1) {
				e1.printStackTrace();
			}
			viewString = "/View/AuthorListView.fxml";
			controller = new AuthorListController(authors);
			break;
			
		case VIEW9:
			viewString = "/View/AuthorView.fxml";
			controller = new AuthorController((AuthorModel) data);
			break;
			
		case VIEW10:
			viewString = "/View/LoginView.fxml";
			controller = new LoginController();
			break;
			
		case VIEW11:
			viewString = "/View/Welcome.fxml";
			controller = new blankCon();
			break;
			
		}

		// tries to set the new view with fxml loader
		if (controller == null) {
			try {
				URL url = this.getClass().getResource(viewString);
				FXMLLoader loader = new FXMLLoader(url);
				loader.setController(currentCon);
				Parent viewNode = loader.load();

				borderPane.setCenter(viewNode);
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("Empty view or controller.");
			}
			return true;
		} else {
			try {
				URL url = this.getClass().getResource(viewString);
				FXMLLoader loader = new FXMLLoader(url);
				loader.setController(controller);
				Parent viewNode = loader.load();

				borderPane.setCenter(viewNode);
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("Empty view or controller.");
			}
			return true;
		}
	}


	
	// handles menu actions
	@FXML
	void onMenuClick(ActionEvent event) {
		// must check clearance for clicking on creating new authors or books
		
		if (event.getSource() == mClose) {
			if (currentCon != null) {
				if (currentCon.hasChanged()) {

					Alert alert = new Alert(AlertType.CONFIRMATION);

					alert.getButtonTypes().clear();
					ButtonType buttonTypeOne = new ButtonType("Yes");
					ButtonType buttonTypeTwo = new ButtonType("No");
					ButtonType buttonTypeThree = new ButtonType("Cancel");
					alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeThree);

					alert.setTitle("Save Changes?");
					alert.setHeaderText("The current view has unsaved changes.");
					alert.setContentText("Do you wish to save them before switching to a different view?");

					Optional<ButtonType> result = alert.showAndWait();
					if (result.get().getText().equalsIgnoreCase("Yes")) {
						currentCon.save();
						logger.info("*** saving the view");
					} else if (result.get().getText().equalsIgnoreCase("Cancel")) {
						return;
					}
				}

			}
			Platform.exit();

		} else if (event.getSource() == mList) {
			
			if(AppController.checkPermissions(AppController.clearance, "add")) {
				switchView(ViewType.VIEW1, null);
				} else {
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Invalid permissions.");
					alert.setContentText("Access denied.");
					alert.showAndWait();
				}
			return;

		} else if (event.getSource() == mAdd) {
			if(AppController.checkPermissions(AppController.clearance, "add")) {
			switchView(ViewType.VIEW3, null);
			} else {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Invalid permissions.");
				alert.setContentText("Access denied.");
				alert.showAndWait();
			}
			return;
			
		} else if (event.getSource() == mAddA) {
			if(checkPermissions(AppController.clearance, "add")) {
			switchView(ViewType.VIEW7, null);
			} else {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Invalid permissions.");
				alert.setContentText("Access denied.");
				alert.showAndWait();
			}
			return;
			
		} else if (event.getSource() == mlistA) {
			if(AppController.checkPermissions(AppController.clearance, "add")) {
				switchView(ViewType.VIEW8, null);
				} else {
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Invalid permissions.");
					alert.setContentText("Access denied.");
					alert.showAndWait();
				}
			return;
		} else if (event.getSource() == mlogout) {
			clearance = 0;
			switchView(ViewType.VIEW10, null);
		}
	}

	public static boolean checkPermissions(int num, String action) {
		if(num == 1 && action.equals("add") ) {
			return false;
		} else if(num == 1 && action.equals("delete") ) {
			return false;
		} else if(num == 2 && action.equals("delete")) {
			return false;
		} else if(num == 1 && action.equals("edit")) {
			return false;
		} else if( num <= 0 || num > 3) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	public BorderPane getBorderPane() {
		return borderPane;
	}

	public void setBorderPane(BorderPane borderPane) {
		this.borderPane = borderPane;
	}
	public static int getClearance() {
		return clearance;
	}
	public static void setClearance(int clearance) {
		AppController.clearance = clearance;
	}

	
}
