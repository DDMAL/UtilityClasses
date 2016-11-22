package mckay.utilities.sound.midi;

import javax.sound.midi.MidiEvent;
import java.util.HashMap;

/**
 * Created by dinamix on 11/16/16.
 */
public class MIDISpecialEvents {
    private HashMap<Byte,MidiEvent> thisTrackSpecialEvents;
    private HashMap<Integer,MidiEvent> notesToNextSequence;

    public MIDISpecialEvents() {
        this.thisTrackSpecialEvents = new HashMap<>();
        this.notesToNextSequence = new HashMap<>();
    }

    public HashMap<Byte, MidiEvent> getThisTrackSpecialEvents() {
        return thisTrackSpecialEvents;
    }

    public HashMap<Integer, MidiEvent> getNotesToNextSequence() {
        return notesToNextSequence;
    }
}
