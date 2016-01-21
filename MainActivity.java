package ogm.arbitrage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    public EditText phone1;
    public EditText pass1;
    public Button btnForget;
    public Button btnSignIn;
    public Button btnRegister;
    public TextView lblWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setContentView(R.layout.activity_register);
        //Context mAppContext = null;
        // TelephonyManager tMgr = (TelephonyManager) mAppContext.getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();
        phone1 = (EditText) findViewById(R.id.txtPhone);
        phone1.setText(mPhoneNumber);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        setMenu(menu);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Globals.login != "No") { showWelcome(); }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        setMenu(menu);
        return true;
    }

    private void setMenu(Menu menu) {
        switch (Globals.memberType){
            case "Assignor":
                menu.findItem(R.id.mnuGames).setVisible(false);
                menu.findItem(R.id.mnuTransfer).setVisible(false);
                menu.findItem(R.id.mnuPassword).setVisible(true);
                menu.findItem(R.id.mnuModify).setVisible(true);
                menu.findItem(R.id.mnuAssociation).setVisible(true);
                menu.findItem(R.id.mnuAssignGames).setVisible(true);
                break;
            case "Referee":
                menu.findItem(R.id.mnuAssociation).setVisible(false);
                menu.findItem(R.id.mnuAssignGames).setVisible(false);
                menu.findItem(R.id.mnuPassword).setVisible(true);
                menu.findItem(R.id.mnuModify).setVisible(true);
                menu.findItem(R.id.mnuGames).setVisible(true);
                menu.findItem(R.id.mnuTransfer).setVisible(true);
                break;
            default:
                menu.findItem(R.id.mnuPassword).setVisible(false);
                menu.findItem(R.id.mnuModify).setVisible(false);
                menu.findItem(R.id.mnuGames).setVisible(false);
                menu.findItem(R.id.mnuTransfer).setVisible(false);
                menu.findItem(R.id.mnuAssociation).setVisible(false);
                menu.findItem(R.id.mnuAssignGames).setVisible(false);
                break;
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent;
        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            case R.id.mnuPassword:
                intent = new Intent(this, ChangePassword.class);
                startActivity(intent);
                return true;
            case R.id.mnuModify:
                intent = new Intent(this, Register.class);
                intent.putExtra("activity", "Modify");
                startActivity(intent);
                return true;
            case R.id.mnuAssignGames:
                intent = new Intent(this, AssignGames.class);
                intent.putExtra("activity", "AssignGames");
                startActivity(intent);
            case R.id.action_settings:
                //perform settings
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void activate_Register(View view) {
        Intent intent = new Intent(this, Register.class);
        intent.putExtra("activity", "Register");
        startActivity(intent);
    }

    public void Log_In(View view) {
        String urlString1 = "";
        phone1 = (EditText) findViewById(R.id.txtPhone);
        pass1 = (EditText) findViewById(R.id.txtPassword);
        /* prepare your search string to be put in a URL. It might have reserved characters or something*/
        String urlString = phone1.getText().toString() + ",";
        urlString = urlString + pass1.getText().toString();
        try { urlString1 = URLEncoder.encode(urlString, "UTF-8"); }
        catch (UnsupportedEncodingException e) {
            /* if this fails for some reason, let the user know why */
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show(); }
        /*create a client to perform networking*/
        AsyncHttpClient client = new AsyncHttpClient();
        //Have the client get a JSONArray of data and define how to respond*/
        client.get(Globals.QUERY_URL + "L=" + urlString1, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(JSONObject jsonObject) {
                /* display a Toast message to announce your success*/
                if (jsonObject.optJSONArray("log").optString(0).contains("Error")) {
                    Toast.makeText(getApplicationContext(), jsonObject.optJSONArray("log").optString(0), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Log in was Successful!", Toast.LENGTH_LONG).show();
                    /*update the data in your custom method*/
                    //mJSONAdapter.updateData(jsonObject.optJSONArray("refs"));
                    Globals.login = "Yes";
                    Globals.name = jsonObject.optJSONArray("log").optString(0);
                    Globals.memberType = jsonObject.optJSONArray("log").optString(1);
                    Globals.phone = jsonObject.optJSONArray("log").optString(2);
                    Globals.eMail = jsonObject.optJSONArray("log").optString(4);
                    Globals.password = jsonObject.optJSONArray("log").optString(3);
                    Globals.grade = jsonObject.optJSONArray("log").optString(5);
                    Globals.expiratioDate = jsonObject.optJSONArray("log").optString(6);
                    Globals.associatoinCode = jsonObject.optJSONArray("log").optString(7);
                    Globals.associatoinName = jsonObject.optJSONArray("log").optString(8);
                    Globals.associatoinAddress = jsonObject.optJSONArray("log").optString(9);
                    showWelcome();
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

    private void showWelcome() {
        phone1.setVisibility(View.INVISIBLE);
        pass1 = (EditText) findViewById(R.id.txtPassword);
        pass1.setVisibility(View.INVISIBLE);
        btnSignIn = (Button) findViewById(R.id.btnLogIn);
        btnSignIn.setVisibility(View.INVISIBLE);
        btnForget = (Button) findViewById(R.id.btnForget);
        btnForget.setVisibility(View.INVISIBLE);
        btnRegister = ( Button) findViewById(R.id.btnRegister);
        btnRegister.setVisibility(View.INVISIBLE);
        lblWelcome = (TextView) findViewById(R.id.lblWelcome);
        lblWelcome.setText("Welcome! " + Globals.name );
        lblWelcome.setVisibility(View.VISIBLE);
    }

    public void sendNewPassword(View view) {
        String urlString1 = "";
        phone1 = (EditText) findViewById(R.id.txtPhone);
        /* prepare your search string to be put in a URL. It might have reserved characters or something*/
        String urlString = phone1.getText().toString();
        try { urlString1 = URLEncoder.encode(urlString, "UTF-8"); }
        catch (UnsupportedEncodingException e) {
            /* if this fails for some reason, let the user know why */
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show(); }
        /*create a client to perform networking*/
        AsyncHttpClient client = new AsyncHttpClient();
        //Have the client get a JSONArray of data and define how to respond*/
        client.get(Globals.QUERY_URL + "FP=" + urlString1, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(JSONObject jsonObject) {
                /* display a Toast message to announce your success*/
                if (jsonObject.optString("created").contains("Error")) {
                //if (jsonObject.optJSONArray("new").optString(0).contains("Error")) {
                    Toast.makeText(getApplicationContext(), jsonObject.optString("new"), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Process was Successful!", Toast.LENGTH_LONG).show();
                    /*inform about the email sent with the new password.
                    //mJSONAdapter.updateData(jsonObject.optJSONArray("refs")); */
                    showDialog("Your new password has been sent to the email address in your registration");            }
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
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
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
