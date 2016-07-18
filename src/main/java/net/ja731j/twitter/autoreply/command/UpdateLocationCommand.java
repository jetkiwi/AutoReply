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

public class UpdateLocationCommand extends BaseCommand {

    private final Pattern updatePattern = Pattern.compile("^@ja731j update_location .{1,30}$");
    private final Pattern removePattern = Pattern.compile("@ja731j update_location ");

    @Override
    public boolean verifySyntax(Status status) {
        boolean result = false;
        ArrayList<UserMentionEntity> mentionList = new ArrayList<>(Arrays.asList(status.getUserMentionEntities()));
        for (UserMentionEntity e : mentionList) {
            if (e.getScreenName().equalsIgnoreCase("ja731j")) {
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
            
            twitter.updateProfile(user.getName(), user.getURL(), result, user.getDescription());
            StatusUpdate update = new StatusUpdate(status.getUser().getName() + "(" + "@" + status.getUser().getScreenName() + ")" + "様のご意思により" + result + "に移動しました。").inReplyToStatusId(status.getId());
            return update;

        } catch (TwitterException ex) {
            Logger.getLogger(MyStreamAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return createReply(status, "エラーが発生しました。");
    }

}
