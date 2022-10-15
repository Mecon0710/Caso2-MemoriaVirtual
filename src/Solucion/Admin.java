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
	private Map<Long, Long> memRealRef;

	// Tabla de páginas y TLB
	private Map<Integer, Integer> TP;
	private int[] TLB;

	private int num_falloPagina;
	
	public Admin(Map<Long, Long> memReal, Map<Integer, Integer> TP, int[] TLB, int falloPag) {
	
		this.memRealRef = memReal;
		this.TP = TP;
		this.TLB = TLB;
		this.num_falloPagina = falloPag;
		ArrayList<Integer> refPaginas = new ArrayList<Integer>();
	}

	public void actualizarTLB(int entradasTLB) {
		//Busca si hay espacio en la TLB
		//Si hay espacio agrega la pagina
		//Si no hay espacio elimina el valor que esta en la posicion 0 y coloca la pagina
		for(int i = 0; i < entradasTLB; i ++) {
			TLB.put(i,TP[i]);
		}

	}
	
	public void actualizarTP () {
		//Actualiza el valor de la pagina con el marco de página
	

		for (int j = 0; j < TP.length; j++) {
			TP[j] = -1;
		}
	
		for (int i = 0; i < memRealRef.length; i++) {
			
			if (TP[refPaginas.get(i)]== -1 && memRealRef[i] == 0) { 

				TP[refPaginas.get(i)] = i;
				memReal[i] = 1;
				try {

					sleep(2);

				} catch (Exception e) {

				}
				
			}
		}

		for (int i = memRealRef.length; i < TP.length; i++) {

			if (TP[refPaginas.get(i)]== -1){

				falloPagina = i;
			
				dormidor.notify(); // despertar a envejecimiento
				// dormir a admin por medio de envejecimiento 
			}
			
		}

	}

	public void crearMemReal(int mp) {
		
		//Llama al getter de envejecimiento para obtener la memoria real 
		//Coge la primera referencia 
		//Mira si esta en TLB
		//Mira si esta en TP
		//Hay fallo de pagina
		//Mira si hay espacio en la memoria real
		//Si no hay espacio busca la pagina de menor valor, la elimina y remplazamos la pagina y ponemos el valor en 0s
		//Pone el 1 a la izquierda de la pagina que entro
		//Llama a las funciones de actualizacion de la TLB (pagina) y TP (entrada -> pagina, marco de pagina)
		//Elimina la referencia de la lista refpaginas
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
	
	public Map<Long, Long> getMemRealRef() {
		return memRealRef;
	}

	
}
