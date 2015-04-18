package dev.cat.encryptingapp;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class MessageListActivity extends SwipeListViewActivity {

    /*
    private ListView mainListView ;
    private ArrayAdapter<String> listAdapter;
    */

    private ListView mListView;
    private ArrayAdapter<String> mAdapter;
    MessageList ml = HomeActivity.ml;
    private static final int CHAR_LIMIT = 28;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        /*
        // Finds the ListView resource.
        mainListView = (ListView) findViewById( R.id.mainListView );

        // Makes a list to put it in message
        String[] animals = new String[] { "Cat", "Dog", "Bird", "Reptile",
                "Weasel", "Squirrel", "Whale", "Seagul"};
        ArrayList<String> animalList = new ArrayList<String>();
        animalList.addAll( Arrays.asList(animals) );

        // Create ArrayAdapter A concrete BaseAdapter that is backed by an array of arbitrary objects
        listAdapter = new ArrayAdapter<String>(this, R.layout.simple_row, animalList);

        // Add more animals.
        listAdapter.add( "Chicken" );
        listAdapter.add( "Rooster" );
        listAdapter.add( "Ferret" );
        listAdapter.add( "Parrot" );
        listAdapter.add( "Seahawk" );

        // Set the ArrayAdapter as the ListView's adapter.
        mainListView.setAdapter( listAdapter );
        */

        // Finds the ListView resource.
        mListView = (ListView) findViewById(R.id.mainListView );

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
                "Swipe to " + (isRight ? "right" : "left") + " direction",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClickListener(ListAdapter adapter, int position) {
        Toast.makeText(this, "Single tap on item position " + position,
                Toast.LENGTH_SHORT).show();
    }

}
