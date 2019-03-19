package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.BookModel;
import controller.GatewayException;

public class BookGateway {
	private static BookGateway instance = null;
	
	private static Connection conn;
	
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
			st = conn.prepareStatement("select * from Books");
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
	
	public void insertBook(BookModel book) {
		PreparedStatement st = null;
		try {
			conn.setAutoCommit(false);
			
			st = conn.prepareStatement("insert into Books title, summary, year_published, isbn values ?, ?, ?, ?");
			st.setString(1, book.getTitle());
			st.setString(2, book.getSummary());
			st.setInt(3, book.getYearPublished());
			st.setString(4, book.getISBN());
			st.executeUpdate();
			
			conn.commit();
			
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
	}
	
	public void deleteBook(BookModel book) throws GatewayException{
		PreparedStatement st = null;
		try {
			conn.setAutoCommit(false);
			
			st = conn.prepareStatement("delete from Books where id = ?");
			st.setInt(1, book.getID());
			st.executeUpdate();
			
			conn.commit();
			
		} catch(SQLException e) {
			try {
				conn.rollback();
				
			} catch(SQLException e1) {
				e1.printStackTrace();
			}
			
			throw new GatewayException(e);
			
		} finally {
			try {
				if(st != null)
					st.close();
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				
				throw new GatewayException("SQL Error: " + e.getMessage());
			}
		}
	}
	
	// part 3 of step 6
	public void updateBook(BookModel aBook) throws GatewayException {
		// TODO: use model's id as a parameter for WHERE clause to SQL to update method
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("update Books set title = ?, summary = ?, year_published = ?, isbn = ? where id = ?");
			st.setString(1, aBook.getTitle());
			st.setString(2, aBook.getSummary());
			st.setInt(3, aBook.getYearPublished());
			st.setString(4, aBook.getISBN());
			st.setInt(5, aBook.getID());
			st.executeUpdate();
			
		} catch(SQLException e) {
			try {
				conn.rollback();
				
			} catch(SQLException e1) {
				e1.printStackTrace();
			}
			
			throw new GatewayException(e);
			
		} finally {
			try {
				if(st != null)
					st.close();
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				
				throw new GatewayException("SQL Error: " + e.getMessage());
			}
		}
		// TODO: throw exception, pass to model, if SQL statement fails, also update gui via JAVAFX Alert
		
	} // end updateBook
	
	public Connection getConnection() {
		return conn;
	}
	
	public void setConnection(Connection connection) {
		BookGateway.conn = connection;
	}
	
	public void close() {
		if(conn != null) {
			try {
				conn.close();
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
	}
}