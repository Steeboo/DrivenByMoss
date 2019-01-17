// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.command.trigger;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.controller.push.mode.Modes;
import de.mossgrabers.controller.push.view.Views;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.ViewManager;


/**
 * Command to display a selection for the play modes.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SelectPlayViewCommand extends AbstractTriggerCommand<PushControlSurface, PushConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public SelectPlayViewCommand (final IModel model, final PushControlSurface surface)
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
        final ViewManager viewManager = this.surface.getViewManager ();
        if (Views.isSessionView (viewManager.getActiveViewId ()))
        {
            final ITrack selectedTrack = this.model.getSelectedTrack ();
            if (selectedTrack == null)
            {
                this.surface.getDisplay ().notify ("Please select a track first.");
                return;
            }

            final Integer preferredView = viewManager.getPreferredView (selectedTrack.getPosition ());
            viewManager.setActiveView (preferredView == null ? Views.VIEW_PLAY : preferredView);

            if (modeManager.isActiveMode (Modes.MODE_SESSION) || modeManager.getActiveOrTempMode ().isTemporary ())
                modeManager.restoreMode ();

            return;
        }

        if (modeManager.isActiveOrTempMode (Modes.MODE_VIEW_SELECT))
            modeManager.restoreMode ();
        else
            modeManager.setActiveMode (Modes.MODE_VIEW_SELECT);
    }
}
