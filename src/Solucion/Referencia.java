package Solucion;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Referencia extends Thread {
	
	private ArrayList<Integer> refPaginas;
	private Map<Integer, ArrayList<Integer>> memReal;
	private Map<Integer, Integer> TP;
	private Map<Integer, Integer> TLB;
	private ArrayList<Integer> marcoPagina;
	
	public Referencia() {
		
		ArrayList<Integer> refPaginas = new ArrayList<Integer>();
		Map<Integer, ArrayList<Integer>> memReal = new HashMap<Integer, ArrayList<Integer>>();
		Map<Integer, Integer> TP = new HashMap<Integer, Integer>();
		Map<Integer, Integer> TLB = new HashMap<Integer, Integer>();  //Falta crear
		
		ArrayList<Integer> marcoPagina = new ArrayList<Integer>();
		
		
	}
	
	
	public void crearMemRealTP(int mp) {
		
		int capacidad = Math.round(64/mp);
		
		for (int i = 0; i < refPaginas.size(); i++) {
			
			int j = capacidad - (i + 1);
			int pagina = refPaginas.get(i);
			
			if (marcoPagina.size() < capacidad && !marcoPagina.contains(pagina)){
				
				marcoPagina.add(pagina);
				
			}
			else if (marcoPagina.size() > capacidad) {
				
				memReal.put(j, marcoPagina);
				
				for (int m = 0; m < marcoPagina.size(); m++) {
					
					int pag = marcoPagina.get(m);
					TP.put(pag, j);
				}
			}
			
		}
	}
	
	
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
