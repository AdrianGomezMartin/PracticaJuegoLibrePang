package pantallas;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import base.PanelJuego;
import base.Pantalla;
import base.Sprite;

public class PantallaLevel2 implements Pantalla {
	/**
	 * Constantes de cada tipo de bola
	 */
	final private static int ANCHO_BOLA_GRANDE = 200;
	final private static int ALTO_BOLA_GRANDE = 200;
	final private static int MIN_VELOCIDAD_BOLA_GRANDE = 8;
	final private static int VELOCIDAD_BOLA_GRANDE = 12;
	final private static int VELOCIDADX = 10;
	final private static int PUNTUACION_BOLA_GRANDE = 10;

	final private static int ANCHO_BOLA_MEDIA = 100;
	final private static int ALTO_BOLA_MEDIA = 100;
	final private static int MIN_VELOCIDAD_BOLA_MEDIA = 12;
	final private static int VELOCIDAD_BOLA_MEDIA = 16;
	final private static int PUNTUACION_BOLA_MEDIA = 20;

	final private static int ALTO_BOLA_PEQUEÑA = 50;
	final private static int ANCHO_BOLA_PEQUEÑA = 50;
	final private static int MIN_VELOCIDAD_BOLA_PEQUEÑA = 16;
	final private static int VELOCIDAD_BOLA_PEQUEÑA = 20;
	final private static int PUNTUACION_BOLA_PEQUEÑA = 30;

	final private static int ALTO_BOLA_MINI = 25;
	final private static int ANCHO_BOLA_MINI = 25;
	final private static int MIN_VELOCIDAD_BOLA_MINI = 21;
	final private static int VELOCIDAD_BOLA_MINI = 24;
	final private static int PUNTUACION_BOLA_MINI = 40;

	final private static int VELOCIDAD_DISPARO = -30;
	final private static int ANCHO_DISPARO = 20;
	final private static int VELOCIDAD_DISPARO_ESPECIAL = -28;
	final private static int ALTO_PERSONAJE = 120;
	final private static int ANCHO_PERSONAJE = 60;
	String rutasPersonaje[] = { "Imagenes/personaje.png", "Imagenes/ataque.png", "Imagenes/correrder.png",
			"Imagenes/correrizq.png" };
	final private static Font fuentePuntuacion = new Font("Think Free", Font.BOLD, 26);
	private int puntuacion;
	BufferedImage imagenOriginal;
	Image imagenReescalada;
	BufferedImage atacando;
	ArrayList<Sprite> bolas;
	Sprite personaje;
	Sprite disparo;
	Sprite disparoFijo;
	PanelJuego panelJuego;
	boolean aplastado = false;
	boolean disparoEspecial = false;
	int posXAnterior;
	int bolasRotas = 0;
	int bolasRotasPorArponFijo = 0;

	public PantallaLevel2(PanelJuego panelJuego) {
		this.panelJuego = panelJuego;
	}

	/**
	 * Metodo que inicializa la pantalla de juego
	 */
	@Override
	public void inicializarPantalla() {
		bolas = new ArrayList<Sprite>();
		try {
			imagenOriginal = ImageIO.read(new File("Imagenes/fondolevel2.png"));

		} catch (IOException e) {
			e.printStackTrace();
		}

		int posX = 100;
		int posY = 100;
		AddBolaGrande(posX, posY);
		posX += 300;
		AddBolaGrande(posX, posY);
		personaje = new Sprite(ANCHO_PERSONAJE, ALTO_PERSONAJE, panelJuego.getWidth() / 2,
				panelJuego.getHeight() - (ALTO_PERSONAJE), 0, 0, rutasPersonaje);

		reescalarImagen();

	}

	/**
	 * Metodo que pinta por pantalla el panel de Juego
	 */
	@Override
	public void pintarPantalla(Graphics g) {
		rellenarFondo(g);
		for (Sprite sprite : bolas) {
			sprite.pintarSpriteEnMundo(g);
		}
		if (disparo != null) {
			disparo.pintarSpriteEnMundo(g);
		}
		if (personaje != null)
			personaje.pintarSpriteEnMundo(g);
		if (disparoFijo != null) {
			disparoFijo.pintarSpriteEnMundo(g);
		}
		pintarPuntuacion(g);

	}

	@Override
	public void ejecutarFrame() {
		moverSprites();
		comprobarDisparos();
		comprobarMuerte();
		comprobarVictoria();
		comprobarDisparoFijo();

	}

	/**
	 * Comprueba que el personaje siga al raton sin salirse de la pantalla
	 */
	@Override
	public void moverRaton(MouseEvent e) {
		if (personaje != null && !aplastado) {
			if (e.getX() < panelJuego.getWidth() - (ANCHO_PERSONAJE / 2) && e.getX() >= 0) {
				personaje.setPosX(e.getX());
				personaje.setPosY(panelJuego.getHeight() - (ALTO_PERSONAJE));
				moverPersonaje(e);
			}
		}

	}

	/**
	 * Genero un disparo y le asigno al personaje la imagen de disparar
	 */
	@Override
	public void pulsarRaton(MouseEvent e) {
		if (disparo == null && !aplastado) {
			if (SwingUtilities.isLeftMouseButton(e)) {

				personaje.actualizarBuffer(1);
				personaje.setPosY(panelJuego.getHeight() - personaje.getAlto());
				disparo = new Sprite(ANCHO_DISPARO, personaje.getPosY(), e.getX() - (ANCHO_PERSONAJE / 4),
						personaje.getPosY(), 0, VELOCIDAD_DISPARO, "Imagenes/arpon.png");
			}
			if (disparoFijo == null) {
				if (disparoEspecial) {
					if (SwingUtilities.isRightMouseButton(e)) {
						personaje.actualizarBuffer(1);
						personaje.setPosY(panelJuego.getHeight() - personaje.getAlto());
						disparoFijo = new Sprite(ANCHO_DISPARO, panelJuego.getHeight(),
								e.getX() - (ANCHO_PERSONAJE / 4), panelJuego.getHeight(), 0, VELOCIDAD_DISPARO_ESPECIAL,
								"Imagenes/arponFijo.png");

					}
				}
			}
		}

	}

	/**
	 * Reescalo el fondo y controlo la altura del personaje
	 */
	@Override
	public void redimensionarPantalla(ComponentEvent e) {
		reescalarImagen();
		if (personaje != null)
			personaje.setPosY(panelJuego.getHeight() - (ALTO_PERSONAJE));

	}
	public int getPuntuacion() {
		return puntuacion;
	}

	public void setPuntuacion(int puntuacion) {
		this.puntuacion = puntuacion;
	}
	/**
	 * Metodo que comprueba si los disparon rompen alguna bola en el camino
	 * incrementa la puntuacion del jugador y genera nuevas bolas si es necesario
	 */
	private void comprobarDisparos() {

		for (int i = 0; i < bolas.size() && disparo != null; i++) {
			if (disparo.colisionan(bolas.get(i))) {
				bolasRotas++;
				int posX = bolas.get(i).getPosX();
				int posY = bolas.get(i).getPosY();

				switch (bolas.get(i).getAncho()) {
				case ANCHO_BOLA_GRANDE:
					setPuntuacion(getPuntuacion() + PUNTUACION_BOLA_GRANDE);
					bolas.remove(i);
					for (int j = 0; j < 2; j++)
						AddBolaMedia(posX + (j * ANCHO_BOLA_MEDIA), posY);
					break;
				case ANCHO_BOLA_MEDIA:
					setPuntuacion(getPuntuacion() + PUNTUACION_BOLA_MEDIA);
					bolas.remove(i);
					for (int j = 0; j < 3; j++)
						AddBolaSmall(posX + (j * ANCHO_BOLA_PEQUEÑA), posY);
					break;
				case ANCHO_BOLA_PEQUEÑA:
					setPuntuacion(getPuntuacion() + PUNTUACION_BOLA_PEQUEÑA);
					bolas.remove(i);
					for (int j = 0; j < 4; j++)
						AddBolaMini(posX + (j * ANCHO_BOLA_MINI), posY);
					break;
				case ANCHO_BOLA_MINI:
					setPuntuacion(getPuntuacion() + PUNTUACION_BOLA_MINI);
					bolas.remove(i);
				}

				disparo = null;
				if (bolasRotas % 5 == 0) {
					disparoEspecial = true;
				}
			}
		}

	}

	private void comprobarDisparoFijo() {
		for (int i = 0; i < bolas.size() && disparoFijo != null; i++) {
			if (disparoFijo.colisionan(bolas.get(i))) {
				bolasRotasPorArponFijo++;
				int posX = bolas.get(i).getPosX();
				int posY = bolas.get(i).getPosY();

				switch (bolas.get(i).getAncho()) {
				case ANCHO_BOLA_GRANDE:
					setPuntuacion(getPuntuacion() + PUNTUACION_BOLA_GRANDE);
					bolas.remove(i);
					for (int j = 0; j < 2; j++)
						AddBolaMedia(posX + (j * ANCHO_BOLA_MEDIA), posY);
					break;
				case ANCHO_BOLA_MEDIA:
					setPuntuacion(getPuntuacion() + PUNTUACION_BOLA_MEDIA);
					bolas.remove(i);
					for (int j = 0; j < 3; j++)
						AddBolaSmall(posX + (j * ANCHO_BOLA_PEQUEÑA), posY);
					break;
				case ANCHO_BOLA_PEQUEÑA:
					setPuntuacion(getPuntuacion() + PUNTUACION_BOLA_PEQUEÑA);
					bolas.remove(i);
					for (int j = 0; j < 4; j++)
						AddBolaMini(posX + (j * ANCHO_BOLA_MINI), posY);
					break;
				case ANCHO_BOLA_MINI:
					setPuntuacion(getPuntuacion() + PUNTUACION_BOLA_MINI);
					bolas.remove(i);
				}
				if (bolasRotasPorArponFijo == 4) {
					disparoFijo = null;
					bolasRotasPorArponFijo = 0;
				}
			}
		}

	}

	/**
	 * Metodo que comprueba que el personaje ha sido eliminado en cuyo caso
	 * inicializa la pantalla de Muerte
	 */
	private void comprobarMuerte() {
		for (int i = 0; i < bolas.size() && personaje != null; i++) {
			if (personaje.colisionan(bolas.get(i))) {
				aplastado = true;
				personaje = null;
				PantallaMuerte pantallaMuerte = new PantallaMuerte(panelJuego);
				pantallaMuerte.inicializarPantalla();
				pantallaMuerte.setPuntuacion(getPuntuacion());
				panelJuego.setPantallaActual(pantallaMuerte);
				panelJuego.setCursor(null);
			}
		}
	}

	/**
	 * Metodo que comprueba la Victoria
	 */
	private void comprobarVictoria() {
		if (!aplastado && bolas.size() == 0) {
			PantallaVictoria pantallaVictoria = new PantallaVictoria(panelJuego);
			pantallaVictoria.inicializarPantalla();
			pantallaVictoria.setPuntuacion(puntuacion);
			panelJuego.setPantallaActual(pantallaVictoria);
		}
	}

	/**
	 * Metodo que mueve los Sprites por el mundo
	 */
	private void moverSprites() {
		for (Sprite sprite : bolas) {
			sprite.moverSprite(panelJuego.getWidth(), panelJuego.getHeight());
		}
		if (disparo != null) {
			disparo.moverSprite();
			if (disparo.getPosY() <= 0) {
				disparo = null;
			}
		}
		if (disparoFijo != null) {
			disparoFijo.moverSprite();
			if (disparoFijo.getPosY() <= 0) {
				disparoFijo.setVelocidadY(0);
			}
		}

	}

	/**
	 * Metodo que añade una bola grande
	 * 
	 * @param posX
	 * @param posY
	 */
	private void AddBolaGrande(int posX, int posY) {
		int velY = (int) Math
				.floor(Math.random() * (VELOCIDAD_BOLA_GRANDE + MIN_VELOCIDAD_BOLA_GRANDE) - MIN_VELOCIDAD_BOLA_GRANDE);
		if (velY > 0)
			bolas.add(new Sprite(ANCHO_BOLA_GRANDE, ALTO_BOLA_GRANDE, posX, posY, VELOCIDADX, velY,
					"Imagenes/bolapang.png"));
		else
			AddBolaGrande(posX, posY);
	}

	/**
	 * Metodo que añade una bola media
	 * 
	 * @param posX
	 * @param posY
	 */
	private void AddBolaMedia(int posX, int posY) {
		int velY = (int) Math
				.floor(Math.random() * (VELOCIDAD_BOLA_MEDIA + MIN_VELOCIDAD_BOLA_MEDIA) - MIN_VELOCIDAD_BOLA_MEDIA);
		if (velY > 0)
			bolas.add(new Sprite(ANCHO_BOLA_MEDIA, ALTO_BOLA_MEDIA, posX, posY, VELOCIDADX, velY,
					"Imagenes/bolapang.png"));
		else {
			AddBolaMedia(posX, posY);
		}
	}

	/**
	 * Metodo que añade una bola pequeña
	 * 
	 * @param posX
	 * @param posY
	 */
	private void AddBolaSmall(int posX, int posY) {
		int velY = (int) Math.floor(
				Math.random() * (VELOCIDAD_BOLA_PEQUEÑA + MIN_VELOCIDAD_BOLA_PEQUEÑA) - MIN_VELOCIDAD_BOLA_PEQUEÑA);
		if (velY > 0)
			bolas.add(new Sprite(ANCHO_BOLA_PEQUEÑA, ALTO_BOLA_PEQUEÑA, posX, posY, VELOCIDADX, velY,
					"Imagenes/bolapang.png"));
		else
			AddBolaSmall(posX, posY);

	}

	/**
	 * Metodo que añade una bola mini
	 * 
	 * @param posX
	 * @param posY
	 */
	private void AddBolaMini(int posX, int posY) {
		int velY = (int) Math
				.floor(Math.random() * (VELOCIDAD_BOLA_MINI + MIN_VELOCIDAD_BOLA_MINI) - MIN_VELOCIDAD_BOLA_MINI);
		bolas.add(new Sprite(ANCHO_BOLA_MINI, ALTO_BOLA_MINI, posX, posY, VELOCIDADX, velY, "Imagenes/bolapang.png"));
	}

	/**
	 * Metodo que pinta la imagen de fondo del juego
	 * 
	 * @param g
	 */
	private void rellenarFondo(Graphics g) {
		// Pintar la imagen de fondo reescalada:
		g.drawImage(imagenReescalada, 0, 0, null);
	}

	/**
	 * Metodo que reecala la imagen de fondo respecto al panel de Juego
	 */
	private void reescalarImagen() {
		if (panelJuego.getWidth() > 0 && panelJuego.getHeight() > 0)
			imagenReescalada = imagenOriginal.getScaledInstance(panelJuego.getWidth(), panelJuego.getHeight(),
					Image.SCALE_SMOOTH);
	}

	/**
	 * Metodo que se encarga de pintar la puntuacion en la pantalla
	 * 
	 * @param g
	 */
	private void pintarPuntuacion(Graphics g) {
		g.setColor(Color.LIGHT_GRAY);
		g.setFont(fuentePuntuacion);
		g.drawString(Integer.toString(getPuntuacion()), 20, 20);
	}

	/**
	 * Metodo que modifica los sprites y mueve al personaje por la pantalla
	 * 
	 * @param e
	 */
	private void moverPersonaje(MouseEvent e) {
		if (personaje != null) {
			int posx = e.getX() - personaje.getAncho() / 2;
			if (posx == posXAnterior) {
				personaje.actualizarBuffer(0);
			} else {
				if (posx < posXAnterior) {
					personaje.actualizarBuffer(3);
				}
				if (posx > posXAnterior) {
					personaje.actualizarBuffer(2);

				}
			}
			personaje.setPosY(panelJuego.getHeight() - personaje.getAlto());
			personaje.setPosX(posx);
			posXAnterior = posx;
		}
	}



}
