package subprocess;

import java.io.InputStream
import java.io.Reader
import java.util.List
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import com.mplify.checkers.Check

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2011, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Helper class to read a child process' stdout and stderr as binary data
 *
 * 2011.11.04 - Created to hoover up stdin in binary
 ******************************************************************************/

class InputStreamRunnable implements Runnable {

    private final static String CLASS = InputStreamRunnable.class.getName()
    private final static Logger LOGGER_run = LoggerFactory.getLogger(CLASS + '.run')

    private final static int READ_BUFFER_SIZE = 2048 // internal buffer size

    private final InputStream is // where to read from
    private final String name // just used for naming of threads
    private final maxBytes // how many bytes to read maximally, must be > 0; use Integer.MAX_INT if necessary

    private int bytesLost = 0 // counts the bytes dropped on the floor because more read than "maxBytes"
    private byte[] data // will contain the recuperated bytes

    /**
     * Pass an inputstream and a non-null name used in naming threads.
     */

    public InputStreamRunnable(InputStream is, String name, int maxBytes) {
        Check.notNull(is, 'input stream')
        Check.notNull(name, 'name')
        Check.largerThanZero(maxBytes,'max bytes')
        this.is = is
        this.name = name
        this.maxBytes = maxBytes
    }

    /**
     * The runnable copies bytes from the the "input stream" passed in the
     * constructor, reads blocks of bytes and buffers them internally. 
     */

    public void run() {
        Logger logger = LOGGER_run
        Check.isTrue(data==null, "The 'data' array is not (null) - this instance has already been used once")
        byte[] readbuf = new byte[READ_BUFFER_SIZE]
        int readInTotal   = 0
        int actuallyRead  = 0
        ByteArrayOutputStream buf = new ByteArrayOutputStream()
        while ((actuallyRead = is.read(readbuf)) >= 0) {
            readInTotal += actuallyRead
            if (actuallyRead == 0) {
                // can it really read 0?
                logger.info 'Actually read 0 bytes!'
            }
            else {
                if (logger.isDebugEnabled()) {
                    logger.debug "Read ${actuallyRead} additional bytes"
                }
                int mayBeAdded = Math.min(actuallyRead,maxBytes - buf.size())
                int addBytesLost = (actuallyRead-mayBeAdded)
                if (addBytesLost > 0) {
                    bytesLost += addBytesLost
                    logger.warn "Overflow (Now at ${buf.size()} bytes; max is ${maxBytes})! Dropping ${addBytesLost} bytes."
                }
                if (mayBeAdded>0) {
                    buf.write(readbuf,0,mayBeAdded)
                }
            }
        }
        //
        // Extract the byte array and that's it
        //
        data = buf.toByteArray()
        if (logger.isInfoEnabled()) {
            logger.info "Done reading; read ${readInTotal} byte; kept ${data.length} byte; dropped ${bytesLost} byte."
        }
    }

    /**
     * Access the byte buffer
     */

    public byte[] getData() {
        return data
    }

    /**
     * Check how many byte were lost
     */

    public int getBytesLost() {
        return bytesLost
    }

    /**
     * Run this! Returns a started daemon thread running this "runnable"
     */

    public Thread startThisThread() {
        Thread t = new Thread(this, name)
        t.setDaemon(true)
        t.start()
        return t;
    }

}