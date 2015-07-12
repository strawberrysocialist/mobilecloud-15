package vandy.mooc.video.server;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ VideoServiceApplicationTests.class,
		VideoServiceIsolationTests.class })
public class AllTests {

}
