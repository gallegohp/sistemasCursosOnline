package model;

public class Cursos {
    private String  titulo;
    private String descripcion;
    private int categoriaId;

    public Cursos( String titulo, String descripcion, int categoriaId) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.categoriaId = categoriaId;
    }

    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public int getCategoriaId() {
        return categoriaId;
    }
    public void setCategoriaId(int categoriaId) {
        this.categoriaId = categoriaId;
    }

    }
}
