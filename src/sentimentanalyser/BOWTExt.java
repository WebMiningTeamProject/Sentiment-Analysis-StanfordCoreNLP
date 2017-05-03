package sentimentanalyser;

/*
 * This class contains the BOW and Uri for each news article
 */
public class BOWTExt {
	
	private String bow;
	private String uri;
	
	
	public BOWTExt(String uri, String bow){
		this.setBow(bow);
		this.setUri(uri);
	}


	public String getBow() {
		return bow;
	}


	public void setBow(String bow) {
		this.bow = bow;
	}


	public String getUri() {
		return uri;
	}


	public void setUri(String uri) {
		this.uri = uri;
	}

}
