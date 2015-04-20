package dev.cat.encryptingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class MessageListActivity extends SwipeListViewActivity {

    static final String ACTION_UPDATE_VIEW = "dev.cat.action.UPDATEVIEW";

    private ListView mListView; // A view that shows items in a vertically scrolling list.
    private ArrayAdapter<String> mAdapter; // A concrete BaseAdapter(An Adapter object acts as a bridge
    // between an AdapterView and the underlying data for that view.) that is backed by an array of arbitrary objects.
    MessageList ml = HomeActivity.ml;
    private static final int CHAR_LIMIT = 28; // Character limit is 28
    private Button newBtn; // used to create new messages


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        newBtn  = (Button) findViewById(R.id.newBtn);

        newBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onNew();
            }
        });


        // Finds the ListView resource.
        mListView = (ListView) findViewById(R.id.mainListView );

        updateView();

    }


    private void updateView(){
        ArrayList<String> listOfText = ml.getListOfMessages(); //the list of all messages' texts
        ArrayList<String> trimmedList = new ArrayList<>(); //reformatted list
        //ArrayList<String> listOfNames = ml.getLIstOfNames(); //for debugging

        for (int i = 0; i < listOfText.size(); i++){

            String text = listOfText.get(i);

            //remove all newline characters
            text = text.replace("\n", "").replace("\r", "");

            //trim the string if its too long
            if (listOfText.get(i).length() > CHAR_LIMIT) {
                trimmedList.add(i+1 + ": " + text.substring(0, CHAR_LIMIT) + "...");
            }
            else
                trimmedList.add(i+1 + ": " + text);
        }

        mAdapter = new ArrayAdapter<String>(this,
                R.layout.simple_row, trimmedList);

        mListView.setAdapter(mAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_message_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public ListView getListView() {
        return mListView;
    }

    @Override
    public void getSwipeItem(boolean isRight, int position) {
        Toast.makeText(this,
                "Message " + (position + 1)  + " was deleted",
                Toast.LENGTH_SHORT).show();
        ml.goTo(position);
        ml.delete();
        updateView();
    }

    @Override
    public void onItemClickListener(ListAdapter adapter, int position) {
        Toast.makeText(this, "Message " + (position + 1) + " was chosen",
                Toast.LENGTH_SHORT).show();
        ml.goTo(position);
        Intent i = new Intent(ACTION_UPDATE_VIEW);
        i.setClass(getBaseContext(), HomeActivity.class);
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
        finish();
    }

    public void onNew()
    {
        ml.newMessage();
        Intent i = new Intent(ACTION_UPDATE_VIEW);
        i.setClass(getBaseContext(), HomeActivity.class);
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
        finish();  // Finishes this activity
    }

}
