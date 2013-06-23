package tests.core_low;

import static org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test


import com.mplify.helpers.GetAsPort
import com.mplify.junit.TestStarter;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2010, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Testing the facilities to get port numbers from /etc/services
 ******************************************************************************/

class TestCaseGetAsPort extends TestStarter {

    final shouldFail = new GroovyTestCase().&shouldFail
    
    File file = new File("/tmp/${TestCaseGetAsPort.class.name}")

    @Before
    void setUp() {
        file.withWriter 'UTF-8', { Writer w ->
            w.println("# service-name  port/protocol  [aliases ...]   [# comment]")
            w.println("")
            w.println("  #  This file has been created by ...")
            w.println("")
            w.println("rtmp                                     1/ddp                         # Routing Table Maintenance Protocol")
            w.println("tcpmux                                   1/tcp                         # TCP port service multiplexer")
            w.println("tcpmux                                   1/udp                         # TCP port service multiplexer")
            w.println("nbp                                      2/ddp                         # Name Binding Protocol")
            w.println("discard                                  9/tcp     null sink")
            w.println("discard                                  9/udp     null sink")
            w.println("daytime                                  13/tcp")
            w.println("daytime                                  13/udp")
            w.println("netstat                                  15/tcp                        # (was once asssigned, no more)")
            w.println("qotd                                     17/tcp    quote")
            w.println("qotd                                     17/udp    quote")
        }
    }

    @After
    void tearDown() {
        file.delete()
    }

    @Test
    void testFoundReading() {
        assertEquals 1, GetAsPort.getAsPort("rtmp", "ddp", file)
        assertEquals 1, GetAsPort.getAsPort("tcpmux", "tcp", file)
        assertEquals 1, GetAsPort.getAsPort("tcpmux", "udp", file)
        assertEquals 2, GetAsPort.getAsPort("nbp", "ddp", file)
        assertEquals 9, GetAsPort.getAsPort("discard", "tcp", file)
        assertEquals 9, GetAsPort.getAsPort("discard", "udp", file)
        assertEquals 9, GetAsPort.getAsPort("null", "tcp", file)
        assertEquals 9, GetAsPort.getAsPort("null", "udp", file)
        assertEquals 9, GetAsPort.getAsPort("sink", "tcp", file)
        assertEquals 9, GetAsPort.getAsPort("sink", "udp", file)
        assertEquals 13, GetAsPort.getAsPort("daytime", "tcp", file)
        assertEquals 13, GetAsPort.getAsPort("daytime", "udp", file)
        assertEquals 15, GetAsPort.getAsPort("netstat", "tcp", file)
        assertEquals 17, GetAsPort.getAsPort("qotd", "tcp", file)
        assertEquals 17, GetAsPort.getAsPort("qotd", "udp", file)
        assertEquals 17, GetAsPort.getAsPort("quote", "tcp", file)
        assertEquals 17, GetAsPort.getAsPort("quote", "udp", file)
    }

    @Test
    void testMissingStuff() {
        shouldFail {
            GetAsPort.getAsPort("tcpmux", "ddp", file)
        }
    }

}
