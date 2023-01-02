package com.cschurch.server.cs_server;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
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
    public List<Bulletin> postBulletin(@RequestParam(name = "date", required = false, defaultValue = "new") String date)  { // 파라미터로 아무런 값도 받지 않을 시 기본 값인 new로 설정.
        List<Bulletin> bulletinList = new ArrayList<>();
        if (Objects.equals(date, "all")) { // 입력 값이 all인 경우 모든 주보를 크롤링하여 db에 업로드함. 주로 초반 데이터 셋팅을 위한 용도.
            ArrayList<Bulletin> bulletins = BulletinService.getBulletins();
            return bulletinRepository.saveAll(bulletins);
        }
        if (Objects.equals(date, "new")) {
            date = "0";
        }
        ArrayList<String> bulletinPhotos = BulletinService.getBulletinPhotosURL(date);
        Bulletin bulletin = BulletinService.getBulletin(date, bulletinPhotos);
        bulletinList.add(bulletinRepository.save(bulletin));
        return bulletinList;
    }

    @GetMapping("/bulletin/{date}")
    public JsonObject getBulletinObject(@PathVariable("date") String date) throws JsonIOException{
        date = BulletinService.changDate(date);
        return BulletinService.stringToBulletinJsonObject(new Gson().toJson(bulletinRepository.findByDate(date).get()));
    }

}
