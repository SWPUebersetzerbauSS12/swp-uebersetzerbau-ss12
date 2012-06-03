package dfaprovider;

import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test-Klasse für den MinimalenDfaProvider-Klasse.
 * @author Maximilian Schröder
 *
 */
public class MinimalDfaProviderTest {
	
	/**
	 * Test of getMinimalDfa method, with false input.
	 */
	@Test
	public void testGetMinimalDfaCorrectInput() throws Exception {
		File failRDFile =  new File("C:\\TEMP\\");
		File failRDPath = new File("C:\\TEMP\\dfaprovider2.test.rd");
		File failDFAPath = new File("C:\\TEMP\\dfaprovider2.test.rd.dfa");
		File rdInputFile = new File("C:\\TEMP\\dfaprovider.test.rd");
		MinimalDfaBuilder builder1 = new IndirectMinimalDfaBuilder();
		MinimalDfaBuilder builder2 = new DirectMinimalDfaBuilder();
		MinimalDfaBuilder failBuilder = null;
		
		//Test for builder
		try {
			MinimalDfaProvider.getMinimalDfa(failRDFile, failBuilder);
			fail("MinimalDfaProviderException should be raised");
		}
		catch(MinimalDfaProviderException expected){
//			expected.printStackTrace();
		}
		
		//Test for file, builder1
		try {
			MinimalDfaProvider.getMinimalDfa(failRDPath, builder1);
			fail("MinimalDfaProviderException should be raised");
		}
		catch(MinimalDfaProviderException expected){
//			expected.printStackTrace();
		}
		
		//Test for file, builder 2
		try {
			MinimalDfaProvider.getMinimalDfa(failRDPath, builder2);
			fail("MinimalDfaProviderException should be raised");
		}
		catch(MinimalDfaProviderException expected){
//			expected.printStackTrace();
		}
		
		//Test for correct rdFile
		try {
			MinimalDfaProvider.getMinimalDfa(failRDFile, builder1);
			fail("MinimalDfaProviderException should be raised");
		}
		catch(MinimalDfaProviderException expected){
//			expected.printStackTrace();
		}
		
		//Test for correct dfaFile
		try {
			MinimalDfaProvider.getMinimalDfa(rdInputFile, builder1,false,false,failDFAPath);
			fail("MinimalDfaProviderException should be raised");
		}
		catch(MinimalDfaProviderException expected){
//			expected.printStackTrace();
		}
	}
	
	/**
	 * Test of getMinimalDfa method, with 2 parameter.
	 */
	@Test
	public void testGetMinimalDfa2Param() throws Exception {
		File rdInputFile = new File("C:\\TEMP\\dfaprovider.test.rd");
		MinimalDfaBuilder builder1 = new IndirectMinimalDfaBuilder();
		MinimalDfaBuilder builder2 = new DirectMinimalDfaBuilder();
		
		//indirektMinimalDfaBuilder
		Assert.assertNotNull(MinimalDfaProvider.getMinimalDfa(rdInputFile, builder1));
		//direktMinimalDfaBuilder
		Assert.assertNotNull(MinimalDfaProvider.getMinimalDfa(rdInputFile, builder2));
		
	}
	
	/**
	 * Test of getMinimalDfa method, with 3 parameter.
	 */
	@Test
	public void testGetMinimalDfa3Param() throws Exception {
		File rdInputFile = new File("C:\\TEMP\\dfaprovider.test.rd");
		MinimalDfaBuilder builder1 = new IndirectMinimalDfaBuilder();
		MinimalDfaBuilder builder2 = new DirectMinimalDfaBuilder();
		
		//indirektMinimalDfaBuilder
		Assert.assertNotNull(MinimalDfaProvider.getMinimalDfa(rdInputFile, builder1, true));
		Assert.assertNotNull(MinimalDfaProvider.getMinimalDfa(rdInputFile, builder1, false));
		
		/* TODO: delete comment, if DirectMinimalDfaBuilder is working
		//direktMinimalDfaBuilder
		Assert.assertNotNull(MinimalDfaProvider.getMinimalDfa(rdInputFile, builder2, true));
		Assert.assertNotNull(MinimalDfaProvider.getMinimalDfa(rdInputFile, builder2, false));
		*/
	}
	
	/**
	 * Test of getMinimalDfa method, with 4 parameter.
	 */
	@Test
	public void testGetMinimalDfa4Param() throws Exception {
		File rdInputFile = new File("C:\\TEMP\\dfaprovider.test.rd");
		MinimalDfaBuilder builder1 = new IndirectMinimalDfaBuilder();
		MinimalDfaBuilder builder2 = new DirectMinimalDfaBuilder();
		
		//indirektMinimalDfaBuilder
		Assert.assertNotNull(MinimalDfaProvider.getMinimalDfa(rdInputFile, builder1, true, true));
		Assert.assertNotNull(MinimalDfaProvider.getMinimalDfa(rdInputFile, builder1, true, false));
		Assert.assertNotNull(MinimalDfaProvider.getMinimalDfa(rdInputFile, builder1, false, true));
		Assert.assertNotNull(MinimalDfaProvider.getMinimalDfa(rdInputFile, builder1, false, false));
		
		/* TODO: delete comment, if DirectMinimalDfaBuilder is working
		//direktMinimalDfaBuilder
		Assert.assertNotNull(MinimalDfaProvider.getMinimalDfa(rdInputFile, builder2, true, true));
		Assert.assertNotNull(MinimalDfaProvider.getMinimalDfa(rdInputFile, builder2, true, false));
		Assert.assertNotNull(MinimalDfaProvider.getMinimalDfa(rdInputFile, builder2, false, true));
		Assert.assertNotNull(MinimalDfaProvider.getMinimalDfa(rdInputFile, builder2, false, false));
		*/
	}
	
	/**
	 * Test of getMinimalDfa method, with 5 parameter.
	 */
	@Test
	public void testGetMinimalDfa5Param() throws Exception {
		File rdInputFile = new File("C:\\TEMP\\dfaprovider.test.rd");
		File dfaInputFile = new File("C:\\TEMP\\dfaprovider.test.rd.dfa");
		MinimalDfaBuilder builder1 = new IndirectMinimalDfaBuilder();
		MinimalDfaBuilder builder2 = new DirectMinimalDfaBuilder();
		
		//indirektMinimalDfaBuilder
		Assert.assertNotNull(MinimalDfaProvider.getMinimalDfa(rdInputFile, builder1, true, true, dfaInputFile));
		Assert.assertNotNull(MinimalDfaProvider.getMinimalDfa(rdInputFile, builder1, true, false, dfaInputFile));
		Assert.assertNotNull(MinimalDfaProvider.getMinimalDfa(rdInputFile, builder1, false, true, dfaInputFile));
		Assert.assertNotNull(MinimalDfaProvider.getMinimalDfa(rdInputFile, builder1, false, false, dfaInputFile));
		
		/* TODO: delete comment, if DirectMinimalDfaBuilder is working
		//direktMinimalDfaBuilder
		Assert.assertNotNull(MinimalDfaProvider.getMinimalDfa(rdInputFile, builder2, true, true, dfaInputFile));
		Assert.assertNotNull(MinimalDfaProvider.getMinimalDfa(rdInputFile, builder2, true, false, dfaInputFile));
		Assert.assertNotNull(MinimalDfaProvider.getMinimalDfa(rdInputFile, builder2, false, true, dfaInputFile));
		Assert.assertNotNull(MinimalDfaProvider.getMinimalDfa(rdInputFile, builder2, false, false, dfaInputFile));
		*/
	}
}
