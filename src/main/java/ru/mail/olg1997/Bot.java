package ru.mail.olg1997;

import org.brunocvcunha.instagram4j.Instagram4j;
import org.brunocvcunha.instagram4j.requests.*;
import org.brunocvcunha.instagram4j.requests.payload.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Bot {
    String usuario;
    String password;
    String message;
    Instagram4j instagram;

    public final long TIMEFORMESSAGE = 0;
    public final long TIMEFORLIKE = 0;
    public final long TIMEFORFOLLOW = 0;

    InstagramSearchUsernameResult userResult;
    InstagramFeedResult userMedia;

    String nextMaxId = null;

    public Bot(String usuario, String password, String message){
        this.usuario = usuario;
        this.password = password;
        this.message = message;

        instagram = Instagram4j.builder().username(usuario).password(password).build();
        instagram.setup();
    }

    public void Rest(long millis, boolean msg){
        try {
            if(msg)
                System.out.println("It's my rest time... let me take like umh " + millis/1000 + "s");
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void login() throws IOException {
        instagram.login();
    }

    public void requestUserMedia() {
        try {
            userMedia = instagram.sendRequest(new InstagramUserFeedRequest(userResult.getUser().getPk()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void like(int publicationNumber){
        List<InstagramFeedItem> items = userMedia.getItems();
        if (items == null || items.size() <= publicationNumber)
            return;

        try {
            long media_id = items.get(publicationNumber).getPk();
            instagram.sendRequest(new InstagramLikeRequest(media_id));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setUser(String profile) {
        try {
            userResult = instagram.sendRequest(new InstagramSearchUsernameRequest(profile));
        } catch (IOException e) {
            userResult = null;
            e.printStackTrace();
        }
    }

    public boolean isUserPrivate() {
        return userResult.getUser().is_private();
    }

    public boolean follow() throws IOException {
        StatusResult statusResult = instagram.sendRequest(new InstagramFollowRequest(userResult.getUser().getPk()));
        return statusResult.getStatus().equals("ok");
    }

    public void unFollow(){
        try {
            instagram.sendRequest(new InstagramUnfollowRequest(userResult.getUser().getPk()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void sendMessage() {
        List<String> recipients = Arrays.asList(((Long) userResult.getUser().getPk()).toString());
        try {
            instagram.sendRequest(InstagramDirectShareRequest
                    .builder().shareType(InstagramDirectShareRequest.ShareType.MESSAGE).recipients(recipients).message(message).build());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getFollowing() {
        List<String> result = new ArrayList<String>();
        String nextMaxId = null;
        long userId = userResult.getUser().getPk();
        try {
            while (true) {
                InstagramGetUserFollowersResult following = instagram.sendRequest(new InstagramGetUserFollowingRequest(userId, nextMaxId));
                List<InstagramUserSummary> users = following.getUsers();
                for (InstagramUserSummary user : users) {
                    result.add(user.getUsername());
                }
                nextMaxId = following.getNext_max_id();
                if (nextMaxId == null) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<String> getFollowers() {
        List<String> result = new ArrayList<>();
        long userId = userResult.getUser().getPk();
        try {
            InstagramGetUserFollowersResult fr = instagram.sendRequest(new InstagramGetUserFollowersRequest(userId, nextMaxId));
            List<InstagramUserSummary> users = fr.getUsers();
            for (InstagramUserSummary user : users) {
                result.add(user.getUsername());
            }
            nextMaxId = fr.getNext_max_id();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public int getFollowingCount() {
        return userResult.getUser().getFollowing_count();
    }
}
