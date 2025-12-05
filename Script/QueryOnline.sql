create database cursosOnline;
use cursosOnline;

-- Tabla de categorías
create table categoria(
    id_categoria int auto_increment primary key,
    titulo_categoria varchar(50)
);

-- Tabla de cursos
create table curso(
    id_curso int auto_increment primary key,
    titulo_curso varchar(50),
    descripcion_curso varchar(200),	
    id_categoria int,
    foreign key (id_categoria) references categoria(id_categoria)
);

-- Tabla de estudiantes
create table estudiante (
    id_estudiante int auto_increment primary key,
    nombre_estudiante varchar(100) not null,
    email_estudiante varchar(100) not null
);

-- Tabla intermedia para relación N:N
create table curso_estudiante (
    id_curso int,
    id_estudiante int,
    primary key (id_curso, id_estudiante),
    foreign key (id_curso) references curso(id_curso),
    foreign key (id_estudiante) references estudiante(id_estudiante)
);

select * from categoria;
select * from curso;
select * from curso_estudiante;



