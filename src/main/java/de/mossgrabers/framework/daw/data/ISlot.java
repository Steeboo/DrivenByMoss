// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data;

/**
 * Interface to a slot.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface ISlot extends IItem
{
    /**
     * Does the slot have content?
     *
     * @return True if it has content
     */
    boolean hasContent ();


    /**
     * Is the slot recording?
     *
     * @return True if it is recording
     */
    boolean isRecording ();


    /**
     * Is the slot playing?
     *
     * @return True if the slot is playing
     */
    boolean isPlaying ();


    /**
     * True if the slot is queued for playback.
     *
     * @return True if the slot is queued for playback.
     */
    boolean isPlayingQueued ();


    /**
     * True if the slot is queued for recording.
     *
     * @return True if the slot is queued for recording.
     */
    boolean isRecordingQueued ();


    /**
     * True if the slot is queued to be stopped.
     *
     * @return True if the slot is queued to be stopped.
     */
    boolean isStopQueued ();

    int getTrackPosition();


    /**
     * Get the color of the slot.
     *
     * @return The color
     */
    double [] getColor ();


    /**
     * Set the color of the clip.
     *
     * @param red The red
     * @param green The green
     * @param blue The blue
     */
    void setColor (double red, double green, double blue);


    /**
     * Launch a clip.
     */
    void launch ();


    /**
     * Record a clip.
     */
    void record ();


    /**
     * Create a clip.
     *
     * @param length The length of the clip
     */
    void create (final int length);


    /**
     * Delete a clip.
     */
    void remove ();


    /**
     * Duplicate a clip.
     */
    void duplicate ();


    /**
     * Opens the context browser to insert a clip.
     */
    void browse ();
}