package com.cschurch.server.cs_server;

public class YouTubeService {

    public static String getChannelId(String APIKey, String videoId) {
        if (videoId.contains("watch?v=")) {
            videoId = videoId.split("watch?v=")[1];
        }
        String url = "https://www.googleapis.com/youtube/v3/videos?part=snippet&"+"key="+APIKey+"&"+"id="+videoId;
        return Service.sendRequest(url,"", "GET").split("\"channelId\": \"")[1].split("\"")[0];
    }
}
