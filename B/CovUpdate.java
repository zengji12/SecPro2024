package com.covupdate;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;

public class App {
    public static String suffix(long count) {
        if (count < 1000) return "" + count;
        int exp = (int) (Math.log(count) / Math.log(1000));
        String idSeparator = "RbJtMy";
        return String.format("%." + ( exp > 1 ? 2 : 0 ) + "f%s", count / Math.pow(1000, exp), idSeparator.substring((exp-1)*2, exp*2));
    }
    
    public static void main(String[] args) throws IOException {
       
        // TODO code application logic here
        CloseableHttpClient httpClient = HttpClients.createDefault();
        System.out.println("Update status Covid-19 Indonesia hari ini");
        System.out.println("=========================================");
        try {

            HttpGet request = new HttpGet("https://gist.githubusercontent.com/hermanka/b6e1556df7cee24f8a8ae5aa7e8d29ca/raw");
            request.addHeader("accept", "application/json");

            CloseableHttpResponse response = httpClient.execute(request);
            
            try {

                if (response.getEntity() != null) {
                    String json = IOUtils.toString(response.getEntity().getContent());
                    JSONArray array = new JSONArray(json);
                    JSONObject object_today = array.getJSONObject(array.length() - 1);
                    JSONObject object_yesterday = array.getJSONObject(array.length() - 2);
                    
                    Integer todayConfirmed = Integer.parseInt(object_today.get("Confirmed").toString());
                    Integer yesterdayConfirmed = Integer.parseInt(object_yesterday.get("Confirmed").toString());
                    Integer todayDeaths = Integer.parseInt(object_today.get("Deaths").toString());
                    Integer yesterdayDeaths = Integer.parseInt(object_yesterday.get("Deaths").toString());                    

                    System.out.println("Terkonfirmasi \t: " + (todayConfirmed - yesterdayConfirmed) + " Kasus");
                    System.out.println("Meninggal \t: " + (todayDeaths==yesterdayDeaths?"Tidak ada yang meninggal hari ini":( todayDeaths - yesterdayDeaths)));
                    System.out.println("Tot. kasus \t: " + suffix(todayConfirmed));
                    System.out.println("Tot. meninggal \t: " + suffix(todayDeaths));
                    String todayDate = object_today.get("Date").toString();
                    todayDate.replace("T00:00:00Z", "");
                    System.out.println(todayDate);
                    DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    OffsetDateTime dateTime = OffsetDateTime.parse(todayDate);
                    System.out.println("Terakhir diperbarui " + dateTime.format(dayFormatter));

                }
            } finally {
                response.close();
            }
        } finally {
            httpClient.close();
        }
    }  
}
