package ogm.arbitrage;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.widget.Spinner;

/*** Created by ogallego on 10/26/2015.*/
/* Define the global variables */
public class Globals {
    public static String login = "No";
    public static String name = "";
    public static String memberType = "";
    public static String phone = "";
    public static String eMail = "";
    public static String password = "";
    public static String grade = "";
    public static String expiratioDate = "";
    public static String associatoinCode = "";
    public static String associatoinName = "";
    public static String associatoinAddress = "";
    public static final String QUERY_URL = "http://nn.tarleton.edu/refs/refHandler.ashx?";
    public static String categories[] = {};
    public static String referees[] = {};
    public static String action="No";
    public static int rowNumber;

    public static int convertDpToPixel(int widthDP, Context context) {
        Resources res = context.getResources();
        DisplayMetrics metrics = res.getDisplayMetrics();
        float pxFloat = widthDP * (metrics.densityDpi / 160f);
        int px = (int)(float)Float.valueOf(pxFloat);
        return px;
    }

}
