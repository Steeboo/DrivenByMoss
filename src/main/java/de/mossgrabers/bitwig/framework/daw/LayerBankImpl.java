// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw;

import de.mossgrabers.bitwig.framework.daw.data.LayerImpl;
import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.DAWColors;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ILayerBank;
import de.mossgrabers.framework.daw.ISceneBank;
import de.mossgrabers.framework.daw.data.ILayer;

import com.bitwig.extension.controller.api.CursorDeviceLayer;
import com.bitwig.extension.controller.api.DeviceLayer;
import com.bitwig.extension.controller.api.DeviceLayerBank;


/**
 * Encapsulates the data of a layer bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class LayerBankImpl extends AbstractBankImpl<DeviceLayerBank, ILayer> implements ILayerBank
{
    private final CursorDeviceLayer cursorDeviceLayer;
    private int                     numSends;
    private int                     numDevices;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param layerBank The layer bank
     * @param cursorDeviceLayer The cursor device layer
     * @param numLayers The number of layers in the page of the bank
     * @param numSends The number of sends
     * @param numDevices The number of devices
     */
    public LayerBankImpl (final IHost host, final IValueChanger valueChanger, final DeviceLayerBank layerBank, final CursorDeviceLayer cursorDeviceLayer, final int numLayers, final int numSends, final int numDevices)
    {
        super (host, valueChanger, layerBank, numLayers);

        this.cursorDeviceLayer = cursorDeviceLayer;

        this.numSends = numSends;
        this.numDevices = numDevices;

        this.initItems ();
    }


    /** {@inheritDoc} */
    @Override
    protected void initItems ()
    {
        for (int i = 0; i < this.pageSize; i++)
        {
            final DeviceLayer deviceLayer = this.bank.getItemAt (i);
            this.items.add (new LayerImpl (this.host, this.valueChanger, deviceLayer, i, this.numSends, this.numDevices));
        }
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        super.enableObservers (enable);

        for (int i = 0; i < this.getPageSize (); i++)
            this.getItem (i).enableObservers (enable);
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasZeroLayers ()
    {
        for (int i = 0; i < this.getPageSize (); i++)
        {
            if (this.getItem (i).doesExist ())
                return false;
        }
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public String getSelectedChannelColorEntry ()
    {
        final ILayer sel = this.getSelectedItem ();
        if (sel == null)
            return DAWColors.COLOR_OFF;
        final double [] color = sel.getColor ();
        return DAWColors.getColorIndex (color[0], color[1], color[2]);
    }


    /** {@inheritDoc} */
    @Override
    public void stop ()
    {
        // No clips in layers.
    }


    /** {@inheritDoc} */
    @Override
    public ISceneBank getSceneBank ()
    {
        // No clips in layers.
        return null;
    }


    /** {@inheritDoc} */
    @Override
    public void setIndication (final boolean enable)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollBackwards ()
    {
        return this.cursorDeviceLayer.hasPrevious ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollForwards ()
    {
        return this.cursorDeviceLayer.hasNext ().get ();
    }
}