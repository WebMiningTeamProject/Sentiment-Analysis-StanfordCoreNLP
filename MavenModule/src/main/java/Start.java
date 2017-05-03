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

	public static void main(String[] args) throws SQLException {
		
		
		 DatabaseHandler handler = setUpDatabaseHandler();
		 if(handler == null){
			 System.out.println("Could not setup database handlers");
			 return;
		 }
		List<BOWTExt> articles = handler.listOfNotProcessedArticles(100);	
		System.out.println("Articles to be processed: "+ articles.size());	
	
		
		int counter = 0;
		Analyser an = new Analyser();
		
		while(counter < articles.size()){
			
			
			System.out.println(counter + " "+ articles.get(counter).getBow());
			int sentiment = an.findSentiment(articles.get(counter).getBow());
			handler.writeSentiment(articles.get(counter).getUri(), sentiment);
			
			counter++;
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			
			if(counter%10 == 0 && counter != 0){
				
				System.out.println("Total Memory (in bytes): " + Runtime.getRuntime().totalMemory());
				System.out.println("Free Memory (in bytes): " + Runtime.getRuntime().freeMemory());  
				System.out.println("Max Memory (in bytes): " + Runtime.getRuntime().maxMemory());
				
				System.out.println("Run garbage collector");
				System.gc ();
				System.runFinalization ();
				System.out.println("Finished running garbage collector");
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		System.out.println("Finished!!!!");
			
		
		
	}
	
	public static DatabaseHandler setUpDatabaseHandler(){
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
