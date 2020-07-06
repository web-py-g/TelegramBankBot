import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MessageParserTest {

    @Test
    public void getDateTest1(){
        String expected = "12.01.2020";
        String actual = MessageParser.getDate(expected);
        assertEquals(expected, actual);
        actual = MessageParser.getDate("12.01.2020");
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void getDateTest2(){
        String expected = "22.06.2020";
        String actual = MessageParser.getDate("27.02.2000");
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void searchWordTest1() throws IOException {
        MessageParser.load();
        boolean actual = MessageParser.isSearch("курс");
        Assertions.assertTrue(actual);
    }

    @Test
    public void searchWordTest2() throws IOException {
        MessageParser.load();
        boolean actual = MessageParser.isSearch("валюта");
        Assertions.assertTrue(actual);
    }

    @Test
    public void cbRateTest1() throws IOException {
        MessageParser.load();
        boolean actual = MessageParser.isCbRates("цб");
        Assertions.assertTrue(actual);
    }

    @Test
    public void cbRateTest2() throws IOException {
        MessageParser.load();
        boolean actual = MessageParser.isCbRates("банк россии");
        Assertions.assertTrue(actual);
    }

    @Test
    public void getCurrencyTest1() throws IOException {
        MessageParser.load();
        String expected = "USD";
        String actual = MessageParser.getCurrency("Доллар");
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void getCurrencyTest2() throws IOException {
        MessageParser.load();
        String expected = "JPY";
        String actual = MessageParser.getCurrency("йены");
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void getCurrencyTest3() throws IOException {
        MessageParser.load();
        String expected = "";
        String actual = MessageParser.getCurrency("рубль");
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void getRegionTest1() throws IOException {
        MessageParser.load();
        String expected = "sankt-peterburg";
        String actual = MessageParser.getRegion("Питер");
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void getRegionTest2() throws IOException {
        MessageParser.load();
        String expected = "moskva";
        String actual = MessageParser.getRegion("мск");
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void getRegionTest3() throws IOException {
        MessageParser.load();
        String expected = "";
        String actual = MessageParser.getRegion("Курск");
        Assertions.assertEquals(expected, actual);
    }
}