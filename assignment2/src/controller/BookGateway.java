package controller;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import model.AuthorBookModel;
import model.AuthorModel;
import model.AuditTrailModel;
import model.BookModel;
import model.PublisherModel;
import controller.GatewayException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

// handles interactions with database and books, however a few functions for audits and publisher tables have been placed here for 
public class BookGateway {
	private static BookGateway instance = null;
	private static Logger logger = LogManager.getLogger(BookGateway.class);
	private static Connection conn;

	private BookGateway() {

	}

	// creates instance of gateway so that there is only 1 connection per
	// application
	public static BookGateway getInstance() {
		if (instance == null) {
			instance = new BookGateway();

		} // end if
		return instance;
	} // end BookGateway

	// gets list of all publishers
	public static List<PublisherModel> getPublishers() {
		List<PublisherModel> pubs = new ArrayList<PublisherModel>();
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("select * from publisher");
			rs = st.executeQuery();

			while (rs.next()) {
				PublisherModel aPub = new PublisherModel(rs.getInt("id"), rs.getString("publisher_name"));
				pubs.add(aPub);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			// throw new AppException(e);

		} finally {
			try {
				if (st != null) {
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

			while (rs.next()) {
				// sets up new book
				BookModel book = new BookModel(rs.getInt("id"), rs.getString("title"), rs.getString("summary"),
						rs.getInt("year_published"), rs.getInt("publisher_id"), rs.getString("isbn"));
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
				if (st != null) {
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
				if (st != null) {
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

			while (rs.next()) {

				Timestamp ts = rs.getTimestamp("date_added");
				date = ts.toLocalDateTime();
				AuditTrailModel anAudit = new AuditTrailModel(rs.getInt("book_id"), date, rs.getString("entry_msg"));

				listA.add(anAudit);

			} // end while
		} catch (SQLException e) {
			e.printStackTrace();

		} finally {
			try {
				if (st != null) {
					st.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();

			}
		}
		return listA;
	} // end getAuditTrail

	// creates a new book and inserts it into table
	public static void insertBook(BookModel book) {
		PreparedStatement st = null;
		try {
			conn.setAutoCommit(false);

			st = conn.prepareStatement(
					"insert into Books (title, summary, year_published, publisher_id, isbn) values ( ?, ?, ?, ?, ?)");
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
				if (st != null) {
					st.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	} // end insertBook

	// deletes a book from the database
	public static void deleteBook(BookModel book) throws GatewayException {
		PreparedStatement st = null;
		try {
			conn.setAutoCommit(false);

			st = conn.prepareStatement("delete from Books where id = ?");
			st.setInt(1, book.getID());
			st.executeUpdate();

			conn.commit();

		} catch (SQLException e) {
			try {
				conn.rollback();

			} catch (SQLException e1) {
				e1.printStackTrace();
			}

			throw new GatewayException(e);

		} finally {
			try {
				if (st != null)
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

			st = conn.prepareStatement("update Books " + " set title = ? " + " , summary = ? "
					+ " , year_published = ? " + " , publisher_id = ? " + " , isbn = ? " + " where id = ?");

			st.setString(1, aBook.getTitle());
			st.setString(2, aBook.getSummary());
			st.setInt(3, aBook.getYearPublished());
			st.setInt(4, aBook.getPublisher().getId());
			st.setString(5, aBook.getISBN());
			st.setInt(6, aBook.getID());
			st.executeUpdate();

			aBook.setLastModified(BookGateway.getBookLastModifiedById(aBook.getID()));

		} catch (SQLException | ValidationException e) {
			try {
				conn.rollback();

			} catch (SQLException e1) {
				e1.printStackTrace();
			}

			throw new GatewayException(e);

		} finally {
			try {
				if (st != null)
					st.close();

			} catch (SQLException e) {

				throw new GatewayException("SQL Error: " + e.getMessage());
			}
		}
 
	} // end updateBook
	
	public static AuthorModel getAuthorById(int id) throws GatewayException {
		AuthorModel author = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("select * from authors where id = ?");
			st.setInt(1, id);
			ResultSet rs = st.executeQuery();
			rs.next();
			author = new AuthorModel(rs.getString("first_name"), rs.getString("last_name"));
			author.setID(rs.getInt("id"));
			author.setGender(rs.getString("gender"));
			author.setWebsite(rs.getString("web_site"));
			//convert old Date object to a LocalDate
			if(rs.getString("dob") != null)
				author.setDob(LocalDate.parse(rs.getString("dob")));
			rs.close();
		} catch (SQLException e) {
			throw new GatewayException(e);
		} finally {
			try {
				st.close();
			} catch (SQLException e) {
				logger.error(e);
				e.printStackTrace();
			}
		}		
		return author;
	}
	
	public static List<AuthorBookModel> getAuthors(int id) throws GatewayException {
		List<AuthorBookModel> list = FXCollections.observableArrayList();
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("select * from author_book where book_id=?");
			st.setInt(1, id);
			ResultSet rs = st.executeQuery();
			while(rs.next()){
				AuthorModel author = BookGateway.getAuthorById(rs.getInt("author_id"));
				BookModel book = BookGateway.getBookById(rs.getInt("book_id"));
				int royalty = rs.getBigDecimal("royalty").multiply(new BigDecimal(100000)).intValueExact();
				list.add(new AuthorBookModel(author,book,royalty));
			}
		} catch (Exception sql) {
			throw new GatewayException(sql);
		} finally {
			try {
				st.close();
			}
			catch (Exception e) {
				logger.error(e);
				e.printStackTrace();
		}
		}
		return list;
	}
	
	public static List<AuthorModel> getAllAuthors() throws GatewayException{
		List<AuthorModel> list = FXCollections.observableArrayList();
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("select * from authors");
			ResultSet rs = st.executeQuery();
			while(rs.next()){
				AuthorModel author = new AuthorModel(rs.getString("first_name"), rs.getString("last_name"));
				author.setID(rs.getInt("id"));
				author.setGender(rs.getString("gender"));
				author.setWebsite(rs.getString("web_site"));
				//convert old Date object to a LocalDate
				if(rs.getString("dob") != null)
					author.setDob(LocalDate.parse(rs.getString("dob")));
				list.add(author);
			}
		} catch (Exception sql) {
			throw new GatewayException(sql);
		} finally {
			try {
				st.close();
			}
			catch (Exception e) {
				logger.error(e);
				e.printStackTrace();
		}
		}
		return list;
	}
	
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
		} catch (SQLException e) {
			e.printStackTrace();

		} finally {
			try {
				if (st != null) {
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

		} catch (SQLException e) {
			e.printStackTrace();
			throw new ValidationException(e);

		} finally {
			try {
				if (st != null) {
					st.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw new ValidationException(e);
			}
		}
		return date;
	} // end getBookLastModified

	public static void bookAuthorDeleteAudit(AuthorBookModel authorBook) {
		PreparedStatement st = null;
		try {
			String sql = "insert into book_audit_trail (book_id, entry_msg) values (?,?)";
			st = conn.prepareStatement(sql,PreparedStatement.RETURN_GENERATED_KEYS);
			st.setInt(1, authorBook.getBook().getID());
			st.setString(2, "Author: " + authorBook.getAuthor().getFirst() +" "+ authorBook.getAuthor().getLast() + " removed from book");
			st.executeUpdate();
		} catch (SQLException e) {
			logger.error(e);
		} finally {
			try {
				st.close();
			} catch (SQLException e) {
				logger.error(e);
				e.printStackTrace();
			}
		}
	}
	
	public static BookModel getBookById(int id) throws GatewayException {
		BookModel book = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("select * from Books where id = ?");
			st.setInt(1, id);
			ResultSet rs = st.executeQuery();
			rs.next();
			book = new BookModel(rs.getInt("id"), rs.getString("title"), rs.getString("summary"), rs.getInt("year_published"), rs.getInt("publisher_id"), rs.getString("isbn") );
			Timestamp ts = rs.getTimestamp("last_modified");
			book.setLastModified(ts.toLocalDateTime());
		} catch (SQLException e) {
			throw new GatewayException(e);
		} finally {
			try {
				st.close();
			} catch (SQLException e) {
				logger.error(e);
				e.printStackTrace();
			}
		}		
		return book;
	}
	
	public static void bookAuthorInsertAudit(AuthorBookModel authorBook) {
		PreparedStatement st = null;
		try {
			String sql = "insert into book_audit_trail (book_id, entry_msg) values (?,?)";
			st = conn.prepareStatement(sql,PreparedStatement.RETURN_GENERATED_KEYS);
			st.setInt(1, authorBook.getBook().getID());
			st.setString(2, "Author " + authorBook.getAuthor().getFirst() +" "+ authorBook.getAuthor().getLast() + "added to book");
			st.executeUpdate();
		} catch (SQLException e) {
			logger.error(e);
		} finally {
			try {
				st.close();
			} catch (SQLException e) {
				logger.error(e);
				e.printStackTrace();
			}
		}
	}
	
	public static void bookAuthorUpdateAudit(AuthorBookModel authorBook, AuthorBookModel old) {
		PreparedStatement st = null;
		try {
			String sql = "insert into book_audit_trail (book_id, entry_msg) values (?,?)";
			st = conn.prepareStatement(sql,PreparedStatement.RETURN_GENERATED_KEYS);
			st.setInt(1, authorBook.getBook().getID());
			st.setString(2, "royalty for  " + authorBook.getAuthor().getFirst() +" "+ authorBook.getAuthor().getLast() + "was " + authorBook.getRoyalty() + " is now " + authorBook);
			st.executeUpdate();
		} catch (SQLException e) {
			logger.error(e);
		} finally {
			try {
				st.close();
			} catch (SQLException e) {
				logger.error(e);
				e.printStackTrace();
			}
		}
	} 

	public static List<BookModel> getBooks(int pageNumber, int pageSize) throws GatewayException {
		List<BookModel> books = FXCollections.observableArrayList();
		PreparedStatement st = null;
		PublisherModel publisher;
		int index = (pageNumber - 1) * pageSize;
		//int index = pageNumber * pageSize;
		try {
			st = conn.prepareStatement("select * from Books limit ?,?");
			st.setInt(1, index);
			st.setInt(2, pageSize);
			ResultSet rs = st.executeQuery();
			while(rs.next()) {

				BookModel book = new BookModel();
				book.setTitle(rs.getString("title"));
				book.setID(rs.getInt("id"));
				book.setSummary(rs.getString("summary"));
				book.setYearPublished(rs.getInt("year_published"));
				publisher = BookGateway.getPublisherbyId(rs.getInt("publisher_id"));
				book.setPublisher(publisher);
				book.setISBN(rs.getString("isbn"));
				if(rs.getString("last_modified") != null) {
					Timestamp ts = rs.getTimestamp("last_modified");
					book.setLastModified(ts.toLocalDateTime());
				
			}
				books.add(book);
			}
			rs.close();
		} catch (SQLException e) {
			throw new GatewayException(e);
		} finally {
			try {
				st.close();
			} catch (SQLException e) {
				logger.error(e);
				e.printStackTrace();
			}
		}
		return books;
	}
	public static List<BookModel> searchBooks(String search, int pageNumber, int pageSize) throws GatewayException {
		List<BookModel> books = FXCollections.observableArrayList();
		PublisherModel publisher;
		PreparedStatement st = null;
		try {
			int index = (pageNumber - 1) * pageSize;
			st = conn.prepareStatement("select * from Books where title like '%" + search + "%' limit ?,?");
			st.setInt(1, index);
			st.setInt(2, pageSize);
			ResultSet rs = st.executeQuery();
			while(rs.next()) {

				BookModel book = new BookModel();
				book.setTitle(rs.getString("title"));
				book.setID(rs.getInt("id"));
				book.setSummary(rs.getString("summary"));
				book.setYearPublished(rs.getInt("year_published"));
				publisher = BookGateway.getPublisherbyId(rs.getInt("publisher_id"));
				book.setPublisher(publisher);
				book.setISBN(rs.getString("isbn"));
				if(rs.getString("last_modified") != null) {
					Timestamp ts = rs.getTimestamp("last_modified");
					book.setLastModified(ts.toLocalDateTime());
				}
				books.add(book);
			}
			rs.close();
		} catch (SQLException e) {
			throw new GatewayException(e);
		} finally {
			try {
				st.close();
			} catch (SQLException e) {
				logger.error(e);
				e.printStackTrace();
			}
		}
		return books;
	}
	public static int  searchBooksLastPage(String search) throws GatewayException {
		PreparedStatement st = null;
		int pageNumber = 0;
		try {
			st = conn.prepareStatement("select * from Books where title like '" + search + "%'");
			ResultSet rs = st.executeQuery();
			while(rs.next()) {
				pageNumber = rs.getInt(1);
			}
			} catch(SQLException e) {
				throw new GatewayException(e);
			}finally {
				try {
					st.close();
				} catch (SQLException e) {
					logger.error(e);
					e.printStackTrace();
				}
			}
		return pageNumber;
	}
	public static int getPageNumber() throws GatewayException {
		PreparedStatement st = null;
		int lastPageNumber = 0;
		try {
			st = conn.prepareStatement("SELECT COUNT(*) FROM `Books`");
			ResultSet rs = st.executeQuery();
			while(rs.next()) {
				lastPageNumber = rs.getInt(1);
			}
			rs.close();
		} catch (SQLException e) {
			throw new GatewayException(e);
		}finally {
			try {
				st.close();
			} catch (SQLException e) {
				logger.error(e);
				e.printStackTrace();
			}
		}
		return lastPageNumber;
	}
	
	public static int getPageNumberSearch(String search) throws GatewayException {
		PreparedStatement st = null;
		int lastPageNumber = 0;
		try {
			st = conn.prepareStatement("SELECT COUNT(*) FROM `Books` where title like '%" + search + "%'");
			ResultSet rs = st.executeQuery();
			while(rs.next()) {
				lastPageNumber = rs.getInt(1);
			}
			rs.close();
		} catch (SQLException e) {
			throw new GatewayException(e);
		}finally {
			try {
				st.close();
			} catch (SQLException e) {
				logger.error(e);
				e.printStackTrace();
			}
		}
		return lastPageNumber;
	}
	// handles connection with database as an active connection
	public Connection getConnection() {
		return conn;
	}

	public void setConnection(Connection connection) {
		BookGateway.conn = connection;
	}

	public void close() {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	} // end close
} // end BookGateway