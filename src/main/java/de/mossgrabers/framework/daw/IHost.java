// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw;

import de.mossgrabers.framework.graphics.IBitmap;
import de.mossgrabers.framework.graphics.IImage;
import de.mossgrabers.framework.osc.IOpenSoundControlCallback;
import de.mossgrabers.framework.osc.IOpenSoundControlMessage;
import de.mossgrabers.framework.osc.IOpenSoundControlServer;
import de.mossgrabers.framework.usb.IUsbDevice;
import de.mossgrabers.framework.usb.UsbException;

import java.util.List;


/**
 * Interface to the Host.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IHost
{
    /**
     * Get the hosts name.
     *
     * @return The name
     */
    String getName ();


    /**
     * Returns true if the DAW supports a clip based view.
     *
     * @return True if the DAW supports a clip based view
     */
    boolean hasClips ();


    /**
     * Returns true if the DAW supports track/device pinning.
     *
     * @return True if the DAW supports track/device pinning
     */
    boolean hasPinning ();


    /**
     * Returns true if the DAW supports a crossfader.
     *
     * @return True if the DAW supports a crossfader
     */
    boolean hasCrossfader ();


    /**
     * Returns true if the DAW supports Drum Device options.
     *
     * @return True if the DAW supports Drum Device options
     */
    boolean hasDrumDevice ();


    /**
     * Returns true if the DAW supports note repeat.
     *
     * @return True if the DAW supports note repeat
     */
    boolean hasRepeat ();


    /**
     * Returns true if the DAW supports editing markers.
     *
     * @return True if the DAW supports editing markerst
     */
    boolean canEditMarkers ();


    /**
     * Schedules the given task for execution after the given delay.
     *
     * @param task The task to execute
     * @param delay The duration after which the callback function will be called in milliseconds
     */
    void scheduleTask (Runnable task, long delay);


    /**
     * Print the error to the console.
     *
     * @param text The description text
     */
    void error (String text);


    /**
     * Print the exception to the console.
     *
     * @param text The description text
     * @param ex The exception
     */
    void error (String text, Throwable ex);


    /**
     * Print a text to the console.
     *
     * @param text The text to print
     */
    void println (String text);


    /**
     * Display a notification in the DAW.
     *
     * @param message The message to display
     */
    void showNotification (String message);


    /**
     * Connect to an OSC server.
     *
     * @param serverAddress The address of the server
     * @param serverPort The port of the server
     * @return Interface for interacting with the server
     */
    IOpenSoundControlServer connectToOSCServer (String serverAddress, int serverPort);


    /**
     * Create an OSC server.
     *
     * @param callback The callback method to handle received messages
     * @param port The port to listen on
     */
    void createOSCServer (IOpenSoundControlCallback callback, int port);


    /**
     * Create an OSC message.
     *
     * @param address The OSC address
     * @param values The values for the message
     * @return The created message
     */
    IOpenSoundControlMessage createOSCMessage (String address, List<Object> values);


    /**
     * Call on shutdown to release all OSC resources.
     */
    void releaseOSC ();


    /**
     * Loads a SVG image. The memory used by this image is guaranteed to be freed once this
     * extension exits.
     *
     * @param imageName The path to the image
     * @param scale The scaling factor
     * @return The loaded SVG image
     */
    IImage loadSVG (String imageName, int scale);


    /**
     * Creates an offscreen bitmap that the extension can use to render into. The memory used by
     * this bitmap is guaranteed to be freed once this extension exits.
     *
     * @param width The width of the bitmap
     * @param height The height of the bitmap
     * @return The created bitmap
     */
    IBitmap createBitmap (int width, int height);


    /**
     * Allocates some memory that will be automatically freed once the extension exits.
     *
     * @param size The size of the memory block in bytes
     * @return The created memory block
     */
    IMemoryBlock createMemoryBlock (int size);


    /**
     * Gets the USB Device at the specified index.
     *
     * @param index The index
     * @return The USB device
     * @throws UsbException Could not lookup or open the device
     */
    IUsbDevice getUsbDevice (int index) throws UsbException;


    /**
     * Call on shutdown to release all USB devices.
     */
    void releaseUsbDevices ();
}