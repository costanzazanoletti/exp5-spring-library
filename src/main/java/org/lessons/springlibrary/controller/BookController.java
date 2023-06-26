package org.lessons.springlibrary.controller;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.lessons.springlibrary.model.Book;
import org.lessons.springlibrary.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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


  // controller che restituisce la pagina con form di creazione del nuovo book
  @GetMapping("/create")
  public String create(Model model) {
    // aggiungo al model l'attributo book contenente un Book vuoto
    model.addAttribute("book", new Book());
    return "/books/create"; // template con form di creazione di un book
  }

  // controller che gestisce la post del form coi dati del book
  @PostMapping("/create")
  public String store(@Valid @ModelAttribute("book") Book formBook, BindingResult bindingResult) {
    // i dati del book sono dentro all'oggetto formBook

    // verifico se l'isbn è univoco
    if (!isUniqueIsbn(formBook)) {
      // aggiungo a mano un errore nella mappa BindingResult
      bindingResult.addError(new FieldError("book", "isbn", formBook.getIsbn(), false, null, null,
          "isbn must be unique"));
    }
    // verifico se in validazione ci sono stati errori
    if (bindingResult.hasErrors()) {
      // ci sono stati errori
      return "/books/create"; // ritorno il template del form ma col book precaricato
    }

    // setto il timestamp di creazione
    formBook.setCreatedAt(LocalDateTime.now());
    // persisto formBook su database
    // il metodo save fa una create sql se l'oggetto con quella PK non esiste, altrimenti fa update
    bookRepository.save(formBook);

    // se tutto va a buon fine rimando alla lista dei books
    return "redirect:/books";
  }


  // metodo per verificare se su database c'è già un book con lo stesso isbn del book passato come parametro
  private boolean isUniqueIsbn(Book formBook) {
    Optional<Book> result = bookRepository.findByIsbn(formBook.getIsbn());
    return result.isEmpty();
  }
}
