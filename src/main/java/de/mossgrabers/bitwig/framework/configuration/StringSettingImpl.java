// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.configuration;

import de.mossgrabers.framework.configuration.IStringSetting;
import de.mossgrabers.framework.configuration.IValueObserver;

import com.bitwig.extension.controller.api.SettableStringValue;
import com.bitwig.extension.controller.api.Setting;


/**
 * Bitwig implementation of a string setting.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class StringSettingImpl extends AbstractSetting<String> implements IStringSetting
{
    private SettableStringValue stringValue;


    /**
     * Constructor.
     *
     * @param stringValue The string value
     */
    public StringSettingImpl (final SettableStringValue stringValue)
    {
        super ((Setting) stringValue);

        this.stringValue = stringValue;
    }


    /** {@inheritDoc} */
    @Override
    public void set (final String value)
    {
        this.stringValue.set (value);
    }


    /** {@inheritDoc} */
    @Override
    public void addValueObserver (final IValueObserver<String> observer)
    {
        this.stringValue.addValueObserver (observer::update);
    }
}
