package com.digios.slowmoserver.websocketserver;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Network {
    final static Logger logger = Logger.getLogger(Network.class);

    public static boolean sendData(String url, Map<String, String> params) {
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(url);

        List<NameValuePair> postParams = new ArrayList<NameValuePair>(2);

        for (String key : params.keySet()) {
            postParams.add(new BasicNameValuePair(key, params.get(key)));
        }

        try {
            httppost.setEntity(new UrlEncodedFormEntity(postParams, "UTF-8"));

            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                InputStream instream = entity.getContent();
                try {
                    // do something useful
                } finally {
                    instream.close();
                }
            }
        }
        catch (Exception e)
        {
            logger.error(e);
            return false;
        }

        return true;
    }
}
