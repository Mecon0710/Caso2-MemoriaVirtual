package Solucion;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Admin extends Thread {

	/**
	 * -----------------------------------------------------
	 * Atributos
	 * -----------------------------------------------------
	 */

	// estos son los datos ya leídos
	private ArrayList<Long> refPaginas;
	// memoria RAM y R bits
	private Map<Long, Long> memRealRef;
	// Tabla de páginas y TLB
	private Map<Long, Long> TP;
	private ArrayList<Long> TLB;
	//Tamaño de la TLB
	private int numTLB;

	// Archivo para el informe
	private File log;
	private FileWriter logger;

	// Tiempo total en nanosegundos
	private long tiempoIter;

	private int num_falloPagina;

	private Object mutex;

	private int[] rbits;
	
	/**
	 * -----------------------------------------------------
	 * Métodos
	 * -----------------------------------------------------
	 */
	
	public Admin(Map<Long, Long> memReal, Map<Long, Long> TP, ArrayList<Long> TLB, int falloPag, int numTLB, Object mutex, int[] rbits) {
	
		Date date = new Date();
		String fecha = date.toString();
		log =  new File("data/log"+ fecha +".txt");
		try {
			log.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			logger = new FileWriter("data/log"+ fecha +".txt");
			logger.append("PaginaReferenciada; TiempoEnProcesar");
			logger.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.memRealRef = memReal;
		this.TP = TP;
		this.TLB = TLB;
		this.numTLB = numTLB;
		this.num_falloPagina = falloPag;
		this.refPaginas = new ArrayList<Long>();
		this.mutex = mutex;
		this.num_falloPagina = 0;
		this.rbits = rbits;
	}

	@Override
	public void run() {

		// esto se hace por cada referencia de página en refPaginas cada 2ms
		for (int i = 0; i < refPaginas.size(); i++) {
			long refActual = refPaginas.get(i);

			//Si la pagina ya esta se actualiza
			if (estaEnTLB(refActual)) {
				//acumular tiempo de sí está; consultar TLB + RAM
				tiempoIter += 32;
				//solo actualizó lo que ya está
				actualizarMemoria(refActual);
			} else {
				if(estaEnTP(refActual)) {
					//acumular tiempo de sí está; consultar TP + RAM
					tiempoIter += 60;
					actualizarTLB(refActual);
					//solo actualizó lo que ya está
					actualizarMemoria(refActual);
				} else {
					num_falloPagina ++;
					// acumular tiempo de sí está; no en TP + FALLO
					tiempoIter += 60 + 1e+7;

					List<Long> llavesNeg = darLlavesNegativas();
					if(darLlavesNegativas().size()>0) {
						long refVieja = llavesNeg.get((int) Math.random() % llavesNeg.size());
						actualizarMemConLLave(refVieja, refActual);
					} else {
						actualizarMasVieja(refActual);
					}
					actualizarTP(refActual, refActual);
					actualizarTLB(refActual);
				}
			}

		}

		try {
			logger.append(TP.size()+";"+TLB.size()+";"+"\n");
			logger.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 1. Leer referencia de refPaginas YA
		// 2. Revisa si está en TLB	YA
			// 2.si: Se registra el tiempo de acceso a la TLB	YA	
				//Se hace la referencia a esa pag	YA
			// 2.no:	YA
				// 3. Revisa si está en la TP   YA
					// 3.si: Se registra el tiempo de acceso usando TP YA
							//llamar actualizarTLB() YA
							//Se hace la referencia a esa pag YA
					// 3.no: Aquí hay fallo de página. YA
						// 4. Se accede a la memoria real y se revisa si está llena YA
							//4.hayEspacio: se coje uno al azar se remplaza por la página  YA
								//Se pone en el espacio y se pone todo en 0000... luego se pone el 1 al inicio YA
								//Se registra el tiempo YA
								//llamar actualizarTP() YA
								//llamar actualizarTLB() YA
							//4.llena: se coje el más viejo y se elimina, se remplaza por la página y ponemos el 1 a la izquierda YA
								//Se coje el más viejo, y remplaza todo por 0000...luego se pone el 1 al inicio YA
								//Se registra el tiempo YA
								//llamar actualizarTP() YA
								//llamar actualizarTLB() YA

	}

	private void actualizarMasVieja(long ref) {
		synchronized(mutex) {
			long menor = (long)0;
			long actual = (long)0;
			Set<Long> o = memRealRef.keySet();
			Iterator<Long> it = o.iterator();
			for (int i = 0; i < memRealRef.keySet().size(); i++) {
				actual = it.next();
				if(actual < menor) {
					menor = actual;
				}
			}
			memRealRef.remove(menor);
			memRealRef.put(ref, ( (long) Math.pow(2,31) ));
			rbits[(int)ref] = 1;
		}
	}

	private void actualizarMemConLLave(long refVieja, long refActual) {
		synchronized(mutex) {
			long rBits = (long)0;
			memRealRef.remove(refVieja);
			memRealRef.put(refActual, rBits +  ( (long) Math.pow(2,31) ));
			rbits[(int)refActual] = 1;
		}
	}

	private List<Long> darLlavesNegativas() {
		//TODO: revisar si hay que sincronizar
		List<Long> miListaDeNegativos;
		miListaDeNegativos = new ArrayList<>();
		Long numPag = (long)-1;
		Set<Long> o = memRealRef.keySet();
		Iterator<Long> it = o.iterator();
		for (int i = 0; i < memRealRef.keySet().size(); i++) {
			numPag = it.next();
			if(numPag < 0) {
				miListaDeNegativos.add(numPag);
			}
		}
		return miListaDeNegativos;
	}

	private boolean estaEnTP(long refActual) {
		return TP.get(refActual) != -1;
	}

	private void actualizarMemoria(long ref) {
		synchronized(mutex) {
			long rBits = memRealRef.get(ref);
			rBits = rBits >> 1;
			memRealRef.put(ref, rBits +  ( (long) Math.pow(2,31) ));
			rbits[(int)ref] = 1;
		}
	}

	public boolean estaEnTLB(long refActual) {
		for (int i = 0; i < TLB.size(); i++) {
			if (TLB.get(i) == refActual) {
				return true;
			}
		}
		return false;
	}

	public void actualizarTLB(long ref) {

		if(TLB.size()<numTLB) {
			TLB.add(ref);
		} else {
			TLB.remove(0);
			TLB.add(ref);
		}
	}
	
	public void actualizarTP (long ref, long marco) {
		TP.put(ref, marco);
	}

	public void cargarDatosBaja() throws Exception {
		
		String dato = new String();
        File doc = new File("data/ej_Baja_64 paginas.txt");
              Scanner obj = new Scanner(doc);

              while (obj.hasNextLine())
                   dato = obj.nextLine();
                   long num = (long) Integer.parseInt(dato);
				   refPaginas.add(num);
				   
		obj.close();
                   
	}
	
	public void cargarDatosAlta() throws Exception {
		
		String dato = new String();
        File doc = new File("data/ej_Alta_64 paginas.txt");
              Scanner obj = new Scanner(doc);

              while (obj.hasNextLine())
                   dato = obj.nextLine();
                   long num = (long) Integer.parseInt(dato);
				   refPaginas.add(num);
				   
	    obj.close();
                   
	}

}
