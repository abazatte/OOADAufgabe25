package main;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.stefanbirkner.systemlambda.SystemLambda;

public class SystemTest {

    private Dialog dialog;

    @BeforeEach
    public void setUp() {
        this.dialog = new Dialog();
    }

    private void reset() throws Exception {
        this.ausfuehren(new String[]{"1"});
    }

    private void add(int wert) throws Exception {
        this.ausfuehren(new String[]{"2", "" + wert});
    }

    private void subtract(int wert) throws Exception{
        this.ausfuehren(new String[]{"3", "" + wert});
    }

    private void inMemory() throws Exception{
        this.ausfuehren(new String[]{"4"});
    }

    private void addMemory() throws Exception{
        this.ausfuehren(new String[]{"5"});
    }

    private void subtractMemory() throws Exception{
        this.ausfuehren(new String[]{"6"});
    }

    private void undo() throws Exception{
        this.ausfuehren(new String[]{"98"});
    }

    private void redo() throws Exception{
        this.ausfuehren(new String[]{"99"});
    }

    private void ausfuehren(String[] inputs) throws Exception {
          SystemLambda.withTextFromSystemIn(inputs)
          .execute(() -> {
            this.dialog.einSchritt();
          });
    }
    
//    private String ausfuehren(String[] inputs) throws Exception {
//      String systemOut = SystemLambda.tapSystemOutNormalized(() -> {
//        SystemLambda.withTextFromSystemIn(inputs)
//        .execute(() -> {
//          this.dialog.einSchritt();
//        });
//      });
//      //System.out.println("inputs: " + Arrays.asList(inputs));
//      return systemOut;
//  }

    private Stack<String> ausgegebeneWerte(String out) throws Exception{
        //String out = this.systemOutMock.getLog();
        Pattern pat = Pattern.compile("\\d+");
        Matcher mat = pat.matcher(out);
        Stack<String> werte = new Stack<>();
        while (mat.find()) {
            werte.add(out.substring(mat.start(), mat.end()));
        }
        //System.out.println("werte: " + werte);
        if (werte.size() < 2) {
            throw new IllegalArgumentException("Kann Werte fuer Speicher und "
                    + "Rechner nicht finden, gefordertes Ausgabeformat "
                    + "eingehalten?");
        }
        return werte;
    }
    
    private void erwartet(Integer speicher, Integer rechner, String out) throws Exception {
        Stack<String> werte = this.ausgegebeneWerte(out);
        Assertions.assertEquals(rechner, Integer.decode(werte.pop())
            , "Wert des Rechners stimmt nicht mit Erwartung "
                + "ueberein");
        Assertions.assertEquals(speicher, Integer.decode(werte.pop())
            , "Wert des Speichers stimmt nicht mit Erwartung "
                + "ueberein");
    }
    
    private boolean wertGefunden(int wert, String out) throws Exception{
        return this.ausgegebeneWerte(out).contains(""+wert);
    }

    @Test
    public void testUndoOhneVorherigenSchritt() throws Exception {
        try {
            String out = SystemLambda.tapSystemOutNormalized(() -> {
              this.undo();
            });
            erwartet(0, 0, out);
        } catch (Exception e) {
            Assertions.fail("Undo ohne vorherigem Schritt darf nicht zu "
                    + "Abbruch fuehren: " + e);
            e.printStackTrace();
        }
    }

    @Test
    public void testRedoOhneVorherigenSchritt() throws Exception {
        try {
     
            String out = SystemLambda.tapSystemOutNormalized(() -> {
              this.redo();
            });
            erwartet(0, 0, out);
        } catch (Exception e) {
            Assertions.fail("Redo ohne vorherigem Schritt darf nicht zu "
                    + "Abbruch fuehren: " + e);
        }
    }

    @Test
    public void testResetOhneVorherigenSchritt() throws Exception {
        try {
            
            String out = SystemLambda.tapSystemOutNormalized(() -> {
              this.reset();
            });
            erwartet(0, 0, out);
        } catch (Exception e) {
            Assertions.fail("Reset ohne vorherigem Schritt darf nicht zu "
                    + "Abbruch fuehren: " + e);
        }
    }

    @Test
    public void testAdd() throws Exception {
        this.add(21);

        String out = SystemLambda.tapSystemOutNormalized(() -> {
          this.add(21);
        });
        erwartet(0, 42, out);
    }
    
    @Test
    public void testSubstract() throws Exception {
        this.add(42);
        this.add(42);
        
        String out = SystemLambda.tapSystemOutNormalized(() -> {
          this.subtract(42);
        });
        erwartet(0, 42, out);
    }
    
    @Test
    public void testToMemory() throws Exception {
        this.add(21);
        this.inMemory();
   
        String out = SystemLambda.tapSystemOutNormalized(() -> {
          this.add(21);
        });
        erwartet(21, 42, out);
    }
    
    @Test
    public void testAddMemory() throws Exception {
        this.add(21);
        this.inMemory();
        this.add(21);
        
        String out = SystemLambda.tapSystemOutNormalized(() -> {
          this.addMemory();
        });
        erwartet(21, 63, out);
    }
    
    @Test
    public void testSubtractMemory() throws Exception{
        this.add(21);
        this.inMemory();
        this.add(21);
        
        String out = SystemLambda.tapSystemOutNormalized(() -> {
          this.subtractMemory();
        });
        erwartet(21, 21, out);
    }
    
    @Test
    public void testReset() throws Exception {
        this.add(21);
        this.inMemory();
        this.add(21);
        this.addMemory();
        
        String out = SystemLambda.tapSystemOutNormalized(() -> {
          this.reset();
        });
        erwartet(0, 0, out);
    }
    
    
    // in der letzten Ausgabe soll kein Undo angeboten werden
    private String keinUndo() throws Exception{
      String systemOut = SystemLambda.tapSystemOutNormalized(() -> {
        this.dialog.ausgabe();
      });  
        Assertions.assertFalse(this.wertGefunden(98, systemOut)
            , "Wenn kein Undo moeglich, soll auch kein "
                + "Undo angeboten werden");     
        return systemOut;
    }
    
    private String keinRedo() throws Exception{
      String systemOut = SystemLambda.tapSystemOutNormalized(() -> {
        this.dialog.ausgabe();
      });  
        Assertions.assertFalse(this.wertGefunden(99, systemOut)
            , "Wenn kein Redo moeglich, soll auch kein "
                + "redo angeboten werden"); 
        return systemOut;
    }    
    
    @Test
    public void testKeinUndoAmAnfang() throws Exception {
        this.keinUndo();
    } 
    
    @Test
    public void testKeinRedoAmAnfang() throws Exception {
        
        String systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.add(21);
        }); 
        Assertions.assertFalse(this.wertGefunden(99, systemOut)
            , "Wenn kein Redo moeglich, soll auch kein "
                + "Redo am Anfang angeboten werden");
    }  
    
    @Test
    public void testKeinRedoOhneUndo() throws Exception {
        this.add(21);
        this.add(21);
        this.inMemory();
        this.add(21);
        this.addMemory();

        String systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.reset();
        }); 
        Assertions.assertFalse(this.wertGefunden(99, systemOut)
            , "Wenn kein Redo moeglich, soll auch kein "
                + "Redo im Verlauf angeboten werden");
    }   
    
    @Test
    public void testResetOhneVorherigenSchrittDannUndo() throws Exception {
        this.reset();
         // erst bei/vor zweiter Aktion Reset sichtbar
        String systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.reset();
        });
        Assertions.assertTrue(this.wertGefunden(98, systemOut)
            , "Undo fuer Reset fehlt");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.undo();
        }); 
        erwartet(0,0, systemOut);

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.undo();
        }); 
        this.keinUndo();
    }

    @Test
    public void testAddDannUndo() throws Exception {
        this.add(21);

        String systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.add(21);
        }); 
        Assertions.assertTrue(this.wertGefunden(98, systemOut)
            , "Undo fehlt");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.undo();
        }); 
        erwartet(0,21, systemOut);
        Assertions.assertTrue(this.wertGefunden(98, systemOut)
            , "Undo fehlt");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.undo();
        }); 
        erwartet(0,0, systemOut);
        this.keinUndo();
    }
    
    @Test
    public void testSubstractDannUndo() throws Exception {
        this.add(42);
        this.add(42);
      
        String systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.subtract(42);
        });
        Assertions.assertTrue(this.wertGefunden(98, systemOut)
            , "Undo fehlt");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.undo();
        }); 
        erwartet(0,84, systemOut);
        Assertions.assertTrue(this.wertGefunden(98, systemOut)
            , "Undo fehlt");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.undo();
        }); 
        erwartet(0,42, systemOut);
        Assertions.assertTrue(this.wertGefunden(98, systemOut)
            , "Undo fehlt");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.undo();
        }); 
        erwartet(0,0, systemOut);
        this.keinUndo();
    }
    
    @Test
    public void testToMemoryDannUndo() throws Exception {
        this.add(21);
        this.inMemory();

        String systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.add(21);
        }); 
        Assertions.assertTrue(this.wertGefunden(98, systemOut)
            , "Undo fehlt");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.undo();
        }); 
        erwartet(21,21, systemOut);
        Assertions.assertTrue(this.wertGefunden(98, systemOut)
            , "Undo fehlt");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.undo();
        }); 
        erwartet(0,21, systemOut);
        Assertions.assertTrue(this.wertGefunden(98, systemOut)
            , "Undo fehlt");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.undo();
        }); 
        erwartet(0,0, systemOut);
        this.keinUndo();
    }
    
    @Test
    public void testAddMemoryDannUndo() throws Exception {
        this.add(21);
        this.inMemory();
        this.add(21);
        
        String systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.addMemory();
        });
        Assertions.assertTrue(this.wertGefunden(98, systemOut)
            , "Undo fehlt");
        
        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.undo();
        }); 
        erwartet(21,42, systemOut);
        Assertions.assertTrue(this.wertGefunden(98, systemOut)
            , "Undo fehlt");
        
        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.undo();
        }); 
        erwartet(21,21, systemOut);
        Assertions.assertTrue(this.wertGefunden(98, systemOut)
            , "Undo fehlt");
        
        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.undo();
        }); 
        erwartet(0,21, systemOut);
        Assertions.assertTrue(this.wertGefunden(98, systemOut)
            , "Undo fehlt");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.undo();
        });
        erwartet(0,0, systemOut);
        this.keinUndo();
    }
    
    @Test
    public void testSubtractMemoryDannUndo() throws Exception {
        this.add(21);
        this.inMemory();
        this.add(21);

        String systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.subtractMemory();
        });
        Assertions.assertTrue(this.wertGefunden(98, systemOut)
            , "Undo fehlt");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.undo();
        });
        erwartet(21,42, systemOut);
        Assertions.assertTrue(this.wertGefunden(98, systemOut)
            , "Undo fehlt");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.undo();
        });
        erwartet(21,21, systemOut);
        Assertions.assertTrue(this.wertGefunden(98, systemOut)
            , "Undo fehlt");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.undo();
        });
        erwartet(0,21, systemOut);
        Assertions.assertTrue(this.wertGefunden(98, systemOut)
            , "Undo fehlt");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.undo();
        });
        erwartet(0,0, systemOut);
        this.keinUndo();
    }
    
    @Test
    public void testResetDannUndo() throws Exception {
        this.add(21);
        this.inMemory();
        this.add(21);
        this.addMemory();

        String systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.reset();
        });
        Assertions.assertTrue(this.wertGefunden(98, systemOut)
            , "Undo fehlt");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.undo();
        });
        erwartet(21,63, systemOut);
        Assertions.assertTrue(this.wertGefunden(98, systemOut)
            , "Undo fehlt");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.undo();
        });
        erwartet(21,42, systemOut);
        Assertions.assertTrue(this.wertGefunden(98, systemOut)
            , "Undo fehlt");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.undo();
        });
        erwartet(21,21, systemOut);
        Assertions.assertTrue(this.wertGefunden(98, systemOut)
            , "Undo fehlt");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.undo();
        });
        erwartet(0,21, systemOut);
        Assertions.assertTrue(this.wertGefunden(98, systemOut)
            , "Undo fehlt");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.undo();
        });
        erwartet(0,0, systemOut);
        this.keinUndo();
    }   
 
    @Test
    public void testResetOhneVorherigenSchrittDannUndoDannRedo() throws Exception {
        this.reset();
        // erst bei/vor zweiter Aktion Reset sichtbar
        String systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.reset();
        });
        Assertions.assertFalse(this.wertGefunden(99, systemOut)
            , "Kein Redo erwartet");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.undo();
        });
        erwartet(0,0, systemOut);
        Assertions.assertFalse(this.wertGefunden(99, systemOut)
            , "Kein Redo erwartet");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.undo();
        });
        systemOut = this.keinUndo();
        Assertions.assertTrue(this.wertGefunden(99, systemOut)
            , "Redo erwartet");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.redo();
        });
        erwartet(0,0, systemOut);
        Assertions.assertTrue(this.wertGefunden(99, systemOut)
            , "Redo erwartet");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.redo();
        });
        erwartet(0,0, systemOut);
        this.keinRedo();
    }

    @Test
    public void testAddDannUndoDannRedo() throws Exception {
        this.add(21);
        
        String systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.add(21);
        });
        Assertions.assertTrue(this.wertGefunden(98, systemOut)
            , "Undo fehlt");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.undo();
        });
        erwartet(0,21, systemOut);
        Assertions.assertTrue(this.wertGefunden(98, systemOut)
            , "Undo fehlt");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.undo();
        });
        erwartet(0,0, systemOut);

        systemOut = this.keinUndo();
        Assertions.assertTrue(this.wertGefunden(99, systemOut)
            , "Redo erwartet");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.redo();
        });
        erwartet(0,21, systemOut);
        Assertions.assertTrue(this.wertGefunden(99, systemOut)
            , "Redo erwartet");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.redo();
        });
        erwartet(0,42, systemOut);
        this.keinRedo();
    }
    
    @Test
    public void testSubstractDannUndoDannRedo() throws Exception {
        this.add(42);
        this.add(42);

        String systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.subtract(42);
        });
        Assertions.assertTrue(this.wertGefunden(98, systemOut)
            , "Undo fehlt");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.undo();
        });
        erwartet(0,84, systemOut);
        Assertions.assertTrue(this.wertGefunden(98, systemOut)
            , "Undo fehlt");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.undo();
        });
        erwartet(0,42, systemOut);
        Assertions.assertTrue(this.wertGefunden(98, systemOut)
            , "Undo fehlt");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.undo();
        });
        erwartet(0,0, systemOut);

        systemOut = this.keinUndo();
        Assertions.assertTrue(this.wertGefunden(99, systemOut)
            , "Redo erwartet");
        
        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.redo();
        });
        erwartet(0,42, systemOut);
        Assertions.assertTrue(this.wertGefunden(99, systemOut)
            , "Redo erwartet");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.redo();
        });
        erwartet(0,84, systemOut);
        Assertions.assertTrue(this.wertGefunden(99, systemOut)
            , "Redo erwartet");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.redo();
        });
        erwartet(0,42, systemOut);
        this.keinRedo();
    }
    
    @Test
    public void testToMemoryDannUndoDannRedo() throws Exception {
        this.add(21);
        this.inMemory();

        String systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.add(21);
        });
        Assertions.assertTrue(this.wertGefunden(98, systemOut)
            , "Undo fehlt");
        
        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.undo();
        });
        erwartet(21,21, systemOut);
        Assertions.assertTrue(this.wertGefunden(98, systemOut)
            , "Undo fehlt");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.undo();
        });
        erwartet(0,21, systemOut);
        Assertions.assertTrue(this.wertGefunden(98, systemOut)
            , "Undo fehlt");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.undo();
        });
        erwartet(0,0, systemOut);

        systemOut = this.keinUndo();
        Assertions.assertTrue(this.wertGefunden(99, systemOut)
            , "Redo erwartet");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.redo();
        });
        erwartet(0,21, systemOut);
        Assertions.assertTrue(this.wertGefunden(99, systemOut)
            , "Redo erwartet");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.redo();
        });
        erwartet(21,21, systemOut);
        Assertions.assertTrue(this.wertGefunden(99, systemOut)
            , "Redo erwartet");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.redo();
        });
        erwartet(21,42, systemOut);
        this.keinRedo();
    }
    
    @Test
    public void testAddMemoryDannUndoDannRedo() throws Exception {
        this.add(21);
        this.inMemory();
        this.add(21);

        String systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.addMemory();
        });
        Assertions.assertTrue(this.wertGefunden(98, systemOut)
            , "Undo fehlt");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.undo();
        });
        erwartet(21,42, systemOut);
        Assertions.assertTrue(this.wertGefunden(98, systemOut)
            , "Undo fehlt");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.undo();
        });
        erwartet(21,21, systemOut);
        Assertions.assertTrue(this.wertGefunden(98, systemOut)
            , "Undo fehlt");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.undo();
        });
        erwartet(0,21, systemOut);
        Assertions.assertTrue(this.wertGefunden(98, systemOut)
            , "Undo fehlt");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.undo();
        });
        erwartet(0,0, systemOut);

        systemOut = this.keinUndo();
        Assertions.assertTrue(this.wertGefunden(99, systemOut)
            , "Redo erwartet");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.redo();
        });
        erwartet(0,21, systemOut);
        Assertions.assertTrue(this.wertGefunden(99, systemOut)
            , "Redo erwartet");
        
        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.redo();
        });
        erwartet(21,21, systemOut);
        Assertions.assertTrue(this.wertGefunden(99, systemOut)
            , "Redo erwartet");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.redo();
        });
        erwartet(21,42, systemOut);
        Assertions.assertTrue(this.wertGefunden(99, systemOut)
            , "Redo erwartet");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.redo();
        });
        erwartet(21,63, systemOut);
        this.keinRedo();
    }
    
    @Test
    public void testSubtractMemoryDannUndoDannRedo() throws Exception {
        this.add(21);
        this.inMemory();
        this.add(21);

        String systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.subtractMemory();
        });
        Assertions.assertTrue(this.wertGefunden(98, systemOut)
            , "Undo fehlt");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.undo();
        });
        erwartet(21,42, systemOut);
        Assertions.assertTrue(this.wertGefunden(98, systemOut)
            , "Undo fehlt");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.undo();
        });
        erwartet(21,21, systemOut);
        Assertions.assertTrue(this.wertGefunden(98, systemOut)
            , "Undo fehlt");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.undo();
        });
        erwartet(0,21, systemOut);
        Assertions.assertTrue(this.wertGefunden(98, systemOut)
            , "Undo fehlt");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.undo();
        });
        erwartet(0,0, systemOut);

        systemOut = this.keinUndo();
        Assertions.assertTrue(this.wertGefunden(99, systemOut)
            , "Redo erwartet");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.redo();
        });
        erwartet(0,21, systemOut);
        Assertions.assertTrue(this.wertGefunden(99, systemOut)
            , "Redo erwartet");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.redo();
        });
        erwartet(21,21, systemOut);
        Assertions.assertTrue(this.wertGefunden(99, systemOut)
            , "Redo erwartet");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.redo();
        });
        erwartet(21,42, systemOut);
        Assertions.assertTrue(this.wertGefunden(99, systemOut)
            , "Redo erwartet");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.redo();
        });
        erwartet(21,21, systemOut);
        this.keinRedo();
    }
    
    @Test
    public void testResetDannUndoDannRedo() throws Exception {
        this.add(21);
        this.inMemory();
        this.add(21);
        this.addMemory();

        String systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.reset();
        });
        Assertions.assertTrue(this.wertGefunden(98, systemOut)
            , "Undo fuer Reset fehlt");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.undo();
        });
        erwartet(21,63, systemOut);
        Assertions.assertTrue(this.wertGefunden(98, systemOut)
            , "Undo fuer Reset fehlt");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.undo();
        });
        erwartet(21,42, systemOut);
        Assertions.assertTrue(this.wertGefunden(98, systemOut)
            , "Undo fuer Reset fehlt");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.undo();
        });
        erwartet(21,21, systemOut);
        Assertions.assertTrue(this.wertGefunden(98, systemOut)
            , "Undo fuer Reset fehlt");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.undo();
        });
        erwartet(0,21, systemOut);
        Assertions.assertTrue(this.wertGefunden(98, systemOut)
            , "Undo fuer Reset fehlt");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.undo();
        });
        erwartet(0,0, systemOut);

        systemOut = this.keinUndo();
        Assertions.assertTrue(this.wertGefunden(99, systemOut)
            , "Redo erwartet");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.redo();
        });
        erwartet(0,21, systemOut);
        Assertions.assertTrue(this.wertGefunden(99, systemOut)
            , "Redo erwartet");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.redo();
        });
        erwartet(21,21, systemOut);
        Assertions.assertTrue(this.wertGefunden(99, systemOut)
            , "Redo erwartet");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.redo();
        });
        erwartet(21,42, systemOut);
        Assertions.assertTrue(this.wertGefunden(99, systemOut)
            , "Redo erwartet");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.redo();
        });
        erwartet(21,63, systemOut);
        Assertions.assertTrue(this.wertGefunden(99, systemOut)
            , "Redo erwartet");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.redo();
        });
        erwartet(0,0, systemOut);

        systemOut = this.keinRedo();
        Assertions.assertTrue(this.wertGefunden(98, systemOut)
            , "Undo fuer Redo fehlt");

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.undo();
        });
        erwartet(21,63, systemOut);
    }
    
    @Test
    public void testResetDannUndoRedoUndoRedo() throws Exception {
        this.add(21);

        String systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.add(21);
        });
        erwartet(0,42, systemOut);

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.undo();
        });
        erwartet(0,21, systemOut);

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.redo();
        });
        erwartet(0,42, systemOut);

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.undo();
        });
        erwartet(0,21, systemOut);

        systemOut = SystemLambda.tapSystemOutNormalized(() -> {
          this.redo();
        });
        erwartet(0,42, systemOut);
    }
        
}
