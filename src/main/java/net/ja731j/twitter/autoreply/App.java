package net.ja731j.twitter.autoreply;

import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

public class App {

    public static void main(String[] args) {

        TwitterStreamFactory twitterStreamFactory = new TwitterStreamFactory(Util.getConfig());
        TwitterStream twitterStream = twitterStreamFactory.getInstance();

        {
            twitterStream.addListener(new MyStreamAdapter());
            twitterStream.user();
        }
    }
}
