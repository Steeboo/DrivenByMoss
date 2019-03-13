// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw;

import de.mossgrabers.bitwig.framework.daw.data.DeviceImpl;
import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IDeviceBank;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.data.IDevice;

import com.bitwig.extension.controller.api.DeviceBank;


/**
 * Encapsulates the data of a device bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DeviceBankImpl extends AbstractBankImpl<DeviceBank, IDevice> implements IDeviceBank
{
    private final ICursorDevice cursorDevice;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param cursorDevice The cursor device
     * @param deviceBank The device bank
     * @param numDevices The number of devices in the page of the bank
     */
    public DeviceBankImpl (final IHost host, final IValueChanger valueChanger, final ICursorDevice cursorDevice, final DeviceBank deviceBank, final int numDevices)
    {
        super (host, valueChanger, deviceBank, numDevices);

        this.cursorDevice = cursorDevice;

        this.initItems ();
    }


    /** {@inheritDoc} */
    @Override
    protected void initItems ()
    {
        for (int i = 0; i < this.pageSize; i++)
            this.items.add (new DeviceImpl (this.bank.getItemAt (i), i));
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        this.cursorDevice.selectNext ();
    }

    @Override
    public void selectNextPage() {
        for (int i = 0; i < this.pageSize; i++){
            this.cursorDevice.selectNext ();
        }
    }

    @Override
    public void selectPreviousPage() {
        for (int i = 0; i < this.pageSize; i++){
            this.cursorDevice.selectPrevious ();
        }
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        this.cursorDevice.selectPrevious ();
    }


    /** {@inheritDoc} */
    @Override
    public IDevice getSelectedItem ()
    {
        return this.cursorDevice.doesExist () ? this.cursorDevice : null;
    }
}