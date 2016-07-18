package net.ja731j.twitter.autoreply;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class Util {

    private static Twitter twitter = null;

    public static Configuration getConfig() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(System.getenv("TwitterConsumerKey"))
                .setOAuthConsumerSecret(System.getenv("TwitterConsumerSecret"))
                .setOAuthAccessToken(System.getenv("TwitterAccessToken"))
                .setOAuthAccessTokenSecret(System.getenv("TwitterAccessTokenSecret"));
        Configuration conf = cb.build();
        return conf;
    }

    public static Twitter getTwitter() {
        if (twitter == null) {
            twitter = new TwitterFactory(Util.getConfig()).getInstance();
        }
        return twitter;
    }
    
}
