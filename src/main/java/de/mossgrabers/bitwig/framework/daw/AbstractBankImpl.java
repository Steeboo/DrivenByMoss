// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw;

import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.AbstractBank;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.data.IItem;

import com.bitwig.extension.controller.api.Bank;


/**
 * An abstract bank which uses internally a Bitwig bank.
 *
 * @param <B> The specific Bitwig bank type
 * @param <T> The specific item type of the bank item
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractBankImpl<B extends Bank<?>, T extends IItem> extends AbstractBank<T>
{
    protected final IHost         host;
    protected final IValueChanger valueChanger;
    protected final B             bank;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param bank The bank to encapsulate
     * @param pageSize The number of elements in a page of the bank
     */
    public AbstractBankImpl (final IHost host, final IValueChanger valueChanger, final B bank, final int pageSize)
    {
        super (pageSize);

        this.host = host;
        this.valueChanger = valueChanger;
        this.bank = bank;

        if (this.bank == null)
            return;

        this.bank.scrollPosition ().markInterested ();
        this.bank.canScrollBackwards ().markInterested ();
        this.bank.canScrollForwards ().markInterested ();
        this.bank.itemCount ().markInterested ();
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        for (final IItem item: this.items)
            item.enableObservers (enable);

        if (this.bank == null)
            return;

        this.bank.scrollPosition ().setIsSubscribed (enable);
        this.bank.canScrollBackwards ().setIsSubscribed (enable);
        this.bank.canScrollForwards ().setIsSubscribed (enable);
        this.bank.itemCount ().setIsSubscribed (enable);
    }


    /** {@inheritDoc} */
    @Override
    public int getItemCount ()
    {
        return this.bank.itemCount ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public int getScrollPosition ()
    {
        return this.bank.scrollPosition ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollBackwards ()
    {
        this.bank.scrollBackwards ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollForwards ()
    {
        this.bank.scrollForwards ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollPageBackwards ()
    {
        this.bank.scrollPageBackwards ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollPageForwards ()
    {
        this.bank.scrollPageForwards ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollBackwards ()
    {
        return this.bank.canScrollBackwards ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollForwards ()
    {
        return this.bank.canScrollForwards ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollTo (final int position)
    {
        this.scrollTo (position, true);
    }


    /** {@inheritDoc} */
    @Override
    public void scrollTo (final int position, final boolean adjustPage)
    {
        if (position < 0 || position >= this.getItemCount ())
            return;
        final int pageSize = this.getPageSize ();
        final int pos = adjustPage ? position / pageSize * pageSize : position;
        this.bank.scrollIntoView (position);
        this.bank.scrollPosition ().set (pos);
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        final T sel = this.getSelectedItem ();
        final int index = sel == null ? 0 : sel.getIndex () + 1;
        if (index == this.pageSize)
            this.selectNextPage ();
        else
            this.getItem (index).select ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        final T sel = this.getSelectedItem ();
        final int index = sel == null ? 0 : sel.getIndex () - 1;
        if (index == -1)
            this.selectPreviousPage ();
        else
            this.getItem (index).select ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousPage ()
    {
        if (!this.canScrollBackwards ())
            return;
        this.scrollPageBackwards ();
        this.host.scheduleTask ( () -> this.getItem (this.pageSize - 1).select (), 75);
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextPage ()
    {
        if (!this.canScrollForwards ())
            return;
        this.scrollPageForwards ();
        this.host.scheduleTask ( () -> this.getItem (0).select (), 75);
    }
}
