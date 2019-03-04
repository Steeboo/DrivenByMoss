// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.autocolor;

import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.configuration.IStringSetting;
import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.IHost;

import java.util.EnumMap;
import java.util.Map;


/**
 * The configuration settings for the Auto Color implementation.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class AutoColorConfiguration extends AbstractConfiguration
{
    private static final String     CATEGORY_AUTO_COLOR = "Auto Color";

    /** ID for dis-/enabling the auto color setting. */
    public static final Integer     ENABLE_AUTO_COLOR   = Integer.valueOf (50);
    /** First ID for all auto color settings. NOTE: All colors increase from that value! */
    public static final Integer     COLOR_REGEX         = Integer.valueOf (100);

    private boolean                 enableAutoColor;
    private Map<NamedColor, String> colorRegEx          = new EnumMap<> (NamedColor.class);


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     */
    public AutoColorConfiguration (final IHost host, final IValueChanger valueChanger)
    {
        super (host, valueChanger);
    }


    /** {@inheritDoc} */
    @Override
    public void init (final ISettingsUI settingsUI)
    {
        ///////////////////////////
        // Auto Color

        final IEnumSetting enableAutoColorSetting = settingsUI.getEnumSetting (CATEGORY_AUTO_COLOR, CATEGORY_AUTO_COLOR, ON_OFF_OPTIONS, ON_OFF_OPTIONS[1]);
        enableAutoColorSetting.addValueObserver (value -> {
            this.enableAutoColor = ON_OFF_OPTIONS[1].equals (value);
            this.notifyObservers (AutoColorConfiguration.ENABLE_AUTO_COLOR);
        });

        final NamedColor [] colors = NamedColor.values ();
        for (int i = 0; i < colors.length; i++)
        {
            final NamedColor color = colors[i];
            final IStringSetting setting = settingsUI.getStringSetting (color.getName (), CATEGORY_AUTO_COLOR, 256, "");
            final int index = i;
            setting.addValueObserver (value -> {
                this.colorRegEx.put (color, value);
                this.notifyObservers (Integer.valueOf (AutoColorConfiguration.COLOR_REGEX.intValue () + index));
            });
        }
    }


    /**
     * Returns true if auto coloring is enabled.
     *
     * @return True if auto coloring is enabled
     */
    public boolean isEnableAutoColor ()
    {
        return this.enableAutoColor;
    }


    /**
     * Get the regex value for the given color.
     *
     * @param color The color
     * @return The regex
     */
    public String getColorRegExValue (final NamedColor color)
    {
        return this.colorRegEx.get (color);
    }
}
