package subprocess;

import java.io.File
import java.util.List

import com.mplify.checkers._check

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2011, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Run a child process
 *
 * 2011.08.25 - Created to check for performance problems.
 * 2011.11.03 - Reused for M-PLIFY msgserver code
 ******************************************************************************/

class RunChildProcess {

    /**
     * Cannot instantiate
     */

    private RunChildProcess() {
        // Unreachable
    }

    /**
     * Response class
     */

    static class BinaryOut {

        final byte[] stdoutBytes
        final int    stdoutBytesLost
        final List<String>   stderrLines
        final int    stderrCharsLost
        final int    exitValue

        public BinaryOut(int exitValue, byte[] stdoutBytes, int stdoutBytesLost, List<String> stderrLines, int stderrCharsLost) {
            this.stdoutBytes     = stdoutBytes
            this.stdoutBytesLost = stdoutBytesLost
            this.stderrLines     = stderrLines
            this.stderrCharsLost = stderrCharsLost
            this.exitValue       = exitValue
        }
    }


    /**
     * Response class
     */

    static class LinesOut {

        final List<String>   stdoutLines
        final int    stdoutCharsLost
        final List<String>   stderrLines
        final int    stderrCharsLost
        final int    exitValue

        public LinesOut(int exitValue, List<String> stdoutLines, int stdoutCharsLost, List<String> stderrLines, int stderrCharsLost) {
            this.stdoutLines     = stdoutLines
            this.stdoutCharsLost = stdoutCharsLost
            this.stderrLines     = stderrLines
            this.stderrCharsLost = stderrCharsLost
            this.exitValue       = exitValue
        }
    }

    /**
     * Run a command, slurping stdout and stderr.
     * Stderr is read as UTF-8 character stream
     * Stdout is read as binary data stream
     */

    public static BinaryOut runWithBinaryStdout(List cmdSeq,String cmdName,int maxBytesOnStdout, int maxCharsOnStderr) {
        _check.notNull(cmdSeq,"command sequence")
        _check.notNull(cmdName,"command name")
        _check.largerThanZero(maxBytesOnStdout,'max bytes on stdout')
        _check.largerThanZero(maxCharsOnStderr,'max chars on stderr')
        //
        // Start process
        //
        def proc = cmdSeq.execute()
        //
        // While this is running, collect STDERR and STDOUT using separate threads (don't want to have the process block writing to stdout/stderr)
        //
        InputStreamRunnable stdoutRunnable = new InputStreamRunnable(proc.getInputStream(),"${cmdName} stdout reader",maxBytesOnStdout)
        ReaderRunnable      stderrRunnable = new ReaderRunnable(new InputStreamReader(proc.getErrorStream(),'UTF-8'),"${cmdName} stderr reader",maxCharsOnStderr)
        Thread stdoutThread = stdoutRunnable.startThisThread()
        Thread stderrThread = stderrRunnable.startThisThread()
        //
        // No exceptions at execute(), good sign; wait for command to finish
        //
        int exitValue = proc.waitFor()
        //
        // Wait for the reader threads to finish, then recuperate their buffers
        //
        stdoutThread.join()
        stderrThread.join()
        return new BinaryOut(exitValue, stdoutRunnable.getData(), stdoutRunnable.getBytesLost(), stderrRunnable.transfer(), stderrRunnable.getCharsLost())
    }

    /**
     * Run a command, slurping stdout and stderr.
     * Stderr is read as UTF-8 character stream
     * Stdout is read as UTF-8 character stream
     */

    public static LinesOut runWithCharacterStdout(List cmdSeq,String cmdName,int maxCharsOnStdout, int maxCharsOnStderr) {
        _check.notNull(cmdSeq,"command sequence")
        _check.notNull(cmdName,"command name")
        _check.largerThanZero(maxCharsOnStdout,'max chars on stdout')
        _check.largerThanZero(maxCharsOnStderr,'max chars on stderr')
        //
        // Start process
        //
        def proc = cmdSeq.execute()
        //
        // While this is running, collect STDERR and STDOUT using separate threads (don't want to have the process block writing to stdout/stderr)
        //
        ReaderRunnable stdoutRunnable = new ReaderRunnable(new InputStreamReader(proc.getInputStream(),'UTF-8'),"${cmdName} stdout reader",maxCharsOnStdout)
        ReaderRunnable stderrRunnable = new ReaderRunnable(new InputStreamReader(proc.getErrorStream(),'UTF-8'),"${cmdName} sterr reader",maxCharsOnStderr)
        Thread stdoutThread = stdoutRunnable.startThisThread()
        Thread stderrThread = stderrRunnable.startThisThread()
        //
        // No exceptions at execute(), good sign; wait for command to finish
        //
        int exitValue = proc.waitFor()
        //
        // Wait for the reader threads to finish, then recuperate their buffers
        //
        stdoutThread.join()
        stderrThread.join()
        return new LinesOut(exitValue, stdoutRunnable.transfer(), stdoutRunnable.getCharsLost(), stderrRunnable.transfer(), stderrRunnable.getCharsLost())
    }
}
