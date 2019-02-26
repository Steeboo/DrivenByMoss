// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.beatstep.view;

import de.mossgrabers.controller.beatstep.BeatstepConfiguration;
import de.mossgrabers.controller.beatstep.controller.BeatstepColors;
import de.mossgrabers.controller.beatstep.controller.BeatstepControlSurface;
import de.mossgrabers.framework.controller.grid.PadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.view.AbstractView;


/**
 * The track view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TrackView extends AbstractView<BeatstepControlSurface, BeatstepConfiguration> implements BeatstepView
{
    private TrackEditing extensions;


    /**
     * Constructor.
     *
     * @param surface The controller
     * @param model The model
     */
    public TrackView (final BeatstepControlSurface surface, final IModel model)
    {
        super ("Track", surface, model);
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

        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final ITrack selectedTrack = tb.getSelectedItem ();
        if (selectedTrack == null)
            return;

        switch (index)
        {
            // Send 5 - 6
            case 12:
            case 13:
                if (!this.model.isEffectTrackBankActive ())
                    selectedTrack.getSendBank ().getItem (index - 8).changeValue (value);
                break;

            case 14:
                // Not used
                break;

            // Crossfader
            case 15:
                this.model.getTransport ().changeCrossfade (value);
                break;

            default:
                // Not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (velocity == 0)
            return;

        final ITrackBank tb = this.model.getCurrentTrackBank ();

        int track;
        final ITrack selectedTrack = tb.getSelectedItem ();
        int index;
        switch (note - 36)
        {
            // Toggle Activate
            case 0:

                if (selectedTrack != null)
                    selectedTrack.toggleIsActivated ();
                break;

            // Track left
            case 1:
                index = selectedTrack == null ? 0 : selectedTrack.getIndex () - 1;
                if (index == -1 || this.surface.isShiftPressed ())
                    tb.selectPreviousPage ();
                else
                    this.selectTrack (index);
                break;

            // Track right
            case 2:
                index = selectedTrack == null ? 0 : selectedTrack.getIndex () + 1;
                if (index == 8 || this.surface.isShiftPressed ())
                    tb.selectNextPage ();
                else
                    this.selectTrack (index);
                break;

            // Move down
            case 3:
                if (selectedTrack != null)
                    selectedTrack.enter ();
                break;

            // Move up
            case 4:
                tb.selectParent ();
                break;

            // Unused
            case 5:
                break;

            // Track Page down
            case 6:
                tb.selectPreviousPage ();
                break;

            // Track Page up
            case 7:
                tb.selectNextPage ();
                break;

            default:
                track = note - 36 - 8;
                this.selectTrack (track);
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final PadGrid padGrid = this.surface.getPadGrid ();
        for (int i = 0; i < 8; i++)
            padGrid.light (44 + i, tb.getItem (i).isSelected () ? BeatstepColors.BEATSTEP_BUTTON_STATE_BLUE : BeatstepColors.BEATSTEP_BUTTON_STATE_OFF);

        final ITrack sel = tb.getSelectedItem ();
        padGrid.light (36, sel != null && sel.isActivated () ? BeatstepColors.BEATSTEP_BUTTON_STATE_RED : BeatstepColors.BEATSTEP_BUTTON_STATE_OFF);
        padGrid.light (37, BeatstepColors.BEATSTEP_BUTTON_STATE_BLUE);
        padGrid.light (38, BeatstepColors.BEATSTEP_BUTTON_STATE_BLUE);
        padGrid.light (39, BeatstepColors.BEATSTEP_BUTTON_STATE_RED);
        padGrid.light (40, BeatstepColors.BEATSTEP_BUTTON_STATE_RED);
        padGrid.light (41, BeatstepColors.BEATSTEP_BUTTON_STATE_OFF);
        padGrid.light (42, BeatstepColors.BEATSTEP_BUTTON_STATE_BLUE);
        padGrid.light (43, BeatstepColors.BEATSTEP_BUTTON_STATE_BLUE);
    }
}