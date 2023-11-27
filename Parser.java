import java.util.List;
import java.util.Stack;

public class Parser {
    private final Token id = new Token(TipoToken.IDENTIFICADOR, "");
    private final Token select = new Token(TipoToken.SELECT,"");
    private final Token from = new Token(TipoToken.FROM,"");
    private final Token distinct = new Token(TipoToken.DISTINCT, "");
    private final Token coma = new Token(TipoToken.COMA, "");
    private final Token punto = new Token(TipoToken.PUNTO, "");
    private final Token asterisco = new Token(TipoToken.ASTERISCO, "");
    private final Token fin = new Token(TipoToken.EOF, "");
    //private Token aux; // Contiene tipo_token y lexema
    private final List<Token> tokens; //declaramos una lista llamada tokens
    public Parser(List<Token> tokens) { //Se inicializa el objeto Parser con una lista de objetos Token
        this.tokens = tokens;
    }
    private Token tokenfree; //token auxiliar para hacer comparaciones, etc
    Stack<Integer> pila = new Stack<>(); //declaramos la pila para el algoritmo del ASA

     //declaramos la tabla de analisis con ayuda de una matriz llamada accion
     private static final String[][] ACCION = {
        /*             *   ,  .  id  select distinct from $ Q D P A A1 A2 A3 T T1 T2 T3 */
        /* edo 0 */ { "", "", "", "", "s2", "", "", "", "1", "", "", "", "", "", "", "", "", "", "" },
        /* edo 1 */ { "", "", "", "", "", "", "", "acc", "", "", "", "", "", "", "", "", "", "", "" },
        /* edo 2 */ { "s6", "", "", "s9", "", "s4", "", "", "", "3", "5", "7", "", "8", "", "", "", "", "" },
        /* edo 3 */ { "", "", "", "", "", "", "s10", "", "", "", "", "", "", "", "", "", "", "", "" },
        /* edo 4 */ { "s6", "", "", "s9", "", "", "", "", "", "", "11", "7", "", "8", "", "", "", "", "" },
        /* edo 5 */ { "", "", "", "", "", "", "r2", "", "", "", "", "", "", "", "", "", "", "", "" },
        /* edo 6 */ { "", "", "", "", "", "", "r3", "", "", "", "", "", "", "", "", "", "", "", "" },
        /* edo 7 */ { "", "", "", "", "", "", "r3", "", "", "", "", "", "", "", "", "", "", "", "" },
        /* edo 8 */ { "", "s13", "", "", "", "", "r7", "", "", "", "", "", "12", "", "", "", "", "", "" },
        /* edo 9 */ { "", "r10", "s15", "", "", "", "r10", "", "", "", "", "", "", "", "14", "", "", "", "" },
        /* edo 10 */ { "", "", "", "s18", "", "", "", "", "", "", "", "", "", "", "", "16", "", "17", "" },
        /* edo 11 */ { "", "", "", "", "", "", "r1", "", "", "", "", "", "", "", "", "", "", "", "" },
        /* edo 12 */ { "", "", "", "", "", "", "r5", "", "", "", "", "", "", "", "", "", "", "", "" },
        /* edo 13 */ { "", "", "", "s9", "", "", "", "", "", "", "", "19", "", "8", "", "", "", "", "" },
        /* edo 14 */ { "", "r8", "", "", "", "", "r8", "", "", "", "", "", "", "", "", "", "", "", "" },
        /* edo 15 */ { "", "", "", "s20", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "" },
        /* edo 16 */ { "", "", "", "", "", "", "", "r0", "", "", "", "", "", "", "", "", "", "", "" },
        /* edo 17 */ { "", "s22", "", "", "", "", "", "r13", "", "", "", "", "", "", "", "", "21", "", "" },
        /* edo 18 */ { "", "r16", "", "s24", "", "", "", "r16", "", "", "", "", "", "", "", "", "", "", "23" },
        /* edo 19 */ { "", "", "", "", "", "", "r6", "", "", "", "", "", "", "", "", "", "", "", "" },
        /* edo 20 */ { "", "r9", "", "", "", "", "r9", "", "", "", "", "", "", "", "", "", "", "", "" },
        /* edo 21 */ { "", "", "", "", "", "", "", "r11", "", "", "", "", "", "", "", "", "", "", "" },
        /* edo 22 */ { "", "", "", "s18", "", "", "", "", "", "", "", "", "", "", "", "25", "", "17", "" },
        /* edo 23 */ { "", "r14", "", "", "", "", "", "r14", "", "", "", "", "", "", "", "", "", "", "" },
        /* edo 24 */ { "", "r15", "", "", "", "", "", "r15", "", "", "", "", "", "", "", "", "", "", "" },
        /* edo 25 */ { "", "", "", "", "", "", "", "r12", "", "", "", "", "", "", "", "", "", "", "" }
};

    private int i; //se genera para poder movernos en la lista de tokens
    public void parse(){
        i=0; //se indexa desde cero
        tokenfree = tokens.get(i); //lo usamos como apoyo solo para manejarlo más facil en el programa
        pila.push(0); //la pila empieza teniendo el estado 0

        if(tokenfree.equals(fin)){ //comprobamos en primer lugar si el primer token analizado no es fin de archivo
            System.out.println("Entrada invalida, pruebe con otra.");
        }
        else{
            while (true) { //creamos un bucle "infinito para el analisis
                int edo = pila.peek(); //sacamos el elemento de la cima de la pila pero sin eliminarlo
                tokenfree = tokens.get(i); //repetimos la instruccion ya que el indice cambiará con cada ciclo
                int colum = colterminales(tokenfree.tipo); //obtenemos el número de columna de la tabla del token que estemos manejando
                //Ahora haciendo uso de la matriz (tabla), con el estado y la columna según el terminal usado obtenemos la acción
                String accion = ACCION[edo][colum];
                if(accion.startsWith("s")){ //desplazamiento
                    int edoaccion = Integer.parseInt(accion.substring(1)); //quitamos el primer caracter y tomamos el entero que es el estado a usar para la accion que diga la tabla
                    pila.push(edoaccion); //a la pila entra el estado del desplazamiento
                    i++; //ya que es desplazamiento pasamos al sig token
                } else if (accion.startsWith("r")) { //reducción
                    int edoaccion = Integer.parseInt(accion.substring(1)); //quitamos el primer caracter y tomamos el entero que es el estado a usar para la accion que diga la tabla
                    reduccion(edoaccion); //mandamos como parametro el edo a reducir para manejarlo más cómodamente
                } else if (accion.equals("acc")) { //cadena aceptada
                    System.out.println("Consulta de entrada válida");
                    return;
                }else{ //error
                    //podemos aquí mandar a imprimir la razón del error
                    System.out.println("Consulta de entrada con sintaxis erronea");
                    return;
                }
            }
        }
    }

    private int colterminales(TipoToken tipo) {
        String term = String.valueOf(tipo); // usamos el tipotoken como un string para hacer comparacion
        if (term.equals("ASTERISCO"))
            return 0;
        else if (term.equals("COMA"))
            return 1;
        else if (term.equals("PUNTO"))
            return 2;
        else if (term.equals("IDENTIFICADOR"))
            return 3;
        else if (term.equals("SELECT"))
            return 4;
        else if (term.equals("DISTINCT"))
            return 5;
        else if (term.equals("FROM"))
            return 6;
        else if (term.equals("EOF"))
            return 7;
        else
            return 19; // ERROR imposible de suceder, en analizador lexico se encarga de manejar estos errores
    }

    // Función para realizar las reducciones
    
    void reduccion(int edored){

        String action;
        int edoActual;
        int edoSig;
        //para la reducción haremos un switch case para cada reducción que tengamos, por lo que se atacará 1 por 1
        //es importante saber que usaremos de nuevo la tabla ACCION, en el apartado ir_a, por lo que hay que definir su número de columna
        // Q  D  P   A   A1  A2  A3  T   T1  T2  T3
        // 8  9  10  11  12  13  14  15  16  17  18
        switch (edored){
            case 0: //Q -> select D from T
                //se quitan 4 elementos de la pila
                pila.pop();
                pila.pop();
                pila.pop();
                pila.pop();
                edoActual = pila.peek(); //obtenemos el elemento que esta en la punta de la pila para saber en qué estado andamos
                action = ACCION[edoActual][8]; //sacamos la siguiente acción
                edoSig = Integer.parseInt(action); //convertimos en enetero el string de la matriz
                pila.push(edoSig); //ponemos en la pila el nuevo estado
                break;
            case 1: //D -> distinct P
                //se quitan 2 elementos de la pila
                pila.pop();
                pila.pop();
                edoActual = pila.peek(); //obtenemos el elemento que esta en la punta de la pila para saber en qué estado andamos
                action = ACCION[edoActual][9];
                edoSig = Integer.parseInt(action); //convertimos en enetero el string de la matriz
                pila.push(edoSig); //ponemos en la pila el nuevo estado
                break;
            case 2: //D -> P
                //se quita 1 elemento de la pila
                pila.pop();
                edoActual = pila.peek(); //obtenemos el elemento que esta en la punta de la pila para saber en qué estado andamos
                action = ACCION[edoActual][9];
                edoSig = Integer.parseInt(action); //convertimos en enetero el string de la matriz
                pila.push(edoSig); //ponemos en la pila el nuevo estado
                break;
            case 3: //P -> * | A
            case 4:
                //se quita 1 elemento de la pila
                pila.pop();
                edoActual = pila.peek(); //obtenemos el elemento que esta en la punta de la pila para saber en qué estado andamos
                action = ACCION[edoActual][10];
                edoSig = Integer.parseInt(action); //convertimos en enetero el string de la matriz
                pila.push(edoSig); //ponemos en la pila el nuevo estado
                break;
            case 5: //A -> A1A2
                //se quitan 2 elementos de la pila
                pila.pop();
                pila.pop();
                edoActual = pila.peek(); //obtenemos el elemento que esta en la punta de la pila para saber en qué estado andamos
                action = ACCION[edoActual][11];
                edoSig = Integer.parseInt(action); //convertimos en enetero el string de la matriz
                pila.push(edoSig); //ponemos en la pila el nuevo estado
                break;
            case 6: //A1 -> ,A
                //se quitan 2 elementos de la pila
                pila.pop();
                pila.pop();
                edoActual = pila.peek(); //obtenemos el elemento que esta en la punta de la pila para saber en qué estado andamos
                action = ACCION[edoActual][12];
                edoSig = Integer.parseInt(action); //convertimos en enetero el string de la matriz
                pila.push(edoSig); //ponemos en la pila el nuevo estado
                break;
            case 7: //A1 -> epsilon
                //no se quita ningún elemento de la pila
                edoActual = pila.peek(); //obtenemos el elemento que esta en la punta de la pila para saber en qué estado andamos
                action = ACCION[edoActual][12];
                edoSig = Integer.parseInt(action); //convertimos en enetero el string de la matriz
                pila.push(edoSig); //ponemos en la pila el nuevo estado
                break;
            case 8: //A2 -> id A3
                //se quitan 2 elementos de la pila
                pila.pop();
                pila.pop();
                edoActual = pila.peek(); //obtenemos el elemento que esta en la punta de la pila para saber en qué estado andamos
                action = ACCION[edoActual][13];
                edoSig = Integer.parseInt(action); //convertimos en enetero el string de la matriz
                pila.push(edoSig); //ponemos en la pila el nuevo estado
                break;
            case 9: //A3 -> . id
                //se quitan 2 elementos de la pila
                pila.pop();
                pila.pop();
                edoActual = pila.peek(); //obtenemos el elemento que esta en la punta de la pila para saber en qué estado andamos
                action = ACCION[edoActual][14];
                edoSig = Integer.parseInt(action); //convertimos en enetero el string de la matriz
                pila.push(edoSig); //ponemos en la pila el nuevo estado
                break;
            case 10: //A3 -> epsilon
                //No se quita ningún elemento de la pila
                edoActual = pila.peek(); //obtenemos el elemento que esta en la punta de la pila para saber en qué estado andamos
                action = ACCION[edoActual][14];
                edoSig = Integer.parseInt(action); //convertimos en enetero el string de la matriz
                pila.push(edoSig); //ponemos en la pila el nuevo estado
                break;
            case 11: //T -> T2T1
                //se quitan 2 elementos de la pila
                pila.pop();
                pila.pop();
                edoActual = pila.peek(); //obtenemos el elemento que esta en la punta de la pila para saber en qué estado andamos
                action = ACCION[edoActual][15];
                edoSig = Integer.parseInt(action); //convertimos en enetero el string de la matriz
                pila.push(edoSig); //ponemos en la pila el nuevo estado
                break;
            case 12: //T1 -> , T
                //se quitan 2 elementos de la pila
                pila.pop();
                pila.pop();
                edoActual = pila.peek(); //obtenemos el elemento que esta en la punta de la pila para saber en qué estado andamos
                action = ACCION[edoActual][16];
                edoSig = Integer.parseInt(action); //convertimos en enetero el string de la matriz
                pila.push(edoSig); //ponemos en la pila el nuevo estado
                break;
            case 13: //T1 -> epsilon
                //No se quita ningun elemento de la pila
                edoActual = pila.peek(); //obtenemos el elemento que esta en la punta de la pila para saber en qué estado andamos
                action = ACCION[edoActual][16];
                edoSig = Integer.parseInt(action); //convertimos en enetero el string de la matriz
                pila.push(edoSig); //ponemos en la pila el nuevo estado
                break;
            case 14: //T2 -> id T3
                //se quitan 2 elementos de la pila
                pila.pop();
                pila.pop();
                edoActual = pila.peek(); //obtenemos el elemento que esta en la punta de la pila para saber en qué estado andamos
                action = ACCION[edoActual][17];
                edoSig = Integer.parseInt(action); //convertimos en enetero el string de la matriz
                pila.push(edoSig); //ponemos en la pila el nuevo estado
                break;
            case 15: //T3 -> id
                //se quita 1 elemento de la pila
                pila.pop();
                edoActual = pila.peek(); //obtenemos el elemento que esta en la punta de la pila para saber en qué estado andamos
                action = ACCION[edoActual][18];
                edoSig = Integer.parseInt(action); //convertimos en enetero el string de la matriz
                pila.push(edoSig); //ponemos en la pila el nuevo estado
                break;
            case 16: //T3 -> epsilon
                //No se quita ningún elemento de la pila
                edoActual = pila.peek(); //obtenemos el elemento que esta en la punta de la pila para saber en qué estado andamos
                action = ACCION[edoActual][18];
                edoSig = Integer.parseInt(action); //convertimos en enetero el string de la matriz
                pila.push(edoSig); //ponemos en la pila el nuevo estado
                break;
        }
    }

}
