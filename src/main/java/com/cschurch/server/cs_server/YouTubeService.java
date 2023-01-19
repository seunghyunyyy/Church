package com.cschurch.server.cs_server;

import com.cschurch.server.cs_server.GoogleLogin.ConfigUtils;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import com.google.common.collect.Lists;
import lombok.extern.java.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class YouTubeService {
    private static ConfigUtils configUtils;
    private static YouTube youtube;
    private static String url = "https://www.googleapis.com/youtube/v3/videos?part=snippet&";

    public static String getChannelId(String APIKey, String videoId) {
        if (videoId.contains("watch?v=")) {
            videoId = videoId.split("watch?v=")[1];
        }
        url += "key="+APIKey+"&"+"id="+videoId;
        System.out.println(Service.sendRequest(url,"", "GET"));
        return Service.sendRequest(url,"", "GET").split("\"channelId\": \"")[1].split("\"")[0];
    }
    public static void postYouTubeStream () {
        // This OAuth 2.0 access scope allows for full read/write access to the
        // authenticated user's account.
        List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube");

        try {
            // Authorize the request.
            Credential credential = Auth.authorize(scopes, "createbroadcast");

            // This object is used to make YouTube Data API requests.
            youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, credential)
                    .setApplicationName("youtube-cmdline-createbroadcast-sample").build();

            // Prompt the user to enter a title for the broadcast.
            String title = getBroadcastTitle();
            System.out.println("You chose " + title + " for broadcast title.");

            // Create a snippet with the title and scheduled start and end
            // times for the broadcast. Currently, those times are hard-coded.
            LiveBroadcastSnippet broadcastSnippet = new LiveBroadcastSnippet();
            broadcastSnippet.setTitle(title);
            broadcastSnippet.setScheduledStartTime(new DateTime("2024-01-30T00:00:00.000Z"));
            broadcastSnippet.setScheduledEndTime(new DateTime("2024-01-31T00:00:00.000Z"));

            // Set the broadcast's privacy status to "private". See:
            // https://developers.google.com/youtube/v3/live/docs/liveBroadcasts#status.privacyStatus
            LiveBroadcastStatus status = new LiveBroadcastStatus();
            status.setPrivacyStatus("private");

            LiveBroadcast broadcast = new LiveBroadcast();
            broadcast.setKind("youtube#liveBroadcast");
            broadcast.setSnippet(broadcastSnippet);
            broadcast.setStatus(status);

            // Construct and execute the API request to insert the broadcast.
            YouTube.LiveBroadcasts.Insert liveBroadcastInsert =
                    youtube.liveBroadcasts().insert(Collections.singletonList("snippet,status"), broadcast);
            LiveBroadcast returnedBroadcast = liveBroadcastInsert.execute();

            // Print information from the API response.
            System.out.println("\n================== Returned Broadcast ==================\n");
            System.out.println("  - Id: " + returnedBroadcast.getId());
            System.out.println("  - Title: " + returnedBroadcast.getSnippet().getTitle());
            System.out.println("  - Description: " + returnedBroadcast.getSnippet().getDescription());
            System.out.println("  - Published At: " + returnedBroadcast.getSnippet().getPublishedAt());
            System.out.println(
                    "  - Scheduled Start Time: " + returnedBroadcast.getSnippet().getScheduledStartTime());
            System.out.println(
                    "  - Scheduled End Time: " + returnedBroadcast.getSnippet().getScheduledEndTime());

            // Prompt the user to enter a title for the video stream.
            title = getStreamTitle();
            System.out.println("You chose " + title + " for stream title.");

            // Create a snippet with the video stream's title.
            LiveStreamSnippet streamSnippet = new LiveStreamSnippet();
            streamSnippet.setTitle(title);

            // Define the content distribution network settings for the
            // video stream. The settings specify the stream's format and
            // ingestion type. See:
            // https://developers.google.com/youtube/v3/live/docs/liveStreams#cdn
            CdnSettings cdnSettings = new CdnSettings();
            cdnSettings.setFormat("1080p");
            cdnSettings.setIngestionType("rtmp");

            LiveStream stream = new LiveStream();
            stream.setKind("youtube#liveStream");
            stream.setSnippet(streamSnippet);
            stream.setCdn(cdnSettings);

            // Construct and execute the API request to insert the stream.
            YouTube.LiveStreams.Insert liveStreamInsert =
                    youtube.liveStreams().insert(Collections.singletonList("snippet,cdn"), stream);
            LiveStream returnedStream = liveStreamInsert.execute();

            // Print information from the API response.
            System.out.println("\n================== Returned Stream ==================\n");
            System.out.println("  - Id: " + returnedStream.getId());
            System.out.println("  - Title: " + returnedStream.getSnippet().getTitle());
            System.out.println("  - Description: " + returnedStream.getSnippet().getDescription());
            System.out.println("  - Published At: " + returnedStream.getSnippet().getPublishedAt());

            // Construct and execute a request to bind the new broadcast
            // and stream.
            YouTube.LiveBroadcasts.Bind liveBroadcastBind =
                    youtube.liveBroadcasts().bind(returnedBroadcast.getId(), Collections.singletonList("id,contentDetails"));
            liveBroadcastBind.setStreamId(returnedStream.getId());
            returnedBroadcast = liveBroadcastBind.execute();

            // Print information from the API response.
            System.out.println("\n================== Returned Bound Broadcast ==================\n");
            System.out.println("  - Broadcast Id: " + returnedBroadcast.getId());
            System.out.println(
                    "  - Bound Stream Id: " + returnedBroadcast.getContentDetails().getBoundStreamId());

        } catch (
                GoogleJsonResponseException e) {
            System.err.println("GoogleJsonResponseException code: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
            e.printStackTrace();

        } catch (
                IOException e) {
            System.err.println("IOException: " + e.getMessage());
            e.printStackTrace();
        } catch (Throwable t) {
            System.err.println("Throwable: " + t.getMessage());
            t.printStackTrace();
        }
    }
    private static final String CLIENT_SECRETS= "/client_secret.json";
    private static final Collection<String> SCOPES = List.of("https://www.googleapis.com/auth/youtube.force-ssl");

    private static final String APPLICATION_NAME = "cs-church-test";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /**
     * Create an authorized Credential object.
     *
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize(final NetHttpTransport httpTransport) throws IOException {
        // Load client secrets.
        InputStream in = YouTubeService.class.getResourceAsStream(CLIENT_SECRETS);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                        .build();
        Credential credential =
                new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
        return credential;
    }

    /**
     * Build and return an authorized API client service.
     *
     * @return an authorized API client service
     * @throws GeneralSecurityException, IOException
     */
    public static YouTube getService() throws GeneralSecurityException, IOException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        Credential credential = authorize(httpTransport);
        return new YouTube.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
    /*public static void startStream() {
        List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube");

        try {
            // Authorize the request.
            Credential credential = Auth.authorize(scopes, "createbroadcast");

            // This object is used to make YouTube Data API requests.
            youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, credential)
                    .setApplicationName("youtube-cmdline-createbroadcast-sample").build();


            // Prompt the user to enter a title for the video stream.
            String title = getStreamTitle();
            System.out.println("You chose " + title + " for stream title.");

            // Create a snippet with the video stream's title.
            LiveStreamSnippet streamSnippet = new LiveStreamSnippet();
            streamSnippet.setTitle(title);

            // Define the content distribution network settings for the
            // video stream. The settings specify the stream's format and
            // ingestion type. See:
            // https://developers.google.com/youtube/v3/live/docs/liveStreams#cdn
            CdnSettings cdnSettings = new CdnSettings();
            cdnSettings.setFormat("1080p");
            cdnSettings.setIngestionType("rtmp");

            LiveStream stream = new LiveStream();
            stream.setKind("youtube#liveStream");
            stream.setSnippet(streamSnippet);
            stream.setCdn(cdnSettings);

            // Construct and execute the API request to insert the stream.
            YouTube.LiveStreams.Insert liveStreamInsert =
                    youtube.liveStreams().insert(Collections.singletonList("snippet,cdn"), stream);
            LiveStream returnedStream = liveStreamInsert.execute();

            // Print information from the API response.
            System.out.println("\n================== Returned Stream ==================\n");
            System.out.println("  - Id: " + returnedStream.getId());
            System.out.println("  - Title: " + returnedStream.getSnippet().getTitle());
            System.out.println("  - Description: " + returnedStream.getSnippet().getDescription());
            System.out.println("  - Published At: " + returnedStream.getSnippet().getPublishedAt());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }*/
    public static void startStream() throws GeneralSecurityException, IOException, GoogleJsonResponseException {
        YouTube youtubeService = getService();

        // Define the LiveStream object, which will be uploaded as the request body.
        LiveStream liveStream = new LiveStream();

        // Add the cdn object property to the LiveStream object.
        CdnSettings cdn = new CdnSettings();
        cdn.setFormat("1080p");
        cdn.setFrameRate("60fps");
        cdn.setIngestionType("rtmp");
        cdn.setResolution("1080p");
        liveStream.setCdn(cdn);

        // Add the snippet object property to the LiveStream object.
        LiveStreamSnippet snippet = new LiveStreamSnippet();
        snippet.setDescription("hello.");
        snippet.setTitle("test");
        liveStream.setSnippet(snippet);

        // Define and execute the API request
        YouTube.LiveStreams.Insert request = youtubeService.liveStreams()
                .insert(Collections.singletonList("snippet, cdn"), liveStream);
        LiveStream response = request.execute();
        System.out.println("response : " + response);
    }
    private static String getStreamTitle() throws IOException {

        String title = "";

        System.out.print("Please enter a stream title: ");
        BufferedReader bReader = new BufferedReader(new InputStreamReader(System.in));
        title = bReader.readLine();

        if (title.length() < 1) {
            // Use "New Stream" as the default title.
            title = "New Stream";
        }
        return title;
    }
    private static String getBroadcastTitle() throws IOException {

        String title = "";

        System.out.print("Please enter a broadcast title: ");
        BufferedReader bReader = new BufferedReader(new InputStreamReader(System.in));
        title = bReader.readLine();

        if (title.length() < 1) {
            // Use "New Broadcast" as the default title.
            title = "New Broadcast";
        }
        return title;
    }
    //public static String getChannelName(String APIKey, String)

}
