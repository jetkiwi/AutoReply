package net.ja731j.twitter.autoreply.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.UserMentionEntity;

public class SynAckCommand extends BaseCommand {

    private final Pattern synPattern = Pattern.compile("^@ja731j ␖*$");
    private final Pattern synSynbolPattern = Pattern.compile("␖");

    @Override
    public boolean verifySyntax(Status status) {
        boolean result = false;
        ArrayList<UserMentionEntity> mentionList = new ArrayList<>(Arrays.asList(status.getUserMentionEntities()));
        for (UserMentionEntity e : mentionList) {
            if (e.getScreenName().equalsIgnoreCase("ja731j")) {
                result = synPattern.matcher(status.getText()).matches();
            }
        }
        return result;
    }

    @Override
    public StatusUpdate execute(Status status) {
        Matcher m = synSynbolPattern.matcher(status.getText());
        String ack = new String();
        while (m.find()) {
            ack = ack + "␆";
        }

        StatusUpdate update = new StatusUpdate("@" + status.getUser().getScreenName() + " " + ack).inReplyToStatusId(status.getId());
        return update;
    }

}
