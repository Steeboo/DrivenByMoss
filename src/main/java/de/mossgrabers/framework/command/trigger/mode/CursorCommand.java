// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.mode;

import de.mossgrabers.bitwig.framework.daw.TrackBankImpl;
import de.mossgrabers.bitwig.framework.daw.data.SlotImpl;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ISceneBank;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.mode.Mode;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command for cursor arrow keys.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class CursorCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    /** The direction of the cursor. */
    public enum Direction
    {
        /** Move left. */
        LEFT,
        /** Move right. */
        RIGHT,
        /** Move up. */
        UP,
        /** Move down. */
        DOWN
    }

    protected Direction direction;
    protected boolean   canScrollLeft;
    protected boolean   canScrollRight;
    protected boolean   canScrollUp;
    protected boolean   canScrollDown;


    /**
     * Constructor.
     *
     * @param direction The direction of the pushed cursor arrow
     * @param model The model
     * @param surface The surface
     */
    public CursorCommand (final Direction direction, final IModel model, final S surface)
    {
        super (model, surface);
        this.direction = direction;
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        switch (this.direction)
        {
            case LEFT:
                this.scrollLeft ();
                break;
            case RIGHT:
                this.scrollRight ();
                break;
            case UP:
                this.scrollUp ();
                break;
            case DOWN:
                this.scrollDown ();
                break;
        }
    }


    /**
     * Update the arrow buttons.
     */
    public void updateArrows ()
    {
        this.updateArrowStates ();
        this.surface.scheduleTask (this::delayedUpdateArrows, 150);
    }


    protected void delayedUpdateArrows ()
    {
        final int buttonOnColor = this.getButtonOnColor ();
        final int buttonOffColor = this.getButtonOffColor ();
        this.surface.updateButton (this.surface.getLeftButtonId (), this.canScrollLeft ? buttonOnColor : buttonOffColor);
        this.surface.updateButton (this.surface.getRightButtonId (), this.canScrollRight ? buttonOnColor : buttonOffColor);
        this.surface.updateButton (this.surface.getUpButtonId (), this.canScrollUp ? buttonOnColor : buttonOffColor);
        this.surface.updateButton (this.surface.getDownButtonId (), this.canScrollDown ? buttonOnColor : buttonOffColor);
    }


    /**
     * Get the color of when the button should be off.
     *
     * @return The color ID
     */
    protected int getButtonOffColor ()
    {
        return this.model.getColorManager ().getColor (ColorManager.BUTTON_STATE_OFF);
    }


    /**
     * Get the color of when the button should be on.
     *
     * @return The color ID
     */
    protected int getButtonOnColor ()
    {
        return this.model.getColorManager ().getColor (ColorManager.BUTTON_STATE_ON);
    }


    /**
     * Update the states of the arrow buttons. Override to update arrow states.
     */
    protected void updateArrowStates ()
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final ISceneBank sceneBank = tb.getSceneBank ();
        this.canScrollUp = true;
        this.canScrollDown = true;

        final Mode mode = this.surface.getModeManager ().getActiveOrTempMode ();
        final boolean shiftPressed = this.surface.isShiftPressed ();
        this.canScrollLeft = true;
        this.canScrollRight = true;
    }

    /**
     * Scroll left. Tracks, devices or parameter banks.
     */
    protected void scrollLeft ()    
    {           
        final ISlot selectedSlot = this.model.getSelectedSlot();
        if (selectedSlot != null){           
            final int selectedSlotPos = this.model.getSelectedSlot().getPosition();
            this.model.getTrackBank().selectPreviousItem();              
            this.surface.scheduleTask ( () -> this.model.getSelectedTrack().getSlotBank().getItem(selectedSlotPos).select(), 45);
        } else {
            this.model.getTrackBank().selectPreviousItem();
        }
    }


    /**
     * Scroll right. Tracks, devices or parameter banks.
     */
    protected void scrollRight ()
    {
        final ISlot selectedSlot = this.model.getSelectedSlot();
        if (selectedSlot != null){           
             final int selectedSlotPos = this.model.getSelectedSlot().getPosition();
             this.model.getTrackBank().selectNextItem();           
             this.surface.scheduleTask ( () -> this.model.getSelectedTrack().getSlotBank().getItem(selectedSlotPos).select(), 45);
        } else {
            this.model.getTrackBank().selectNextItem();
        }
    }


    /**
     * Scroll scenes up.
     */
    protected void scrollUp ()
    {
        this.model.getSelectedTrack().getSlotBank ().selectPreviousItem(); 
    }


    /**
     * Scroll scenes down.
     */
    protected void scrollDown ()
    {            
        this.model.getSelectedTrack().getSlotBank ().selectNextItem();        
    }
}
