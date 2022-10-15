package Solucion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {

    //Tiempos
    private final static int T_TLB = 2;
    private final static int T_TP = 30;
    private final static int T_DATOS_RAM = 30;
    private final static int T_FALLA_PAG = 60;
    private final static int T_SOL_FALLA_PAG = 10;
    
    
    private static int numTLB;
    private static int numMP;
    private static String nombre_archivo;
    

    public static void main(String[] args) throws Exception{
    	
    	Scanner input = new Scanner (System.in);
    	
        System.out.println("Indique el numero de entradas de la TLB");
        numTLB = input.nextInt();
        
        System.out.println("Indique el numero de marcos de pagina en memoria RAM que el sistema le asigna al proceso");
        numMP = input.nextInt();
        
        System.out.println("Indique el nombre del archivo con las referencias (Alta o Baja)");
        nombre_archivo = input.toString();
        
        input.close();

        // Creación de estructuras de memoria real, tabla de páginas y TLB
		Map<Long, Long> memReal = new HashMap<Long, Long>();
            for(int i = 0; i < numMP; i++){
                memReal.put((long) -1, (long) 0);
            }
            
		Map<Integer, Integer> TP = new HashMap<Integer, Integer>();
		int[] TLB = new int[numTLB];
        int num_falloPag = 0;

        Admin admin = new Admin(memReal, TP, TLB, num_falloPag);
        
        // Carga de datos
        
        if (nombre_archivo.equals("Alta")) {
        	
        	admin.cargarDatosAlta();
        
        }
        else if (nombre_archivo.equals("Baja")) {
        	
        	admin.cargarDatosBaja();
        }
        else {
        	
        	System.out.println(nombre_archivo + " no es una respuesta valida");
        }

        //se inicializan la memoria real y la TLB
        //admin.crearMemRealTP(numMP);
        //admin.crearTLB(numTLB);
        
        
    }

}
