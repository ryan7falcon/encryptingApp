package dev.cat.encryptingapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class HomeActivity extends Activity {

    //field variables
    private TextView label;
    private EditText msgField;
    private EditText keyField;
    private Button listBtn;
    private Button newBtn;
    private Button saveBtn;
    private Button deleteBtn;
    private Button nextBtn;
    private Button previousBtn;
    private Button encryptBtn;
    private Button decryptBtn;

    //static and package visibility to make it accessible from MessageListActivity
    static MessageList ml;

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
        updateView(ml.getText());
    }

    private void onNext() {
        onSave();
        if(!ml.checkNext()){
            AlertDialog dlg = new AlertDialog.Builder(this).create();
            dlg.setTitle("End of a list");
            dlg.setMessage("This is a last message in the list. Go to the first one?");
            dlg.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int which){
                            ml.goToFirst();  updateView(ml.getText());}
                    }
            );
            dlg.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel", (DialogInterface.OnClickListener)null);
            dlg.show(); // Does not wait for dialog!
        }
        else{
            ml.goToNext();
        }
        updateView(ml.getText());

    }

    private void onPrevious(){
        onSave();
        if(!ml.checkPrevious()){
            AlertDialog dlg = new AlertDialog.Builder(this).create();
            dlg.setTitle("End of a list");
            dlg.setMessage("This is a first message in the list. Go to the last one?");
            dlg.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int which){
                           ml.goToLast();  updateView(ml.getText());}
                    }
            );
            dlg.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel", (DialogInterface.OnClickListener)null);
            dlg.show(); // Does not wait for dialog!
        }
        else{
            ml.goToPrevious();
        }
        updateView(ml.getText());
    }

    private void onSave() {
       ml.save(msgField.getText().toString());
    }

    private void onList() {
        onSave();
        Intent i = new Intent(getBaseContext(), MessageListActivity.class);
        startActivity(i);
    }

    private void onEncrypt() {

       onSave();
        ml.encrypt(keyField.getText().toString());
       updateView(ml.getText());
       onSave();
    }

    private void onDecrypt() {
        onSave();
        ml.decrypt(keyField.getText().toString());
        updateView(ml.getText());
        onSave();
    }

    //create a new empty message
    private void onNew(){
        onSave();
        ml.newMessage();
        updateView(ml.getText());
    }

    private void onDelete(){
        AlertDialog dlg = new AlertDialog.Builder(this).create();
        dlg.setTitle("Delete");
        dlg.setMessage("Are you sure you want to delete this message?");
        dlg.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        ml.delete();  updateView(ml.getText());}
                }
        );
        dlg.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel", (DialogInterface.OnClickListener)null);
        dlg.show(); // Does not wait for dialog!
    }

    private void updateView(String s){
        msgField.setText(s);
        updateLabel();
    }

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
