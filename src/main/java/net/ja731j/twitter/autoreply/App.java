package net.ja731j.twitter.autoreply;

import java.util.logging.Level;
import java.util.logging.Logger;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

public class App {

    public static void main(String[] args) {

        TwitterStreamFactory twitterStreamFactory = new TwitterStreamFactory(Util.getConfig());
        TwitterStream twitterStream = twitterStreamFactory.getInstance();

        try {
            twitterStream.addListener(new MyStreamAdapter());
            twitterStream.user();
        } catch (TwitterException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("An error has occured while initializing.");
        }

    }
}
