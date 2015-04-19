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
import java.util.Date;
/**
 * Created by Ryan on 4/17/2015.
 */
public class MessageList {

    protected Context context; // Interface to global information about an application environment.
    private ArrayList<Message> _list; // a list of messages
    private static final String FNAME = "message"; //prefix for filenames
    private static final String LIST_FILE_NAME = "list.txt"; //file for storing filenames
    private static final int START_ASCII = 32; // ASCII keycode starting with space
    private static final int FIMISH_ASCII = 126; // ASCII keycode ending with ~ which will be tne range for keyboard characters, numbers, and letters.
    private Message cm; // current message






    /**
     * constructor which sets and creates a new ArrayList of messages and sets the application context
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
        Date date = new Date(); // Creates a date object which has the date inside the object.
        Message m = new Message(); // Creates a message object to store the new object
        m.setPath(FNAME + (date.getTime())); //unique name for a message file based on current time
        m.setText("");
        _list.add(m); // adds the message to the array list
        cm = m; //
        //add filename to the list.txt of files
        updateList();
    }

    /**
     * updates the list of filenames in list.txt
     */
    private void updateList(){

        //writes all message's paths to the list.txt file

        OutputStreamWriter writer; // Data written to the target input stream is converted into bytes by either a default or a provided character converter.
        try {
            writer = new OutputStreamWriter(context.openFileOutput(LIST_FILE_NAME, Context.MODE_PRIVATE)); // Open a private file associated with this Context's application package for writing.

            // Goes through the array list and writes to the file
            for (int i = 0; i < _list.size(); i++){
                String string = _list.get(i).getPath();
                writer.write(string + ",");
            }

            writer.close(); // Closes the writer
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
        _list.clear(); // clears the array list

        String ret;
        //read file names from list.txt
        try {
            InputStream inputStream =  context.openFileInput(LIST_FILE_NAME);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream); //  Data reads from the source input stream is changed into characters by either given character converter. The default encoding is taken from the "file.encoding" system property. InputStreamReader contains a buffer of bytes read from the source stream and converts these into characters as needed. The buffer size is 8K.
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader); // Wraps an existing Reader and buffers the input.
                StringBuilder stringBuilder = new StringBuilder(); // Being able to change a combination of characters for use in creating strings.
                String receiveString;


                //  while the receivingString references the bufferedReader's line which it is reading is not equal to null, add the read line to the receiveString
                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close(); // closes the input stream
                ret = stringBuilder.toString();

                String[] fields = ret.split(",");

                // for each string in fields populate it with new Message and string field.
                for (String field : fields) {
                    Message m = new Message("", field);
                    //read message from a file
                    try {
                        inputStream = context.openFileInput(m.getPath());
                        if (inputStream != null) {

                            inputStreamReader = new InputStreamReader(inputStream); // Creates new InputStreamReader object and passes the inputStream
                            bufferedReader = new BufferedReader(inputStreamReader); // Creates new BufferedReader object and passes the inputStreamReader
                            stringBuilder = new StringBuilder(); // Creates new InputStreamReader object and passes the inputStream

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

    // Deletes the message
    public void delete(){
        int index = _list.indexOf(cm);
        _list.remove(index); // removes the index from the array list
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

    // Getter for label
    public String getLabel(){
        return "Message " + (_list.indexOf(cm) + 1) + "/" + _list.size();
    }

    // Getter for ArrayList to get list OF messages
    public ArrayList<String> getListOfMessages(){

        ArrayList<String> l = new ArrayList<>();
        for (Message msg : _list) {
            l.add(msg.getText());
        }
        return l;
    }

    // Getter for ArrayList to get list of names
    public ArrayList<String> getLIstOfNames(){

        ArrayList<String> l = new ArrayList<>();
        for (Message msg : _list) {
            l.add(msg.getPath());
        }
        return l;
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
    public void goToNext()
    {
        goTo(_list.indexOf(cm) + 1);

    }


    /**
     * go to first message
     */
    public void goToFirst()
    {
        goTo(0);
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
        goTo(_list.indexOf(cm) - 1);
    }

    /**
     * go to last message
     */
    public void goToLast(){
        goTo(_list.size() - 1);
    }


    public void goTo(int num)
    {
        cm = _list.get(num);
    }
    //-----------------------------------------------------------------------------------------------

    //--------------------------Encryption-Decryption---------------------------------------------
    public void encrypt( String key){
        String s = cm.getText();
        String result = "";
        for (int i = 1; i <= s.length(); i++){
            char c = s.charAt(i - 1);
            if ((c >= START_ASCII && c <=FIMISH_ASCII )){ // If the Start ASCII key codes for keyboard characters is between the two ranges perform the sequences
                int remainder = i % key.length(); // The remainder left over from i and the key's length
                int index = (remainder == 0) ? key.length() - 1 : remainder - 1;
                int keyChar = key.charAt(index) - START_ASCII; // The keys Character index is going to be whichever character at index minus the starting ASCII code
                int sum = c -START_ASCII + keyChar; // Total is going to be key Character index plus the key's character index
                int sumNorm = sum % (FIMISH_ASCII + 1 -START_ASCII);
                char e = (char)(sumNorm + START_ASCII);
                result+=e; // total result of calculation
            }
            else
                result+=c;
        }
        cm.setText(result);
    }

    // Decrypt method which will decrypt the message by first
    public void decrypt( String key){
        String s = cm.getText();
        String result = "";
        for (int i = 1; i <= s.length(); i++){
            char c = s.charAt(i - 1);// Whichever the text's character is at minus 1 to not go out of bounds
            if ((c >= START_ASCII && c <=FIMISH_ASCII)){ // If the Start ASCII key codes for keyboard characters is between the two ranges perform the sequences
                int remainder = i % key.length(); // The remainder left over from i and the key's length
                int index = (remainder == 0) ? key.length() - 1 : remainder - 1;
                int keyChar = key.charAt(index) - START_ASCII;
                int sum = c -START_ASCII - keyChar; //  sum of the Key's Character index subtracted from the start of the ASCII key code
                int sumResult = (sum >= 0) ? sum + START_ASCII: sum + FIMISH_ASCII + 1; // The total sum if it is greater than 0 add sum plus start ASCII key code and if not add sum plus FINISH ASCII key code instead + 1 to reach last letter.
                char e = (char)(sumResult); // cast convert the sumresult to a char
                result+=e; // store total result
            }
            else
                result+=c;
        }
        cm.setText(result);
    }

}
