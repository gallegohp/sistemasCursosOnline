package respository;

import db.Conexion;
import model.Categoria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaRepository {

    public int crearCategoria(String nombre) {
        // Lógica para crear una categoría en la base de datos
        //consulta a la base de datos
        String sql = "INSERT INTO categoria (titulo_categoria) VALUES (?)";
        int generatedId = -1;
        try (Connection connection = Conexion.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                                                                                    //devuelve el id generado por la base de datos
            preparedStatement.setString(1, nombre);
            int affectedRows = preparedStatement.executeUpdate();
            //comprobante que se inserto correctamente
            //si no se inserto ninguna fila, lanza una excepcion
            if (affectedRows == 0) {
                throw new SQLException("Crear categoría falló, no se insertaron filas.");
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                //este resulsetset tiene las llaves generadas anteriormente por el preparedstatement
                if (generatedKeys.next()) {
                    generatedId = generatedKeys.getInt(1);//obtenemos el id generado, en la posicion 1
                } else {
                    throw new SQLException("Crear categoría falló, no se obtuvo el ID generado.");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
            return generatedId;
        }

        public List<Categoria> listarCategorias() {
            String sql = "SELECT * FROM categoria";
            List<Categoria> categorias = new ArrayList<>();

            try (Connection connection = Conexion.getConnection();
                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {

                while (resultSet.next()) {
                    int id = resultSet.getInt("id_categoria");
                    String titulo = resultSet.getString("titulo_categoria");
                    categorias.add(new Categoria(id, titulo));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return categorias;
        }

        public void eliminarCategoriaPorId (int id) {
            String sql = "DELETE FROM categoria WHERE id_categoria = ?";

            try (Connection connection = Conexion.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

                preparedStatement.setInt(1, id);
                preparedStatement.executeUpdate();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void actualizarCategoria(int id, String nuevoNombre) {
            String sql = "UPDATE categoria SET titulo_categoria = ? WHERE id_categoria = ?";

            try (Connection connection = Conexion.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

                preparedStatement.setString(1, nuevoNombre);
                preparedStatement.setInt(2, id);
                preparedStatement.executeUpdate();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public Categoria buscarCategoriaPorId(int id) {
            String sql = "SELECT * FROM categoria WHERE id_categoria = ?";
            Categoria categoriaEncontrada = null;

            try (Connection connection = Conexion.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

                preparedStatement.setInt(1, id);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                       int categoriaId = resultSet.getInt("id_categoria");
                       String titulo = resultSet.getString("titulo_categoria");
                       categoriaEncontrada = new Categoria(categoriaId, titulo);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return categoriaEncontrada;
        }





}
