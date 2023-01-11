package com.cschurch.server.cs_server.enroll;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.data.domain.Sort;

import java.util.Objects;

public class EducationService {
    public static JsonObject stringToEducationJsonObject(String jsonString) {
        JsonObject education = new JsonObject();

        JsonObject jsonObject = (JsonObject) JsonParser.parseString(jsonString);

        String subject = jsonObject.get("subject").getAsString();
        String teacher = jsonObject.get("teacher").getAsString();
        String time = jsonObject.get("time").getAsString();

        education.addProperty("subject", subject);
        education.addProperty("teacher", teacher);
        education.addProperty("time", time);

        return education;
    }
    /*public static JsonArray stringToJsonArray(String jsonString, Integer num, String...type) {
        JsonArray jsonArray = new JsonArray();
        JsonArray tmp = (JsonArray) JsonParser.parseString(jsonString);
        for (int i = 0; i < num; i++) {

        }
    }*/
    public static JsonArray stringToEducationJsonArray(String jsonString) {

        JsonArray jsonArray = new JsonArray();
        JsonArray tmp = (JsonArray) JsonParser.parseString(jsonString);
        for (int i = 0; i < tmp.size(); i++) {
            JsonObject education = new JsonObject();
            JsonObject jsonObject = (JsonObject) tmp.get(i);

            String subject = jsonObject.get("subject").getAsString();
            String teacher = jsonObject.get("teacher").getAsString();
            String time = jsonObject.get("time").getAsString();

            education.addProperty("subject", subject);
            education.addProperty("teacher", teacher);
            education.addProperty("time", time);

            jsonArray.add(education);
        }
        return jsonArray;
    }
}
