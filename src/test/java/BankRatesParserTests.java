import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.regex.Pattern;

public class BankRatesParserTests {

    @Test
    public void parseManyTest() throws IOException {


        String expected = "Курс фунта в Санкт-Петербурге:\n\n" +
                "Ак Барс Банк\nПокупка : [0-9]{1,3}.{0,1}[0-9]{0,2}\nПродажа : [0-9]{1,3}.{0,1}[0-9]{0,2}\n\n" +
                "Альфа-Банк\nПокупка : [0-9]{1,3}.{0,1}[0-9]{0,2}\nПродажа : [0-9]{1,3}.{0,1}[0-9]{0,2}\n\n" +
                "ББР Банк\nПокупка : [0-9]{1,3}.{0,1}[0-9]{0,2}\nПродажа : [0-9]{1,3}.{0,1}[0-9]{0,2}\n\n" +
                "Банк «Санкт-Петербург»\nПокупка : [0-9]{1,3}.{0,1}[0-9]{0,2}\nПродажа : [0-9]{1,3}.{0,1}[0-9]{0,2}\n\n" +
                "Банк ВТБ\nПокупка : [0-9]{1,3}.{0,1}[0-9]{0,2}\nПродажа : [0-9]{1,3}.{0,1}[0-9]{0,2}\n\n";

        Parsing.getCur("фунт");
        String s1 =   Parsing.getCityBank("Питер");
        Document doctest = Jsoup.connect(s1).get();
        String actual = Parsing.getBankAndCur(doctest);


        Assertions.assertTrue(Pattern.matches(expected, actual));
    }

    @Test
    public void parseLessThenFiveTest() throws IOException {

        String expected = "Курс юаня в банках Перми:\n\n" +
                "Банк ВТБ\nПокупка : [0-9]{1,3}.{0,1}[0-9]{0,2}\nПродажа : [0-9]{1,3}.{0,1}[0-9]{0,2}\n\n" +
                "Росбанк\nПокупка : [0-9]{1,3}.{0,1}[0-9]{0,2}\nПродажа : [0-9]{1,3}.{0,1}[0-9]{0,2}\n\n";

        Parsing.getCur("Юань");
        String s =   Parsing.getCityBank("Пермь");
        Document doctest1 = Jsoup.connect(s).get();
        String actual = Parsing.getBankAndCur(doctest1);
        Assertions.assertTrue(Pattern.matches(expected,actual));
    }
}