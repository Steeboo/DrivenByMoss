// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw.data;

import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.data.IDrumPad;

import com.bitwig.extension.controller.api.DrumPad;


/**
 * The data of a channel.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DrumPadImpl extends LayerImpl implements IDrumPad
{
    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The valueChanger
     * @param drumPad The drum pad
     * @param index The index of the channel in the page
     * @param numSends The number of sends of a bank
     * @param numDevices The number of devices of a bank
     */
    public DrumPadImpl (final IHost host, final IValueChanger valueChanger, final DrumPad drumPad, final int index, final int numSends, final int numDevices)
    {
        super (host, valueChanger, drumPad, index, numSends, numDevices);
    }


    /** {@inheritDoc} */
    @Override
    public void browseToInsert ()
    {
        ((DrumPad) this.channel).insertionPoint ().browse ();
    }
}
