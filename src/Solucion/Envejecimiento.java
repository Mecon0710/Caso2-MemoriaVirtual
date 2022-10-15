package Solucion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.lang.Long;
public class Envejecimiento extends Thread {
	
	 private static Map<Long, Long> memRealEnvej;

	private Admin admin;
	
	 public Envejecimiento() {
			
		 this.memRealEnvej = admin.getMemRealRef();
		 
		}
	//Obtiene la memoria real
	//Hace el corrimiento de todas las paginas que hay en memoria real (marcos)

	public Map<Long, Long> getMemRealEnvej() {
		return memRealEnvej;
	}

	public static void corrimiento(){
		Set<Long> paginas = memRealEnvej.keySet();
		Iterator iter = paginas.iterator(); 
		Long nuevo = (long) 0;
		
		for(int i = 0; i < memRealEnvej.size(); i++)
			nuevo = memRealEnvej.get(iter.next());
			nuevo = nuevo >> 1;
			memRealEnvej.replace((Long) iter.next(), nuevo);
    }
}
