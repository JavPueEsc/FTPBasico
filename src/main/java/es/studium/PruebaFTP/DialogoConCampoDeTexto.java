package es.studium.PruebaFTP;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class DialogoConCampoDeTexto extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JLabel lblMensaje;
	private JTextField txtIntroducir;
	public JTextField getTxtIntroducir() {
		return txtIntroducir;
	}
	public void setTxtIntroducir(JTextField txtIntroducir) {
		this.txtIntroducir = txtIntroducir;
	}
	public void setResultado(String resultado) {
		this.resultado = resultado;
	}
	private String mensaje;
	private String nombreACambiar;
	private String resultado;
	/*public static void main(String[] args) {
		try {
			DialogoConCampoDeTexto dialog = new DialogoConCampoDeTexto(mensaje);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/

	/**
	 * Create the dialog.
	 */
	
	public DialogoConCampoDeTexto(String mensaje, String nombreACambiar) {
		this.mensaje = mensaje;
		this.nombreACambiar= nombreACambiar;
		
		setBounds(100, 100, 357, 159);
		getContentPane().setLayout(null);
		contentPanel.setBounds(0, 0, 336, 120);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel);
		contentPanel.setLayout(null);
		setModal(true);
		{
			lblMensaje = new JLabel(mensaje);
			lblMensaje.setBounds(52, 13, 231, 14);
			lblMensaje.setFont(new Font("Tahoma", Font.PLAIN, 11));
			contentPanel.add(lblMensaje);
		}
		
		txtIntroducir = new JTextField();
		txtIntroducir.setText(nombreACambiar);
		txtIntroducir.setBounds(62, 38, 221, 23);
		contentPanel.add(txtIntroducir);
		txtIntroducir.setColumns(10);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setBounds(62, 70, 221, 33);
			contentPanel.add(buttonPane);
			buttonPane.setLayout(null);
			{
				JButton okButton = new JButton("Aceptar");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						resultado = txtIntroducir.getText(); // Guarda el texto ingresado
			            dispose();
					}
				});
				okButton.setBounds(10, 5, 100, 23);
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						resultado = ""; // Guarda el texto ingresado
			            dispose();
					}
				});
				cancelButton.setBounds(115, 5, 100, 23);
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);
	}
	public String getResultado() {
        return resultado;
    }
}
