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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Register extends AppCompatActivity {
    //EditText phone = (EditText) findViewById(R.id.phone_number);
    public EditText phone;
    public EditText name;
    public EditText memberType;
    public EditText eMail;
    public EditText password;
    public EditText grade;
    public EditText expirationDate;
    public EditText association;
    public EditText associationAddress;
    public EditText associationName;
    public RadioButton rdReferee;
    public RadioButton rdAssignor;
    public String value = "";
    public String queryType="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //Get phone number.
        String mPhoneNumber = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();
        phone = (EditText) findViewById(R.id.phone_number);
        phone.setText(mPhoneNumber);
        if (name==null) { name = (EditText) findViewById(R.id.referee_name); }
        if (eMail==null) { eMail = (EditText) findViewById(R.id.email_address); }
        if (password==null) { password= (EditText) findViewById(R.id.password); }
        if (grade==null) { grade = (EditText) findViewById(R.id.grade); }
        if (expirationDate==null) { expirationDate = (EditText) findViewById(R.id.expiration_date); }
        if (association==null) { association = (EditText) findViewById(R.id.association); }
        if (associationName==null) { associationName = (EditText) findViewById(R.id.associationName); }
        if (associationAddress==null) { associationAddress = (EditText) findViewById(R.id.associationAddress); }
        if (rdAssignor==null) { rdAssignor = (RadioButton) findViewById(R.id.rdbAssignor); }
        if (rdReferee==null) { rdReferee = (RadioButton) findViewById(R.id.rdbReferee); }
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            value = extras.getString("activity").toString();
            String modify = "Modify";
            queryType = "R";
            if (value.equals(modify)) {
                queryType = "M";
                rdAssignor.setEnabled(false);
                rdReferee.setEnabled(false);
                password.setEnabled(false);
                phone.setEnabled(false);
                association.setEnabled(false);
                assignValues();
            }
        }
    }

    public void submit_Register(View view) {
        String urlString1 = "";
        /* prepare your search string to be put in a URL. It might have reserved characters or something*/
        String urlString = name.getText() + ",";
        //EditText phone = (EditText) findViewById(R.id.phone_number);
        urlString = urlString + phone.getText() + ",";
        final EditText email = (EditText) findViewById(R.id.email_address);
        urlString= urlString + email.getText() + ",";
        final EditText passw = (EditText) findViewById(R.id.password);
        urlString = urlString + passw.getText() + ",";
        final EditText grade = (EditText) findViewById(R.id.grade);
        urlString = urlString + grade.getText() + ",";
        EditText expiration = (EditText) findViewById(R.id.expiration_date);
        urlString = urlString + expiration.getText() + ",";
        final EditText association = (EditText) findViewById(R.id.association);
        urlString = urlString + association.getText() + ",";
        RadioGroup rdbType = (RadioGroup) findViewById(R.id.rdgType);
        final RadioButton rb = (RadioButton) findViewById(rdbType.getCheckedRadioButtonId());
        if (rb == null || name.getText().toString() == "") {
            showDialog("You should select Assignor or Referee");
            return;
        }
        urlString = urlString + rb.getText();
        if (Globals.memberType.equals("Assignor")) {
            queryType = queryType + "A=";
            EditText associationName = (EditText) findViewById(R.id.associationName);
            urlString = urlString + "," + associationName.getText();
            EditText associationAddress = (EditText) findViewById(R.id.associationAddress);
            urlString = urlString + "," + associationAddress.getText();
        }
        else { queryType = queryType + "R="; }

        String msg = validateEntries(urlString);
        if (msg != "OK") {
            showDialog(msg);
            return;
        }

        try { urlString1 = URLEncoder.encode(urlString, "UTF-8"); }
        catch (UnsupportedEncodingException e) {
            /* if this fails for some reason, let the user know why */
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show(); }
        /*create a client to perform networking*/
        AsyncHttpClient client = new AsyncHttpClient();
        //Have the client get a JSONArray of data and define how to respond*/
        client.get(Globals.QUERY_URL + queryType + urlString1, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(JSONObject jsonObject) {
                /* display a Toast message to announce your success*/
                if (jsonObject.optString("reg").contains("OK")) {
                    Toast.makeText(getApplicationContext(), "Registration was Successful!", Toast.LENGTH_LONG).show();
                /*update the data in your custom method*/
                    //mJSONAdapter.updateData(jsonObject.optJSONArray("refs"));
                    Globals.login = "Yes";
                    Globals.name = name.getText().toString();
                    Globals.memberType=rb.getText().toString();
                    Globals.phone=phone.getText().toString();
                    Globals.eMail=eMail.getText().toString();
                    Globals.password=password.getText().toString();
                    Globals.grade=grade.getText().toString();
                    Globals.expiratioDate=expirationDate.getText().toString();
                    Globals.associatoinCode=association.getText().toString();
                    Globals.associatoinName=associationName.getText().toString();
                    Globals.associatoinAddress=associationAddress.getText().toString();
                    finish();
                }
                else { Toast.makeText(getApplicationContext(), jsonObject.optJSONArray("reg").optString(0), Toast.LENGTH_LONG).show(); }
            }

            @Override
            public void onFailure(int statusCode, Throwable throwable, JSONObject error) {
                // Display a "Toast" message to announce the failure
                Toast.makeText(getApplicationContext(), "Error: " + statusCode + " " + throwable.getMessage(), Toast.LENGTH_LONG).show();

                // Log error message to help solve any problems
                Log.e("omg android", statusCode + " " + throwable.getMessage());
            }
        });
    }

    private void showDialog(String msg) {
        AlertDialog alertDialog = new AlertDialog.Builder(Register.this).create();
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

    private String validateEntries(String urlString){
        String msg="";
        String[] separated = urlString.split(",");
        for (int i=0; i<=separated.length-1; i++) {
            if (separated[i].length() == 0) {
                switch (i) {
                    case 0: msg = "Enter Name - "; break;
                    case 1: msg = msg + "Enter Phone - "; break;
                    case 2: msg = msg + "Enter Email - "; break;
                    case 3: msg = msg + "Enter Password - "; break;
                    case 4: msg = msg + "Enter Grade - "; break;
                    case 5: msg = msg + "Enter Expiration Date - "; break;
                    case 6: msg = msg + "Enter Valid Association - "; break;
                    case 7: msg = msg + "Select Assignor or Referee - "; break;
                    case 8: msg = msg + "Enter Association Name - "; break;
                    case 9: msg = msg + "Enter Association Address"; break;
                }
            }
        }
        if (msg != "") { return msg; }
        else { return "OK"; }
    }

    public void getRefereeMember(View view) { getRefereeControls(); }

    private void getRefereeControls() {
        associationAddress.setVisibility(View.INVISIBLE);
        associationName.setVisibility(View.INVISIBLE);
        Globals.memberType="Referee";
    }

    private void getAssignorControls() {
        associationAddress.setVisibility(View.VISIBLE);
        associationName.setVisibility(View.VISIBLE);
        Globals.memberType="Assignor";
    }

    public void getAssignorMember(View view) { getAssignorControls(); }

    private void assignValues() {
        name.setText(Globals.name);
        phone.setText(Globals.phone);
        eMail.setText(Globals.eMail);
        password.setText(Globals.password);
        grade.setText(Globals.grade );
        expirationDate.setText(Globals.expiratioDate);
        association.setText(Globals.associatoinCode);
        associationName.setText(Globals.associatoinName);
        associationAddress.setText(Globals.associatoinAddress);
        if (Globals.memberType.equals("Referee")) { rdReferee.setChecked(true); rdAssignor.setChecked(false); getRefereeControls();}
        else { rdReferee.setChecked(false); rdAssignor.setChecked(true);  getAssignorControls(); }
        return;
    }
}
