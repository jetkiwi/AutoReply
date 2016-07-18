package net.ja731j.twitter.autoreply;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.ja731j.twitter.autoreply.command.BaseCommand;
import net.ja731j.twitter.autoreply.command.FoodCommand;
import net.ja731j.twitter.autoreply.command.RainCommand;
import net.ja731j.twitter.autoreply.command.SynAckCommand;
import net.ja731j.twitter.autoreply.command.UpdateLocationCommand;
import net.ja731j.twitter.autoreply.command.UpdateName2Command;
import net.ja731j.twitter.autoreply.command.UpdateNameCommand;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.UserStreamAdapter;

public class MyStreamAdapter extends UserStreamAdapter {

    private String screenName;
    private long userId;
    ArrayList<BaseCommand> commands = new ArrayList<>();

    MyStreamAdapter() throws TwitterException {
        super();
        User u = Util.getTwitter().verifyCredentials();
        userId = u.getId();
        screenName = u.getScreenName();
        List<BaseCommand> list = Arrays.<BaseCommand>asList(
                new SynAckCommand(this),
                new UpdateNameCommand(this),
                new UpdateName2Command(this),
                new UpdateLocationCommand(this),
                new RainCommand(this),
                new FoodCommand(this));
        commands.addAll(list);
        commands.stream().forEach((command) -> {
            command.updateSyntax();
        });
    }

    @Override
    public void onStatus(Status status) {
        for (BaseCommand command : commands) {
            if (command.verifySyntax(status)) {
                StatusUpdate update = command.execute(status);
                if (update != null) {
                    try {
                        Util.getTwitter().updateStatus(update);
                    } catch (TwitterException ex) {
                        Logger.getLogger(MyStreamAdapter.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                break;
            }
        }
    }

    @Override
    public void onUserProfileUpdate(User user) {
        if (getUserId() == user.getId() && !screenName.equals(user.getScreenName())) {
            screenName = user.getScreenName();
            commands.stream().forEach((command) -> {
                command.updateSyntax();
            });
        }
    }

    /**
     * @return the screenName
     */
    public String getScreenName() {
        return screenName;
    }

    /**
     * @return the userId
     */
    public long getUserId() {
        return userId;
    }

}
