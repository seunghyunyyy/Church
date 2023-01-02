package com.cschurch.server.cs_server;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
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

    @PostMapping("/bulletin") // 크롤링 하여서 datebase에 저장
    public List<Bulletin> postBulletin(@RequestParam(name = "date", required = false, defaultValue = "new") String date)  { // 파라미터로 아무런 값도 받지 않을 시 기본 값인 new로 설정.
        List<Bulletin> bulletinList = new ArrayList<>();
        if (Objects.equals(date, "all")) { // 입력 값이 all인 경우 모든 주보를 크롤링하여 db에 업로드함. 주로 초반 데이터 셋팅을 위한 용도.
            List<Bulletin> bulletins = BulletinService.getBulletins();
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

    /**
     * 주보를 페이지 단위로 가져오는 api
     * @param sort 정렬 방식(기본값 : 내림차순)
     * @param num 한 페이지에 표시할 일자(기본값 : 10)
     * @param page 표시할 페이지(기본값 : 1)
     * @return
     * @throws JsonIOException
     */
    @GetMapping("/bulletin")
    public JsonArray getBulletinObjects(@RequestParam(name = "sort", required = false, defaultValue = "DESC") String sort,
                                        @RequestParam(name = "num", required = false, defaultValue = "10") String num,
                                        @RequestParam(name = "page", required = false, defaultValue = "1") String page) throws JsonIOException{
        JsonArray jsonArray = new JsonArray();
        List<Bulletin> bulletins = new ArrayList<>();
        List<String> strings = new ArrayList<>();

        if (Objects.equals(sort, "DESC")) {
            bulletins = bulletinRepository.findAll(Sort.by(Sort.Direction.DESC, "date"));
        } else if (Objects.equals(sort, "ASC")){
            bulletins = bulletinRepository.findAll(Sort.by(Sort.Direction.ASC, "date"));
        }

        if (Objects.equals(num, "all") || Objects.equals(page, "all")) {
            for (int i = 0; i < bulletinRepository.findAll().size(); i++) {
                jsonArray.add(new Gson().toJson(bulletins.get(i)));
                strings.add(jsonArray.get(i).getAsString());
            }
        } else {
            int Num = Integer.parseInt(num);
            int Page = Integer.parseInt(page);

            for (int i = 0; i < Num; i++) {
                jsonArray.add(new Gson().toJson(bulletins.get((Page - 1) * Num + i)));
                strings.add(jsonArray.get(i).getAsString());
            }
        }
        System.out.println(strings);
        return BulletinService.stringToBulletinJsonArray(strings.toString());
    }
    @GetMapping("/bulletin/{date}")
    public JsonObject getBulletinObject(@PathVariable("date") String date) throws JsonIOException{
        date = BulletinService.changDate(date);
        return BulletinService.stringToBulletinJsonObject(new Gson().toJson(bulletinRepository.findByDate(date).get()));
    }

}
