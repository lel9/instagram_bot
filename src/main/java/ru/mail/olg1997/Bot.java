package ru.mail.olg1997;

import org.brunocvcunha.instagram4j.Instagram4j;
import org.brunocvcunha.instagram4j.requests.*;
import org.brunocvcunha.instagram4j.requests.payload.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.Keys;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


import org.openqa.selenium.TimeoutException;


public class Bot {
    //WebDriver driver;
    String usuario;
    String password;
    String message;
    Instagram4j instagram;

    public final long TIMEFORMESSAGE = 0;
    public final long TIMEFORLIKE = 0;
    public final long TIMEFORFOLLOW = 0;

    InstagramSearchUsernameResult userResult;
    InstagramFeedResult userMedia;

    public Bot(String usuario, String password, String message){
//        System.setProperty("webdriver.gecko.driver", "C:\\Linux\\geckodriver-v0.21.0-win64\\geckodriver.exe");
//        DesiredCapabilities capabilities = DesiredCapabilities.firefox();
//        capabilities.setCapability("marionette", true);
//
//        driver = new FirefoxDriver();
//        this.usuario = usuario;
//        this.password = password;

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

//    public void waitElement(String elemento, int tiempo){
//        WebDriverWait esperar = new WebDriverWait(driver, tiempo);
//        esperar.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(elemento)));
//    }

    public void login() throws IOException {
        instagram.login();
//        driver.get("https://www.instagram.com/accounts/login/");
//
//        waitElement("/html/body/span/section/main/div/article/div/div[1]/div/form/div[1]/div/div[1]/input", 20); 	//MODIFY XPATH HERE IN CASE INSTAGRAM CHANGE HIS STRUCTURE
//        waitElement("/html/body/span/section/main/div/article/div/div[1]/div/form/div[2]/div/div[1]/input", 20);	//MODIFY XPATH HERE IN CASE INSTAGRAM CHANGE HIS STRUCTURE
//        driver.findElement(By.xpath("/html/body/span/section/main/div/article/div/div[1]/div/form/div[1]/div/div[1]/input")).sendKeys(usuario);	//MODIFY XPATH HERE IN CASE INSTAGRAM CHANGE HIS STRUCTURE
//        Rest(1000,true);
//        driver.findElement(By.xpath("/html/body/span/section/main/div/article/div/div[1]/div/form/div[2]/div/div[1]/input")).sendKeys(password);	//MODIFY XPATH HERE IN CASE INSTAGRAM CHANGE HIS STRUCTURE
//        WebElement button = driver.findElement(By.xpath("//*[@id='react-root']/section/main/div/article/div/div[1]/div/form/span[1]/button"));//MODIFY XPATH HERE IN CASE INSTAGRAM CHANGE HIS STRUCTURE
//        Rest(1000,true);
//        button.click();
//        Rest(3000, true);
//        driver.get("https://www.instagram.com/" + usuario);
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

//
//        WebElement publication = driver.findElement(By.xpath("/html/body/span/section/main/div/div[2]/article/div[1]/div"));
//
//        Rest(1000, false);
//        List<WebElement> refs = publication.findElements(By.tagName("a"));
//
//        if(refs.size() == 0)
//            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
//
//        for (int i = 0; i < Math.min(publicationCount, refs.size()); i++) {
//            String href = refs.get(i).getAttribute("href");
//            href = "https://www.instagram.com/" + href;
//            Rest(sleepTime, false);
//            driver.get(href);
//            Rest(1000, false);
//            waitElement("/html/body/span/section/main/div/div/article/div[2]/section[1]/span[1]/button", 1);
//            WebElement likeButton = driver.findElement(By.xpath("/html/body/span/section/main/div/div/article/div[2]/section[1]/span[1]/button"));
//            Rest(1000, false);
//            if (likeButton.getText().equals("Мне нравится"))
//                likeButton.click();
//            Rest(1000, false);
//        }
//
//        for (int i = 0; i < publicationCount - refs.size(); i++)
//            Rest(sleepTime, false);
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
        //
//        InstagramSearchUsernameRequest
//        driver.get(profile);
//        if (driver.getPageSource().contains("Это закрытый аккаунт")) {
//            Rest(4, false);
//            return false;
//        }
//
//        waitElement("/html/body/span/section/main/div/header/section/div[1]/a/button", 3);
//        Rest(2, false);
//        WebElement followButton = driver.findElement(By.xpath("/html/body/span/section/main/div/header/section/div[1]/a/button"));
//        if (followButton.getText().equals("Подписаться")) {
//            followButton.click();
//            Rest(1, false);
//            return true;
//        }
//        Rest(1, false);
//        return false;
    }

    public void unFollow(){
        try {
            instagram.sendRequest(new InstagramUnfollowRequest(userResult.getUser().getPk()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //        driver.get(profile);
//        if (driver.getPageSource().contains("Это закрытый аккаунт")) {
//            Rest(4, false);
//            return;
//        }
//
//        waitElement("/html/body/span/section/main/div/header/section/div[1]/a/button", 3);
//        Rest(2, false);
//        WebElement followButton = driver.findElement(By.xpath("/html/body/span/section/main/div/header/section/div[1]/a/button"));
//        if (followButton.getText().equals("Отписаться")) {
//            followButton.click();
//        }
//        Rest(1, false);
    }


    public void sendMessage() {
        List<String> recipients = Arrays.asList(((Long) userResult.getUser().getPk()).toString());
        try {
            instagram.sendRequest(InstagramDirectShareRequest
                    .builder(InstagramDirectShareRequest.ShareType.MESSAGE, recipients).message(message).build());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getFollowing() {
        List<String> result = new ArrayList<String>();
        try {
            InstagramGetUserFollowersResult following = instagram.sendRequest(new InstagramGetUserFollowingRequest(userResult.getUser().getPk()));
            List<InstagramUserSummary> users = following.getUsers();
            for (InstagramUserSummary user : users) {
                result.add(user.getUsername());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<String> getFollowers() {
        List<String> result = new ArrayList<String>();
        try {
            InstagramGetUserFollowersResult followers = instagram.sendRequest(new InstagramGetUserFollowersRequest(userResult.getUser().getPk()));
            List<InstagramUserSummary> users = followers.getUsers();
            for (InstagramUserSummary user : users) {
                result.add(user.getUsername());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public int getFollowingCount() {
        return userResult.getUser().getFollowing_count();
    }
}


