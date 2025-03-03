package es.studium.PruebaFTP;

import java.awt.Color;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import java.awt.Toolkit;

public class ClienteFTPBasico extends JFrame {
	private static final long serialVersionUID = 1L;
	// Campos de la cabecera parte superior
	static JTextField txtServidor = new JTextField();
	static JTextField txtUsuario = new JTextField();
	static JTextField txtDirectorioRaiz = new JTextField();
	// Campos de mensajes parte inferior
	private static JTextField txtArbolDirectoriosConstruido = new JTextField();
	private static JTextField txtActualizarArbol = new JTextField();
	// Botones
	JButton botonCargar = new JButton("Subir fichero");
	JButton botonDescargar = new JButton("Descargar fichero");
	JButton botonBorrar = new JButton("Eliminar fichero");
	JButton botonCreaDir = new JButton("Crear carpeta");
	JButton botonDelDir = new JButton("Eliminar carpeta");
	JButton botonSalir = new JButton("Salir");
	// Lista para los datos del directorio
	static JList<String> listaDirec = new JList<String>();
	// contenedor
	private final Container c = getContentPane();
	// Datos del servidor FTP - Servidor local
	static FTPClient cliente = new FTPClient();// cliente FTP
	String servidor = "127.0.0.1";
	String user = "Javi";
	String pasw = "Studium2023;";
	boolean login;
	static String direcInicial = "/";
	// para saber el directorio y fichero seleccionado
	static String direcSelec = direcInicial;
	static String ficheroSelec = "";
	private JButton botonVolver;
	private JButton botonRenombrarCarpeta;
	private JButton botonRenombrarFichero;

	private final String MENSAJE_RENOMBRAR_CARPETA = "¿Con qué nombre desea renombrar la carpeta?";
	private final String MENSAJE_RENOMBRAR_FICHERO = "¿Con qué nombre desea renombrar el fichero?";

	public static void main(String[] args) throws IOException {
		new ClienteFTPBasico();
	} // final del main

	public ClienteFTPBasico() throws IOException {
		super("CLIENTE B�SICO FTP");
		getContentPane().setBackground(new Color(255, 128, 128));
		setBackground(new Color(255, 255, 255));
		setIconImage(Toolkit.getDefaultToolkit().getImage("C:\\Users\\dekad\\Desktop\\GrupoStudium\\Icono_Frame.png"));
		setTitle("Cliente básico FTP");
		// para ver los comandos que se originan
		cliente.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
		cliente.connect(servidor); // conexi�n al servidor
		cliente.enterLocalPassiveMode();
		login = cliente.login(user, pasw);
		// Se establece el directorio de trabajo actual
		cliente.changeWorkingDirectory(direcInicial);
		// Obteniendo ficheros y directorios del directorio actual
		FTPFile[] files = cliente.listFiles();
		llenarLista(files, direcInicial);
		txtArbolDirectoriosConstruido.setEnabled(false);
		txtArbolDirectoriosConstruido.setBounds(37, 464, 335, 23);
		// Construyendo la lista de ficheros y directorios
		// del directorio de trabajo actual
		// preparar campos de pantalla
		txtArbolDirectoriosConstruido.setText("<< ARBOL DE DIRECTORIOS CONSTRUIDO >>");
		txtDirectorioRaiz.setBounds(37, 430, 335, 23);
		txtDirectorioRaiz.setText("DIRECTORIO RAIZ: " + direcInicial);
		txtActualizarArbol.setEnabled(false);
		txtActualizarArbol.setText("DIRECTORIO ACTUAL: " + direcSelec);

		getContentPane().setLayout(null);
		listaDirec.setBackground(new Color(255, 255, 255));

		// Preparaci�n de la lista
		// se configura el tipo de selecci�n para que solo se pueda
		// seleccionar un elemento de la lista

		listaDirec.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// barra de desplazamiento para la lista
		JScrollPane barraDesplazamiento = new JScrollPane(listaDirec);
		barraDesplazamiento.setPreferredSize(new Dimension(335, 420));
		barraDesplazamiento.setBounds(new Rectangle(37, 24, 335, 340));
		c.add(barraDesplazamiento);
		txtServidor.setEnabled(false);
		txtServidor.setBounds(37, 390, 200, 23);
		txtServidor.setText("Servidor FTP: " + servidor);
		c.add(txtServidor);
		txtUsuario.setEnabled(false);
		txtUsuario.setBounds(272, 390, 100, 23);
		txtUsuario.setText("Usuario: " + user);
		c.add(txtUsuario);
		c.add(txtDirectorioRaiz);
		c.add(txtArbolDirectoriosConstruido);
		txtActualizarArbol.setBounds(37, 495, 335, 23);
		c.add(txtActualizarArbol);
		botonCargar.setBounds(394, 219, 150, 23);
		c.add(botonCargar);
		botonCreaDir.setBounds(394, 24, 150, 23);
		c.add(botonCreaDir);
		botonDelDir.setBounds(394, 96, 150, 23);
		c.add(botonDelDir);
		botonDescargar.setBounds(394, 255, 150, 23);
		c.add(botonDescargar);
		botonBorrar.setBounds(394, 291, 150, 23);
		c.add(botonBorrar);
		botonSalir.setBounds(394, 342, 150, 23);
		c.add(botonSalir);
		c.setLayout(null);

		botonVolver = new JButton("Volver");
		botonVolver.setBounds(394, 132, 150, 23);
		getContentPane().add(botonVolver);

		JLabel lblNewLabel = new JLabel("txtDirectorioRaiz");
		lblNewLabel.setBounds(382, 434, 150, 14);
		getContentPane().add(lblNewLabel);

		JLabel lblTxtarboldirectoriosconstruido = new JLabel("txtArbolDirectoriosConstruido");
		lblTxtarboldirectoriosconstruido.setBounds(382, 468, 190, 14);
		getContentPane().add(lblTxtarboldirectoriosconstruido);

		JLabel lblTxtactualizararbol = new JLabel("txtActualizarArbol");
		lblTxtactualizararbol.setBounds(382, 499, 150, 14);
		getContentPane().add(lblTxtactualizararbol);

		botonRenombrarCarpeta = new JButton("Renombrar carpeta");
		botonRenombrarCarpeta.setBounds(394, 60, 150, 23);
		getContentPane().add(botonRenombrarCarpeta);

		botonRenombrarFichero = new JButton("Renombrar fichero");
		botonRenombrarFichero.setBounds(394, 183, 150, 23);
		getContentPane().add(botonRenombrarFichero);

		JSeparator separator = new JSeparator();
		separator.setBounds(394, 168, 150, 2);
		getContentPane().add(separator);

		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(394, 327, 150, 2);
		getContentPane().add(separator_1);

		// se a�aden el resto de los campos de pantalla
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(588, 569);
		setResizable(false);
		setVisible(true);
		// Acciones al pulsar en la lista o en los botones
		listaDirec.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent lse) {
				// TODO Auto-generated method stub
				String fic = "";
				if (lse.getValueIsAdjusting()) {
					ficheroSelec = "";
					// elemento que se ha seleccionado de la lista
					fic = listaDirec.getSelectedValue().toString();
					// Se trata de un fichero
					ficheroSelec = direcSelec;
					txtArbolDirectoriosConstruido.setText("FICHERO SELECCIONADO: " + ficheroSelec);
					ficheroSelec = fic;// nos quedamos con el nocmbre
					txtActualizarArbol.setText("DIRECTORIO ACTUAL: " + direcSelec);
				}
			}
		});

		// 1. Gestión de Evento de un click en elementos de la lista.(Cambio de nombre
		// en fichero seleccionado)
		// 2. Gestión de doble click. Si es directorio se cambia directorio y se
		// actualizan campos de texto.
		listaDirec.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				if (evt.getClickCount() == 1) { // Un clic detectado
					int index = listaDirec.locationToIndex(evt.getPoint());
					if (index >= 0) {
						String itemSeleccionado = listaDirec.getModel().getElementAt(index);
						// JOptionPane.showMessageDialog(null, "Doble clic en: " + itemSeleccionado);
						txtArbolDirectoriosConstruido.setText("FICHERO SELECCIONADO: " + itemSeleccionado);

					}
				}

				if (evt.getClickCount() == 2) { // Doble clic detectado
					int index = listaDirec.locationToIndex(evt.getPoint());
					if (index >= 0) {
						String itemSeleccionado = listaDirec.getModel().getElementAt(index);
						if (itemSeleccionado.contains("(DIR")) {
							String nombreDirectorioSinDIR = itemSeleccionado.replace("(DIR) ", "");

							try {
								String ruta = txtActualizarArbol.getText().replace("DIRECTORIO ACTUAL: ", "");
								String nuevaRuta;
								if (ruta.endsWith("/")) {
									nuevaRuta = ruta + nombreDirectorioSinDIR;
								} else {
									nuevaRuta = ruta + "/" + nombreDirectorioSinDIR;
								}

								if (cliente.changeWorkingDirectory(nuevaRuta)) {
									FTPFile[] files = cliente.listFiles();
									llenarLista(files, nuevaRuta);
									txtArbolDirectoriosConstruido.setText("<< ARBOL DE DIRECTORIOS CONSTRUIDO >>");

									txtActualizarArbol.setText("DIRECTORIO ACTUAL: " + nuevaRuta);

								} else {
									JOptionPane.showMessageDialog(null,
											"No se pudo cambiar al directorio: " + nuevaRuta);
								}
							} catch (IOException ex) {
								ex.printStackTrace();
							}
						} else {
							// System.out.println("No es un directorio");
							JOptionPane.showMessageDialog(null, "No es un directorio");

							String rutaCarpetaServidor = "C:\\Users\\dekad\\Desktop\\GrupoStudium\\3. Segundo DAM\\3. Programación de servicios y procesos\\4. Tema 4. Generación de servicios en Red\\practicaPSPT4";

							String inicioRutaA = txtActualizarArbol.getText().replace("DIRECTORIO ACTUAL: /",
									rutaCarpetaServidor);
							String inicioRutab = inicioRutaA.replace("/", "\\");
							String fichero = txtArbolDirectoriosConstruido.getText().replace("FICHERO SELECCIONADO: ",
									"");
							String rutaFichero = inicioRutab + "\\" + fichero;
							System.out.println(rutaFichero);

							File archivo = new File(rutaFichero);

							if (archivo.exists()) {

							}
							try {
								if (Desktop.isDesktopSupported()) {
									Desktop.getDesktop().open(archivo);
								} else {

									// Conseguimos que se abra el fichero independientemente de su tipo
									// los .exe los abre del tirón, para otro tipo de archivos hay que indicar el
									// programa
									// por eso se abre un cmd y se ejecuta desde ahí.
									ProcessBuilder pb;
									pb = new ProcessBuilder("cmd", "/c", "start", rutaFichero);
									pb.start();
								}
							} catch (IOException ex) {
								System.err.println("No se pudo abrir el archivo: " + ex.getMessage());
							}
						}

					}
				}
			}
		});

		botonVolver.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (txtActualizarArbol.getText().equals("DIRECTORIO ACTUAL: /")
						|| txtActualizarArbol.getText().isEmpty()) {

					JOptionPane.showMessageDialog(null, "Ya se encuentra en el directorio Raíz.");
				} else {
					System.out.println("Se retrocede.");
					String PRE_directorioAlQueVolver = "";
					int indiceUltimaBarra = txtActualizarArbol.getText().lastIndexOf("/");
					System.out.println(txtActualizarArbol.getText());
					if (indiceUltimaBarra != 1) {
						PRE_directorioAlQueVolver = txtActualizarArbol.getText().substring(0, indiceUltimaBarra);
						if (PRE_directorioAlQueVolver.endsWith(": ")) {
							PRE_directorioAlQueVolver = "DIRECTORIO ACTUAL: /";
						}
						String directorioAlQueVolver = PRE_directorioAlQueVolver.replace("DIRECTORIO ACTUAL: ", "");

						System.out.println(directorioAlQueVolver + "<-----");
						try {
							if (cliente.changeWorkingDirectory(directorioAlQueVolver)) {
								FTPFile[] files = cliente.listFiles();
								llenarLista(files, directorioAlQueVolver);
								txtArbolDirectoriosConstruido.setText("<< ARBOL DE DIRECTORIOS CONSTRUIDO >>");
								txtActualizarArbol.setText("DIRECTORIO ACTUAL: " + directorioAlQueVolver);
							} else {
								JOptionPane.showMessageDialog(null,
										"No se pudo cambiar al directorio: " + directorioAlQueVolver);
							}
						} catch (IOException ex) {
							ex.printStackTrace();
						}
					}

				}
			}
		});

		botonSalir.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					cliente.disconnect();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				System.exit(0);
			}
		});
		botonCreaDir.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String nombreCarpeta = JOptionPane.showInputDialog(null, "Introduce el nombre del directorio",
						"carpeta");
				if (!(nombreCarpeta == null)) {
					String directorio = direcSelec;
					if (!direcSelec.equals("/"))
						directorio = directorio + "/";
					// nombre del directorio a crear
					directorio += nombreCarpeta.trim();
					// quita blancos a derecha y a izquierda
					try {
						if (cliente.makeDirectory(directorio)) {
							String m = nombreCarpeta.trim() + " => Se ha creado correctamente ...";
							JOptionPane.showMessageDialog(null, m);
							txtArbolDirectoriosConstruido.setText(m);
							// directorio de trabajo actual
							cliente.changeWorkingDirectory(direcSelec);
							FTPFile[] ff2 = null;
							// obtener ficheros del directorio actual
							ff2 = cliente.listFiles();
							// llenar la lista
							llenarLista(ff2, direcSelec);
						} else
							JOptionPane.showMessageDialog(null, nombreCarpeta.trim() + " => No se ha podido crear ...");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} // final del if
			}
		}); // final del bot�n CreaDir

		// 4. Gestinar botón de borrado de carpetas; primero se implementa que el nombre
		// de la carpeta aparezca en el cuadro de dialogo
		// 4.1 Control de errores: controlar que se haya seleccionado una carpeta
		// 4.2 controlar cuando se intenta eliminar un fichero con este botón.
		botonDelDir.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (esCarpeta(txtArbolDirectoriosConstruido.getText(), "eliminar")) {

					String nombreDeCarpetaAeliminar = txtArbolDirectoriosConstruido.getText()
							.replace("FICHERO SELECCIONADO: (DIR) ", "");

					String nombreCarpeta = JOptionPane.showInputDialog(null,
							"Introduce el nombre del directorio a eliminar", nombreDeCarpetaAeliminar);
					if (!(nombreCarpeta == null)) {
						String directorio = direcSelec;
						if (!direcSelec.equals("/"))
							directorio = directorio + "/";
						// nombre del directorio a eliminar
						directorio += nombreCarpeta.trim(); // quita blancos a derecha y a izquierda
						File directorioConSubdirectorios = new File(directorio);
						try {
							if (cliente.removeDirectory(directorio)) {
								String m = nombreCarpeta.trim() + " => Se ha eliminado correctamente ...";
								JOptionPane.showMessageDialog(null, m);
								txtArbolDirectoriosConstruido.setText(m);
								// directorio de trabajo actual
								cliente.changeWorkingDirectory(direcSelec);
								FTPFile[] ff2 = null;
								// obtener ficheros del directorio actual
								ff2 = cliente.listFiles();
								// llenar la lista
								llenarLista(ff2, direcSelec);
							} else {
								
								int opcion = JOptionPane.showConfirmDialog(null,
										"La carpeta seleccionada contiene archivos \n¿Está seguro de que quiere borrarla?",
										"Confirmación", JOptionPane.YES_NO_OPTION);

								// Verificar la opción seleccionada
								if (opcion == JOptionPane.YES_OPTION) {
									// JOptionPane.showMessageDialog(null, "Se presionó Aceptar.");
									borrarDirectorioConSubdirectorios(cliente, directorio);
									String m = nombreCarpeta.trim()
											+ " => Eliminado correctamente junto a contenido... ";
									JOptionPane.showMessageDialog(null, m);
									txtArbolDirectoriosConstruido.setText(m);
									// directorio de trabajo actual
									cliente.changeWorkingDirectory(direcSelec);
									FTPFile[] ff2 = null;
									// obtener ficheros del directorio actual
									ff2 = cliente.listFiles();
									// llenar la lista con los ficheros del directorio actual
									llenarLista(ff2, direcSelec);
									
								} else if (opcion == JOptionPane.NO_OPTION) {
									JOptionPane.showMessageDialog(null, "La carpeta no se eliminará.");
								}

							}
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
					// final del if
				}
			}
		});
		// final del bot�n Eliminar Carpeta
		botonCargar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser f;
				File file;
				f = new JFileChooser();
				// solo se pueden seleccionar ficheros
				f.setFileSelectionMode(JFileChooser.FILES_ONLY);
				// t�tulo de la ventana
				f.setDialogTitle("Selecciona el fichero a subir al servidor FTP");
				// se muestra la ventana
				int returnVal = f.showDialog(f, "Cargar");
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					// fichero seleccionado
					file = f.getSelectedFile();
					// nombre completo del fichero
					String archivo = file.getAbsolutePath();
					// solo nombre del fichero
					String nombreArchivo = file.getName();
					try {
						SubirFichero(archivo, nombreArchivo);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		}); // Fin bot�n subir
		botonDescargar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String directorio = direcSelec;
				if (!direcSelec.equals("/"))
					directorio = directorio + "/";
				if (!direcSelec.equals("")) {
					DescargarFichero(directorio + ficheroSelec, ficheroSelec);
				}
			}
		}); // Fin bot�n descargar

		// 5. Gestinar botón de borrado de ficheros; primero se implementa que el nombre
		// de la carpeta aparezca en el cuadro de dialogo
		// 5.1 Control de errores: controlar que se haya seleccionado un fichero
		// 5.2 controlar cuando se intenta eliminar una carpeta con este botón.
		botonBorrar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				if (esFichero(txtArbolDirectoriosConstruido.getText(), "eliminar")) {
					String nombreFicheroAEliminar = txtArbolDirectoriosConstruido.getText()
							.replace("FICHERO SELECCIONADO: ", "");

					String directorio = direcSelec;
					if (!direcSelec.equals("/"))
						directorio = directorio + "/";
					if (!direcSelec.equals("")) {
						BorrarFichero(directorio + ficheroSelec, nombreFicheroAEliminar);
					}
				}
			}
		});
		// 6. Renombrar carpetas
		botonRenombrarCarpeta.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (esCarpeta(txtArbolDirectoriosConstruido.getText(), "renombrar")) {
					String nombreCarpetaARenombrar = txtArbolDirectoriosConstruido.getText()
							.replace("FICHERO SELECCIONADO: (DIR) ", "");
					DialogoConCampoDeTexto dialogo = new DialogoConCampoDeTexto(MENSAJE_RENOMBRAR_CARPETA,
							nombreCarpetaARenombrar);
					String nuevoNombreCarpeta = dialogo.getResultado();

					String rutaActual = txtActualizarArbol.getText().replace("DIRECTORIO ACTUAL: ", "")
							+ nombreCarpetaARenombrar;
					String nuevaRuta = rutaActual.replace(nombreCarpetaARenombrar, nuevoNombreCarpeta);
					System.out.println(rutaActual);
					System.out.println(nuevaRuta);

					if (dialogo.getTxtIntroducir().getText().equals(nombreCarpetaARenombrar)) {
						JOptionPane.showMessageDialog(null, "La carpeta no se renombrará.");
					}

					else if (nuevoNombreCarpeta.isEmpty()) {
						JOptionPane.showMessageDialog(null, "No es posible dejar el nombre de una carpeta vacía.");
					} else if (nuevoNombreCarpeta.equals(nombreCarpetaARenombrar)) {
						JOptionPane.showMessageDialog(null, "Ha introducido elmismo nombre de carpeta");
					} else {
						try {

							if (cliente.rename(rutaActual, nuevaRuta)) {
								String m = "Carpeta " + nuevoNombreCarpeta + " => Renombrada correctamente... ";
								JOptionPane.showMessageDialog(null, m);
								txtArbolDirectoriosConstruido.setText(m);
								// directorio de trabajo actual
								cliente.changeWorkingDirectory(direcSelec);
								FTPFile[] ff2 = null;
								// obtener ficheros del directorio actual
								ff2 = cliente.listFiles();
								// llenar la lista con los ficheros del directorio actual
								llenarLista(ff2, direcSelec);
							} else {
								JOptionPane.showMessageDialog(null,
										nombreCarpetaARenombrar + " => No se ha podido renombrar ...");
							}
						} catch (IOException ex) {
							ex.printStackTrace();
							System.out.println("Error de conexión FTP: " + ex.getMessage());
						}
					}
				}
			}
		});

		// 6. Renombrar carpetas
		botonRenombrarFichero.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (esFichero(txtArbolDirectoriosConstruido.getText(), "renombrar")) {
					String nombreFicheroARenombrar = txtArbolDirectoriosConstruido.getText()
							.replace("FICHERO SELECCIONADO: ", "");
					DialogoConCampoDeTexto dialogo = new DialogoConCampoDeTexto(MENSAJE_RENOMBRAR_FICHERO,
							nombreFicheroARenombrar);
					String nuevoNombreFichero = dialogo.getResultado();

					String rutaActual = txtActualizarArbol.getText().replace("DIRECTORIO ACTUAL: ", "")
							+ nombreFicheroARenombrar;
					String nuevaRuta = rutaActual.replace(nombreFicheroARenombrar, nuevoNombreFichero);
					System.out.println(rutaActual);
					System.out.println(nuevaRuta);

					if (dialogo.getTxtIntroducir().getText().equals(nombreFicheroARenombrar)) {
						JOptionPane.showMessageDialog(null, "El fichero no se renombrará.");
					} else if (nuevoNombreFichero.isEmpty()) {
						JOptionPane.showMessageDialog(null, "No es posible dejar el nombre de un fichero vacío.");
					} else if (nuevoNombreFichero.equals(nombreFicheroARenombrar)) {
						JOptionPane.showMessageDialog(null, "Ha introducido elmismo nombre de fichero");
					} else {
						try {

							if (cliente.rename(rutaActual, nuevaRuta)) {
								String m = "Carpeta " + nuevoNombreFichero + " => Renombrada correctamente... ";
								JOptionPane.showMessageDialog(null, m);
								txtArbolDirectoriosConstruido.setText(m);
								// directorio de trabajo actual
								cliente.changeWorkingDirectory(direcSelec);
								FTPFile[] ff2 = null;
								// obtener ficheros del directorio actual
								ff2 = cliente.listFiles();
								// llenar la lista con los ficheros del directorio actual
								llenarLista(ff2, direcSelec);
							} else {
								JOptionPane.showMessageDialog(null,
										nombreFicheroARenombrar + " => No se ha podido renombrar ...");
							}
						} catch (IOException ex) {
							ex.printStackTrace();
							System.out.println("Error de conexión FTP: " + ex.getMessage());
						}
					}
				}
			}
		});

	} // fin constructor

	public boolean esCarpeta(String elementoSeleccionado, String accion) {

		if (elementoSeleccionado.contains("(DIR)")) {
			return true;
		} else if (elementoSeleccionado.contains("FICHERO SELECCIONADO")) {
			String ficheroSeleccionado = elementoSeleccionado.replace("FICHERO SELECCIONADO: ", "");
			JOptionPane.showMessageDialog(null,
					"No se ha podido " + accion + "  '" + ficheroSeleccionado + "' porque no es una carpeta.");
			return false;
		} else {
			JOptionPane.showMessageDialog(null, "Debe seleccionar una carpeta.");
			return false;
		}
	}

	public boolean esFichero(String elementoSeleccionado, String accion) {

		if (elementoSeleccionado.contains("(DIR)")) {
			String ficheroSeleccionado = elementoSeleccionado.replace("FICHERO SELECCIONADO: (DIR) ", "");
			JOptionPane.showMessageDialog(null,
					"No se ha podido " + accion + " '" + ficheroSeleccionado + "' porque no es un fichero.");
			return false;
		} else if (elementoSeleccionado.contains("FICHERO SELECCIONADO")) {
			return true;
		} else {
			JOptionPane.showMessageDialog(null, "Debe seleccionar un fichero.");
			return false;
		}
	}

	private static void llenarLista(FTPFile[] files, String direc2) {
		if (files == null)
			return;
		// se crea un objeto DefaultListModel
		DefaultListModel<String> modeloLista = new DefaultListModel<String>();
		modeloLista = new DefaultListModel<String>();
		// se definen propiedades para la lista, color y tipo de fuente

		listaDirec.setForeground(Color.blue);
		Font fuente = new Font("Courier", Font.PLAIN, 12);
		listaDirec.setFont(fuente);
		// se eliminan los elementos de la lista
		listaDirec.removeAll();
		try {
			// se establece el directorio de trabajo actual
			cliente.changeWorkingDirectory(direc2);
		} catch (IOException e) {
			e.printStackTrace();
		}
		direcSelec = direc2; // directorio actual
		// se a�ade el directorio de trabajo al listmodel,
		// primerelementomodeloLista.addElement(direc2);
		// se recorre el array con los ficheros y directorios
		for (int i = 0; i < files.length; i++) {
			if (!(files[i].getName()).equals(".") && !(files[i].getName()).equals("..")) {
				// nos saltamos los directorios . y ..
				// Se obtiene el nombre del fichero o directorio
				String f = files[i].getName();
				// Si es directorio se a�ade al nombre (DIR)
				if (files[i].isDirectory())
					f = "(DIR) " + f;
				// se a�ade el nombre del fichero o directorio al listmodel
				modeloLista.addElement(f);
			} // fin if
		} // fin for
		try {
			// se asigna el listmodel al JList,
			// se muestra en pantalla la lista de ficheros y direc
			listaDirec.setModel(modeloLista);
		} catch (NullPointerException n) {
			; // Se produce al cambiar de directorio
		}
	}// Fin llenarLista

	private boolean SubirFichero(String archivo, String soloNombre) throws IOException {
		cliente.setFileType(FTP.BINARY_FILE_TYPE);
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(archivo));
		boolean ok = false;
		// directorio de trabajo actual
		cliente.changeWorkingDirectory(direcSelec);
		if (cliente.storeFile(soloNombre, in)) {
			String s = " " + soloNombre + " => Subido correctamente...";
			txtArbolDirectoriosConstruido.setText(s);
			// txtActualizarArbol.setText("Se va a actualizar el �rbol de directorios...");
			JOptionPane.showMessageDialog(null, s);
			FTPFile[] ff2 = null;
			// obtener ficheros del directorio actual
			ff2 = cliente.listFiles();
			// llenar la lista con los ficheros del directorio actual
			llenarLista(ff2, direcSelec);
			ok = true;
		} else
			txtArbolDirectoriosConstruido.setText("No se ha podido subir... " + soloNombre);
		return ok;
	}// final de SubirFichero

	// 6. Gestión de la introducción por parte del usuario de una ruta.
	private void DescargarFichero(String NombreCompleto, String nombreFichero) {
		// obtener la ruta que ha introducido el usuario
		String procesarRuta1 = txtDirectorioRaiz.getText().replace("DIRECTORIO RAIZ: /", "");
		String procesarRuta2 = procesarRuta1.replace("/", "\\");
		String rutaDescargaPorDefecto;
		if (!txtDirectorioRaiz.getText().isEmpty()) {
			if (txtDirectorioRaiz.getText().contains("C:\\")) {
				rutaDescargaPorDefecto = procesarRuta2;
			} else {
				rutaDescargaPorDefecto = "C:\\" + procesarRuta2;
			}
		} else {
			rutaDescargaPorDefecto = procesarRuta2;
		}
		System.out.println(rutaDescargaPorDefecto);

		// Declarar la ventana de descarga
		File file;
		String archivoyCarpetaDestino = "";
		String carpetaDestino = "";
		JFileChooser f = new JFileChooser();

		if (!txtDirectorioRaiz.getText().equals("DIRECTORIO RAIZ: /")) {

			// Creamos una carpeta con la ruta especificada por el usuario
			File carpetaDestiniIntroducidaPorUsuario = new File(rutaDescargaPorDefecto);

			// Comprobamos que la ruta introducida por el usuario existe
			if (carpetaDestiniIntroducidaPorUsuario.exists()) {
				System.out.println("La ruta existe");
				f.setCurrentDirectory(carpetaDestiniIntroducidaPorUsuario); // <-- Se establece la ruta que ha escrito
																			// el usuario
			} else {
				JOptionPane.showMessageDialog(null,
						"La ruta especificada no existe en el equipo.\nSe usará la ruta de descarga por defecto.");
				System.out.println("La ruta no existe."); // <-- Se introduce una ruta que no es válida ( se dejará la
															// ruta que establece el JFileChooser).
			}
		}

		// solo se pueden seleccionar directorios
		// f.setCurrentDirectory(new File("C:\\Users\\dekad\\Desktop"));
		f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		// t�tulo de la ventana
		f.setDialogTitle("Selecciona el Directorio donde Descargar el Fichero");
		int returnVal = f.showDialog(null, "Descargar");
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file = f.getSelectedFile();
			// obtener carpeta de destino
			carpetaDestino = (file.getAbsolutePath()).toString();
			// construimos el nombre completo que se crear� en nuestro disco
			archivoyCarpetaDestino = carpetaDestino + File.separator + nombreFichero;
			try {
				cliente.setFileType(FTP.BINARY_FILE_TYPE);
				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(archivoyCarpetaDestino));
				if (cliente.retrieveFile(NombreCompleto, out))
					JOptionPane.showMessageDialog(null, nombreFichero + " => Se ha descargado correctamente ...");
				else
					JOptionPane.showMessageDialog(null, nombreFichero + " => No se ha podido descargar ...");
				out.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	} // Final de DescargarFichero

	private void BorrarFichero(String NombreCompleto, String nombreFichero) {
		// pide confirmaci�n
		int seleccion = JOptionPane.showConfirmDialog(null, "¿Desea eliminar el fichero '" + nombreFichero + "'?");

		if (seleccion == JOptionPane.OK_OPTION) {
			try {
				if (cliente.deleteFile(NombreCompleto)) {
					String m = nombreFichero + " => Eliminado correctamente... ";
					JOptionPane.showMessageDialog(null, m);
					txtArbolDirectoriosConstruido.setText(m);
					// directorio de trabajo actual
					cliente.changeWorkingDirectory(direcSelec);
					FTPFile[] ff2 = null;
					// obtener ficheros del directorio actual
					ff2 = cliente.listFiles();
					// llenar la lista con los ficheros del directorio actual
					llenarLista(ff2, direcSelec);
					// txtArbolDirectoriosConstruido.setText("<< ARBOL DE DIRECTORIOS CONSTRUIDO
					// >>");
				} else {
					JOptionPane.showMessageDialog(null, nombreFichero + " => No se ha podido eliminar ...");
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}// Final de BorrarFichero

	//Borrado de carpetas con carpetas dentro (recursividad)
	public static boolean borrarDirectorioConSubdirectorios(FTPClient cliente, String directorio) throws IOException {
		FTPFile[] archivos = cliente.listFiles(directorio); // Obtener archivos y subdirectorios

		if (archivos != null) {
			for (FTPFile archivo : archivos) {
				String rutaCompleta = directorio + "/" + archivo.getName(); // Construir la ruta completa

				if (archivo.isDirectory()) {
					// Llamada recursiva para eliminar subdirectorios
					borrarDirectorioConSubdirectorios(cliente, rutaCompleta);
				} else {
					// Eliminar archivo
					if (!cliente.deleteFile(rutaCompleta)) {
						System.err.println("No se pudo eliminar el archivo: " + rutaCompleta);
					} else {
						System.out.println("Archivo eliminado: " + rutaCompleta);
					}
				}
			}
		}

		// Intentar eliminar el directorio una vez vacío
		if (cliente.removeDirectory(directorio)) {
			System.out.println("Directorio eliminado: " + directorio);
			return true;
		} else {
			System.err.println("No se pudo eliminar el directorio: " + directorio);
			return false;
		}
	}
}// Final de la clase ClienteFTPBasico