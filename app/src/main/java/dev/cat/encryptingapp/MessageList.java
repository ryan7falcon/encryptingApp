package dev.cat.encryptingapp;

import android.content.Context;
import android.util.Log;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
/**
 * Created by Ryan on 4/17/2015.
 */
public class MessageList {

    protected Context context; //application context
    private ArrayList<Message> _list; // a list of messages
    private static final String FNAME = "message"; //prefix for filenames
    private static final String LIST_FILE_NAME = "list.txt"; //file for storing filenames
    private Message cm;

    /**
     * constructor
     * @param context Application context - needed for fileIO
     */
    public MessageList(Context context){
        _list = new ArrayList<>();
        this.context = context;
    }

    /**
     * creates new blank message
     */
    public void newMessage(){
        Message m = new Message();
        m.setPath(FNAME + (_list.size() + 1));
        m.setText("");
        _list.add(m);
        cm = m;
        //add filename to the list.txt of files
        updateList();
    }

    /**
     * updates the list of filenames in list.txt
     */
    private void updateList(){

        //writes all message's paths to the list.txt file

        OutputStreamWriter writer;
        try {
            writer = new OutputStreamWriter(context.openFileOutput(LIST_FILE_NAME, Context.MODE_PRIVATE));

            for (int i = 0; i < _list.size(); i++){
                String string = _list.get(i).getPath();
                writer.write(string + ",");
            }

            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * sets current message's text
     * @param s text that will be stored in a message
     */
    public void setText(String s){
        cm.setText(s);
    }

    /**
     *
     * @return the String containing current message
     */
    public String getText(){
        return cm.getText();
    }

    /**
     * saves current message to a file
     * @param s a text to be saved in a message and a message's file
     */
    public void save(String s){
        //updates array list.txt
        cm.setText(s);

        //write message into the file
        String string = cm.getText();
        OutputStreamWriter outputStream;

        try {
            outputStream = new OutputStreamWriter(context.openFileOutput(cm.getPath(), Context.MODE_PRIVATE));
            outputStream.write(string);
            outputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * loads messages from files. File names are stored in a separate file list.txt
     * @return true if the load was successful
     */
    public boolean load(){
        _list.clear();

        String ret;
        //read file names from list.txt
        try {
            InputStream inputStream =  context.openFileInput(LIST_FILE_NAME);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder stringBuilder = new StringBuilder();
                String receiveString;

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();

                String[] fields = ret.split(",");

                for (String field : fields) {
                    Message m = new Message("", field);
                    //read message from a file
                    try {
                        inputStream = context.openFileInput(m.getPath());
                        if (inputStream != null) {

                            inputStreamReader = new InputStreamReader(inputStream);
                            bufferedReader = new BufferedReader(inputStreamReader);
                            stringBuilder = new StringBuilder();

                            while ((receiveString = bufferedReader.readLine()) != null) {
                                stringBuilder.append(receiveString);
                            }

                            inputStream.close();
                            ret = stringBuilder.toString();

                            m.setText(ret);

                            _list.add(m);

                        }

                    } catch (FileNotFoundException e) {
                        Log.e("Error", "File not found: " + e.toString());
                    } catch (IOException e) {
                        Log.e("Error", "Can not read file: " + e.toString());
                    }

                }
            }
        }
        catch (FileNotFoundException e) {
            Log.e("Error", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("Error", "Can not read file: " + e.toString());
        }


        //if smth loaded return true
        if (_list.size() > 0){
            //load the first message
            cm = _list.get(0);
            return true;
        }

        else
            return false;
    }

    public void delete(){
        int index = _list.indexOf(cm);
        _list.remove(index);
        if (_list.size() == 0) {
            newMessage();
            cm.setText("This is your first message. Enter any key and press 'Encrypt' button.");
        }
        else {
            if (_list.size() < index + 1)
                index--;
        }
        cm = _list.get(index);
        updateList();
    }
//----------------------Navigation between messages---------------------------------------
    /**
     * check if next message exists
     */
    public boolean checkNext(){
        int index = _list.indexOf(cm);
        return (index + 1 < _list.size());
    }

    /**
     * go to next message
     */
    public void goToNext(){
        cm = _list.get(_list.indexOf(cm) + 1);
    }


    /**
     * go to first message
     */
    public void goToFirst(){
        cm = _list.get(0);
    }

    /**
     * check if previous message exists
     * @return true if there is a previous message
     */
    public boolean checkPrevious(){

        int index = _list.indexOf(cm);
        return (index - 1 >= 0);
    }
    /**
     * switch to previous message
     */
    public void goToPrevious(){
        cm = _list.get(_list.indexOf(cm) - 1);
    }

    /**
     * go to last message
     */
    public void goToLast(){
        cm = _list.get(_list.size() - 1);
    }
    //-----------------------------------------------------------------------------------------------

    //--------------------------Encryption-Decryption---------------------------------------------
    public void encrypt( String key){
        String s = cm.getText();
        String result = "";
        for (int i = 1; i <= s.length(); i++){
            char c = s.charAt(i - 1);
            if ((c >= 'A' && c <='z')){
                int remainder = i % key.length();
                int index = (remainder == 0) ? key.length() - 1 : remainder - 1;
                int keyChar = key.charAt(index) - 'A';
                int sum = c -'A' + keyChar;
                int sumNorm = sum % ('z'+ 1 -'A');
                char e = (char)(sumNorm + 'A');
                result+=e;
            }
            else
                result+=c;
        }
        cm.setText(result);
    }

    public void decrypt( String key){
        String s = cm.getText();
        String result = "";
        for (int i = 1; i <= s.length(); i++){
            char c = s.charAt(i - 1);
            if ((c >= 'A' && c <='z')){
                int remainder = i % key.length();
                int index = (remainder == 0) ? key.length() - 1 : remainder - 1;
                int keyChar = key.charAt(index) - 'A';
                int sum = c -'A' - keyChar;
                int sumResult = (sum >= 0) ? sum +'A': sum + 'z' + 1;
                char e = (char)(sumResult);
                result+=e;
            }
            else
                result+=c;
        }
        cm.setText(result);
    }

}