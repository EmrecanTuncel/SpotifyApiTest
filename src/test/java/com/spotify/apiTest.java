package com.spotify;

import com.google.common.io.Resources;
import io.restassured.RestAssured;
import io.restassured.internal.RestAssuredResponseImpl;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import static io.restassured.RestAssured.given;

import static org.testng.AssertJUnit.assertEquals;

public class apiTest {
    String userId = "";
    String playlistId = "";
    String tracks = "";
    String authToken = "BQDys2WOLMcc92cZYouZpTXL97vSjJFs6LIUyt8J_pcp9nrX6Xdo2Ha1VZQAXetxIaEGD_eCg4sis05RDMjwwnKy9E1zFZSZEWRxFuCD8-w-WD6if5iOb6EDXePmjkWfcGPGvcROImafMumjtgZUYYIDeJm6JW3aTIxwB7QezuS60aDGhOy0KqTWKTBEby44VNQVBh4hZNz0SDjMcZN5NxmsNz9qzhUZgtNSGjLGbfUDJTetUgq64rv_tY11HUEvHTEOJCLaO-Wg1cFq4o-OiFgHNxavuW-c1mj1PzIO";
    @BeforeMethod
    public void beforeTest() throws IOException {
        RestAssured.baseURI = "https://api.spotify.com/v1";
    }
    @Test
    public void Test() throws IOException {
        String trackName = "Arnavut Kaldırım";
        String newName = "Update Name";
        getUserId();
        createNewPlaylist();
        addItemsToPlaylist(getTrackUri(trackName));
        assertEquals(getTrackUri(trackName),isItemAdded());
       // changePlaylistName(newName);
      //  assertEquals(newName,getPlaylistName());

    }
    public void getUserId() {
        Response response =
                given()
                        .contentType("application/json; charset=UTF-8")
                        .header("Authorization", "Bearer " + authToken)
                .when()
                        .get("/me")
                .then()
                        .statusCode(200)
                        .extract()
                        .response();

        userId = response.getBody().jsonPath().getString("id");
        System.out.println("User ID: " + userId);
    }
    public void createNewPlaylist() throws IOException {
        URL file = Resources.getResource("newPlaylist.json");
        String myJson = Resources.toString(file, Charset.defaultCharset());
        JSONObject json = new JSONObject(myJson);
        Response playlistResponse =
                given()
                        .contentType("application/json; charset=UTF-8")
                        .header("Authorization", "Bearer " + authToken)
                        .body(json.toString())
                .when()
                        .post("/users/{userId}/playlists",userId)
                .then()
                        .statusCode(201)
                        .extract()
                        .response();
        playlistId = playlistResponse.getBody().jsonPath().getString("id");
        System.out.println("PlaylistID: "+  playlistId);
    }


    public void addItemsToPlaylist(String trackUri){
        given()
                .contentType("application/json; charset=UTF-8")
                .header("Authorization", "Bearer " + authToken)
                .queryParam("playlist_id",playlistId)
                .queryParam("uris",trackUri)
        .when()
                .post("playlists/{playlist_id}/tracks",playlistId)
        .then()
                .statusCode(201);

    }
    public String getTrackUri(String trackName){
        Response trackUriResponse =
                given()
                        .contentType("application/json; charset=UTF-8")
                        .header("Authorization", "Bearer " + authToken)
                        .queryParam("q",trackName )
                        .queryParam("type", "track")
                        .queryParam("market", "US")
                        .queryParam("limit","3")
                .when()
                        .get("search")
                .then()
                        .statusCode(200)
                        .extract()
                        .response();
        ArrayList arrayList = trackUriResponse.path("tracks.items.uri");
        tracks = trackUriResponse.getBody().jsonPath().getString("uri");
        System.out.println("TracksID: "+  tracks);

        return arrayList.get(0).toString();
    }
    public String isItemAdded() {
        Response itemResponse =
                given()
                        .contentType("application/json; charset=UTF-8")
                        .header("Authorization", "Bearer " + authToken)
                        .queryParam("playlist_id", playlistId)
                        .queryParam("market", "TR")
                        .queryParam("limit", "3")
                        .when()
                        .get("playlists/{playlist_id}/tracks", playlistId)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();

           ArrayList arraylist =  itemResponse.path("items.track.uri");
           return arraylist.get(0).toString();

         }

}
