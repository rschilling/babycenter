package richard.schilling.newyorktimes;

/**
 * Created by richard on 12/15/15.
 */
public final class Constants {

    public static final String ACTION_DETAIL_CACHED =
            "richard.schilling.newyorktimes.ACTION_DETAIL_CACHED";

    public static final String KEY_SECTION_NAME = "KEY_SECTION";

    // TODO: parameterize URL strings.

    public static String API_KEY = "a0214f17473349c11c3c7d70d7dde927:16:73780352";

    public static String API_URL_SECTIONS =
            "http://api.nytimes.com/svc/mostpopular/v2/mostviewed/sections-list.json?api-key="
                    + API_KEY;
    public static String PREF_SECTION_CONTENT = "PREF_SECTIONS_JSON";

    /**
     * Broadcast whenever the sections data is cached in shared preferences.
     */
    public static String ACTION_SECTIONS_CACHED =
            "richard.schilling.newyorktimes.ACTION_SECTIONS_CACHED";


    private Constants() {
        throw new UnsupportedOperationException("never instantiate this class.");
    }


}
