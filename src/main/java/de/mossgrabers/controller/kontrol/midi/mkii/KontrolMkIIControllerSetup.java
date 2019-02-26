// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.midi.mkii;

import de.mossgrabers.controller.kontrol.midi.mkii.command.trigger.KontrolRecordCommand;
import de.mossgrabers.controller.kontrol.midi.mkii.controller.KontrolMkIIColors;
import de.mossgrabers.controller.kontrol.midi.mkii.controller.KontrolMkIIControlSurface;
import de.mossgrabers.controller.kontrol.midi.mkii.view.ControlView;
import de.mossgrabers.framework.command.Commands;
import de.mossgrabers.framework.command.trigger.application.DeleteCommand;
import de.mossgrabers.framework.command.trigger.application.RedoCommand;
import de.mossgrabers.framework.command.trigger.application.UndoCommand;
import de.mossgrabers.framework.command.trigger.clip.NewCommand;
import de.mossgrabers.framework.command.trigger.clip.QuantizeCommand;
import de.mossgrabers.framework.command.trigger.clip.StartClipCommand;
import de.mossgrabers.framework.command.trigger.clip.StopClipCommand;
import de.mossgrabers.framework.command.trigger.track.MuteCommand;
import de.mossgrabers.framework.command.trigger.track.SoloCommand;
import de.mossgrabers.framework.command.trigger.transport.MetronomeCommand;
import de.mossgrabers.framework.command.trigger.transport.PlayCommand;
import de.mossgrabers.framework.command.trigger.transport.StopCommand;
import de.mossgrabers.framework.command.trigger.transport.TapTempoCommand;
import de.mossgrabers.framework.command.trigger.transport.ToggleLoopCommand;
import de.mossgrabers.framework.command.trigger.transport.WriteArrangerAutomationCommand;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.DefaultValueChanger;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ISceneBank;
import de.mossgrabers.framework.daw.ISlotBank;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.FrameworkException;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.view.ViewManager;
import de.mossgrabers.framework.view.Views;


/**
 * Setup for the Komplete Kontrol MkII.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class KontrolMkIIControllerSetup extends AbstractControllerSetup<KontrolMkIIControlSurface, KontrolMkIIConfiguration>
{
    private static final Integer CONT_COMMAND_HELLO           = Integer.valueOf (100);
    private static final Integer CONT_COMMAND_MOVE_TRACK      = Integer.valueOf (101);
    private static final Integer CONT_COMMAND_MOVE_TRACK_BANK = Integer.valueOf (102);
    private static final Integer CONT_COMMAND_TRACK_SELECT    = Integer.valueOf (103);
    private static final Integer CONT_COMMAND_TRACK_MUTE      = Integer.valueOf (104);
    private static final Integer CONT_COMMAND_TRACK_SOLO      = Integer.valueOf (105);
    private static final Integer CONT_COMMAND_TRACK_ARM       = Integer.valueOf (106);
    private static final Integer CONT_COMMAND_NAVIGATE_CLIPS  = Integer.valueOf (107);
    private static final Integer CONT_COMMAND_MOVE_TRANSPORT  = Integer.valueOf (108);
    private static final Integer CONT_COMMAND_MOVE_LOOP       = Integer.valueOf (109);
    private static final Integer CONT_COMMAND_NAVIGATE_VOLUME = Integer.valueOf (110);
    private static final Integer CONT_COMMAND_NAVIGATE_PAN    = Integer.valueOf (111);

    static final int             CONT_COMMAND_TRACK_VOLUME    = 120;
    static final int             CONT_COMMAND_TRACK_PAN       = 130;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param factory The factory
     * @param settings The settings
     */
    public KontrolMkIIControllerSetup (final IHost host, final ISetupFactory factory, final ISettingsUI settings)
    {
        super (factory, host, settings);

        this.colorManager = new ColorManager ();
        KontrolMkIIColors.addColors (this.colorManager);
        this.valueChanger = new DefaultValueChanger (1024, 5, 1);
        this.configuration = new KontrolMkIIConfiguration (this.valueChanger);
    }


    /** {@inheritDoc} */
    @Override
    public void init ()
    {
        if (OperatingSystem.get () == OperatingSystem.LINUX)
            throw new FrameworkException ("Komplete Kontrol MkII is not supported on Linux since there is no Native Instruments DAW Integration Host.");

        super.init ();
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        this.updateButtons ();
        this.updateData ();
    }


    /** {@inheritDoc} */
    @Override
    protected void createScales ()
    {
        this.scales = new Scales (this.valueChanger, 0, 128, 128, 1);
        this.scales.setChromatic (true);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModel ()
    {
        final ModelSetup ms = new ModelSetup ();
        ms.setHasFullFlatTrackList (true);
        ms.setNumSends (0);
        ms.setNumFilterColumnEntries (0);
        ms.setNumResults (0);
        ms.setNumDeviceLayers (0);
        ms.setNumDevicesInBank (0);
        ms.setNumDrumPadLayers (0);
        ms.setNumMarkers (0);
        this.model = this.factory.createModel (this.colorManager, this.valueChanger, this.scales, ms);
    }


    /** {@inheritDoc} */
    @Override
    protected void createSurface ()
    {
        final IMidiAccess midiAccess = this.factory.createMidiAccess ();
        final IMidiOutput output = midiAccess.createOutput ();
        midiAccess.createInput (1, "Keyboard", "80????" /* Note off */, "90????" /* Note on */,
                "B0????" /* Sustainpedal + Modulation + Strip */, "D0????" /* Channel Aftertouch */,
                "E0????" /* Pitchbend */);
        this.surfaces.add (new KontrolMkIIControlSurface (this.host, this.colorManager, this.configuration, output, midiAccess.createInput (null)));
    }


    /** {@inheritDoc} */
    @Override
    protected void createViews ()
    {
        final KontrolMkIIControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();
        viewManager.registerView (Views.VIEW_CONTROL, new ControlView (surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        final KontrolMkIIControlSurface surface = this.getSurface ();
        this.addTriggerCommand (Commands.COMMAND_PLAY, KontrolMkIIControlSurface.KONTROL_BUTTON_PLAY, 15, new PlayCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_NEW, KontrolMkIIControlSurface.KONTROL_BUTTON_RESTART, 15, new NewCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_RECORD, KontrolMkIIControlSurface.KONTROL_BUTTON_RECORD, 15, new KontrolRecordCommand (true, this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_REC_ARM, KontrolMkIIControlSurface.KONTROL_BUTTON_COUNT_IN, 15, new KontrolRecordCommand (false, this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_STOP, KontrolMkIIControlSurface.KONTROL_BUTTON_STOP, 15, new StopCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_DELETE, KontrolMkIIControlSurface.KONTROL_BUTTON_CLEAR, 15, new DeleteCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_LOOP, KontrolMkIIControlSurface.KONTROL_BUTTON_LOOP, 15, new ToggleLoopCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_METRONOME, KontrolMkIIControlSurface.KONTROL_BUTTON_METRO, 15, new MetronomeCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_TAP_TEMPO, KontrolMkIIControlSurface.KONTROL_BUTTON_TEMPO, 15, new TapTempoCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_UNDO, KontrolMkIIControlSurface.KONTROL_BUTTON_UNDO, 15, new UndoCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_REDO, KontrolMkIIControlSurface.KONTROL_BUTTON_REDO, 15, new RedoCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_QUANTIZE, KontrolMkIIControlSurface.KONTROL_BUTTON_QUANTIZE, 15, new QuantizeCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_AUTOMATION, KontrolMkIIControlSurface.KONTROL_BUTTON_AUTOMATION, 15, new WriteArrangerAutomationCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_CLIP, KontrolMkIIControlSurface.KONTROL_PLAY_CLIP, 15, new StartClipCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_STOP_CLIP, KontrolMkIIControlSurface.KONTROL_STOP_CLIP, 15, new StopClipCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_MUTE, KontrolMkIIControlSurface.KONTROL_TOGGLE_MUTE, 15, new MuteCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_SOLO, KontrolMkIIControlSurface.KONTROL_TOGGLE_SOLO, 15, new SoloCommand<> (this.model, surface));
    }


    /** {@inheritDoc} */
    @Override
    protected void registerContinuousCommands ()
    {
        final KontrolMkIIControlSurface surface = this.getSurface ();

        this.addContinuousCommand (CONT_COMMAND_HELLO, KontrolMkIIControlSurface.CMD_HELLO, 15, value -> {
            surface.setProtocolVersion (value);

            // Initial flush of the whole DAW state...
            surface.clearCache ();
        });

        this.addContinuousCommand (CONT_COMMAND_MOVE_TRACK, KontrolMkIIControlSurface.KONTROL_NAVIGATE_TRACKS, 15, value -> {
            if (value == 1)
                this.model.getTrackBank ().selectNextItem ();
            else
                this.model.getTrackBank ().selectPreviousItem ();
        });

        this.addContinuousCommand (CONT_COMMAND_MOVE_TRACK_BANK, KontrolMkIIControlSurface.KONTROL_NAVIGATE_BANKS, 15, value -> {
            if (value == 1)
                this.model.getTrackBank ().selectNextPage ();
            else
                this.model.getTrackBank ().selectPreviousPage ();
        });

        this.addContinuousCommand (CONT_COMMAND_NAVIGATE_CLIPS, KontrolMkIIControlSurface.KONTROL_NAVIGATE_CLIPS, 15, value -> {
            final ITrack selectedTrack = this.model.getSelectedTrack ();
            if (selectedTrack == null)
                return;
            if (value == 1)
                selectedTrack.getSlotBank ().selectPreviousItem ();
            else if (value == 127)
                selectedTrack.getSlotBank ().selectNextItem ();
        });

        this.addContinuousCommand (CONT_COMMAND_MOVE_TRANSPORT, KontrolMkIIControlSurface.KONTROL_NAVIGATE_MOVE_TRANSPORT, 15, this::changeTransportPosition);
        this.addContinuousCommand (CONT_COMMAND_MOVE_LOOP, KontrolMkIIControlSurface.KONTROL_NAVIGATE_MOVE_LOOP, 15, this::changeLoopPosition);

        // Only on S models
        this.addContinuousCommand (CONT_COMMAND_NAVIGATE_VOLUME, KontrolMkIIControlSurface.KONTROL_CHANGE_VOLUME, 15, this::changeTransportPosition);
        this.addContinuousCommand (CONT_COMMAND_NAVIGATE_PAN, KontrolMkIIControlSurface.KONTROL_CHANGE_PAN, 15, this::changeTransportPosition);

        this.addContinuousCommand (CONT_COMMAND_TRACK_SELECT, KontrolMkIIControlSurface.KONTROL_BUTTON_SELECT, 15, value -> this.model.getTrackBank ().getItem (value).select ());
        this.addContinuousCommand (CONT_COMMAND_TRACK_MUTE, KontrolMkIIControlSurface.KONTROL_BUTTON_MUTE, 15, value -> this.model.getTrackBank ().getItem (value).toggleMute ());
        this.addContinuousCommand (CONT_COMMAND_TRACK_SOLO, KontrolMkIIControlSurface.KONTROL_BUTTON_SOLO, 15, value -> this.model.getTrackBank ().getItem (value).toggleSolo ());
        this.addContinuousCommand (CONT_COMMAND_TRACK_ARM, KontrolMkIIControlSurface.KONTROL_BUTTON_ARM, 15, value -> this.model.getTrackBank ().getItem (value).toggleRecArm ());

        for (int i = 0; i < 8; i++)
        {
            final int index = i;
            this.addContinuousCommand (Integer.valueOf (CONT_COMMAND_TRACK_VOLUME + i), KontrolMkIIControlSurface.KONTROL_KNOB_VOLUME + i, 15, value -> this.model.getTrackBank ().getItem (index).changeVolume (value));
            this.addContinuousCommand (Integer.valueOf (CONT_COMMAND_TRACK_PAN + i), KontrolMkIIControlSurface.KONTROL_KNOB_PAN + i, 15, value -> this.model.getTrackBank ().getItem (index).changePan (value));
        }
    }


    /** {@inheritDoc} */
    @Override
    public void startup ()
    {
        final IControlSurface<KontrolMkIIConfiguration> surface = this.getSurface ();
        surface.getViewManager ().setActiveView (Views.VIEW_CONTROL);

        this.getSurface ().sendCommand (KontrolMkIIControlSurface.CMD_HELLO, 0);
    }


    /** {@inheritDoc} */
    @Override
    protected void updateIndication (final Integer mode)
    {
        // Unused
    }


    private void updateButtons ()
    {
        final ITransport t = this.model.getTransport ();
        final IControlSurface<KontrolMkIIConfiguration> surface = this.getSurface ();
        surface.updateButton (KontrolMkIIControlSurface.KONTROL_BUTTON_PLAY, t.isPlaying () ? ColorManager.BUTTON_STATE_HI : ColorManager.BUTTON_STATE_ON);
        surface.updateButton (KontrolMkIIControlSurface.KONTROL_BUTTON_RECORD, t.isRecording () ? ColorManager.BUTTON_STATE_HI : ColorManager.BUTTON_STATE_ON);
        surface.updateButton (KontrolMkIIControlSurface.KONTROL_BUTTON_COUNT_IN, t.isRecording () ? ColorManager.BUTTON_STATE_HI : ColorManager.BUTTON_STATE_ON);
        surface.updateButton (KontrolMkIIControlSurface.KONTROL_BUTTON_STOP, !t.isPlaying () ? ColorManager.BUTTON_STATE_HI : ColorManager.BUTTON_STATE_ON);
        surface.updateButton (KontrolMkIIControlSurface.KONTROL_BUTTON_CLEAR, ColorManager.BUTTON_STATE_HI);
        surface.updateButton (KontrolMkIIControlSurface.KONTROL_BUTTON_LOOP, t.isLoop () ? ColorManager.BUTTON_STATE_HI : ColorManager.BUTTON_STATE_ON);
        surface.updateButton (KontrolMkIIControlSurface.KONTROL_BUTTON_METRO, t.isMetronomeOn () ? ColorManager.BUTTON_STATE_HI : ColorManager.BUTTON_STATE_ON);
        surface.updateButton (KontrolMkIIControlSurface.KONTROL_BUTTON_UNDO, ColorManager.BUTTON_STATE_HI);
        surface.updateButton (KontrolMkIIControlSurface.KONTROL_BUTTON_REDO, ColorManager.BUTTON_STATE_HI);
        surface.updateButton (KontrolMkIIControlSurface.KONTROL_BUTTON_QUANTIZE, ColorManager.BUTTON_STATE_HI);
        surface.updateButton (KontrolMkIIControlSurface.KONTROL_BUTTON_AUTOMATION, t.isWritingArrangerAutomation () ? ColorManager.BUTTON_STATE_HI : ColorManager.BUTTON_STATE_ON);
        surface.updateButton (KontrolMkIIControlSurface.KONTROL_BUTTON_TEMPO, ColorManager.BUTTON_STATE_ON);
    }


    private void updateData ()
    {
        final KontrolMkIIControlSurface surface = this.getSurface ();

        final int [] vuData = new int [16];

        final ITrackBank trackBank = this.model.getTrackBank ();

        for (int i = 0; i < 8; i++)
        {
            final ITrack track = trackBank.getItem (i);

            // Track Available
            final int trackType = TrackType.toTrackType (track.getType ());
            surface.sendKontrolTrackSysEx (KontrolMkIIControlSurface.KONTROL_TRACK_EXISTS, trackType, i);

            surface.sendKontrolTrackSysEx (KontrolMkIIControlSurface.KONTROL_BUTTON_SELECT, track.isSelected () ? 1 : 0, i);
            surface.sendKontrolTrackSysEx (KontrolMkIIControlSurface.KONTROL_BUTTON_MUTE, track.isMute () ? 1 : 0, i);
            surface.sendKontrolTrackSysEx (KontrolMkIIControlSurface.KONTROL_BUTTON_SOLO, track.isSolo () ? 1 : 0, i);
            surface.sendKontrolTrackSysEx (KontrolMkIIControlSurface.KONTROL_BUTTON_ARM, track.isRecArm () ? 1 : 0, i);

            surface.sendKontrolTrackSysEx (KontrolMkIIControlSurface.KONTROL_TRACK_VOLUME, 0, i, track.getVolumeStr ());
            surface.sendKontrolTrackSysEx (KontrolMkIIControlSurface.KONTROL_TRACK_PAN, 0, i, track.getPanStr ());
            surface.sendKontrolTrackSysEx (KontrolMkIIControlSurface.KONTROL_TRACK_NAME, 0, i, track.getName ());

            final int j = 2 * i;
            vuData[j] = this.valueChanger.toMidiValue (track.getVuLeft ());
            vuData[j + 1] = this.valueChanger.toMidiValue (track.getVuRight ());

            surface.updateButton (KontrolMkIIControlSurface.KONTROL_KNOB_VOLUME + i, this.valueChanger.toMidiValue (track.getVolume ()));
            surface.updateButton (KontrolMkIIControlSurface.KONTROL_KNOB_PAN + i, this.valueChanger.toMidiValue (track.getPan ()));
        }

        surface.sendKontrolTrackSysEx (KontrolMkIIControlSurface.KONTROL_TRACK_VU, 2, 0, vuData);
        surface.sendKontrolTrackSysEx (KontrolMkIIControlSurface.KONTROL_TRACK_INSTANCE, 0, 0, this.getKompleteInstance ());

        surface.updateButton (KontrolMkIIControlSurface.KONTROL_NAVIGATE_TRACKS, (trackBank.canScrollBackwards () ? 1 : 0) + (trackBank.canScrollForwards () ? 2 : 0));
        surface.updateButton (KontrolMkIIControlSurface.KONTROL_NAVIGATE_BANKS, (trackBank.canScrollPageBackwards () ? 1 : 0) + (trackBank.canScrollPageForwards () ? 2 : 0));

        final ITrack selectedTrack = trackBank.getSelectedItem ();
        int value = 0;
        if (selectedTrack != null)
        {
            final ISlotBank slotBank = selectedTrack.getSlotBank ();
            value = (slotBank.canScrollForwards () ? 1 : 0) + (slotBank.canScrollBackwards () ? 2 : 0);
        }
        surface.updateButton (KontrolMkIIControlSurface.KONTROL_NAVIGATE_CLIPS, value);

        final ISceneBank sceneBank = trackBank.getSceneBank ();
        surface.updateButton (KontrolMkIIControlSurface.KONTROL_NAVIGATE_SCENES, (sceneBank.canScrollForwards () ? 1 : 0) + (sceneBank.canScrollBackwards () ? 2 : 0));
    }


    /**
     * Get the name of an Komplete Kontrol instance on the current track, or an empty string
     * otherwise. A track contains a Komplete Kontrol instance if: There is an instance of a plugin
     * whose name starts with Komplete Kontrol and the first parameter label exposed by the plugin
     * is NIKBxx, where xx is a number between 00 and 99 If the conditions are satisfied.
     *
     * @return The instance name, which is the actual label of the first parameter (e.g. NIKB01). An
     *         empty string if none is present
     */
    private String getKompleteInstance ()
    {
        final ICursorDevice instrumentDevice = this.model.getInstrumentDevice ();
        if (instrumentDevice.doesExist () && instrumentDevice.getName ().startsWith ("Komplete Kontrol"))
            return instrumentDevice.getParameterBank ().getItem (0).getName ();
        return "";
    }


    private void changeTransportPosition (final int value)
    {
        this.model.getTransport ().changePosition (value <= 63);
    }


    private void changeLoopPosition (final int value)
    {
        // Changing of loop position is not possible. Therefore, change position fine grained
        this.model.getTransport ().changePosition (value <= 63, true);
    }
}
