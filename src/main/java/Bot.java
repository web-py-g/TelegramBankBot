import Exe.groupNoInputInfoException;
import Exe.haveNoTextException;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;

public class Bot extends TelegramLongPollingBot {

    private static final Logger logger = Logger.getGlobal();
    private static final String attentionURL =
            "https://www.meme-arsenal.com/create/meme/465655";


    public static void main(String[] args) {

        try {
            MessageParser.load();
        } catch (IOException e) {
            logger.warning("Lose data files");
        }

        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();

        try {
            telegramBotsApi.registerBot(new Bot());
        } catch (TelegramApiRequestException e) {
            logger.warning("Error bot launching");
        }


    }

    private void sendMsg(Message message, String text){
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText(text);

        try{
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            logger.warning("Error sending message");
        }
    }

    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (message != null) {
            try {
                sendMsg(message, MessageParser.ParsMesg(message));
            } catch (haveNoTextException e) {
                try {
                    sendAttention(message);
                } catch (TelegramApiException telegramApiException) {
                    logger.warning("Error sending attention message");
                }
            }catch (groupNoInputInfoException ignored){} catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getBotUsername() {
        return "BankBotForG4";
    }

    public String getBotToken() {
        return "1061702752:AAFGfONLFNo9FeuNnkS6V8GTsuIqXmaMQwU";
    }

    private void sendAttention(Message message) throws TelegramApiException {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setPhoto(attentionURL);
        sendPhoto.setCaption("Дай текст!!!");
        sendPhoto.setChatId(message.getChatId().toString());
        sendPhoto(sendPhoto);
    }
}
