package com.cschurch.server.cs_server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class BulletinService {
    private static final String BASEURL = "https://www.cschurch.or.kr";
    private static String URL = null;
    private static ArrayList<String> URLs = new ArrayList<String>();
    private static final ArrayList<String> PhotoUrlList = new ArrayList<>();
    private static Document doc = null;

    public static boolean isNumber(String str) { // 정수인지 확인하는 함수
        try {   Long.parseLong(str);    return true;    }
        catch (NumberFormatException e) { return false; }
    }
    public static void getBulletinSiteURL(String Date) { // 웹 크롤링을 통해 주보 사진이 있는 URL 추출
        String BulletinMainURL;
        if (Objects.equals(Date, "0") || Date == null) BulletinMainURL = BASEURL + "/front/F060600"; // 입력받은 일자가 없거나 0이면 가장 최근 일자
        else  {
            String year, mon, day;
            if (isNumber(Date)) { //입력된 일자가 정수로 되어 있을 때
                long tmp = Long.parseLong(Date);
                year = (tmp / 10000) + "년";
                mon = ((tmp % 10000) / 100) + "월";
                day = (tmp % 100) + "일";
            }   else {
                year = Date.split("년")[0].replaceAll(" ", "") + "년";
                mon = Integer.parseInt(Date.split("년")[1].split("월")[0].replaceAll(" ", "")) + "월";
                day = Integer.parseInt(Date.split("년")[1].split("월")[1].split("일")[0].replaceAll(" ", "")) + "일";
            }
            BulletinMainURL = BASEURL + "/front/F060600?searchKey=title&searchWord=" + year + "%20" + mon + "%20" + day;
        }
        try { doc = Jsoup.connect(BulletinMainURL).get(); } catch (IOException e) { e.printStackTrace(); }
        Elements site = doc.select("ul.board_list").select("li").select("a"); //해당 일자의 게시물 넘버
        URL = BASEURL + "/board/F060600" + "/1/" + site.toString().split("'")[1] + "?page=1";
    }
    public static void getBulletinSiteURLs() {
        int page = 0;
        do {
            page++;
            String date = null;
            String tmpURL = BASEURL + "/front/F060600?page=" + page + "&searchKey=title&searchWord=";
            try {
                doc = Jsoup.connect(tmpURL).get();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Elements el1 = doc.select("ul.board_list").select("li");
            for (Element tmp : el1) {
                date = tmp.select("a").toString().split(">")[1].split("<")[0];
                URL = BASEURL + "/board/F060600" + "/1/" + tmp.select("a").toString().split("'")[1] + "?page=1";
                URLs.add(URL);
            }
            if (Objects.equals(date, "2021년 5월 2일")) break;
        } while (true);
        // 반복문 문제 가능성 99%
        URL = null;
    }
    public static String changeBulletinNullToDate(String Date) {
        getBulletinSiteURL(Date);

        try { doc = Jsoup.connect(URL).get(); } catch (IOException e) { e.printStackTrace(); }
        String tmp = doc.select("h3.title_view").toString().split(">")[1].split("<")[0];

        // 년/월/일 로 입력한 date를 20230101형식으로 바꾸기 위한 과정
        String year = tmp.split("년")[0].replaceAll(" ", "");
        String mon = tmp.split("년")[1].split("월")[0].replaceAll(" ", "");
        String day = tmp.split("년")[1].split("월")[1].split("일")[0].replaceAll(" ", "");

        //2023년 01월 01일을 20230101로 저장하기 위한 과정 -> 이 과정이 없다면 date는 202311로 저장이 됨
        if (Integer.parseInt(mon) < 10 && mon.charAt(0) != '0')     mon = "0" + mon;
        if (Integer.parseInt(day) < 10 && day.charAt(0) != '0')     day = "0" + day;

        return year + mon + day;
    }
    public static ArrayList<String> getBulletinPhotosURL(String Date) {
        String PhotoURL;
        getBulletinSiteURL(Date);
        try { doc = Jsoup.connect(URL).get(); } catch (IOException e) { e.printStackTrace(); }
        Elements photosURL = doc.select("div.view_contents").select("p").select("img");
        for (Element photo : photosURL) {
            PhotoURL = BASEURL + photo.toString().split("\"")[1];
            PhotoUrlList.add(PhotoURL);
        }
        return PhotoUrlList;
    }

    /**
     * 모든 주보를 가져옴
     * @return
     */
    public static ArrayList<Bulletin> getBulletins() {
        ArrayList<Bulletin> bulletins = new ArrayList<>();
        getBulletinSiteURLs();
        for (String url : URLs) { // url 별로 반복
            ArrayList<String> PhotoUrlList = new ArrayList<>();
            try { doc = Jsoup.connect(url).get(); } catch (IOException e) { e.printStackTrace(); }
            Elements photosURL = doc.select("div.view_contents").select("p").select("img");
            for (Element photo : photosURL) {
                String PhotoURL = BASEURL + photo.toString().split("\"")[1]; //임시
                PhotoUrlList.add(PhotoURL); // -> 한 페이지의 모든 주보를 저장함
            }
            String date = doc.select("h3.title_view").toString().split(">")[1].split("<")[0];
            Bulletin bulletin = getBulletin(date, PhotoUrlList);

            bulletins.add(bulletin);
        }
        return bulletins;
    }

    /**
     * @param date  원하는 날짜 입력
     * @param bulletinPhotos
     * @return
     */
    public static Bulletin getBulletin(String date, ArrayList<String> bulletinPhotos) {
        Bulletin bulletin = new Bulletin();

        date = changDate(date);

        bulletin.setDate(date);
        bulletin.setBulletin1(bulletinPhotos.get(0));
        bulletin.setBulletin2(bulletinPhotos.get(1));
        bulletin.setBulletin3(bulletinPhotos.get(2));
        bulletin.setBulletin4(bulletinPhotos.get(3));
        bulletin.setBulletin5(bulletinPhotos.get(4));
        bulletin.setBulletin6(bulletinPhotos.get(5));
        bulletin.setBulletin7(bulletinPhotos.get(6));
        bulletin.setBulletin8(bulletinPhotos.get(7));

        return bulletin;
    }
    public static JsonObject stringToBulletinJsonObject(String jsonString) {
        JsonObject bulletin = new JsonObject();
        JsonObject photoJson = new JsonObject();

        JsonObject jsonObject = (JsonObject) JsonParser.parseString(jsonString);

        Long date = jsonObject.get("date").getAsLong();

        String bulletin1 = jsonObject.get("bulletin1").getAsString();
        String bulletin2 = jsonObject.get("bulletin2").getAsString();
        String bulletin3 = jsonObject.get("bulletin3").getAsString();
        String bulletin4 = jsonObject.get("bulletin4").getAsString();
        String bulletin5 = jsonObject.get("bulletin5").getAsString();
        String bulletin6 = jsonObject.get("bulletin6").getAsString();
        String bulletin7 = jsonObject.get("bulletin7").getAsString();
        String bulletin8 = jsonObject.get("bulletin8").getAsString();

        photoJson.addProperty("bulletin1", bulletin1);
        photoJson.addProperty("bulletin2", bulletin2);
        photoJson.addProperty("bulletin3", bulletin3);
        photoJson.addProperty("bulletin4", bulletin4);
        photoJson.addProperty("bulletin5", bulletin5);
        photoJson.addProperty("bulletin6", bulletin6);
        photoJson.addProperty("bulletin7", bulletin7);
        photoJson.addProperty("bulletin8", bulletin8);

        bulletin.addProperty("date", date);
        bulletin.add("photo", photoJson);

        return bulletin;
    }

    /**
     * @param date 230101 or 20230101 or 2023년 1월 1일 or 2023년 01월 01일
     * @return
     */
    public static String changDate(String date) {
        date = date.replaceAll(" ", "");
        if (date.equals("2021년12년12일")) {
            date = "2021년12월12일";
        }

        // 날짜가 new인 경우 changeBulletinNullToDate를 호출하여 가장 최근 날짜를 불러옴.
        if (Objects.equals(date, "0")) {
            date = changeBulletinNullToDate("0");
        }

        // 입력받은 date에 한글이 있는 경우
        if (!isNumber(date)) {
            String year, mon, day;

            // 년/월/일 로 입력한 date를 20230101형식으로 바꾸기 위한 과정
            year = date.split("년")[0].replaceAll(" ", "");
            mon = date.split("년")[1].split("월")[0].replaceAll(" ", "");
            day = date.split("년")[1].split("월")[1].split("일")[0].replaceAll(" ", "");

            //2023년 01월 01일을 20230101로 저장하기 위한 과정 -> 이 과정이 없다면 date는 202311로 저장이 됨
            if (Integer.parseInt(mon) < 10 && mon.charAt(0) != '0')     mon = "0" + mon;
            if (Integer.parseInt(day) < 10 && day.charAt(0) != '0')     day = "0" + day;

            date = (String) year + (String) mon + (String) day;
        }
        // 입력받은 date가 230101 형식으로 입력된 경우 20230101 형식으로 변환하는 과정. 100년이 지날 경우 수정해야 함.
        if ((Long.parseLong(date) / 10000000) == 0) {
            long tmp = 20 * 1000000 + Long.parseLong(date);
            date = Long.toString(tmp);
        }

        return date.replaceAll(" ", "");
    }



    /**
     * @param path 주보를 저장할 경로를 인자로 받습니다.
     * @param Date 원하는 날짜의 년/월/일 을 공백을 기준으로 입력합니다.
     * ex) 23년 1월 1일 O    2023년 1월 1일 O    2023년 01월 01일 X    22년1월1일 X    2023년01월01일 X
     * 키오스크용 함수? 삭제 가능
     */
    /*
    public static void saveBulletin(String path, String Date) {
        String title = getBulletinTitle(Date);
        File dir = new File(path + "\\" + title);

        if (!dir.exists()) try { dir.mkdir(); } catch (Exception e) { e.getStackTrace(); } // 폴더가 없을 경우 폴더 생성

        if(Objects.requireNonNull(dir.list()).length < 8) {
            File[] files = dir.listFiles();
            for (int i = 0; i < files.length; i++) files[i].delete();
            System.out.println("폴더 내 파일들을 삭제했습니다.");
        } //폴더 내에 파일이 하나라도 없는 경우 모두 삭제
        if (Objects.requireNonNull(dir.list()).length == 0) { //폴더 내에 파일이 하나도 없는 경우 웹으로부터 이미지를 불러옴
            getBulletinPhotosURL(Date);
            for (int i = 0; i < PhotoUrlList.size(); i++) {
                String url = PhotoUrlList.get(i);
                System.out.println(url);
                try {
                    BufferedImage photo = ImageIO.read(new URL(url));
                    File file = new File(dir + "\\" + (i+1) +".jpg");
                    ImageIO.write(photo, "jpg", file);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println("저장했습니다.");
        } else System.out.println("이미 저장되었습니다.");
    }*/
}
