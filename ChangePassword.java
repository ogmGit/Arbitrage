package ogm.arbitrage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ChangePassword extends AppCompatActivity {
    public EditText phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        //take member phone number
        String mPhoneNumber = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();
        phone = (EditText) findViewById(R.id.txtPhone);
        phone.setText(mPhoneNumber);
    }

    public void changePassword(View view) {
        String urlString1 = "";
        phone = (EditText) findViewById(R.id.txtPhone);
        EditText oldPass = (EditText) findViewById(R.id.txtOldPassword);
        oldPass = (EditText) findViewById(R.id.txtOldPassword);
        EditText newPass = (EditText) findViewById(R.id.txtNewPassword);
        newPass = (EditText) findViewById(R.id.txtNewPassword);
        EditText confirmPass = (EditText) findViewById(R.id.txtConfirmPassword);
        confirmPass = (EditText) findViewById(R.id.txtConfirmPassword);

        if (!newPass.getText().toString().equals(confirmPass.getText().toString())) {
            showDialog("New Password and Confirm Password are different. Please try again");
            return;
        }
        /* prepare your search string to be put in a URL. It might have reserved characters or something*/
        String urlString = phone.getText() + ",";
        urlString = urlString + oldPass.getText() + "," + newPass.getText();
        try { urlString1 = URLEncoder.encode(urlString, "UTF-8"); }
        catch (UnsupportedEncodingException e) {
                /* if this fails for some reason, let the user know why */
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show(); }
        /*create a client to perform networking*/
        AsyncHttpClient client = new AsyncHttpClient();
        //Have the client get a JSONArray of data and define how to respond*/
        client.get(Globals.QUERY_URL + "CP=" + urlString1, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(JSONObject jsonObject) {
                    /* display a Toast message to announce your success*/
                if (jsonObject.optJSONArray("log").optString(0).contains("Error")) {
                    Toast.makeText(getApplicationContext(), jsonObject.optJSONArray("log").optString(0), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Change was Successful!", Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
                public void onFailure(int statusCode, Throwable throwable, JSONObject error) {
                // Display a "Toast" message
                // to announce the failure
                Toast.makeText(getApplicationContext(), "Error: " + statusCode + " " + throwable.getMessage(), Toast.LENGTH_LONG).show();

                // Log error message
                // to help solve any problems
                Log.e("omg android", statusCode + " " + throwable.getMessage());
            }
        });
    }

    private void showDialog(String msg) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(msg);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

}
