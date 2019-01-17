// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.mode;

import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.daw.IApplication;
import de.mossgrabers.framework.daw.IArranger;
import de.mossgrabers.framework.daw.IMixer;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.graphics.display.DisplayModel;
import de.mossgrabers.framework.mode.AbstractMode;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Editing of accent parameters.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class FrameMode extends BaseMode
{
    private static final String    ROW0          = "Layouts:                  Panels:                                   ";
    private static final String    ROW1          = "Arrange  Mix     Edit     Notes   Automate Device  Mixer    Inspectr";
    private static final String    ARRANGER_ROW2 = "Arranger:                                                           ";
    private static final String    ARRANGER_ROW3 = "ClpLnchr I/O     Markers  TimelineFXTracks Follow  TrckHght Full    ";
    private static final String    MIXER_ROW2    = "Mixer:                                                              ";
    private static final String    MIXER_ROW3    = "ClpLnchr I/O     CrossFde Device  Meters   Sends            Full    ";
    private static final String    EMPTY         = "                                                                    ";

    private static final String [] LAYOUTS1      =
    {
        "Layouts",
        "",
        "",
        "Panels",
        "",
        "",
        "",
        ""
    };
    private static final String [] LAYOUTS2      =
    {
        "Arrange",
        "Mix",
        "Edit",
        "Notes",
        "Automate",
        "Device",
        "Mixer",
        "Inspector"
    };
    private static final String [] ARRANGER1     =
    {
        "Arranger",
        "",
        "",
        "",
        "",
        "",
        "",
        ""
    };
    private static final String [] ARRANGER2     =
    {
        "Clip Launcher",
        "I/O",
        "Markers",
        "Timeline",
        "FX Tracks",
        "Follow",
        "Track Height",
        "Fullscreen"
    };
    private static final String [] MIXER1        =
    {
        "Mixer",
        "",
        "",
        "",
        "",
        "",
        "",
        ""
    };
    private static final String [] MIXER2        =
    {
        "Clip Launcher",
        "I/O",
        "Crossfader",
        "Device",
        "Meters",
        "Sends",
        "",
        "Fullscreen"
    };


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public FrameMode (final PushControlSurface surface, final IModel model)
    {
        super ("Frame", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        this.setActive (true);
    }


    /** {@inheritDoc} */
    @Override
    public void onDeactivate ()
    {
        this.setActive (false);
    }


    /** {@inheritDoc} */
    @Override
    public void onFirstRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;
        final IApplication app = this.model.getApplication ();
        switch (index)
        {
            case 0:
                app.setPanelLayout ("ARRANGE");
                break;
            case 1:
                app.setPanelLayout ("MIX");
                break;
            case 2:
                app.setPanelLayout ("EDIT");
                break;
            case 3:
                app.toggleNoteEditor ();
                break;
            case 4:
                app.toggleAutomationEditor ();
                break;
            case 5:
                app.toggleDevices ();
                break;
            case 6:
                app.toggleMixer ();
                break;
            case 7:
                app.toggleInspector ();
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onSecondRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;
        final IApplication app = this.model.getApplication ();
        if (app.isArrangeLayout ())
        {
            final IArranger arrange = this.model.getArranger ();
            switch (index)
            {
                case 0:
                    arrange.toggleClipLauncher ();
                    break;
                case 1:
                    arrange.toggleIoSection ();
                    break;
                case 2:
                    arrange.toggleCueMarkerVisibility ();
                    break;
                case 3:
                    arrange.toggleTimeLine ();
                    break;
                case 4:
                    arrange.toggleEffectTracks ();
                    break;
                case 5:
                    arrange.togglePlaybackFollow ();
                    break;
                case 6:
                    arrange.toggleTrackRowHeight ();
                    break;
                case 7:
                    app.toggleFullScreen ();
                    break;
            }
        }
        else if (app.isMixerLayout ())
        {
            final IMixer mix = this.model.getMixer ();
            switch (index)
            {
                case 0:
                    mix.toggleClipLauncherSectionVisibility ();
                    break;
                case 1:
                    mix.toggleIoSectionVisibility ();
                    break;
                case 2:
                    mix.toggleCrossFadeSectionVisibility ();
                    break;
                case 3:
                    mix.toggleDeviceSectionVisibility ();
                    break;
                case 4:
                    mix.toggleMeterSectionVisibility ();
                    break;
                case 5:
                    mix.toggleSendsSectionVisibility ();
                    break;
                case 7:
                    app.toggleFullScreen ();
                    break;
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 ()
    {
        final IApplication app = this.model.getApplication ();
        final Display d = this.surface.getDisplay ();
        d.setRow (0, FrameMode.ROW0).setRow (1, FrameMode.ROW1).setRow (2, app.isArrangeLayout () ? FrameMode.ARRANGER_ROW2 : app.isMixerLayout () ? FrameMode.MIXER_ROW2 : FrameMode.EMPTY).setRow (3, app.isArrangeLayout () ? FrameMode.ARRANGER_ROW3 : app.isMixerLayout () ? FrameMode.MIXER_ROW3 : FrameMode.EMPTY);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 ()
    {
        final IApplication app = this.model.getApplication ();
        final DisplayModel message = this.surface.getDisplay ().getModel ();
        for (int i = 0; i < FrameMode.ARRANGER1.length; i++)
            message.addOptionElement (app.isArrangeLayout () ? FrameMode.ARRANGER1[i] : app.isMixerLayout () ? FrameMode.MIXER1[i] : "", app.isArrangeLayout () ? FrameMode.ARRANGER2[i] : app.isMixerLayout () ? FrameMode.MIXER2[i] : "", this.getSecondRowButtonState (i) > 0, FrameMode.LAYOUTS1[i], FrameMode.LAYOUTS2[i], this.getFirstRowButtonState (i), false);
        message.send ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateFirstRow ()
    {
        for (int i = 0; i < 8; i++)
            this.surface.updateButton (20 + i, this.getFirstRowButtonState (i) ? AbstractMode.BUTTON_COLOR_HI : AbstractMode.BUTTON_COLOR_ON);
    }


    /** {@inheritDoc} */
    @Override
    public void updateSecondRow ()
    {
        for (int i = 0; i < 8; i++)
        {
            final int state = this.getSecondRowButtonState (i);
            this.surface.updateButton (102 + i, state == 1 ? AbstractMode.BUTTON_COLOR2_HI : state == 0 ? AbstractMode.BUTTON_COLOR2_ON : AbstractMode.BUTTON_COLOR_OFF);
        }
    }


    private boolean getFirstRowButtonState (final int index)
    {
        switch (index)
        {
            case 0:
                return this.model.getApplication ().isArrangeLayout ();
            case 1:
                return this.model.getApplication ().isMixerLayout ();
            case 2:
                return this.model.getApplication ().isEditLayout ();
            default:
                return false;
        }
    }


    private int getSecondRowButtonState (final int index)
    {
        final IApplication app = this.model.getApplication ();
        if (app.isArrangeLayout ())
        {
            final IArranger arrange = this.model.getArranger ();
            switch (index)
            {
                case 0:
                    return arrange.isClipLauncherVisible () ? 1 : 0;
                case 1:
                    return arrange.isIoSectionVisible () ? 1 : 0;
                case 2:
                    return arrange.areCueMarkersVisible () ? 1 : 0;
                case 3:
                    return arrange.isTimelineVisible () ? 1 : 0;
                case 4:
                    return arrange.areEffectTracksVisible () ? 1 : 0;
                case 5:
                    return arrange.isPlaybackFollowEnabled () ? 1 : 0;
                case 6:
                    return arrange.hasDoubleRowTrackHeight () ? 1 : 0;
                default:
                    return 0;
            }
        }

        if (app.isMixerLayout ())
        {
            final IMixer mix = this.model.getMixer ();
            switch (index)
            {
                case 0:
                    return mix.isClipLauncherSectionVisible () ? 1 : 0;
                case 1:
                    return mix.isIoSectionVisible () ? 1 : 0;
                case 2:
                    return mix.isCrossFadeSectionVisible () ? 1 : 0;
                case 3:
                    return mix.isDeviceSectionVisible () ? 1 : 0;
                case 4:
                    return mix.isMeterSectionVisible () ? 1 : 0;
                case 5:
                    return mix.isSendSectionVisible () ? 1 : 0;
                case 6:
                    return -1;
                default:
                    return 0;
            }
        }

        return -1;
    }


    private void setActive (final boolean enable)
    {
        this.model.getArranger ().enableObservers (enable);
        this.model.getMixer ().enableObservers (enable);
    }
}
