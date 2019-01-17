// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.mode;

import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Interface to a mode. A mode has a number of knobs, one or two rows of buttons to navigate it and
 * optionally a display. Furthermore, a mode (normally) edits N items (e.g. track volume, pan,
 * device parameter). Items are organized in pages. The number of items on a page should be
 * identical to the number of knobs and buttons of a row.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface Mode
{
    /**
     * Get the name of the mode.
     *
     * @return The name
     */
    String getName ();


    /**
     * Called when a mode is activated.
     */
    void onActivate ();


    /**
     * Called when a mode is deactivated.
     */
    void onDeactivate ();


    /**
     * Get if this is a mode which is only temporarily displayed.
     *
     * @return True if temporary
     */
    boolean isTemporary ();


    /**
     * Update the display.
     */
    void updateDisplay ();


    /**
     * Update the first row buttons.
     */
    void updateFirstRow ();


    /**
     * Update the second row buttons.
     */
    void updateSecondRow ();


    /**
     * A knob has been used.
     *
     * @param index The index of the knob
     * @param value The value the knob sent
     */
    void onKnobValue (int index, int value);


    /**
     * Get the value of the parameter that is controlled by the knob.
     *
     * @param index The index of the knob
     * @return The value or -1
     */
    int getKnobValue (int index);


    /**
     * A knob has been touched.
     *
     * @param index The index of the knob
     * @param isTouched True if the knob has been touched
     */
    void onKnobTouch (final int index, final boolean isTouched);


    /**
     * A row button has been pressed.
     *
     * @param row The number of the button row
     * @param index The index of the button
     * @param event The button event
     */
    void onButton (int row, int index, ButtonEvent event);


    /**
     * Select an item.
     *
     * @param index The items index
     */
    void selectItem (int index);


    /**
     * Get the selected item if any.
     *
     * @return THe selected item or null
     */
    String getSelectedItemName ();


    /**
     * Selects the previous item in the page. Scrolls the page to the previous page if the first
     * item was selected (and if there is a previous page).
     */
    void selectPreviousItem ();


    /**
     * Selects the next item in the page. Scrolls the page to the next page if the last item on the
     * page was selected (if there is a next page).
     */
    void selectNextItem ();


    /**
     * Selects the previous item page and selects the last item of the page.
     */
    void selectPreviousItemPage ();


    /**
     * Selects the next item page and selects the first item of the page.
     */
    void selectNextItemPage ();
}
