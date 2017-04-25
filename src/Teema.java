import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Teema {
    private String pealkiri;
    private List<Küsimus> küsimused = new ArrayList<>();
    
    public Teema(String pealkiri) {
    	super();
    	this.pealkiri = pealkiri;
    }
    
    public void lisaKüsimus(Küsimus k) {
    	küsimused.add(k);
    }
    
    public void segaKüsimused() {
    	Collections.shuffle(küsimused);
    }
    
    public String getPealkiri() {
    	return pealkiri;
    }
    
    public List<Küsimus> getKüsimused() {
    	return küsimused;
    }
}