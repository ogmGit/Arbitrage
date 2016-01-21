package ogm.arbitrage;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class NewGame extends AppCompatActivity {
    Spinner spnCategories;
    Spinner spnCentralRef;
    Spinner spnAR1;
    Spinner spnAR2;
    String type = "";
    EditText txtGameNumber;
    EditText txtGameTime;
    EditText txtGameField;
    EditText txtPayed;
    EditText txtCheck;
    private static String categories[] = {};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //find views
        spnCategories = (Spinner) findViewById(R.id.spnCategories);
        spnCentralRef = (Spinner) findViewById(R.id.spnCentralRef);
        spnAR1 = (Spinner) findViewById(R.id.spnAR1);
        spnAR2 = (Spinner) findViewById(R.id.spnAR2);
        txtGameNumber = (EditText) findViewById(R.id.txtGameNumber);
        txtGameTime = (EditText) findViewById(R.id.txtGameTime);
        txtGameField = (EditText) findViewById(R.id.txtGameField);
        txtPayed = (EditText) findViewById(R.id.txtPayed);
        txtCheck= (EditText) findViewById(R.id.txtCheck);
        // set values
        txtGameNumber.setText(AssignGames.gameNumber);
        txtGameTime.setText(AssignGames.gameTime);
        txtGameField.setText(AssignGames.gameField);
        txtPayed.setText(AssignGames.gamePayed);
        txtCheck.setText(AssignGames.gameCheck);
        if (Globals.categories.length == 0) { type="CATREF="; connectionToServer("Yes");}
        else { setCategoryAddapter(); setRefAddapter(); }
        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }); */
    }

    private int findPosition(Spinner spn, String name) {
        int count = spn.getCount();
        int i;
        for (i=0; i<count; i++) {
            if (spn.getItemAtPosition(i).toString().equals(name)) { break; }
        }
        return i;
    }

    public void connectionToServer(String urlString) {
        String urlString1="";
    /* prepare your search string to be put in a URL. It might have reserved characters or something*/
        try { urlString1 = URLEncoder.encode(urlString, "UTF-8"); }
        catch (UnsupportedEncodingException e) {
        /* if this fails for some reason, let the user know why */
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show(); }
    /*create a client to perform networking*/
        AsyncHttpClient client = new AsyncHttpClient();
        //Have the client get a JSONArray of data and define how to respond*/
        client.get(Globals.QUERY_URL + type + urlString1, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                if (type.equals("ADD=") || type.equals("UPD=")) {
                    if (type.equals("ADD=")) {
                        Toast.makeText(getApplicationContext(), "Game Added Successful!", Toast.LENGTH_LONG).show();
                        Globals.action = "Add"; }
                    else {
                        Toast.makeText(getApplicationContext(), "Game Updated Successful!", Toast.LENGTH_LONG).show();
                        Globals.action = "Update";
                    }
                    AssignGames.gameNumber= txtGameNumber.getText().toString();
                    AssignGames.gameTime= txtGameTime.getText().toString();
                    AssignGames.gameField= txtGameField.getText().toString();
                    AssignGames.gameCheck= txtCheck.getText().toString();
                    AssignGames.gamePayed= txtPayed.getText().toString();
                    AssignGames.gameCategory=  spnCategories.getSelectedItem().toString();
                    AssignGames.gameCenter=  spnCentralRef.getSelectedItem().toString();
                    AssignGames.gameAR1=  spnAR1.getSelectedItem().toString();
                    AssignGames.gameAR2=  spnAR2.getSelectedItem().toString();
                    finish();
                }
                else {
                    fillSpinners(jsonObject.optJSONArray("categories"), "CAT");
                    fillSpinners(jsonObject.optJSONArray("referees"), "REF");
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable throwable, JSONObject error) {
                // Display a "Toast" message // to announce the failure
                Toast.makeText(getApplicationContext(), "Error: " + statusCode + " " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                // Log error message // to help solve any problems
                Log.e("omg android", statusCode + " " + throwable.getMessage());
            }
        });
    }

    private void fillSpinners(JSONArray jsonArray, String s) {
        /* display a Toast message to announce your success*/
        if (jsonArray.optString(0).contains("Error")) {
            Toast.makeText(getApplicationContext(), jsonArray.optString(0), Toast.LENGTH_LONG).show();
        } else {
            /*update the data in your custom method*/
            switch (s) {
                case "CAT":
                    categories = new String[jsonArray.length()];
                    for (int i = 0; i < jsonArray.length(); i++) { categories[i] = jsonArray.optString(i); }
                    setCategoryAddapter();
                    break;
                case "REF":
                    Globals.referees = new String[jsonArray.length()];
                    for (int i = 0; i < jsonArray.length(); i++) { Globals.referees[i] = jsonArray.optString(i); }
                    setRefAddapter();
                    break;
            }
        }
    }

    private void setCategoryAddapter() {
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(NewGame.this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spnCategories.setAdapter(adapter);
        if (Globals.action.equals("New")) {
            if (Games.games.size() > 0) { spnCategories.setSelection(0); }
        }
        else { spnCategories.setSelection(findPosition(spnCategories, AssignGames.gameCategory)); }
    }

    private void setRefAddapter() {
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(NewGame.this, android.R.layout.simple_spinner_item, Globals.referees);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spnCentralRef.setAdapter(adapter);
        spnAR1.setAdapter(adapter);
        spnAR2.setAdapter(adapter);
        //determine spinner position
        if (Globals.action.equals("New")) {
            if (Games.games.size() > 0) {
                spnCentralRef.setSelection(0);
                spnAR1.setSelection(0);
                spnAR2.setSelection(0);
            }
        }
        else {
            spnCentralRef.setSelection(findPosition(spnCentralRef, AssignGames.gameCenter));
            spnAR1.setSelection(findPosition(spnAR1, AssignGames.gameAR1));
            spnAR2.setSelection(findPosition(spnAR2, AssignGames.gameAR2));
        }
    }

    public void btnAddField_OnClick(View view) {
        switch (Globals.action) {
            case "New":
                type = "ADD=";
                break;
            case "Existing":
                type = "UPD=";
                break;
        }
        String month = AssignGames.txtMonth.getText().toString();
        String day = AssignGames.txtDay.getText().toString();
        if (Integer.parseInt(month) < 10) {
            month = "0" + month;
        }
        if (Integer.parseInt(day) < 10) {
            day = "0" + day;
        }
        String var = AssignGames.txtYear.getText().toString() + "," + month + "," + day + ",";
        var += txtGameNumber.getText().toString() + ",";
        var += txtGameTime.getText().toString() + ",";
        var += spnCategories.getSelectedItem().toString() + ",";
        var += spnCentralRef.getSelectedItem().toString() + ",";
        var += spnAR1.getSelectedItem().toString() + ",";
        var += spnAR2.getSelectedItem().toString() + ",";
        var += txtGameField.getText().toString() + ",";
        if (txtPayed.getText().toString().equals("")) { txtPayed.setText("No"); }
        var += txtPayed.getText().toString() + ",";
        if (txtCheck.getText().toString().equals("")) { txtCheck.setText("0"); }
        var += txtCheck.getText().toString();
        connectionToServer(var);
    }
}
