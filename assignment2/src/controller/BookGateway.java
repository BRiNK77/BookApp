package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.BookModel;

public class BookGateway {
	private static BookGateway instance = null;
	
	private static Connection connection;
	
	private BookGateway(){
		
	}
	
		public static BookGateway getInstance() {
			if(instance == null) {
				instance = new BookGateway();
				
			} // end if
			return instance;
		} // end BookGateway
		
	public static List<BookModel> getBooks() {
		
		List<BookModel> books = new ArrayList<BookModel>();
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = connection.prepareStatement("select * from Books");
			rs = st.executeQuery();
			
			while(rs.next()) {
				// System.out.println(rs.getInt("id"));
				BookModel book = new BookModel(rs.getInt("id"), rs.getString("title"), rs.getString("summary"), rs.getInt("year_published"), rs.getInt("publisher_id"), rs.getString("isbn") );
				books.add(book);
				
			} // end while
		} catch (SQLException e) { 
			e.printStackTrace();
			// throw new AppException(e);
			
		} finally {
			try {
				if(st != null) {
					st.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
				// throw new AppException(e);
			}
		}
		return books;
	} // end getBooks
	
	public void insert(BookModel book) {
		
	}
	
	// part 3 of step 6
	public void updateBook() {
		// TODO: use model's id as a parameter for WHERE clause to SQL to update method
		
		// TODO: throw exception, pass to model, if SQL statement fails, also update gui via JAVAFX Alert
		
	}
	
	public Connection getConnection() {
		return connection;
	}
	
	public void setConnection(Connection connection) {
		BookGateway.connection = connection;
	}
}