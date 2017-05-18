package evaluation;

/**
 * Created by sven on 16.05.17.
 */
public class SentimentEvaluation {


    //SET sentimentAsNumber = IF(sentiment like 'pos',1, IF(sentiment like 'neg',0, sentimentAsNumber)) Where source_uri is not null;
    //SET pos_neg_sentiment = IF(sentiment <2 ,0, IF( sentiment >2,1, pos_neg_sentiment)) Where source_uri is not null;
    //Select scn.source_uri, pos_neg_sentiment, sentimentAsNumber from SentimentCoreNlp as scn Join NewsArticlesNaiveBayes_SPSentiment as sb ON scn.source_uri = sb.source_uri Where scn.sentiment != 2


    public static void main(String args[]){


    }
}
