package dev.cat.encryptingapp;

/**
 * Created by Ryan on 4/15/2015.
 */
public class Message {

    private String _text;
    private String _path;

    public Message(String text, String path){
        _text = text;
        _path = path;
    }

    public Message(){
        _text = "";
        _path = "";
    }

    public String getText() {
        return _text;
    }

    public String getPath() {
        return _path;
    }


    public void setText(String text) {
        _text = text;
    }

    public void setPath(String path) {
        _path = path;
    }


}
