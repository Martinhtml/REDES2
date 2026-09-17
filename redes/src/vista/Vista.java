package vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import controlador.ControladorDF;
import modelo.DispositivoFinal;
import java.awt.*;
import java.net.*;

public class Vista extends JFrame{
	
	private JTextField ipInicioTxt, ipFinalTxt, tiempoEsperaTxt, nroReintentosTxt;
	private JProgressBar barraCompletado; 
	private JTable tablaEncontrados;
	private JButton iniciarScanBtn, detenerScanBtn, limpiarBtn;
	private JLabel totalEquiposLbl;
	
	public Vista() {
		setTitle("Escáner de Red");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        setVisible(true);
        
        ControladorDF controlador = new ControladorDF();

       
        
        JPanel panelSuperior = new JPanel(new GridLayout(4,2));
        panelSuperior.add(new JLabel("IP Inicial"));
        ipInicioTxt = new JTextField();
        panelSuperior.add(ipInicioTxt);
        
        panelSuperior.add(new JLabel("IP Final"));
        ipFinalTxt = new JTextField();
        panelSuperior.add(ipFinalTxt);
        
        
        panelSuperior.add(new JLabel("Timeout (ms)"));
        tiempoEsperaTxt = new JTextField("1000");
        panelSuperior.add(tiempoEsperaTxt);

        iniciarScanBtn = new JButton("Iniciar");

        detenerScanBtn = new JButton("Detener");
        
        panelSuperior.add(iniciarScanBtn);
        panelSuperior.add(detenerScanBtn);
        
        add(panelSuperior, BorderLayout.NORTH);

        

        
        
        String[] columnas = {
    		    "IP",
    		    "Nombre",
    		    "Conectado",
    		    "Tiempo (ms)"};
        
        DefaultTableModel modeloTabla = new DefaultTableModel(columnas, 0);
        
        
        tablaEncontrados = new JTable(modeloTabla);
        
        
        add(new JScrollPane(tablaEncontrados), BorderLayout.CENTER);
        
        JPanel panelInferior = new JPanel(new BorderLayout());
        
        
        barraCompletado = new JProgressBar();

        barraCompletado.setMinimum(0);
        barraCompletado.setMaximum(100);
        
        panelInferior.add(barraCompletado, BorderLayout.NORTH);
        
        totalEquiposLbl = new JLabel("Equipos activos: 0");
        
        panelInferior.add(totalEquiposLbl, BorderLayout.CENTER);
        
        
        limpiarBtn = new JButton("Limpiar");

        panelInferior.add(limpiarBtn, BorderLayout.SOUTH);

        add(panelInferior, BorderLayout.SOUTH);
        
        iniciarScanBtn.addActionListener(e -> {
            limpiarTabla();

            String ipInicio =
                    ipInicioTxt.getText();

            String ipFin =
                    ipFinalTxt.getText();

            int timeout =
                    Integer.parseInt(
                            tiempoEsperaTxt.getText()
                    );

            if(!controlador.validarIP(ipInicio) || !controlador.validarIP(ipFin)){
                JOptionPane.showMessageDialog(this, "IP inválida");
                return;
            }

            var dispositivos = controlador.escanearRango(ipInicio, ipFin, timeout);

            int activos = 0;

            for(var dispositivo : dispositivos){

                agregarDispositivo(dispositivo);

                if(dispositivo.isEstaConectado())
                    activos++;
            }

            totalEquiposLbl.setText(
                    "Equipos activos: " +
                            activos
            );
        });
       
	}
    public JTable getTablaEncontrados() {
        return tablaEncontrados;
    }

    public JTextField getIpInicioTxt() {
        return ipInicioTxt;
    }

    public JTextField getIpFinalTxt() {
        return ipFinalTxt;
    }

    public JTextField getTiempoEsperaTxt() {
        return tiempoEsperaTxt;
    }

    public JButton getIniciarScanBtn() {
        return iniciarScanBtn;
    }

    public JButton getDetenerScanBtn() {
        return detenerScanBtn;
    }

    public JButton getLimpiarBtn() {
        return limpiarBtn;
    }

    public JProgressBar getBarraCompletado() {
        return barraCompletado;
    }

    public JLabel getTotalEquiposLbl() {
        return totalEquiposLbl;
    }
    
    public void agregarDispositivo(DispositivoFinal dispositivo){

        DefaultTableModel modelo = (DefaultTableModel) tablaEncontrados.getModel();

        modelo.addRow(new Object[]{
                dispositivo.getIp(),
                dispositivo.getNombre(),
                dispositivo.isEstaConectado(),
                dispositivo.getTiempoRespuestaMs()
        });
    }
    
    
    
    public void limpiarTabla(){

        DefaultTableModel modelo =
                (DefaultTableModel)
                        tablaEncontrados.getModel();

        modelo.setRowCount(0);
    }
    
    
    
	
    
        
}
	
	

