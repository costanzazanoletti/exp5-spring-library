package org.lessons.springlibrary.repository;

import java.util.List;
import org.lessons.springlibrary.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Integer> {

  // metodo per cercare i libri con titolo uguale a quello passato
  List<Book> findByTitle(String title);

  // metodo per cercare i libri il cui titolo o autore contiene una stringa
  List<Book> findByTitleContainingIgnoreCaseOrAuthorsContainingIgnoreCase(String title,
      String authors);
}
