/*
 * MIDIMethods.java
 *
 * Last modified on May 19, 2016.
 * Marianopolis College, McGill University and University of Waikato
 */

package mckay.utilities.sound.midi;

import javax.sound.midi.*;
import java.io.*;
import java.math.BigInteger;
import java.util.*;


/**
 * A holder class for static methods relating to MIDI.
 *
 * @author Cory McKay and Tristano Tenaglia
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
          // Note that we round down with int due to error obtained from the getMicroSecondLength() functions.
          // This returns inconsistent values with respect to the placement of notes relative to the PPQ.
          // This causes a very subtle error due to what seems like rounding at the lower end of the decimal
          // point area. Converting to int eliminates this minor error and proves to be consistent due to the
          // fact that the second length is much less than the tick length in all cases
          // (i.e. varied PPQs and midi event tick placements).
          //TODO check if this solution of going from int to double is correct, maybe better to round but probably not
          double mean_ticks_per_sec = (int)(((double) sequence.getTickLength()) / ((double) sequence.getMicrosecondLength() / 1000000.0));

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
     * Returns the cummulative seconds up until the given at_tick.
     * @param at_tick Tick to stop at (including).
     * @param seconds_per_tick Number of seconds per tick.
     * @return Returns the cummulative seconds up until the given at_tick.
     */
     public static double getSecondsAtTick(int at_tick, double[] seconds_per_tick) {
         double seconds_at_tick = 0.0;
         for(int tick = 0; tick <= at_tick; tick++) {
             seconds_at_tick += seconds_per_tick[tick];
         }
         return seconds_at_tick;
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
      * @param window_start_ticks       MIDI start ticks corresponding
      *                                 to each MIDI sequence window.
      * @param window_end_ticks         MIDI end ticks corresponding
      *                                 to each MIDI sequence window.
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

          //Add an immutable end of track message to each track so we know
          //where the true end of track is with respect to the original sequence.
          //This is a natural solution to passing meta messages through subsequent windows
          for(Track track : original_tracks) {
              MidiMessage endOfTrack = new MetaMessage(0x2F, new byte[]{}, 0);
              track.add(new MidiEvent(endOfTrack, original_sequence.getTickLength()));
          }
               
          int current_sequence_index = 0;
          for(int track_index = 0; track_index < original_tracks.length; track_index++) 
          {
              Track originalTrack = original_tracks[track_index];
              MIDISpecialEvents specialEvents = new MIDISpecialEvents();
              for(int event_index = 0; event_index < originalTrack.size(); event_index++)
              {
                  //Get all required data needed for window
                  MidiEvent thisEvent = originalTrack.get(event_index);
                  int startTick = (int)thisEvent.getTick();
                  int sequence_index = getSequenceIndex(startTick, window_start_ticks, window_end_ticks);
                  Track thisTrack = windowed_tracks[sequence_index][track_index];
                  
                  //Check for special events and if we need to copy to new window sequence
                  current_sequence_index = checkForNewSequence(current_sequence_index, sequence_index, track_index,
                          thisTrack, specialEvents, windowed_tracks, window_start_ticks, window_end_ticks);
                  checkForSpecialMidiEvent(thisEvent, specialEvents);

                  //Normalize event to specified sequence and add to track
                  //Then add this event to all appropriate windowed sequences
                  passEventToAllAppropriateWindows(thisEvent, window_overlap_offset, sequence_index, startTick,
                          track_index, window_start_ticks, window_end_ticks, windowed_tracks);
              }
          }

          // Return the windows of MIDI data
          return windowed_sequences;
     }

    /**
     * Used for general testing of midi events within a particular track.
     * Currently, it will print out detailed information about tempo messages
     * and time signature messages.
     * @param thisEvent The event to be tested and printed out.
     * @param track_index The track in which this event occurs for convenience.
     */
     public static void checkMetaMessages(MidiEvent thisEvent, int track_index) {
         int startTick = (int)thisEvent.getTick();
         if(thisEvent.getMessage() instanceof MetaMessage) {
             MetaMessage m = (MetaMessage)thisEvent.getMessage();
             if(m.getType() == 0x51) { //tempo message
                 byte[] data = m.getData();
                 String byteOne = byteArrayToString(data);
                 int value = new BigInteger(byteOne, 16).intValue();
                 System.out.println("On track " + track_index +
                         " Tempo Message : " + 60000000.0 / value + " bpm" +
                         " at tick  : " + startTick);
             }
             else if(m.getType() == 0x58) { //time signature message
                 byte[] data = m.getData();
                 int numerator = data[0];
                 int denominator = (int)Math.round(Math.pow(2, data[1]));
                 System.out.println("On track " + track_index +
                         " Time Signature : " + numerator + ":" + denominator +
                         " at tick : " + startTick);
             }
         }
     }

    private static String byteArrayToString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
     
     /**
      * Get the start and end tick arrays so that they only need to be processed
      * once during general file processing.
      * @param original_sequence the original sequence to be processed
      * @param window_duration window duration given by user
      * @param window_overlap_offset window overlap offset given by user
      * @param seconds_per_tick number of seconds per tick for the given original_sequence
      * @return a list of int[] where list.get(0) will return startTicks[] and
      *         list.get(1) will return endTicks[]
      * @throws Exception	Throws an informative exception if a problem occurs.
      */
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
          List<Integer> window_start_ticks_list = new LinkedList<>();
          List<Integer> window_end_ticks_list = new LinkedList<>();
          double total_duration = original_sequence.getMicrosecondLength() / 1000000.0;
          double time_interval_to_next_tick = window_duration - window_overlap_offset;
          boolean found_next_tick = false;
          int tick_of_next_beginning = 0;
          int this_tick = 0;
          double total_seconds_accumulated_so_far = 0.0;
          while (total_seconds_accumulated_so_far <= total_duration && this_tick < seconds_per_tick.length)
          {
               window_start_ticks_list.add(new Integer(this_tick));
               double seconds_accumulated_so_far = 0.0;
               while (seconds_accumulated_so_far < window_duration && this_tick < seconds_per_tick.length)
               {
                    seconds_accumulated_so_far += seconds_per_tick[this_tick];
                    this_tick++;
                    if (!found_next_tick && seconds_accumulated_so_far >= time_interval_to_next_tick)
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

          List<int[]> startEndTickList = new ArrayList<>();
          int[] start_tick_array = Arrays.stream(window_start_ticks_list.toArray(new Integer[0])).mapToInt(i->i).toArray();
          int[] end_tick_array = Arrays.stream(window_end_ticks_list.toArray(new Integer[0])).mapToInt(i->i).toArray();
          startEndTickList.add(start_tick_array);
          startEndTickList.add(end_tick_array);
          return startEndTickList;
     }

     
     /**
      * This Event is passed on to the given sequence_index and track_index
      * corresponding to the windowed_tracks array. The start tick of the event
      * is therefore normalized to the given windowed sequence.
      * If the event occurs in any previous window, then it is also copied in those
      * windows. This accounts for window overlap offset functionality. This will only run
      * once if we do not have window overlap offset since each event occurs in exactly
      * one window.
      * @param thisEvent the event to be added to the sequence
	  * @param window_overlap_offset the window offset (left here for convenience)
      * @param sequence_index the current sequence index before this function call
      * @param startTick the start of this event
      * @param track_index the track index that the current event was taken from
      * @param window_start_ticks all sequence window start ticks
      * @param window_end_ticks all sequence window end ticks
      * @param windowed_tracks all windowed tracks
      */
     public static void passEventToAllAppropriateWindows(MidiEvent thisEvent,
                                                         double window_overlap_offset, //left here for convenience
                                                         int sequence_index,
                                                         int startTick,
                                                         int track_index,
                                                         int[] window_start_ticks,
                                                         int[] window_end_ticks,
                                                         Track[][] windowed_tracks)
     {
         for(int loop_index = sequence_index; loop_index >= 0; loop_index--)
         {
             //TODO This check added due to added tick passed last window_end_ticks value
             //TODO Could maybe change if last window_end_ticks would end on actual last tick
             if(loop_index == window_end_ticks.length - 1 &&
                     startTick == window_end_ticks[window_end_ticks.length - 1] + 1)
             {
                //Then do nothing and don't make the following check
             }
             else if(startTick > window_end_ticks[loop_index]) {
                 break;
             }
             int current_sequence_start_tick = window_start_ticks[loop_index];
             int normalized_tick = startTick - current_sequence_start_tick;
             Track thisTrack = windowed_tracks[loop_index][track_index];
             MidiEvent normalizedTickEvent = getDeepCopyMidiEventWithNewTick(thisEvent, normalized_tick);
             thisTrack.add(normalizedTickEvent);
         }
     }
     
     /**
      * Check if we have new sequence to add special Events.
      * WILL NOT ADD TO A SEQUENCE IF THAT SEQUENCE TIME RANGE HAS NO MIDI DATA.
      * @param current_sequence_index current index of the sequence in the array
      * @param sequence_index the next indexed sequence
      * @param thisTrack the track which needs to be checked for special events
      * @param specialEvents the maps of special midi events
      * @throws Exception 
      */
     private static int checkForNewSequence( int current_sequence_index,
                                             int sequence_index,
                                             int track_index,
                                             Track thisTrack,
                                             MIDISpecialEvents specialEvents,
                                             Track[][] windowed_tracks,
                                             int[] sequence_start_ticks,
                                             int[] sequence_end_ticks)
             throws Exception
     {
         if(current_sequence_index != sequence_index) 
         {
             replaceAllTicksToThisSequence(specialEvents);
             thisTrackSpecialEventsToNextSequence(thisTrack, track_index, specialEvents, current_sequence_index,
                     sequence_index, windowed_tracks, sequence_start_ticks, sequence_end_ticks);
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
      * Also check for note ons so that we can keep track of notes that are still on
      * at the end of a sequence to pass them to next sequence.
      * It is worth noting that this is used together with
      * @param thisEvent the event to be added to special events list
      * @param specialEvents the special events that will be added to
      */
     private static void checkForSpecialMidiEvent(MidiEvent thisEvent, MIDISpecialEvents specialEvents)
     {
         MidiMessage thisMessage = thisEvent.getMessage();
         HashMap<Byte,MidiEvent> thisTrackSpecialEvents = specialEvents.getThisTrackSpecialEvents();
         HashMap<Integer,MidiEvent> notesToNextSequence = specialEvents.getNotesToNextSequence();
         //COULD ALSO DO THIS BASED OF MIDIMETASTATUSHASH
         //If special status byte then add to special message list
         if(statusByteIsSpecial(thisMessage))
         {
             Byte midiStatusHash = getMidiMetaStatusHash(thisEvent);
             thisTrackSpecialEvents.put(midiStatusHash, thisEvent);
         }
         else if(thisEvent.getMessage() instanceof ShortMessage) {
             ShortMessage note = (ShortMessage)thisEvent.getMessage();
             int pitch = note.getData1();
             if(note.getCommand() == 0x90 && //note on
                     note.getData2() != 0) { //not 0 velocity
                 //add note when we find it starts
                notesToNextSequence.put(pitch,thisEvent);
             }
             else if(note.getCommand() == 0x80 || //note off
                     (note.getCommand() == 0x90 && //note on with 0 velocity
                             note.getData2() == 0)) {
                 //remove note when we get note off
                 notesToNextSequence.remove(pitch);
             }
         }
     }

     /**
      * Used to replace ticks to current sequence window when changing window
      * and copying over special midi events to new window.
      * Copy at tick 0 because we want to start in a new window sequence.
      * @param specialEvents the special events currently in this track
      */
     private static void replaceAllTicksToThisSequence(MIDISpecialEvents specialEvents) {
         //Copy special events (e.g. program changes and meta messages)
         HashMap<Byte,MidiEvent> thisTrackSpecialEvents = specialEvents.getThisTrackSpecialEvents();
         for(Byte key : thisTrackSpecialEvents.keySet()) {
             MidiEvent originalEvent = thisTrackSpecialEvents.get(key);
             MidiEvent eventCopy = getDeepCopyMidiEventWithNewTick(originalEvent,0);
             thisTrackSpecialEvents.replace(key, eventCopy);
         }
         //Copy note ons that have not been turned off between windows
         HashMap<Integer,MidiEvent> notesToNextSequence = specialEvents.getNotesToNextSequence();
         for(Integer pitch : notesToNextSequence.keySet()) {
             MidiEvent originalEvent = notesToNextSequence.get(pitch);
             MidiEvent eventCopy = getDeepCopyMidiEventWithNewTick(originalEvent,0);
             notesToNextSequence.replace(pitch, eventCopy);
         }
     }
     
     /**
      * Returns a deep copy of the given midi event with a new starting tick.
      * Deep copy in this case is a newly instantiated object with the same
      * midi message but a new starting tick.
      * @param originalEvent event with old tick
      * @param new_tick new tick to be added to deep copy of event
      * @return Deep copy of originalEvent with new_tick
      */
     private static MidiEvent getDeepCopyMidiEventWithNewTick(MidiEvent originalEvent,int new_tick)
     {
         MidiMessage originalMessage = originalEvent.getMessage();
         return new MidiEvent(originalMessage, new_tick);
     }
     
     /**
      * Adds all special messages to new track for a new sequence window.
      * @param track the track to be added to the special events list
      * @param specialEvents the special midi events that will be added
      */
     private static void thisTrackSpecialEventsToNextSequence(Track track,
                                                              int track_index,
                                                              MIDISpecialEvents specialEvents,
                                                              int current_sequence_index,
                                                              int sequence_index,
                                                              Track[][] windowed_tracks,
                                                              int[] sequence_start_ticks,
                                                              int[] sequence_end_ticks) {
         HashMap<Byte,MidiEvent> thisTrackSpecialEvents = specialEvents.getThisTrackSpecialEvents();
         for(MidiEvent event : thisTrackSpecialEvents.values()) {
             //Add special event to this sequence index
             track.add(event);
             //Add to all sequences from after it was added till sequence_index
             for(int seq_ind = current_sequence_index + 1; seq_ind < sequence_index; seq_ind++) {
                windowed_tracks[seq_ind][track_index].add(event);
             }
         }

         //maybe only do this if we do not have overlap offset
         HashMap<Integer,MidiEvent> notesToNextSequence = specialEvents.getNotesToNextSequence();
         for(MidiEvent event : notesToNextSequence.values()) {
             track.add(event);
             //For each event added to next sequence, add a note off to end of old sequence
             ShortMessage note_on = (ShortMessage) event.getMessage();
             for(int seq_ind = current_sequence_index; seq_ind < sequence_index; seq_ind++) {
                 //Add Note on to all corresponding sequence
                 //But no first one since we already added it there when we found it
                 if(seq_ind != current_sequence_index) {
                     windowed_tracks[seq_ind][track_index].add(event);
                 }
                 //Then add note offs to end of all corresponding sequences
                 int previous_sequence_end_tick = sequence_end_ticks[seq_ind] - sequence_start_ticks[seq_ind];
                 MidiEvent note_off_event = null;
                 try {
                     note_off_event = MidiBuildEvent.createNoteOffEvent(
                             note_on.getData1(),
                             previous_sequence_end_tick,
                             note_on.getChannel()
                     );
                 } catch (InvalidMidiDataException ex) {
                     //Do Nothing since this should never happen
                     System.err.println(ex.getMessage());
                 }
                 windowed_tracks[seq_ind][track_index].add(note_off_event);
             }
         }
     }
     
     /**
      * Switch statement for all special midi message status bytes.
      * Use status byte to work for meta messages we may not know about.
      * * It is worth noting that this needs to be updated with getMidiMetaStatusHash() function below.
      * @param message the midi message to be checked
      * @return true for 255 (meta-message) or 192-207 (program change), else false
      */
     private static boolean statusByteIsSpecial(MidiMessage message) {
         int status = message.getStatus();
         //NEED TO CHANGE HERE AND IN getMidiMetaStatusHash(MidiEvent)
         if(status == 255) {
             return true;
         } else if(status >= 192 && status <= 207) {
             return true;
         } else {
             return false;
         }
     }

    /**
     * Gets the second byte in the midi meta data array and uses this
     * as the hash code to store special midi events.
     * It is worth noting that this needs to be updated with statusByteIsSpecial() function above.
     * @param newEvent event containing the message
     * @return if we have a meta-message it will return the second data byte
     *         and if we have a program change, it will return the first data byte
     */
    private static Byte getMidiMetaStatusHash(MidiEvent newEvent) {
        MidiMessage message = newEvent.getMessage();
        byte[] newMidiMessageArray = message.getMessage();
        int status = message.getStatus();
        Byte metaTypeByte;
        //program change
        //NEED TO CHANGE HERE AND IN statusByteIsSpecial(MidiMessage)
        if(status >= 192 && status <= 207) {
            //Program change contains status byte 1100 XXXX
            //Where XXXX ranges between 16 bits i.e. 11000000 to 11001111 (= 192 to 207)
            metaTypeByte = newMidiMessageArray[0];
        }
        else /* if status == 255 */{
            //meta-message
            metaTypeByte = newMidiMessageArray[1];
        }
        return metaTypeByte;
    }

     /**
      * Helper method for breakSequenceIntoWindows to validate tick indices and
      * to be able to get the appropriate sequence window.
      * @param thisTick tick to be checked between start and end ticks
      * @param window_start_ticks array of start ticks
      * @param window_end_ticks array of end ticks
      * @return the appropriate index but if some error occurred -1
      */
     private static int getSequenceIndex(long thisTick, int[] window_start_ticks, int[] window_end_ticks) {
         int intTick = (int)thisTick; //checked 
         
         //Check if thisTick is greater than last start tick
         //this is useful for window overlaps that wont be recognized otherwise
         int lastIndex = window_end_ticks.length - 1;
         if(intTick > window_end_ticks[lastIndex]) {
             return lastIndex;
         }
         //Also check if it is in the last window and so just return it
         if(intTick >= window_start_ticks[lastIndex] &&
                 intTick <= window_end_ticks[lastIndex]) {
             return lastIndex;
         }
         
         //Check all ticks to find last proper window
         //This allows for natural copying of midi events to previous windows
         boolean found_index = false;
         int last_index_found = 0;
         for(int i = 0; i < window_start_ticks.length; i++) {
             if(window_start_ticks[i] <= intTick && window_end_ticks[i] >= intTick) {
                 found_index = true;
                 last_index_found = i;
             }
             else if(found_index && intTick <= window_start_ticks[i]) {
                 return last_index_found;
             }
         }
         return -1; //this only happens if an error occured
     }
}
