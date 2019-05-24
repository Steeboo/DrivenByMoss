// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mcu.command.pitchbend;

import de.mossgrabers.controller.mcu.MCUConfiguration;
import de.mossgrabers.controller.mcu.controller.MCUControlSurface;
import de.mossgrabers.framework.command.core.AbstractPitchbendCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.mode.Modes;


/**
 * Command to handle pitchbend.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PitchbendVolumeCommand extends AbstractPitchbendCommand<MCUControlSurface, MCUConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public PitchbendVolumeCommand (final IModel model, final MCUControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void onPitchbend (final int channel, final int data1, final int data2)
    {
        final int value = Math.min (data2 * 127 + data1, this.model.getValueChanger ().getUpperBound () - 1);
        if (channel == 8)
        {
            if (this.surface.isShiftPressed ())
                this.model.getTransport ().setMetronomeVolume (value);
            else
                this.model.getMasterTrack ().setVolume (value);
            return;
        }

        final int extenderOffset = this.surface.getExtenderOffset ();
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final ITrack track = tb.getItem (extenderOffset + channel);
        if (this.surface.getConfiguration ().useFadersAsKnobs ())
        {
            final ModeManager modeManager = this.surface.getModeManager ();
            if (modeManager.isActiveOrTempMode (Modes.MODE_VOLUME))
                track.setVolume (value);
            else if (modeManager.isActiveOrTempMode (Modes.MODE_PAN))
                track.setPan (value);
            else if (modeManager.isActiveOrTempMode (Modes.MODE_TRACK))
                this.handleTrack (channel, value);
            else if (modeManager.isActiveOrTempMode (Modes.MODE_SEND1))
                track.getSendBank ().getItem (0).setValue (value);
            else if (modeManager.isActiveOrTempMode (Modes.MODE_SEND2))
                track.getSendBank ().getItem (1).setValue (value);
            else if (modeManager.isActiveOrTempMode (Modes.MODE_SEND3))
                track.getSendBank ().getItem (2).setValue (value);
            else if (modeManager.isActiveOrTempMode (Modes.MODE_SEND4))
                track.getSendBank ().getItem (3).setValue (value);
            else if (modeManager.isActiveOrTempMode (Modes.MODE_SEND5))
                track.getSendBank ().getItem (4).setValue (value);
            else if (modeManager.isActiveOrTempMode (Modes.MODE_SEND6))
                track.getSendBank ().getItem (5).setValue (value);
            else if (modeManager.isActiveOrTempMode (Modes.MODE_SEND7))
                track.getSendBank ().getItem (6).setValue (value);
            else if (modeManager.isActiveOrTempMode (Modes.MODE_SEND8))
                track.getSendBank ().getItem (7).setValue (value);
            else if (modeManager.isActiveOrTempMode (Modes.MODE_DEVICE_PARAMS))
                this.model.getCursorDevice ().getParameterBank ().getItem (extenderOffset + channel).setValue (value);
            return;
        }

        track.setVolume (value);
    }


    private void handleTrack (final int index, final int value)
    {
        final ITrack selectedTrack = this.model.getSelectedTrack ();
        switch (index)
        {
            case 0:
                selectedTrack.setVolume (value);
                break;

            case 1:
                selectedTrack.setPan (value);
                break;

            case 2:
                if (this.surface.getConfiguration ().isDisplayCrossfader ())
                {
                    final double range = this.model.getValueChanger ().getUpperBound () / 3.0;
                    selectedTrack.setCrossfadeModeAsNumber ((int) Math.round (value / range));
                }
                else if (!this.model.isEffectTrackBankActive ())
                    selectedTrack.getSendBank ().getItem (0).setValue (value);
                break;

            default:
                if (!this.model.isEffectTrackBankActive ())
                    selectedTrack.getSendBank ().getItem (index - (this.surface.getConfiguration ().isDisplayCrossfader () ? 3 : 2)).setValue (value);
                break;
        }
    }
}
