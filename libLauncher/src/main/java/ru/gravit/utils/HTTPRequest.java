package ru.gravit.utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import ru.gravit.utils.helper.IOHelper;

public class HTTPRequest {
    private static final int TIMEOUT = 10;

    public static int sendCrashreport(String strurl, byte[] data) throws IOException {
        URL url = new URL(strurl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded");
        connection.setRequestProperty("Content-Length",
                Integer.toString(data.length));
        connection.setRequestProperty("Content-Language", "en-US");
        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(data);
        outputStream.close();
        return connection.getResponseCode();
    }

    public static int sendCrashreport(String strurl, String data) throws IOException {
        return sendCrashreport(strurl, data.getBytes(IOHelper.UNICODE_CHARSET));
    }

    public static JsonValue jsonRequest(JsonObject request, URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        connection.setRequestProperty("Accept", "application/json");
        if (TIMEOUT > 0)
            connection.setConnectTimeout(TIMEOUT);

        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), Charset.forName("UTF-8"));
        writer.write(request.toString());
        writer.flush();
        writer.close();

        InputStreamReader reader;
        int statusCode = connection.getResponseCode();

        if (200 <= statusCode && statusCode < 300)
            reader = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8);
        else
            reader = new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8);
        JsonValue content = Json.parse(reader);
        return content;
    }
}
