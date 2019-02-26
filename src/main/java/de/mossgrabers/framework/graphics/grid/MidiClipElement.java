// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.graphics.grid;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.graphics.Align;
import de.mossgrabers.framework.graphics.IGraphicsConfiguration;
import de.mossgrabers.framework.graphics.IGraphicsContext;
import de.mossgrabers.framework.graphics.IGraphicsDimensions;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * An element which displays the notes of a midi clip.
 *
 * Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MidiClipElement extends AbstractGridElement
{
    private static final ColorEx DIVIDERS_COLOR    = ColorEx.fromRGB (50, 50, 50);
    private static final ColorEx NOTE_BACKGROUND   = ColorEx.fromRGB (100, 100, 100);
    private static final ColorEx LOOP_BACKGROUND   = ColorEx.fromRGB (84, 84, 84);
    private static final ColorEx HEADER_BACKGROUND = ColorEx.fromRGB (140, 140, 140);

    private final INoteClip      clip;
    private int                  quartersPerMeasure;


    /**
     * Constructor.
     *
     * @param clip The clip to display
     * @param quartersPerMeasure The quarters of a measure
     */
    public MidiClipElement (final INoteClip clip, final int quartersPerMeasure)
    {
        super (null, false, null, null, null, false);
        this.clip = clip;
        this.quartersPerMeasure = quartersPerMeasure;
    }


    /** {@inheritDoc} */
    @Override
    public void draw (final IGraphicsContext gc, final IGraphicsConfiguration configuration, final IGraphicsDimensions dimensions, final double left, final double width, final double height)
    {
        final int top = 14;
        final double noteAreaHeight = height - top;

        // Draw the background
        gc.fillRectangle (left, top, width, noteAreaHeight, HEADER_BACKGROUND);

        // Draw the loop, if any and ...
        final int numSteps = this.clip.getNumSteps ();
        final double stepLength = this.clip.getStepLength ();
        final double pageLength = numSteps * stepLength;
        final int editPage = this.clip.getEditPage ();
        final double startPos = editPage * pageLength;
        final double endPos = (editPage + 1) * pageLength;
        final int len = top - 1;
        if (this.clip.isLoopEnabled ())
        {
            final double loopStart = this.clip.getLoopStart ();
            final double loopLength = this.clip.getLoopLength ();
            // ... the loop is visible in the current page
            if (loopStart < endPos && loopStart + loopLength > startPos)
            {
                final double start = Math.max (0, loopStart - startPos);
                final double end = Math.min (endPos, loopStart + loopLength) - startPos;
                final double x = width * start / pageLength;
                final double w = width * end / pageLength - x;
                // The header loop
                gc.fillRectangle (x + 1, 0, w, len, LOOP_BACKGROUND);

                // Background in note area
                gc.fillRectangle (x + 1, top, w, noteAreaHeight, NOTE_BACKGROUND);
            }
        }
        // Draw play start in header
        final double [] clipColor = this.clip.getColor ();
        final double playStart = this.clip.getPlayStart ();
        final ColorEx noteColor = new ColorEx (clipColor[0], clipColor[1], clipColor[2]);
        final ColorEx lineColor = noteColor;
        if (playStart >= startPos && playStart <= endPos)
        {
            final double start = playStart - startPos;
            final double x = width * start / pageLength;
            gc.fillTriangle (x + 1, 0, x + 1 + len, len / 2.0, x + 1, len, lineColor);
        }
        // Draw play end in header
        final double playEnd = this.clip.getPlayEnd ();
        if (playEnd >= startPos && playEnd <= endPos)
        {
            final double end = playEnd - startPos;
            final double x = width * end / pageLength;
            gc.fillTriangle (x + 1, 0, x + 1, len, x + 1 - top, len / 2.0, lineColor);
        }

        // Draw dividers
        final double stepWidth = width / numSteps;
        for (int step = 0; step <= numSteps; step++)
        {
            final double x = left + step * stepWidth;
            gc.fillRectangle (x, top, 1, noteAreaHeight, DIVIDERS_COLOR);

            // Draw measure texts
            if (step % 4 == 0)
            {
                final double time = startPos + step * stepLength;
                final String measureText = StringUtils.formatMeasures (this.quartersPerMeasure, time, 1);
                gc.drawTextInHeight (measureText, x, 0, top - 1.0, ColorEx.WHITE, top);
            }
        }

        // Draw the notes
        final int lowerRowWithData = this.clip.getLowerRowWithData ();
        if (lowerRowWithData == -1)
            return;
        final int upperRowWithData = this.clip.getUpperRowWithData ();
        final int range = 1 + upperRowWithData - lowerRowWithData;
        final double stepHeight = noteAreaHeight / range;

        final double fontSize = gc.calculateFontSize (stepHeight, stepWidth);

        for (int row = 0; row < range; row++)
        {
            gc.fillRectangle (left, top + (range - row - 1) * stepHeight, width, 1, DIVIDERS_COLOR);

            for (int step = 0; step < numSteps; step++)
            {
                final int note = lowerRowWithData + row;

                // Get step, check for length
                final int stepState = this.clip.getStep (step, note);
                if (stepState == 0)
                    continue;

                double x = left + step * stepWidth - 1;
                double w = stepWidth + 2;
                final boolean isStart = stepState == 2;
                if (isStart)
                {
                    x += 2;
                    w -= 2;
                }

                gc.strokeRectangle (x, top + (range - row - 1) * stepHeight + 2, w, stepHeight - 2, ColorEx.BLACK);
                gc.fillRectangle (x + (isStart ? 0 : -2), top + (range - row - 1) * stepHeight + 2, w - 1 + (isStart ? 0 : 2), stepHeight - 3, noteColor);

                if (isStart && fontSize > 0)
                {
                    final String text = Scales.formatDrumNote (note);
                    final ColorEx textColor = ColorEx.calcContrastColor (noteColor);
                    gc.drawTextInBounds (text, x, top + (range - row - 1) * stepHeight + 2, w - 1, stepHeight - 3, Align.CENTER, textColor, fontSize);
                }
            }
        }

        // Draw the play cursor
        final int playStep = this.clip.getCurrentStep ();
        if (playStep >= 0)
            gc.fillRectangle (left + playStep * stepWidth - 1, 0, 3, height, ColorEx.WHITE);
    }
}
