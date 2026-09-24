package controlador;
import modelo.DispositivoFinal;
import java.net.*;
import java.util.ArrayList;
public class ControladorDF {
	private Process procesoActual;
	public boolean validarIP(String ip) {
	    String[] partes = ip.split("\\.");
	    if (partes.length != 4) {
	        return false;
	    }
	    try {
	        for (String parte : partes) {
	            int numero = Integer.parseInt(parte);
	            if (numero < 0 || numero > 255) {
	                return false;
	            }
	        }
	        return true;
	    } catch (NumberFormatException e) {
	        return false;
	    }
	}
	
	public DispositivoFinal escanearIp(String ip, int timeout) {
		DispositivoFinal dispositivo = new DispositivoFinal(ip);
		try {
			long inicio = System.currentTimeMillis();
			ProcessBuilder pb = new ProcessBuilder("ping", "-n", "1", "-w", String.valueOf(timeout), ip);
			procesoActual = pb.start();
			Process proceso = procesoActual;
			int resultado = proceso.waitFor();
			long fin = System.currentTimeMillis();
			long tiempo = fin - inicio;
			if (resultado == 0) {
				dispositivo.setEstaConectado(true);
				dispositivo.setTiempoRespuestaMs(tiempo);
				try {
					InetAddress direccion = InetAddress.getByName(ip);
					String nombre = direccion.getCanonicalHostName();
					if (!nombre.equals(ip)) {
						dispositivo.setNombre(nombre);
					}
				} catch (Exception e) {
					dispositivo.setNombre("Desconocido");
				}
			} else {
				dispositivo.setEstaConectado(false);
				dispositivo.setTiempoRespuestaMs(0);
				dispositivo.setNombre("Desconocido");
			}
		} catch (Exception e) {
			dispositivo.setEstaConectado(false);
			dispositivo.setTiempoRespuestaMs(0);
			dispositivo.setNombre("Desconocido");
		}
		procesoActual = null;
		return dispositivo;
	}
	public void detenerEscaneo() {
		if (procesoActual != null && procesoActual.isAlive()) {
			procesoActual.destroyForcibly();
			procesoActual = null;
		}
	}
	
	public int ipAEntero(String ip) {
		String[] partes = ip.split("\\.");
		int resultado = 0;
		for (int i = 0; i < 4; i++) {
			resultado = resultado * 256 + Integer.parseInt(partes[i]);
		}
		return resultado;
	}
	public String enteroAIp(int numero) {
		return String.format("%d.%d.%d.%d", (numero >> 24) & 255, (numero >> 16) & 255, (numero >> 8) & 255,
				numero & 255);
	}
	
}


