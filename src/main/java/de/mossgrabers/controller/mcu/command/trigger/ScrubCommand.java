// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mcu.command.trigger;

import de.mossgrabers.controller.mcu.MCUConfiguration;
import de.mossgrabers.controller.mcu.controller.MCUControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to toggles between Track and Device editing mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ScrubCommand extends AbstractTriggerCommand<MCUControlSurface, MCUConfiguration>
{
    private ModeSwitcher switcher;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public ScrubCommand (final IModel model, final MCUControlSurface surface)
    {
        super (model, surface);
        this.switcher = new ModeSwitcher (surface);
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event == ButtonEvent.DOWN)
            this.switcher.scrollUp ();
    }
}
