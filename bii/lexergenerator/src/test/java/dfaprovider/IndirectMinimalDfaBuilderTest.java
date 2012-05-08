package dfaprovider;

import java.io.File;

import lexergen.Settings;

import org.junit.Test;

public class IndirectMinimalDfaBuilderTest {

	@Test
	public void test() throws MinimalDfaBuilderException {
		MinimalDfaBuilder indirectMinimalDfaBuilder = new IndirectMinimalDfaBuilder();
		Settings.readSettings();
		indirectMinimalDfaBuilder.buildMinimalDfa(new File(Settings
				.getRegularDefinitionFileName()));
	}

}
