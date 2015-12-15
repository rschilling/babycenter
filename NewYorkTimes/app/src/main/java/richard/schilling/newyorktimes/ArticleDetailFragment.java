package richard.schilling.newyorktimes;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;

import richard.schilling.newyorktimes.dummy.DummyContent;
import richard.schilling.newyorktimes.network.DetailTask;

/**
 * A fragment representing a single Article detail screen.
 * This fragment is either contained in a {@link ArticleListActivity}
 * in two-pane mode (on tablets) or a {@link ArticleDetailActivity}
 * on handsets.
 */
public class ArticleDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private DummyContent.DummyItem mItem;

    private BroadcastReceiver mReceiver;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArticleDetailFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.content);
            }

            try {
                new DetailTask(getContext(),
                        new URL("http://api.nytimes.com/svc/mostpopular/v2/mostviewed/Food/1.json?api-key=a0214f17473349c11c3c7d70d7dde927:16:73780352"))
                        .execute(mItem.content);
            } catch (MalformedURLException e) {
                // do nothing.
            }

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_article_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.article_detail)).setText(mItem.details);
        }

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);


        mReceiver = new DetailReceiver();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_DETAIL_CACHED);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        getContext().registerReceiver(mReceiver, filter);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        getContext().unregisterReceiver(mReceiver);
    }

    private class DetailReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                if (!Util.hasConnection(context)) {
                    Toast.makeText(context, "No network connectivity.", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
            if (intent.getAction().equals(Constants.ACTION_DETAIL_CACHED)) {

                String section = intent.getStringExtra(Constants.KEY_SECTION_NAME);
                Log.i("FragmentDetail", "section " + section);

            }

        }
    }

}
