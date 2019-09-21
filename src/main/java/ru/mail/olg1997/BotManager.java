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
    private int followingCount;
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
    private int maxMessagesInDay;
    private double daysBeforeSleep;
    private int sleepHours;
    private String message;
    private ArrayDeque<String> myFollowing;
    private ArrayDeque<String> queueForProfiles;

    BotManager(String filename) {
        this.maxMessagesInDay = 50;
        this.daysBeforeSleep = 5.0D;
        this.sleepHours = 24;

        this.settingFilename = filename;
        myFollowing = new ArrayDeque<String>();
        queueForProfiles = new ArrayDeque<String>();
    }

    private boolean parseFileByAnh(String filename) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));

            List<String> list = new ArrayList<String>();
            String line;
            while ((line = br.readLine()) != null) {
                String temp = line.substring(line.indexOf(":") + 2);
                list.add(temp);
            }
            Pattern pattern = Pattern.compile("\\b[\\d]+\\b");
            Matcher matcher = pattern.matcher((CharSequence) list.get(10));
            list.remove(10);
            while (matcher.find()) {
                list.add(matcher.group());
            }
            matcher = pattern.matcher((CharSequence) list.get(10));
            list.remove(10);
            while (matcher.find()) {
                list.add(matcher.group());
            }
            this.login = (String) list.get(0);
            this.password = (String) list.get(1);
            this.startProfile = (String) list.get(2);
            this.message = (String) list.get(3);
            this.maxMessagesInDay = Integer.parseInt((String) list.get(4));
            this.likesCount = Integer.parseInt((String) list.get(5));
            this.followCountInHour = Integer.parseInt((String) list.get(6));
            this.dayBeforeUnfollow = Double.parseDouble((String) list.get(7));
            this.unfollowCount = Integer.parseInt((String) list.get(8));
            this.maxFollowing = Integer.parseInt((String) list.get(9));
            this.daysOfUnfollowingWhenLimit = Double.parseDouble((String) list.get(10));
            this.unfollowingCountWhenLimit = Integer.parseInt((String) list.get(11));
            this.daysBeforeSleep = Double.parseDouble((String) list.get(12));
            this.sleepHours = Integer.parseInt((String) list.get(13));
        }
        catch (Exception exc) {
                System.out.println("Read File ERROR: " + exc.getMessage());
                return false;
        }
        return true;

    }

    private void sleep(long millis) {
        if (millis <= 0)
            return;
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
        bot.setUser(startProfile);
        queueForProfiles.addAll(bot.getFollowers());
        long spendTime = 0L;
        long timeBeforeSleep = 0L;
        long oneHour = 0L;
        long oneDay = 0L;

        int followInCurrentHour = 0;
        boolean needMessage = true;

        int messageCount = 0;

        String currentProfile;

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
                    if (this.bot.follow()) {

                        if (!this.bot.isUserPrivate()) {

                            checkTimeEnd = System.currentTimeMillis();

                            sleep(timeForOneAction - (checkTimeEnd - checkTimeBegin));

                            checkTimeBegin = System.currentTimeMillis();
                            this.bot.requestUserMedia();
                            checkTimeEnd = System.currentTimeMillis();

                            sleep(timeForOneAction - (checkTimeEnd - checkTimeBegin));

                            for (int i = 0; i < this.likesCount; i++) {
                                checkTimeBegin = System.currentTimeMillis();
                                this.bot.like(i);
                                checkTimeEnd = System.currentTimeMillis();
                                sleep(timeForOneAction - (checkTimeEnd - checkTimeBegin));
                            }

                        } else {

                            sleep(timeForOneAction * (this.likesCount + 2));
                        }

                        checkTimeBegin = System.currentTimeMillis();
                        if (needMessage) {
                            this.bot.sendMessage();
                            messageCount++;
                        }
                        checkTimeEnd = System.currentTimeMillis();
                        sleep(timeForOneAction - (checkTimeEnd - checkTimeBegin));
                    }
                    else {
                        sleep(10*60*1000);
                    }

                }
                catch (Exception ex) {
                    System.out.println("Work with user ERROR: " + ex.getMessage());
                }
            } else {

                sleep(1000L);
            }

            if (messageCount >= this.maxMessagesInDay) {
                needMessage = false;
            }
            long end = System.currentTimeMillis();

            oneHour += end - begin;
            timeBeforeSleep += end - begin;
            System.out.println("Spend sec to 1 user: " + ((end - begin) / 1000L));

            if (oneHour >= 3600000L) {
                followInCurrentHour = 0;
                oneHour = 0L;
            }

            if (timeBeforeSleep >= this.daysBeforeSleep * 24.0D * 60.0D * 60.0D * 1000.0D) {
                timeBeforeSleep = 0L;
                sleep((this.sleepHours * 60 * 60 * 1000));
            }

            end = System.currentTimeMillis();
            spendTime += end - begin;
            oneDay += end - begin;

            if (oneDay >= 86400000L) {
                oneDay = 0L;
                messageCount = 0;
                needMessage = true;
            }

            if (spendTime >= this.dayBeforeUnfollow * 24.0D * 60.0D * 60.0D * 1000.0D) {
                spendTime = 0L;
                unFollow(1.0D, this.unfollowCount);
            }

            if (this.followingCount == this.maxFollowing) {
                spendTime = 0L;
                unFollow(this.daysOfUnfollowingWhenLimit, this.unfollowingCountWhenLimit);
            }

            if (queueForProfiles.isEmpty()) {
                bot.setUser(startProfile);
                queueForProfiles.addAll(bot.getFollowers());
            }
        }
    }

    public void unFollow(double days, int unfollowInHour) {
        if (unfollowInHour == 0) {
            sleep((long)days * 24L * 60L * 60L * 1000L);
        }
        long timeForOneUnFollow = (60*60*1000 - bot.TIMEFORFOLLOW*unfollowInHour) / unfollowInHour / 3;
        double limit = days*24*60*60*1000;
        long spendTime = 0;
        while (spendTime < limit) {
            long begin = System.currentTimeMillis();
            sleep(timeForOneUnFollow);
            if (!this.myFollowing.isEmpty()) {
                String unFollowProfile = (String)this.myFollowing.pollLast();

                long checkTimeBegin = System.currentTimeMillis();
                this.bot.setUser(unFollowProfile);
                long checkTimeEnd = System.currentTimeMillis();
                sleep(timeForOneUnFollow - (checkTimeEnd - checkTimeBegin));

                checkTimeBegin = System.currentTimeMillis();
                this.bot.unFollow();
                checkTimeEnd = System.currentTimeMillis();
                sleep(timeForOneUnFollow - (checkTimeEnd - checkTimeBegin));
            } else {

                this.bot.setUser(this.login);
                sleep(2000L);
                List<String> following = this.bot.getFollowing();
                if (following == null)
                    return;
                for (String foll : following)
                    this.myFollowing.push(foll);
                if (this.myFollowing.isEmpty())
                    return;
            }
            long end = System.currentTimeMillis();
            spendTime += end - begin;
        }
    }
}
