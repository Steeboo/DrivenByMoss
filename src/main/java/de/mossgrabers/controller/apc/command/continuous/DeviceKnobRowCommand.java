// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.apc.command.continuous;

import de.mossgrabers.controller.apc.APCConfiguration;
import de.mossgrabers.controller.apc.controller.APCControlSurface;
import de.mossgrabers.framework.command.core.AbstractContinuousCommand;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IModel;

import java.util.Date;


/**
 * Command to change a device parameter.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DeviceKnobRowCommand extends AbstractContinuousCommand<APCControlSurface, APCConfiguration>
{
    private int     index;
    private long    moveStartTime;
    private boolean isKnobMoving;


    /**
     * Constructor.
     *
     * @param index The index of the button
     * @param model The model
     * @param surface The surface
     */
    public DeviceKnobRowCommand (final int index, final IModel model, final APCControlSurface surface)
    {
        super (model, surface);
        this.index = index;
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final int value)
    {
        final ICursorDevice cd = this.model.getCursorDevice ();
        if (!cd.doesExist ())
            return;
        cd.getParameterBank ().getItem (this.index).setValue (value);

        this.moveStartTime = new Date ().getTime ();
        if (this.isKnobMoving)
            return;

        this.isKnobMoving = true;
        this.startCheckKnobMovement ();
    }


    /**
     * Returns true if knob is moving.
     *
     * @return True if knob is moving
     */
    public boolean isKnobMoving ()
    {
        return this.isKnobMoving;
    }


    private void checkKnobMovement ()
    {
        if (!this.isKnobMoving)
            return;
        if (new Date ().getTime () - this.moveStartTime > 200)
            this.isKnobMoving = false;
        else
            this.startCheckKnobMovement ();
    }


    private void startCheckKnobMovement ()
    {
        this.surface.scheduleTask (this::checkKnobMovement, 100);
    }
}
