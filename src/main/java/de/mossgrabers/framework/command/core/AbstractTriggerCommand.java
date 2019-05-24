// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.core;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Abstract base class for trigger commands.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractTriggerCommand<S extends IControlSurface<C>, C extends Configuration> implements TriggerCommand
{
    protected final IModel model;
    protected final S      surface;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public AbstractTriggerCommand (final IModel model, final S surface)
    {
        this.model = model;
        this.surface = surface;
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event)
    {
        if (this.surface.isShiftPressed ())
            this.executeShifted (event);
        else
            this.executeNormal (event);
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        // Intentionally empty
    }
}
