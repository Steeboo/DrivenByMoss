// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.display.DummyDisplay;
import de.mossgrabers.framework.daw.IHost;


/**
 * Dummy control surface for implementations, which only implement a protocol.
 *
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DummyControlSurface<C extends Configuration> extends AbstractControlSurface<C>
{
    /**
     * Constructor.
     *
     * @param host The host
     * @param colorManager The color manager
     * @param configuration The configuration
     */
    public DummyControlSurface (final IHost host, final ColorManager colorManager, final C configuration)
    {
        super (host, configuration, colorManager, null, null, null, new int [0]);

        this.setDisplay (new DummyDisplay (this.host));
    }


    /** {@inheritDoc} */
    @Override
    public void setButtonEx (int button, int channel, int value)
    {
        // Intentionally empty
    }
}