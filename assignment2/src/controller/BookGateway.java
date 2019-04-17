package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import model.AuditTrailModel;
import model.BookModel;
import model.PublisherModel;
import controller.GatewayException;

// handles interactions with database and books, however a few functions for audits and publisher tables have been placed here for 
public class BookGateway {
	private static BookGateway instance = null;
	
	private static Connection conn;
	
	private BookGateway(){
		
	}
		// creates instance of gateway so that there is only 1 connection per application
		public static BookGateway getInstance() {
			if(instance == null) {
				instance = new BookGateway();
				
			} // end if
			return instance;
		} // end BookGateway
	
	// gets list of all publishers
	public static List<PublisherModel> getPublishers(){
		List<PublisherModel> pubs = new ArrayList<PublisherModel>();
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("select * from publisher");
			rs = st.executeQuery();
			
			while(rs.next()) {
				PublisherModel aPub = new PublisherModel(rs.getInt("id"), rs.getString("publisher_name"));
				pubs.add(aPub);
			}
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
		return pubs;
	} // end getPub
	
	// gets list of all books
	public static List<BookModel> getBooks() {
		
		List<BookModel> books = new ArrayList<BookModel>();
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement("select * from Books");
			rs = st.executeQuery();
			
			while(rs.next()) {
				// sets up new book 
				BookModel book = new BookModel(rs.getInt("id"), rs.getString("title"), rs.getString("summary"), rs.getInt("year_published"), rs.getInt("publisher_id"), rs.getString("isbn") );
				// gets timestamp
				Timestamp ts = rs.getTimestamp("last_modified");
				book.setLastModified(ts.toLocalDateTime());
				// adds book to list
				books.add(book);
				
			} // end while
		} catch (SQLException e) { 
			e.printStackTrace();
			
			
		} finally {
			try {
				if(st != null) {
					st.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
				
			}
		}
		return books;
	} // end getBooks
	
	// creates an audit with given data and inserts it into database
	public static void insertAudit(AuditTrailModel audit) {
		PreparedStatement st = null;
		try {
			conn.setAutoCommit(false);
			
			st = conn.prepareStatement("insert into book_audit_trail (book_id, entry_msg) values ( ?, ?)");
			st.setInt(1, audit.getId());
			st.setString(2, audit.getMessage());
			st.executeUpdate();
			
			conn.commit();
			
		} catch (SQLException e) { 
			try {
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
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
	} // end instertAudit
	
// gets audit trail for given book id
public static List<AuditTrailModel> getAuditTrail(int id) {
		
		List<AuditTrailModel> listA = new ArrayList<AuditTrailModel>();
		PreparedStatement st = null;
		ResultSet rs = null;
		LocalDateTime date = null;
		
		try {
			st = conn.prepareStatement("select * from book_audit_trail where book_id = ? order by date_added asc");
			st.setInt(1, id);
			rs = st.executeQuery();
			
			while(rs.next()) {
				
				Timestamp ts = rs.getTimestamp("date_added");
				date = ts.toLocalDateTime();
				AuditTrailModel anAudit = new AuditTrailModel(rs.getInt("book_id"), date, rs.getString("entry_msg") );
				
				listA.add(anAudit);
				
			} // end while
		} catch (SQLException e) { 
			e.printStackTrace();
			
			
		} finally {
			try {
				if(st != null) {
					st.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
				
			}
		}
		return listA;
	} //  end getAuditTrail

	// creates a new book and inserts it into table
	public static void insertBook(BookModel book) {
		PreparedStatement st = null;
		try {
			conn.setAutoCommit(false);
			
			st = conn.prepareStatement("insert into Books (title, summary, year_published, publisher_id, isbn) values ( ?, ?, ?, ?, ?)");
			st.setString(1, book.getTitle());
			st.setString(2, book.getSummary());
			st.setInt(3, book.getYearPublished());
			st.setInt(4, book.getPublisher().getId());
			st.setString(5, book.getISBN());
			st.executeUpdate();
			
			conn.commit();
			
		} catch (SQLException e) { 
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
			
		} finally {
			try {
				if(st != null) {
					st.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	} // end insertBook
	
	// deletes a book from the database
	public static void deleteBook(BookModel book) throws GatewayException{
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
	} // end deleteBook
	
	// updates the book with newly acquired data upon a save
	public static void updateBook(BookModel aBook) throws GatewayException {
		PreparedStatement st = null;
		try {
			
			st = conn.prepareStatement("update Books "
					+ " set title = ? "
					+ " , summary = ? "
					+ " , year_published = ? "
					+ " , publisher_id = ? "
					+ " , isbn = ? "
					+ " where id = ?");
			
			st.setString(1, aBook.getTitle());
			st.setString(2, aBook.getSummary());
			st.setInt(3, aBook.getYearPublished());
			st.setInt(4, aBook.getPublisher().getId());
			st.setString(5, aBook.getISBN());
			st.setInt(6, aBook.getID());
			st.executeUpdate();
			
			aBook.setLastModified(BookGateway.getBookLastModifiedById(aBook.getID()));
			
		} catch(SQLException | ValidationException e) {
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
				
			} catch (SQLException e) {
				
				throw new GatewayException("SQL Error: " + e.getMessage());
			}
		}
		
	} // end updateBook
	
	// gets the publisher for book using book id
	public static PublisherModel getPublisherbyId(int id) {
		PreparedStatement st = null;
		PublisherModel pub = null;
		try {
			st = conn.prepareStatement("select * from publisher where id = ?");
			st.setInt(1, id);
			ResultSet rs = st.executeQuery();
			rs.next();
			pub = new PublisherModel(rs.getInt("id"), rs.getString("publisher_name"));
		} catch(SQLException e) {
			e.printStackTrace();
			
		} finally {
			try {
				if(st != null) {
					st.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
		}
	}
		return pub;
	} // getPubbyID
	
	// gets the time stamp for the last modifed record of the book from its id
	public static LocalDateTime getBookLastModifiedById(int id) throws ValidationException {
		LocalDateTime date = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("select * from Books where id = ?");
			st.setInt(1, id);
			ResultSet rs = st.executeQuery();
			rs.next();
			Timestamp ts = rs.getTimestamp("last_modified");
			date = ts.toLocalDateTime();
			
		} catch(SQLException e) {
			e.printStackTrace();
			throw new ValidationException(e);
			
		} finally {
			try {
				if(st != null) {
					st.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw new ValidationException(e);
			}
		}
		return date;
	} // end getBookLastModified
	
	// handles connection with database as an active connection
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
	} // end close
} // end BookGateway