package com.estrella.literalura.Principal;

import com.estrella.literalura.model.*;
import com.estrella.literalura.repository.LibroRepository;
import com.estrella.literalura.service.ConsumeAPI;
import com.estrella.literalura.service.ConvierteDatos;


import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private Scanner teclado = new Scanner(System.in);
    private ConsumeAPI consumeApi = new ConsumeAPI();
    private final String URL_BASE = "https://gutendex.com/books/";
    private final String BUSCAR = "?search=";
    private final String IDIOMA = "?languages=";
    private ConvierteDatos conversor = new ConvierteDatos();
    private LibroRepository repositorio;
    private List<Libro> libros;

    public Principal(LibroRepository repository) {
        this.repositorio = repository;
    }


    public void muestraMenu(){

        var opcion = -1;
        System.out.println("************* Bienvenido a Literalura *************");
        while (opcion != 0){
            var menu = """
                    \nSelecciona la opción que necesites:
                    1 - Buscar y guardar libro
                    2 - Buscar libro por título
                    2 - Buscar libros por idioma
                    3 - Ver lista de libros registrados
                    4 - Ver lista de autores registrados
                    5 - Buscar autores vivos en un año determinado
                    
                    0 - Salir
                    """;
            System.out.println(menu);

            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    guardarLibro();
                    break;
                case 2:
                    buscarLibroPorTitulo();
                    break;
                case 3:
                    buscarLibrosPorIdioma();
                    break;
                case 4:
                    librosRegistrados();
                    break;
                case 5:
                    autoresRegistrados();
                    break;
                case 6:
                    buscarAutoresVivosPorAnio();
                    break;

                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;

                default:
                    System.out.println("Opción inválida");
            }
        }
    }



    private void guardarLibro() {
        System.out.println("Escribe el nombre del libro que deseas guardar en la base de datos: ");
        var tituloLibro = teclado.nextLine();
        var json = consumeApi.obtenerDatos(URL_BASE + BUSCAR + tituloLibro.replace(" ", "+"));
        System.out.println(json);
        Datos datosBusqueda = conversor.obtenerDatos(json, Datos.class);



        Optional<DatosLibro> datosLibro = datosBusqueda.libros().stream()
                .filter(l -> l.titulo().toLowerCase().contains(tituloLibro.toLowerCase()))
                .findFirst();

        if(datosLibro.isPresent()){
            DatosLibro libroEncontrado = datosLibro.get();
            List<DatosAutor> datosAutor = libroEncontrado.autores();
            List<Autor> autores = datosAutor.stream()
                    .map(a -> new Autor(a))
                    .collect(Collectors.toList());

            Libro libro = new Libro(libroEncontrado);
            libro.setAutores(autores);


            System.out.println(libro);
            repositorio.save(libro);
            System.out.println(datosLibro);
        }
    }

    private void buscarLibroPorTitulo() {
        System.out.println("Escribe del libro que deseas buscar: ");
        var titulo = teclado.nextLine();
        Libro libroEncontrado = repositorio.buscarLibro(titulo);
        System.out.println("Libro encontrado: ");
        System.out.println(libroEncontrado);
    }
    private void buscarLibrosPorIdioma() {
        System.out.println("Escribe código del idioma en el cual deseas buscar libros: " +
                "\nes - español" +
                "\nen - inglés" +
                "\npt - portugués" +
                "\nfr - francés" +
                "\nPara ver más códigos de idiomas ingresa aquí: " +
                "\nhttps://es.wiktionary.org/wiki/Wikcionario:C%C3%B3digos_de_idioma");
        List<String> idiomaLibro = Collections.singletonList(teclado.nextLine());
        List<Libro> librosPorIdioma = repositorio.libroPoridioma(idiomaLibro);
        System.out.println("Libros en el idioma: " + idiomaLibro);
        librosPorIdioma.forEach(l -> System.out.println("-" + l.getTitulo()));
    }

    private void autoresRegistrados() {
        List<Autor> todosAutores = repositorio.listaDeAutores();
        System.out.println("---------Autores registrados----------");
        todosAutores.stream()
                .forEach(System.out::println);
    }

    private void buscarAutoresVivosPorAnio() {
        System.out.println("Escribe el año del cual quieres buscar autores: ");
        var anio = teclado.nextLine();
        List<Autor> autoresAnio = repositorio.autoresPorAnio(anio);
        System.out.println("Autores vivos en el año indicado: ");
        autoresAnio.forEach(a -> System.out.println("-" + a.getNombre()));
    }

    private void librosRegistrados() {
        libros = repositorio.findAll();
        libros.stream()
                .forEach(System.out::println);
    }

}
