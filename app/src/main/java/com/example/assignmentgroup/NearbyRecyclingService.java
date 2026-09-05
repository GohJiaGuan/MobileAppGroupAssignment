package com.example.assignmentgroup;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

// Looks up real nearby recycling points via OpenStreetMap's free Overpass API -
// no API key or billing account needed, unlike Google Places.
public class NearbyRecyclingService {

    private static final String ENDPOINT = "https://overpass-api.de/api/interpreter";
    private static final MediaType TEXT = MediaType.get("text/plain; charset=utf-8");
    private static final OkHttpClient client = new OkHttpClient();
    private static final int RADIUS_METERS = 5000;

    public interface Callback2 {
        void onResult(List<String> centres);
        void onError(String message);
    }

    public static class Centre {
        String name;
        double distanceKm;
    }

    public static void findNearby(double lat, double lon, Callback2 callback) {
        String query = String.format(Locale.US,
                "[out:json][timeout:15];node[\"amenity\"=\"recycling\"](around:%d,%f,%f);out body 20;",
                RADIUS_METERS, lat, lon);

        Request request = new Request.Builder()
                .url(ENDPOINT)
                .addHeader("Accept", "*/*")
                .addHeader("User-Agent", "Recira-Android-App")
                .post(RequestBody.create(query, TEXT))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("NearbyRecycling", "onFailure", e);
                callback.onError("Network error: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (Response resp = response) {
                    if (!resp.isSuccessful() || resp.body() == null) {
                        String bodyPreview = resp.body() != null ? resp.body().string() : "(no body)";
                        Log.e("NearbyRecycling", "HTTP " + resp.code() + ": " + bodyPreview);
                        callback.onError("Overpass API error " + resp.code());
                        return;
                    }
                    List<Centre> centres = parse(resp.body().string(), lat, lon);
                    if (centres.isEmpty()) {
                        callback.onError("No recycling points found nearby.");
                        return;
                    }
                    List<String> formatted = new ArrayList<>();
                    for (Centre c : centres) {
                        formatted.add(String.format(Locale.US, "%s - %.1f km away", c.name, c.distanceKm));
                    }
                    callback.onResult(formatted);
                } catch (JSONException e) {
                    callback.onError("Failed to parse Overpass response: " + e.getMessage());
                }
            }
        });
    }

    private static List<Centre> parse(String body, double userLat, double userLon) throws JSONException {
        JSONObject root = new JSONObject(body);
        JSONArray elements = root.optJSONArray("elements");
        List<Centre> centres = new ArrayList<>();
        if (elements == null) return centres;

        for (int i = 0; i < elements.length(); i++) {
            JSONObject el = elements.getJSONObject(i);
            double lat = el.optDouble("lat", Double.NaN);
            double lon = el.optDouble("lon", Double.NaN);
            if (Double.isNaN(lat) || Double.isNaN(lon)) continue;

            JSONObject tags = el.optJSONObject("tags");
            String name = tags != null ? tags.optString("name", "") : "";
            if (name.isEmpty()) name = "Recycling point";

            Centre c = new Centre();
            c.name = name;
            c.distanceKm = haversineKm(userLat, userLon, lat, lon);
            centres.add(c);
        }

        Collections.sort(centres, Comparator.comparingDouble(c -> c.distanceKm));
        return centres.size() > 5 ? centres.subList(0, 5) : centres;
    }

    private static double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        double r = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return r * c;
    }
}
