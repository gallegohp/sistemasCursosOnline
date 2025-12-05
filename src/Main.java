// java
import ScannerUtils.ScannerUtils;
import model.Categoria;
import model.Curso;
import respository.CategoriaRepository;
import respository.CursoRepository;
import respository.EstudianteRepository;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        ScannerUtils scannerUtils = new ScannerUtils();
        CategoriaRepository categoriaRepository = new CategoriaRepository();
        CursoRepository cursoRepository = new CursoRepository();
        EstudianteRepository estudianteRepository = new EstudianteRepository();

        final String SEP = "--------------------------------------------------";

        System.out.println(SEP);
        System.out.println("Bienvenido al sistema de gestión de cursos online.");
        System.out.println(SEP);

        boolean esActivo = true;
        while (esActivo) {
            System.out.println();
            System.out.println(SEP);
            System.out.println("Seleccione una opción:");
            System.out.println("1. Gestionar Estudiantes");
            System.out.println("2. Gestionar Cursos");
            System.out.println("3. Inscribir Estudiante en Curso");
            System.out.println("4. Salir");
            System.out.println("5. Gestionar Categorías");
            System.out.println(SEP);

            int opcion = scannerUtils.leerEntero("Opción: ");
            System.out.println();

            switch (opcion) {
                case 1:
                    boolean volverEstudiantes = false;
                    while (!volverEstudiantes) {
                        System.out.println(SEP);
                        System.out.println("Gestión de Estudiantes");
                        System.out.println(SEP);
                        System.out.println("1. Registrar estudiante");
                        System.out.println("2. Listar estudiantes");
                        System.out.println("3. Consultar estudiante por ID");
                        System.out.println("4. Actualizar estudiante");
                        System.out.println("5. Eliminar estudiante");
                        System.out.println("6. Volver");
                        System.out.println(SEP);

                        int opEst = scannerUtils.leerEntero("Opción: ");
                        System.out.println();

                        switch (opEst) {
                            case 1:
                                System.out.println(SEP);
                                String nombreEst = scannerUtils.leerCadena("Nombre del estudiante: ").trim();
                                String emailEst = scannerUtils.leerCadena("Email del estudiante: ").trim();
                                if (nombreEst.isBlank() || emailEst.isBlank()) {
                                    System.out.println("Nombre y email son obligatorios. Operación cancelada.");
                                    System.out.println(SEP);
                                    break;
                                }
                                if (estudianteRepository.emailExiste(emailEst)) {
                                    System.out.println("Ya existe un estudiante con ese email. Operación cancelada.");
                                    System.out.println(SEP);
                                    break;
                                }
                                // asignar cursos (opcional, validar existencia)
                                System.out.println("Ingrese IDs de cursos a asignar separados por comas (enter para ninguno):");
                                String entradaCursos = scannerUtils.leerCadena("Cursos: ").trim();
                                List<Integer> cursosAsignados = null;
                                if (!entradaCursos.isBlank()) {
                                    String[] parts = entradaCursos.split(",");
                                    cursosAsignados = new java.util.ArrayList<>();
                                    boolean cursosInvalidos = false;
                                    for (String p : parts) {
                                        try {
                                            int cid = Integer.parseInt(p.trim());
                                            if (cursoRepository.buscarCursoPorId(cid) == null) {
                                                System.out.println("Curso con ID " + cid + " no existe.");
                                                cursosInvalidos = true;
                                                break;
                                            }
                                            cursosAsignados.add(cid);
                                        } catch (NumberFormatException ex) {
                                            System.out.println("Entrada de curso inválida: " + p);
                                            cursosInvalidos = true;
                                            break;
                                        }
                                    }
                                    if (cursosInvalidos) {
                                        System.out.println("Operación cancelada por cursos inválidos.");
                                        System.out.println(SEP);
                                        break;
                                    }
                                }
                                model.Estudiante nuevoEst = new model.Estudiante(0, nombreEst, emailEst);
                                int idEst = estudianteRepository.crearEstudiante(nuevoEst, cursosAsignados);
                                if (idEst > 0) {
                                    System.out.println("Estudiante registrado con id: " + idEst);
                                } else {
                                    System.out.println("Error al registrar estudiante.");
                                }
                                System.out.println(SEP);
                                break;
                            case 2:
                                System.out.println(SEP);
                                List<model.Estudiante> estudiantes = estudianteRepository.listarEstudiantes();
                                if (estudiantes.isEmpty()) {
                                    System.out.println("No hay estudiantes registrados.");
                                } else {
                                    System.out.println("Listado de estudiantes:");
                                    for (model.Estudiante e : estudiantes) {
                                        System.out.println("- " + e.getId() + ": " + e.getNombre() + " <" + e.getEmail() + ">");
                                    }
                                }
                                System.out.println(SEP);
                                break;
                            case 3:
                                System.out.println(SEP);
                                int idBuscarEst = scannerUtils.leerEntero("Ingrese ID del estudiante: ");
                                model.Estudiante encontradoEst = estudianteRepository.buscarEstudiantePorId(idBuscarEst);
                                if (encontradoEst == null) {
                                    System.out.println("No se encontró estudiante con ID " + idBuscarEst);
                                } else {
                                    System.out.println("Estudiante: " + encontradoEst.getId() + " - " + encontradoEst.getNombre());
                                    System.out.println("Email: " + encontradoEst.getEmail());
                                    List<model.Curso> cursosEst = encontradoEst.getCursos();
                                    if (cursosEst == null || cursosEst.isEmpty()) {
                                        System.out.println("Cursos: Ninguno");
                                    } else {
                                        System.out.println("Cursos inscritos:");
                                        for (model.Curso c : cursosEst) {
                                            System.out.println("- " + c.getId() + ": " + c.getTitulo());
                                        }
                                    }
                                }
                                System.out.println(SEP);
                                break;
                            case 4:
                                System.out.println(SEP);
                                int idAct = scannerUtils.leerEntero("Ingrese ID del estudiante a actualizar: ");
                                model.Estudiante estExist = estudianteRepository.buscarEstudiantePorId(idAct);
                                if (estExist == null) {
                                    System.out.println("No existe estudiante con ID " + idAct);
                                    System.out.println(SEP);
                                    break;
                                }
                                String nuevoNombre = scannerUtils.leerCadena("Nuevo nombre (enter para mantener " + estExist.getNombre() + "): ").trim();
                                String nuevoEmail = scannerUtils.leerCadena("Nuevo email (enter para mantener " + estExist.getEmail() + "): ").trim();
                                String nombreFinal = nuevoNombre.isBlank() ? estExist.getNombre() : nuevoNombre;
                                String emailFinal = nuevoEmail.isBlank() ? estExist.getEmail() : nuevoEmail;
                                if (!emailFinal.equalsIgnoreCase(estExist.getEmail()) && estudianteRepository.emailExiste(emailFinal)) {
                                    System.out.println("El email indicado ya está en uso. Operación cancelada.");
                                    System.out.println(SEP);
                                    break;
                                }
                                // asignar cursos nuevos
                                System.out.println("Ingrese IDs de cursos a asignar separados por comas (enter para mantener actuales):");
                                String entradaCursosAct = scannerUtils.leerCadena("Cursos: ").trim();
                                List<Integer> cursosNuevos = null;
                                if (!entradaCursosAct.isBlank()) {
                                    String[] partsAct = entradaCursosAct.split(",");
                                    cursosNuevos = new java.util.ArrayList<>();
                                    boolean invalid = false;
                                    for (String p : partsAct) {
                                        try {
                                            int cid = Integer.parseInt(p.trim());
                                            if (cursoRepository.buscarCursoPorId(cid) == null) {
                                                System.out.println("Curso con ID " + cid + " no existe.");
                                                invalid = true;
                                                break;
                                            }
                                            cursosNuevos.add(cid);
                                        } catch (NumberFormatException ex) {
                                            System.out.println("Entrada de curso inválida: " + p);
                                            invalid = true;
                                            break;
                                        }
                                    }
                                    if (invalid) {
                                        System.out.println("Operación cancelada por cursos inválidos.");
                                        System.out.println(SEP);
                                        break;
                                    }
                                }
                                boolean actualizado = estudianteRepository.actualizarEstudiante(idAct, nombreFinal, emailFinal, cursosNuevos);
                                if (actualizado) {
                                    System.out.println("Estudiante actualizado: " + idAct);
                                } else {
                                    System.out.println("Error al actualizar estudiante.");
                                }
                                System.out.println(SEP);
                                break;
                            case 5:
                                System.out.println(SEP);
                                int idDel = scannerUtils.leerEntero("Ingrese ID del estudiante a eliminar: ");
                                model.Estudiante estDel = estudianteRepository.buscarEstudiantePorId(idDel);
                                if (estDel == null) {
                                    System.out.println("No existe estudiante con ID " + idDel);
                                } else {
                                    estudianteRepository.eliminarEstudiante(idDel);
                                    System.out.println("Estudiante eliminado: " + estDel.getNombre());
                                }
                                System.out.println(SEP);
                                break;
                            case 6:
                                volverEstudiantes = true;
                                break;
                            default:
                                System.out.println(SEP);
                                System.out.println("Opción de estudiantes no válida.");
                                System.out.println(SEP);
                        }
                    }
                    break;
                case 2:
                    // Submenú de Cursos
                    boolean volverCursos = false;
                    while (!volverCursos) {
                        System.out.println(SEP);
                        System.out.println("Gestión de Cursos");
                        System.out.println(SEP);
                        System.out.println("1. Crear curso");
                        System.out.println("2. Listar cursos");
                        System.out.println("3. Consultar curso por ID");
                        System.out.println("4. Actualizar curso");
                        System.out.println("5. Eliminar curso");
                        System.out.println("6. Volver");
                        System.out.println(SEP);

                        int opCurso = scannerUtils.leerEntero("Opción: ");
                        System.out.println();

                        switch (opCurso) {
                            case 1:
                                System.out.println(SEP);
                                String titulo = scannerUtils.leerCadena("Título del curso: ").trim();
                                if (titulo.isBlank()) {
                                    System.out.println("Título vacío. Operación cancelada.");
                                    System.out.println(SEP);
                                    break;
                                }
                                String descripcion = scannerUtils.leerCadena("Descripción del curso: ").trim();

                                // Mostrar categorías para elegir (requisito: no permitir crear curso sin categoría)
                                List<Categoria> cats = categoriaRepository.listarCategorias();
                                if (cats.isEmpty()) {
                                    System.out.println("No hay categorías. Cree una categoría antes de asignarla. No se puede crear curso sin categoría.");
                                    System.out.println(SEP);
                                    break;
                                }
                                System.out.println("Categorías disponibles:");
                                for (Categoria c : cats) {
                                    System.out.println("- " + c.getId() + ": " + c.getNombre());
                                }
                                int catId = scannerUtils.leerEntero("ID de la categoría a asignar: ");
                                if (catId <= 0) {
                                    System.out.println("ID de categoría no válido. Operación cancelada.");
                                    System.out.println(SEP);
                                    break;
                                }
                                Categoria catExist = categoriaRepository.buscarCategoriaPorId(catId);
                                if (catExist == null) {
                                    System.out.println("Categoría no encontrada. Operación cancelada.");
                                    System.out.println(SEP);
                                    break;
                                }

                                Curso nuevo = new Curso(0, titulo, descripcion, catId);
                                int idCreado = cursoRepository.crearCurso(nuevo);
                                if (idCreado > 0) {
                                    System.out.println("Curso creado con id: " + idCreado);
                                } else {
                                    System.out.println("Error al crear el curso.");
                                }
                                System.out.println(SEP);
                                break;
                            case 2:
                                System.out.println(SEP);
                                List<Curso> cursos = cursoRepository.listarCursos();
                                if (cursos.isEmpty()) {
                                    System.out.println("No hay cursos registrados.");
                                } else {
                                    System.out.println("Listado de cursos:");
                                    for (Curso c : cursos) {
                                        String nombreCat = c.getCategoria() != null ? c.getCategoria().getNombre() : "Sin categoría";
                                        System.out.println("- " + c.getId() + ": " + c.getTitulo() + " [" + nombreCat + "]");
                                    }
                                }
                                System.out.println(SEP);
                                break;
                            case 3:
                                System.out.println(SEP);
                                int idBuscar = scannerUtils.leerEntero("Ingrese ID del curso: ");
                                Curso encontrado = cursoRepository.buscarCursoPorId(idBuscar);
                                if (encontrado == null) {
                                    System.out.println("No se encontró curso con ID " + idBuscar);
                                } else {
                                    String catName = encontrado.getCategoria() != null ? encontrado.getCategoria().getNombre() : "Sin categoría";
                                    System.out.println("Curso: " + encontrado.getId() + " - " + encontrado.getTitulo());
                                    System.out.println("Descripción: " + encontrado.getDescripcion());
                                    System.out.println("Categoría: " + catName + " (ID: " + encontrado.getCategoriaId() + ")");
                                }
                                System.out.println(SEP);
                                break;
                            case 4:
                                System.out.println(SEP);
                                int idActualizar = scannerUtils.leerEntero("Ingrese ID del curso a actualizar: ");
                                Curso existente = cursoRepository.buscarCursoPorId(idActualizar);
                                if (existente == null) {
                                    System.out.println("No existe curso con ID " + idActualizar);
                                    System.out.println(SEP);
                                    break;
                                }
                                String nuevoTitulo = scannerUtils.leerCadena("Nuevo título (dejar vacío para no cambiar): ").trim();
                                String nuevaDesc = scannerUtils.leerCadena("Nueva descripción (dejar vacío para no cambiar): ").trim();

                                // mostrar categorías y permitir cambio
                                List<Categoria> categoriasDisponibles = categoriaRepository.listarCategorias();
                                int nuevaCatId = existente.getCategoriaId();
                                if (!categoriasDisponibles.isEmpty()) {
                                    System.out.println("Categorías:");
                                    for (Categoria c : categoriasDisponibles) {
                                        System.out.println("- " + c.getId() + ": " + c.getNombre());
                                    }
                                    String entradaCat = scannerUtils.leerCadena("ID nueva categoría (enter para mantener " + nuevaCatId + "): ").trim();
                                    if (!entradaCat.isBlank()) {
                                        try {
                                            int parsed = Integer.parseInt(entradaCat);
                                            if (categoriaRepository.buscarCategoriaPorId(parsed) != null) {
                                                nuevaCatId = parsed;
                                            } else {
                                                System.out.println("Categoría indicada no existe. Se mantiene la anterior.");
                                            }
                                        } catch (NumberFormatException ex) {
                                            System.out.println("Entrada inválida. Se mantiene la categoría anterior.");
                                        }
                                    }
                                }

                                String tituloFinal = nuevoTitulo.isBlank() ? existente.getTitulo() : nuevoTitulo;
                                String descFinal = nuevaDesc.isBlank() ? existente.getDescripcion() : nuevaDesc;

                                cursoRepository.actualizarCurso(idActualizar, tituloFinal, descFinal, nuevaCatId);
                                System.out.println("Curso actualizado: " + idActualizar);
                                System.out.println(SEP);
                                break;
                            case 5:
                                System.out.println(SEP);
                                int idEliminar = scannerUtils.leerEntero("Ingrese ID del curso a eliminar: ");
                                Curso cursoEliminar = cursoRepository.buscarCursoPorId(idEliminar);
                                if (cursoEliminar == null) {
                                    System.out.println("No existe curso con ID " + idEliminar);
                                } else {
                                    cursoRepository.eliminarCurso(idEliminar);
                                    System.out.println("Curso eliminado: " + cursoEliminar.getTitulo());
                                }
                                System.out.println(SEP);
                                break;
                            case 6:
                                volverCursos = true;
                                break;
                            default:
                                System.out.println(SEP);
                                System.out.println("Opción de cursos no válida.");
                                System.out.println(SEP);
                        }
                    }
                    break;
                // java
                case 3:
                    System.out.println(SEP);
                    int idInscribir = scannerUtils.leerEntero("Ingrese ID del estudiante a inscribir en cursos: ");
                    model.Estudiante estParaInscribir = estudianteRepository.buscarEstudiantePorId(idInscribir);
                    if (estParaInscribir == null) {
                        System.out.println("No existe estudiante con ID " + idInscribir);
                        System.out.println(SEP);
                        break;
                    }

                    // Mostrar cursos actuales
                    List<model.Curso> cursosActuales = estParaInscribir.getCursos();
                    if (cursosActuales == null || cursosActuales.isEmpty()) {
                        System.out.println("Cursos actuales: Ninguno");
                    } else {
                        System.out.println("Cursos actuales:");
                        for (model.Curso c : cursosActuales) {
                            System.out.println("- " + c.getId() + ": " + c.getTitulo());
                        }
                    }

                    System.out.println();
                    System.out.println("Ingrese IDs de cursos a asignar separados por comas (enter para cancelar):");
                    String entradaCursosIns = scannerUtils.leerCadena("Cursos: ").trim();
                    if (entradaCursosIns.isBlank()) {
                        System.out.println("Operación cancelada.");
                        System.out.println(SEP);
                        break;
                    }

                    String[] partesIns = entradaCursosIns.split(",");
                    List<Integer> nuevosIds = new java.util.ArrayList<>();
                    boolean invalidos = false;

                    // iniciar con IDs actuales para evitar duplicados (si los hay)
                    if (cursosActuales != null) {
                        for (model.Curso c : cursosActuales) {
                            nuevosIds.add(c.getId());
                        }
                    }

                    for (String p : partesIns) {
                        try {
                            int cid = Integer.parseInt(p.trim());
                            if (cid <= 0) {
                                System.out.println("ID de curso inválido: " + p);
                                invalidos = true;
                                break;
                            }
                            if (cursoRepository.buscarCursoPorId(cid) == null) {
                                System.out.println("Curso con ID " + cid + " no existe.");
                                invalidos = true;
                                break;
                            }
                            if (!nuevosIds.contains(cid)) {
                                nuevosIds.add(cid);
                            }
                        } catch (NumberFormatException ex) {
                            System.out.println("Entrada de curso inválida: " + p);
                            invalidos = true;
                            break;
                        }
                    }

                    if (invalidos) {
                        System.out.println("Operación cancelada por cursos inválidos.");
                        System.out.println(SEP);
                        break;
                    }

                    // Usar actualizarEstudiante para reemplazar las relaciones con la lista combinada
                    boolean okIns = estudianteRepository.actualizarEstudiante(
                            idInscribir,
                            estParaInscribir.getNombre(),
                            estParaInscribir.getEmail(),
                            nuevosIds
                    );

                    if (okIns) {
                        System.out.println("Cursos asignados correctamente al estudiante ID " + idInscribir);
                    } else {
                        System.out.println("Error al asignar cursos.");
                    }
                    System.out.println(SEP);
                    break;
                case 4:
                    System.out.println(SEP);
                    System.out.println("Saliendo...");
                    System.out.println(SEP);
                    esActivo = false;
                    break;
                case 5:
                    System.out.println(SEP);
                    System.out.println("Gestión de Categorías");
                    System.out.println(SEP);
                    int eleccionCategoria = scannerUtils.leerEntero(
                            "1. Crear Categoria\n2. Listar Categorias\n3. Eliminar Categoria\n4. Actualizar Categoria\n5. Buscar Categoria por ID\nOpción: ");
                    System.out.println();

                    switch (eleccionCategoria) {
                        case 1:
                            System.out.println(SEP);
                            String nombreCategoria = scannerUtils.leerCadena("Ingrese el nombre de la nueva categoría: ").trim();
                            if (nombreCategoria.isBlank()) {
                                System.out.println("El nombre de la categoría no puede estar vacío.");
                            } else {
                                int idCreada = categoriaRepository.crearCategoria(nombreCategoria);
                                if (idCreada > 0) {
                                    System.out.println("Categoría creada con id: " + idCreada);
                                } else {
                                    System.out.println("Error al crear la categoría.");
                                }
                            }
                            System.out.println(SEP);
                            break;
                        case 2:
                            System.out.println(SEP);
                            List<Categoria> categorias = categoriaRepository.listarCategorias();
                            if (categorias.isEmpty()) {
                                System.out.println("No hay categorías registradas.");
                            } else {
                                System.out.println("Listado de Categorías:");
                                for (Categoria categoria : categorias) {
                                    System.out.println("- " + categoria.getId() + ": " + categoria.getNombre());
                                }
                            }
                            System.out.println(SEP);
                            break;
                        case 3:
                            System.out.println(SEP);
                            int idEliminar = scannerUtils.leerEntero("Ingrese el ID de la categoría a eliminar: ");
                            Categoria existEliminar = categoriaRepository.buscarCategoriaPorId(idEliminar);
                            if (existEliminar == null) {
                                System.out.println("No existe categoría con ID " + idEliminar);
                            } else {
                                categoriaRepository.eliminarCategoriaPorId(idEliminar);
                                System.out.println("Categoría eliminada: " + existEliminar.getNombre());
                            }
                            System.out.println(SEP);
                            break;
                        case 4:
                            System.out.println(SEP);
                            int idActualizar = scannerUtils.leerEntero("Ingrese el ID de la categoría a actualizar: ");
                            Categoria existActualizar = categoriaRepository.buscarCategoriaPorId(idActualizar);
                            if (existActualizar == null) {
                                System.out.println("No existe categoría con ID " + idActualizar);
                            } else {
                                String nuevoNombre = scannerUtils.leerCadena("Ingrese el nuevo nombre de la categoría: ").trim();
                                if (nuevoNombre.isBlank()) {
                                    System.out.println("El nombre de la categoría no puede estar vacío.");
                                } else {
                                    categoriaRepository.actualizarCategoria(idActualizar, nuevoNombre);
                                    System.out.println("Categoría actualizada: " + idActualizar + " -> " + nuevoNombre);
                                }
                            }
                            System.out.println(SEP);
                            break;
                        case 5:
                            System.out.println(SEP);
                            int idBuscar = scannerUtils.leerEntero("Ingrese el ID de la categoría a buscar: ");
                            Categoria categoriaEncontrada = categoriaRepository.buscarCategoriaPorId(idBuscar);
                            if (categoriaEncontrada != null) {
                                System.out.println("Categoría encontrada: " + categoriaEncontrada);
                            } else {
                                System.out.println("No se encontró categoría con ID " + idBuscar);
                            }
                            System.out.println(SEP);
                            break;
                        default:
                            System.out.println(SEP);
                            System.out.println("Opción de categorías no válida.");
                            System.out.println(SEP);
                    }
                    break;
                default:
                    System.out.println(SEP);
                    System.out.println("Opción no válida. Intente nuevamente.");
                    System.out.println(SEP);
            }
        }

        System.out.println(SEP);
        System.out.println("Programa finalizado.");
        System.out.println(SEP);

        scannerUtils.close();
    }
}