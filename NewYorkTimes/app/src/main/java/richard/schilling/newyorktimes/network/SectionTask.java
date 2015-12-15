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

/**
 * Created by richard on 12/15/15.
 */
public class SectionTask extends AsyncTask<Void, Void, Throwable> {

    private final Context mContext;

    public SectionTask(Context ctx) {
        mContext = ctx;
    }

    @Override
    protected Throwable doInBackground(Void... voids) {

        try {

            URL url = new URL(Constants.API_URL_SECTIONS);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            InputStream is = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader inputBuffer = new BufferedReader(new InputStreamReader(is));

            StringBuilder sb = new StringBuilder();

            String inputLine = null;
            while ((inputLine = inputBuffer.readLine()) != null) {
                sb.append(inputLine);
            }

            SharedPreferences.Editor editor =
                    PreferenceManager.getDefaultSharedPreferences(mContext).edit();
            editor.putString(Constants.PREF_SECTION_CONTENT, sb.toString());
            editor.commit();

            mContext.sendBroadcast(new Intent(Constants.ACTION_SECTIONS_CACHED));

            urlConnection.disconnect();


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

        Toast.makeText(mContext, "Unable to download section list: " + t.getMessage(),
                Toast.LENGTH_SHORT).show();


    }
}
