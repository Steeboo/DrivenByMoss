// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.graphics.grid;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.graphics.Align;
import de.mossgrabers.framework.graphics.IGraphicsConfiguration;
import de.mossgrabers.framework.graphics.IGraphicsContext;
import de.mossgrabers.framework.graphics.IGraphicsDimensions;
import de.mossgrabers.framework.utils.Pair;

import java.util.ArrayList;
import java.util.List;


/**
 * An element in the grid which contains several text items, which represent a clip. Each item can
 * be selected.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ClipListGridElement extends AbstractGridElement
{
    private final List<Pair<ITrack, ISlot>> items;


    /**
     * Constructor.
     *
     * @param slots The list items
     */
    public ClipListGridElement (final List<Pair<ITrack, ISlot>> slots)
    {
        super (null, false, null, null, null, false);
        this.items = new ArrayList<> (slots);
    }


    /** {@inheritDoc} */
    @Override
    public void draw (final IGraphicsContext gc, final IGraphicsConfiguration configuration, final IGraphicsDimensions dimensions, final double left, final double width, final double height)
    {
        final double separatorSize = dimensions.getSeparatorSize ();
        final double inset = dimensions.getInset ();

        final int size = this.items.size ();
        final double itemLeft = left + separatorSize;
        final double itemWidth = width - separatorSize;
        final double itemHeight = height / size;
        final double fontHeight = itemHeight > 30 ? itemHeight / 2 : itemHeight * 2 / 3;
        final double boxLeft = itemLeft + inset;
        final double boxWidth = fontHeight - 2 * separatorSize;
        final double radius = boxWidth / 2;

        final ColorEx textColor = configuration.getColorText ();
        final ColorEx borderColor = configuration.getColorBackgroundLighter ();

        for (int i = 0; i < size; i++)
        {
            final Pair<ITrack, ISlot> pair = this.items.get (i);
            final ISlot slot = pair.getValue ();
            final ITrack track = pair.getKey ();

            final double itemTop = i * itemHeight;

            String name = slot.getName ();

            final double boxTop = itemTop + (itemHeight - fontHeight) / 2;

            // Draw the background
            final ColorEx clipBackgroundColor = new ColorEx (slot.getColor ());
            if (track.isGroup ())
            {
                if (name.isEmpty ())
                    name = "Scene " + (slot.getPosition () + 1);
                gc.fillRectangle (itemLeft, itemTop + separatorSize, itemWidth, itemHeight - 2 * separatorSize, ColorEx.darker (ColorEx.DARK_GRAY));
                gc.fillRectangle (itemLeft + itemWidth - 2 * inset, itemTop + separatorSize, 2 * inset, itemHeight - 2 * separatorSize, clipBackgroundColor);
            }
            else
                gc.fillRectangle (itemLeft, itemTop + separatorSize, itemWidth, itemHeight - 2 * separatorSize, clipBackgroundColor);

            if (slot.doesExist ())
            {
                // Draw the play/record state indicator box
                final boolean isPlaying = slot.isPlaying ();
                if (isPlaying || slot.isRecording () || slot.isPlayingQueued () || slot.isRecordingQueued ())
                    gc.fillRectangle (boxLeft, boxTop, fontHeight, fontHeight, ColorEx.BLACK);

                // Draw the play, record or stop symbol depending on the slots state
                if (slot.hasContent ())
                {
                    if (slot.isRecording ())
                        gc.fillCircle (boxLeft + separatorSize + radius, boxTop + separatorSize + radius, radius, ColorEx.RED);
                    else
                    {
                        ColorEx fillColor = ColorEx.darker (clipBackgroundColor);
                        if (isPlaying)
                            fillColor = ColorEx.GREEN;
                        else if (slot.isPlayingQueued () || slot.isRecordingQueued ())
                            fillColor = ColorEx.WHITE;
                        gc.fillTriangle (boxLeft + separatorSize, boxTop + separatorSize, boxLeft + separatorSize, boxTop + fontHeight - separatorSize, boxLeft + fontHeight - separatorSize, boxTop + fontHeight / 2, fillColor);
                    }
                }
                else
                {
                    if (track.isRecArm ())
                        gc.fillCircle (boxLeft + separatorSize + radius, boxTop + separatorSize + radius, radius, ColorEx.DARK_GRAY);
                    else
                        gc.fillRectangle (boxLeft + separatorSize, boxTop + separatorSize, boxWidth, boxWidth, ColorEx.DARK_GRAY);
                }

                // Draw the text
                gc.drawTextInBounds (name, itemLeft + 2 * inset + fontHeight, itemTop - 1, itemWidth - 2 * inset, itemHeight, Align.LEFT, ColorEx.BLACK, fontHeight);
            }

            // Draw the border
            ColorEx color = borderColor;
            if (slot.isSelected ())
                color = textColor;
            else if (track.isSelected ())
                color = ColorEx.darker (ColorEx.YELLOW);
            gc.strokeRectangle (itemLeft, itemTop + separatorSize, itemWidth, itemHeight - 2 * separatorSize, color, slot.isSelected () ? 2 : 1);
        }
    }
}
