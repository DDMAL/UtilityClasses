/*
 * MIDIMethods.java
 * Version 3.2
 *
 * Last modified on October 29, 2013.
 * Marianopolis College, McGill University and University of Waikato
 */

package mckay.utilities.sound.midi;

import javax.sound.midi.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


/**
 * A holder class for static methods relating to MIDI.
 *
 * @author Cory McKay
 */
public class MIDIMethods
{
     /**
      * Plays the given MIDI Sequence and returns the Sequencer that is playing
      * it. The default system Sequencer and Synthesizer are used.
      *
      * @param	midi_sequence The MIDI sequence to play
      * @return               A sequencer that is playing the midi_sequence
      * @throws	Exception     Throws an exception if an empty MIDI sequence
      *                       is passed as an argument or if cannoth play.
      */
     public static Sequencer playMIDISequence(Sequence midi_sequence)
     throws Exception
     {
          // Throw exception if empty midi_sequence passed
          if (midi_sequence == null)
               throw new Exception("No MIDI data passed for playback.");
          
          // Acquire a MIDI Sequencer from the system
          Sequencer sequencer = MidiSystem.getSequencer();
          if (sequencer == null)
               throw new Exception("Could not acquire a MIDI sequencer from the system.");
          
          // Prepare a holder for a MIDI Synthesizer
          Synthesizer synthesizer = null;
          
          // Open the sequencer
          sequencer.open();
          
          // Feed the sequencer the sequence it is to play
          sequencer.setSequence(midi_sequence);
          
          // Set the desinations that the Sequence should be played on.
          // Some Java Sound implemntations combine the default
          // sequencer and the default synthesizer into one. This
          // checks if this is the case, and forms the needed
          // connections if it is not the case.
          if ( !(sequencer instanceof Synthesizer))
          {
               synthesizer = MidiSystem.getSynthesizer();
               synthesizer.open();
               Receiver synth_receiver = synthesizer.getReceiver();
               Transmitter	seq_transmitter = sequencer.getTransmitter();
               seq_transmitter.setReceiver(synth_receiver);
          }
          
          // Begin playback
          sequencer.start();
          
          // Return the sequencer that is performing playback
          return sequencer;
     }
     
     
     /**
      * Returns information regarding a given MIDI file. This information
      * consists of the file name, the number of tracks in the file, its
      * duration in seconds, the total number of MIDI ticks, its MIDI timing
      * division type, its MIDI resolution type and the tick duration. Also
      * returned is the MIDI file type and any stored meta-data.
      *
      * @param	file          The file to return data about.
      * @return               Data in string form about the specified MIDI file.
      * @throws	Exception     Throws informative exceptions if the file is
      *                       invalid.
      */
     public static String getMIDIFileFormatData(File file)
     throws Exception
     {
          try
          {
               // Load the sequence
               Sequence sequence = MidiSystem.getSequence(file);
               
               // Get the MIDI file type
               MidiFileFormat file_format = MidiSystem.getMidiFileFormat(file);
               
               // Find the timing division type used in the MIDI file
               float division_code = sequence.getDivisionType();
               String	division_type = null;
               if (division_code == Sequence.PPQ)
                    division_type = "PPQ";
               else if (division_code == Sequence.SMPTE_24)
                    division_type = "SMPTE, 24 frames per second";
               else if (division_code == Sequence.SMPTE_25)
                    division_type = "SMPTE, 25 frames per second";
               else if (division_code == Sequence.SMPTE_30DROP)
                    division_type = "SMPTE, 29.97 frames per second";
               else if (division_code == Sequence.SMPTE_30)
                    division_type = "SMPTE, 30 frames per second";
               String	timing_resolution_type = null;
               if (sequence.getDivisionType() == Sequence.PPQ)
                    timing_resolution_type = " ticks per beat";
               else
                    timing_resolution_type = " ticks per frame";
               
               // Format and return the information
               String data = new String();
               data += new String("FILE NAME: " + file.getName() + "\n");
               data += new String("MIDI FILE TYPE: " + file_format.getType() + "\n");
               data += new String("NUMBER OF TRACKS: " + sequence.getTracks().length + "\n");
               data += new String("DURATION: " + (sequence.getMicrosecondLength() / 1000000.0) + " seconds\n");
               data += new String("NUMBER OF TICKS: " + sequence.getTickLength() + " ticks\n");
               data += new String("TIMING DIVISION TYPE: " + division_type + "\n");
               data += new String("TIMING RESOLUTION: " + sequence.getResolution() + timing_resolution_type + "\n");
               data += new String("TICK DURATION: " + (double) sequence.getMicrosecondLength() / 1000000.0 / (double) sequence.getTickLength() + " seconds\n");
               data += new String("TITLE: " + ((String) file_format.getProperty("title")) + "\n");
               data += new String("AUTHOR: " + ((String) file_format.getProperty("author")) + "\n");
               data += new String("COPYRIGHT: " + ((String) file_format.getProperty("copyright")) + "\n");
               data += new String("COMMENT: " + ((String) file_format.getProperty("comment")) + "\n");
               return data;
          }
          catch (IOException ex)
          {
               throw new Exception("File " + file.getName() + " is not a readable MIDI file.");
          }
     }
     
     
     /**
      * Returns an array with an entry for each MIDI tick in the given MIDI
      * sequence. The value at each indice gives the duration of a tick in
      * seconds at that particular point in the recording. Tempo change
      * messages ARE taken into account.
      *
      * @param	sequence The MIDI Sequence from which to extract the tick
      *                  durations.
      * @return          An array with an entry for each MIDI tick in the given
      *                  MIDI sequence.
      */
     public static double[] getSecondsPerTick(Sequence sequence)
     {
          // Find the number of PPQ ticks per beat
          int ticks_per_beat = sequence.getResolution();
          
          // Caclulate the average number of MIDI ticks corresponding to 1 second of score time
          double mean_ticks_per_sec = ((double) sequence.getTickLength()) / ((double) sequence.getMicrosecondLength() / 1000000.0);
          
          // Instantiate seconds_per_tick array and initialize entries to the average
          // number of ticks per second
          double[] seconds_per_tick = new double[ (int) sequence.getTickLength() + 1];
          for (int i = 0; i < seconds_per_tick.length; i++)
               seconds_per_tick[i] = 1.0 / mean_ticks_per_sec;
          
          // Get the MIDI tracks from the Sequence
          Track[] tracks = sequence.getTracks();
          
          // Fill in seconds_per_tick to reflect dynamic tempo changes
          for (int n_track = 0; n_track < tracks.length; n_track++)
          {
               // Go through all the events in the current track, searching for tempo
               // change messages
               Track track = tracks[n_track];
               for (int n_event = 0; n_event < track.size(); n_event++)
               {
                    // Get the MIDI message corresponding to the next MIDI event
                    MidiEvent event = track.get(n_event);
                    MidiMessage message = event.getMessage();
                    
                    // If message is a MetaMessage (which tempo change messages are)
                    if (message instanceof MetaMessage)
                    {
                         MetaMessage meta_message = (MetaMessage) message;
                         if (meta_message.getType() == 0x51) // tempo change message
                         {
                              // Find the number of microseconds per beat
                              byte[]	meta_data = meta_message.getData();
                              int	microseconds_per_beat = ((meta_data[0] & 0xFF) << 16)
                              | ((meta_data[1] & 0xFF) << 8)
                              | (meta_data[2] & 0xFF);
                              
                              // Find the number of seconds per tick
                              double current_seconds_per_tick = ((double) microseconds_per_beat) / ((double) ticks_per_beat);
                              current_seconds_per_tick = current_seconds_per_tick / 1000000.0;
                              
                              // Make all subsequent tempos be at the current_seconds_per_tick rate
                              for (int i = (int) event.getTick(); i < seconds_per_tick.length; i++)
                                   seconds_per_tick[i] = current_seconds_per_tick;
                         }
                    }
               }
          }
          
          // Return the results
          return seconds_per_tick;
     }
     
     
     /**
      * <B>IMPORTANT: THIS METHOD IS DEPENDENT ON getStartEndTickArrays()
      * method below for int[] start_ticks and int[] end_ticks.</b>
      *
      * Breaks the given MIDI Sequence into windows of equal duration. These
      * windows may or may not be overlapping. The original Sequence is not
      * changed. Tempo change messages ARE taken into account, so different
      * windows will have the same time duration, but not necessarily the same
      * number of MIDI ticks.
      *
      * @param	original_sequence	The MIDI Sequence to break into windows.
      * @param	window_duration		The duration in seconds of each window.
      * @param	window_overlap_offset   The number of seconds that windows are
      *                                 offset by. A value of zero means that
      *                                 there is no window overlap.
     * @param window_start_ticks
     * @param window_end_ticks
      * @return				An array of sequences representing the
      *					windows of the original sequence in
      *					consecutive order.
      * @throws	Exception		Throws an informative exception if the
      *					MIDI file uses SMTPE timing instead of
      *					PPQ timing or if it is too large.
      */
     public static Sequence[] breakSequenceIntoWindows( Sequence original_sequence,
                                                        double window_duration,
                                                        double window_overlap_offset,
                                                        int[] window_start_ticks,
                                                        int[] window_end_ticks)
          throws Exception
     {
          if (original_sequence.getDivisionType() != Sequence.PPQ)
               throw new Exception("The specified MIDI sequence uses SMPTE time encoding." +
                    "\nOnly PPQ time encoding is accepted here.");
          if ( ((double) original_sequence.getTickLength()) > ((double) Integer.MAX_VALUE) - 1.0)
               throw new Exception("The MIDI sequence could not be processed because it is too long.");
          
          // Prepare the sequences representing each window of MIDI data and the tracks in
          // each sequence
          Sequence[] windowed_sequences = new Sequence[window_start_ticks.length];
          Track[][] windowed_tracks = new Track[window_start_ticks.length][];
          for (int win = 0; win < windowed_sequences.length; win++)
          {
               windowed_sequences[win] = new Sequence( original_sequence.getDivisionType(),
                    original_sequence.getResolution(),
                    original_sequence.getTracks().length );
               windowed_tracks[win] = windowed_sequences[win].getTracks();
          }
          
          // Prepare the original tracks of MIDI data
          Track[] original_tracks = original_sequence.getTracks();
               
          int current_sequence_index = 0;
          for(int track_index = 0; track_index < original_tracks.length; track_index++) 
          {
              Track originalTrack = original_tracks[track_index];
              // key = meta midi byte, value = List of special midi messages
              HashMap<Byte,MidiEvent> thisTrackSpecialEvents = new HashMap<>(); //hashmap for each track
              for(int event_index = 0; event_index < originalTrack.size(); event_index++) 
              {
                  //Get all required data needed for window
                  MidiEvent thisEvent = originalTrack.get(event_index);
                  int startTick = (int)thisEvent.getTick();
                  int sequence_index = getSequenceIndex(startTick, window_start_ticks, window_end_ticks);
                  int current_sequence_start_tick = window_start_ticks[sequence_index];
                  int normalized_tick = startTick - current_sequence_start_tick;
                  Track thisTrack = windowed_tracks[sequence_index][track_index];
                  
                  //Check for special events and if we need to copy to sequence
                  checkForSpecialMidiEvent(thisEvent, thisTrackSpecialEvents);
                  current_sequence_index = checkForNewSequence(current_sequence_index, 
                                                          current_sequence_start_tick, 
                                                                       sequence_index, 
                                                                            thisTrack, 
                                                              thisTrackSpecialEvents);
                  
                  //Normalize event to speicfied sequence and add to track
                  MidiEvent normalizedTickEvent = getDeepCopyMidiEventWithNewTick(thisEvent, normalized_tick);
                  thisTrack.add(normalizedTickEvent);
              }
          }
          // Return the windows of MIDI data
          return windowed_sequences;
     }
     
     public static List<int[]> getStartEndTickArrays(Sequence original_sequence,
                                                        double window_duration,
                                                        double window_overlap_offset,
                                                        double[] seconds_per_tick) throws Exception {
         if (original_sequence.getDivisionType() != Sequence.PPQ)
               throw new Exception("The specified MIDI sequence uses SMPTE time encoding." +
                    "\nOnly PPQ time encoding is accepted here.");
          if ( ((double) original_sequence.getTickLength()) > ((double) Integer.MAX_VALUE) - 1.0)
               throw new Exception("The MIDI sequence could not be processed because it is too long.");
          
          // Calculate the window start and end tick indices
          LinkedList<Integer> window_start_ticks_list = new LinkedList<Integer>();
          LinkedList<Integer> window_end_ticks_list = new LinkedList<Integer>();
          double total_duration = original_sequence.getMicrosecondLength() / 1000000.0;
          double time_interval_to_next_tick = window_duration - window_overlap_offset;
          boolean found_next_tick = false;
          int tick_of_next_beginning = 0;
          int this_tick = 0;
          double total_seconds_accumulated_so_far = 0.0;
          while (total_seconds_accumulated_so_far < total_duration && this_tick < seconds_per_tick.length)
          {
               window_start_ticks_list.add(new Integer(this_tick));
               double seconds_accumulated_so_far = 0.0;
               while (seconds_accumulated_so_far < window_duration && this_tick < seconds_per_tick.length)
               {
                    seconds_accumulated_so_far += seconds_per_tick[this_tick];
                    this_tick++;
                    if (!found_next_tick)
                         if (seconds_accumulated_so_far > time_interval_to_next_tick)
                         {
                         tick_of_next_beginning = this_tick;
                         found_next_tick = true;
                         }
               }
               window_end_ticks_list.add(new Integer(this_tick - 1));
               if (found_next_tick)
                    this_tick = tick_of_next_beginning;
               found_next_tick = false;
               total_seconds_accumulated_so_far += seconds_accumulated_so_far - window_overlap_offset;
          }
          
          // Store the window start and end tick indices
          Integer[] window_start_ticks_I = window_start_ticks_list.toArray(new Integer[1]);
          int[] window_start_ticks = new int[window_start_ticks_I.length];
          for (int i = 0; i < window_start_ticks.length; i++)
               window_start_ticks[i] = window_start_ticks_I[i].intValue();
          Integer[] window_end_ticks_I = window_end_ticks_list.toArray(new Integer[1]);
          int[] window_end_ticks = new int[window_end_ticks_I.length];
          for (int i = 0; i < window_end_ticks.length; i++)
               window_end_ticks[i] = window_end_ticks_I[i].intValue();
          
          List<int[]> startEndTickList = new ArrayList<>();
          startEndTickList.add(window_start_ticks);
          startEndTickList.add(window_end_ticks);
          
          return startEndTickList;
     }
     
     /**
      * Check if we have new sequence to add special Events.
      * WILL NOT ADD TO A SEQUENCE IF THAT SEQUENCE TIME RANGE HAS NO MIDI DATA.
      * @param current_sequence_index
      * @param sequence_index
      * @param thisTrack
      * @param thisTrackSpecialEvents
      * @throws Exception 
      */
     private static int checkForNewSequence( int current_sequence_index,
                                             int current_sequence_start_tick,
                                             int sequence_index,
                                             Track thisTrack,
                                 HashMap<Byte,MidiEvent> thisTrackSpecialEvents) 
             throws Exception
     {
         if(current_sequence_index != sequence_index) 
         {
             replaceAllTicksToThisSequence(current_sequence_start_tick,thisTrackSpecialEvents);
             thisTrackSpecialEventsToNextSequence(thisTrack, 
                                                  thisTrackSpecialEvents);
             current_sequence_index = sequence_index;
         }  
         if(sequence_index == -1) 
         {
             throw new Exception("Array index does not match up with window ticks");
         }
         return current_sequence_index;
     }
     
     /**
      * Check whether you need to add thisEvent to the special events hash or not.
      * @param thisEvent
      * @param thisTrackSpecialEvents 
      */
     private static void checkForSpecialMidiEvent(MidiEvent thisEvent,
                                 HashMap<Byte,MidiEvent> thisTrackSpecialEvents)
     {
         MidiMessage thisMessage = thisEvent.getMessage();
         
         //COULD ALSO DO THIS BASED OF MIDIMETASTATUSHASH
         //If special status byte then add to special message list
         if(statusByteIsSpecial(thisMessage)) 
         {
             Byte midiStatusHash = getMidiMetaStatusHash(thisEvent);
             thisTrackSpecialEvents.put(midiStatusHash, thisEvent);
         }        
     }
     
     /**
      * Used to replace ticks to current sequence window when changing window
      * and copying over special midi events to new window.
      * Copy at tick 0 because we want to start in a new window sequence.
      * @param window_start_tick
      * @param thisTrackSpecialEvents 
      */
     private static void replaceAllTicksToThisSequence(int window_start_tick,
                              HashMap<Byte,MidiEvent> thisTrackSpecialEvents) {
         for(Byte key : thisTrackSpecialEvents.keySet()) {
             MidiEvent originalEvent = thisTrackSpecialEvents.get(key);
             MidiEvent eventCopy = getDeepCopyMidiEventWithNewTick(originalEvent,0);
             thisTrackSpecialEvents.replace(key, eventCopy);
         }
     }
     
     /**
      * Returns a deep copy of the given midi event with a new starting tick.
      * Deep copy in this case is a newly instantiated object with the same
      * midi message but a new starting tick.
      * @param originalEvent
      * @param new_tick
      * @return Copy of originalEvent with new_tick.
      */
     private static MidiEvent getDeepCopyMidiEventWithNewTick(MidiEvent originalEvent,
                                                                int new_tick) {
         MidiMessage originalMessage = originalEvent.getMessage();
         return new MidiEvent(originalMessage, new_tick);
     }
     
     /**
      * Gets the second byte in the midi meta data array and uses this
      * as the hash code to store special midi events.
      * @param newEvent
      * @return 
      */
     private static Byte getMidiMetaStatusHash(MidiEvent newEvent) {
         MidiMessage message = newEvent.getMessage();
         byte[] newMidiMessageArray = message.getMessage();
         int status = message.getStatus();
         Byte metaTypeByte;
         //program change
         if(status == 192) {
            metaTypeByte = newMidiMessageArray[0];
         }
         //meta-message
         else // if status == 255 
         {
             metaTypeByte = newMidiMessageArray[1];
         }
         return metaTypeByte;
     }
     
     /**
      * Adds all special messages to new track for a new sequence window.
      * @param track
      * @param thisTrackSpecialEvents 
      */
     private static void thisTrackSpecialEventsToNextSequence(Track track, 
                               HashMap<Byte,MidiEvent> thisTrackSpecialEvents) {
         for(MidiEvent event : thisTrackSpecialEvents.values()) {
             track.add(event);
         }
     }
     
     /**
      * Switch statement for all special midi message status bytes.
      * Use status byte to work for meta messages we may not know about.
      * COULD USE META BYTE (MIDIMETASTATUSHASH) INSTEAD OF STATUS BYTE.
      * @param status
      * @return true for 255 or else false
      */
     private static boolean statusByteIsSpecial(MidiMessage message) {
         int status = message.getStatus();
         switch (status) {
             case 255 : return true; //meta message
             case 192 : return true; //program change
             default : return false;
         }
     }
     
     /**
      * Helper method for breakSequenceIntoWindows to validate tick indices.
      * @param thisTick
      * @param window_start_ticks
      * @param window_end_ticks 
      */
     private static int getSequenceIndex(long thisTick, int[] window_start_ticks, int[] window_end_ticks) {
         int intTick = (int)thisTick; //checked 
         
         //CHECK FOR OFF BY MINUS ONE ERROR
         //Check if thisTick is greater than last tick
         //or else make it this tick
         int lastIndex = window_end_ticks.length - 1;
         if(intTick > window_end_ticks[lastIndex]) {
             return lastIndex;
         }
         
         //Check all ticks to find proper window
         for(int i = 0; i < window_start_ticks.length; i++) {
             if(window_start_ticks[i] <= intTick && window_end_ticks[i] >= intTick) {
                 return i;
             }
         }
         return -1; //this only happens if an error occured
     }
}
