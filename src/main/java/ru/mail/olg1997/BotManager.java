package ru.mail.olg1997;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BotManager {
    private Bot bot;
    private String settingFilename;
    int followingCount;

    private String login;
    private String password;
    private String startProfile;
    private int followCountInHour;
    private int likesCount;
    private double dayBeforeUnfollow;
    private int unfollowCount;
    private double daysOfUnfollowingWhenLimit;
    private int unfollowingCountWhenLimit;
    private int maxFollowing;
    private String message;

    private ArrayDeque<String> myFollowing; // на кого подписались
    private ArrayDeque<String> queueForProfiles; // на кого еще нужно подписаться

    BotManager(String filename) {
        this.settingFilename = filename;
        myFollowing = new ArrayDeque<String>();
        queueForProfiles = new ArrayDeque<String>();
    }

    private boolean parseFileByAnh(String filename) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line;
            List<String> list = new ArrayList<String>();
            while ((line = br.readLine()) != null) {
                String temp = line.substring(line.indexOf(":")+2);
                list.add(temp);
            }
            Pattern pattern = Pattern.compile("\\b[\\d]+\\b");
            Matcher matcher = pattern.matcher(list.get(9));
            list.remove(9);
            while (matcher.find()) {
                System.out.println(matcher.group());
                list.add(matcher.group());
            }
            login = list.get(0);
            password = list.get(1);
            startProfile = list.get(2);
            message = list.get(3);
            likesCount = Integer.parseInt(list.get(5));
            followCountInHour = Integer.parseInt(list.get(4));
            dayBeforeUnfollow = Integer.parseInt(list.get(6));
            unfollowCount = Integer.parseInt(list.get(7));
            maxFollowing = Integer.parseInt(list.get(8));
            daysOfUnfollowingWhenLimit = Integer.parseInt(list.get(9)); // сколько дней отписка
            unfollowingCountWhenLimit = Integer.parseInt(list.get(10)); // среднее число отписок в час
        }
        catch (Exception exc) {
            if (exc instanceof FileNotFoundException || exc instanceof IOException || exc instanceof NumberFormatException ) {
                System.out.println("Read File ERROR: " + exc.getMessage());
                return false;
            }
        }
        return true;

    }

    private void sleep(long millis) {
        long var = millis/5;
        long time = ThreadLocalRandom.current().nextLong(millis-var, millis+var);
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            System.out.println("Sleep ERROR: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Sleep ERROR: " + e.getMessage());
        }
    }

    public void start() {
        if (!parseFileByAnh(settingFilename))
            return;

        bot = new Bot(login, password, message);

        try {
            bot.login();
            sleep(2000);
            bot.setUser(login);
            sleep(2000);
            List<String> following = bot.getFollowing();
            myFollowing.addAll(following);
            sleep(2000);
            followingCount = bot.getFollowingCount();
            sleep(2000);
        } catch (Exception ex) {
            System.out.println("Login ERROR: " + ex.getMessage());
            return;
        }

        long timeForAll = 60*60*1000 / followCountInHour;
        long timeForOneAction = (timeForAll - bot.TIMEFORMESSAGE - likesCount*bot.TIMEFORLIKE - bot.TIMEFORFOLLOW) / (likesCount+3);

        queueForProfiles.push(startProfile);

        String currentProfile;

        long spendTime = 0;
        // int failFollowCount = 0;
        int followInCurrentHour = 0;

        while (!queueForProfiles.isEmpty()) {
            long begin = System.currentTimeMillis();

            currentProfile = queueForProfiles.peekFirst();

            long checkTimeBegin = System.currentTimeMillis();
            long checkTimeEnd = System.currentTimeMillis();
            boolean needFollow = !currentProfile.equals(login) &&
                    !myFollowing.contains(currentProfile) &&
                    (followInCurrentHour < (int) ((double) this.followCountInHour * 1.2));

            if (currentProfile.equals(login) || myFollowing.contains(currentProfile)) {
                queueForProfiles.pop();
            }

            if (needFollow) {
                try {
                    queueForProfiles.pop();
                    bot.setUser(currentProfile);
                    myFollowing.push(currentProfile);
                    followingCount++;
                    followInCurrentHour++;
                    if (bot.follow()) {
                        // failFollowCount = 0;
                        if (!bot.isUserPrivate()) {

                            if (queueForProfiles.size() < 20000)
                                queueForProfiles.addAll(bot.getFollowers());

                            checkTimeEnd = System.currentTimeMillis();

                            sleep(timeForOneAction - (checkTimeEnd - checkTimeBegin));

                            checkTimeBegin = System.currentTimeMillis();
                            bot.requestUserMedia();
                            checkTimeEnd = System.currentTimeMillis();

                            sleep(timeForOneAction - (checkTimeEnd - checkTimeBegin));

                            for (int i = 0; i < likesCount; i++) {
                                checkTimeBegin = System.currentTimeMillis();
                                bot.like(i);
                                checkTimeEnd = System.currentTimeMillis();
                                sleep(timeForOneAction - (checkTimeEnd - checkTimeBegin));
                            }
                        }

                        else {
                            sleep(timeForOneAction * (likesCount + 2));
                        }

                        checkTimeBegin = System.currentTimeMillis();
                        bot.sendMessage();
                        checkTimeEnd = System.currentTimeMillis();
                        sleep(timeForOneAction - (checkTimeEnd - checkTimeBegin));
                    }
                    else {
                        sleep(10*60*1000);
                    //// failFollowCount++;
                    // if (failFollowCount == 10) {
                    // spendTime = 0;
                    // unFollow(1, unfollowCount);
                    // }
                     }

                } catch (Exception ex) {
                    System.out.println("Work with user ERROR: " + ex.getMessage());
                }
            }
            else {
                sleep(1000);
            }


            long end = System.currentTimeMillis();
            spendTime += end - begin;
            System.out.println("Spend sec to 1 user: " + (end-begin)/1000);

            if (spendTime >= 60*60*1000) {
                followInCurrentHour = 0;
            }

            if (spendTime >= dayBeforeUnfollow *24*60*60*1000) {
                spendTime = 0;
                unFollow(1, unfollowCount);
            }

            if (followingCount == maxFollowing) {
                unFollow(daysOfUnfollowingWhenLimit, unfollowingCountWhenLimit);
            }
        }
    }

    public void unFollow(double days, int unfollowInHour) {
        long timeForOneUnFollow = (60*60*1000 - bot.TIMEFORFOLLOW*unfollowInHour) / unfollowInHour / 3;
        double limit = days*24*60*60*1000;
        long spendTime = 0;
        while (spendTime < limit) {
            long begin = System.currentTimeMillis();
            sleep(timeForOneUnFollow);
            if (!myFollowing.isEmpty()) {
                String unFollowProfile = myFollowing.pollLast();

                long checkTimeBegin = System.currentTimeMillis();
                bot.setUser(unFollowProfile);
                long checkTimeEnd = System.currentTimeMillis();
                sleep(timeForOneUnFollow - (checkTimeEnd - checkTimeBegin));

                checkTimeBegin = System.currentTimeMillis();
                bot.unFollow();
                checkTimeEnd = System.currentTimeMillis();
                sleep(timeForOneUnFollow - (checkTimeEnd - checkTimeBegin));
            }
            else {
                bot.setUser(login);
                sleep(2000);
                List<String> following = bot.getFollowing();
                if (following == null)
                    return;
                for (String foll : following)
                    myFollowing.push(foll);
                if (myFollowing.isEmpty())
                    return;
            }
            long end = System.currentTimeMillis();
            spendTime += end - begin;
        }
    }
}