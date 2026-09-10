package vista;

import javax.swing.*;
import java.awt.*;

public class Vista extends JFrame{
	
	private JTextField ipInicioTxt, ipFinalTxt, tiempoEsperaTxt, nroReintentosTxt;
	private JProgressBar barraCompletado; 
	private JTable tablaEncontrados;
	private JButton iniciarScanBtn, detenerScanBtn, limpiarBtn;
	
	public Vista() {
		 setTitle("Escáner de Red");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
		
	}
	
	
}
