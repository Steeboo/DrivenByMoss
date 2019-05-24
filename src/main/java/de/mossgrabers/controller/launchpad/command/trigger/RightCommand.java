// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchpad.command.trigger;

import de.mossgrabers.controller.launchpad.LaunchpadConfiguration;
import de.mossgrabers.controller.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.command.trigger.transport.MetronomeCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.View;


/**
 * Command to dis-/enable the metronome. Also toggles metronome ticks when Shift is pressed.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class RightCommand extends MetronomeCommand<LaunchpadControlSurface, LaunchpadConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public RightCommand (final IModel model, final LaunchpadControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event)
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final ITrack sel = tb.getSelectedItem ();
        final int index = sel == null ? 0 : sel.getIndex () + 1;
        final View view = this.surface.getViewManager ().getActiveView ();
        if (index == 8 || this.surface.isShiftPressed ())
        {
            if (!tb.canScrollPageForwards ())
                return;
            tb.selectNextPage ();
            final int newSel = index == 8 || sel == null ? 0 : sel.getIndex ();
            this.surface.scheduleTask ( () -> view.selectTrack (newSel), 75);
            return;
        }
        view.selectTrack (index);
    }
}
