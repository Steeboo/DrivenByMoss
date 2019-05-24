// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.mkii.command.trigger;

import de.mossgrabers.controller.kontrol.mkii.KontrolMkIIConfiguration;
import de.mossgrabers.controller.kontrol.mkii.controller.KontrolMkIIControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.command.trigger.clip.StartClipCommand;
import de.mossgrabers.framework.command.trigger.clip.StartSceneCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to start the currently selected clip or scene depending on the configuration setting.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class StartClipOrSceneCommand extends AbstractTriggerCommand<KontrolMkIIControlSurface, KontrolMkIIConfiguration>
{
    private final StartClipCommand<KontrolMkIIControlSurface, KontrolMkIIConfiguration>  clipCommand;
    private final StartSceneCommand<KontrolMkIIControlSurface, KontrolMkIIConfiguration> sceneCommand;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public StartClipOrSceneCommand (final IModel model, final KontrolMkIIControlSurface surface)
    {
        super (model, surface);

        this.clipCommand = new StartClipCommand<> (model, surface);
        this.sceneCommand = new StartSceneCommand<> (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event)
    {
        if (this.surface.getConfiguration ().isFlipClipSceneNavigation ())
            this.sceneCommand.execute (event);
        else
            this.clipCommand.execute (event);
    }
}
