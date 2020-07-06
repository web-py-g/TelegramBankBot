import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.print.Doc;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ClientInfoStatus;
import java.util.*;


public class Parsing {

    static String url = "https://ru.myfin.by/currency/";
    Document doc = Jsoup.connect(url).get();

    static  List<String> val = new ArrayList<String >(Arrays.asList("usd","eur","jpy","cny","gbp"));

    static byte counter = 0;
    static String result = "";

    public Parsing() throws IOException {
    }

    public static String getCityBank(String city) throws FileNotFoundException {
        String nameCityURL = "";
        Gson gson = new Gson();
        HashMap<String,String> cities = gson.fromJson(new JsonReader(new FileReader("./src/main/resources/cities.json")), new TypeToken<HashMap<String,String>>(){}.getType());

        for (Map.Entry<String,String> el : cities.entrySet()){
                if (city.toLowerCase().matches(el.getValue())){
                    nameCityURL += el.getValue() + "/";
                    break;
                }
        }
        if (nameCityURL == ""){
            return "Город не найден";
        }
        url+=nameCityURL;
        return url;
    }


    public static String getCBCur (String date) throws IOException {
        Document cb = Jsoup.connect("http://www.cbr.ru/scripts/XML_daily.asp?date_req=" + date).get();

        Elements elements = cb.select("Valute");

        for (Element el : elements){
            String currency = el.select("CharCode").text().toLowerCase();
            if (val.contains(currency)){
                String CharCode = el.select("CharCode").text();
                double Nominal = Double.parseDouble(el.select("Nominal").text().replace(',','.'));
                double Value = Double.parseDouble(el.select("Value").text().replace(',','.'));

                result += "1.0 " + CharCode+ " " + "= " + Value/Nominal + "\n";
            }
        }
        if (result.isEmpty()) return "Не верно указана дата";


        return "Курс ЦБ РФ " + date + "\n" + result;
    }


    public static String getCur(String cur) throws IOException {

        String curURL = "";
        Gson gson = new Gson();
        HashMap<String,String> currency = gson.fromJson(new JsonReader(new FileReader("./src/main/resources/currency.json")), new TypeToken<HashMap<String,String>>(){}.getType());

        for (Map.Entry<String,String> el : currency.entrySet()){
            if (cur.toLowerCase().matches(el.getValue())){
                curURL += el.getValue() + "/";
                break;
            }
        }

        url+=curURL;
        return url;
    }


    public static String getBankAndCur(Document doc){

        Elements elements = doc.select("#g_bank_rates > table > tbody > tr");

        for (Element el : elements){
            if (counter == 5){
                counter = 0;
                break;
            }

            if (el.hasAttr("data-key")){
                counter++;
                String BankName = el.select("td.bank_name > a").text();
                double BuyCur = Double.parseDouble(el.select("td:nth-child(2)").text().replace(',','.'));
                double SellCur = Double.parseDouble(el.select("td:nth-child(3)").text().replace(',','.'));

                result += BankName + "\n" + "Покупка:" + BuyCur + "\n" + "Продажа:" + SellCur + "\n\n";
            }
        }

        if (result.equals("")){
            return "В данном городе нет банков торгующей данной валютой";
        }

        Elements header = doc.select("body > div.container.page_cont > div.row >" +
                " div.col-md-9.main-container.pos-r.ovf-hidden > div:nth-child(1) >" +
                " div.content_i-head.content_i-head--datepicker-fix > div.wrapper-flex > div.wrapper-flex__title > h1");
        result = header.text() + ":\n\n"  + result;

        return result;
    }
}