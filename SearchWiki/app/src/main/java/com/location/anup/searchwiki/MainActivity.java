package com.location.anup.searchwiki;

import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import model.PageModelData;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputLayout mSearchTextInput;
    private Button mSearchButton;
    private RecyclerView mImageRecyclerView;
    private EditText mSearchEditText;
    private ArrayList<PageModelData> pageItemList= new ArrayList<PageModelData>();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mSearchTextInput=(TextInputLayout)findViewById(R.id.search_text_input);
        mSearchEditText=(EditText)findViewById(R.id.search_edit_text);
        mSearchButton=(Button)findViewById(R.id.search_button);
        mImageRecyclerView=(RecyclerView)findViewById(R.id.image_view_rv);
        mImageRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mSearchButton.setOnClickListener(this);
        mSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mSearchTextInput.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length()>0){
                    getResultImage();
                }else{
                    pageItemList.clear();
                    mImageRecyclerView.getAdapter().notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.search_button:
                getResultImage();
                break;
        }
    }

    private void getResultImage() {
        String searchTerm=mSearchEditText.getText().toString();
        if(TextUtils.isEmpty(searchTerm)){
            mSearchTextInput.setErrorEnabled(true);
            mSearchTextInput.setError(getResources().getString(R.string.please_enter_some_text));
            return;
        }else{
            String url = "https://en.wikipedia.org/w/api.php?" +
                    "action=query&prop=pageimages&format=json&piprop=thumbnail&pithumbsize=50&" +
                    "pilimit=50&generator=prefixsearch&gpssearch=";
            url=url+searchTerm;
            new FetchImages().execute(url);
        }
    }

    /**
     *
     */
    private class FetchImages extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... uri) {

            URL url = null;
            String response = null;

            try {
                //String postParams = getUserNotifyPostParams();
                url = new URL(uri[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
                //conn.setRequestProperty("Content-Length", String.valueOf(postParams.getBytes().length));
                conn.setDoOutput(true);
                //conn.getOutputStream().write(postParams.getBytes());

                // read the response
                InputStream in = new BufferedInputStream(conn.getInputStream());
                response = convertInputStreamToString(in);
                //System.out.println(response);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //System.out.println("FROM POST EXECUTE: " + result);
            try {

                JSONObject jObject = new JSONObject(result);
                handleResponse(jObject);

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * @param jObject
         * @throws Exception
         */
        private void handleResponse(JSONObject jObject) throws Exception {
            pageItemList.clear();
            if(jObject.has("query")){
                JSONObject query=jObject.getJSONObject("query");
                if(query.has("pages")){
                    JSONObject pages=query.getJSONObject("pages");
                    Iterator keys = pages.keys();

                    while(keys.hasNext()) {
                        // loop to get the dynamic key
                        String currentDynamicKey = (String)keys.next();

                        // get the value of the dynamic key
                        JSONObject currentDynamicValue = pages.getJSONObject(currentDynamicKey);
                        Gson gson = new Gson();
                        PageModelData pageItem=gson.fromJson(currentDynamicValue.toString(), PageModelData.class);
                        pageItemList.add(pageItem);
                        // do something here with the value...
                    }
                    if(pageItemList.size()>0 && mSearchEditText.getText().length()>0){
                        setAdapterForResult(pageItemList);
                    }else{
                        pageItemList.clear();
                    }
                }
            }
        }
    }

    private void setAdapterForResult(ArrayList<PageModelData> pageItemList) {
        mImageRecyclerView.setAdapter(new ImageResultAdapter(pageItemList));
    }

    // convert inputstream to String
    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

}
