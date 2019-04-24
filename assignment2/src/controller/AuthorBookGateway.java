package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import model.AuthorBookModel;
import model.PublisherModel;

public class AuthorBookGateway {

	private static AuthorBookGateway instance = null;
	private static Logger logger = LogManager.getLogger(AuthorBookGateway.class);
	private static Connection conn;

	private AuthorBookGateway() {

	}

	// creates instance of gateway so that there is only 1 connection per
	// application
	public static AuthorBookGateway getInstance() {
		if (instance == null) {
			instance = new AuthorBookGateway();

		} // end if
		return instance;
	} // end BookGateway

		
			
		public static void deleteAuthorBook(AuthorBookModel authorBook) throws GatewayException {
			PreparedStatement st = null;
			try {
				String sql = "DELETE FROM author_book WHERE author_id=? AND book_id=?";
				st = conn.prepareStatement(sql);
				st.setInt(1, authorBook.getAuthor().getID());
				st.setInt(2, authorBook.getBook().getID());
				st.executeUpdate();
				BookGateway.bookAuthorDeleteAudit(authorBook);
			}
			catch (SQLException e) {
				throw new GatewayException(e);
			} finally {
				try {
					st.close();
				} catch (SQLException e) {
					logger.error(e);
					e.printStackTrace();
				}
			}
		}
		
		public static void insertAuthorBook(AuthorBookModel authorBook ) {
			PreparedStatement st = null;
			try {
				String sql = "insert into author_book (author_id,book_id,royalty) values (?,?,?)";
				st = conn.prepareStatement(sql);
				st.setInt(1, authorBook.getAuthor().getID());
				st.setInt(2, authorBook.getBook().getID());
				st.setFloat(3, ((float) (authorBook.getRoyalty()) / 100000));
				st.executeUpdate();
				BookGateway.bookAuthorInsertAudit(authorBook);
			}
			catch (SQLException e) {
				logger.error(e);
			} finally {
				try {
					st.close();
				} catch (SQLException e) {
					logger.error(e);
					//e.printStackTrace();
				}
			}
			logger.info("data inserted into Author_Book");
		}
		
		public static void updateAuthorBook(AuthorBookModel authorBook) throws GatewayException {
			AuthorBookModel old = authorBook;
			PreparedStatement st = null;
			try {
				String sql = " update author_book set royalty = ? where author_id=? AND book_id=?";
				st = conn.prepareStatement(sql);
				st.setFloat(1, ((float) (authorBook.getRoyalty()) / 100000));
				st.setInt(2, authorBook.getAuthor().getID());
				st.setInt(3, authorBook.getBook().getID());
				st.executeUpdate();
				BookGateway.bookAuthorUpdateAudit(authorBook, old);
			}
			catch (SQLException e) {
				throw new GatewayException(e);
			} finally {
				try {
					st.close();
				} catch (SQLException e) {
					logger.error(e);
					e.printStackTrace();
				}
			}
			logger.info("royalty updated");
		}
		
		
public Connection getConnection() {
	return conn;
}

public void setConnection(Connection connection) {
	AuthorBookGateway.conn = connection;
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