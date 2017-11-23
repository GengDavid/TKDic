package com.example.abnervictor.tkdic;

/**
 * Created by AHoo-Yuan on 2017/10/30.
 */

public class MessageEvent {

    private final characterInfo message;
    public MessageEvent(characterInfo message) {
        this.message = message;
    }
    public characterInfo getmessage() {
        return message;
    }

}
