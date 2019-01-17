// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchpad.command.trigger;

import de.mossgrabers.controller.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.controller.launchpad.mode.Modes;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Track mute command.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MuteCommand extends AbstractTrackCommand
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public MuteCommand (final IModel model, final LaunchpadControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event)
    {
        this.onModeButton (event, Modes.MODE_MUTE, "Mute");
    }
}
