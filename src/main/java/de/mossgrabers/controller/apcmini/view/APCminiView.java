// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.apcmini.view;

import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Additional interface for APCmini views.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface APCminiView
{
    /**
     * A track button was pressed
     *
     * @param index The index of the button
     * @param event The event
     */
    void onSelectTrack (final int index, final ButtonEvent event);
}
