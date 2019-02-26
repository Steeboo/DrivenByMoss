// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.mode.track;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.controller.display.Format;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ISendBank;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.graphics.display.DisplayModel;
import de.mossgrabers.framework.graphics.grid.SendData;
import de.mossgrabers.framework.utils.Pair;


/**
 * Mode for editing a track parameters.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TrackMode extends AbstractTrackMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public TrackMode (final PushControlSurface surface, final IModel model)
    {
        super ("Track", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        final ITrack selectedTrack = this.model.getSelectedTrack ();
        if (selectedTrack == null)
            return;

        switch (index)
        {
            case 0:
                selectedTrack.changeVolume (value);
                return;
            case 1:
                selectedTrack.changePan (value);
                return;
            default:
                // Not used
                break;
        }

        final ISendBank sendBank = selectedTrack.getSendBank ();
        final PushConfiguration config = this.surface.getConfiguration ();
        if (this.isPush2)
        {
            switch (index)
            {
                case 2:
                    this.changeCrossfader (value, selectedTrack);
                    break;
                case 3:
                    break;
                default:
                    final int sendOffset = config.isSendsAreToggled () ? 0 : 4;
                    sendBank.getItem (index - sendOffset).changeValue (value);
                    break;
            }
            return;
        }

        if (index == 2)
        {
            if (config.isDisplayCrossfader ())
                this.changeCrossfader (value, selectedTrack);
            else
                sendBank.getItem (0).changeValue (value);
        }
        else
            sendBank.getItem (index - (config.isDisplayCrossfader () ? 3 : 2)).changeValue (value);
    }


    private void changeCrossfader (final int value, final ITrack selectedTrack)
    {
        if (this.increaseKnobMovement ())
            selectedTrack.changeCrossfadeModeAsNumber (value);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        final ITrack selectedTrack = this.model.getSelectedTrack ();
        if (selectedTrack == null)
            return;

        this.isKnobTouched[index] = isTouched;

        final ISendBank sendBank = selectedTrack.getSendBank ();
        final PushConfiguration config = this.surface.getConfiguration ();
        if (this.isPush2)
        {
            if (isTouched && this.surface.isDeletePressed ())
            {
                this.surface.setButtonConsumed (PushControlSurface.PUSH_BUTTON_DELETE);
                switch (index)
                {
                    case 0:
                        selectedTrack.resetVolume ();
                        break;
                    case 1:
                        selectedTrack.resetPan ();
                        break;
                    case 2:
                        selectedTrack.setCrossfadeMode ("AB");
                        break;
                    case 3:
                        // Not used
                        break;
                    default:
                        sendBank.getItem (index - 4).resetValue ();
                        break;
                }
                return;
            }

            switch (index)
            {
                case 0:
                    selectedTrack.touchVolume (isTouched);
                    break;
                case 1:
                    selectedTrack.touchPan (isTouched);
                    break;
                case 2:
                case 3:
                    // Not used
                    break;
                default:
                    final int sendIndex = index - 4;
                    sendBank.getItem (sendIndex).touchValue (isTouched);
                    break;
            }

            this.checkStopAutomationOnKnobRelease (isTouched);
            return;
        }

        if (isTouched && this.surface.isDeletePressed ())
        {
            this.surface.setButtonConsumed (PushControlSurface.PUSH_BUTTON_DELETE);
            switch (index)
            {
                case 0:
                    selectedTrack.resetVolume ();
                    break;
                case 1:
                    selectedTrack.resetPan ();
                    break;
                case 2:
                    if (config.isDisplayCrossfader ())
                        selectedTrack.setCrossfadeMode ("AB");
                    else
                        sendBank.getItem (0).resetValue ();
                    break;
                default:
                    sendBank.getItem (index - (config.isDisplayCrossfader () ? 3 : 2)).resetValue ();
                    break;
            }
            return;
        }

        switch (index)
        {
            case 0:
                selectedTrack.touchVolume (isTouched);
                break;
            case 1:
                selectedTrack.touchPan (isTouched);
                break;
            case 2:
                if (!config.isDisplayCrossfader ())
                    sendBank.getItem (0).touchValue (isTouched);
                break;
            default:
                final int sendIndex = index - (config.isDisplayCrossfader () ? 3 : 2);
                sendBank.getItem (sendIndex).touchValue (isTouched);
                break;
        }

        this.checkStopAutomationOnKnobRelease (isTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 ()
    {
        final Display d = this.surface.getDisplay ().clear ();
        final ITrack t = this.model.getSelectedTrack ();
        if (t == null)
            d.setRow (1, "                     Please selecta track...                        ").done (0).done (2);
        else
        {
            final PushConfiguration config = this.surface.getConfiguration ();
            d.setCell (0, 0, "Volume").setCell (1, 0, t.getVolumeStr (8)).setCell (2, 0, config.isEnableVUMeters () ? t.getVu () : t.getVolume (), Format.FORMAT_VALUE);
            d.setCell (0, 1, "Pan").setCell (1, 1, t.getPanStr (8)).setCell (2, 1, t.getPan (), Format.FORMAT_PAN);

            int sendStart = 2;
            int sendCount = 6;
            if (config.isDisplayCrossfader ())
            {
                sendStart = 3;
                sendCount = 5;
                final String crossfadeMode = t.getCrossfadeMode ();
                final int upperBound = this.model.getValueChanger ().getUpperBound ();
                d.setCell (0, 2, "Crossfdr").setCell (1, 2, "A".equals (crossfadeMode) ? "A" : "B".equals (crossfadeMode) ? "       B" : "   <> ");
                d.setCell (2, 2, "A".equals (crossfadeMode) ? 0 : "B".equals (crossfadeMode) ? upperBound : upperBound / 2, Format.FORMAT_PAN);
            }
            final boolean isEffectTrackBankActive = this.model.isEffectTrackBankActive ();
            final ISendBank sendBank = t.getSendBank ();
            for (int i = 0; i < sendCount; i++)
            {
                final int pos = sendStart + i;
                if (!isEffectTrackBankActive)
                {
                    final ISend send = sendBank.getItem (i);
                    if (send.doesExist ())
                        d.setCell (0, pos, send.getName ()).setCell (1, pos, send.getDisplayedValue (8)).setCell (2, pos, send.getValue (), Format.FORMAT_VALUE);
                }
            }
            d.done (0).done (1).done (2);
        }

        this.drawRow4 ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 ()
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final ITrack selectedTrack = tb.getSelectedItem ();

        // Get the index at which to draw the Sends element
        final int selectedIndex = selectedTrack == null ? -1 : selectedTrack.getIndex ();
        int sendsIndex = selectedTrack == null || this.model.isEffectTrackBankActive () ? -1 : selectedTrack.getIndex () + 1;
        if (sendsIndex == 8)
            sendsIndex = 6;

        this.updateMenuItems (0);

        final PushConfiguration config = this.surface.getConfiguration ();
        final DisplayModel message = this.surface.getDisplay ().getModel ();
        final boolean displayCrossfader = config.isDisplayCrossfader ();
        for (int i = 0; i < 8; i++)
        {
            final ITrack t = tb.getItem (i);

            // The menu item
            final Pair<String, Boolean> pair = this.menu.get (i);
            final String topMenu = pair.getKey ();
            final boolean topMenuSelected = pair.getValue ().booleanValue ();

            // Channel info
            final String bottomMenu = t.doesExist () ? t.getName () : "";
            final double [] bottomMenuColor = t.getColor ();
            final boolean isBottomMenuOn = t.isSelected ();

            final IValueChanger valueChanger = this.model.getValueChanger ();
            if (t.isSelected ())
            {
                final int crossfadeMode = displayCrossfader ? t.getCrossfadeModeAsNumber () : -1;
                final boolean enableVUMeters = config.isEnableVUMeters ();
                final int vuR = valueChanger.toDisplayValue (enableVUMeters ? t.getVuRight () : 0);
                final int vuL = valueChanger.toDisplayValue (enableVUMeters ? t.getVuLeft () : 0);
                message.addChannelElement (topMenu, topMenuSelected, bottomMenu, t.getType (), bottomMenuColor, isBottomMenuOn, valueChanger.toDisplayValue (t.getVolume ()), valueChanger.toDisplayValue (t.getModulatedVolume ()), this.isKnobTouched[0] ? t.getVolumeStr (8) : "", valueChanger.toDisplayValue (t.getPan ()), valueChanger.toDisplayValue (t.getModulatedPan ()), this.isKnobTouched[1] ? t.getPanStr (8) : "", vuL, vuR, t.isMute (), t.isSolo (), t.isRecArm (), crossfadeMode);
            }
            else if (sendsIndex == i)
            {
                final ITrack selTrack = tb.getItem (selectedIndex);
                final SendData [] sendData = new SendData [4];
                for (int j = 0; j < 4; j++)
                {
                    if (selTrack != null)
                    {
                        final int sendOffset = config.isSendsAreToggled () ? 4 : 0;
                        final int sendPos = sendOffset + j;
                        final ISend send = selTrack.getSendBank ().getItem (sendPos);
                        if (send != null)
                        {
                            final boolean exists = send.doesExist ();
                            sendData[j] = new SendData (send.getName (), exists && this.isKnobTouched[4 + j] ? send.getDisplayedValue (8) : "", valueChanger.toDisplayValue (exists ? send.getValue () : 0), valueChanger.toDisplayValue (exists ? send.getModulatedValue () : 0), true);
                            continue;
                        }
                    }
                    sendData[j] = new SendData ("", "", 0, 0, true);
                }
                message.addSendsElement (topMenu, topMenuSelected, bottomMenu, t.getType (), bottomMenuColor, isBottomMenuOn, sendData, true);
            }
            else
                message.addChannelSelectorElement (topMenu, topMenuSelected, bottomMenu, t.getType (), bottomMenuColor, isBottomMenuOn);
        }
        message.send ();
    }
}