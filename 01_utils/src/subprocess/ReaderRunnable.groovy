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
 * Helper class to read a child process' stdout and stderr as text data
 *
 * 2011.08.25 - Created to check for performance problems.
 * 2011.11.03 - Reused for M-PLIFY msgserver code
 ******************************************************************************/

class ReaderRunnable implements Runnable {

    private final static String CLASS = ReaderRunnable.class.getName()
    private final static Logger LOGGER_run = LoggerFactory.getLogger(CLASS + '.run')

    private final static int READ_BUFFER_SIZE = 2048 // internal buffer size

    private final Reader reader // where to read from
    private final String name // just for naming of threads
    private final maxChars; // how many characters to read maximally

    private int charsLost = 0 // counts the chars dropped on the floor because more read than "maxChars"
    private String data // will contain the recuperated string

    /**
     * Pass a reader and a non-null name used in naming threads.
     */

    public ReaderRunnable(Reader reader, String name, int maxChars) {
        _check.notNull(reader, 'reader')
        _check.notNull(name, 'name')
        _check.largerThanZero(maxChars,'max chars')
        this.reader = reader
        this.name = name
        this.maxChars = maxChars
    }

    /**
     * The runnable copies characters from the "reader" sitting on top of the "input stream" passed in the
     * constructor, reads blocks of characters and buffers them internally. 
     */

    public void run() {
        Logger logger = LOGGER_run
        _check.isTrue(data==null, "The 'data' is not (null) - this instance has already been used once")
        char[] readbuf = new char[READ_BUFFER_SIZE]
        int readInTotal  = 0
        int actuallyRead = 0
        StringBuilder buf = new StringBuilder()
        while ((actuallyRead = reader.read(readbuf)) >= 0) {
            readInTotal += actuallyRead
            if (actuallyRead == 0) {
                // can it really read 0?
                logger.info 'Actually read 0 characters!'
            }
            else {
                if (logger.isDebugEnabled()) {
                    logger.debug "Read ${actuallyRead} additional characters"
                }
                int mayBeAdded = Math.min(actuallyRead,maxChars - buf.size())
                int addCharsLost = (actuallyRead-mayBeAdded)
                if (addCharsLost > 0) {
                    charsLost += addCharsLost
                    logger.warn "Overflow (Now at ${buf.size()} bytes; max is ${maxChars})! Dropping ${addCharsLost} bytes."
                }
                if (mayBeAdded>0) {
                    buf.append(readbuf,0,mayBeAdded)
                }
            }
        }
        //
        // Extract the String and that's it
        //
        data = buf.toString()
        if (logger.isInfoEnabled()) {
            logger.info "Done reading; read ${readInTotal} characters; buffered ${buf.size()} characters"
        }

    }

    /**
     * Access the resulting String
     */

    public String getData() {
        return data
    }

    /**
     * Check how many chars were lost
     */

    public int getCharsLost() {
        return charsLost
    }

    /**
     * "Transfer" reads the recuperated String into a sequence-of-lines using a LineNumberReader.
     * As this is a LineNumberReader, a final empty line will be lost!
     * Maybe there is a simpler way in groovy?
     */

    public List transfer() {
        PipedWriter w = new PipedWriter() // reads from "seqbuf"
        PipedReader r = new PipedReader() // a thread reads lines from this
        w.connect(r)
        // what we will return
        List result = new LinkedList()
        // start the "pipe reading thread" which reads lines from "r"
        Thread t = Thread.startDaemon("pipe reader for ${name}", {
            LineNumberReader lnr = new LineNumberReader(r)
            String line
            while ((line=lnr.readLine())!=null) {
                result << line
            }
            r.close()
        })
        // write the seqbuf to "w", which is connected to "r", then flush and close
        w.write(data)
        w.flush()
        w.close()
        // wait till all is read
        t.join()
        // and done!
        return result
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