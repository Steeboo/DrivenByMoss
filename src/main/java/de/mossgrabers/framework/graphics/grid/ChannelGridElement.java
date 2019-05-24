// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.graphics.grid;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.framework.daw.resource.ResourceHandler;
import de.mossgrabers.framework.graphics.Align;
import de.mossgrabers.framework.graphics.IGraphicsConfiguration;
import de.mossgrabers.framework.graphics.IGraphicsContext;
import de.mossgrabers.framework.graphics.IGraphicsDimensions;
import de.mossgrabers.framework.graphics.IImage;


/**
 * An element in the grid which contains the channel settings: Volume, VU, Pan, Mute, Solo and Arm.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ChannelGridElement extends SelectionGridElement
{
    /** Edit volume. */
    public static final int EDIT_TYPE_VOLUME     = 0;
    /** Edit panorma. */
    public static final int EDIT_TYPE_PAN        = 1;
    /** Edit crossfader setting. */
    public static final int EDIT_TYPE_CROSSFADER = 2;
    /** Edit all settings. */
    public static final int EDIT_TYPE_ALL        = 3;

    private final double    editType;
    private final double    volumeValue;
    private final double    modulatedVolumeValue;
    private final String    volumeText;
    private final double    panValue;
    private final double    modulatedPanValue;
    private final String    panText;
    private final double    vuValueLeft;
    private final double    vuValueRight;
    private final boolean   isMute;
    private final boolean   isSolo;
    private final boolean   isArm;
    private final double    crossfadeMode;


    /**
     * Constructor.
     *
     * @param editType What to edit, 0 = Volume, 1 = Pan, 2 = Crossfade Mode
     * @param menuName The text for the menu
     * @param isMenuSelected True if the menu is selected
     * @param name The of the grid element (track name, parameter name, etc.)
     * @param color The color to use for the header, may be null
     * @param isSelected True if the grid element is selected
     * @param type The type of the track
     * @param volumeValue The value of the volume
     * @param modulatedVolumeValue The modulated value of the volume, -1 if not modulated
     * @param volumeText The textual form of the volumes value
     * @param panValue The value of the panorama
     * @param modulatedPanValue The modulated value of the panorama, -1 if not modulated
     * @param panText The textual form of the panorama
     * @param vuValueLeft The value of the VU of the left channel
     * @param vuValueRight The value of the VU of the right channel
     * @param isMute True if muted
     * @param isSolo True if soloed
     * @param isArm True if recording is armed
     * @param crossfadeMode The crossfader mode: 0 = A, 1 = AB, B = 2, -1 turns it off
     */
    public ChannelGridElement (final double editType, final String menuName, final boolean isMenuSelected, final String name, final ColorEx color, final boolean isSelected, final ChannelType type, final double volumeValue, final double modulatedVolumeValue, final String volumeText, final double panValue, final double modulatedPanValue, final String panText, final double vuValueLeft, final double vuValueRight, final boolean isMute, final boolean isSolo, final boolean isArm, final double crossfadeMode)
    {
        super (menuName, isMenuSelected, name, color, isSelected, type);

        this.editType = editType;
        this.volumeValue = volumeValue;
        this.modulatedVolumeValue = modulatedVolumeValue;
        this.volumeText = volumeText;
        this.panValue = panValue;
        this.modulatedPanValue = modulatedPanValue;
        this.panText = panText;
        this.vuValueLeft = vuValueLeft;
        this.vuValueRight = vuValueRight;
        this.isMute = isMute;
        this.isSolo = isSolo;
        this.isArm = isArm;
        this.crossfadeMode = crossfadeMode;
    }


    /** {@inheritDoc} */
    @Override
    public void draw (final IGraphicsContext gc, final IGraphicsConfiguration configuration, final IGraphicsDimensions dimensions, final double left, final double width, final double height)
    {
        final double halfWidth = width / 2;

        final double separatorSize = dimensions.getSeparatorSize ();
        final double menuHeight = dimensions.getMenuHeight ();
        final double unit = dimensions.getUnit ();
        final double halfUnit = dimensions.getHalfUnit ();
        final double controlsTop = dimensions.getControlsTop ();
        final double inset = dimensions.getInset ();

        final int trackRowHeight = (int) (1.6 * unit);
        final double trackRowTop = height - trackRowHeight - unit - separatorSize;

        final double controlWidth = halfWidth - halfUnit - halfUnit / 2;
        final double controlStart = left + halfWidth + halfUnit - halfUnit / 2;

        final double panWidth = controlWidth - 2;
        final double panStart = controlStart + 1;
        final double panTop = controlsTop + 1.0;
        final double panHeight = unit - separatorSize;
        final double panTextTop = panTop + panHeight;

        final double faderOffset = controlWidth / 4;
        final double faderTop = panTop + panHeight + separatorSize + 1;
        final double vuX = controlStart + separatorSize;
        final double faderLeft = vuX + faderOffset;
        final double faderHeight = trackRowTop - faderTop - inset + 1;
        final double faderInnerHeight = faderHeight - 2 * separatorSize;

        final double volumeTextWidth = 1.4 * controlWidth;
        final double volumeTextLeft = faderLeft - volumeTextWidth - 2;

        final double buttonHeight = (faderHeight - 4 * separatorSize) / 3;

        //
        // Drawing
        //

        final ColorEx textColor = configuration.getColorText ();
        this.drawMenu (gc, configuration, dimensions, left, width);

        final String name = this.getName ();
        // Element is off if the name is empty
        if (name == null || name.length () == 0)
            return;

        final ColorEx backgroundColor = configuration.getColorBackground ();
        this.drawTrackInfo (gc, configuration, dimensions, left, width, height, trackRowTop, name);

        // Draw the background
        gc.fillRectangle (left, menuHeight + 1, width, trackRowTop - (menuHeight + 1), this.isSelected () ? configuration.getColorBackgroundLighter () : backgroundColor);

        // Background of pan and slider area
        final ColorEx borderColor = configuration.getColorBorder ();
        gc.fillRectangle (controlStart, controlsTop, halfWidth - unit + halfUnit / 2, unit, borderColor);
        gc.fillRectangle (controlStart, faderTop, controlWidth, faderHeight, borderColor);

        final ColorEx backgroundDarker = configuration.getColorBackgroundDarker ();
        final ColorEx editColor = configuration.getColorEdit ();

        final ChannelType type = this.getType ();
        if (type != ChannelType.MASTER && type != ChannelType.LAYER && this.crossfadeMode != -1)
        {
            // Crossfader A|B
            final double crossWidth = controlWidth / 3;
            final ColorEx selColor = this.editType == EDIT_TYPE_CROSSFADER || this.editType == EDIT_TYPE_ALL ? editColor : textColor;

            final IImage crossfaderAIcon = ResourceHandler.getSVGImage ("track/crossfade_a.svg");
            gc.maskImage (crossfaderAIcon, left + inset + (crossWidth - crossfaderAIcon.getWidth ()) / 2, controlsTop + (panHeight - crossfaderAIcon.getHeight ()) / 2, this.crossfadeMode == 0 ? selColor : backgroundDarker);
            final IImage crossfaderABIcon = ResourceHandler.getSVGImage ("track/crossfade_ab.svg");
            gc.maskImage (crossfaderABIcon, crossWidth + left + inset + (crossWidth - crossfaderAIcon.getWidth ()) / 2, controlsTop + (panHeight - crossfaderAIcon.getHeight ()) / 2, this.crossfadeMode == 1 ? selColor : backgroundDarker);
            final IImage crossfaderBIcon = ResourceHandler.getSVGImage ("track/crossfade_b.svg");
            gc.maskImage (crossfaderBIcon, 2 * crossWidth + left + inset + (crossWidth - crossfaderAIcon.getWidth ()) / 2, controlsTop + (panHeight - crossfaderAIcon.getHeight ()) / 2, this.crossfadeMode == 2 ? selColor : backgroundDarker);
        }

        // Panorama
        gc.fillRectangle (panStart, panTop, panWidth, panHeight, backgroundDarker);

        final double panRange = panWidth / 2;
        final double panMiddle = panStart + panRange;

        gc.drawLine (panMiddle, panTop, panMiddle, panTop + panHeight, borderColor);

        final double maxValue = getMaxValue ();
        final double halfMax = maxValue / 2;
        final boolean isPanTouched = this.panText.length () > 0;

        // Panned to the left or right?
        final boolean isRight = this.panValue > halfMax;
        final boolean isModulatedRight = this.modulatedPanValue > halfMax;
        final double v = isRight ? (this.panValue - halfMax) * panRange / halfMax : panRange - this.panValue * panRange / halfMax;
        final boolean isPanModulated = this.modulatedPanValue != -1;
        final double vMod = isPanModulated ? isModulatedRight ? (this.modulatedPanValue - halfMax) * panRange / halfMax : panRange - this.modulatedPanValue * panRange / halfMax : v;

        final ColorEx faderColor = configuration.getColorFader ();
        gc.fillRectangle ((isPanModulated ? isModulatedRight : isRight) ? panMiddle + 1 : panMiddle - vMod, controlsTop + 1, vMod, panHeight, faderColor);

        if (this.editType == EDIT_TYPE_PAN || this.editType == EDIT_TYPE_ALL)
        {
            final double w = isPanTouched ? 3 : 1;
            final double start = isRight ? Math.min (panMiddle + panRange - w, panMiddle + v) : Math.max (panMiddle - panRange, panMiddle - v);
            gc.fillRectangle (start, controlsTop + 1, w, panHeight, editColor);
        }

        // Volume slider
        // Ensure that maximum value is reached even if rounding errors happen
        final double volumeWidth = controlWidth - 2 * separatorSize - faderOffset;
        final double volumeHeight = this.volumeValue >= maxValue - 1 ? faderInnerHeight : faderInnerHeight * this.volumeValue / maxValue;
        final boolean isVolumeModulated = this.modulatedVolumeValue != -1;
        final double modulatedVolumeHeight = isVolumeModulated ? (double) (this.modulatedVolumeValue >= maxValue - 1 ? faderInnerHeight : faderInnerHeight * this.modulatedVolumeValue / maxValue) : volumeHeight;
        final double volumeTop = faderTop + separatorSize + faderInnerHeight - volumeHeight;
        final double modulatedVolumeTop = isVolumeModulated ? faderTop + separatorSize + faderInnerHeight - modulatedVolumeHeight : volumeTop;

        gc.fillRectangle (faderLeft, modulatedVolumeTop, volumeWidth, modulatedVolumeHeight, faderColor);

        final boolean isVolumeTouched = this.volumeText.length () > 0;
        if (this.editType == EDIT_TYPE_VOLUME || this.editType == EDIT_TYPE_ALL)
        {
            final double h = isVolumeTouched ? 3 : 1;
            gc.fillRectangle (faderLeft, Math.min (volumeTop + volumeHeight - h, volumeTop), volumeWidth, h, editColor);
        }

        // VU
        final double vuHeightLeft = this.vuValueLeft >= maxValue - 1 ? faderInnerHeight : faderInnerHeight * this.vuValueLeft / maxValue;
        final double vuHeightRight = this.vuValueRight >= maxValue - 1 ? faderInnerHeight : faderInnerHeight * this.vuValueRight / maxValue;
        final double vuOffsetLeft = faderInnerHeight - vuHeightLeft;
        final double vuOffsetRight = faderInnerHeight - vuHeightRight;
        final double vuWidth = faderOffset - separatorSize;
        gc.fillRectangle (vuX, faderTop + separatorSize, vuWidth + 1, faderInnerHeight, backgroundDarker);
        final ColorEx colorVu = configuration.getColorVu ();
        gc.fillRectangle (vuX, faderTop + separatorSize + vuOffsetLeft, vuWidth / 2, vuHeightLeft, colorVu);
        gc.fillRectangle (vuX + vuWidth / 2, faderTop + separatorSize + vuOffsetRight, vuWidth / 2, vuHeightRight, colorVu);

        double buttonTop = faderTop;

        if (type != ChannelType.LAYER)
        {
            // Rec Arm
            drawButton (gc, left + inset - 1, buttonTop, controlWidth - 1, buttonHeight - 1, backgroundColor, configuration.getColorRecord (), textColor, this.isArm, "channel/record_arm.svg", configuration);
        }

        // Solo
        buttonTop += buttonHeight + 2 * separatorSize;
        drawButton (gc, left + inset - 1, buttonTop, controlWidth - 1, buttonHeight - 1, backgroundColor, configuration.getColorSolo (), textColor, this.isSolo, "channel/solo.svg", configuration);

        // Mute
        buttonTop += buttonHeight + 2 * separatorSize;
        drawButton (gc, left + inset - 1, buttonTop, controlWidth - 1, buttonHeight - 1, backgroundColor, configuration.getColorMute (), textColor, this.isMute, "channel/mute.svg", configuration);

        // Draw panorama text on top if set
        if (isPanTouched)
        {
            gc.fillRectangle (controlStart, panTextTop, controlWidth, unit, backgroundDarker);
            gc.strokeRectangle (controlStart, panTextTop, controlWidth - 1, unit, borderColor);
            gc.drawTextInBounds (this.panText, controlStart, panTextTop, controlWidth, unit, Align.CENTER, textColor, unit);
        }

        // Draw volume text on top if set
        if (isVolumeTouched)
        {
            final double volumeTextTop = this.volumeValue >= maxValue - 1 ? faderTop : Math.min (volumeTop - 1, faderTop + faderInnerHeight + separatorSize - unit + 1);
            gc.fillRectangle (volumeTextLeft, volumeTextTop, volumeTextWidth, unit, backgroundDarker);
            gc.strokeRectangle (volumeTextLeft, volumeTextTop, volumeTextWidth - 1, unit, borderColor);
            gc.drawTextInBounds (this.volumeText, volumeTextLeft, volumeTextTop, volumeTextWidth, unit, Align.CENTER, textColor, unit);
        }
    }


    /**
     * Draws a button a gradient background.
     *
     * @param gc The graphics context
     * @param left The left bound of the drawing area
     * @param top The top bound of the drawing area
     * @param width The width of the drawing area
     * @param height The height of the drawing area
     * @param backgroundColor The background color
     * @param isOnColor The color if the button is on
     * @param textColor The color of the buttons text
     * @param isOn True if the button is on
     * @param iconName The name of the buttons icon
     * @param configuration The layout settings
     */
    private static void drawButton (final IGraphicsContext gc, final double left, final double top, final double width, final double height, final ColorEx backgroundColor, final ColorEx isOnColor, final ColorEx textColor, final boolean isOn, final String iconName, final IGraphicsConfiguration configuration)
    {
        final ColorEx borderColor = configuration.getColorBorder ();
        final double radius = 2.0;

        gc.fillRoundedRectangle (left, top, width, height, radius, borderColor);

        if (isOn)
            gc.fillRoundedRectangle (left + 1, top + 1, width - 2, height - 2, radius, isOnColor);
        else
            gc.fillGradientRoundedRectangle (left + 1, top + 1, width - 2, height - 2, radius, backgroundColor, ColorEx.brighter (backgroundColor));

        final IImage icon = ResourceHandler.getSVGImage (iconName);
        gc.maskImage (icon, left + (width - icon.getWidth ()) / 2, top + (height - icon.getHeight ()) / 2, isOn ? borderColor : textColor);
    }
}
