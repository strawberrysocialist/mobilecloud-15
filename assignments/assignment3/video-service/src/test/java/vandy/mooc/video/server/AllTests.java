package vandy.mooc.video.server;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import vandy.mooc.video.server.controller.tests.VideoServiceControllerTest;
import vandy.mooc.video.server.integration.tests.VideoServiceIntegrationTest;

@RunWith(Suite.class)
@SuiteClasses({ VideoServiceIntegrationTest.class,
		VideoServiceControllerTest.class })
public class AllTests {

}
