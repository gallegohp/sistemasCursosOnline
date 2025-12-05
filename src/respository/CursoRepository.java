package respository;

import db.Conexion;
import model.Curso;
import model.Categoria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CursoRepository {

    public int crearCurso(Curso curso) {
        String sql = "INSERT INTO curso (titulo_curso, descripcion_curso, id_categoria) VALUES (?, ?, ?)";
        int generatedId = -1;
        try (Connection connection = Conexion.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, curso.getTitulo());
            preparedStatement.setString(2, curso.getDescripcion());
            preparedStatement.setInt(3, curso.getCategoriaId());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Crear curso falló, no se insertaron filas.");
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    generatedId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Crear curso falló, no se obtuvo el ID generado.");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return generatedId;
    }

    public List<Curso> listarCursos() {
        String sql = "SELECT id_curso, titulo_curso, descripcion_curso, id_categoria FROM curso";
        List<Curso> cursos = new ArrayList<>();
        CategoriaRepository categoriaRepo = new CategoriaRepository();

        try (Connection connection = Conexion.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id_curso");
                String titulo = resultSet.getString("titulo_curso");
                String descripcion = resultSet.getString("descripcion_curso");
                int categoriaId = resultSet.getInt("id_categoria");

                Curso curso = new Curso(id, titulo, descripcion, categoriaId);
                Categoria cat = categoriaRepo.buscarCategoriaPorId(categoriaId);
                curso.setCategoria(cat);
                cursos.add(curso);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return cursos;
    }

    public Curso buscarCursoPorId(int id) {
        String sql = "SELECT id_curso, titulo_curso, descripcion_curso, id_categoria FROM curso WHERE id_curso = ?";
        Curso cursoEncontrado = null;
        CategoriaRepository categoriaRepo = new CategoriaRepository();

        try (Connection connection = Conexion.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int cursoId = resultSet.getInt("id_curso");
                    String titulo = resultSet.getString("titulo_curso");
                    String descripcion = resultSet.getString("descripcion_curso");
                    int categoriaId = resultSet.getInt("id_categoria");

                    cursoEncontrado = new Curso(cursoId, titulo, descripcion, categoriaId);
                    Categoria cat = categoriaRepo.buscarCategoriaPorId(categoriaId);
                    cursoEncontrado.setCategoria(cat);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return cursoEncontrado;
    }

    public void actualizarCurso(int id, String nuevoTitulo, String nuevaDescripcion, int nuevaCategoriaId) {
        String sql = "UPDATE curso SET titulo_curso = ?, descripcion_curso = ?, id_categoria = ? WHERE id_curso = ?";

        try (Connection connection = Conexion.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, nuevoTitulo);
            preparedStatement.setString(2, nuevaDescripcion);
            preparedStatement.setInt(3, nuevaCategoriaId);
            preparedStatement.setInt(4, id);
            preparedStatement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void eliminarCurso(int id) {
        String sql = "DELETE FROM curso WHERE id_curso = ?";

        try (Connection connection = Conexion.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}