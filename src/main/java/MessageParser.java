import Exe.groupNoInputInfoException;
import Exe.haveNoTextException;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.telegram.telegrambots.api.objects.Message;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class MessageParser {

    private static LinkedList<String> cbPatterns;
    private static LinkedList<String> searchPatterns;
    private static HashMap<String, String> regions;
    private static HashMap<String, String> currency;
    private static String region = "moskva";
    private static final Logger logger = Logger.getGlobal();


    public static void load() throws IOException {

        Gson gson = new Gson();
        regions = gson.fromJson(new JsonReader(new FileReader("./src/main/resources/cities.json")), new TypeToken<HashMap<String, String>>() {}.getType());
        currency = gson.fromJson(new JsonReader(new FileReader("./src/main/resources/currency.json")), new TypeToken<HashMap<String, String>>(){}.getType());

        cbPatterns = new LinkedList<>();
        searchPatterns = new LinkedList<>();
        String date = "";

        BufferedReader reader = new BufferedReader(new FileReader(new File("./src/main/resources/searchPattern.txt")));

        String line = reader.readLine();
        while (line != null){
            searchPatterns.add(line);
            line = reader.readLine();
        }
        reader.close();

        reader = new BufferedReader(new FileReader(new File("./src/main/resources/cbPattern.txt")));
        line = reader.readLine();
        while (line != null){
            cbPatterns.add(line);
            line = reader.readLine();
        }
        reader.close();
    }

    public static String ParsMesg(Message message) throws IOException, groupNoInputInfoException, haveNoTextException {
        if (message.hasText()){
            String[] words = message.getText().split("\\s+");
            String currency = "";
            String city = "";
            boolean isSearch = false;
            boolean isCb = false;
            String date = "";

            boolean isLogged = false;

            for (int i = 0; i<words.length;i++){
                String word = words[i];

                if (word.equals("/help")){
                    return "Бот для получения курса валют: Долларов, Евро, Йен, Юань, Фунтов \n\n" +
                            "Для получения информации по нужной вам валюте, напишите боту её название. Если вы хотите узнать курс всех валют по цб рф, то напишите боту цб в сообщении. \n" +
                            "Так же, для получения курса цб рф по определенной дате, напишите цб и дату в формате DD.MM.YYYY\n\n" +
                            "Можно получить курс валют в банках по регионам (если в сообщении не указан регион, оттображается курс последнего использованого города, или же курс в Москве).\n" +
                            "В качестве регионов доступны города с начелением более 500.000 человек на территории РФ";
                }

                if (word.equals("/start")){
                    return "Здравствуй! Я бот для вывода информации по курсу валют, если хотите узнать как я работаю напишите /help.";
                }

                if(city.isEmpty()) city = getRegion(word);
                if(currency.isEmpty()) currency = getCurrency(word);
                if(!isSearch) isSearch = isSearch(word);
                if(!isCb) isCb = isCbRates(word);
                if(date.isEmpty()) date = getDate(word);

                if (isCb){
                    if(!isLogged){
                        logger.info("Request: " + message.getText());
                        isLogged = true;
                    }

                    if(!date.isEmpty()) {
                        Parsing.result ="";
                        return Parsing.getCBCur(date);
                    }
                    if(i == words.length - 1)
                        return Parsing.getCBCur(getTime(message.getDate()));
                }
                else {
                    if (isSearch || !currency.isEmpty()) {
                        if (!isLogged) {
                            logger.info("Request: " + message.getText());
                            isLogged = true;
                        }
                        Parsing.result = "";
                        if(!city.isEmpty()) region = city;
                        if(!currency.isEmpty() && !city.isEmpty()){
                            String onestep = Parsing.getCur(currency);
                            String twostep = Parsing.getCityBank(region);
                            Document finish = Jsoup.connect(twostep).get();
                            Parsing.url = "https://ru.myfin.by/currency/";
                            return Parsing.getBankAndCur(finish);
                        }

                        if(i == words.length - 1){
                            if(city.isEmpty() && !currency.isEmpty()){
                                String firststep = Parsing.getCur(currency);
                                String secondstep = Parsing.getCityBank(region);
                                Document finishCur = Jsoup.connect(secondstep).get();
                                Parsing.url = "https://ru.myfin.by/currency/";
                                return Parsing.getBankAndCur(finishCur);
                            }
                            String last = Parsing.getCityBank(region);
                            Document lastdoc = Jsoup.connect(last).get();
                            Parsing.url = "https://ru.myfin.by/currency/";
                            return Parsing.getBankAndCur(lastdoc);

                        }
                    }
                }
            }
        }else if(!message.isGroupMessage())throw new haveNoTextException();
        if(message.isGroupMessage()) throw new groupNoInputInfoException();
        return "Не достаточно информации для вывода информации";
    }

    public static String getRegion(String word){
        for (Map.Entry<String, String> item : regions.entrySet()){
            if (Pattern.matches(item.getKey(), word.toLowerCase())) return item.getValue();
        }
        return "";
    }

    public static String getCurrency(String word){
        for (Map.Entry<String, String> item : currency.entrySet()){
            if (Pattern.matches(item.getKey(), word.toLowerCase())) return item.getValue();
        }
        return "";
    }

    public static String getDate(String word){
        String pattern = "[0-9]{2}[\\/.][0-9]{2}[\\/.][0-9]{4}";
        if (Pattern.matches(pattern, word))
            return word.replaceAll("\\/","\\.");
        return "";
    }

    public static boolean isSearch(String word){
        for (String pattern: searchPatterns){
            if(Pattern.matches(pattern, word.toLowerCase()))
                return true;
        }
        return false;
    }

    public static boolean isCbRates(String word){
        for (String pattern: cbPatterns){
            if(Pattern.matches(pattern, word.toLowerCase()))
                return true;
        }
        return false;
    }

    private static String getTime(long unix){
        Date date = new Date(unix*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        return sdf.format(date);
    }
}