package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sentimentanalyser.BOWTExt;

/**
 * This class...
 */
public class DatabaseHandler {
	
	private String dbName;
	private String dbPassword;
	private String user;
	private String url;
	private Connection conn;
	
	private final String SQLQuery = "Select * from NewsArticles";
	private final String SQLQueryWriteSentiment = "Insert into SentimentCoreNlp (source_uri, sentiment) VALUES";
	private final String SQLQueryWriteSentimentAvgSentence = "Insert into SentimentCoreNlp (source_uri, sentimentAvgSentence) VALUES";

	private final String SQLQuerySelectNotProcessed = "Select n.source_uri, n.text from NewsArticles n " +
			"Left Join SentimentCoreNlp nlp ON nlp.source_uri = n.source_uri" +
			" Where nlp.source_uri IS NULL";


	/*private final String SQLQuerySelectNotProcessedAvg = "Select n.source_uri, n.text from NewsArticles n " +
			"Left Join SentimentCoreNlp nlp ON nlp.source_uri = n.source_uri" +
			" Where nlp.source_uri IS NOT NULL And sentimentAvgSentence is NULL";*/

	private final String SQLQuerySelectNotProcessedAvg = "Select n.source_uri, n.text from NewsArticles n " +
			"Left Join SentimentCoreNlp nlp ON nlp.source_uri = n.source_uri" +
			" Where senAvgSentence2 is null and lineID > 10000";



	private final String SQLQuerySelectDB = "Use webmining";
	
	public DatabaseHandler(String url, String dbName, String user, String dbPassword) throws SQLException{
		this.dbName = dbName;
		this.dbPassword = dbPassword;
		this.user = user;
		this.url = "jdbc:mysql://"+ url;
		
		this.connectToDB();
	}
	
	
	public void connectToDB() throws SQLException{
		if(dbName == null || dbPassword == null || url == null || user == null){
			System.out.println("Coud not connect to database");
			return;
		}
		
		try{
			Class.forName("com.mysql.jdbc.Driver"); 
			System.out.println("Connecting to database...");
		    conn = DriverManager.getConnection(url,user,dbPassword);
		    System.out.println("Connected to database"); 
		}catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		}
	
	}
	
	public void closeConnection(){
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * SQL-Query
	 * **/
	public Boolean writeSentimentAvgSentence(String uri, int sentiment){
		String query = this.SQLQueryWriteSentimentAvgSentence +"('" +uri+"',"+sentiment +");";
		return this.executeInsertSQLStatement(query);
	}

	/**
	 * SQL-Query
	 * **/
	public Boolean writeSentiment(String uri, int sentiment){
		String query = this.SQLQueryWriteSentiment +"('" +uri+"',"+sentiment +");";
		return this.executeInsertSQLStatement(query);
	}
	/**
	 * Update AVG Sentiment
	 * @param
	 * @return
	 */
	public boolean updateSentiment(String uri, int sentiment){
		System.out.println(sentiment);
		String SQLQueryUpdateSentimentRow = "UPDATE SentimentCoreNlp\n" +
				"SET senAvgSentence2 = '"+ sentiment+"'\n" +
				"WHERE source_uri = '"+uri+"';";
		return this.executeInsertSQLStatement(SQLQueryUpdateSentimentRow);
	}
	
	private Boolean executeInsertSQLStatement(String query){
		try {
			if(conn.isClosed() == true){
				return false;
			}
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(query);
			
			stmt.close();
			System.out.println("Updated sentiment");
			return true;
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	    return false;
	}
	
	

	public List<BOWTExt> notProcessedAvgSentence(){
		try {
			return getArticles(SQLQuerySelectNotProcessedAvg, "source_uri", "text");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public List<BOWTExt> listOfNotProcessedArticles(){
		try {
			return getArticles(SQLQuerySelectNotProcessed, "source_uri", "text");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	

	/*
	 * Retuns a list of all articles stored at the database
	 * */
	private List<BOWTExt> getArticles(String query, String col1, String col2) throws SQLException{
		List<BOWTExt> l = new ArrayList<BOWTExt>();
		List<Map<String, Object>> res = this.executeReadSQLStatement(query);
		
		if(res == null){
			return null;
		}
		
		for(int i= 0; i<res.size(); i++){
			Map<String, Object> currentEntry = res.get(i);
			
			String uri = (String) currentEntry.get(col1);
			String bow = (String) currentEntry.get(col2);
			l.add(new BOWTExt(uri, bow));
		}
		
		
		return l;
	    
	}
	
	/*
	 * Executes SQL statement
	 * */
	private List<Map<String, Object>> executeReadSQLStatement(String statement){
		try {
			if(conn.isClosed() == true){
				return null;
			}
			if(!this.selectDatabase()){
				return null;
			}
			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(statement);
			
			
			 List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
			 Map<String, Object> row = null;

			 ResultSetMetaData metaData = rs.getMetaData();
			 Integer columnCount = metaData.getColumnCount();

			 while (rs.next()) {
			        row = new HashMap<String, Object>();
			        for (int i = 1; i <= columnCount; i++) {
			            row.put(metaData.getColumnName(i), rs.getObject(i));
			        }
			        resultList.add(row);
			}
			
			rs.close();
			stmt.close();
			
			return resultList;
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	    return null;
	}
	

	
	/*
	 * Select database
	 * */
	private boolean selectDatabase(){
		try {
			if(conn.isClosed() == true){
				return false;
			}
			
			Statement stmt = conn.createStatement();
			stmt.executeQuery(SQLQuerySelectDB);	
			System.out.println("Select database "+ this.dbName);
			return true;
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	    return false;
	}


	/**
	 *
	 * @return
	 */
	public Connection getConn() {
		return conn;
	}


	/**
	 *
	 * @param conn
	 */
	public void setConn(Connection conn) {
		this.conn = conn;
	}
	
}
