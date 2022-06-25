package pokemon;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;

public class Test {
	PokemonDatabase poke = new PokemonDatabase();
	
	@org.junit.Test
	public void test_connect() {
		
		//devuelve true si la conexion esta abierta tras ser llamdo (PORQUE se abra o porque a estuviera abierta) 
		//false en caso contrario 
		
		
		//Se conceta por pirmera vez.
		boolean concetadoPrueba1 = poke.connect();
		assertTrue(concetadoPrueba1);
		
		//Ya estaba cpncetado asique deveria volver a dar true
		boolean concetadoPrueba2 = poke.connect();
		assertTrue(concetadoPrueba2);
		
		//Desconecto la conexion que ya habia y la vuelvo a conectar
		poke.disconnect();
		boolean concetadoPrueba3 = poke.connect();
		assertTrue(concetadoPrueba3);
		poke.disconnect();
	}
	
	@org.junit.Test
	public void test_disconnect() {
		// devuelve true si se ejecuta sin errores 
		//y fasle si slata alguna excepcion
		
		// cerramos la conexion tras abrirla
		poke.connect();
		boolean desconectadoPueba1 = poke.disconnect();
		assertTrue(desconectadoPueba1);
		
		//cerram9os una conexion que no esta abierta
		boolean desconcetadoPrueba2 = poke.disconnect();
		assertTrue(desconcetadoPrueba2); // según el enunciado deberia dar true porque no han saltado ninguna excepcion
	}
	
	@org.junit.Test
	public void test_createTableAprende(){
		//el metodo devuelve false si la tabla no se ha podido crear(por algun tipo de error o porque ya existia
		//devuelve true si la tabla se ha creado correctamente
		
		//creas la tabla por pirmera vez por lo que deberia ser true
		boolean creaAprendePrueba1 = poke.createTableAprende();
		assertTrue(creaAprendePrueba1);
		
		//la creas por segunda ves por lo que deveria ser false
		boolean creaAprendePrueba2 = poke.createTableAprende();
		assertFalse(creaAprendePrueba2);
	}
	
	@org.junit.Test
	public void test_createTableConoce(){
		//el método devuelve false si la tabla no se ha podido crar (por algun tipo de error o porwque ya exisitia)
		//devuelve true sui se ha podido crear  correctamente
		
		//creas la tabla por pirmera vez por lo que deberia ser true
		boolean creaConocePrueba1 = poke.createTableConoce();
		assertTrue(creaConocePrueba1);

		//la creas por segunda ves por lo que deveria ser false
		boolean creaConocePrueba2 = poke.createTableConoce();
		assertFalse(creaConocePrueba2);
	}
	
	@org.junit.Test
	public void test_loadAprende(){
		//cada insercion debe ser tratado como una transaccion separada del resto
		//el métoodo debe retonrnar la cantidad de elementos insertados en la tablae
		
		//Si le metes null no deberia meter ninguno en las tablas
		int loadAprendePrueba1 = poke.loadAprende(null);
		assertEquals(0,loadAprendePrueba1);
		
		
		//hacerlo bien -- si ya hay cosas da error
		int loadAprendePrueba2 = poke.loadAprende("aprende.csv");
		assertEquals(68,loadAprendePrueba2);
		
		
	}
	@org.junit.Test
	public void test_loadConoce(){
		//debe ejecutarse la creaccion y carga de todos los datos como si fuera una unicca transssicoon, 
		//de tal forma que cualquier fallo intermendio de lugar desace por completo los cambios anteriores.
		//El método debe devolver  la cantidad de elementos insertados en la tabla

		//Si le metes null no deberia meter ninguno en las tablas
		int loadConocePrueba1 = poke.loadConoce(null);
		assertEquals(0,loadConocePrueba1);


		//hacerlo bien -- si ya hay  cosas da error
		int loadConocePrueba2 = poke.loadConoce("aprende.csv");
		assertEquals(68,loadConocePrueba2);
	}
	
	@org.junit.Test
	public void test_pokedex(){
		//Si no hya  especies alamacenadas debbe retornarse un ArrayList vacio. 
		//Si se produjera alguna excepcion el metodo devuelve null
		
		ArrayList<Especie> vacio = new ArrayList<Especie>();
		
		
	}
	@org.junit.Test
	public void test_getEjemplares(){
		//Si no hay ejemplares alamacenadas debe retornarse un ArrayList vacio. 
		//Si se produjera alguna excepcion el metodo devuelve null
		
		ArrayList<Especie> vacio = new ArrayList<Especie>();
		
		
	}
	
	@org.junit.Test
	public void test_coronaPokerus(){
		//debe devolver el numero de infectados al final de los dias
		// si no se termiana los dias debe dar los infectados el ultimo dia antess de estropearse
		
		//si te mete un null donde vdeveria ir los ejemplares lo contagiados no cambiarian
		int coronapokerusPrueba3 = poke.coronapokerus(null, 2);
		System.out.println(coronapokerusPrueba3);
		assertEquals(0, coronapokerusPrueba3);
		
		//si le damos un array vacio, tampoco contagiaria a nadie, por lo que daria o 0 o lo ya contagiados
		ArrayList<Ejemplar> vacio = new ArrayList<Ejemplar>();
		int coronapokerusPrueba4 = poke.coronapokerus(vacio, 0);
		assertEquals(0, coronapokerusPrueba4);
		
				
		//caso en el que todos se han contagiado 
		int coronapokerusPrueba1 = poke.coronapokerus(poke.getEjemplares(), 12);
		assertEquals(29, coronapokerusPrueba1);


	}	
	@org.junit.Test
	public void test_getSprinte(){
		//Debe retornar true si la imagen existia en la base de datos y se ha alamcenado correctamente
		//False en caso contrario
		
		//cojemos la imagen de charmander, como existe deveria dar true
		boolean getSpritePrueba1 = poke.getSprite(4,"Charmander");
		assertTrue(getSpritePrueba1);
		
		//coejmos la otro poquemon que no tiene deveria dar false
		boolean getSpritePureba2 = poke.getSprite(2, "Ivysaur");
		assertFalse(getSpritePureba2);
		
		//si le metemos una filename no valido deveria dar false
		boolean getSpritePureba3 = poke.getSprite(1, null);
		assertFalse(getSpritePureba3);
		
		//si le damos un pokemon que no esta en la pokedex deberia dar falso
		boolean getSpritePrueba4 = poke.getSprite(0,"Ivysaur");
		assertFalse(getSpritePrueba4);
		
		
	}
}
