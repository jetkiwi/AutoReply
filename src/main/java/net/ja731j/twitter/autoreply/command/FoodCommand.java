package net.ja731j.twitter.autoreply.command;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import net.ja731j.twitter.autoreply.MyStreamAdapter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import twitter4j.Status;
import twitter4j.StatusUpdate;
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
    public StatusUpdate execute(Status status) {
        try {
            String result = "@" + status.getUser().getScreenName() + " おすすめメニュー情報です。\n\n";

            List<Map.Entry<String, Integer>> items = fetchMenu();
            int total = 0;
            for (Map.Entry<String, Integer> item : items) {
                result = result.concat(String.format("%s(%d円)\n", item.getKey(), item.getValue()));
                total += item.getValue();
            }
            result = result.concat(String.format("合計%d円", total));
            return new StatusUpdate(result).inReplyToStatusId(status.getId());

        } catch (IOException ex) {
            Logger.getLogger(MyStreamAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }

        return createReply(status, "エラーが発生しました。");
    }

    protected List<Map.Entry<String, Integer>> fetchMenu() throws IOException {
        List<Map.Entry<String, Integer>> result = new ArrayList<>();
        Document doc = Jsoup.connect("http://gakushoku.coop/setmenu.php?feeling=C&price=500").get();

        Element list = doc.getElementById("setList");
        //Get name and price for each item
        for (Element item : list.select(":root > li")) {
            //Get name
            Elements e = item.getElementsByClass("menuphoto").first().getElementsByAttribute("alt");
            String name = englishPattern.matcher(e.last().attr("alt")).replaceAll("");
            //Get price
            int price = Integer.parseInt(item.getElementsByClass("tt-prices").first().text().replace("円", ""));
            result.add(new AbstractMap.SimpleEntry<>(name, price));
        }

        return result;
    }

}
