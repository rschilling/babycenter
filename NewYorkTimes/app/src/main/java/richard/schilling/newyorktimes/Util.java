package richard.schilling.newyorktimes;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by richard on 12/15/15.
 */
public  final class Util {
    private Util(){
        throw new UnsupportedOperationException("never instantiate");
    }

    public static boolean hasConnection(Context ctx){
        ConnectivityManager cm =
                (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

    }
}
