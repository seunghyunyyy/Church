package com.cschurch.server.cs_server;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.java.Log;
import org.springframework.data.domain.Sort;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;
@org.springframework.stereotype.Service
public class Service {
    public static boolean isNumber(String str) { // 정수인지 확인하는 함수
        try {   Long.parseLong(str);    return true;    }
        catch (NumberFormatException e) { return false; }
    }

    public Sort sortByvDate(String dir, String data) {
        if (Objects.equals(dir, "DESC") || Objects.equals(dir, "desc")) return Sort.by(Sort.Direction.DESC, data);
        else if (Objects.equals(dir, "ASC") || Objects.equals(dir, "asc")) return Sort.by(Sort.Direction.ASC, data);
        else return Sort.by(Sort.Direction.DESC, data);
    }

    public static JsonObject stringToJsonObject(String jsonString, String...type) {
        boolean multi = false;

        JsonObject jsonObject = new JsonObject();
        JsonObject objectInObject = new JsonObject();
        JsonObject tmpJson = (JsonObject) JsonParser.parseString(jsonString);

        for (String str : type) if (isNumber(str))  {   multi = true;   break;  }

        int checkNum = 0;
        int objectNum = 0;
        boolean check = false;
        String objectName = null;

        for (String str : type) {
            if (!multi) {
                String tmp = tmpJson.get(str).getAsString();
                jsonObject.addProperty(str, tmp);
            }
            if (isNumber(str)) {
                check = true;
                checkNum = Integer.parseInt(str); //2
            } else {
                if (check) {
                    objectNum++;
                    String tmp = tmpJson.get(str).getAsString();
                    objectInObject.addProperty(str, tmp);
                    jsonObject.add(objectName, objectInObject);
                    if (objectNum == checkNum) { check = false; checkNum = 0; }
                } else {
                    objectName = str; //data
                }
            }
        }
        return jsonObject;
    }
    public static JsonArray jsonElementsToJsonArray(ArrayList<JsonObject> jsonObjects) {
        JsonArray jsonArray = new JsonArray();
        for (JsonObject tmp : jsonObjects) {
            jsonArray.add(tmp);
        }
        return jsonArray;
    }
    public static JsonArray stringToBulletinJsonArray(String jsonString) {

        JsonArray jsonArray = new JsonArray();
        JsonArray tmp = (JsonArray) JsonParser.parseString(jsonString);
        for (int i = 0; i < tmp.size(); i++) {
            JsonObject bulletin = new JsonObject();
            JsonObject photoJson = new JsonObject();
            JsonObject jsonObject = (JsonObject) tmp.get(i);

            String date = jsonObject.get("date").getAsString();

            String bulletin1 = jsonObject.get("bulletin1").getAsString();
            String bulletin8 = jsonObject.get("bulletin8").getAsString();

            photoJson.addProperty("bulletin1", bulletin1);
            photoJson.addProperty("bulletin8", bulletin8);

            bulletin.addProperty("date", date);
            bulletin.add("photo", photoJson);

            System.out.println(bulletin);

            jsonArray.add(bulletin);
            System.out.println(jsonArray);
        }
        return jsonArray;
    }
    /**
     * http request
     * @param urlString request url
     * @param parameter request parameter, expect query string(GET) or json string(POST, PUT)
     * @param method "GET", "POST", "PUT", ...
     * @return return response as string
     */
    public static String sendRequest(String urlString, String parameter, String method){
        try{
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setRequestProperty("Accept", "application/json");

            if(!method.equals("GET")){
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                OutputStream os = conn.getOutputStream();
                os.write(parameter.getBytes(StandardCharsets.UTF_8));
                os.close();
            }

            conn.connect();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = br.readLine()) != null) sb.append(line).append("\n");
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}
