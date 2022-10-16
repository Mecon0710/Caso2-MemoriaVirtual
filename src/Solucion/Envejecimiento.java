package Solucion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.lang.Long;
public class Envejecimiento extends Thread {
	
	private Map<Long, Long> memRealEnvej;
	private int[] rBits;
	private Object mutex;
	
	 public Envejecimiento(Map<Long, Long> memRealRef, int[] rbits, Object mutex) {
			
		this.memRealEnvej = memRealRef;
		this.rBits = rbits;
		this.mutex = mutex;
		 
		}
	//Obtiene la memoria real
	//Hace el corrimiento de todas las paginas que hay en memoria real (marcos)

	public Map<Long, Long> getMemRealEnvej() {
		return memRealEnvej;
	}

	public void corrimiento(){
		synchronized (mutex){
		Set<Long> paginas = memRealEnvej.keySet();
		Iterator iter = paginas.iterator(); 
		Long nuevo = (long) 0;

		if (rBits.length != 64){
			for(int i = 0; i < memRealEnvej.size(); i++){
				nuevo = memRealEnvej.get(iter.next());
				nuevo = nuevo >> 1;
				memRealEnvej.replace((Long) iter.next(), nuevo);
			}
		} else {
			for(int i = 0; i < memRealEnvej.size(); i++){
				int llave = (int) iter.next();

				if (rBits[llave] == 0){
					nuevo = memRealEnvej.get((long)llave);
					nuevo = nuevo >> 1;
					memRealEnvej.replace((Long) iter.next(), nuevo);
					}
				}
			}
		}
	}
}
