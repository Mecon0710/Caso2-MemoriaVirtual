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

        //Inputs
    	
    	Scanner input = new Scanner (System.in);
    	
        System.out.println("Indique el numero de entradas de la TLB");
        numTLB = input.nextInt();
        
        System.out.println("Indique el numero de marcos de pagina en memoria RAM que el sistema le asigna al proceso");
        numMP = input.nextInt();
        
        System.out.println("Indique el nombre del archivo con las referencias (Alta o Baja)");
        nombre_archivo = input.next();
        
        input.close();

        // Creacion de estructuras de memoria real, tabla de paginas y TLB

		Map<Long, Long> memReal = new HashMap<Long, Long>();
        for(int i = 0; i < numMP; i++){
            int j = -1*(i+1);
            memReal.put((long) j, (long) 0);
        }

		Map<Long, Long> TP = new HashMap<Long, Long>();
        for(long i = 0; i < 64; i++){
            TP.put(i, (long) -1);
        }

		ArrayList<Long> TLB = new ArrayList<>();

        // Creacion de otros elementos

        int num_falloPag = 0;

        Object mutex = new Object();

        int[] rBits = new int[64];

        //Llamado de threads 

        Admin admin = new Admin(memReal, TP, TLB, num_falloPag, numTLB, mutex, rBits);
        Envejecimiento envejecimiento = new Envejecimiento(memReal, rBits, mutex);
        
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

        // Inicializacion de threads

        admin.start();
        envejecimiento.start();

        
        
    }

}
