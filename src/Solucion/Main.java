package Solucion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {

    private final static int T_TLB = 2;
    private final static int T_TP = 30;
    private final static int T_DATOS_RAM = 30;
    private final static int T_FALLA_PAG = 60;
    private final static int T_SOL_FALLA_PAG = 10;
    
    
    private static int numTLB;
    private static int numMP;
    private static String nombre_archivo;
    
    private Referencia ref;

    public static void main(String[] args) throws Exception{
    	
    	Scanner input = new Scanner (System.in);
    	
        System.out.println("Indique el numero de entradas de la TLB");
        numTLB = input.nextInt();
        
        System.out.println("Indique el numero de marcos de pagina en memoria RAM que el sistema le asigna al proceso");
        numMP = input.nextInt();
        
        System.out.println("Indique el nombre del archivo con las referencias (Alta o Baja)");
        nombre_archivo = input.toString();
        
        input.close();

        ArrayList<Integer> refPaginas = new ArrayList<Integer>();
		Map<Integer, ArrayList<Integer>> memReal = new HashMap<Integer, ArrayList<Integer>>();
		Map<Integer, Integer> TP = new HashMap<Integer, Integer>();
		Map<Integer, Integer> TLB = new HashMap<Integer, Integer>();
        
        Admin admin = new Admin(refPaginas, memReal, TP, TLB);
        
        
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
        admin.crearMemRealTP(numMP);
        admin.crearTLB(numTLB);
        
        
    }

}
