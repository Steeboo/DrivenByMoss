// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw;

import de.mossgrabers.bitwig.framework.daw.data.DeviceImpl;
import de.mossgrabers.framework.controller.IValueChanger;
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
    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param deviceBank The device bank
     * @param numDevices The number of devices in the page of the bank
     */
    public DeviceBankImpl (final IHost host, final IValueChanger valueChanger, final DeviceBank deviceBank, final int numDevices)
    {
        super (host, valueChanger, deviceBank, numDevices);
        this.initItems ();
    }


    /** {@inheritDoc} */
    @Override
    protected void initItems ()
    {
        for (int i = 0; i < this.pageSize; i++)
            this.items.add (new DeviceImpl (this.bank.getItemAt (i), i));
    }
}