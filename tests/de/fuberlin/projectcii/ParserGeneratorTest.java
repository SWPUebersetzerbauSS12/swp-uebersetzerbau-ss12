package de.fuberlin.projectcii;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.junit.Before;
import org.junit.Test;

import de.fuberlin.projectcii.ParserGenerator.src.ParserGenerator;
import de.fuberlin.projectcii.ParserGenerator.src.Settings;


public class ParserGeneratorTest {

    private String filePath = "tests/resources/de/fuberlin/projectcii/";
    
    ParserGenerator generator;
    ParserGenerator generatorMod;
    ParserGenerator generatorEpsilon;
    
    @Before
    public void init(){
        try {
            Settings.initalize(); 
            generator = new ParserGenerator();
            generator.initialize(false, filePath+"testLanguage.txt", false);
            generatorMod = new ParserGenerator();
            generatorMod.initialize(true, filePath+"testLanguage.txt", false);
            generatorEpsilon = new ParserGenerator();
            generatorEpsilon.initialize(true, filePath+"testLanguageEpsilonExclude.txt", false);

        } catch (IOException e) {
            fail("I/O-Error in Settings" + e.getMessage());
        }
    }

    @Test
    public void testFirstSet(){

    	try {
            /**
             * Test if ParserGenerator generates the first sets from unmodified Grammatik correctly.
             *  
             */

            Map<String, HashMap<String, Vector<Integer>>> firstSetsProductions = generator.getFirstSetsProductions();

            assertTrue("Head E not found as first set",firstSetsProductions.containsKey("E"));
            HashMap<String, Vector<Integer>> firstSet = firstSetsProductions.get("E");
            assertTrue("id not found underfirst set of E",firstSet.containsKey("id"));
            assertTrue("( not found underfirst set of E",firstSet.containsKey("("));        		

            
            assertTrue("Head T not found as first set",firstSetsProductions.containsKey("T"));
            firstSet = firstSetsProductions.get("T");
            assertTrue("id not found underfirst set of T",firstSet.containsKey("id"));
            assertTrue("( not found underfirst set of T",firstSet.containsKey("("));     

              

            assertTrue("Head F not found as first set",firstSetsProductions.containsKey("F"));
            firstSet = firstSetsProductions.get("F");
            assertTrue("id not found underfirst set of F",firstSet.containsKey("id"));
            assertTrue("( not found underfirst set of F",firstSet.containsKey("("));     
        } catch (Exception e) {
            fail("Error testing Generator" + e.getMessage());
        }
    }

    @Test
    public void testFirstSetModified() {
        try {
            /**
             * Test if ParserGenerator generates the first sets from modified Grammatik correctly.
             *  
             */

            Map<String, HashMap<String, Vector<Integer>>> firstSetsProductions = generatorMod.getFirstSetsProductions();

            assertTrue("Head E not found as first set",firstSetsProductions.containsKey("E"));
            HashMap<String, Vector<Integer>> firstSet = firstSetsProductions.get("E");
            assertTrue("id not found underfirst set of E",firstSet.containsKey("id"));
            assertTrue("( not found underfirst set of E",firstSet.containsKey("("));        		

            assertTrue("Head E$ not found as first set", firstSetsProductions.containsKey("E$"));
            firstSet = firstSetsProductions.get("E$");
            assertTrue("+ not found underfirst set of E$",firstSet.containsKey("+"));
            assertTrue("@ not found underfirst set of E$",firstSet.containsKey("@"));     

            assertTrue("Head T not found as first set",firstSetsProductions.containsKey("T"));
            firstSet = firstSetsProductions.get("T");
            assertTrue("id not found underfirst set of T",firstSet.containsKey("id"));
            assertTrue("( not found underfirst set of T",firstSet.containsKey("("));     



            assertTrue(firstSetsProductions.containsKey("T$"));
            firstSet = firstSetsProductions.get("T$");
            assertTrue("* not found underfirst set of T$",firstSet.containsKey("*"));
            assertTrue("@ not found underfirst set of T$",firstSet.containsKey("@"));     

            assertTrue("Head F not found as first set",firstSetsProductions.containsKey("F"));
            firstSet = firstSetsProductions.get("F");
            assertTrue("id not found underfirst set of F",firstSet.containsKey("id"));
            assertTrue("( not found underfirst set of F",firstSet.containsKey("("));     
        } catch (Exception e) {
            fail("Error testing Generator" + e.getMessage());
        }
    }

    @Test
    public void testFollowSetunmodified(){

    	/**
         * Test if ParserGenerator generates the Follow sets from unmodified Grammatik correctly.
         *  
         */
        
    	try{
            Map<String, Set<String>> followSets = generator.getFollowSets();

            assertTrue("E not found as follow set", followSets.containsKey("E"));
            Set<String> followSet = followSets.get("E");
            assertTrue("EOF not found under followset of E", followSet.contains(Settings.getEOF()));
            assertTrue(") not found under followset of E", followSet.contains(")"));
            assertTrue(" + not found under followset of E", followSet.contains("+"));

            
            assertTrue("T not found as follow set", followSets.containsKey("T"));
            followSet = followSets.get("T");
            assertTrue("EOF not found under followset of T", followSet.contains(Settings.getEOF()));
            assertTrue("+ not found under followset of T", followSet.contains("+"));
            assertTrue(") not found under followset of T", followSet.contains(")"));
            assertTrue("* not found under followset of T", followSet.contains("*"));

            
            assertTrue("F not found as follow set", followSets.containsKey("F"));
            followSet = followSets.get("F");
            assertTrue("EOF not found under followset of F", followSet.contains(Settings.getEOF()));
            assertTrue(") not found under followset of F", followSet.contains(")"));
            assertTrue("* not found under followset of F", followSet.contains("*"));
            assertTrue("+ not found under followset of F", followSet.contains("+"));
        } catch (Exception e) {
            fail("Error testing Generator" + e.getMessage());
        }
    }

    @Test
    public void testFollowSetModified() {
        /**
         * Test if ParserGenerator generates the Follow sets from ,modified Grammatik correctly.
         *  
         */
        try{
            Map<String, Set<String>> followSets = generatorMod.getFollowSets();
            
            assertTrue("E not found as follow set", followSets.containsKey("E"));
            Set<String> followSet = followSets.get("E");
            assertTrue("EOF not found under followset of E", followSet.contains(Settings.getEOF()));
            assertTrue(") not found under followset of E", followSet.contains(")"));

            assertTrue("E$ not found as follow set", followSets.containsKey("E$"));
            followSet = followSets.get("E$");
            assertTrue("EOF not found under followset of E$", followSet.contains(Settings.getEOF()));
            assertTrue(") not found under followset of E$", followSet.contains(")"));

            assertTrue("T not found as follow set", followSets.containsKey("T"));
            followSet = followSets.get("T");
            assertTrue("EOF not found under followset of T", followSet.contains(Settings.getEOF()));
            assertTrue("+ not found under followset of T", followSet.contains("+"));
            assertTrue(") not found under followset of T", followSet.contains(")"));

            assertTrue("T$ not found as follow set", followSets.containsKey("T$"));
            followSet = followSets.get("T$");
            assertTrue("EOF not found under followset of T$", followSet.contains(Settings.getEOF()));
            assertTrue("+ not found under followset of T$", followSet.contains("+"));
            assertTrue(") not found under followset of T$", followSet.contains(")"));

            assertTrue("F not found as follow set", followSets.containsKey("F"));
            followSet = followSets.get("F");
            assertTrue("EOF not found under followset of F", followSet.contains(Settings.getEOF()));
            assertTrue(") not found under followset of F", followSet.contains(")"));
            assertTrue("* not found under followset of F", followSet.contains("*"));
            assertTrue("+ not found under followset of F", followSet.contains("+"));
        } catch (Exception e) {
            fail("Error testing Generator" + e.getMessage());
        }
    }


    @Test
    public void testParserTableUnmodified() {
        
    	/**
         * Test if ParserGenerator generates the ParserTable  from unmodified Grammatik correctly.
         */
    	
    	try{

            Map<String, HashMap<String, Vector<Integer>>> parseTable = generator.getParseTable();

            assertTrue("E not found in parse table",parseTable.containsKey("E"));
            HashMap<String, Vector<Integer>> subset = parseTable.get("E");
            assertTrue("id not found under E",subset.containsKey("id"));
            Vector<Integer> prodNr = subset.get("id");
            assertTrue("id not found under E",prodNr.elementAt(0)== 0);
            assertTrue("id not found under E",prodNr.elementAt(1)== 1);
            
            assertTrue("( not found under E",subset.containsKey("("));
            prodNr = subset.get("(");
            assertTrue("( not found under E",prodNr.elementAt(0)== 0);
            assertTrue("( not found under E",prodNr.elementAt(1)== 1);


            assertTrue("T not found in parse table",parseTable.containsKey("T"));
            subset = parseTable.get("T");
            assertTrue("id not found under T",subset.containsKey("id"));
            prodNr = subset.get("id");
            assertTrue("id not found under T",prodNr.elementAt(0)== 0);
            assertTrue("id not found under T",prodNr.elementAt(1)== 1);
            assertTrue("( not found under T",subset.containsKey("("));
            prodNr = subset.get("(");
            assertTrue("( not found under T",prodNr.elementAt(0)== 0);
            assertTrue("( not found under T",prodNr.elementAt(1)== 1);

            

            assertTrue("F not found in parse table",parseTable.containsKey("F"));
            subset = parseTable.get("F");
            assertTrue("id not found under F",subset.containsKey("id"));
            prodNr = subset.get("id");
            assertTrue("id not found under F",prodNr.elementAt(0)== 0);
            assertTrue("( not found under F",subset.containsKey("("));
            prodNr = subset.get("(");
            assertTrue("( not found under F",prodNr.elementAt(0)== 1);

        } catch (Exception e) {
            fail("Error testing Generator" + e.getMessage());
        }
    }


    @Test
    public void testParserTableModified() {



    	/**
         * Test if ParserGenerator generates the ParserTable  from modified Grammatik correctly.
         */
    	
        try{

            Map<String, HashMap<String, Vector<Integer>>> parseTable = generatorMod.getParseTable();

            assertTrue("E not found in parse table",parseTable.containsKey("E"));
            HashMap<String, Vector<Integer>> subset = parseTable.get("E");
            assertTrue("id not found under E",subset.containsKey("id"));
            Vector<Integer> prodNr = subset.get("id");
            assertTrue("id not found under E",prodNr.elementAt(0)== 0);
            assertTrue("( not found under E",subset.containsKey("("));
            prodNr = subset.get("(");
            assertTrue("( not found under E",prodNr.elementAt(0)== 0);


            assertTrue("E$ not found in parse table",parseTable.containsKey("E$"));
            subset = parseTable.get("E$");
            assertTrue("+ not found under E$",subset.containsKey("+"));
            prodNr = subset.get("+");
            assertTrue("+ not found under E$",prodNr.elementAt(0)== 0);
            assertTrue(") not found under E$",subset.containsKey(")"));
            prodNr = subset.get(")");
            assertTrue(") not found under E$",prodNr.elementAt(0)== 1);
            assertTrue("EOF not found under E$",subset.containsKey(Settings.getEOF()));
            prodNr = subset.get(Settings.getEOF());
            assertTrue("EOF not found under E$",prodNr.elementAt(0)== 1);
            
            assertTrue("T not found in parse table",parseTable.containsKey("T"));
            subset = parseTable.get("T");
            assertTrue("id not found under T",subset.containsKey("id"));
            prodNr = subset.get("id");
            assertTrue("id not found under T",prodNr.elementAt(0)== 0);
            assertTrue("( not found under T",subset.containsKey("("));
            prodNr = subset.get("(");
            assertTrue("( not found under T",prodNr.elementAt(0)== 0);

            assertTrue("T$ not found in parse table",parseTable.containsKey("T$"));
            subset = parseTable.get("T$");
            assertTrue("+ not found under T$",subset.containsKey("+"));
            prodNr = subset.get("+");
            assertTrue("+ not found under T$",prodNr.elementAt(0)== 1);
            assertTrue("* not found under T$",subset.containsKey("*"));
            prodNr = subset.get("*");
            assertTrue("* not found under T$",prodNr.elementAt(0)== 0);
            assertTrue(") not found under T$",subset.containsKey(")"));
            prodNr = subset.get(")");
            assertTrue(") not found under T$",prodNr.elementAt(0)== 1);
            assertTrue("EOF not found under T$",subset.containsKey(Settings.getEOF()));
            prodNr = subset.get(Settings.getEOF());
            assertTrue("EOF not found under T$",prodNr.elementAt(0)== 1);

            assertTrue("F not found in parse table",parseTable.containsKey("F"));
            subset = parseTable.get("F");
            assertTrue("id not found under F",subset.containsKey("id"));
            prodNr = subset.get("id");
            assertTrue("id not found under F",prodNr.elementAt(0)== 0);
            assertTrue("( not found under F",subset.containsKey("("));
            prodNr = subset.get("(");
            assertTrue("( not found under F",prodNr.elementAt(0)== 1);

        } catch (Exception e) {
            fail("Error testing Generator" + e.getMessage());
        }
    }
    
    @Test
    public void testFirstSetEpsilonExcluded() {

        try {
            /**
             * Test if ParserGenerator generates the first sets from unmodified Grammatik correctly.
             *  
             */

            Map<String, HashMap<String, Vector<Integer>>> firstSetsProductions = generatorEpsilon.getFirstSetsProductions();

            assertTrue("Head A not found as first set",firstSetsProductions.containsKey("A"));
            HashMap<String, Vector<Integer>> firstSet = firstSetsProductions.get("A");
            assertTrue("c not found in first set of A",firstSet.containsKey("c"));
            assertTrue("d not found in first set of A",firstSet.containsKey("d"));    
            assertTrue("e not found in first set of A",firstSet.containsKey("e")); 
            assertFalse("@ found in first set of A",firstSet.containsKey("@"));

        } catch (Exception e) {
            fail("Error testing Generator" + e.getMessage());
        }
    }


}
