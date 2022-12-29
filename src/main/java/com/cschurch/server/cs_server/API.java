package com.cschurch.server.cs_server;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.tomcat.util.net.openssl.OpenSSLUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/server/v1")
public class API {
    private final BulletinRepository bulletinRepository;
    //private final Json json;

    @PostMapping("/bulletin")
    public Bulletin postBulletin(@RequestParam(name = "date", required = false, defaultValue = "0") String date)  {
        Bulletin bulletin = new Bulletin();
        ArrayList<String> bulletinPhotos = BulletinService.getBulletinPhotosURL(date);
        return bulletinRepository.save(BulletinService.getBulletin(date, bulletin, bulletinPhotos));
    }

    @GetMapping("/bulletin/{date}")
    public JsonObject getBulletinObject(@PathVariable("date") String date) throws JsonIOException{
        date = BulletinService.changDate(date);
        return BulletinService.stringToBulletinJsonObject(new Gson().toJson(bulletinRepository.findByDate(date).get()));
    }
/*
    @GetMapping("/bulletin")
    public JsonArray getBulletin(@RequestParam(name = "start", required = false, defaultValue = "0") Integer start,
                                 @RequestParam(name = "end", required = false, defaultValue = "0") Integer end) {

    }*/
}
