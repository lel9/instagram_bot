package ru.mail.olg1997;

import ru.mail.olg1997.BotManager;

public class BotTest {
//    public static void main(String[] args) throws IOException {
//
//        ru.mail.olg1997.Bot bot = new ru.mail.olg1997.Bot("goodhuman001", "password1234");
//
//        try {
//            bot.login();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        bot.setUser("fennesmee");
//        bot.follow();
//        bot.requestUserMedia();
//        bot.like(3);
//        bot.unFollow();
//
//        bot.getFollowers();
//    }
//
    public static void main(String[] args) {
        /* в конструктор менеджера передать имя файла настроек */
        BotManager manager = new BotManager("settings.txt");
        manager.start();
    }
}