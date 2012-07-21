package de.fuberlin.projectcii;

import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.junit.runner.RunWith;

@RunWith(Suite.class)
@SuiteClasses( {
    GrammarreaderTest.class,
    ParserGeneratorTest.class
})
public class AllTests {

}
