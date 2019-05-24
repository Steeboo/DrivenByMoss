// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mcu.mode;

import de.mossgrabers.controller.mcu.controller.MCUControlSurface;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.daw.IMarkerBank;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IMarker;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Mode for markers.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MarkerMode extends BaseMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public MarkerMode (final MCUControlSurface surface, final IModel model)
    {
        super ("Marker", surface, model);

        this.isTemporary = true;
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final Display d = this.surface.getDisplay ().clear ();

        final IMarkerBank markerBank = this.model.getMarkerBank ();
        final int extenderOffset = this.surface.getExtenderOffset ();
        for (int i = 0; i < 8; i++)
        {
            final IMarker marker = markerBank.getItem (extenderOffset + i);
            if (!marker.doesExist ())
                continue;
            final String name = StringUtils.shortenAndFixASCII (marker.getName (), 6);
            d.setCell (0, i, name);
        }
        d.allDone ();
    }


    /** {@inheritDoc} */
    @Override
    protected void resetParameter (final int index)
    {
        final int extenderOffset = this.surface.getExtenderOffset ();
        final IMarker item = this.model.getMarkerBank ().getItem (extenderOffset + index);
        if (!item.doesExist ())
            return;
        if (this.surface.isShiftPressed ())
            item.select ();
        else
            item.launch (true);
    }


    /** {@inheritDoc} */
    @Override
    protected void updateKnobLEDs ()
    {
        final int extenderOffset = this.surface.getExtenderOffset ();
        final IMarkerBank markerBank = this.model.getMarkerBank ();
        for (int i = 0; i < 8; i++)
        {
            final boolean exists = markerBank.getItem (extenderOffset + i).doesExist ();
            this.surface.setKnobLED (i, MCUControlSurface.KNOB_LED_MODE_WRAP, 0, exists ? 1 : 0);
        }
    }
}