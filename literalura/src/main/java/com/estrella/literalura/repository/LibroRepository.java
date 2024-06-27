package com.estrella.literalura.repository;

import com.estrella.literalura.model.Autor;
import com.estrella.literalura.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LibroRepository extends JpaRepository<Libro, Long> {

    @Query("SELECT l from Libro l WHERE l.titulo ILIKE %:titulo%")
    Libro buscarLibro(String titulo);
    @Query("SELECT a FROM Libro l JOIN l.autores a")
    List<Autor> listaDeAutores();
    @Query("SELECT a FROM Libro l JOIN l.autores a WHERE a.anioNacimiento < :anio AND a.anioMuerte > :anio")
    List<Autor> autoresPorAnio(String anio);

    @Query("SELECT l FROM Libro l WHERE l.idiomas = :idioma")
    List<Libro> libroPoridioma(List<String> idioma);
}
