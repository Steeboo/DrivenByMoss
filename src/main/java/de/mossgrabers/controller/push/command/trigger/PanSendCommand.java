// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.command.trigger;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.controller.push.mode.Modes;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to edit the Pan and Sends of tracks.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PanSendCommand extends AbstractTriggerCommand<PushControlSurface, PushConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public PanSendCommand (final IModel model, final PushControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final ModeManager modeManager = this.surface.getModeManager ();
        final Integer currentMode = modeManager.getActiveOrTempModeId ();

        // Layer mode selection for Push 1
        Integer mode;
        final PushConfiguration config = this.surface.getConfiguration ();
        if (!config.isPush2 () && this.surface.isSelectPressed () && Modes.isLayerMode (currentMode))
        {
            if (this.model.isEffectTrackBankActive ())
            {
                // No Sends on FX tracks
                mode = Modes.MODE_DEVICE_LAYER_PAN;
            }
            else
            {
                mode = Integer.valueOf (currentMode.intValue () + 1);
                // Wrap
                if (mode.intValue () < Modes.MODE_DEVICE_LAYER_PAN.intValue () || mode.intValue () > Modes.MODE_DEVICE_LAYER_SEND8.intValue ())
                    mode = Modes.MODE_DEVICE_LAYER_PAN;
                // Check if Send channel exists
                final ITrackBank tb = this.model.getTrackBank ();
                if (mode.intValue () < Modes.MODE_DEVICE_LAYER_SEND1.intValue () || mode.intValue () > Modes.MODE_DEVICE_LAYER_SEND8.intValue () || tb.canEditSend (mode.intValue () - Modes.MODE_DEVICE_LAYER_SEND1.intValue ()))
                    mode = Modes.MODE_DEVICE_LAYER_PAN;
            }
            modeManager.setActiveMode (mode);
            return;
        }

        if (this.model.isEffectTrackBankActive ())
        {
            // No Sends on FX tracks
            mode = Modes.MODE_PAN;
        }
        else
        {
            mode = Integer.valueOf (currentMode.intValue () + 1);
            // Wrap
            if (mode.intValue () < Modes.MODE_PAN.intValue () || mode.intValue () > Modes.MODE_SEND8.intValue ())
                mode = Modes.MODE_PAN;
            // Check if Send channel exists
            final ITrackBank tb = this.model.getTrackBank ();
            if (mode.intValue () < Modes.MODE_SEND1.intValue () || mode.intValue () > Modes.MODE_SEND8.intValue () || !tb.canEditSend (mode.intValue () - Modes.MODE_SEND1.intValue ()))
                mode = Modes.MODE_PAN;
        }
        modeManager.setActiveMode (mode);
    }
}
