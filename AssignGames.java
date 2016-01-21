package ogm.arbitrage;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class AssignGames extends AppCompatActivity {
    static EditText txtYear;
    static EditText txtMonth;
    static EditText txtDay;
    static String gameNumber;
    static String gameTime;
    static String gameCategory;
    static String gameCenter;
    static String gameAR1;
    static String gameAR2;
    static String gameField;
    static String gamePayed;
    static String gameCheck;
    private String type;

    View.OnClickListener btnDeleteListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //saveCurrentFieldsInfo();
            int dltNumber = (Integer) v.getTag();  //get row index
            int gameNumber = Games.games.get(dltNumber-1).gameNumber;
            Games.delGame(dltNumber - 1);           // delete game from array
            type = "DEL=";
            /*LinearLayout layout = (LinearLayout) layoutView;
            View textView = layout.getChildAt(1);
            TextView view = (TextView) textView;
            int dltNumber;
            try {
                dltNumber = Integer.parseInt(view.getText().toString());
                table.removeViewAt(dltNumber);
                //addTitles();
                //LoadFields();
            } catch (NumberFormatException nfe) {            }*/
            get_Games(type, txtYear.getText().toString() + "," + txtMonth.getText().toString() + "," + txtDay.getText().toString() + "," + Integer.toString(gameNumber), "Game Deleted Successfully");
            table.removeAllViews();                 // remove all of the views to reload
            LoadGames();                            // Reload the games
        }
    };

    View.OnClickListener rowListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TextView tv;
            //cast the view to Linear Layout
            LinearLayout ly = (LinearLayout) v;
            int i = (Integer) ly.getTag();
            Globals.rowNumber = i;
            //get game number text view
            tv = (TextView) ly.getChildAt(1);  gameNumber = tv.getText().toString();
            tv = (TextView) ly.getChildAt(2);  gameCategory = tv.getText().toString();
            tv = (TextView) ly.getChildAt(3);  gameTime = tv.getText().toString();
            tv = (TextView) ly.getChildAt(4);  gameCenter = tv.getText().toString();
            tv = (TextView) ly.getChildAt(5);  gameAR1 = tv.getText().toString();
            tv = (TextView) ly.getChildAt(6);  gameAR2= tv.getText().toString();
            tv = (TextView) ly.getChildAt(7);  gameField= tv.getText().toString();
            tv = (TextView) ly.getChildAt(8);  gamePayed= tv.getText().toString();
            tv = (TextView) ly.getChildAt(9);  gameCheck= tv.getText().toString();
            Globals.action = "Existing";
            setIntent();
        }
    };

    TableLayout table;
    TableRow tr;
    TableRow.LayoutParams llp = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    LinearLayout cell;
    TextView tv;
    EditText et;
    ShapeDrawable border = new ShapeDrawable(new RectShape());
    Button btnDelete;
    CheckBox chkBox;
    LinearLayout.LayoutParams Params1;
    int delWidth = 70;
    int numWidth = 50;
    int nameWidth = 150;
    int timeWidth = 90;
    int categoryWidth = 130;
    int height = 60;
    int chkWidth = 60;
    TextView view;
    Games.Game tempGame;

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        // Ignore orientation change to keep activity from restarting
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_games);
        setInitialTableStyles();
        txtYear = (EditText) findViewById(R.id.txtYear);
        txtMonth = (EditText) findViewById(R.id.txtMonth);
        txtDay = (EditText) findViewById(R.id.txtDay);
    }

    @Override
    public void onResume() {
        super.onResume();
        switch (Globals.action) {
            case "Add":
                addGame("add");
                break;
            case "Update":
                updateGame();
                break;
        }
        Globals.action="No";
    }

    public void updateGame() {
        TextView tv;
        TableRow tr = (TableRow) table.getChildAt(Globals.rowNumber);
        LinearLayout ly = (LinearLayout) tr.getChildAt(0);
        tv = (TextView) ly.getChildAt(1);  tv.setText(gameNumber);
        tv = (TextView) ly.getChildAt(2);  tv.setText(gameCategory);
        tv = (TextView) ly.getChildAt(3);  tv.setText(gameTime);
        tv = (TextView) ly.getChildAt(4);  tv.setText(gameCenter);
        tv = (TextView) ly.getChildAt(5);  tv.setText(gameAR1);
        tv = (TextView) ly.getChildAt(6);  tv.setText(gameAR2);
        tv = (TextView) ly.getChildAt(7);  tv.setText(gameField);
        tv = (TextView) ly.getChildAt(8);  tv.setText(gamePayed);
        tv = (TextView) ly.getChildAt(9);  tv.setText(gameCheck);
    }

    private void setInitialTableStyles() {
        table = (TableLayout) findViewById(R.id.tblLayout);
        llp.setMargins(0, 0, 2, 0);//2px right-margin
        border.getPaint().setStyle(Paint.Style.STROKE);
        border.getPaint().setColor(Color.BLACK);
    }

    private void addTitles() {
        //add new row
        setNewRow();
//Add New Cell
        setNewLinearLayout(0);
//add Delete Column
        addTextView(getString(R.string.delete), delWidth);
//add Game number Column
        addTextView(getString(R.string.gameNum), numWidth);
//add game category Column
        addTextView(getString(R.string.category), categoryWidth);
//add game time Column
        addTextView(getString(R.string.time), timeWidth);
//add center ref Column
        addTextView(getString(R.string.refereeName), nameWidth);
//add ar1 ref Column
        addTextView(getString(R.string.ar1name), nameWidth);
//add ar2 ref Column
        addTextView(getString(R.string.ar2name), nameWidth);
//add field number
        addTextView(getString(R.string.fieldNumber), numWidth);
//add field number
        addTextView(getString(R.string.gamePayed), chkWidth);
//add field number
        addTextView(getString(R.string.gameCheck), numWidth);
//add all columns to the row
        tr.addView(cell);
//add the row to the column.
        table.addView(tr);
    }

    private void LoadGames() {
        table.removeAllViews();
        addTitles();
        for (int i = 0; i< Games.games.size(); i++) {
            tempGame = new Games.Game();
            tempGame.gameNumber = Games.games.get(i).gameNumber;
            tempGame.gameCategory = Games.games.get(i).gameCategory;
            tempGame.gameTime = Games.games.get(i).gameTime;
            tempGame.centralName = Games.games.get(i).centralName;
            tempGame.ar1Name = Games.games.get(i).ar1Name;
            tempGame.ar2Name = Games.games.get(i).ar2Name;
            tempGame.gameField = Games.games.get(i).gameField;
            tempGame.gamePayed = Games.games.get(i).gamePayed;
            tempGame.checkNumber = Games.games.get(i).checkNumber;
            addTableRow(i+1, tempGame);
        }
    }

    public void addGame() {
        tempGame = new Games.Game();
        tempGame.gameNumber = 0;
        tempGame.gameCategory = "";
        tempGame.gameTime = "";
        tempGame.centralName = "";
        tempGame.ar1Name = "";
        tempGame.ar2Name = "";
        tempGame.gameField="";
        tempGame.gamePayed="";
        tempGame.checkNumber="";
        Games.addNewGame(tempGame);
    }

    public void addGame(String val) {
        tempGame = new Games.Game();
        tempGame.gameNumber = Integer.parseInt(gameNumber);
        tempGame.gameCategory = gameCategory;
        tempGame.gameTime = gameTime;
        tempGame.centralName = gameCenter;
        tempGame.ar1Name = gameAR1;
        tempGame.ar2Name = gameAR2;
        tempGame.gameField=gameField;
        tempGame.gamePayed=gamePayed;
        tempGame.checkNumber=gameCheck;
        Games.addNewGame(tempGame);
        addTableRow(Games.games.size(), tempGame);
    }

    public void btnNewGame_onClick(View view) throws ClassNotFoundException {
        gameNumber = "";
        gameCategory = "";
        gameTime = "";
        gameCenter = "";
        gameAR1 = "";
        gameAR2= "";
        gameField= "";
        gamePayed= "";
        gameCheck= "";
        Globals.action = "New";
        setIntent();
    }

    private void setIntent() {
        Intent intent;
        intent = new Intent(this, NewGame.class);
        startActivity(intent);
    }

    public void tblLayout_OnClick(View view) {
        setIntent();
    }

    private void addTableRow(int i, Games.Game newField) {
        //new row
        setNewRow();
        //New Cell
        setNewLinearLayout(i);
        //add delete button
        addBtnDelete(i);
        //add fieldnumber
        addTextView(Integer.toString(newField.gameNumber), numWidth);
        //add field name
        addTextView(newField.gameCategory, categoryWidth);
        //add field Area
        addTextView(newField.gameTime, timeWidth);
        //add field average slope
        addTextView(newField.centralName, nameWidth);
        //add field average slope
        addTextView(newField.ar1Name, nameWidth);
        //add field average slope
        addTextView(newField.ar2Name, nameWidth);
        //add field number
        addTextView(newField.gameField, numWidth);
        //add field number
        addTextView(newField.gamePayed, chkWidth);
        //add field number
        addTextView(newField.checkNumber, numWidth);
        //add all columns to the row
        tr.addView(cell);
        //add the row to the column.
        if (table.getChildCount()==0) { addTitles(); }
        table.addView(tr, i);
    }

    private void setNewLinearLayout(int i) {
        cell = new LinearLayout(this);
        cell.setBackgroundColor(Color.WHITE);
        cell.setLayoutParams(llp);//2px border on the right for the cell
        cell.setTag(i);
        cell.setOnClickListener(rowListener);
    }

    private void setNewRow() {
        tr = new TableRow(this);
        tr.setBackgroundColor(Color.BLACK);
        tr.setPadding(0, 0, 0, 2); //Border between rows
        //tr.setOnClickListener(rowListener);
    }

    private void addBtnDelete(int i) {
        btnDelete = new Button(this);
        btnDelete.setText(R.string.delete);
        btnDelete.setPadding(0, 0, 4, 3);
        btnDelete.setBackground(border);
        btnDelete.setOnClickListener(btnDeleteListener);
        btnDelete.setTag(i);
        Params1 = new LinearLayout.LayoutParams(Globals.convertDpToPixel(delWidth, this), height);
        btnDelete.setLayoutParams(Params1);
        cell.addView(btnDelete);
    }

    private void addTextView(String text, int width) {
        tv = new TextView(this);
        tv.setTextColor(Color.BLUE);
        tv.setPadding(0, 0, 4, 3);
        if (width == numWidth) {
            tv.setGravity(Gravity.RIGHT);
            tv.setPadding(0,0,10,0);
        }
        tv.setText(text);
        Params1 = new LinearLayout.LayoutParams(Globals.convertDpToPixel(width, this), height);
        tv.setLayoutParams(Params1);
        //tv.setBackground(border);
        cell.addView(tv);
    }

    private void getTextView(int index, int child) {
        View rowView = table.getChildAt(index);
        TableRow row = (TableRow) rowView;
        View layoutView = row.getChildAt(0);
        LinearLayout layout = (LinearLayout) layoutView;
        View textView = layout.getChildAt(child);
        view = (TextView) textView;
    }

    private void saveCurrentFieldsInfo() {
        //clear current fields to save whatever is coming from table
        Games.clear();
        for (int i=1; i < table.getChildCount(); i++) {
            //add a new field to Games.games
            addGame();
            // get game number
            getTextView(i, 0);
            Games.games.get(i - 1).gameNumber = Integer.parseInt(view.getText().toString());
            //get category
            getTextView(i, 1);
            Games.games.get(i-1).gameCategory= view.getText().toString();
            //get game time
            getTextView(i, 2);
            Games.games.get(i-1).gameTime= view.getText().toString();
            //get center ref
            getTextView(i, 3);
            Games.games.get(i-1).centralName = view.getText().toString();
            //get ar1
            getTextView(i, 4);
            Games.games.get(i-1).ar1Name = view.getText().toString();
            //get ar2
            getTextView(i, 5);
            Games.games.get(i - 1).ar2Name = view.getText().toString();
            //get field n umber
            getTextView(i, 6);
            Games.games.get(i - 1).gameField = view.getText().toString();
            //get field n umber
            getTextView(i, 7);
            Games.games.get(i - 1).gamePayed = view.getText().toString();
            //get field n umber
            getTextView(i, 8);
            Games.games.get(i - 1).checkNumber = view.getText().toString();
        }
    }

    public void btnSubmit_onClick(View view) {
        saveCurrentFieldsInfo();
    }

    public void btnLoadGames_onClick(View view){
        txtYear = (EditText) findViewById(R.id.txtYear);
        txtMonth = (EditText) findViewById(R.id.txtMonth);
        txtDay = (EditText) findViewById(R.id.txtDay);
        type = "LGS=";
        get_Games(type, txtYear.getText().toString() + "," + txtMonth.getText().toString() + "," + txtDay.getText().toString(), "Games on this date");
        LinearLayout lytButtons = (LinearLayout) findViewById(R.id.lytButtons);
        lytButtons.setVisibility(View.VISIBLE);
    }

    public void get_Games(final String queryType, String urlString, final String msg) {
        String urlString1 = "";
        /* prepare your search string to be put in a URL. It might have reserved characters or something*/
        //String urlString = "1";
        //queryType = "Seasons=";
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
                if (jsonObject.optString("games").contains("Error")) {
                    if (type.equals("LGS=")) {
                        Games.games.clear(); table.removeAllViews(); }
                    Toast.makeText(getApplicationContext(), jsonObject.optString("games"), Toast.LENGTH_LONG).show();
                } else {
                    switch (type) {
                        case "LGS=":
                            //clear the current array list ot be sure games are not duplciated.
                            Games.games.clear();
                            //clear the table layout with games to be sure the games are no duplicated on screen
                            table.removeAllViews();
                            JSONObject game;
                            //addTitles();                //add titles again because the views had been removed
                            for (int i = 0; i < jsonObject.optJSONArray("games").length(); i++) {
                                try {
                                    game = jsonObject.optJSONArray("games").getJSONObject(i);
                                    UpdateGamesList(game, i);    //move games to array list
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            LoadGames();                //Load games into tabla layout
                            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                            break;
                        case "DEL=":
                            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                    }
                }
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

    private void  UpdateGamesList(JSONObject game, int i) {
        /* Load games into Games.games arrayList */
        addGame();
        // get game number
        Games.games.get(i).gameNumber = Integer.parseInt(game.optString("Game"));
        //get category
        Games.games.get(i).gameCategory= game.optString("U");
        //get game time
        Games.games.get(i).gameTime= game.optString("Time");
        //get center ref
        Games.games.get(i).centralName = game.optString("CR");
        //get ar1
        Games.games.get(i).ar1Name = game.optString("AR1");
        //get ar2
        Games.games.get(i).ar2Name = game.optString("AR2");
        //get field number
        Games.games.get(i).gameField = game.optString("FieldId");
        //get field number
        Games.games.get(i).gamePayed= game.optString("Pay");
        //get field number
        Games.games.get(i).checkNumber= game.optString("Check");
    }

    private void showDialog(String msg) {
        AlertDialog alertDialog = new AlertDialog.Builder(AssignGames.this).create();
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