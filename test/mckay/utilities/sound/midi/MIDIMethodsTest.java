/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mckay.utilities.sound.midi;

import java.io.File;
import java.util.List;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

import org.ddmal.jmei2midi.MeiSequence;
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
        testTrack.add(MidiBuildEvent.createProgramChange(24, 0, 0));
        testTrack.add(MidiBuildEvent.createNoteOnEvent(35, 0, 0));
        testTrack.add(MidiBuildEvent.createNoteOffEvent(35, 1024, 0));
        
        Sequence[] test_sequence = new Sequence[3];
        test_sequence[0] = new Sequence(Sequence.PPQ, 256);
        Track test_sequence_0 = test_sequence[0].createTrack();
        test_sequence_0.add(MidiBuildEvent.createProgramChange(24, 0, 0));
        test_sequence_0.add(MidiBuildEvent.createNoteOnEvent(35, 0, 0));
        test_sequence_0.add(MidiBuildEvent.createNoteOffEvent(35, 511, 0));
        
        test_sequence[1] = new Sequence(Sequence.PPQ, 256);
        Track test_sequence_1 = test_sequence[1].createTrack();
        test_sequence_1.add(MidiBuildEvent.createProgramChange(24, 0, 0));
        test_sequence_1.add(MidiBuildEvent.createNoteOnEvent(35, 0, 0));
        test_sequence_1.add(MidiBuildEvent.createNoteOffEvent(35, 511, 0));

        test_sequence[2] = new Sequence(Sequence.PPQ, 256);
        Track test_sequence_2 = test_sequence[2].createTrack();
        test_sequence_2.add(MidiBuildEvent.createProgramChange(24, 0, 0));
        test_sequence_2.add(MidiBuildEvent.createNoteOnEvent(35, 0, 0));
        test_sequence_2.add(MidiBuildEvent.createNoteOffEvent(35, 0, 0));
        
        //512 ticks per second with 0.5 second windows makes 4 windows in 1024 ticks
        double window_duration = 0.5;
        double window_overlap_offset = 0;
        double[] seconds_per_tick = MIDIMethods.getSecondsPerTick(sequence);
        List<int[]> startEndTickArrays = MIDIMethods.getStartEndTickArrays(sequence,
                                                                            window_duration, 
                                                                           window_overlap_offset,
                                                                           seconds_per_tick);
        int[] start_ticks = startEndTickArrays.get(0);
        int[] end_ticks = startEndTickArrays.get(1);
        Sequence[] windows = MIDIMethods.breakSequenceIntoWindows(sequence, 
                                                                window_duration, 
                                                                window_overlap_offset,
                                                                start_ticks,
                                                                end_ticks);
        assertEquals(5, windows.length);

        window_duration = 1.0;
        window_overlap_offset = 0;
        startEndTickArrays = MIDIMethods.getStartEndTickArrays(sequence, 
                                                                            window_duration, 
                                                                           window_overlap_offset,
                                                                           seconds_per_tick);
        start_ticks = startEndTickArrays.get(0);
        end_ticks = startEndTickArrays.get(1);
        Sequence[] windowed_sequences = MIDIMethods.breakSequenceIntoWindows(sequence, window_duration, window_overlap_offset, start_ticks, end_ticks);
        compareEventByteArrayTest(windowed_sequences,test_sequence);
        compareEventByteArrayTest(test_sequence,windowed_sequences);

        //TEST KEY SIGNATURE
        Sequence sequenceKeysig = new Sequence(Sequence.PPQ, 256);
        Track trackKeysig = sequenceKeysig.createTrack();
        trackKeysig.add(MidiBuildEvent.createKeySignature("1s", "minor", 0));
        trackKeysig.add(MidiBuildEvent.createNoteOnEvent(35, 0, 0));
        trackKeysig.add(MidiBuildEvent.createNoteOffEvent(35, 1024, 0));
        
        Sequence[] test_sequence_key = new Sequence[3];
        test_sequence_key[0] = new Sequence(Sequence.PPQ, 256);
        Track test_sequence_key_0 = test_sequence_key[0].createTrack();
        test_sequence_key_0.add(MidiBuildEvent.createKeySignature("1s", "minor", 0));
        test_sequence_key_0.add(MidiBuildEvent.createNoteOnEvent(35, 0, 0));
        test_sequence_key_0.add(MidiBuildEvent.createNoteOffEvent(35, 511, 0));
        
        test_sequence_key[1] = new Sequence(Sequence.PPQ, 256);
        Track test_sequence_key_1 = test_sequence_key[1].createTrack();
        test_sequence_key_1.add(MidiBuildEvent.createKeySignature("1s", "minor", 0));
        test_sequence_key_1.add(MidiBuildEvent.createNoteOnEvent(35, 0, 0));
        test_sequence_key_1.add(MidiBuildEvent.createNoteOffEvent(35, 511, 0));

        test_sequence_key[2] = new Sequence(Sequence.PPQ, 256);
        Track test_sequence_key_2 = test_sequence_key[2].createTrack();
        test_sequence_key_2.add(MidiBuildEvent.createKeySignature("1s", "minor", 0));
        test_sequence_key_2.add(MidiBuildEvent.createNoteOnEvent(35, 0, 0));
        test_sequence_key_2.add(MidiBuildEvent.createNoteOffEvent(35, 0, 0));
        
        window_duration = 1.0;
        window_overlap_offset = 0;
        seconds_per_tick = MIDIMethods.getSecondsPerTick(sequenceKeysig);
        startEndTickArrays = MIDIMethods.getStartEndTickArrays(sequenceKeysig, 
                                                                            window_duration, 
                                                                           window_overlap_offset,
                                                                           seconds_per_tick);
        start_ticks = startEndTickArrays.get(0);
        end_ticks = startEndTickArrays.get(1);
        Sequence[] windowed_sequences_key = MIDIMethods.breakSequenceIntoWindows(sequenceKeysig, window_duration, window_overlap_offset,start_ticks,end_ticks);
        compareEventByteArrayTest(windowed_sequences_key,test_sequence_key);
        compareEventByteArrayTest(test_sequence_key,windowed_sequences_key);
        
        //TEST TEMPO
        Sequence sequenceTempo = new Sequence(Sequence.PPQ, 256);
        Track trackTempo = sequenceTempo.createTrack();
        trackTempo.add(MidiBuildEvent.createTrackTempo(90, 0));
        trackTempo.add(MidiBuildEvent.createNoteOnEvent(35, 0, 0));
        trackTempo.add(MidiBuildEvent.createTrackTempo(120, 700));
        trackTempo.add(MidiBuildEvent.createNoteOffEvent(35, 1024, 0));
        
        Sequence[] test_sequence_tempo = new Sequence[3];
        test_sequence_tempo[0] = new Sequence(Sequence.PPQ, 256);
        Track test_sequence_tempo_0 = test_sequence_tempo[0].createTrack();
        test_sequence_tempo_0.add(MidiBuildEvent.createTrackTempo(90, 0));
        test_sequence_tempo_0.add(MidiBuildEvent.createNoteOnEvent(35, 0, 0));
        test_sequence_tempo_0.add(MidiBuildEvent.createNoteOffEvent(35, 384, 0));
        
        test_sequence_tempo[1] = new Sequence(Sequence.PPQ, 256);
        Track test_sequence_tempo_1 = test_sequence_tempo[1].createTrack();
        test_sequence_tempo_1.add(MidiBuildEvent.createTrackTempo(90, 0));
        test_sequence_tempo_1.add(MidiBuildEvent.createNoteOnEvent(35, 0, 0));
        test_sequence_tempo_1.add(MidiBuildEvent.createTrackTempo(120, 315));
        test_sequence_tempo_1.add(MidiBuildEvent.createNoteOffEvent(35, 407, 0));
        
        test_sequence_tempo[2] = new Sequence(Sequence.PPQ, 256);
        Track test_sequence_tempo_2 = test_sequence_tempo[2].createTrack();
        test_sequence_tempo_2.add(MidiBuildEvent.createTrackTempo(120, 0));
        test_sequence_tempo_2.add(MidiBuildEvent.createNoteOnEvent(35, 0, 0));
        test_sequence_tempo_2.add(MidiBuildEvent.createNoteOffEvent(35, 231, 0));
        
        window_duration = 1.0;
        window_overlap_offset = 0;
        seconds_per_tick = MIDIMethods.getSecondsPerTick(sequenceTempo);
        startEndTickArrays = MIDIMethods.getStartEndTickArrays(sequenceTempo, 
                                                                window_duration, 
                                                                window_overlap_offset,
                                                                seconds_per_tick);
        start_ticks = startEndTickArrays.get(0);
        end_ticks = startEndTickArrays.get(1);
        Sequence[] windowed_sequence_tempo = MIDIMethods.breakSequenceIntoWindows(sequenceTempo, window_duration, window_overlap_offset,start_ticks,end_ticks);
        assertEquals(3, windowed_sequence_tempo.length);

        compareEventByteArrayTest(windowed_sequence_tempo,test_sequence_tempo);
        compareEventByteArrayTest(test_sequence_tempo,windowed_sequence_tempo);

        //Short file to test windows
        //PUT INTO A JUNIT TEST FILE
        window_duration = 10.0;
        window_overlap_offset = 0;
        File tempDir = tempFolder.newFolder("MozartQuintettWindows");
        String tempDirName = tempDir.getPath();
        File mozartFile = new File("./test/mckay/utilities/sound/midi/"
                                + "midi-test-resources/Mozart_Quintett.mei");
        MeiSequence mozs = new MeiSequence(mozartFile);
        Sequence mozartSequence = mozs.getSequence();
        double[] mozart_seconds_per_tick = MIDIMethods.getSecondsPerTick(mozartSequence);
        List<int[]> mozartStartEndTickArrays = MIDIMethods.getStartEndTickArrays(mozartSequence,
                window_duration,
                window_overlap_offset,
                mozart_seconds_per_tick);
        int[] mozart_start_ticks = mozartStartEndTickArrays.get(0);
        int[] mozart_end_ticks = mozartStartEndTickArrays.get(1);
        Sequence[] mozart_sequence_windows = MIDIMethods.breakSequenceIntoWindows(mozartSequence,
                window_duration, window_overlap_offset,mozart_start_ticks,mozart_end_ticks);
        //To write out mozart midi files
        /*int sequence_num = 0;
        for(Sequence mozart_sequence : mozart_sequence_windows)
        {   
            File tempFile = new File(tempDirName + File.separator
                                + "mozart_sequence_" + sequence_num + ".midi");
            MidiSystem.write(mozart_sequence, 1, tempFile);
            //To output the midi file for manual checks
            MidiSystem.write(mozart_sequence,1,new File("mozarttest" + sequence_num + ".midi"));
            sequence_num++;
        }
        
        File[] mozartSequenceWindows = tempDir.listFiles();*/
        assertEquals(4, mozart_sequence_windows.length);


        //Saint-Saens window tests
        window_duration = 10.0;
        window_overlap_offset = 0;
        MeiSequence ms = new MeiSequence("./test/mckay/utilities/sound/midi/" +
                "midi-test-resources/Saint-Saens_LeCarnevalDesAnimmaux.mei");
        Sequence saintsaensSequence = ms.getSequence();
        double[] saintsaens_seconds_per_tick = MIDIMethods.getSecondsPerTick(saintsaensSequence);
        List<int[]> saintsaensStartEndTickArrays = MIDIMethods.getStartEndTickArrays(saintsaensSequence,
                window_duration,
                window_overlap_offset,
                saintsaens_seconds_per_tick);
        int[] saintsaens_start_ticks = saintsaensStartEndTickArrays.get(0);
        int[] saintsaens_end_ticks = saintsaensStartEndTickArrays.get(1);
        Sequence[] saintsaensWindows = MIDIMethods.breakSequenceIntoWindows(saintsaensSequence,
                window_duration, window_overlap_offset, saintsaens_start_ticks, saintsaens_end_ticks);

        /*To save windowed midi files
        int testcount = 1;
        for(Sequence s : saintsaensWindows) {
            MidiSystem.write(s,1,new File("saintsaenstest" + testcount + ".midi"));
            testcount++;
        }*/
        assertEquals(14, saintsaensWindows.length);
        
        //TEST WINDOW OVERLAP OFFSET
        Sequence sequenceOverlap = new Sequence(Sequence.PPQ, 256);
        Track trackOverlap = sequenceOverlap.createTrack();
        trackOverlap.add(MidiBuildEvent.createKeySignature("1s", "minor", 0));
        trackOverlap.add(MidiBuildEvent.createNoteOnEvent(35, 0, 0));
        trackOverlap.add(MidiBuildEvent.createNoteOffEvent(35, 1023, 0));

        Sequence[] test_sequence_overlap = new Sequence[4];
        test_sequence_overlap[0] = new Sequence(Sequence.PPQ, 256);
        Track test_sequence_overlap_0 = test_sequence_overlap[0].createTrack();
        test_sequence_overlap_0.add(MidiBuildEvent.createKeySignature("1s", "minor", 0));
        test_sequence_overlap_0.add(MidiBuildEvent.createNoteOnEvent(35, 0, 0));
        test_sequence_overlap_0.add(MidiBuildEvent.createNoteOffEvent(35, 511, 0));

        test_sequence_overlap[1] = new Sequence(Sequence.PPQ, 256);
        Track test_sequence_overlap_1 = test_sequence_overlap[1].createTrack();
        test_sequence_overlap_1.add(MidiBuildEvent.createKeySignature("1s", "minor", 0));
        test_sequence_overlap_1.add(MidiBuildEvent.createNoteOnEvent(35, 0, 0));
        test_sequence_overlap_1.add(MidiBuildEvent.createNoteOffEvent(35, 511, 0));

        test_sequence_overlap[2] = new Sequence(Sequence.PPQ, 256);
        Track test_sequence_overlap_2 = test_sequence_overlap[2].createTrack();
        test_sequence_overlap_2.add(MidiBuildEvent.createKeySignature("1s", "minor", 0));
        test_sequence_overlap_2.add(MidiBuildEvent.createNoteOnEvent(35, 0, 0));
        test_sequence_overlap_2.add(MidiBuildEvent.createNoteOffEvent(35, 511, 0));
        //Second one due to off by one in end tick array, does not change actual MIDI
        test_sequence_overlap_2.add(MidiBuildEvent.createNoteOffEvent(35, 511, 0));

        test_sequence_overlap[3] = new Sequence(Sequence.PPQ, 256);
        Track test_sequence_overlap_3 = test_sequence_overlap[3].createTrack();
        test_sequence_overlap_3.add(MidiBuildEvent.createKeySignature("1s", "minor", 0));
        test_sequence_overlap_3.add(MidiBuildEvent.createNoteOnEvent(35, 0, 0));
        test_sequence_overlap_3.add(MidiBuildEvent.createNoteOffEvent(35, 255, 0));

        window_duration = 1.0;
        window_overlap_offset = 0.5;
        seconds_per_tick = MIDIMethods.getSecondsPerTick(sequenceOverlap);
        startEndTickArrays = MIDIMethods.getStartEndTickArrays(sequenceOverlap,
                window_duration,
                window_overlap_offset,
                seconds_per_tick);
        start_ticks = startEndTickArrays.get(0);
        end_ticks = startEndTickArrays.get(1);
        Sequence[] windowed_sequences_overlap = MIDIMethods.breakSequenceIntoWindows(sequenceOverlap, window_duration, window_overlap_offset,start_ticks,end_ticks);
        compareEventByteArrayTest(windowed_sequences_overlap,test_sequence_overlap);
        compareEventByteArrayTest(test_sequence_overlap,windowed_sequences_overlap);
    }

    private void compareEventByteArrayTest(Sequence[] actualSequences, Sequence[] expectedSequences) {
        for (int s = 0; s < actualSequences.length; s++)
        {
            Sequence actualSequence = actualSequences[s];
            Sequence expectedSequence = expectedSequences[s];
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
    }
}
