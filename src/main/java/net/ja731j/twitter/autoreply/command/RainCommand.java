package net.ja731j.twitter.autoreply.command;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import net.ja731j.twitter.autoreply.MyStreamAdapter;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.TwitterException;
import twitter4j.UserMentionEntity;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;

public class RainCommand extends BaseCommand {

    private final String appId = System.getenv("YahooAppId");

    private final Pattern updatePattern = Pattern.compile("^@ja731j rain .{1,30}$");
    private final Pattern removePattern = Pattern.compile("@ja731j rain ");

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
    public void execute(Status status) {
        try {
            String text = status.getText();
            String loc = removePattern.matcher(text).replaceFirst("");
            String result = "@" + status.getUser().getScreenName() + " ";

            Map<String, String> target = getCoord(loc);
            if (target.isEmpty()) {
                result = result.concat("場所を見つけられませんでした。");
            } else {
                String targetName = target.get("name");
                String targetCoord = target.get("coord");

                result = result.concat(targetName + "における降雨アドバイス\n").concat(getAdviceText(decide(getRain(targetCoord))));
            }

            StatusUpdate update = new StatusUpdate(result).inReplyToStatusId(status.getId());
            twitter.updateStatus(update);

        } catch (TwitterException ex) {
            Logger.getLogger(MyStreamAdapter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
            Logger.getLogger(RainCommand.class.getName()).log(Level.SEVERE, null, ex);
        } catch (HttpResponseException ex) {
            System.err.println("oops");
            Logger.getLogger(RainCommand.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RainCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Map<String, String> getCoord(String str) throws URISyntaxException, IOException, HttpResponseException {
        URIBuilder geoApi = new URIBuilder("http://geo.search.olp.yahooapis.jp/OpenLocalPlatform/V1/geoCoder");
        geoApi.addParameter("appid", appId);
        geoApi.addParameter("query", str);
        geoApi.addParameter("output", "json");
        HttpGet httpGet = new HttpGet(geoApi.build());

        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(httpGet);
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new HttpResponseException(response.getStatusLine().getStatusCode(), "Unacceptable status code");
        }

        Map<String, String> result = new HashMap<>();

        HttpEntity entity = response.getEntity();
        try (InputStream is = entity.getContent();
                JsonReader rdr = Json.createReader(is)) {
            JsonObject obj = rdr.readObject();
            JsonArray array = obj.getJsonArray("Feature");

            if (array == null) {
                return result;
            }

            String targetName = null;
            String targetCoord = null;
            for (JsonValue res : array) {
                if (res.getValueType() == ValueType.OBJECT) {
                    JsonObject geo = ((JsonObject) res).getJsonObject("Geometry");
                    JsonString type = geo.getJsonString("Type");
                    if (type.getString().equals("point")) {
                        targetName = ((JsonObject) res).getString("Name");
                        targetCoord = geo.getString("Coordinates");
                        break;
                    }
                }
            }
            result.put("name", targetName);
            result.put("coord", targetCoord);

        }
        return result;
    }

    public List<Double> getRain(String str) throws URISyntaxException, IOException {
        URIBuilder rainApi = new URIBuilder("http://weather.olp.yahooapis.jp/v1/place");
        rainApi.addParameter("appid", appId);
        rainApi.addParameter("coordinates", str);
        rainApi.addParameter("output", "json");
        HttpGet httpGet = new HttpGet(rainApi.build());

        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(httpGet);
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new HttpResponseException(response.getStatusLine().getStatusCode(), "Unacceptable status code");
        }

        List<Double> result = new ArrayList<>();

        HttpEntity entity = response.getEntity();
        try (InputStream is = entity.getContent();
                JsonReader rdr = Json.createReader(is)) {
            JsonObject obj = rdr.readObject();
            JsonObject feature = obj.getJsonArray("Feature").getJsonObject(0);
            JsonObject prop = feature.getJsonObject("Property");
            JsonArray weatherList = prop.getJsonObject("WeatherList").getJsonArray("Weather");

            for (JsonValue res : weatherList) {
                if (res.getValueType() == ValueType.OBJECT) {
                    JsonObject weather = (JsonObject) res;
                    Double rain = weather.getJsonNumber("Rainfall").doubleValue();
                    result.add(rain);
                }
            }
        }
        return result;
    }

    private enum Decision {

        NOT_RAINING, NO_DIFFERENCE, GO, WAIT
    }

    private Decision decide(List<Double> rains) {
        double sum = 0;
        for (Double rain : rains) {
            sum += rain;
        }
        double average = sum / rains.size();

        if (average == 0) {
            return Decision.NOT_RAINING;
        } else {
            double relative = rains.get(0) / average;
            if (relative < 0.8) {
                return Decision.GO;
            } else if (relative > 1.2) {
                return Decision.WAIT;
            } else {
                return Decision.NO_DIFFERENCE;
            }
        }
    }

    private String getAdviceText(Decision d) {
        switch (d) {
            case NOT_RAINING:
                return "降っていないのでいつ行っても大丈夫です。";
            case NO_DIFFERENCE:
                return "いつ行っても同じです。";
            case GO:
                return "今行ったほうがいいです。";
            case WAIT:
                return "少し待ってから行ったほうがいいです。";
            default:
                return "ごめんなさい。バグりました。";
        }
    }
}
