package in.incognitech.reminder.provider;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.incognitech.reminder.util.ActivityImageFetcherBridge;

/**
 * Created by udit on 02/03/16.
 */
public class StockImageFetcher extends AsyncTask<Void, Void, String> {

    private ActivityImageFetcherBridge context;

    private static final int READ_TIMEOUT = 10000;
    private static final int CONNECT_TIMEOUT = 10000;
    private static final String API_ENDPOINT = "https://www.pexels.com/?format=js";

    public StockImageFetcher(ActivityImageFetcherBridge context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(Void... params) {
        String imageUri = "";

        // HTTP Request
        InputStream is = null;
        try {
            URL url = new URL(API_ENDPOINT);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(READ_TIMEOUT /* milliseconds */);
            conn.setConnectTimeout(CONNECT_TIMEOUT /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();

            int response = conn.getResponseCode();
            System.out.println("The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = this.convertInputStreamToString(is);

            System.out.println(contentAsString);

            String pattern = "data-pin-media=\\\\\"(.*?)\\\\\"";
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(contentAsString);

            List<String> images = new ArrayList<String>();
            while(m.find()) {
                images.add(m.group(1));
            }

            Random random = new Random();
            int index = random.nextInt(images.size()-0);
            imageUri = images.get(index);


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return imageUri;
    }

    public String convertInputStreamToString(InputStream stream) throws IOException, UnsupportedEncodingException {
        BufferedInputStream bis = new BufferedInputStream(stream);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int result = bis.read();
        while (result != -1) {
            byte b = (byte) result;
            buf.write(b);
            result = bis.read();
        }
        return buf.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        this.context.loadImage(s);
        super.onPostExecute(s);
    }
}
