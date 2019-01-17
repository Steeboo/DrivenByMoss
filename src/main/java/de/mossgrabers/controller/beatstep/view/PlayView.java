// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.beatstep.view;

import de.mossgrabers.controller.beatstep.BeatstepConfiguration;
import de.mossgrabers.controller.beatstep.controller.BeatstepColors;
import de.mossgrabers.controller.beatstep.controller.BeatstepControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.PadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.view.AbstractPlayView;


/**
 * The Play view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PlayView extends AbstractPlayView<BeatstepControlSurface, BeatstepConfiguration> implements BeatstepView
{
    private TrackEditing extensions;


    /**
     * Constructor.
     *
     * @param surface The controller
     * @param model The model
     */
    public PlayView (final BeatstepControlSurface surface, final IModel model)
    {
        super ("Play", surface, model, false);
        this.extensions = new TrackEditing (surface, model);
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
            // Chromatic
            case 12:
                this.scales.setChromatic (!isInc);
                this.surface.getConfiguration ().setScaleInKey (isInc);
                this.surface.getDisplay ().notify (isInc ? "In Key" : "Chromatic");
                break;

            // Base Note
            case 13:
                this.scales.changeScaleOffset (value);
                final String scaleBase = Scales.BASES[this.scales.getScaleOffset ()];
                this.surface.getDisplay ().notify (scaleBase);
                this.surface.getConfiguration ().setScaleBase (scaleBase);
                break;

            // Scale
            case 14:
                if (isInc)
                    this.scales.nextScale ();
                else
                    this.scales.prevScale ();
                final String scale = this.scales.getScale ().getName ();
                this.surface.getConfiguration ().setScale (scale);
                this.surface.getDisplay ().notify (scale);
                break;

            // Octave
            case 15:
                this.keyManager.clearPressedKeys ();
                if (isInc)
                    this.scales.incOctave ();
                else
                    this.scales.decOctave ();
                this.surface.getDisplay ().notify ("Octave " + (this.scales.getOctave () > 0 ? "+" : "") + this.scales.getOctave () + " (" + this.scales.getRangeText () + ")");
                break;
        }

        this.updateNoteMapping ();
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final boolean isKeyboardEnabled = this.model.canSelectedTrackHoldNotes ();
        final PadGrid padGrid = this.surface.getPadGrid ();
        for (int i = 36; i < 52; i++)
            padGrid.light (i, this.getPadColor (isKeyboardEnabled, i));
    }


    protected int getPadColor (final boolean isKeyboardEnabled, final int pad)
    {
        if (!isKeyboardEnabled)
            return BeatstepColors.BEATSTEP_BUTTON_STATE_OFF;
        if (this.keyManager.isKeyPressed (pad))
            return BeatstepColors.BEATSTEP_BUTTON_STATE_PINK;
        final ColorManager colorManager = this.model.getColorManager ();
        return colorManager.getColor (this.keyManager.getColor (pad));
    }
}