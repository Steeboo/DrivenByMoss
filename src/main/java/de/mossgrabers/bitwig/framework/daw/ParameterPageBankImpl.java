// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw;

import de.mossgrabers.framework.daw.IParameterPageBank;
import de.mossgrabers.framework.observer.ItemSelectionObserver;

import com.bitwig.extension.controller.api.CursorRemoteControlsPage;
import com.bitwig.extension.controller.api.SettableIntegerValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Encapsulates the data of parameter pages. Bitwig pages have no banking, we need to do it
 * ourselves.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ParameterPageBankImpl implements IParameterPageBank
{
    private CursorRemoteControlsPage remoteControls;
    private List<String>             pageNames = new ArrayList<> ();
    private int                      pageSize;


    /**
     * Constructor.
     *
     * @param remoteControlsPage The remote controls bank
     * @param numParameterPages The number of parameter pages in the page of the bank
     */
    public ParameterPageBankImpl (final CursorRemoteControlsPage remoteControlsPage, final int numParameterPages)
    {
        this.pageSize = numParameterPages;

        this.remoteControls = remoteControlsPage;

        this.remoteControls.selectedPageIndex ().markInterested ();
        this.remoteControls.pageNames ().addValueObserver (this::handlePageNames);
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        this.remoteControls.selectedPageIndex ().setIsSubscribed (enable);
        this.remoteControls.pageNames ().setIsSubscribed (enable);
    }


    /** {@inheritDoc} */
    @Override
    public int getItemCount ()
    {
        return this.pageNames.size ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollBackwards ()
    {
        return this.canScrollPageBackwards ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollForwards ()
    {
        return this.getSelectedItemPosition () < this.pageNames.size ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollPageBackwards ()
    {
        int selectedItemPosition = this.getSelectedItemPosition ();
        return selectedItemPosition > 0;
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollPageForwards ()
    {
        final int sel = this.getSelectedItemPosition ();
        final int end = sel / this.pageSize * this.pageSize + this.pageSize;
        return end < this.pageNames.size ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollBackwards ()
    {
        final SettableIntegerValue index = this.remoteControls.selectedPageIndex ();
        index.set (Math.max (index.get () - 1, 0));
    }


    /** {@inheritDoc} */
    @Override
    public void scrollForwards ()
    {
        final SettableIntegerValue index = this.remoteControls.selectedPageIndex ();
        index.set (Math.min (index.get () + 1, this.pageNames.size () - 1));
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousPage ()
    {               
        for (int i = 0; i < this.pageSize; i++){
            if (this.remoteControls.hasPrevious().get())
            this.remoteControls.selectPreviousPage (false);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextPage ()
    {       
        for (int i = 0; i < this.pageSize; i++){
            if (this.remoteControls.hasNext().get())
            this.remoteControls.selectNextPage (false);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void scrollTo (final int position)
    {
        final SettableIntegerValue index = this.remoteControls.selectedPageIndex ();
        index.set (Math.max (Math.min (position, this.pageNames.size () - 1), 0));
    }


    /** {@inheritDoc} */
    @Override
    public void scrollTo (final int position, final boolean adjustPage)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public int getPageSize ()
    {
        return this.pageSize;
    }


    /** {@inheritDoc} */
    @Override
    public String getItem (final int index)
    {
        final int start = this.getScrollPosition () + index;
        return start >= 0 && start < this.pageNames.size () ? this.pageNames.get (start) : "";
    }


    /** {@inheritDoc} */
    @Override
    public int getSelectedItemPosition ()
    {
        return this.remoteControls.selectedPageIndex ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public int getSelectedItemIndex ()
    {
        return this.getSelectedItemPosition () % this.pageSize;
    }


    /** {@inheritDoc} */
    @Override
    public String getSelectedItem ()
    {
        final int sel = this.getSelectedItemPosition ();
        return sel >= 0 && sel < this.pageNames.size () ? this.pageNames.get (sel) : "";
    }


    /** {@inheritDoc} */
    @Override
    public List<String> getSelectedItems ()
    {
        return Collections.singletonList (this.getSelectedItem ());
    }


    /** {@inheritDoc} */
    @Override
    public void selectPage (final int index)
    {
        this.scrollTo (this.getScrollPosition () + index);
    }


    /** {@inheritDoc} */
    @Override
    public void addSelectionObserver (final ItemSelectionObserver observer)
    {
        // Not selected
    }


    /** {@inheritDoc} */
    @Override
    public int getScrollPosition ()
    {
        final int selectedItemPosition = this.getSelectedItemPosition ();
        return selectedItemPosition / this.pageSize * this.pageSize;
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void selectItemAtPosition (final int position)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public int getPositionOfLastItem ()
    {
        return Math.min (this.getScrollPosition () + this.pageSize, this.pageNames.size ()) - 1;
    }


    private void handlePageNames (final String [] pageNames)
    {
        this.pageNames.clear ();
        Collections.addAll (this.pageNames, pageNames);
    }
}