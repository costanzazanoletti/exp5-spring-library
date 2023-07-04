package org.lessons.springlibrary.api;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.lessons.springlibrary.exceptions.BookNotFoundException;
import org.lessons.springlibrary.exceptions.NotUniqueIsbnException;
import org.lessons.springlibrary.model.Book;
import org.lessons.springlibrary.repository.BookRepository;
import org.lessons.springlibrary.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@CrossOrigin
@RequestMapping("api/v1/books")
public class BookRestController {

  // controller per la risorsa Book

  @Autowired
  private BookRepository bookRepository;

  @Autowired
  private BookService bookService;

  // servizio per avere la lista dei libri
  @GetMapping
  public List<Book> index(@RequestParam Optional<String> keyword) {
    // restituisco la lista di tutti i libri presi da database
    return bookService.getAll(keyword);
  }

  // servizio per avere il dettaglio del singolo libro
  @GetMapping("/{id}")
  public Book get(@PathVariable Integer id) {
    // cerco il libro per id su database
    try {
      return bookService.getById(id);
    } catch (BookNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  // servizio per creare un nuovo book, che arriva come JSON nel Request Body
  @PostMapping
  public Book create(@Valid @RequestBody Book book) {
    try {
      return bookService.create(book);
    } catch (NotUniqueIsbnException e) {
      // manipolo il bindingResult...
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }
  }

  // servizio per cancellare un book preso per id
  @DeleteMapping("/{id}")
  public void delete(@PathVariable Integer id) {
    bookRepository.deleteById(id);
  }

  @PutMapping("/{id}")
  public Book update(@PathVariable Integer id, @Valid @RequestBody Book book) {
    book.setId(id);
    return bookRepository.save(book);
  }

  // servizio solo dimostrativo della paginazione
  @GetMapping("/page")
  public Page<Book> page(
      /*@RequestParam(defaultValue = "3") Integer size,
      @RequestParam(defaultValue = "0") Integer page*/
      Pageable pageable
  ) {
    // creo un Pageable a partire da size e page
    // Pageable pageable = PageRequest.of(page, size);
    // restituisco una Page estratta da database dal metodo findAll
    return bookRepository.findAll(pageable);
  }
}
