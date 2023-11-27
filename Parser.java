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
    private static final String[][] ACCION = {};

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
}
