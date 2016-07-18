package net.ja731j.twitter.autoreply.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import net.ja731j.twitter.autoreply.MyStreamAdapter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.TwitterException;
import twitter4j.UserMentionEntity;

public class FoodCommand extends BaseCommand {

    private final Pattern commandPattern = Pattern.compile("^@ja731j coop_food$");
    private final Pattern englishPattern = Pattern.compile("[（(][\\p{Alnum},.' （()）]+[)）]");

    @Override
    public boolean verifySyntax(Status status) {
        boolean result = false;
        ArrayList<UserMentionEntity> mentionList = new ArrayList<>(Arrays.asList(status.getUserMentionEntities()));
        for (UserMentionEntity e : mentionList) {
            if (e.getScreenName().equalsIgnoreCase("ja731j")) {
                result = commandPattern.matcher(status.getText()).matches();
            }
        }
        return result;
    }

    @Override
    public void execute(Status status) {
        try {
            String result = "@" + status.getUser().getScreenName() + " おすすめメニュー情報です。\n\n";

            Document doc = Jsoup.connect("http://gakushoku.coop/setmenu.php?feeling=C&price=500").get();

            Element list = doc.getElementById("setList");
            //Get name and price for each item
            for (Element item : list.getElementsByTag("li")) {
                //Get name
                Elements e = item.getElementsByClass("menuphoto").first().getElementsByAttribute("alt");
                String name = e.first().attr("alt");
                //Get price
                String price = item.getElementsByClass("tt-prices").get(0).text();
                result = result.concat(name + " (" + price + ")\n");
            }
            //Append the total price at the bottom
            result = result.concat(doc.getElementById("set-total-value").getElementsByTag("span").get(0).text().replace("\u00a0", ""));

            StatusUpdate update = new StatusUpdate(result).inReplyToStatusId(status.getId());
            twitter.updateStatus(update);
        } catch (TwitterException ex) {
            Logger.getLogger(MyStreamAdapter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MyStreamAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
