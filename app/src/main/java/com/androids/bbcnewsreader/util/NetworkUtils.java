package com.androids.bbcnewsreader.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtils {

    private static final String TAG = "NetworkUtils";
    private static final String BASE_URL = "http://feeds.bbci.co.uk/news/world/us_and_canada/rss.xml";

    public static String getResponseFromHttpUrl() throws IOException {
        URL url = new URL(BASE_URL);
        Log.d(TAG, "Request URL: " + url.toString());
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setInstanceFollowRedirects(true);  // Follow redirects

        try {
            int responseCode = urlConnection.getResponseCode();
            Log.d(TAG, "Response Code: " + responseCode);

            // Handle redirection manually if necessary
            if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP || responseCode == HttpURLConnection.HTTP_MOVED_PERM) {
                String newUrl = urlConnection.getHeaderField("Location");
                Log.d(TAG, "Redirected to: " + newUrl);
                url = new URL(newUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                responseCode = urlConnection.getResponseCode();
                Log.d(TAG, "New Response Code: " + responseCode);
            }

            if (responseCode != HttpURLConnection.HTTP_OK) {
                Log.e(TAG, "Error response code: " + responseCode);
                return null;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n");
            }
            reader.close();
            Log.d(TAG, "Response: " + response.toString());
            return response.toString();
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage(), e);
            return null;
        } finally {
            urlConnection.disconnect();
        }
    }
}
