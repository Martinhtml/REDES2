package modelo;

public class DispositivoFinal {
	private String ip;
    private String nombre;
    private boolean estaConectado;
    private long tiempoRespuestaMs;
    
    public DispositivoFinal(String ip) {
        this.ip = ip;
        this.nombre = "Desconocido";
        this.estaConectado = false;
        this.tiempoRespuestaMs = 0;
    }

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public boolean isEstaConectado() {
		return estaConectado;
	}

	public void setEstaConectado(boolean estaConectado) {
		this.estaConectado = estaConectado;
	}

	public long getTiempoRespuestaMs() {
		return tiempoRespuestaMs;
	}

	public void setTiempoRespuestaMs(long tiempoRespuestaMs) {
		this.tiempoRespuestaMs = tiempoRespuestaMs;
	}

	@Override
	public String toString() {
		return "DispositivoFinal [ip=" + ip + ", nombre=" + nombre + ", estaConectado=" + estaConectado
				+ ", tiempoRespuestaMs=" + tiempoRespuestaMs + "]";
	}
    
    
}
