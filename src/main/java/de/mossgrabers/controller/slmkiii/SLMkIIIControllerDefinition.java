// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.slmkiii;

import de.mossgrabers.framework.controller.DefaultControllerDefinition;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.utils.Pair;

import java.util.List;
import java.util.UUID;


/**
 * Definition class for the Novation SL MkIII controller extension.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SLMkIIIControllerDefinition extends DefaultControllerDefinition
{
    private static final UUID EXTENSION_ID = UUID.fromString ("20C2BF16-8D2A-4C00-8D55-27FA2E6EAA07");


    /**
     * Constructor.
     */
    public SLMkIIIControllerDefinition ()
    {
        super (EXTENSION_ID, "SL MkIII", "Novation", 2, 1);
    }


    /** [{@inheritDoc} */
    @Override
    public List<Pair<String [], String []>> getMidiDiscoveryPairs (final OperatingSystem os)
    {
        final List<Pair<String [], String []>> midiDiscoveryPairs = super.getMidiDiscoveryPairs (os);

        if (os == OperatingSystem.MAC)
        {
            midiDiscoveryPairs.add (this.addDeviceDiscoveryPair (new String []
            {
                "SL MkIII MIDI 2",
                "SL MkIII MIDI 1"
            }, new String []
            {
                "SL MkIII MIDI 2"
            }));
        }
        else
        {
            // WINDOWS + Linux
            midiDiscoveryPairs.add (this.addDeviceDiscoveryPair (new String []
            {
                "MIDIIN2 (Novation SL MkIII)",
                "Novation SL MkIII"
            }, new String []
            {
                "MIDIOUT2 (Novation SL MkIII)"
            }));
        }

        return midiDiscoveryPairs;
    }
}
