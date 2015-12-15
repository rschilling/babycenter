package richard.schilling.newyorktimes;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import richard.schilling.newyorktimes.dummy.DummyContent;
import richard.schilling.newyorktimes.network.SectionTask;

/**
 * A list fragment representing a list of Articles. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link ArticleDetailFragment}.
 * <p/>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class ArticleListFragment extends ListFragment {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    private ArrayAdapter mAdapter;

    private BroadcastReceiver mReceiver;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(String id);
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(String id) {
        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArticleListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new ArrayAdapter<DummyContent.DummyItem>(
                getActivity(),
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1,
                DummyContent.ITEMS);

        // TODO: replace with a real list adapter.
        setListAdapter(mAdapter);


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_SECTIONS_CACHED);

        mReceiver = new UserInterfaceReceiver();
        getContext().registerReceiver(mReceiver, filter);


        // register broadcast receiver and request data.
        new SectionTask(activity).execute();

    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;

        getContext().unregisterReceiver(mReceiver);
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        mCallbacks.onItemSelected(DummyContent.ITEMS.get(position).id);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }



    private class UserInterfaceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(Constants.ACTION_SECTIONS_CACHED)){
                // parse the sections and update the list.
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                String sectionJson = prefs.getString(Constants.PREF_SECTION_CONTENT, null);

                if (sectionJson == null){
                    throw new IllegalStateException(
                            "ACTION_SECTIONS_CACHED received but no section data exists");

                }

                JSONObject sectionObject = null;
                try {
                    sectionObject = new JSONObject(sectionJson);
                    String status = sectionObject.getString("status");
                    if (!status.equals("OK")){
                        Toast.makeText(context, "unable to process sections.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    JSONArray resultArray = sectionObject.getJSONArray("results");
                    for (int i = 0; i < resultArray.length(); i++){
                        JSONObject cur = resultArray.getJSONObject(i);
                        Log.i("Fragment", "name: " + cur.getString("name"));
                    }
                } catch (JSONException e) {
                    Toast.makeText(context, "unable to process sections: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }





            }

        }
    }


}
