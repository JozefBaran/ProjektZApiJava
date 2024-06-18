package org.example;

import com.google.gson.Gson;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class APIClient {
    // API zanieczyszczenia powietrza
    private static final String AIR_POLLUTION_API_URL = "http://api.openweathermap.org/data/2.5/air_pollution?lat=%s&lon=%s&appid=%s";
    // API geokodowania
    private static final String GEO_CODING_API_URL = "http://api.openweathermap.org/geo/1.0/direct?q=%s&limit=1&appid=%s";

    // Klucz API
    private String apiKey;

    // Konstruktor inicjujący klienta API z kluczem API
    public APIClient(String apiKey) {
        this.apiKey = apiKey;
    }

    // Metoda pobierająca dane o zanieczyszczeniu powietrza dla podanej szerokości i długości geograficznej
    public AirPollutionData getAirPollutionData(double lat, double lon) throws Exception {
        // Konstrukcja pełnego URL dla zapytania do API zanieczyszczeń powietrza
        String urlString = String.format(AIR_POLLUTION_API_URL, lat, lon, apiKey);
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        // Odczyt odpowiedzi z API
        InputStreamReader reader = new InputStreamReader(conn.getInputStream());
        // Parsowanie odpowiedzi JSON do obiektu za pomocą Gson
        AirPollutionData data = new Gson().fromJson(reader, AirPollutionData.class);
        reader.close();

        return data;
    }

    // Metoda pobierająca współrzędne geograficzne dla podanej nazwy miasta
    public CityCoordinates getCoordinates(String cityName) throws Exception {
        // Konstrukcja pełnego URL dla zapytania do API geokodowania
        String urlString = String.format(GEO_CODING_API_URL, cityName, apiKey);
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        // Odczyt odpowiedzi z API
        InputStreamReader reader = new InputStreamReader(conn.getInputStream());
        // Parsowanie odpowiedzi JSON do tablicy obiektów za pomocą Gson
        CityCoordinates[] coordinatesArray = new Gson().fromJson(reader, CityCoordinates[].class);
        reader.close();

        // zwraca pierwszy element (współrzędne miasta)
        if (coordinatesArray.length > 0) {
            return coordinatesArray[0];
        } else {
            throw new Exception("City not found");
        }
    }
}
