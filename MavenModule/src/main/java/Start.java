import database.DatabaseHandler;
import sentimentanalyser.*; 
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;

/**
 * This class contains the main method which will calculate the sentiment score for each article and write it back
 * to the database.
 *
 * Make sure you have the crawler_config.ini file in place.
 */
public class Start {

	private  DatabaseHandler handler;

	public Start(){
		handler = setUpDatabaseHandler();
	}

	public static void main(String[] args) throws SQLException {


		Start s = new Start();
		Boolean state = s.processArticles();

		if(state = true){
			System.out.println("Finished!!!!");
		}else{
			System.out.println("Something went wrong!!!!");
		}
	}



	/*
	* Method calculates sentiment for not processed articles
	* */
	public Boolean processArticles(){
		int counter = 0;
		Analyser an = new Analyser();
		List<BOWTExt> articles = handler.listOfNotProcessedArticles();
		System.out.println("Articles to be processed: "+ articles.size());

		if(handler == null){
			System.out.println("Could not setup database handlers");
			return false;
		}

		while(counter < articles.size()){
			
			System.out.println(counter + " "+ articles.get(counter).getBow());
			int sentiment = an.findSentimentAverage(articles.get(counter).getBow());

			System.out.println("Sentiment: "+ sentiment);
			if(sentiment != -500){
				//handler.writeSentiment(articles.get(counter).getUri(), sentiment);
				handler.updateSentiment(articles.get(counter).getUri(), sentiment);
			}
			counter++;
		}
		return true;
	}



	/*
	* Set upd the Database Connection
	* */
	public DatabaseHandler setUpDatabaseHandler(){
		File f = new File("crawler_config.ini");
		Ini ini;
		try {
			ini = new Ini(f);
			String user = ini.get("DATABASE", "User");
			String password = ini.get("DATABASE", "Password");
			String database = ini.get("DATABASE", "DB");
			String host = ini.get("DATABASE", "Host");
			
			f = null;
			ini = null;
			
			DatabaseHandler handler;
			try {
				handler = new DatabaseHandler(host, database, user, password);
				return handler;
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
			return null;
		} catch (InvalidFileFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
		
		
	}

}
