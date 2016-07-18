package net.ja731j.twitter.autoreply.command;

import net.ja731j.twitter.autoreply.Util;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;

public abstract class BaseCommand {
    protected Twitter twitter = Util.getTwitter();
    public abstract boolean verifySyntax(Status status);
    public abstract StatusUpdate execute(Status status);
    
    protected StatusUpdate createReply(Status status, String text) {
        return new StatusUpdate("@" + status.getUser().getScreenName() + " " + text).inReplyToStatusId(status.getId());
    }
}
