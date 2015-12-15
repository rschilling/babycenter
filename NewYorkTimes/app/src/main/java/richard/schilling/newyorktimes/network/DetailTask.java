package richard.schilling.newyorktimes.network;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import richard.schilling.newyorktimes.Constants;
import richard.schilling.newyorktimes.Util;

/**
 * Created by richard on 12/15/15.
 */
public class DetailTask extends AsyncTask<String, Void, Throwable> {

        private final Context mContext;
        private final URL mUrl;

        public DetailTask(Context ctx, URL url) {
                mContext = ctx;
                mUrl = url;
        }

        @Override
        protected Throwable doInBackground(String... sections) {

                if (!Util.hasConnection(mContext)){
                        return new IOException("no network connectivity");
                }

                try {


                        HttpURLConnection urlConnection = (HttpURLConnection) mUrl.openConnection();

                        InputStream is = new BufferedInputStream(urlConnection.getInputStream());
                        BufferedReader inputBuffer = new BufferedReader(new InputStreamReader(is));

                        StringBuilder sb = new StringBuilder();

                        String inputLine = null;
                        while ((inputLine = inputBuffer.readLine()) != null) {
                                sb.append(inputLine);
                        }

                        urlConnection.disconnect();

                        SharedPreferences.Editor editor =
                                PreferenceManager.getDefaultSharedPreferences(mContext).edit();
                        editor.putString(sections[0], sb.toString());
                        editor.commit();

                        Intent i = new Intent(Constants.ACTION_DETAIL_CACHED);
                        i.putExtra(Constants.KEY_SECTION_NAME, sections[0]);

                        mContext.sendBroadcast(i);


                } catch (MalformedURLException e) {
                        return e;

                } catch (IOException e) {
                        return e;
                }

                return null;
        }

        /**
         * If thrownable is null, then everything is O.K.
         *
         * @param t if an exception occurred during doInBackground this will be non-null.
         */
        @Override
        protected void onPostExecute(Throwable t) {

                // on the main thread - update the UI or show some issue.

                if (t != null) {
                        Toast.makeText(mContext, "Unable to download section list: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                }


        }
}
