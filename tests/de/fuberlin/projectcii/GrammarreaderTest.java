package de.fuberlin.projectcii;

import static org.junit.Assert.*;
/**
 * Test of Grammar Reader Methods
 */

import java.io.IOException;

import java.util.Map;
import java.util.Vector;

import de.fuberlin.projectcii.ParserGenerator.src.*;

import org.junit.Before;
import org.junit.Test;

public class GrammarreaderTest {
    
	private String filePath = "tests/resources/de/fuberlin/projectcii/testLanguage.txt";
    GrammarReader reader;
    
    @Before
    public void init(){
        reader = new GrammarReader();
        try {
            Settings.initalize();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            fail("I/O-Error in Settings" + e.getMessage());
        }
        
    }

    @Test
    public void testGrammarUnmodified() {
        try {
            /**
             * Test if Grammer Reader read correct 
             * our Grammatik 
             * 
             */

            Map<String, Vector<Vector<String>>> grammer = reader.createGrammar(5, false,
                    filePath, false);

            String startSymbol = reader.getStartSymbol();
            assertNotNull(startSymbol);
            for (String leftsight: grammer.keySet()){
                System.out.println("Head = '" + leftsight+"'");
                if (leftsight.equals("E")){
                    for (int i=0; i<grammer.get(leftsight).size();i++){
                        if(i==0)
                        {
                            Vector<String> production = grammer.get(leftsight).elementAt(i);
                            System.out.println("Productions:" + production.elementAt(0)
                                    +","+production.elementAt(1)+","+ production.elementAt(2) );

                            boolean correct=true;
                            if (!(production.elementAt(0).equals("E")))
                            {
                                correct = false;
                            }
                            if (!(production.elementAt(1).equals("+")))
                            {
                                correct = false;
                            }
                            if (!(production.elementAt(2).equals("T")))
                            {
                                correct = false;
                            }
                            assertTrue("Incorrect Production", correct);
                        }
                        else if(i==1){
                            Vector<String> production = grammer.get(leftsight).elementAt(i);
                            System.out.println("Productions:" + production.elementAt(0)  );
                            boolean correct = true;
                            if (!(production.elementAt(0).equals("T"))){
                                correct = false;
                            }        
                            assertTrue("Incorrect Production", correct);
                        }
                    }
                }
                else if (leftsight.equals("T")){
                    for (int i=0; i<grammer.get(leftsight).size();i++){
                        if(i==0){
                            Vector<String> production = grammer.get(leftsight).elementAt(i);
                            System.out.println("Productions:" + production.elementAt(0)
                                    +","+production.elementAt(1)+","+ production.elementAt(2) );
                            boolean correct=true;
                            if (!(production.elementAt(0).equals("T"))){
                                correct = false;

                            }
                            if (!(production.elementAt(1).equals("*"))){
                                correct = false;
                            }
                            if (!(production.elementAt(2).equals("F"))){

                                correct = false;
                            }
                            assertTrue("Incorrect Production", correct);
                        }
                        if(i==1){
                            Vector<String> production = grammer.get(leftsight).elementAt(i);
                            boolean correct = true;
                            if (!(production.elementAt(0).equals("F"))){
                                correct = false;
                            }
                            assertTrue("Incorrect Production", correct);
                        }
                    }
                }
                else if (leftsight.equals("F")){
                    for (int i=0; i<grammer.get(leftsight).size();i++){
                        if(i==0){
                            Vector<String> production = grammer.get(leftsight).elementAt(i);

                            boolean correct=true;
                            if (!(production.elementAt(0).equals("id"))){
                                correct = false;
                            }
                            assertTrue("Incorrect Production", correct);
                        }
                        if(i==1){
                            Vector<String> production = grammer.get(leftsight).elementAt(i);
                            System.out.println("Productions:" + production.elementAt(0)
                                    +","+production.elementAt(1)+","+ production.elementAt(2) );

                            boolean correct = true;
                            if (!(production.elementAt(0).equals("("))){
                                correct = false;
                            }
                            if (!(production.elementAt(1).equals("E"))){
                                correct = false;
                            }
                            if (!(production.elementAt(2).equals(")"))){
                                correct = false;
                            }
                            assertTrue("Incorrect Production", correct);
                        }
                    }
                }
                else{
                    fail("Incorrect Grammarhead");
                }
            }
        } catch (IOException e) {
            fail("Incorrect Grammarhead" + e.getMessage());
        }
    }

        @Test
        public void testGrammarModified() {


            /**
             * Test if Grammer Reader turn 
             * our Grammatik
             * 
             */
            try{
                Map<String, Vector<Vector<String>>> grammer1 = reader.createGrammar(5,
                        true, filePath, false);

                String startSymbol1 = reader.getStartSymbol();
                assertNotNull(startSymbol1);

                for (String leftsight: grammer1.keySet()){
                    if (leftsight.equals("E")){
                        for (int i=0; i<grammer1.get(leftsight).size();i++){
                            if(i==0){
                                Vector<String> production =
                                    grammer1.get(leftsight).elementAt(i);
                                boolean correct=true;
                                if (!(production.elementAt(0).equals("T"))){
                                    correct = false;

                                }

                                assertTrue("Incorrect Production", correct);
                            }
                            if(i==1){
                                Vector<String> production =
                                    grammer1.get(leftsight).elementAt(i);
                                boolean correct = true;
                                if (!(production.elementAt(0).equals("E$"))){
                                    correct = false;
                                }
                                assertTrue("Incorrect Production", correct);
                            }
                        }
                    }
                    else if (leftsight.equals("E$")){
                        for (int i=0; i<grammer1.get(leftsight).size();i++){
                            if(i==0){
                                Vector<String> production =
                                    grammer1.get(leftsight).elementAt(i);
                                boolean correct=true;
                                if (!(production.elementAt(0).equals("+"))){
                                    correct = false;

                                }
                                if (!(production.elementAt(1).equals( "T"))){
                                    correct = false;
                                }
                                if (!(production.elementAt(2).equals("E$"))){

                                    correct = false;
                                }
                                assertTrue("Incorrect Production", correct);
                            }
                            if(i==1){
                                Vector<String> production =
                                    grammer1.get(leftsight).elementAt(i);
                                boolean correct = true;
                                if (!(production.elementAt(0).equals("@"))){
                                    correct = false;
                                }
                                assertTrue("Incorrect Production", correct);
                            }
                        }
                    }
                    else if (leftsight.equals("T")){
                        for (int i=0; i<grammer1.get(leftsight).size();i++){
                            if(i==0){
                                Vector<String> production =
                                    grammer1.get(leftsight).elementAt(i);
                                boolean correct=true;
                                if (!(production.elementAt(0).equals("F"))){
                                    correct = false;

                                }
                                if (!(production.elementAt(1).equals("T$"))){
                                    correct = false;

                                }

                                assertTrue("Incorrect Production", correct);
                            }
                        }
                    }

                    else if (leftsight.equals("T$")){
                        for (int i=0; i<grammer1.get(leftsight).size();i++){
                            if(i==0){
                                Vector<String> production =
                                    grammer1.get(leftsight).elementAt(i);
                                boolean correct=true;
                                if (!(production.elementAt(0).equals("*"))){
                                    correct = false;

                                }
                                if (!(production.elementAt(1).equals("F"))){
                                    correct = false;

                                }
                                if (!(production.elementAt(2).equals("T$"))){
                                    correct = false;

                                }

                                assertTrue("Incorrect Production", correct);
                            }
                            else if(i==1){
                                Vector<String> production =
                                    grammer1.get(leftsight).elementAt(i);
                                boolean correct = true;
                                if (!(production.elementAt(0).equals("@"))){
                                    correct = false;
                                }


                                assertTrue("Incorrect Production", correct);
                            }
                        }
                    }
                    else if (leftsight.equals("F")){
                        for (int i=0; i<grammer1.get(leftsight).size();i++){
                            if(i==0){
                                Vector<String> production =
                                    grammer1.get(leftsight).elementAt(i);
                                boolean correct=true;
                                if (!(production.elementAt(0).equals("id"))){
                                    correct = false;
                                }

                                assertTrue("Incorrect Production", correct);
                            }
                            else if(i==1){
                                Vector<String> production =
                                    grammer1.get(leftsight).elementAt(i);
                                boolean correct = true;
                                if (!(production.elementAt(0).equals("("))){
                                    correct = false;
                                }
                                if (!(production.elementAt(1).equals("E"))){
                                    correct = false;
                                }
                                if (!(production.elementAt(2).equals(")")))
                                {
                                    correct = false;
                                }                                                                                                

                                assertTrue("Incorrect Production", correct);
                            }
                        }
                    }
                    else{
                        fail("Incorrect Grammarhead, head '" + leftsight +"'" );
                    }
                }
            } catch (IOException e){
                fail("Incorrect Grammarhead" + e.getMessage());
            }        

        }
}

