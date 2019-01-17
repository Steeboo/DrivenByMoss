// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw;

import de.mossgrabers.framework.daw.data.ITrack;


/**
 * Interface to a track bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface ITrackBank extends IChannelBank<ITrack>
{
    /**
     * Selects the first child if this is a group track.
     */
    void selectChildren ();


    /**
     * Selects the parent track if any (track must be inside a group).
     */
    void selectParent ();


    /**
     * Returns true if there is a parent track.
     *
     * @return True if there is a parent track
     */
    boolean hasParent ();


    /**
     * Returns true if one of the clips of the current bank page is recording.
     *
     * @return True if one of the clips of the current bank page is recording
     */
    boolean isClipRecording ();


    /**
     * Check if there is a send at the given index, which can be edited.
     *
     * @param sendIndex The index of the send
     * @return True if there is a send to edit
     */
    boolean canEditSend (int sendIndex);


    /**
     * DAWs which can put different sends in a slot can return here a name to be displayed for a
     * slot.
     *
     * @param sendIndex The index of the send
     * @return The name to display
     */
    String getEditSendName (int sendIndex);
}