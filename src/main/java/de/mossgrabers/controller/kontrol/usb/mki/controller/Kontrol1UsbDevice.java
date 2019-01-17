// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.usb.mki.controller;

import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IMemoryBlock;
import de.mossgrabers.framework.usb.IHidDevice;
import de.mossgrabers.framework.usb.IUsbDevice;
import de.mossgrabers.framework.usb.UsbException;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * USB connection for display and UI controls of Kontrol 1.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Kontrol1UsbDevice
{
    private static final byte [] []            UPPER_CHARACTERS = new byte [] []
    {
        {
            (byte) 207,
            24
        },                                                                                // A
        {
            (byte) 63,
            82
        },                                                                                // B
        {
            (byte) 243,
            0
        },                                                                                // C
        {
            (byte) 63,
            66
        },                                                                                // D
        {
            (byte) 243,
            24
        },                                                                                // E
        {
            (byte) 195,
            24
        },                                                                                // F
        {
            (byte) 251,
            16
        },                                                                                // G
        {
            (byte) 204,
            24
        },                                                                                // H
        {
            (byte) 51,
            66
        },                                                                                // I
        {
            (byte) 31,
            0
        },                                                                                // J
        {
            (byte) 192,
            (byte) 140
        },                                                                                // K
        {
            (byte) 240,
            0
        },                                                                                // L
        {
            (byte) 204,
            5
        },                                                                                // M
        {
            (byte) 204,
            (byte) 129
        },                                                                                // N
        {
            (byte) 255,
            0
        },                                                                                // O
        {
            (byte) 199,
            24
        },                                                                                // P
        {
            (byte) 255,
            (byte) 128
        },                                                                                // Q
        {
            (byte) 199,
            (byte) 152
        },                                                                                // R
        {
            (byte) 187,
            24
        },                                                                                // S
        {
            (byte) 3,
            66
        },                                                                                // T
        {
            (byte) 252,
            0
        },                                                                                // U
        {
            (byte) 192,
            36
        },                                                                                // V
        {
            (byte) 204,
            (byte) 160
        },                                                                                // W
        {
            (byte) 0,
            (byte) 165
        },                                                                                // X
        {
            (byte) 0,
            69
        },                                                                                // Y
        {
            (byte) 51,
            36
        }                                                                                 // Z
    };

    private static final byte [] []            LOWER_CHARACTERS = new byte [] []
    {
        {
            (byte) 207,
            24
        },                                                                                // A
        {
            (byte) 248,
            24
        },                                                                                // b
        {
            (byte) 112,
            24
        },                                                                                // c
        {
            (byte) 124,
            24
        },                                                                                // d
        {
            (byte) 243,
            24
        },                                                                                // E
        {
            (byte) 193,
            8
        },                                                                                // f
        {
            (byte) 251,
            16
        },                                                                                // G
        {
            (byte) 200,
            24
        },                                                                                // h
        {
            0,
            64
        },                                                                                // i
        {
            (byte) 31,
            0
        },                                                                                // J
        {
            (byte) 192,
            (byte) 140
        },                                                                                // K
        {
            0,
            66
        },                                                                                // l
        {
            72,
            88
        },                                                                                // m
        {
            64,
            72
        },                                                                                // n
        {
            (byte) 120,
            24
        },                                                                                // o
        {
            (byte) 199,
            24
        },                                                                                // P
        {
            (byte) 255,
            (byte) 128
        },                                                                                // Q
        {
            (byte) 199,
            (byte) 152
        },                                                                                // R
        {
            (byte) 187,
            24
        },                                                                                // S
        {
            (byte) 224,
            8
        },                                                                                // t
        {
            (byte) 120,
            0
        },                                                                                // u
        {
            64,
            32
        },                                                                                // v
        {
            (byte) 120,
            64
        },                                                                                // w
        {
            (byte) 0,
            (byte) 165
        },                                                                                // X
        {
            (byte) 0,
            69
        },                                                                                // Y
        {
            (byte) 51,
            36
        }                                                                                 // Z
    };

    private static final byte [] []            NUMBERS          = new byte [] []
    {
        {
            (byte) 255,
            0
        },
        {
            (byte) 12,
            0
        },
        {
            (byte) 119,
            24
        },
        {
            (byte) 63,
            24
        },
        {
            (byte) 140,
            24
        },
        {
            (byte) 187,
            24
        },
        {
            (byte) 251,
            24
        },
        {
            (byte) 15,
            0
        },
        {
            (byte) 255,
            24
        },
        {
            (byte) 191,
            24
        }
    };

    private static final byte []               MINUS            = new byte []
    {
        0,
        (byte) 24
    };

    private static final byte []               PLUS             = new byte []
    {
        0,
        (byte) 90
    };

    private static final byte []               PERCENT          = new byte []
    {
        (byte) 153,
        (byte) 126
    };

    private static final byte []               GREATER          = new byte []
    {
        0,
        (byte) 33
    };

    private static final byte []               APOSTROPH        = new byte []
    {
        (byte) 128,
        0
    };

    private static final byte []               FWD_SLASH        = new byte []
    {
        0,
        (byte) 36
    };

    private static final byte []               BWD_SLASH        = new byte []
    {
        0,
        (byte) 129
    };

    private static final int []                BYTE_0           =
    {
        Kontrol1ControlSurface.BUTTON_MAIN_ENCODER,
        Kontrol1ControlSurface.BUTTON_PRESET_UP,
        Kontrol1ControlSurface.BUTTON_ENTER,
        Kontrol1ControlSurface.BUTTON_PRESET_DOWN,
        Kontrol1ControlSurface.BUTTON_BROWSE,
        Kontrol1ControlSurface.BUTTON_INSTANCE,
        Kontrol1ControlSurface.BUTTON_OCTAVE_DOWN,
        Kontrol1ControlSurface.BUTTON_OCTAVE_UP
    };

    private static final int []                BYTE_1           =
    {
        Kontrol1ControlSurface.BUTTON_STOP,
        Kontrol1ControlSurface.BUTTON_REC,
        Kontrol1ControlSurface.BUTTON_PLAY,
        Kontrol1ControlSurface.BUTTON_NAVIGATE_RIGHT,
        Kontrol1ControlSurface.BUTTON_NAVIGATE_DOWN,
        Kontrol1ControlSurface.BUTTON_NAVIGATE_LEFT,
        Kontrol1ControlSurface.BUTTON_BACK,
        Kontrol1ControlSurface.BUTTON_NAVIGATE_UP
    };

    private static final int []                BYTE_2           =
    {
        Kontrol1ControlSurface.BUTTON_SHIFT,
        Kontrol1ControlSurface.BUTTON_SCALE,
        Kontrol1ControlSurface.BUTTON_ARP,
        Kontrol1ControlSurface.BUTTON_LOOP,
        Kontrol1ControlSurface.BUTTON_PAGE_RIGHT,
        Kontrol1ControlSurface.BUTTON_PAGE_LEFT,
        Kontrol1ControlSurface.BUTTON_RWD,
        Kontrol1ControlSurface.BUTTON_FWD
    };

    private static final int []                BYTE_3           =
    {
        Kontrol1ControlSurface.TOUCH_ENCODER_1,
        Kontrol1ControlSurface.TOUCH_ENCODER_2,
        Kontrol1ControlSurface.TOUCH_ENCODER_3,
        Kontrol1ControlSurface.TOUCH_ENCODER_4,
        Kontrol1ControlSurface.TOUCH_ENCODER_5,
        Kontrol1ControlSurface.TOUCH_ENCODER_6,
        Kontrol1ControlSurface.TOUCH_ENCODER_7,
        Kontrol1ControlSurface.TOUCH_ENCODER_8
    };

    private static final int []                BYTE_4           =
    {
        Kontrol1ControlSurface.TOUCH_ENCODER_MAIN
    };

    private static final int []                TEST_BITS        =
    {
        0x01,
        0x02,
        0x04,
        0x08,
        0x10,
        0x20,
        0x40,
        0x80
    };

    private static final int []                KEY_SIZES        = new int []
    {
        25,
        49,
        61,
        88
    };

    private static final int                   REPORT_ID_UI     = 1;

    private static final int                   SIZE_DISPLAY     = 248;
    private static final int                   SIZE_BUTTON_LEDS = 25;

    private final int                          sizeKeyLEDs;
    private final IMemoryBlock                 displayBlock;
    private final IMemoryBlock                 ledBlock;
    private final IMemoryBlock                 keyLedBlock;
    private final IMemoryBlock                 initBlock;

    private static final Map<Integer, Integer> LED_MAPPING      = new HashMap<> (21);

    private final IHost                        host;
    private IUsbDevice                         usbDevice;
    private IHidDevice                         hidDevice;

    private UIChangeCallback                   callback;

    private int                                mainEncoderValue;
    private int []                             encoderValues    = new int [8];

    private byte []                            buttonStates     = new byte [21];
    private byte []                            oldButtonStates  = new byte [21];

    private byte []                            keyColors        = new byte [88 * 3];
    private byte []                            oldKeyColors     = new byte [88 * 3];

    private final boolean [] []                dots             = new boolean [2] [72];
    private final int [] []                    bars             = new int [9] [9];
    private final char [] []                   texts            = new char [2] [72];
    private final char [] []                   oldTexts         = new char [2] [72];

    private final byte []                      line0            = new byte [SIZE_DISPLAY];

    private boolean                            isFirstStateMsg  = true;

    static
    {
        LED_MAPPING.put (Integer.valueOf (Kontrol1ControlSurface.BUTTON_SHIFT), Integer.valueOf (0));
        LED_MAPPING.put (Integer.valueOf (Kontrol1ControlSurface.BUTTON_SCALE), Integer.valueOf (1));
        LED_MAPPING.put (Integer.valueOf (Kontrol1ControlSurface.BUTTON_ARP), Integer.valueOf (2));
        LED_MAPPING.put (Integer.valueOf (Kontrol1ControlSurface.BUTTON_LOOP), Integer.valueOf (3));
        LED_MAPPING.put (Integer.valueOf (Kontrol1ControlSurface.BUTTON_RWD), Integer.valueOf (4));
        LED_MAPPING.put (Integer.valueOf (Kontrol1ControlSurface.BUTTON_FWD), Integer.valueOf (5));
        LED_MAPPING.put (Integer.valueOf (Kontrol1ControlSurface.BUTTON_PLAY), Integer.valueOf (6));
        LED_MAPPING.put (Integer.valueOf (Kontrol1ControlSurface.BUTTON_REC), Integer.valueOf (7));
        LED_MAPPING.put (Integer.valueOf (Kontrol1ControlSurface.BUTTON_STOP), Integer.valueOf (8));
        LED_MAPPING.put (Integer.valueOf (Kontrol1ControlSurface.BUTTON_PAGE_LEFT), Integer.valueOf (9));
        LED_MAPPING.put (Integer.valueOf (Kontrol1ControlSurface.BUTTON_PAGE_RIGHT), Integer.valueOf (10));
        LED_MAPPING.put (Integer.valueOf (Kontrol1ControlSurface.BUTTON_BROWSE), Integer.valueOf (11));
        LED_MAPPING.put (Integer.valueOf (Kontrol1ControlSurface.BUTTON_PRESET_UP), Integer.valueOf (12));
        LED_MAPPING.put (Integer.valueOf (Kontrol1ControlSurface.BUTTON_INSTANCE), Integer.valueOf (13));
        LED_MAPPING.put (Integer.valueOf (Kontrol1ControlSurface.BUTTON_PRESET_DOWN), Integer.valueOf (14));
        LED_MAPPING.put (Integer.valueOf (Kontrol1ControlSurface.BUTTON_BACK), Integer.valueOf (15));
        LED_MAPPING.put (Integer.valueOf (Kontrol1ControlSurface.BUTTON_NAVIGATE_UP), Integer.valueOf (16));
        LED_MAPPING.put (Integer.valueOf (Kontrol1ControlSurface.BUTTON_ENTER), Integer.valueOf (17));
        LED_MAPPING.put (Integer.valueOf (Kontrol1ControlSurface.BUTTON_NAVIGATE_LEFT), Integer.valueOf (18));
        LED_MAPPING.put (Integer.valueOf (Kontrol1ControlSurface.BUTTON_NAVIGATE_DOWN), Integer.valueOf (19));
        LED_MAPPING.put (Integer.valueOf (Kontrol1ControlSurface.BUTTON_NAVIGATE_RIGHT), Integer.valueOf (20));
    }


    /**
     * Constructor.
     *
     * @param modelIndex The index of the model (S25, S49, S61, S88)
     * @param host The controller host
     */
    public Kontrol1UsbDevice (final int modelIndex, final IHost host)
    {
        this.host = host;

        try
        {
            this.usbDevice = host.getUsbDevice (0);
            this.hidDevice = this.usbDevice.getHidDevice ();
            if (this.hidDevice != null)
                this.hidDevice.setCallback (this::processHIDMessage);
        }
        catch (final UsbException ex)
        {
            this.usbDevice = null;
            this.hidDevice = null;
            host.error ("Could not open USB connection: " + ex.getMessage ());
        }

        this.sizeKeyLEDs = KEY_SIZES[modelIndex] * 3;

        this.displayBlock = host.createMemoryBlock (SIZE_DISPLAY);
        this.ledBlock = host.createMemoryBlock (SIZE_BUTTON_LEDS);
        this.keyLedBlock = host.createMemoryBlock (this.sizeKeyLEDs);
        this.initBlock = host.createMemoryBlock (2);

        // To send black LEDs on startup
        this.oldKeyColors[0] = -1;
    }


    /**
     * Callback function for device control changes.
     *
     * @param callback The callback
     */
    public void setCallback (final UIChangeCallback callback)
    {
        this.callback = callback;
    }


    /**
     * Send the initialization message to the device.
     */
    public void init ()
    {
        if (this.hidDevice == null)
            return;

        synchronized (this.initBlock)
        {
            final ByteBuffer buffer = this.initBlock.createByteBuffer ();
            padBuffer (buffer);
            this.hidDevice.sendOutputReport ((byte) 0xA0, this.initBlock);
        }
    }


    /**
     * Set a character.
     *
     * @param row The row (0-1)
     * @param index The column index (0-71)
     * @param character The character to set
     */
    public void setCharacter (final int row, final int index, final char character)
    {
        if (row < 0 || row > 1 || index < 0 || index > 71)
            return;
        this.texts[row][index] = character;
    }


    /**
     * Set the dots.
     *
     * @param row The row (0-1)
     * @param index The column index (0-71)
     * @param set Set or clear
     */
    public void setDot (final int row, final int index, final boolean set)
    {
        if (row < 0 || row > 1 || index < 0 || index > 71)
            return;
        this.dots[row][index] = set;
    }


    /**
     * Set a value bar.
     *
     * @param column The column (0-8)
     * @param hasBorder True to draw a border around the value bar
     * @param value The value to set the bar to
     * @param maxValue The maximum possible value
     */
    public void setBar (final int column, final boolean hasBorder, final int value, final int maxValue)
    {
        final int v = value * 36 / maxValue;
        final int full = v / 4;

        for (int i = 0; i < 9; i++)
        {
            this.bars[column][i] = i < full ? 3 : 0;
            if (hasBorder)
                this.bars[column][i] += 68;
        }

        if (full < 9)
        {
            int dashes = v % 4;
            // Strangely, 1 dash 2 but 2 dashes are 1...
            if (dashes == 1)
                dashes = 2;
            else if (dashes == 2)
                dashes = 1;
            this.bars[column][full] = dashes;
            if (hasBorder)
                this.bars[column][full] += 68;
        }
    }


    /**
     * Set a value bar drawn as panorama.
     *
     * @param column The column (0-8)
     * @param hasBorder True to draw a border around the value bar
     * @param value The value to set the bar to
     * @param maxValue The maximum possible value
     */
    public void setPanBar (final int column, final boolean hasBorder, final int value, final int maxValue)
    {
        for (int i = 0; i < 9; i++)
            this.bars[column][i] = i == 4 ? 3 : 0;

        final int middle = maxValue / 2;
        if (value != middle)
        {
            final boolean isLeft = value < middle;
            final int pos = isLeft ? middle - value : value - middle;
            final int noOfBars = 16 * pos / maxValue;
            final int half = noOfBars / 2;
            final int rest = noOfBars % 2;

            if (isLeft)
            {
                for (int i = 4 - half; i <= 4; i++)
                    this.bars[column][i] = 3;
                if (rest > 0 && 4 - half - 1 >= 0)
                    this.bars[column][4 - half - 1] = 2;
            }
            else
            {
                for (int i = 0; i <= half; i++)
                    this.bars[column][5 + i] = 3;
                if (rest > 0 && 5 + half + 1 <= 8)
                    this.bars[column][5 + half + 1] = 2;
            }
        }

        if (!hasBorder)
            return;
        for (int i = 0; i < 9; i++)
            this.bars[column][i] += 68;
    }


    /**
     * Send all display data to the device.
     */
    public void sendDisplayData ()
    {
        if (this.hidDevice == null)
            return;

        synchronized (this.displayBlock)
        {
            final ByteBuffer displayBuffer = this.displayBlock.createByteBuffer ();
            fillHeader (displayBuffer, 0);
            for (int j = 0; j < 72; j++)
            {
                final int col = j / 8;
                displayBuffer.put ((byte) this.bars[col][j - col * 8]);

                if (j % 8 == 7)
                    displayBuffer.put ((byte) this.bars[col][8]);
                else
                {
                    if (this.dots[0][j] && this.dots[1][j])
                        displayBuffer.put ((byte) 255);
                    else if (this.dots[0][j])
                        displayBuffer.put ((byte) 253);
                    else if (this.dots[1][j])
                        displayBuffer.put ((byte) 254);
                    else
                        displayBuffer.put ((byte) 0);
                }
            }
            padBuffer (displayBuffer);

            final byte [] l0 = new byte [SIZE_DISPLAY];
            displayBuffer.rewind ();
            displayBuffer.get (l0);

            if (!Arrays.equals (this.line0, l0))
            {
                System.arraycopy (l0, 0, this.line0, 0, this.line0.length);
                this.hidDevice.sendOutputReport ((byte) 0xE0, this.displayBlock);
            }

            for (int row = 1; row < 3; row++)
            {
                if (!Arrays.equals (this.oldTexts[row - 1], this.texts[row - 1]))
                {
                    System.arraycopy (this.texts[row - 1], 0, this.oldTexts[row - 1], 0, this.oldTexts[row - 1].length);

                    fillHeader (displayBuffer, row);
                    for (int j = 0; j < 72; j++)
                        displayBuffer.put (this.getCharacter (row - 1, j));

                    padBuffer (displayBuffer);
                    this.hidDevice.sendOutputReport ((byte) 0xE0, this.displayBlock);
                }
            }
        }
    }


    /**
     * Stop sending USB data.
     */
    public void shutdown ()
    {
        this.hidDevice = null;
    }


    /**
     * Set a button LED.
     *
     * @param buttonID The ID of the button
     * @param intensity 0-255 The light intensity (0 is off)
     */
    public void setButtonLED (final int buttonID, final int intensity)
    {
        final Integer pos = LED_MAPPING.get (Integer.valueOf (buttonID));
        if (pos == null)
        {
            this.host.error ("Illegal button LED: " + buttonID);
            return;
        }

        this.buttonStates[pos.intValue ()] = (byte) intensity;
    }


    /**
     * Send the LED status updates to the device.
     */
    public void updateButtonLEDs ()
    {
        if (this.hidDevice == null)
            return;

        synchronized (this.ledBlock)
        {
            if (Arrays.equals (this.oldButtonStates, this.buttonStates))
                return;
            System.arraycopy (this.buttonStates, 0, this.oldButtonStates, 0, this.oldButtonStates.length);

            final ByteBuffer ledBuffer = this.ledBlock.createByteBuffer ();
            ledBuffer.clear ();
            ledBuffer.put (this.buttonStates);
            padBuffer (ledBuffer);
            this.hidDevice.sendOutputReport ((byte) 0x80, this.ledBlock);
        }
    }


    /**
     * Set a key LED. Note that key 0 is always the first key of the Sxx, which means they are
     * different for the different models. Furthermore, this is independent from the octave
     * transposition.
     *
     * @param key The index of the key 0-87
     * @param red The red value 0-255
     * @param green The green value 0-255
     * @param blue The blue value 0-255
     */
    public void setKeyLED (final int key, final int red, final int green, final int blue)
    {
        if (key < 0 || key >= 88)
            return;
        final int pos = 3 * key;
        this.keyColors[pos] = (byte) red;
        this.keyColors[pos + 1] = (byte) green;
        this.keyColors[pos + 2] = (byte) blue;
    }


    /**
     * Send the key LED stati updates to the device.
     */
    public void updateKeyLEDs ()
    {
        if (this.hidDevice == null)
            return;

        synchronized (this.keyLedBlock)
        {
            if (Arrays.equals (this.oldKeyColors, this.keyColors))
                return;
            System.arraycopy (this.keyColors, 0, this.oldKeyColors, 0, this.oldKeyColors.length);
            final ByteBuffer keyLedBuffer = this.keyLedBlock.createByteBuffer ();
            keyLedBuffer.clear ();
            keyLedBuffer.put (this.keyColors, 0, this.sizeKeyLEDs);
            padBuffer (keyLedBuffer);
            this.hidDevice.sendOutputReport ((byte) 0x82, this.keyLedBlock);
        }
    }


    /**
     * Fill the display buffer with the header data
     *
     * @param displayBuffer The display buffer to which to add the header
     * @param row The row number (0-3)
     */
    private static void fillHeader (final ByteBuffer displayBuffer, final int row)
    {
        displayBuffer.clear ();
        displayBuffer.put ((byte) 0x00);
        displayBuffer.put ((byte) 0x00);
        displayBuffer.put ((byte) row);
        displayBuffer.put ((byte) 0x00);
        displayBuffer.put ((byte) 0x48);
        displayBuffer.put ((byte) 0x00);
        displayBuffer.put ((byte) 0x01);
        displayBuffer.put ((byte) 0x00);
    }


    /**
     * Process the received HID message.
     *
     * @param reportID The report (= function/method) number
     * @param data The data
     * @param received The number of valid bytes in the data array
     */
    private void processHIDMessage (final byte reportID, final byte [] data, final int received)
    {
        if (reportID != REPORT_ID_UI)
            return;

        boolean encoderChange = false;

        // Decode main knob
        final int currentEncoderValue = Byte.toUnsignedInt (data[5]);
        if (currentEncoderValue != this.mainEncoderValue)
        {
            final boolean valueIncreased = (this.mainEncoderValue < currentEncoderValue || this.mainEncoderValue == 0x0F && currentEncoderValue == 0) && !(this.mainEncoderValue == 0 && currentEncoderValue == 0x0F);
            this.mainEncoderValue = currentEncoderValue;
            if (!this.isFirstStateMsg)
                this.callback.mainEncoderChanged (valueIncreased);
            encoderChange = true;
        }

        // Decode 8 value knobs
        final int start = 6;
        for (int encIndex = 0; encIndex < 8; encIndex++)
        {
            final int pos = start + 2 * encIndex;

            final int value = Byte.toUnsignedInt (data[pos]) | Byte.toUnsignedInt (data[pos + 1]) << 8;
            final int hValue = Byte.toUnsignedInt (data[pos + 1]);
            if (this.encoderValues[encIndex] != value)
            {
                final int prevHValue = (this.encoderValues[encIndex] & 0xF00) >> 8;
                final boolean valueIncreased = (this.encoderValues[encIndex] < value || prevHValue == 3 && hValue == 0) && !(prevHValue == 0 && hValue == 3);
                this.encoderValues[encIndex] = value;
                if (!this.isFirstStateMsg)
                    this.callback.encoderChanged (encIndex, valueIncreased);
                encoderChange = true;
            }
        }

        this.isFirstStateMsg = false;

        // Test the pressed buttons
        this.testByteForButtons (data[0], BYTE_0);
        this.testByteForButtons (data[1], BYTE_1);
        this.testByteForButtons (data[2], BYTE_2);
        // Don't test touch events on encoder change to prevent flickering
        if (!encoderChange)
        {
            this.testByteForButtons (data[3], BYTE_3);
            this.testByteForButtons (data[4], BYTE_4);
        }
    }


    private byte [] getCharacter (final int row, final int index)
    {
        final char c = this.texts[row][index];

        if (c >= 65 && c <= 90)
            return UPPER_CHARACTERS[c - 65];

        if (c >= 97 && c <= 122)
            return LOWER_CHARACTERS[c - 97];

        if (c >= 48 && c <= 57)
            return NUMBERS[c - 48];

        switch (c)
        {
            case '-':
                return MINUS;
            case '+':
                return PLUS;
            case '%':
                return PERCENT;
            case '>':
                return GREATER;
            case '\'':
                return APOSTROPH;
            case '/':
                return FWD_SLASH;
            case '\\':
                return BWD_SLASH;

            default:
                return new byte []
                {
                    0,
                    0
                };
        }
    }


    private void testByteForButtons (final byte b, final int [] buttons)
    {
        if (this.callback == null)
            return;

        final int t = Byte.toUnsignedInt (b);
        for (int i = 0; i < buttons.length; i++)
            this.callback.buttonChange (buttons[i], (t & TEST_BITS[i]) > 0);
    }


    /**
     * Turn off all button LEDs.
     */
    public void turnOffButtonLEDs ()
    {
        for (final Integer buttonLED: LED_MAPPING.values ())
            this.buttonStates[buttonLED.intValue ()] = 0;
        this.updateButtonLEDs ();
    }


    private static void padBuffer (final ByteBuffer buffer)
    {
        while (buffer.position () < buffer.capacity ())
            buffer.put ((byte) 0x00);
    }
}
