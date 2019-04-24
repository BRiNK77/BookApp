package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import model.AuthorModel;

public class AuthorGateway{
	
	private AuthorGateway() {
		
	}
	
	private static AuthorBookGateway instance = null;
	private static Logger logger = LogManager.getLogger(AuthorBookGateway.class);
	private static Connection conn;

	public static void deleteAuthor(AuthorModel author) throws GatewayException {
		PreparedStatement st = null;
		try {
			String sql = "delete from author where id = ? ";
			st = conn.prepareStatement(sql);
			st.setInt(1, author.getID());
			st.executeUpdate();

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
	}
	
	public static void insertAuthor(AuthorModel author) throws GatewayException {
		PreparedStatement st = null;
		try {
			String sql = "insert into author "
					+ " (first_name, last_name, gender, web_site, dob) "
					+ " values (?, ?, ?, ?, ?) ";
			st = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
			st.setString(1, author.getFirst());
			st.setString(2, author.getLast());
			st.setString(3, author.getGender());
			st.setString(4, author.getWebsite());
			//Date tempDate = java.sql.Date.valueOf(author.getDateOfBirth());
			st.setString(5, author.getDob().toString());
			st.executeUpdate();
			ResultSet rs = st.getGeneratedKeys();
			System.out.println(rs);
			rs.first();
			author.setID(rs.getInt(1));
			
			logger.info("new id is " + author.getID());
			
			rs.close();
			//insertAudit(author);
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
	}


	public static void updateAuthor(AuthorModel author) throws GatewayException {
		PreparedStatement st = null;
		LocalDateTime old = getAuthorLastModified(author);
		AuthorModel oldAuthor = getAuthorById(author.getID()) ;
		AuthorModel updateAuthor;
		if (old.equals(author.getLastModified()) ) {
			try {
				String sql = "update author "
						+ " set first_name = ?, last_name = ?, gender = ?, web_site = ?, dob = ? "
						+ " where id = ? ";
				st = conn.prepareStatement(sql);
				st.setString(1, author.getFirst());
				st.setString(2, author.getLast());
				st.setString(3, author.getGender());
				st.setString(4, author.getWebsite());
				st.setString(5, author.getDob().toString());
				st.setInt(6, author.getID());
				st.executeUpdate();
				updateAuthor = getAuthorById(author.getID());
				author.setLastModified(updateAuthor.getLastModified());
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
		}
		else {
			AlertHelper.showWarningMessage("Oh crap!", "Out of Date reference", "It seems like the author youre tring to update is not up to date! Please go back to the Author List and fetch the author again!");
		}
		//updateAudit(oldAuthor);
	}

	public static AuthorModel getAuthorById(int id) throws GatewayException {
		AuthorModel author = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("select * from author where id = ?");
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
	
	public static LocalDateTime getAuthorLastModified(AuthorModel author) throws GatewayException {
		PreparedStatement st = null;
		LocalDateTime old = null; 
		try {
			st = conn.prepareStatement("select * from author where id = ?");
			st.setInt(1, author.getID());
			ResultSet rs = st.executeQuery();
			rs.next();
			old = rs.getTimestamp("last_modified").toLocalDateTime();
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
		return old;
	}
}