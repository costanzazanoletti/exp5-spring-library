package org.lessons.springlibrary.controller;

import java.util.List;
import java.util.Optional;
import org.lessons.springlibrary.model.Book;
import org.lessons.springlibrary.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/books")
public class BookController {

  // dipende da BookRepository
  @Autowired
  private BookRepository bookRepository;

/*  @GetMapping
  public String list(
      Model model) { // Model è la mappa di attributi che il controller passa alla view
    // recupero la lista di libri dal database
    List<Book> books = bookRepository.findAll();
    // passo la lista dei libri alla view
    model.addAttribute("bookList", books);
    // restituisco il nome del template della view
    return "/books/list";
  }*/

  /* metodo che può ricevere opzionalmente un parametro da query string,
  - se quel parametro c'è
    dobbiamo filtrare la lista dei libri per quel parametro
  - se quel parametro non c'è dobbiamo restituire la lista di tutti i libri
  * */
  @GetMapping
  public String list(
      @RequestParam(name = "keyword", required = false) String searchString,
      Model model) { // Model è la mappa di attributi che il controller passa alla view
    List<Book> books;

    if (searchString == null || searchString.isBlank()) {
      // se non ho il parametro searchString faccio la query generica
      // recupero la lista di libri dal database
      books = bookRepository.findAll();
    } else {
      // se ho il parametro searchString faccio la query con filtro
      // books = bookRepository.findByTitle(searchString);
      books = bookRepository.findByTitleContainingIgnoreCaseOrAuthorsContainingIgnoreCase(
          searchString, searchString);
    }

    // passo la lista dei libri alla view
    model.addAttribute("bookList", books);
    model.addAttribute("searchInput", searchString == null ? "" : searchString);
    // restituisco il nome del template della view
    return "/books/list";
  }

  @GetMapping("/{id}")
  public String detail(@PathVariable("id") Integer bookId, Model model) {
    // cerca su database i dettagli del libro con quell'id
    Optional<Book> result = bookRepository.findById(bookId);
    if (result.isPresent()) {
      // passa il libro alla view
      model.addAttribute("book", result.get());
      // ritorna il nome del template della view
      return "/books/detail";
    } else {
      // ritorno un HTTP Status 404 Not Found
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,
          "Book with id " + bookId + " not found");
    }
  }
}
