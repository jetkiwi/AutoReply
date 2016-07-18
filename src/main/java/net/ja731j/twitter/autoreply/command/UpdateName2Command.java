package net.ja731j.twitter.autoreply.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import net.ja731j.twitter.autoreply.MyStreamAdapter;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.UserMentionEntity;

public class UpdateName2Command extends BaseCommand {

    private Pattern updatePattern;
    private Pattern removePattern;

    public UpdateName2Command(MyStreamAdapter manager) {
        super(manager);
    }

    @Override
    public void updateSyntax() {
        updatePattern = Pattern.compile(String.format("^.{1,20}\\(@%s\\)$",manager.getScreenName()));
        removePattern = Pattern.compile(String.format("\\(@%s\\)",manager.getScreenName()));
    }

    @Override
    public boolean verifySyntax(Status status) {
        boolean result = false;
        ArrayList<UserMentionEntity> mentionList = new ArrayList<>(Arrays.asList(status.getUserMentionEntities()));
        for (UserMentionEntity e : mentionList) {
            if (e.getId() == manager.getUserId()) {
                result = updatePattern.matcher(status.getText()).matches();
            }
        }
        return result;
    }

    @Override
    public StatusUpdate execute(Status status) {
        try {
            String text = status.getText();
            String result = removePattern.matcher(text).replaceFirst("");
            User user = twitter.verifyCredentials();

            twitter.updateProfile(result, user.getURL(), user.getLocation(), user.getDescription());
            StatusUpdate update = new StatusUpdate(status.getUser().getName() + "(" + "@" + status.getUser().getScreenName() + ")" + "様のご意思により" + result + "に改名しました。").inReplyToStatusId(status.getId());
            return update;

        } catch (TwitterException ex) {
            Logger.getLogger(MyStreamAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return createReply(status, "エラーが発生しました。");
    }

}
