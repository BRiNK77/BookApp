package controller;
/* Assignment 2 By: Tristan Zaleski
 *  got stuck on step 4, could not get information to display properly, will be fixing and resubmitting
 * */
import java.net.URL;
import controller.BookGateway;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mysql.cj.jdbc.MysqlDataSource;
import java.sql.Connection;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;

public class Main extends Application {

	private static final Logger logger = LogManager.getLogger();
	
	@Override
	public void start(Stage stage) throws Exception {
		URL url = this.getClass().getResource("/View/MainView.fxml");
		FXMLLoader loader = new FXMLLoader(url);
		
		// gets instance of controller to handle functions of the main screen
		AppController controller = AppController.getInstance();
		loader.setController(controller);
		
		Parent mainNode = loader.load();
		controller.setBorderPane( (BorderPane) mainNode);
		
		stage.setScene(new Scene(mainNode)); // sets scene here
		
		stage.setTitle("Book Organizer");  // sets title of stage
		stage.setWidth(600);
		stage.setHeight(500);
		stage.show();
	}
	
	public void init() throws Exception {
		super.init();
		logger.info("Creating Connection with DB...");
		
		MysqlDataSource ds = new MysqlDataSource();
		ds.setURL("jdbc:mysql://easel2.fulgentcorp.com/ior298");
		ds.setUser("ior298");
		ds.setPassword("Ok6rP1rmmDF7EjyLILwq");
		Connection connection = ds.getConnection();
		
		BookGateway.getInstance().setConnection(connection);
	}
	
	public void stop() throws Exception {
		super.stop();
		
		logger.info("Closing connection to DB...");
		
		BookGateway.getInstance().getConnection().close();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
