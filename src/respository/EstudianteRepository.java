package respository;
import db.Conexion;
import model.Curso;
import model.Estudiante;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstudianteRepository {

    private final CursoRepository cursoRepo = new CursoRepository();

    public boolean emailExiste(String email) {
        String sql = "SELECT 1 FROM estudiante WHERE email_estudiante = ? LIMIT 1";
        try (Connection connection = Conexion.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public int crearEstudiante(Estudiante estudiante, List<Integer> cursosIds) {
        String sqlEst = "INSERT INTO estudiante (nombre_estudiante, email_estudiante) VALUES (?, ?)";
        String sqlInsCurso = "INSERT INTO curso_estudiante (id_curso, id_estudiante) VALUES (?, ?)";
        int generatedId = -1;

        try (Connection connection = Conexion.getConnection()) {
            connection.setAutoCommit(false);

            // insertar estudiante
            try (PreparedStatement ps = connection.prepareStatement(sqlEst, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, estudiante.getNombre());
                ps.setString(2, estudiante.getEmail());
                int affected = ps.executeUpdate();
                if (affected == 0) throw new SQLException("Crear estudiante falló, no se insertaron filas.");
                try (ResultSet gk = ps.getGeneratedKeys()) {
                    if (gk.next()) generatedId = gk.getInt(1);
                    else throw new SQLException("No se obtuvo ID generado.");
                }
            }

            // validar y asociar cursos
            if (cursosIds != null) {
                for (Integer cid : cursosIds) {
                    if (cursoRepo.buscarCursoPorId(cid) == null) {
                        connection.rollback();
                        return -1; // curso inexistente
                    }
                    try (PreparedStatement ps2 = connection.prepareStatement(sqlInsCurso)) {
                        ps2.setInt(1, cid);
                        ps2.setInt(2, generatedId);
                        ps2.executeUpdate();
                    }
                }
            }

            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
            generatedId = -1;
        }
        return generatedId;
    }

    public List<Estudiante> listarEstudiantes() {
        String sql = "SELECT id_estudiante, nombre_estudiante, email_estudiante FROM estudiante";
        List<Estudiante> lista = new ArrayList<>();
        try (Connection connection = Conexion.getConnection();
             Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id_estudiante");
                String nombre = rs.getString("nombre_estudiante");
                String email = rs.getString("email_estudiante");
                Estudiante e = new Estudiante(id, nombre, email);
                e.setCursos(listarCursosPorEstudiante(id));
                lista.add(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public Estudiante buscarEstudiantePorId(int id) {
        String sql = "SELECT id_estudiante, nombre_estudiante, email_estudiante FROM estudiante WHERE id_estudiante = ?";
        Estudiante estudiante = null;
        try (Connection connection = Conexion.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String nombre = rs.getString("nombre_estudiante");
                    String email = rs.getString("email_estudiante");
                    estudiante = new Estudiante(id, nombre, email);
                    estudiante.setCursos(listarCursosPorEstudiante(id));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return estudiante;
    }

    private List<Curso> listarCursosPorEstudiante(int estudianteId) {
        String sql = "SELECT c.id_curso, c.titulo_curso, c.descripcion_curso, c.id_categoria " +
                "FROM curso c JOIN curso_estudiante ce ON c.id_curso = ce.id_curso " +
                "WHERE ce.id_estudiante = ?";
        List<Curso> cursos = new ArrayList<>();
        try (Connection connection = Conexion.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, estudianteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id_curso");
                    String titulo = rs.getString("titulo_curso");
                    String descripcion = rs.getString("descripcion_curso");
                    int catId = rs.getInt("id_categoria");
                    Curso c = new Curso(id, titulo, descripcion, catId);
                    cursos.add(c);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cursos;
    }

    public boolean actualizarEstudiante(int id, String nuevoNombre, String nuevoEmail, List<Integer> nuevosCursos) {
        String sqlUpd = "UPDATE estudiante SET nombre_estudiante = ?, email_estudiante = ? WHERE id_estudiante = ?";
        String sqlDelCursos = "DELETE FROM curso_estudiante WHERE id_estudiante = ?";
        String sqlInsCurso = "INSERT INTO curso_estudiante (id_curso, id_estudiante) VALUES (?, ?)";
        try (Connection connection = Conexion.getConnection()) {
            connection.setAutoCommit(false);

            // validar email único (si cambia)
            Estudiante actual = buscarEstudiantePorId(id);
            if (actual == null) return false;
            if (!actual.getEmail().equalsIgnoreCase(nuevoEmail) && emailExiste(nuevoEmail)) {
                return false;
            }

            try (PreparedStatement ps = connection.prepareStatement(sqlUpd)) {
                ps.setString(1, nuevoNombre);
                ps.setString(2, nuevoEmail);
                ps.setInt(3, id);
                ps.executeUpdate();
            }

            try (PreparedStatement psDel = connection.prepareStatement(sqlDelCursos)) {
                psDel.setInt(1, id);
                psDel.executeUpdate();
            }

            if (nuevosCursos != null) {
                for (Integer cid : nuevosCursos) {
                    if (cursoRepo.buscarCursoPorId(cid) == null) {
                          connection.rollback();
                        return false;
                    }
                    try (PreparedStatement psIns = connection.prepareStatement(sqlInsCurso)) {
                        psIns.setInt(1, cid);
                        psIns.setInt(2, id);
                        psIns.executeUpdate();
                    }
                }
            }

            connection.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void eliminarEstudiante(int id) {
        String sqlDelCursos = "DELETE FROM curso_estudiante WHERE id_estudiante = ?";
        String sqlDelEst = "DELETE FROM estudiante WHERE id_estudiante = ?";
        try (Connection connection = Conexion.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sqlDelCursos)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            try (PreparedStatement ps2 = connection.prepareStatement(sqlDelEst)) {
                ps2.setInt(1, id);
                ps2.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}