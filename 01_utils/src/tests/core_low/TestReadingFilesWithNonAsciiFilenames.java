package tests.core_low;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mplify.junit.TestStarter;

//@SuppressWarnings("static-method")
public class TestReadingFilesWithNonAsciiFilenames extends TestStarter {

    private final static String CLASS = TestReadingFilesWithNonAsciiFilenames.class.getName();

    @Test
    public void testCreateDirectoryWithWeirdFiles() throws IOException {
        Logger logger = LoggerFactory.getLogger(CLASS + ".testCreateDirectoryWithWeirdFiles");
        logger.info("This is VM: " + System.getProperty("java.vm.name") + "," + System.getProperty("java.version") + "," + System.getProperty("java.vm.version") + "," + System.getProperty("java.vm.vendor"));
        File tmpDir = new File(System.getProperty("java.io.tmpdir"));        
        assertTrue("It seems that " + tmpDir + " is not a directory", tmpDir.isDirectory());
        File myTmpDir = new File(tmpDir, CLASS + Long.toString(System.currentTimeMillis()));
        {
            boolean ok = myTmpDir.mkdir();
            assertTrue("The directory " + myTmpDir + " could not be created", ok);
        }
        Set<File> specials = new HashSet<File>();
        {
            specials.add(new File(myTmpDir, "Simple"));
            specials.add(new File(myTmpDir, "With Space in the middle"));
            specials.add(new File(myTmpDir, " WithSpaceAtTheLeft"));
            specials.add(new File(myTmpDir, "WithSpaceAtTheRight "));
            specials.add(new File(myTmpDir, "With(Parenthesis)"));
            specials.add(new File(myTmpDir, "With{Braces}"));
            specials.add(new File(myTmpDir, "WithUmlauts_é_à_ä_ö_"));
            specials.add(new File(myTmpDir, "WithRussianCharacters_Включить_"));
        }
        for (File special : specials) {
            assertFalse("File " + special + " already exists", special.exists());
            boolean ok = special.createNewFile();
            assertTrue("The file " + special + " could not be created", ok);
        }
    }
}
