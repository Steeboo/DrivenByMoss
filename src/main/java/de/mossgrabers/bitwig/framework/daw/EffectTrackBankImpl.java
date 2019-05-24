// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw;

import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ITrackBank;

import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.controller.api.TrackBank;


/**
 * A track bank of all effect tracks.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class EffectTrackBankImpl extends AbstractTrackBankImpl
{
    private ITrackBank audioInstrumentTrackBank;


    /**
     * Constructor.
     *
     * @param host The host
     * @param valueChanger The value changer
     * @param cursorTrack The cursor track assigned to this track bank
     * @param effectTrackBank The effect track bank
     * @param numTracks The number of track of a bank page
     * @param numScenes The number of scenes of a bank page
     * @param audioInstrumentTrackBank The trackbank which monitors the audio and instrument tracks
     */
    public EffectTrackBankImpl (final IHost host, final IValueChanger valueChanger, final CursorTrack cursorTrack, final TrackBank effectTrackBank, final int numTracks, final int numScenes, final ITrackBank audioInstrumentTrackBank)
    {
        super (host, valueChanger, cursorTrack, effectTrackBank, numTracks, numScenes, 0);

        this.bank.followCursorTrack (cursorTrack);
        this.audioInstrumentTrackBank = audioInstrumentTrackBank;
    }


    /** {@inheritDoc} */
    @Override
    public void scrollTo (final int position)
    {
        super.scrollTo (position - this.audioInstrumentTrackBank.getItemCount ());
    }


    /** {@inheritDoc} */
    @Override
    public boolean canEditSend (final int sendIndex)
    {
        // Sends don't have sends.
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public String getEditSendName (final int sendIndex)
    {
        return "";
    }


    /** {@inheritDoc} */
    @Override
    public void selectParent ()
    {
        // Effect bank is flat
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasParent ()
    {
        // Effect bank is flat
        return false;
    }
}