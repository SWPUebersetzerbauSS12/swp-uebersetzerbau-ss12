package lexergen;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;

public class SettingsTest {

	@Test
	public void test() {
		Settings.readSettings();
		Assert.assertNotNull(Settings.getVersion());
		Assert.assertNotNull(Settings.getDefaultTokenDef());
		Assert.assertNotNull(Settings.getApplicationPath());
		Assert.assertNotNull(Settings.getSourceProgramFile());
		Assert.assertNotNull(Settings.getErrorCorrectionMode());
		Assert.assertNotNull(Settings.getWorkingDirectory());
	}

}
