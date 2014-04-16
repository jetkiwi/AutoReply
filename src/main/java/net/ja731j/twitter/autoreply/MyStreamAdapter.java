package net.ja731j.twitter.autoreply;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.ja731j.twitter.autoreply.command.BaseCommand;
import net.ja731j.twitter.autoreply.command.FoodCommand;
import net.ja731j.twitter.autoreply.command.RainCommand;
import net.ja731j.twitter.autoreply.command.SynAckCommand;
import net.ja731j.twitter.autoreply.command.UpdateLocationCommand;
import net.ja731j.twitter.autoreply.command.UpdateName2Command;
import net.ja731j.twitter.autoreply.command.UpdateNameCommand;
import twitter4j.Status;
import twitter4j.UserStreamAdapter;

public class MyStreamAdapter extends UserStreamAdapter {

    ArrayList<BaseCommand> commands = new ArrayList<BaseCommand>();

    MyStreamAdapter(){
        super();
        List<BaseCommand> list =  Arrays.<BaseCommand>asList(
                new SynAckCommand(),
                new UpdateNameCommand(),
                new UpdateName2Command(),
                new UpdateLocationCommand(),
                new RainCommand(),
                new FoodCommand());
        commands.addAll(list);
    }
    
    @Override
    public void onStatus(Status status) {
        for(BaseCommand command:commands){
            if(command.verifySyntax(status)){
                command.execute(status);
                break;
            }
        }
    }

}
