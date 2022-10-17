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

	@Override
	public void run() {
		corrimiento();
		try {
			sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void corrimiento(){
		synchronized (memRealEnvej) {
			Set<Long> paginas = memRealEnvej.keySet();
			Iterator iter = paginas.iterator(); 
			Long nuevo = (long) 0;
			for(int i = 0; i < memRealEnvej.size(); i++) {
				long llave = (long) iter.next();
				if (llave >= 0 && rBits[(int)llave] == 0){
					nuevo = memRealEnvej.get((long)llave);
					nuevo = nuevo >> 1;
					memRealEnvej.replace((Long) llave, nuevo);	
				} 
				if(llave >= 0 && rBits[(int)llave] == 1) {
					rBits[(int)llave] = 0;
				}
			}
		}
	}
}
