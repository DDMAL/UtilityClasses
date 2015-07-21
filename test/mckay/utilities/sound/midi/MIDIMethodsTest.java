/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mckay.utilities.sound.midi;

import java.io.File;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 *
 * @author dinamix
 */
public class MIDIMethodsTest {

    @Rule public TemporaryFolder tempFolder = new TemporaryFolder();
    
    @After
    public void cleanupTempFolder() {
        tempFolder.delete();
    }
    
    /**
     * Test of breakSequenceIntoWindows method, of class MIDIMethods.
     * @throws java.lang.Exception
     */
    @Test
    public void testBreakSequenceIntoWindows() 
            throws Exception 
    {
        //TEST PROGRAM CHANGE
        Sequence sequence = new Sequence(Sequence.PPQ, 256);
        Track testTrack = sequence.createTrack();
        testTrack.add(MidiBuildMessage.createProgramChange(19, 0, 0));
        testTrack.add(MidiBuildMessage.createNoteOnEvent(35, 0, 0));
        testTrack.add(MidiBuildMessage.createNoteOffEvent(35, 1024, 0));
        
        Sequence[] test_sequence = new Sequence[2];
        test_sequence[0] = new Sequence(Sequence.PPQ, 256);
        Track test_sequence_0 = test_sequence[0].createTrack();
        test_sequence_0.add(MidiBuildMessage.createProgramChange(19, 0, 0));
        test_sequence_0.add(MidiBuildMessage.createNoteOnEvent(35, 0, 0));
        
        test_sequence[1] = new Sequence(Sequence.PPQ, 256);
        Track test_sequence_1 = test_sequence[1].createTrack();
        test_sequence_1.add(MidiBuildMessage.createProgramChange(19, 0, 0));
        test_sequence_1.add(MidiBuildMessage.createNoteOffEvent(35, 512, 0));
        
        //512 ticks per second with 0.5 second windows makes 4 windows in 1024 ticks
        Sequence[] windows = MIDIMethods.breakSequenceIntoWindows(sequence, 0.5, 0);
        assertEquals(4, windows.length);
        
        Sequence[] windowed_sequences = MIDIMethods.breakSequenceIntoWindows(sequence, 1, 0);
        for (int s = 0; s < windowed_sequences.length; s++) 
        {
            Sequence actualSequence = windowed_sequences[s];
            Sequence expectedSequence = test_sequence[s];
            for (int t = 0; t < actualSequence.getTracks().length; t++) 
            {
                Track actualTrack = actualSequence.getTracks()[t];
                Track expectedTrack = expectedSequence.getTracks()[t];
                for (int event = 0; event < actualTrack.size(); event++) 
                {
                    MidiEvent actualEvent = actualTrack.get(event);
                    MidiEvent expectedEvent = expectedTrack.get(event);
                    byte[] actualArray = actualEvent.getMessage().getMessage();
                    byte[] expectedArray = expectedEvent.getMessage().getMessage();
                    assertArrayEquals(expectedArray, actualArray);
                    assertEquals(expectedEvent.getTick(), actualEvent.getTick());
                }
            }
        }
        for (int s = 0; s < test_sequence.length; s++) 
        {
            Sequence actualSequence = windowed_sequences[s];
            Sequence expectedSequence = test_sequence[s];
            for (int t = 0; t < expectedSequence.getTracks().length; t++) 
            {
                Track actualTrack = actualSequence.getTracks()[t];
                Track expectedTrack = expectedSequence.getTracks()[t];
                for (int event = 0; event < expectedTrack.size(); event++) 
                {
                    MidiEvent actualEvent = actualTrack.get(event);
                    MidiEvent expectedEvent = expectedTrack.get(event);
                    byte[] actualArray = actualEvent.getMessage().getMessage();
                    byte[] expectedArray = expectedEvent.getMessage().getMessage();
                    assertArrayEquals(expectedArray, actualArray);
                    assertEquals(expectedEvent.getTick(), actualEvent.getTick());
                }
            }
        }
        
        //TEST KEY SIGNATURE
        Sequence sequenceKeysig = new Sequence(Sequence.PPQ, 256);
        Track trackKeysig = sequenceKeysig.createTrack();
        trackKeysig.add(MidiBuildMessage.createKeySignature("1s", "minor", 0));
        trackKeysig.add(MidiBuildMessage.createNoteOnEvent(35, 0, 0));
        trackKeysig.add(MidiBuildMessage.createNoteOffEvent(35, 1024, 0));
        
        Sequence[] test_sequence_key = new Sequence[2];
        test_sequence_key[0] = new Sequence(Sequence.PPQ, 256);
        Track test_sequence_key_0 = test_sequence_key[0].createTrack();
        test_sequence_key_0.add(MidiBuildMessage.createKeySignature("1s", "minor", 0));
        test_sequence_key_0.add(MidiBuildMessage.createNoteOnEvent(35, 0, 0));
        
        test_sequence_key[1] = new Sequence(Sequence.PPQ, 256);
        Track test_sequence_key_1 = test_sequence_key[1].createTrack();
        test_sequence_key_1.add(MidiBuildMessage.createKeySignature("1s", "minor", 0));
        test_sequence_key_1.add(MidiBuildMessage.createNoteOffEvent(35, 512, 0));
        
        Sequence[] windowed_sequences_key = MIDIMethods.breakSequenceIntoWindows(sequenceKeysig, 1, 0);
        for (int s = 0; s < windowed_sequences_key.length; s++) 
        {
            Sequence actualSequence = windowed_sequences_key[s];
            Sequence expectedSequence = test_sequence_key[s];
            for (int t = 0; t < actualSequence.getTracks().length; t++) 
            {
                Track actualTrack = actualSequence.getTracks()[t];
                Track expectedTrack = expectedSequence.getTracks()[t];
                for (int event = 0; event < actualTrack.size(); event++) 
                {
                    MidiEvent actualEvent = actualTrack.get(event);
                    MidiEvent expectedEvent = expectedTrack.get(event);
                    byte[] actualArray = actualEvent.getMessage().getMessage();
                    byte[] expectedArray = expectedEvent.getMessage().getMessage();
                    assertArrayEquals(expectedArray, actualArray);
                    assertEquals(expectedEvent.getTick(), actualEvent.getTick());
                }
            }
        }
        for (int s = 0; s < test_sequence_key.length; s++) 
        {
            Sequence actualSequence = windowed_sequences_key[s];
            Sequence expectedSequence = test_sequence_key[s];
            for (int t = 0; t < expectedSequence.getTracks().length; t++) 
            {
                Track actualTrack = actualSequence.getTracks()[t];
                Track expectedTrack = expectedSequence.getTracks()[t];
                for (int event = 0; event < expectedTrack.size(); event++) 
                {
                    MidiEvent actualEvent = actualTrack.get(event);
                    MidiEvent expectedEvent = expectedTrack.get(event);
                    byte[] actualArray = actualEvent.getMessage().getMessage();
                    byte[] expectedArray = expectedEvent.getMessage().getMessage();
                    assertArrayEquals(expectedArray, actualArray);
                    assertEquals(expectedEvent.getTick(), actualEvent.getTick());
                }
            }
        }
        
        //TEST TEMPO
        Sequence sequenceTempo = new Sequence(Sequence.PPQ, 256);
        Track trackTempo = sequenceTempo.createTrack();
        trackTempo.add(MidiBuildMessage.createTrackTempo(90, 0));
        trackTempo.add(MidiBuildMessage.createNoteOnEvent(35, 0, 0));
        trackTempo.add(MidiBuildMessage.createNoteOffEvent(35, 1024, 0));
        
        Sequence[] test_sequence_tempo = new Sequence[3];
        test_sequence_tempo[0] = new Sequence(Sequence.PPQ, 256);
        Track test_sequence_tempo_0 = test_sequence_tempo[0].createTrack();
        test_sequence_tempo_0.add(MidiBuildMessage.createTrackTempo(90, 0));
        test_sequence_tempo_0.add(MidiBuildMessage.createNoteOnEvent(35, 0, 0));
        
        //Nothing ever happens in this sequence time range so it will be empty
        //Special events will not get copied into it because it is unecessary
        test_sequence_tempo[1] = new Sequence(Sequence.PPQ, 256);
        Track test_sequence_tempo_1 = test_sequence_tempo[1].createTrack();
        
        test_sequence_tempo[2] = new Sequence(Sequence.PPQ, 256);
        Track test_sequence_tempo_2 = test_sequence_tempo[2].createTrack();
        test_sequence_tempo_2.add(MidiBuildMessage.createTrackTempo(90, 0));
        test_sequence_tempo_2.add(MidiBuildMessage.createNoteOffEvent(35, 254, 0));
        
        Sequence[] windowed_sequence_tempo = MIDIMethods.breakSequenceIntoWindows(sequenceTempo, 1, 0);
        assertEquals(3, windowed_sequence_tempo.length);
        
        for (int s = 0; s < windowed_sequence_tempo.length; s++) 
        {
            Sequence actualSequence = windowed_sequence_tempo[s];
            Sequence expectedSequence = test_sequence_tempo[s];
            for (int t = 0; t < actualSequence.getTracks().length; t++) 
            {
                Track actualTrack = actualSequence.getTracks()[t];
                Track expectedTrack = expectedSequence.getTracks()[t];
                for (int event = 0; event < actualTrack.size(); event++) 
                {
                    MidiEvent actualEvent = actualTrack.get(event);
                    MidiEvent expectedEvent = expectedTrack.get(event);
                    byte[] actualArray = actualEvent.getMessage().getMessage();
                    byte[] expectedArray = expectedEvent.getMessage().getMessage();
                    assertArrayEquals(expectedArray, actualArray);
                    assertEquals(expectedEvent.getTick(), actualEvent.getTick());
                }
            }
        }
        for (int s = 0; s < test_sequence_tempo.length; s++) 
        {
            Sequence actualSequence = windowed_sequence_tempo[s];
            Sequence expectedSequence = test_sequence_tempo[s];
            for (int t = 0; t < expectedSequence.getTracks().length; t++) 
            {
                Track actualTrack = actualSequence.getTracks()[t];
                Track expectedTrack = expectedSequence.getTracks()[t];
                for (int event = 0; event < expectedTrack.size(); event++) 
                {
                    MidiEvent actualEvent = actualTrack.get(event);
                    MidiEvent expectedEvent = expectedTrack.get(event);
                    byte[] actualArray = actualEvent.getMessage().getMessage();
                    byte[] expectedArray = expectedEvent.getMessage().getMessage();
                    assertArrayEquals(expectedArray, actualArray);
                    assertEquals(expectedEvent.getTick(), actualEvent.getTick());
                }
            }
        }
        
        //Short file to test windows
        //PUT INTO A JUNIT TEST FILE
        File tempDir = tempFolder.newFolder("MozartQuintettWindows");
        String tempDirName = tempDir.getPath();
        File mozartFile = new File("./test/mckay/utilities/sound/midi/"
                                + "midi-test-resources/Mozart_Quintett.midi");
        Sequence mozartSequence = MidiSystem.getSequence(mozartFile);
        Sequence[] mozart_sequence_windows = MIDIMethods.breakSequenceIntoWindows(mozartSequence, 10, 0);
        
        int sequence_num = 0;
        for(Sequence mozart_sequence : mozart_sequence_windows)
        {   
            File tempFile = new File(tempDirName + File.separator
                                + "mozart_sequence_" + sequence_num + ".midi");
            MidiSystem.write(mozart_sequence, 1, tempFile);
            sequence_num++;
        }
        
        File[] mozartSequenceWindows = tempDir.listFiles();
        assertEquals(4, mozartSequenceWindows.length);
    }
    
}
