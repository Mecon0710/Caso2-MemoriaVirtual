package Solucion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Envejecimiento extends Thread {
	
	 private Map<Integer, Long> memoriaReal;

	private Admin admin;
	
	 public Envejecimiento() {
			
		 Map<Integer, Long> memoriaReal = admin.getMemReal();
		 
		}
	//Obtiene la memoria real
	//Hace el corrimiento de todas las paginas que hay en memoria real (marcos)

	public Map<Integer, Long> getMemoriaReal() {
		return memoriaReal;
	}
}
