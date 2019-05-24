// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchpad;

import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.IHost;


/**
 * The configuration settings for Launchpad.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class LaunchpadConfiguration extends AbstractConfiguration
{
    private final boolean isPro;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param isPro Is Pro or MkII?
     */
    public LaunchpadConfiguration (final IHost host, final IValueChanger valueChanger, final boolean isPro)
    {
        super (host, valueChanger);
        this.isPro = isPro;
    }


    /**
     * Is Launchpad Pro or MkII?
     *
     * @return True if Pro
     */
    public boolean isPro ()
    {
        return this.isPro;
    }


    /** {@inheritDoc} */
    @Override
    public void init (final ISettingsUI settingsUI)
    {
        ///////////////////////////
        // Play and Sequence

        this.activateQuantizeAmountSetting (settingsUI);

        ///////////////////////////
        // Scale

        this.activateScaleSetting (settingsUI);
        this.activateScaleBaseSetting (settingsUI);
        this.activateScaleInScaleSetting (settingsUI);
        this.activateScaleLayoutSetting (settingsUI);

        ///////////////////////////
        // Workflow

        this.activateBehaviourOnStopSetting (settingsUI);
        this.activateSelectClipOnLaunchSetting (settingsUI);
        this.activateFlipSessionSetting (settingsUI);
        if (this.isPro)
            this.activateFlipRecordSetting (settingsUI);
        this.activateAutoSelectDrumSetting (settingsUI);
        this.activateTurnOffEmptyDrumPadsSetting (settingsUI);
        this.activateNewClipLengthSetting (settingsUI);

        ///////////////////////////
        // Pad Sensitivity

        this.activateConvertAftertouchSetting (settingsUI);
    }
}
