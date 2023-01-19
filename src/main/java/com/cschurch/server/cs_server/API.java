package com.cschurch.server.cs_server;

import com.cschurch.server.cs_server.enroll.*;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/server/v1")
public class API {
    private final Service service;
    private final BulletinRepository bulletinRepository;
    private final EducationRepository educationRepository;
    private final PlaceRepository placeRepository;
    private final EducationEnrollRepository educationEnrollRepository;
    private final PlaceEnrollRepository placeEnrollRepository;
    private final UserRepository userRepository;

    /**
     * @param date 2023년 01월 01일 & 2023년 1월 1일 & 띄어쓰기 없이 가능
     *              23년 01월 01일 23년 1월 1일 & 띄어쓰기 없이 가능
     *              230101
     *             all 입력 시 모든 주보를 db에 업로드함.
     *             미입력 시 자동으로 가장 최근 주보를 db에 업로드함.
     * @return
     */
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
     * 주보를 페이지 단위로 가져와
     * @param sort 정렬 방식(기본값 : 내림차순(DESC))
     * @param num 한 페이지에 표시할 일자(기본값 : 10)
     * @param page 표시할 페이지(기본값 : 1)
     *             num 또는 page에 all 값 입력 시 모든 주보를 가져옴.
     * @return
     * @throws JsonIOException
     */
    @GetMapping("/bulletin")
    public JsonArray getBulletinObjects(@RequestParam(name = "sort", required = false, defaultValue = "DESC") String sort,
                                        @RequestParam(name = "sortName", required = false, defaultValue = "date") String sortName,
                                        @RequestParam(name = "num", required = false, defaultValue = "all") String num,
                                        @RequestParam(name = "page", required = false, defaultValue = "all") String page) throws JsonIOException {
        JsonArray jsonArray = new JsonArray();

        List<Bulletin> bulletins = new ArrayList<>();
        List<String> strings = new ArrayList<>();

        if (Objects.equals(sort, "DESC")) {
            bulletins = bulletinRepository.findAll(Sort.by(Sort.Direction.DESC, sortName));
        } else if (Objects.equals(sort, "ASC")){
            bulletins = bulletinRepository.findAll(Sort.by(Sort.Direction.ASC, sortName));
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
        return BulletinService.stringToBulletinJsonArray(strings.toString());
    }
    /**
     * @param date 2023년 01월 01일 & 2023년 1월 1일 & 띄어쓰기 없이 가능
     *              23년 01월 01일 23년 1월 1일 & 띄어쓰기 없이 가능
     *              230101
     * @return
     * @throws JsonIOException
     * 특정 일자의 주보를 가져옴
     */
    @GetMapping("/bulletin/{date}")
    public JsonObject getBulletinObject(@PathVariable("date") String date) throws JsonIOException{
        date = BulletinService.changDate(date);
        return BulletinService.stringToBulletinJsonObject(new Gson().toJson(bulletinRepository.findByDate(date).get()));
    }
    /**
     * @param education { "teacher"="string", "subject":"string", "time":"string" }
     * @return
     */


    @PostMapping("/education")
    public Education postEducation(@RequestBody Education education) {
        return educationRepository.save(new Education(education.getTeacher(), education.getSubject(), education.getTime()));
    }
    /**
     * @param sort 정렬 종류 ASC or DESC (기본값 : ASC)
     * @param type 정렬 기준 (time or subject or teacher (기본값 : time)
     * @return JsonArray
     * @throws JsonIOException
     */
    @GetMapping("/education")
    public JsonArray getEducationArray(@RequestParam(name = "sort", required = false, defaultValue = "ASC") String sort,
                                        @RequestParam(name = "type", required = false, defaultValue = "time") String type) throws JsonIOException {
        JsonArray jsonTmp = new JsonArray();

        List<Education> educations = new ArrayList<>();
        ArrayList<JsonObject> jsonObjects = new ArrayList<>();

        if (Objects.equals(sort, "DESC")) {
            educations = educationRepository.findAll(Sort.by(Sort.Direction.DESC, type));
        } else if (Objects.equals(sort, "ASC")){
            educations = educationRepository.findAll(Sort.by(Sort.Direction.ASC, type));
        }
        for (int i = 0; i < educationRepository.findAll().size(); i++) {
            jsonTmp.add(new Gson().toJson(educations.get(i)));
            jsonObjects.add(Service.stringToJsonObject(jsonTmp.get(i).getAsString(), "teacher", "subject", "time"));
        }

        return Service.jsonElementsToJsonArray(jsonObjects);
    }
    @GetMapping("/education/{subject}")
    public JsonObject getEducationSubject(@PathVariable("subject") String subject) throws JsonIOException{
        return Service.stringToJsonObject(new Gson().toJson(educationRepository.findBySubject(subject)), "teacher", "subject", "time");
    }

    @PostMapping("/place")
    public Place postPlace(@RequestBody Place place) {
        return placeRepository.save(new Place(place.getRoom(), place.getMember()));
    }

    @PostMapping("/user")
    public User postUser(@RequestBody User user) {
        return userRepository.save(new User(user.getEmail(), user.getToken(), user.getName(), user.getSex(),
                user.getAge(), user.getBirth(), user.getPhone(), user.getHome(), user.getOfficer(), user.getProfile()));
    }

    @PostMapping("/education/enroll")
    public Education_Enroll postEducationEnroll(@RequestBody Education_Enroll education_enroll) {
        return educationEnrollRepository.save(new Education_Enroll(education_enroll.getEmail(), education_enroll.getSubject(),
                education_enroll.getSituation(), LocalDateTime.now(ZoneId.of("Asia/Seoul")).toString()));
    }
    @GetMapping("/education/enroll")
    public JsonArray getEducationEnrollArray(@RequestParam(name = "sort", required = false, defaultValue = "DESC") String sort,
                                             @RequestParam(name = "sortName", required = false, defaultValue = "subject") String sortName,
                                             @RequestParam(name = "num", required = false, defaultValue = "all") String num,
                                             @RequestParam(name = "page", required = false, defaultValue = "all") String page) {
        JsonArray jsonArray = new JsonArray();
        List<Education_Enroll> educations = new ArrayList<>();
        List<String> strings = new ArrayList<>();

        if (Objects.equals(sort, "DESC")) {
            educations = educationEnrollRepository.findAll(Sort.by(Sort.Direction.DESC, sortName));
        } else if (Objects.equals(sort, "ASC")){
            educations = educationEnrollRepository.findAll(Sort.by(Sort.Direction.ASC, sortName));
        }

        if (Objects.equals(page, "all") || Objects.equals(num, "all")) {
            for (int i = 0; i < bulletinRepository.findAll().size(); i++) {
                jsonArray.add(new Gson().toJson(educations.get(i)));
                strings.add(jsonArray.get(i).getAsString());
            }
        } else {
            int Num = Integer.parseInt(num);
            int Page = Integer.parseInt(page);

            for (int i = 0; i < Num; i++) {
                jsonArray.add(new Gson().toJson(educations.get((Page - 1) * Num + i)));
                strings.add(jsonArray.get(i).getAsString());
            }
        }
        return EducationService.stringToEducationJsonArray(strings.toString());
    }

    @PostMapping("/place/enroll")
    public Place_Enroll postPlaceEnroll(@RequestBody Place_Enroll place_enroll) {
        return placeEnrollRepository.save(new Place_Enroll(place_enroll.getEmail(), place_enroll.getRoom(), place_enroll.getMember(),
                place_enroll.getStart(), place_enroll.getEnd(), place_enroll.getSituation(), LocalDateTime.now(ZoneId.of("Asia/Seoul")).toString()));
    }

    @GetMapping("/youtube/channelId")
    public String getChannelId(@RequestParam(name = "APIKey", required = false, defaultValue = "AIzaSyAUtQ_AOWPhTa1L-opp3z4vQjP_FhJvFUo") String APIKey,
                               @RequestParam(name = "videoId", required = false, defaultValue = "W4CqY8fWL_0") String videoId) {
        return YouTubeService.getChannelId(APIKey, videoId);
    }
    @GetMapping("/youtube/livestream")
    public void start() throws GeneralSecurityException, IOException, GoogleJsonResponseException {
        YouTubeService.startStream();
    }

}
