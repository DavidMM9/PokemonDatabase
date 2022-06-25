package pokemon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import javafx.geometry.Insets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PokemonDatabase {

    private Connection conn;

    public PokemonDatabase() {

    }

    /**
     * Metodo de conexión a la base de datos.
     *
     * @return booleano que ídica si la operación se ha hecho correctamente.
     */
    public boolean connect() {

        String server = "localhost:3306";
        String db = "pokemon";
        String user = "pokemon_user";
        String pass = "pokemon_pass";
        String url = "jdbc:mysql://" + server + "/" + db;

        try {
            if (conn == null || !conn.isValid(5)) {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(url, user, pass);
                System.out.println("Conectado a la BD");
            }
            return true;
        } catch (ClassNotFoundException e) {
            System.out.println("Error al cargar el driver: " + e.getMessage());
            return false;
        } catch (SQLException e) {
            System.out.println("Error al abrir la conexion: " + e.getMessage());
            return false;
        }

    }

    /**
     * Metodo para la desconexión de la base de datos
     *
     * @return booleano que ídica si la operación se ha hecho correctamente.
     */
    public boolean disconnect() {
        try {
            if (conn != null && conn.isClosed()) {
                conn.close();
                conn = null;
                System.out.println("Conexion Cerrada OK");
            }
            return true;
            //tratamiento de excepciones.
        } catch (SQLException e) {
            System.out.println("Error al cerrar la conexión: " + e.getMessage());
            return false;
        }
    }

    /**
     * metodo para la creación de la tabla aprende.
     *
     * @return booleano que ídica si la operación se ha hecho correctamente.
     */
    public boolean createTableAprende() {

        connect();
        String query = "CREATE TABLE aprende(" + "n_pokedex INT NOT NULL, " + "id_ataque INT NOT NULL, "
            + "nivel INT NOT NULL, " + "PRIMARY KEY (n_pokedex, id_ataque), "
            + "FOREIGN KEY (n_pokedex) REFERENCES especie (n_pokedex), "
            + "FOREIGN KEY (id_ataque) REFERENCES ataque (id_ataque) " + ");";

        ResultSet rs = null;
        Statement st = null;
        boolean res = false;

        try {
            st = conn.createStatement();
            res = st.executeUpdate(query) == 0;
            //tratamiento de excepciones.
        } catch (SQLException e) {
            System.out.println("Error en la ejecucion del metodo: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error al obtener los datos de las especies: " + e.getMessage());
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (st != null)
                    st.close();
            } catch (SQLException e) {
                System.out.println("Error en la ejecucion del metodo: " + e.getMessage());
                res = false;
            }
        }
        return res;
    }

    /**
     * metodo para la creación de la tabla conoce.
     *
     * @return booleano que ídica si la operación se ha hecho correctamente.
     */
    public boolean createTableConoce() {
        connect();
        String query = "CREATE TABLE conoce(" + "id_ataque INT NOT NULL, " + "n_pokedex INT NOT NULL, "
            + "n_encuentro INT NOT NULL, " + "PRIMARY KEY (id_ataque, n_pokedex, n_encuentro), "
            + "FOREIGN KEY (id_ataque) REFERENCES ataque(id_ataque), "
            + "FOREIGN KEY (n_pokedex, n_encuentro) REFERENCES ejemplar(n_pokedex, n_encuentro) " + ");";

        ResultSet rs = null;
        Statement st = null;
        boolean res = false;

        try {
            st = conn.createStatement();
            res = st.executeUpdate(query) == 0;

            //tratamiento de excepciones
        } catch (SQLException e) {
            System.out.println("Error al crear la tabla conoce: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error en la ejecución del metodo: " + e.getMessage());
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (st != null)
                    st.close();
            } catch (SQLException e) {
                System.out.println("Error en la ejecucion del metodo: " + e.getMessage());
                res = false;
            }
        }
        return res;
    }

    /**
     * metodo para la carga de datos en la tabla aprende.
     *
     * @param fileName
     *            nombre del archivo conlos datos a cargar.
     * @return int con la cantidad de entradas insertadas.
     */
    public int loadAprende(String fileName) {
        if (fileName == null || !fileName.equals("aprende.csv")) {
            return 0;
        }
        connect();
        ArrayList<Aprende> aprendeArray = new ArrayList<Aprende>();
        PreparedStatement pst = null;
        int inserted = 0;
        try {
            String query = "INSERT INTO aprende (n_pokedex, id_ataque, nivel) VALUES (?, ?, ?);";
            pst = conn.prepareStatement(query);
            int res = 1;
            aprendeArray = Aprende.readData(fileName);
            for (int i = 0; i < aprendeArray.size() && res == 1; i++) {
                Aprende selected = aprendeArray.get(i);
                pst.setInt(1, selected.getId_especie());
                pst.setInt(2, selected.getId_ataque());
                pst.setInt(3, selected.getNivel());
                res = pst.executeUpdate();
                inserted++;
            }
            //tratamiento de excepciones.
        } catch (SQLException e) {
            System.out.println("Error en la ejecucion del metodo: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error al obtener los datos de las especies: " + e.getMessage());
        } finally {
            try {
                if (pst != null)
                    pst.close();
            } catch (SQLException e) {
                System.out.println("Error crítico al cerrar un PreparedStatement: " + e.getMessage());
            }
        }
        return inserted;
    }

    /**
     * metodo para la carga de datos en la tabla conoce.
     *
     * @param fileName
     *            nombre del archivo conlos datos a cargar.
     * @return int con la cantidad de entradas insertadas.
     */

    public int loadConoce(String fileName) {
        if (fileName == null || !fileName.equals("conoce.csv")) {
            return 0;
        }
        connect();
        ArrayList<Conoce> conoceArray = new ArrayList<Conoce>();
        PreparedStatement pst = null;
        int inserted = 0;
        try {
            conn.setAutoCommit(false);
            String query = "INSERT INTO conoce (id_ataque, n_pokedex, n_encuentro) VALUES (?, ?, ?);";
            pst = conn.prepareStatement(query);
            int res = 1;
            conoceArray = Conoce.readData(fileName);
            for (int i = 0; i < conoceArray.size() && res == 1; i++) {
                Conoce selected = conoceArray.get(i);
                pst.setInt(1, selected.getId_ataque());
                pst.setInt(2, selected.getId_especie());
                pst.setInt(3, selected.getN_encuentro());
                res = pst.executeUpdate();
                inserted++;
            }
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            System.out.println("Error de SQL en la ejecucion del metodo: " + e.getMessage());
            inserted = 0;
        } catch (Exception e) {
            System.out.println("Error al obtener los datos de las especies: " + e.getMessage());
            inserted = 0;
        } finally {
            try {
                if (pst != null)
                    pst.close();
            } catch (SQLException e) {
                System.out.println("error crítico al cerrar el PreparedStatement: ");
                inserted = 0;
            }
        }
        return inserted;
    }

    /**
     * metodo para la extracción de los datos de la pokedex.
     *
     * @return ArrayList con los objetos Especie de la lista.
     */
    public ArrayList<Especie> pokedex() {

        connect();
        ArrayList<Especie> pokedex = new ArrayList<Especie>();
        Statement st = null;
        ResultSet rs = null;
        try {
            st = conn.createStatement();
            String query = "SELECT * FROM especie";
            rs = st.executeQuery(query);
            while (rs.next()) {
                Especie especie = new Especie();
                especie.setN_pokedex(rs.getInt("n_pokedex"));
                especie.setNombre(rs.getString("nombre"));
                especie.setDescripcion(rs.getString("descripcion"));
                especie.setEvoluciona(rs.getInt("evoluciona"));
                pokedex.add(especie);
            }
        } catch (SQLException e) {
            System.out.println("Error en la ejecucion del metodo: " + e.getMessage());
            pokedex = null;
        } catch (Exception e) {
            System.out.println("Error al obtener los datos de las especies: " + e.getMessage());
            pokedex = null;
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (st != null)
                    st.close();
            } catch (SQLException e) {
                System.out.println("Error crítico al finalizar la ejecución: " + e.getMessage());
                pokedex = null;
            }
        }
        return pokedex;
    }

    /**
     * metodo para la extracción de los datos de la pokedex ordenado ascendentemente
     * por el número en la pokedex y el número de encuentro.
     *
     * @return ArrayList con los objetos Especie de la lista.
     */

    public ArrayList<Ejemplar> getEjemplares() {

        connect();
        ArrayList<Ejemplar> ejemplares = new ArrayList<Ejemplar>();
        Statement st = null;
        ResultSet rs = null;
        try {
            st = conn.createStatement();
            String query = "SELECT * FROM ejemplar ORDER BY n_pokedex ASC, n_encuentro ASC";
            rs = st.executeQuery(query);
            while (rs.next()) {
                Ejemplar ejemplar = new Ejemplar();
                ejemplar.setN_pokedex(rs.getInt("n_pokedex"));
                ejemplar.setN_encuentro(rs.getInt("n_encuentro"));
                ejemplar.setApodo(rs.getString("apodo"));
                ejemplar.setSexo(rs.getString("sexo").charAt(0));
                ejemplar.setNivel(rs.getInt("nivel"));
                ejemplar.setInfectado(rs.getInt("infectado"));
                ejemplares.add(ejemplar);
            }
            //tratamiento de excepciones.
        } catch (SQLException e) {
            System.out.println("Error en la ejecucion del metodo " + e.getMessage());
            ejemplares = null;
        } catch (Exception e) {
            System.out.println("Error al obtener los datos de las especies:" + e.getMessage());
            ejemplares = null;
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (st != null)
                    st.close();
            } catch (SQLException e) {
                System.out.println("Error crítico al finalizar la ejecución " + e.getMessage());
                ejemplares = null;
            }
        }
        return ejemplares;
    }

    /**
     * metodo para la simulación del coronapokerus.
     * @param ejemplares lista de ejemplares que se pueden infectar.
     * @param dias dias que dura la pandemia.
     * @return cantidad de infectados al final de la pandemia.
     */
    public int coronapokerus(ArrayList<Ejemplar> ejemplares, int dias) {
        connect();
        PreparedStatement pst = null;
        int res;
        int infected = 0;
        try {
            conn.setAutoCommit(false);
            String query = "UPDATE ejemplar SET infectado = 1 WHERE infectado = 0 AND n_pokedex = ? AND n_encuentro = ?";
            pst = conn.prepareStatement(query);
            if (dias > 0) {
                // eleccion del paciente 0

                //simulación de la propagacion del virus.
                for (int i = 0; i< dias; i++) {
                    int dia = 0;//variable que almacena los infectados por día.
                    // eleccion del paciente 0, el unico caso en el que la primera vuelta del do tiene sentido
                    do{
                        Ejemplar s2 = Ejemplar.ejemplarRandom(ejemplares);
                        if (s2.getInfectado() == 0) {
                            s2.setInfectado(1);
                            pst.setInt(1, s2.getN_pokedex());
                            pst.setInt(2, s2.getN_encuentro());
                            res = pst.executeUpdate();
                            dia++;
                        }
                    }while(dia< infected);
                    infected += dia;
                    conn.commit();
                }
                //actualización del commit a su estado por defecto.
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.out.println("Error de SQL al actualizar los datos de los infectados " + e.getMessage());
            res = 0;
        } catch (Exception e) {
            System.out.println("Error en la ejecucion del metodo " + e.getMessage());
            res = 0;
        } finally {
            try {
                if (conn!=null && res==0)
                    conn.rollback();
                if (pst != null)
                    pst.close();
            } catch (SQLException e) {
                System.out.println("Error en la ejecucion del metodo " + e.getMessage());
            }
        }
        return infected;
    }

    /**
     * metodo que extrae el sprite deseado de la base de datos
     * @param n_pokedex id del pokemon del que se quiere su sprite
     * @param filename ruta donde se almacenara el la imagen.
     * @return booleano que indica si la operación se ha realizado con exito.
     */

    public boolean getSprite(int n_pokedex, String filename) {

        if (filename == null || n_pokedex == 0)
            return false;
        connect();
        String query = "SELECT sprite FROM especie WHERE n_pokedex=(?)";
        ResultSet rs = null;
        PreparedStatement pst = null;
        FileOutputStream fos;
        try {
            //Inicializo la query
            pst = conn.prepareStatement(query);
            pst.setInt(1, n_pokedex);
            rs = pst.executeQuery();
            boolean result = false;
            while (rs.next() && !result) {
                InputStream input = rs.getBinaryStream(1);
                if (input != null) {
                    File file = new File(filename);
                    fos = new FileOutputStream(file);
                    byte[] buffer = new byte[2048];
                    int c;
                    while ((c = input.read(buffer)) > 0) {
                        fos.write(buffer, 0, c);
                    }
                    result = true;
                }
            }
        } catch (SQLException | IOException e) {
            System.out.println("Error al extraer el sprite de la base de datos" + e.getMessage());
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (pst != null)
                    pst.close();
                if (fos != null)
                    fos.close();
            } catch (SQLException | IOException e) {
                System.out.println("error de SQL al finalizar la ejecución" + e.getMessage());
                result = false;
            }
        }
        return result;
    }
}
