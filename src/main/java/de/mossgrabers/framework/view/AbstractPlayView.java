// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.view;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.controller.grid.PadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.utils.ButtonEvent;

import java.util.Arrays;
import java.util.List;


/**
 * Abstract implementation for a play grid.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractPlayView<S extends IControlSurface<C>, C extends Configuration> extends AbstractView<S, C> implements TransposeView
{
    /** ID of the color to use when a pad is played. */
    public static final String COLOR_PLAY   = "PLAY_VIEW_COLOR_PLAY";
    /** ID of the color to use when a pad is played and recording is enabled. */
    public static final String COLOR_RECORD = "PLAY_VIEW_COLOR_RECORD";
    /** ID of the color to use when a pad does not contain a note. */
    public static final String COLOR_OFF    = "PLAY_VIEW_COLOR_OFF";

    protected final int []     defaultVelocity;
    protected final boolean    useTrackColor;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     * @param useTrackColor True to use the color of the current track for coloring the octaves
     */
    public AbstractPlayView (final S surface, final IModel model, final boolean useTrackColor)
    {
        this ("Play", surface, model, useTrackColor);
    }


    /**
     * Constructor.
     *
     * @param name The name of the view
     * @param surface The surface
     * @param model The model
     * @param useTrackColor True to use the color of the current track for coloring the octaves
     */
    public AbstractPlayView (final String name, final S surface, final IModel model, final boolean useTrackColor)
    {
        super (name, surface, model);

        this.useTrackColor = useTrackColor;

        this.defaultVelocity = new int [128];
        for (int i = 0; i < 128; i++)
            this.defaultVelocity[i] = i;

        final ITrackBank tb = model.getTrackBank ();
        // Light notes sent from the sequencer
        for (int i = 0; i < tb.getPageSize (); i++)
            tb.getItem (i).addNoteObserver (this.keyManager::call);
        tb.addSelectionObserver ( (index, isSelected) -> this.keyManager.clearPressedKeys ());
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final boolean isKeyboardEnabled = this.model.canSelectedTrackHoldNotes ();
        final boolean isRecording = this.model.hasRecordingState ();

        final ITrack selectedTrack = this.model.getSelectedTrack ();
        final PadGrid gridPad = this.surface.getPadGrid ();
        for (int i = this.scales.getStartNote (); i < this.scales.getEndNote (); i++)
            gridPad.light (i, this.getGridColor (isKeyboardEnabled, isRecording, selectedTrack, i));
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int key, final int velocity)
    {
        if (!this.model.canSelectedTrackHoldNotes ())
            return;

        // Mark selected notes immediately for better performance
        final int note = this.keyManager.map (key);
        if (note != -1)
            this.keyManager.setAllKeysPressed (note, velocity);
    }


    /**
     * Get the color for a pad.
     *
     * @param isKeyboardEnabled Can we play?
     * @param isRecording Is recording enabled?
     * @param track The track to use the color for octaves
     * @param note The note of the pad
     * @return The ID of the color
     */
    protected String getGridColor (final boolean isKeyboardEnabled, final boolean isRecording, final ITrack track, final int note)
    {
        if (isKeyboardEnabled)
        {
            if (this.keyManager.isKeyPressed (note))
                return isRecording ? AbstractPlayView.COLOR_RECORD : AbstractPlayView.COLOR_PLAY;
            return this.getColor (note, this.useTrackColor ? track : null);
        }
        return AbstractPlayView.COLOR_OFF;
    }


    /** {@inheritDoc} */
    @Override
    public void onOctaveDown (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        this.keyManager.clearPressedKeys ();
        this.scales.decOctave ();
        this.updateNoteMapping ();
        this.surface.getDisplay ().notify (this.scales.getRangeText ());
    }


    /** {@inheritDoc} */
    @Override
    public void onOctaveUp (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        this.keyManager.clearPressedKeys ();
        this.scales.incOctave ();
        this.updateNoteMapping ();
        this.surface.getDisplay ().notify (this.scales.getRangeText ());
    }


    /**
     * Get the currently pressed keys.
     *
     * @return The list with the keys
     */
    public List<Integer> getPressedKeys ()
    {
        return this.keyManager.getPressedKeys ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateNoteMapping ()
    {
        this.delayedUpdateNoteMapping (this.model.canSelectedTrackHoldNotes () ? this.scales.getNoteMatrix () : EMPTY_TABLE);
    }


    /**
     * Updates the velocity transition table based on the fixed accent value.
     */
    protected void initMaxVelocity ()
    {
        final int [] maxVelocity = new int [128];
        final Configuration config = this.surface.getConfiguration ();
        Arrays.fill (maxVelocity, config.getFixedAccentValue ());
        maxVelocity[0] = 0;
        this.surface.setVelocityTranslationTable (config.isAccentActive () ? maxVelocity : this.defaultVelocity);
    }
}