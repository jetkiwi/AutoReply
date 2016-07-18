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

public class UpdateNameCommand extends BaseCommand{
    private final Pattern updatePattern = Pattern.compile("^@ja731j update_name .{1,20}$");
    private final Pattern removePattern = Pattern.compile("@ja731j update_name ");

    @Override
    public boolean verifySyntax(Status status) {
        boolean result = false;
        ArrayList<UserMentionEntity> mentionList = new ArrayList<UserMentionEntity>(Arrays.asList(status.getUserMentionEntities()));
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
            
            twitter.updateProfile(result, user.getURL(), user.getLocation(), user.getDescription());
            return new StatusUpdate(status.getUser().getName() + "(" +"@" + status.getUser().getScreenName()+ ")" + "様のご意思により" + result + "に改名しました。").inReplyToStatusId(status.getId());

        } catch (TwitterException ex) {
            Logger.getLogger(MyStreamAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return createReply(status, "エラーが発生しました。");
    }
    
}
