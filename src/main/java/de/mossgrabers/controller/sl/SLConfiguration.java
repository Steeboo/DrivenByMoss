// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.sl;

import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.IValueChanger;


/**
 * The configuration settings for the Novation SL.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SLConfiguration extends AbstractConfiguration
{
    /** Touchpad mode. */
    public static final Integer    TOUCHPAD_MODE              = Integer.valueOf (30);
    /** Use drum pads for mode selection. */
    public static final Integer    DRUMPADS_AS_MODE_SELECTION = Integer.valueOf (31);

    /** Touchpad mode: Use as the crossfader. */
    public static final String     TOUCHPAD_MODE_CROSSFADER   = "Crossfader";
    /** Touchpad mode: Use to modify the first two remote parameters. */
    public static final String     TOUCHPAD_MODE_PARAMETER    = "Remote Parameter 1&2";
    private static final String [] TOUCHPAD_OPTIONS           = new String []
    {
        TOUCHPAD_MODE_CROSSFADER,
        TOUCHPAD_MODE_PARAMETER
    };

    private String                 touchpadMode;
    private boolean                drumpadsAsModeSelection    = false;
    private final boolean          isMkII;


    /**
     * Constructor.
     *
     * @param valueChanger The value changer
     * @param isMkII Is MkI or MkII?
     */
    public SLConfiguration (final IValueChanger valueChanger, final boolean isMkII)
    {
        super (valueChanger);
        this.isMkII = isMkII;
    }


    /**
     * Is SL MkI or MkII?
     *
     * @return True if Pro
     */
    public boolean isMkII ()
    {
        return this.isMkII;
    }


    /** {@inheritDoc} */
    @Override
    public void init (final ISettingsUI settingsUI)
    {
        ///////////////////////////
        // Play and Sequence

        final IEnumSetting touchpadModeSetting = settingsUI.getEnumSetting ("Mode", "Touchpad", TOUCHPAD_OPTIONS, TOUCHPAD_OPTIONS[1]);
        touchpadModeSetting.addValueObserver (value -> {
            this.touchpadMode = value;
            this.notifyObservers (TOUCHPAD_MODE);
        });

        ///////////////////////////
        // Workflow

        this.activateBehaviourOnStopSetting (settingsUI);
        this.activateDisplayCrossfaderSetting (settingsUI);
        this.activateNewClipLengthSetting (settingsUI);

        final IEnumSetting drumpadsAsModeSelectionSetting = settingsUI.getEnumSetting ("Use drum pads for mode selection", CATEGORY_WORKFLOW, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        drumpadsAsModeSelectionSetting.addValueObserver (value -> {
            this.drumpadsAsModeSelection = ON_OFF_OPTIONS[1].equals (value);
            this.notifyObservers (DRUMPADS_AS_MODE_SELECTION);
        });
    }


    /**
     * Get the touchpad mode.
     *
     * @return The touchpad mode
     */
    public String getTouchpadMode ()
    {
        return this.touchpadMode;
    }


    /**
     * Returns true if the drum pads should be used for mode selection.
     *
     * @return True if the drum pads should be used for mode selection
     */
    public boolean isDrumpadsAsModeSelection ()
    {
        return this.drumpadsAsModeSelection;
    }
}
