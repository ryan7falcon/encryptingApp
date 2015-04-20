package dev.cat.encryptingapp;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Class: HomeActivity
 * Author: Ryan
 *
 * Main Activity of encrypting app where the user can create a message,
 * encrypt/decrypt it, delete it, navigate to the next/previous one
 * and open a MessageListActivity to view all messages as a list
 */



public class HomeActivity extends Activity {

    //field variables
    private TextView label; // Label to display the title
    private EditText msgField; // Displays  the message field for the message being displayed
    private EditText keyField; // Takes the encrypting key password
    private Button listBtn; // List button which will take you to the list of messages activity
    private Button newBtn; // New button to create a new message
    private Button saveBtn; // Save button which will save the message
    private Button deleteBtn; // Delete button to delete the message
    private Button nextBtn; // Next Button which goes to the next message
    private Button previousBtn; // Previous Button which goes back to previous message
    private Button encryptBtn; // Encrypt button which encrypts the message
    private Button decryptBtn; // Decrypt button which decrypts the message

    //static and package visibility to make it accessible from MessageListActivity
    static MessageList ml;

    // First default message to be displayed
    private static final String FIRST_MESSAGE = "This is your first message. Enter any key and press 'Encrypt' button.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ml = new MessageList(getApplicationContext());

        label = (TextView) findViewById(R.id.msgLabel);
        msgField = (EditText) findViewById(R.id.msgField);
        keyField = (EditText) findViewById(R.id.keyField);
        listBtn = (Button) findViewById(R.id.listBtn);
        newBtn  = (Button) findViewById(R.id.newBtn);
        nextBtn = (Button) findViewById(R.id.nextBtn);
        previousBtn = (Button) findViewById(R.id.previousBtn);
        deleteBtn = (Button) findViewById(R.id.deleteBtn);
        saveBtn = (Button) findViewById(R.id.saveBtn);
        encryptBtn = (Button) findViewById(R.id.encryptBtn);
        decryptBtn = (Button) findViewById(R.id.decryptBtn);

        //broadcast receiver for Intents
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("dev.cat.action.UPDATEVIEW"));


        newBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onNew();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onDelete();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onSave();
            }
        });

        listBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onList();
            }
        });

        encryptBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onEncrypt();
            }
        });

        decryptBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onDecrypt();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onNext();
            }
        });

        previousBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onPrevious();
            }
        });

        //create first message if nothing is loaded from text files
        if(!ml.load()){
            ml.newMessage();
            ml.setText(FIRST_MESSAGE);
        }
        updateView();
    }

    /**
     *  Asks the message list to switch to the next method and updates the information on screen.
     */
    private void onNext() {
        onSave();
        if(!ml.checkNext()){
            AlertDialog dlg = new AlertDialog.Builder(this).create();
            dlg.setTitle("End of a list");
            dlg.setMessage("This is a last message in the list. Go to the first one?");
            dlg.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int which){
                            ml.goToFirst();  updateView();}
                    }
            );
            dlg.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel", (DialogInterface.OnClickListener)null);
            dlg.show(); // Does not wait for dialog!
        }
        else{
            ml.goToNext();
        }
        updateView();

    }

    /**
     * Event handler for received intents, called when "dev.cat.action.UPDATEVIEW" intent is broadcasted
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateView();
        }
    };

    /**
     * called when activity is being destroyed
     */
    @Override
    protected void onDestroy(){
        //unregister receiver because the activity is about to be closed
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    /**
     * Asks the message list to switch to the previous method and updates the information on screen.
     */
    private void onPrevious(){
        onSave();
        if(!ml.checkPrevious()){
            AlertDialog dlg = new AlertDialog.Builder(this).create();
            dlg.setTitle("End of a list");
            dlg.setMessage("This is a first message in the list. Go to the last one?");
            dlg.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int which){
                           ml.goToLast();  updateView();}
                    }
            );
            dlg.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel", (DialogInterface.OnClickListener)null);
            dlg.show(); // Does not wait for dialog!
        }
        else{
            ml.goToPrevious();
        }
        updateView();
    }

    /**
     * Saves the message
     */
    private void onSave() {
       ml.save(msgField.getText().toString());
    }

    /**
     * called when "List" button is pressed
     * Opens MessageListActivity
     */
    private void onList() {
        onSave();
        Intent i = new Intent(getBaseContext(), MessageListActivity.class);
        startActivity(i);
    }

    /**
     * called when "Encrypt" button is pressed
     * Encrypts the message
     */
    private void onEncrypt() {

       onSave();
       ml.encrypt(keyField.getText().toString());
       updateView();
       onSave();
    }

    /**
     * called when "Decrypt" button is pressed
     * Decrypts the message
     */
    private void onDecrypt() {
        onSave();
        ml.decrypt(keyField.getText().toString());
        updateView();
        onSave();
    }

    /**
     * called when "New" button is pressed
     * creates a new empty message
     */
    void onNew(){
        onSave();
        ml.newMessage();
        updateView();
    }

    /**
     * called when "Delete" button is pressed
     * Deletes the message you would like to delete
     */
    private void onDelete(){
        AlertDialog dlg = new AlertDialog.Builder(this).create();
        dlg.setTitle("Delete");
        dlg.setMessage("Are you sure you want to delete this message?");
        dlg.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        ml.delete();  updateView();}
                }
        );
        dlg.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel", (DialogInterface.OnClickListener)null);
        dlg.show(); // Does not wait for dialog!
    }

    /**
     * Updates the view - the text of the current message and the label - message's number
     */
    private void updateView(){
        msgField.setText(ml.getText());
        updateLabel();
    }

    /**
     * Updates the label
      */

    private void updateLabel(){
        label.setText(ml.getLabel());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
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


}
