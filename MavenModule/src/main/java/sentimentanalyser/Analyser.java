package sentimentanalyser;

import java.util.List;
import java.util.Properties;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;


/**
 * This class calculates the sentiment score for each article.
 */
public class Analyser {


	private StanfordCoreNLP pipeline;
	private Properties props;
	
	public Analyser(){
		this.init();
	}
	
	
	private void init(){
		props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, parse, sentiment");
		this.pipeline = new StanfordCoreNLP(props);
		System.out.println("Finished Setup"); 
	}

    /**
     * Finds the sentiment.
     * @param bow The string for which the sentiment is to be found.
     * @return The sentiment score, ranges from XX to YY.
     */
	public int findSentiment(String bow){

		int mainSentiment = 0;
		if(bow == null || bow.length() <= 0){
			System.out.println("Test lenght is zero");
			return -500;
		}
		
		if(bow.length() > 500000){
			System.out.println("Text to big for Core NLP, need to be processed later");
			return -500;
		}

		System.out.println("Start finding Sentiment");
		int longest = 0;
		System.out.println("---");
		Annotation annotation = pipeline.process(bow);
		System.out.println("---");
        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        
        for (CoreMap sentence : sentences) {
			Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
			int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
			String partText = sentence.toString();
			
			if (partText.length() > longest) {
				mainSentiment = sentiment;
				longest = partText.length();
			}
			tree = null;
		}
        
        sentences = null;
        annotation = null;
       
        System.out.println("Finished processing sentiment analysis");
		return mainSentiment;
	}
	
	public void clear(){
		pipeline = null;
		props = null;
	}
}
