public class Mälumäng {
	public Teema teema;
	private Küsimus praegune;
	private int järg;
	private int õiged;
	private int küsimusteArv;
	
	public Mälumäng(Teema teema, int küsimusteArv) {
		super();
		if (teema.getKüsimused().size() < küsimusteArv)
			throw new RuntimeException("Teema " + teema.getPealkiri() + " kohta pole nii palju küsimusi");
		this.teema = teema;
		this.teema.segaKüsimused();
		this.küsimusteArv = küsimusteArv;
		järg = 1;
		õiged = 0;
		praegune = teema.getKüsimused().get(0);
	}
	
	public void järgmineKüsimus() {
		järg++;
		praegune = teema.getKüsimused().get(järg - 1);
	}
	
	public String getKüsimuseTekst() {
		return praegune.getKüsimus();
	}
	
	public String[] küsimuseVastused() {
		return praegune.getVastused();
	}
	
	public String getÕigeVastus() {
		return praegune.getVastused()[0];
	}
	
	public void õigeVastus() {
		õiged++;
	}
	
	public int getKüsimusteArv() {
		return küsimusteArv;
	}
	
	public int getJärg() {
		return järg;
	}
	
	public int getÕiged() {
		return õiged;
	}
	
	public Teema getTeema() {
		return teema;
	}
}
