package com.c3labs.dss.WebService.Asyncs;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.c3labs.dss.Controls.Methods;
import com.c3labs.dss.WebService.Refferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by c3 on 3/23/2018.
 */

public class AsyncURLConnection extends AsyncTask<ArrayList<File>, Void, Void> {
    Context context;

    public AsyncURLConnection(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(ArrayList<File>[] arrayLists) {
//        String TAG = "AssURL";
        try {
            String files = "";
            ArrayList<File> arrayList = arrayLists[0];

            for (int i = 0; i < arrayList.size(); i++) {
//                Log.d(TAG, "doInBackground: " + arrayList.get(i));
//                Log.d(TAG, "doInBackground: " + arrayList.get(i).replace(".", ",").split(",")[0]);
                files += arrayList.get(i).getName().replace(".", ",").split(",")[0];
                if (i != arrayList.size() - 1) {
                    files += ",";
                }
            }

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("nodeId", Methods.getNodeId(context));
            jsonObject.put("files", files);

            URL postURL = new URL(Refferences.UpdateFileDownloadStatus.methodName);
            Log.d("MyURL----------", "doInBackground: " + Refferences.UpdateFileDownloadStatus.methodName + " - " + files);

            HttpURLConnection con = (HttpURLConnection) postURL.openConnection();
            con.setRequestProperty("Content-Type", "application/json");
            con.setConnectTimeout(10 * 1000);
            con.setReadTimeout(30 * 1000);
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);


            OutputStream os = con.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            writer.write(jsonObject.toString());
            writer.flush();
            writer.close();
            os.close();

            con.connect();


            int status = con.getResponseCode();
//            Log.d("stat", "status: " + status);
//            ValueHolder.HTTP_CONECTION_CODE = status;
            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    br.close();

                    String response = sb.toString();
                    Log.d("Server Response : \n", response);
                    Log.d("Size postToServer : ", +response.toString().getBytes().length + " bytes");
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
