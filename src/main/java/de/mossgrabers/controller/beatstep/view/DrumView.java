// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.beatstep.view;

import de.mossgrabers.controller.beatstep.controller.BeatstepColors;
import de.mossgrabers.controller.beatstep.controller.BeatstepControlSurface;
import de.mossgrabers.framework.controller.grid.PadGrid;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.scale.Scales;


/**
 * The Drum view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DrumView extends BaseSequencerView
{
    private static final int NUM_DISPLAY_COLS = 16;


    /**
     * Constructor.
     *
     * @param surface The controller
     * @param model The model
     */
    public DrumView (final BeatstepControlSurface surface, final IModel model)
    {
        super ("Drum", surface, model, 128, DrumView.NUM_DISPLAY_COLS);

        this.offsetY = Scales.DRUM_NOTE_START;

        final ITrackBank tb = model.getTrackBank ();
        // Light notes send from the sequencer
        for (int i = 0; i < tb.getPageSize (); i++)
            tb.getItem (i).addNoteObserver (this::updateNote);
        tb.addSelectionObserver ( (index, isSelected) -> this.keyManager.clearPressedKeys ());
    }


    /** {@inheritDoc} */
    @Override
    public void onKnob (final int index, final int value)
    {
        if (index < 12)
        {
            this.extensions.onTrackKnob (index, value);
            return;
        }

        final boolean isInc = value >= 65;

        switch (index)
        {
            case 12:
                this.changeScrollPosition (value);
                break;

            case 13:
                this.changeResolution (value);
                this.surface.getDisplay ().notify (RESOLUTION_TEXTS[this.selectedIndex]);
                break;

            // Up/Down
            case 14:
                this.keyManager.clearPressedKeys ();
                if (isInc)
                {
                    this.scales.incDrumOctave ();
                    this.model.getInstrumentDevice ().getDrumPadBank ().scrollPageForwards ();
                }
                else
                {
                    this.scales.decDrumOctave ();
                    this.model.getInstrumentDevice ().getDrumPadBank ().scrollPageBackwards ();
                }
                this.offsetY = Scales.DRUM_NOTE_START + this.scales.getDrumOctave () * 16;
                this.updateNoteMapping ();
                this.surface.getDisplay ().notify (this.scales.getDrumRangeText ());
                break;

            // Toggle play / sequencer
            case 15:
                this.isPlayMode = !this.isPlayMode;
                this.surface.getDisplay ().notify (this.isPlayMode ? "Play/Select" : "Sequence");
                this.updateNoteMapping ();
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (!this.model.canSelectedTrackHoldNotes ())
            return;

        final int index = note - 36;

        if (this.isPlayMode)
        {
            this.selectedPad = index; // 0-16

            // Mark selected note
            this.keyManager.setKeyPressed (this.offsetY + this.selectedPad, velocity);
        }
        else
        {
            if (velocity != 0)
                this.getClip ().toggleStep (index < 8 ? index + 8 : index - 8, this.offsetY + this.selectedPad, this.configuration.isAccentActive () ? this.configuration.getFixedAccentValue () : velocity);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateNoteMapping ()
    {
        this.delayedUpdateNoteMapping (this.model.canSelectedTrackHoldNotes () && this.isPlayMode ? this.scales.getDrumMatrix () : EMPTY_TABLE);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final PadGrid padGrid = this.surface.getPadGrid ();
        if (!this.model.canSelectedTrackHoldNotes ())
        {
            padGrid.turnOff ();
            return;
        }

        if (this.isPlayMode)
        {
            final ICursorDevice primary = this.model.getInstrumentDevice ();
            final boolean hasDrumPads = primary.hasDrumPads ();
            boolean isSoloed = false;
            if (hasDrumPads)
            {
                for (int i = 0; i < 16; i++)
                {
                    if (primary.getDrumPadBank ().getItem (i).isSolo ())
                    {
                        isSoloed = true;
                        break;
                    }
                }
            }
            for (int y = 0; y < 2; y++)
            {
                for (int x = 0; x < 8; x++)
                {
                    final int index = 8 * y + x;
                    padGrid.lightEx (x, y, this.getPadColor (index, primary, isSoloed));
                }
            }
        }
        else
        {
            final INoteClip clip = this.getClip ();
            // Paint the sequencer steps
            final int step = clip.getCurrentStep ();
            final int hiStep = this.isInXRange (step) ? step % DrumView.NUM_DISPLAY_COLS : -1;
            for (int col = 0; col < DrumView.NUM_DISPLAY_COLS; col++)
            {
                final int isSet = clip.getStep (col, this.offsetY + this.selectedPad);
                final boolean hilite = col == hiStep;
                final int x = col % 8;
                final int y = col / 8;
                padGrid.lightEx (x, 1 - y, isSet > 0 ? hilite ? BeatstepColors.BEATSTEP_BUTTON_STATE_PINK : BeatstepColors.BEATSTEP_BUTTON_STATE_BLUE : hilite ? BeatstepColors.BEATSTEP_BUTTON_STATE_PINK : BeatstepColors.BEATSTEP_BUTTON_STATE_OFF);
            }
        }
    }


    private int getPadColor (final int index, final ICursorDevice primary, final boolean isSoloed)
    {
        // Playing note?
        if (this.keyManager.isKeyPressed (this.offsetY + index))
            return BeatstepColors.BEATSTEP_BUTTON_STATE_PINK;
        // Selected?
        if (this.selectedPad == index)
            return BeatstepColors.BEATSTEP_BUTTON_STATE_RED;
        // Exists and active?
        final IChannel drumPad = primary.getDrumPadBank ().getItem (index);
        if (!drumPad.doesExist () || !drumPad.isActivated ())
            return BeatstepColors.BEATSTEP_BUTTON_STATE_OFF;
        // Muted or soloed?
        if (drumPad.isMute () || isSoloed && !drumPad.isSolo ())
            return BeatstepColors.BEATSTEP_BUTTON_STATE_OFF;
        return BeatstepColors.BEATSTEP_BUTTON_STATE_BLUE;
    }


    /**
     * The callback function for playing note changes.
     *
     * @param trackIndex The index of the track on which the note is playing
     * @param note The played note
     * @param velocity The played velocity
     */
    private void updateNote (final int trackIndex, final int note, final int velocity)
    {
        final ITrack sel = this.model.getCurrentTrackBank ().getSelectedItem ();
        if (sel != null && sel.getIndex () == trackIndex)
            this.keyManager.setKeyPressed (note, velocity);
    }
}