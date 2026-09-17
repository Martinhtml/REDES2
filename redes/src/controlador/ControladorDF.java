package controlador;


import modelo.DispositivoFinal;
import java.net.*;
import java.util.ArrayList;

public class ControladorDF {
	 public boolean validarIP(String ip) {

	        try {
	            InetAddress.getByName(ip);
	            return true;
	        }
	        catch (Exception e) {
	            return false;
	        }
	    }
	public DispositivoFinal escanearIp(String ip, int timeOut) {
		DispositivoFinal dispositivo = new DispositivoFinal(ip);
		try {
			
			InetAddress direccion = InetAddress.getByName(ip);
			long inicio = System.currentTimeMillis();
			
			boolean responde = direccion.isReachable(timeOut);
			long fin = System.currentTimeMillis();
			dispositivo.setEstaConectado(responde);
			if (responde) {
				dispositivo.setTiempoRespuestaMs(fin-inicio);
				dispositivo.setNombre(direccion.getHostName());
			}
		}catch(Exception e) {
			dispositivo.setEstaConectado(false);
		}
		return dispositivo;
	}
	
	public int ipAEntero(String ip) {

	    String[] partes = ip.split("\\.");

	    int resultado = 0;

	    for(int i = 0; i < 4; i++) {

	        resultado = resultado * 256 +
	                Integer.parseInt(partes[i]);
	    }

	    return resultado;
	}
	
	
	public String enteroAIp(int numero) {

	    return String.format("%d.%d.%d.%d",
	            (numero >> 24) & 255,
	            (numero >> 16) & 255,
	            (numero >> 8) & 255,
	            numero & 255);
	}
	
	
	
	public ArrayList<DispositivoFinal> escanearRango(String ipInicio, String ipFin, int timeout){

	    ArrayList<DispositivoFinal> lista = new ArrayList<>();

	    int inicio = ipAEntero(ipInicio);
	    int fin = ipAEntero(ipFin);

	    for(int i = inicio; i <= fin; i++) {

	        String ipActual = enteroAIp(i);

	        DispositivoFinal dispositivo = escanearIp(ipActual, timeout);

	        lista.add(dispositivo);
	    }

	    return lista;
	}
}
