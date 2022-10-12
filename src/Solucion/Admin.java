package Solucion;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.tools.Diagnostic;

public class Admin extends Thread {
	
	// estos son los datos ya leidos
	private ArrayList<Integer> refPaginas;

	// num de marcos en memoria principal
	private int[] memReal;

	// Tabla de páginas y TLB
	private int[] TP;
	private Map<Integer, Integer> TLB;

	private Integer falloPagina;
	
	public Admin(int[] memReal, int[] TP, Map<Integer, Integer> TLB, Object dormidor, Integer falloPag) {
	
		this.memReal = memReal;
		this.TP = TP;
		this.TLB = TLB;
		this.falloPagina = falloPag;
		ArrayList<Integer> refPaginas = new ArrayList<Integer>();
		ArrayList<Integer> falloPagina = new ArrayList<Integer>();
	}

	public void crearTLB(int entradasTLB) {
		for(int i = 0; i < entradasTLB; i ++) {
			TLB.put(i,TP[i]);
		}

	}
	
	public void crearTP () {
		for (int j = 0; j < TP.length; j++) {
			TP[j] = -1;
		}
	
		for (int i = 0; i < memReal.length; i++) {
			
			if (TP[refPaginas.get(i)]== -1 && memReal[i] == 0) { 

				TP[refPaginas.get(i)] = i;
				memReal[i] = 1;
				try {

					sleep(2);

				} catch (Exception e) {

				}
				
			}
		}

		for (int i = memReal.length; i < TP.length; i++) {

			if (TP[refPaginas.get(i)]== -1){

				falloPagina = i;
			
				dormidor.notify(); // despertar a envejecimiento
				// dormir a admin por medio de envejecimiento 
			}
			
		}

	}

	// public void crearMemRealTP(int mp) {
		
	// 	int capacidad = Math.round(64/mp);
		
	// 	for (int i = 0; i < refPaginas.size(); i++) {
			
	// 		int j = capacidad - (i + 1);
	// 		int pagina = refPaginas.get(i);
			
	// 		if (marcoPagina.size() < capacidad && !marcoPagina.contains(pagina)){
				
	// 			marcoPagina.add(pagina);
				
	// 		}
	// 		else if (marcoPagina.size() > capacidad) {
				
	// 			memReal.put(j, marcoPagina);
				
	// 			for (int m = 0; m < marcoPagina.size(); m++) {
					
	// 				int pag = marcoPagina.get(m);
	// 				TP.put(pag, j);
	// 			}
	// 		}
			
	// 	}
	// }
	
	public void cargarDatosBaja() throws Exception {
		
		String dato = new String();
        File doc = new File("data/ej_Baja_64 paginas.txt");
              Scanner obj = new Scanner(doc);

              while (obj.hasNextLine())
                   dato = obj.nextLine();
                   int num = Integer.parseInt(dato);
				   refPaginas.add(num);
				   
		obj.close();
                   
	}
	
	
	public void cargarDatosAlta() throws Exception {
		
		String dato = new String();
        File doc = new File("data/ej_Alta_64 paginas.txt");
              Scanner obj = new Scanner(doc);

              while (obj.hasNextLine())
                   dato = obj.nextLine();
                   int num = Integer.parseInt(dato);
				   refPaginas.add(num);
				   
	    obj.close();
                   
	}
	

	
}
