package vista;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import controlador.ControladorDF;
import modelo.DispositivoFinal;
import java.awt.*;
import java.net.*;
public class Vista extends JFrame {
	private JTextField filtroTxt;
	private volatile boolean detenerEscaneo = false;
	private JButton guardarBtn;
	private JTextField ipInicioTxt, ipFinalTxt, tiempoEsperaTxt;
	private JProgressBar barraCompletado;
	private JTable tablaEncontrados;
	private JButton iniciarScanBtn, detenerScanBtn, limpiarBtn;
	private JLabel totalEquiposLbl;
	private SwingWorker<Void, DispositivoFinal> worker;
	public Vista() {
		setTitle("Escáner de Red");
		setSize(700, 500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout(10, 10));
		setVisible(true);
		ControladorDF controlador = new ControladorDF();
		JPanel panelSuperior = new JPanel(new GridLayout(5, 2));
		panelSuperior.add(new JLabel("Filtrar por IP"));
		filtroTxt = new JTextField();
		panelSuperior.add(filtroTxt);
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
		String[] columnas = { "IP", "Nombre", "Conectado", "Tiempo (ms)" };
		DefaultTableModel modeloTabla = new DefaultTableModel(columnas, 0);
		tablaEncontrados = new JTable(modeloTabla);
		tablaEncontrados.setAutoCreateRowSorter(true);
		TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modeloTabla);
		tablaEncontrados.setRowSorter(sorter);
		filtroTxt.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
			private void filtrar() {
				String texto = filtroTxt.getText().trim();
				if (texto.isEmpty()) {
					sorter.setRowFilter(null);
				} else {
					sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto, 0));
				}
			}
			@Override
			public void insertUpdate(javax.swing.event.DocumentEvent e) {
				filtrar();
			}
			@Override
			public void removeUpdate(javax.swing.event.DocumentEvent e) {
				filtrar();
			}
			@Override
			public void changedUpdate(javax.swing.event.DocumentEvent e) {
				filtrar();
			}
		});
		add(new JScrollPane(tablaEncontrados), BorderLayout.CENTER);
		JPanel panelInferior = new JPanel(new BorderLayout());
		barraCompletado = new JProgressBar();
		barraCompletado.setMinimum(0);
		barraCompletado.setMaximum(100);
		panelInferior.add(barraCompletado, BorderLayout.NORTH);
		totalEquiposLbl = new JLabel("Equipos activos: 0");
		panelInferior.add(totalEquiposLbl, BorderLayout.CENTER);
		limpiarBtn = new JButton("Limpiar");
		guardarBtn = new JButton("Guardar");
		JPanel panelBotones = new JPanel();
		panelBotones.add(guardarBtn);
		panelBotones.add(limpiarBtn);
		panelInferior.add(panelBotones, BorderLayout.SOUTH);
		add(panelInferior, BorderLayout.SOUTH);
		iniciarScanBtn.addActionListener(e -> {
			limpiarTabla();
			String ipInicio = ipInicioTxt.getText();
			String ipFin = ipFinalTxt.getText();
			int timeout;
			try {
				timeout = Integer.parseInt(tiempoEsperaTxt.getText());
				if (timeout <= 0) {
				    JOptionPane.showMessageDialog(this, "El timeout debe ser mayor a 0.");
				    return;
				}
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(this, "El timeout debe ser un número.");
				return;
			}
			if (!controlador.validarIP(ipInicio) || !controlador.validarIP(ipFin)) {
				JOptionPane.showMessageDialog(this, "IP inválida.");
				return;
			}
			int inicio = controlador.ipAEntero(ipInicio);
			int fin = controlador.ipAEntero(ipFin);
			if (inicio > fin) {
				JOptionPane.showMessageDialog(this, "La IP inicial no puede ser mayor que la IP final.");
				return;
			}
			int totalIps = fin - inicio + 1;
			barraCompletado.setValue(0);
			iniciarScanBtn.setEnabled(false);
			detenerScanBtn.setEnabled(true);
			detenerEscaneo = false;
			worker = new SwingWorker<Void, DispositivoFinal>() {
				int activos = 0;
				int procesadas = 0;
				@Override
				protected Void doInBackground() throws Exception {
					for (int i = inicio; i <= fin; i++) {
						if (detenerEscaneo) {
							break;
						}
						String ipActual = controlador.enteroAIp(i);
						DispositivoFinal dispositivo = controlador.escanearIp(ipActual, timeout);
						if (detenerEscaneo) {
							break;
						}
						if (dispositivo.isEstaConectado()) {
							activos++;
						}
						procesadas++;
						publish(dispositivo);
						int progreso = (procesadas * 100) / totalIps;
						setProgress(progreso);
					}
					return null;
				}
				@Override
				protected void process(java.util.List<DispositivoFinal> dispositivos) {
					for (DispositivoFinal dispositivo : dispositivos) {
						agregarDispositivo(dispositivo);
					}
				}
				@Override
				protected void done() {
					iniciarScanBtn.setEnabled(true);
					detenerScanBtn.setEnabled(false);
					if (detenerEscaneo) {
						totalEquiposLbl.setText("Escaneo detenido");
					} else {
						barraCompletado.setValue(100);
						totalEquiposLbl.setText("Equipos activos: " + activos);
						JOptionPane.showMessageDialog(Vista.this, "Escaneo finalizado.");
					}
				}
			};
			barraCompletado.setIndeterminate(false);
			worker.addPropertyChangeListener(evt -> {
				if ("progress".equals(evt.getPropertyName())) {
					barraCompletado.setValue((Integer) evt.getNewValue());
				}
			});
			worker.execute();
		});
		detenerScanBtn.addActionListener(e -> {
			detenerEscaneo = true;
			controlador.detenerEscaneo();
			detenerScanBtn.setEnabled(false);
			iniciarScanBtn.setEnabled(true);
			totalEquiposLbl.setText("Escaneo detenido");
		});
		limpiarBtn.addActionListener(e -> {
			limpiarTabla();
			filtroTxt.setText("");
			ipInicioTxt.setText("");
			ipFinalTxt.setText("");
			tiempoEsperaTxt.setText("1000");
			totalEquiposLbl.setText("Equipos activos: 0");
			barraCompletado.setValue(0);
		});
		guardarBtn.addActionListener(e -> {
			JFileChooser selector = new JFileChooser();
			int opcion = selector.showSaveDialog(this);
			if (opcion == JFileChooser.APPROVE_OPTION) {
				try {
					java.io.File archivo = selector.getSelectedFile();
					java.io.PrintWriter escritor = new java.io.PrintWriter(archivo);
					escritor.println("IP,Nombre,Conectado,Tiempo(ms)");
					DefaultTableModel modelo = (DefaultTableModel) tablaEncontrados.getModel();
					for (int i = 0; i < modelo.getRowCount(); i++) {
						escritor.println(modelo.getValueAt(i, 0) + "," + modelo.getValueAt(i, 1) + ","
								+ modelo.getValueAt(i, 2) + "," + modelo.getValueAt(i, 3));
					}
					escritor.close();
					JOptionPane.showMessageDialog(this, "Resultados guardados correctamente.");
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(this, "No se pudieron guardar los resultados.");
				}
			}
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
	public void agregarDispositivo(DispositivoFinal dispositivo) {
		DefaultTableModel modelo = (DefaultTableModel) tablaEncontrados.getModel();
		modelo.addRow(new Object[] { dispositivo.getIp(), dispositivo.getNombre(), dispositivo.isEstaConectado(),
				dispositivo.getTiempoRespuestaMs() });
	}
	public void limpiarTabla() {
		DefaultTableModel modelo = (DefaultTableModel) tablaEncontrados.getModel();
		modelo.setRowCount(0);
	}
}
	
	
