// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mcu.mode.track;

import de.mossgrabers.controller.mcu.controller.MCUControlSurface;
import de.mossgrabers.controller.mcu.mode.BaseMode;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Abstract base mode for all track modes.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractTrackMode extends BaseMode
{
    /**
     * Constructor.
     *
     * @param name The name of the mode
     * @param surface The control surface
     * @param model The model
     */
    public AbstractTrackMode (final String name, final MCUControlSurface surface, final IModel model)
    {
        super (name, surface, model);
    }


    protected boolean drawTrackHeader ()
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();

        final Display d = this.surface.getDisplay ().clear ();

        // Format track names
        final int extenderOffset = this.surface.getExtenderOffset ();
        for (int i = 0; i < 8; i++)
        {
            final ITrack t = tb.getItem (extenderOffset + i);
            d.setCell (0, i, StringUtils.shortenAndFixASCII (t.getName (), 6));
        }
        d.done (0);

        return true;
    }


    /** {@inheritDoc} */
    @Override
    protected ITrackBank getBank ()
    {
        return this.model.getCurrentTrackBank ();
    }
}