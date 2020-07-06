import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class CBTests {

    @Test
    public void testParse() throws IOException {
        String expected = "Курс ЦБ РФ 27.04.2001\n" +
                "1.0 GBP = 41.55\n" +
                "1.0 USD = 28.86\n" +
                "1.0 EUR = 25.88\n" +
                "1.0 JPY = 0.2357\n";
        String actual = Parsing.getCBCur("27.04.2001");
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void loseParseTest() throws IOException {
        String expected = "Курс ЦБ РФ на 27.04.2001\n" +
                "GBP : 86.3619\n" +
                "USD : 69.9513\n" +
                "EUR : 78.6812\n" +
                "CNY : 19.88278\n" +
                "JPY : 0.23123\n";
        String actual = Parsing.getCBCur ("27.04.2001");
        Assertions.assertNotEquals(expected, actual);
    }

    @Test
    public void brokenDate() throws IOException {
        String expected = "Не верно указана дата";
        String actual = Parsing.getCBCur ("11.02.2022");
        Assertions.assertEquals(expected, actual);
    }
}