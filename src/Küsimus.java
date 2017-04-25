public class Küsimus {
    private String küsimus;
    private String[] vastused; //length=3; esimene vastus õige
    
	public Küsimus(String küsimus, String[] vastused) {
		super();
		this.küsimus = küsimus;
		this.vastused = vastused;
	}
    
    public String getKüsimus() {
    	return küsimus;
    }
    
    public String[] getVastused() {
    	return vastused;
    }
}