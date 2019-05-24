// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.sl.command.continuous;

import de.mossgrabers.controller.sl.SLConfiguration;
import de.mossgrabers.controller.sl.controller.SLControlSurface;
import de.mossgrabers.framework.command.core.AbstractContinuousCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.mode.Modes;


/**
 * Command to change the volumes of the current track bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class FaderCommand extends AbstractContinuousCommand<SLControlSurface, SLConfiguration>
{
    private int index;


    /**
     * Constructor.
     *
     * @param index The index of the button
     * @param model The model
     * @param surface The surface
     */
    public FaderCommand (final int index, final IModel model, final SLControlSurface surface)
    {
        super (model, surface);
        this.index = index;
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final int value)
    {
        final IMasterTrack masterTrack = this.model.getMasterTrack ();
        if (masterTrack.isSelected ())
        {
            if (this.index == 0)
                masterTrack.setVolume (value);
        }
        else
            this.model.getCurrentTrackBank ().getItem (this.index).setVolume (value);

        final ModeManager modeManager = this.surface.getModeManager ();
        if (!modeManager.isActiveOrTempMode (Modes.MODE_VOLUME))
            modeManager.setActiveMode (Modes.MODE_VOLUME);
    }
}
