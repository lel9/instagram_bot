package ru.mail.olg1997;

import java.io.*;

public class ParserByAnh {
    public static class InformationAboutUsersfile
    {
        public InformationAboutUsersfile(String login, String password, int likesCount, int averageSubscribesInHour, int subscriptionDuration, int averageUsersCountUnsubscriptionInHour, int maxSubscribes) {
            this.login = login;
            this.password = password;
            this.likesCount = likesCount;
            this.averageSubscribesInHour = averageSubscribesInHour;
            this.subscriptionDuration = subscriptionDuration;
            this.averageUsersCountUnsubscriptionInHour = averageUsersCountUnsubscriptionInHour;
            this.maxSubscribes = maxSubscribes;
        }

        public InformationAboutUsersfile ()
        {
            this.login = null;
            this.password = null;
            this.likesCount = 0;
            this.averageSubscribesInHour = 0;
            this.subscriptionDuration = 0;
            this.averageUsersCountUnsubscriptionInHour = 0;
            this.maxSubscribes = 0;
        }
        public void ReadFile(String filename) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(filename));
                String line;
                if ((line = br.readLine())!=null){
                    String[] subStr;
                    subStr = line.split(" ");
                    for(int i = 0; i < subStr.length; i++) {
                        System.out.println(subStr[i]);
                    }
                    login = subStr[0];
                    password = subStr[1];
                    likesCount = Integer.parseInt(subStr[2]);
                    averageSubscribesInHour = Integer.parseInt(subStr[3]);
                    subscriptionDuration = Integer.parseInt(subStr[4]);
                    averageUsersCountUnsubscriptionInHour = Integer.parseInt(subStr[5]);
                    maxSubscribes = Integer.parseInt(subStr[6]);
                }
                // while ((line = br.readLine()) != null) {
                // process the line.
                // }
            }
            catch (Exception exc) {
                if (exc instanceof FileNotFoundException|| exc instanceof IOException || exc instanceof NumberFormatException ) {
                    exc.printStackTrace();
                }
            }

        }

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public int getLikesCount() {
            return likesCount;
        }

        public void setLikesCount(int likesCount) {
            this.likesCount = likesCount;
        }

        public int getAverageSubscribesInHour() {
            return averageSubscribesInHour;
        }

        public void setAverageSubscribesInHour(int averageSubscribesInHour) {
            this.averageSubscribesInHour = averageSubscribesInHour;
        }

        public int getSubscriptionDuration() {
            return subscriptionDuration;
        }

        public void setSubscriptionDuration(int subscriptionDuration) {
            this.subscriptionDuration = subscriptionDuration;
        }

        public int getAverageUsersCountUnsubscriptionInHour() {
            return averageUsersCountUnsubscriptionInHour;
        }

        public void setAverageUsersCountUnsubscriptionInHour(int averageUsersCountUnsubscriptionInHour) {
            this.averageUsersCountUnsubscriptionInHour = averageUsersCountUnsubscriptionInHour;
        }

        public int getMaxSubscribes() {
            return maxSubscribes;
        }

        public void setMaxSubscribes(int maxSubscribes) {
            this.maxSubscribes = maxSubscribes;
        }

        private String login;
        private String password;
        private int likesCount;
        private int averageSubscribesInHour;
        private int subscriptionDuration;
        private int averageUsersCountUnsubscriptionInHour;
        private int maxSubscribes;

    }

    public static void main(String[] args) throws IOException {

        InformationAboutUsersfile info = new InformationAboutUsersfile();
        String filename = "the-file-name.txt";
        info.ReadFile(filename);
        System.out.println(info.likesCount+info.subscriptionDuration+1);
    }
}