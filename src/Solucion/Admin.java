package Solucion;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
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

	// Los datos ya leidos
	private ArrayList<Long> refPaginas;

	// Memoria RAM = paginas y bits
	private Map<Long, Long> memRealRef;

	// Tabla de paginas y TLB
	private Map<Long, Long> TP;
	private ArrayList<Long> TLB;

	// Tamanio de la TLB
	private int numTLB;

	// Datos para el informe
	private File log;
	private  BufferedWriter logger;

	// Tiempo total en nanosegundos
	private long tiempoDirecciones;
	private long tiempoDatos;

	// Numero de fallos de pagina
	private int num_falloPagina;

	// Indica que paginas fueron referenciadas 
	private int[] rbits;
	
	/**
	 * -----------------------------------------------------
	 * Metodos
	 * -----------------------------------------------------
	 */
	
	public Admin(Map<Long, Long> memReal, Map<Long, Long> TP, ArrayList<Long> TLB, int falloPag, int numTLB, int[] rbits) {

		this.memRealRef = memReal;
		this.TP = TP;
		this.TLB = TLB;
		this.numTLB = numTLB;
		this.num_falloPagina = falloPag;
		this.refPaginas = new ArrayList<Long>();
		this.num_falloPagina = 0;
		this.rbits = rbits;

		this.tiempoDirecciones = 0;
		this.tiempoDatos = 0;
	}

	@Override
	public void run() {

		// Se hace por cada referencia de pagina en refPaginas cada 2ms
		for (int i = 0; i < refPaginas.size(); i++) {
			long refActual = refPaginas.get(i);
				
				if (estaEnTLB(refActual)) {
					// Acumula tiempo de si esta; consultar TLB + RAM
					tiempoDirecciones += 2;
					tiempoDatos += 30;
					// Actualiza lo que ya esta
					actualizarMemoria(refActual);
				} else {

					if(estaEnTP(refActual)) {
						// Acumula tiempo de si esta; consultar TP + RAM
						tiempoDirecciones += 30;
						tiempoDatos += 30;
						actualizarTLB(refActual);
						// Solo actualiza lo que ya esta
						actualizarMemoria(refActual);
					} else {
						num_falloPagina ++;
						// Acumula tiempo de si esta; FALLO + resolucion FALLO
						tiempoDirecciones += 60;
						tiempoDatos += 1e+7;
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
				try {
					sleep(2);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		}

		try {
			logger.append(memRealRef.size()+"; "+numTLB+"; "+ tiempoDirecciones + ";"+ tiempoDatos + "; " + num_falloPagina +"\n");
			logger.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private void actualizarMasVieja(long ref) {
		synchronized(memRealRef) {
			long menor = Long.MAX_VALUE;
			Set<Long> o = memRealRef.keySet();
			Iterator<Long> it = o.iterator();
			long menorLlave = it.next();
			long actual = menorLlave;
			for (int i = 0; i < memRealRef.keySet().size()-1; i++) {
				long contenido = memRealRef.get(actual);
				if(contenido < menor) {
					menor = contenido;
					menorLlave = actual;
				}
				actual = it.next();
			}
			memRealRef.remove(menorLlave);
			actualizarTP(menorLlave, (long)-1);
			removeFromTLB(menorLlave);
			memRealRef.put(ref, ( (long) Math.pow(2,31) ));
			rbits[(int)ref] = 0;
		}
	}

	private void actualizarMemConLLave(long refVieja, long refActual) {
		synchronized(memRealRef) {
			//long bits = (long)0;
			memRealRef.remove(refVieja);
			//Solo actualiza TP con las direcciones v??lidas
			if(refVieja >= 0) {
				actualizarTP(refVieja, (long)-1);
				removeFromTLB(refActual);
			}
			//bits = bits >> 1;
			memRealRef.put(refActual, (long)0);
			rbits[(int)refActual] = 1;
		}
	}

	private List<Long> darLlavesNegativas() {
		synchronized(memRealRef) {
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
	}

	private boolean estaEnTP(long refActual) {
		return TP.get(refActual) != -1;
	}

	private void actualizarMemoria(long ref) {

		synchronized(memRealRef) {
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

	public void removeFromTLB(long ref) {
		for (int i = 0; i < TLB.size(); i++) {
			if (TLB.get(i) == ref) {
				TLB.remove(i);
			}
		}
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
		File doc = new File("data/ej_Baja_64paginas.txt");
              Scanner obj = new Scanner(doc);

              while (obj.hasNextLine()) {
                   dato = obj.nextLine();
                   long num = (long) Integer.parseInt(dato);
				   refPaginas.add(num);
			  }
				   
		obj.close();

		log =  new File("data/log-Baja.csv");
		try {
			if(log.exists()) {
				List<String> read = Files.readAllLines(log.toPath());
				logger = new BufferedWriter(new FileWriter(log));
				for (int i = 0; i < read.size(); i++) {
					logger.append(read.get(i) + "\n");
				}
			} else {
				log.createNewFile();
				logger = new BufferedWriter(new FileWriter(log));
				logger.append("TamanioMemoriaReal; TamanioTLB; TiempoDirecciones; TiempoDatos; NumeroDeFalloDePaginas \n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
                   
	}
	
	public void cargarDatosAlta() throws Exception {
		
		String dato = new String();
		File doc = new File("data/ej_Alta_64paginas.txt");
              Scanner obj = new Scanner(doc);

              while (obj.hasNextLine()) {
				dato = obj.nextLine();
				long num = (long) Integer.parseInt(dato);
				refPaginas.add(num);
			  }
				   
	    obj.close();

		log =  new File("data/log-Alta.csv");
		try {
			if(log.exists()) {
				List<String> read = Files.readAllLines(log.toPath());
				logger = new BufferedWriter(new FileWriter(log));
				for (int i = 0; i < read.size(); i++) {
					logger.append(read.get(i) + "\n");
				}
			} else {
				log.createNewFile();
				logger = new BufferedWriter(new FileWriter(log));
				logger.append("TamanioMemoriaReal; TamanioTLB; TiempoDirecciones; TiempoDatos; NumeroDeFalloDePaginas \n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
                   
	}

}
