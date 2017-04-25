
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Peaklass extends Application {
	Mälumäng m;

	public static List<Teema> loeKüsimused(String failinimi) throws IOException {
		try (BufferedReader sisend = new BufferedReader(new InputStreamReader(new FileInputStream(failinimi), "UTF-8"))) {
			List<Teema> teemad = new ArrayList<>();
			String rida;
			while ((rida = sisend.readLine()) != null) {
				String[] osad = rida.split(";");
				if (rida.charAt(0) == 'T') {
					teemad.add(new Teema(osad[1]));
				}
				else {
					Küsimus k = new Küsimus(osad[1], new String[] {osad[2], osad[3], osad[4]});
					teemad.get(teemad.size() - 1).lisaKüsimus(k);
				}
			}
			return teemad;
		}
	}

	private void salvestaTulemus(String nimi) throws IOException {
		try (BufferedWriter väljund = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("edetabel.txt", true), "UTF-8"))) {
			väljund.write("\n" + nimi + ";" + m.getTeema().getPealkiri() + ";" + m.getÕiged() + "/" + m.getKüsimusteArv());
		}
	}

	public static List<String> loeTulemused() throws IOException { // tagastab top 7 tulemust Stringina ja järjestatult
		try (BufferedReader sisend = new BufferedReader(new InputStreamReader(new FileInputStream("edetabel.txt"), "UTF-8"))) {
			
			class Tulemus implements Comparable<Tulemus> { //eraldi klass et oleks lihtsam järjestada ja Stringiks teisendada
				private double skoor;
				private String nimi;
				private String teema;
				private String tulemus;
				
				public Tulemus(String rida) {
					super();
					String[] osad = rida.split(";");
					this.nimi = osad[0];
					this.teema = osad[1];
					this.tulemus = osad[2];
					String[] tulemusOsad = osad[2].split("/");
					this.skoor = Integer.parseInt(tulemusOsad[0]) / (double)Integer.parseInt(tulemusOsad[1]);
				}
				
				@Override
				public int compareTo(Tulemus teine) {
					if (this.getSkoor() > teine.getSkoor())
						return 1;
					if (this.getSkoor() < teine.getSkoor())
						return -1;
					return 0;
				}
				
				public double getSkoor() {
					return skoor;
				}
				
				public String getTeema() {
					return teema;
				}
				
				public String getNimi() {
					return nimi;
				}
				
				public String getTulemus() {
					return tulemus;
				}
			}
			
			String rida;
			List<Tulemus> tulemused = new ArrayList<>();
			sisend.readLine(); //esimene rida tühi
			while ((rida = sisend.readLine()) != null) {
				Tulemus t = new Tulemus(rida);
				tulemused.add(t);
			}
			Collections.sort(tulemused, Collections.reverseOrder());
			List<Tulemus> tulemusedTop;
			if (tulemused.size() < 7) {
				tulemusedTop = tulemused.subList(0, tulemused.size());
			}
			else {
				tulemusedTop = tulemused.subList(0, 7);
			}
			List<String> tulemusedTopString = new ArrayList<>();
			for (int i = 0; i < tulemusedTop.size(); i++) {
				Tulemus t = tulemusedTop.get(i);
				tulemusedTopString.add((i + 1) + ".\t" + t.getNimi() + "\t\t" + t.getTeema() + "\t\t" + t.getTulemus()); 
			}
			return tulemusedTopString;
		}
	}

	@Override
	public void start(Stage peaLava) throws IOException  {
		List<Teema> teemad = loeKüsimused("küsimused.txt");
		int algLaius = 700;
		int algKõrgus = 500;

		Group juurVälis = new Group();
		Pane juur = new Pane();
		juurVälis.getChildren().add(juur);
		juur.setPrefSize(algLaius, algKõrgus);
		Scene stseen = new Scene(juurVälis, algLaius + 100, algKõrgus, Color.SNOW);
		peaLava.setMinWidth(algLaius + 40);
		peaLava.setMinHeight(algKõrgus + 40);
		peaLava.setTitle("Mälumäng");
		peaLava.setScene(stseen);

		List<Button> nupud = new ArrayList<>();
		List<Label> sildid = new ArrayList<>();
		
		//ERINEVAD VAATED
		
		//Peamenüü
		
		Label lPealkiri = new Label("Mälumäng");
		Button bAlusta = new Button("Alusta");
		Button bEdetabel = new Button("Edetabel");
		Button bVälju = new Button("Välju");
		HBox hPeaNupud = new HBox(bAlusta, bEdetabel, bVälju);
		Group gPeamenüü = new Group(lPealkiri, hPeaNupud);

		hPeaNupud.setPrefWidth(algLaius);
		hPeaNupud.setAlignment(Pos.CENTER);
		hPeaNupud.setSpacing(70);
		hPeaNupud.setLayoutY(350);
		lPealkiri.setFont(new Font(36));
		lPealkiri.setLayoutY(70);

		//Teema valiku menüü
		
		Label lValiTeema = new Label("Vali teema");
		HBox hTeemaNupud = new HBox();
		List<Button> teemaNupud = new ArrayList<>();
		for (Teema t : teemad) {  //Teemade nuppude arv vastavalt vajadusele
			Button b = new Button(t.getPealkiri());
			nupud.add(b);
			teemaNupud.add(b);
			hTeemaNupud.getChildren().add(b);
		}
		Group gTeemaValik = new Group(lValiTeema, hTeemaNupud);

		hTeemaNupud.setPrefWidth(algLaius);
		hTeemaNupud.setAlignment(Pos.CENTER);
		hTeemaNupud.setSpacing(30);
		hTeemaNupud.setLayoutY(350);
		lValiTeema.setFont(new Font(30));
		lValiTeema.setLayoutY(80);

		//Küsimuse vaade
		
		Label lKüsimus = new Label();
		Label lJärg = new Label();
		Label lVastus = new Label();
		Button bValik1 = new Button();
		Button bValik2 = new Button();
		Button bValik3 = new Button();
		HBox hVastusteNupud = new HBox(bValik1, bValik2, bValik3);
		Group gKüsimus = new Group(lKüsimus, lJärg, lVastus, hVastusteNupud);
		List<Button> vastuseNupud = new ArrayList<>(Arrays.asList(bValik1, bValik2, bValik3));

		hVastusteNupud.setPrefWidth(algLaius);
		hVastusteNupud.setAlignment(Pos.CENTER);
		hVastusteNupud.setSpacing(50);
		hVastusteNupud.setLayoutY(350);
		lKüsimus.setFont(new Font(20));
		lKüsimus.setLayoutY(110);
		lKüsimus.setWrapText(true);
		lJärg.setFont(new Font(20));
		lJärg.setLayoutY(70);
		lVastus.setFont(new Font(26));
		lVastus.setLayoutY(200);

		//Tulemus
		
		Label lTulemus = new Label();
		Label lSisesta = new Label("Sisesta oma nimi:");
		TextField tNimi = new TextField();
		Button bSalvesta = new Button("Salvesta");
		HBox hTulemus = new HBox(lSisesta, tNimi);
		Group gTulemus = new Group(lTulemus, hTulemus, bSalvesta);

		lSisesta.setFont(new Font(18));
		lTulemus.setFont(new Font(24));
		lTulemus.setLayoutY(100);
		hTulemus.setPrefWidth(algLaius);
		hTulemus.setAlignment(Pos.CENTER);
		hTulemus.setSpacing(10);
		hTulemus.setLayoutY(200);
		bSalvesta.setLayoutY(350);
		bSalvesta.setLayoutX(algLaius / 2 - 60);

		//Edetabel
		
		Label lEdetabel = new Label("Top 7 tulemust");
		Button bTagasi = new Button("Tagasi");
		Group gEdetabel = new Group(lEdetabel, bTagasi);
		Label[] edetabelTulemused = new Label[10];
		for (int i = 0; i < 7; i++) {
			Label l = new Label((i + 1) + ".\t-----\t  ------\t-------");
			l.setLayoutY(100 + i * 40);
			l.setFont(new Font(18));
			edetabelTulemused[i] = l;
			gEdetabel.getChildren().add(l);
		}

		lEdetabel.setFont(new Font(24));
		lEdetabel.setLayoutY(40);
		bTagasi.setLayoutY(400);
		bTagasi.setPrefWidth(100);
		bTagasi.setPrefHeight(50);
		bTagasi.setFont(new Font(16));
		bTagasi.setLayoutX(algLaius / 2 - 50);

		
		
		
		juur.getChildren().addAll(gPeamenüü, gTeemaValik, gKüsimus, gTulemus, gEdetabel);

		
		for (Node n : juur.getChildren()) {
			n.setVisible(false);
		}
		gPeamenüü.setVisible(true);

		nupud.addAll(Arrays.asList(bAlusta, bEdetabel, bVälju, bTagasi, bValik1, bValik2, bValik3, bSalvesta));
		for (Button nupp : nupud) {
			if (nupp != bTagasi) {
				nupp.setPrefWidth(120);
				nupp.setPrefHeight(80);
				nupp.setFont(new Font(16));
				nupp.setWrapText(true);
			}
		}

		sildid.addAll(Arrays.asList(lPealkiri, lValiTeema, lKüsimus, lJärg, lVastus, lTulemus, lSisesta, lEdetabel)); //ei sisalda edetabeli väärtuste silte
		for (Label l : sildid) {
			if (l != lSisesta) {
				l.setPrefWidth(algLaius);
				l.setAlignment(Pos.CENTER);
				l.setTextAlignment(TextAlignment.CENTER);
			}
		}
		
		//FUNKTSIONAALSUS
		
		peaLava.widthProperty().addListener(new ChangeListener<Number>() { //Graafilise liidese põhiosa püsib üleval keskel, kui akna suurust muudetakse
			@Override
			public void changed(ObservableValue<? extends Number> vaadeldav, Number vana, Number uus) {
				juur.setLayoutX((uus.doubleValue() - algLaius) / 2 - 10);
			}
		});

		bAlusta.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent ae) {
				gPeamenüü.setVisible(false);
				gTeemaValik.setVisible(true);
			}
		});
		
		bTagasi.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent ae) {
				gEdetabel.setVisible(false);
				gPeamenüü.setVisible(true);
			}
		});

		bEdetabel.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent ae) {
				try {
					List<String> tulemused = loeTulemused();
					for (int i = 0; i < tulemused.size(); i++) {
						edetabelTulemused[i].setText(tulemused.get(i));
					}
				}
				catch (IOException e) {
					throw new RuntimeException(e);
				}
				gPeamenüü.setVisible(false);
				gEdetabel.setVisible(true);
			}
		});

		for (Button b : teemaNupud) {
			b.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent ae) {
					String pealkiri = ((Button)ae.getTarget()).getText();
					for (Teema t : teemad) {
						if (t.getPealkiri().equals(pealkiri)) {
							m = new Mälumäng(t, 10); //Kümme küsimust ühe mängu jooksul; nõuab ka et vähemalt niipalju küsimusi oleks antud teemas
							break;
						}
					}
					gTeemaValik.setVisible(false);
					gKüsimus.setVisible(true);
					//Alusta mänguga
					lVastus.setText("");
					lJärg.setText("Küsimus " + "1/" + m.getKüsimusteArv());
					lKüsimus.setText(m.getKüsimuseTekst());
					String[] vastused = m.küsimuseVastused();
					if (Math.random() < (1 / 3.0)) {
						bValik1.setText(vastused[0]);
						if (Math.random() < 0.5) {
							bValik2.setText(vastused[1]);
							bValik3.setText(vastused[2]);
						}
						else {
							bValik3.setText(vastused[1]);
							bValik2.setText(vastused[2]);
						}
					}
					else if (Math.random() < 0.5) {
						bValik2.setText(vastused[0]);
						if (Math.random() < 0.5) {
							bValik1.setText(vastused[1]);
							bValik3.setText(vastused[2]);
						}
						else {
							bValik3.setText(vastused[1]);
							bValik1.setText(vastused[2]);
						}
					}
					else {
						bValik3.setText(vastused[0]);
						if (Math.random() < 0.5) {
							bValik1.setText(vastused[1]);
							bValik2.setText(vastused[2]);
						}
						else {
							bValik2.setText(vastused[1]);
							bValik1.setText(vastused[2]);
						}
					}
				}
			});
		}

		for (Button b : vastuseNupud) {
			b.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent ae) {
					String pealkiri = ((Button)ae.getTarget()).getText();
					boolean viimane = m.getJärg() == m.getKüsimusteArv();
					if (pealkiri.equals(m.getÕigeVastus())) {
						m.õigeVastus();
						lVastus.setText("Õige");
					}
					else {
						lVastus.setText("Vale");
					}
					bValik1.setDisable(true);
					bValik2.setDisable(true);
					bValik3.setDisable(true);
					//Ootab ühe sekundi enne edasi liikumist
					Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1000), new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent ae) {
							if (!viimane) {
								m.järgmineKüsimus();
								lVastus.setText("");
								lJärg.setText("Küsimus " + m.getJärg() + "/" + m.getKüsimusteArv());
								lKüsimus.setText(m.getKüsimuseTekst());
								String[] vastused = m.küsimuseVastused();
								if (Math.random() < (1 / 3.0)) {
									bValik1.setText(vastused[0]);
									if (Math.random() < 0.5) {
										bValik2.setText(vastused[1]);
										bValik3.setText(vastused[2]);
									}
									else {
										bValik3.setText(vastused[1]);
										bValik2.setText(vastused[2]);
									}
								}
								else if (Math.random() < 0.5) {
									bValik2.setText(vastused[0]);
									if (Math.random() < 0.5) {
										bValik1.setText(vastused[1]);
										bValik3.setText(vastused[2]);
									}
									else {
										bValik3.setText(vastused[1]);
										bValik1.setText(vastused[2]);
									}
								}
								else {
									bValik3.setText(vastused[0]);
									if (Math.random() < 0.5) {
										bValik1.setText(vastused[1]);
										bValik2.setText(vastused[2]);
									}
									else {
										bValik2.setText(vastused[1]);
										bValik1.setText(vastused[2]);
									}
								}
							}
							else {
								gKüsimus.setVisible(false);
								gTulemus.setVisible(true);
								lTulemus.setText("Tulemus: " + m.getÕiged() + "/" + m.getKüsimusteArv());
							}
							bValik1.setDisable(false);
							bValik2.setDisable(false);
							bValik3.setDisable(false);
						}
					}));
					timeline.play();
				}
			});
		}

		bSalvesta.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent ae) {
				try {
					salvestaTulemus(tNimi.getText());
				}
				catch (IOException e) {
					throw new RuntimeException(e);
				}
				gTulemus.setVisible(false);
				gPeamenüü.setVisible(true);
			}
		});
		
		bVälju.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent ae) {
				peaLava.close();
			}
		});

		peaLava.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
