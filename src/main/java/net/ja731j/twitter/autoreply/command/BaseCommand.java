package net.ja731j.twitter.autoreply.command;

import net.ja731j.twitter.autoreply.Util;
import twitter4j.Status;
import twitter4j.Twitter;

public abstract class BaseCommand {
    protected Twitter twitter = Util.getTwitter();
    public abstract boolean verifySyntax(Status status);
    public abstract void execute(Status status);
}
