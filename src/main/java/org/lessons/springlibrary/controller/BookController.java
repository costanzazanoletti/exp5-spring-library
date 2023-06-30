package org.lessons.springlibrary.controller;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.lessons.springlibrary.messages.AlertMessage;
import org.lessons.springlibrary.messages.AlertMessageType;
import org.lessons.springlibrary.model.Book;
import org.lessons.springlibrary.repository.BookRepository;
import org.lessons.springlibrary.repository.CategoryRepository;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/books")
public class BookController {

  // dipende da BookRepository
  @Autowired
  private BookRepository bookRepository;

  @Autowired
  private CategoryRepository categoryRepository;
/*
  METODI PER READ
* */
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
    /*// cerca su database i dettagli del libro con quell'id
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
    }*/
    Book book = getBookById(bookId);
    // passa il libro alla view
    model.addAttribute("book", book);
    // ritorna il nome del template della view
    return "/books/detail";
  }

  /*
    METODI PER LA CREATE
  * */
  // controller che restituisce la pagina con form di creazione del nuovo book
  @GetMapping("/create")
  public String create(Model model) {
    // aggiungo al model l'attributo book contenente un Book vuoto
    model.addAttribute("book", new Book());
    // aggiungo al model la lista delle categorie per popolare le checkbox
    model.addAttribute("categoryList", categoryRepository.findAll());
    // return "/books/create"; // template con form di creazione di un book
    return "/books/edit"; // template unico per create e edit
  }

  // controller che gestisce la post del form coi dati del book
  @PostMapping("/create")
  public String store(@Valid @ModelAttribute("book") Book formBook, BindingResult bindingResult,
      RedirectAttributes redirectAttributes, Model model) {
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
      // return "/books/create"; // ritorno il template del form ma col book precaricato
      // aggiungo al model la lista delle categorie per popolare le checkbox
      model.addAttribute("categoryList", categoryRepository.findAll());
      return "/books/edit"; // template unico per create e edit
    }

    // setto il timestamp di creazione
    formBook.setCreatedAt(LocalDateTime.now());
    // persisto formBook su database
    // il metodo save fa una create sql se l'oggetto con quella PK non esiste, altrimenti fa update
    bookRepository.save(formBook);

    // se tutto va a buon fine rimando alla lista dei books
    redirectAttributes.addFlashAttribute("message",
        new AlertMessage(AlertMessageType.SUCCESS, "Book created!"));
    return "redirect:/books";
  }

  /*
    METODI PER UPDATE
  * */
  @GetMapping("/edit/{id}")
  public String edit(@PathVariable Integer id, Model model) {
    /*// verificare se esiste un book con quell'id
    Optional<Book> result = bookRepository.findById(id);
    // se non esiste ritorno un http 404
    if (result.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "book with id " + id + " not found");
    }*/
    Book book = getBookById(id);
    // recupero i dati di quel book da database
    // aggiungo il book al model
    model.addAttribute("book", book);
    // aggiungo al model la lista delle categorie per popolare le checkbox
    model.addAttribute("categoryList", categoryRepository.findAll());
    // restituisco il template con il form di edit
    return "/books/edit";
  }

  @PostMapping("/edit/{id}")
  public String doEdit(
      @PathVariable Integer id,
      @Valid @ModelAttribute("book") Book formBook,
      BindingResult bindingResult,
      RedirectAttributes redirectAttributes,
      Model model
  ) {
    // cerco il book per id
    Book bookToEdit = getBookById(id); // vecchia versione del book
    // nuova versione del book è formBook
    // valido formBook
    // se il vecchio isbn e quello nuovo sono diversi e quello nuovo è già presente su database allora errore
    if (!bookToEdit.getIsbn().equals(formBook.getIsbn()) && !isUniqueIsbn(formBook)) {
      bindingResult.addError(new FieldError("book", "isbn", formBook.getIsbn(), false, null, null,
          "isbn must be unique"));
    }
    if (bindingResult.hasErrors()) {
      // aggiungo al model la lista delle categorie per popolare le checkbox
      model.addAttribute("categoryList", categoryRepository.findAll());
      // se ci sono errori ritorno il template col form
      return "/books/edit";
    }

    // trasferisco su formBook tutti i valori dei campi che non sono presenti nel form (altrimenti li perdo)
    formBook.setId(bookToEdit.getId());
    formBook.setCreatedAt(bookToEdit.getCreatedAt());
    // salvo i dati
    bookRepository.save(formBook);
    redirectAttributes.addFlashAttribute("message",
        new AlertMessage(AlertMessageType.SUCCESS, "Book updated!"));
    return "redirect:/books";
  }


  /*
   * MEDODI PER DELETE
   * */
  @PostMapping("/delete/{id}")
  public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
    // verifichiamo che esiste il book con quell'id
    Book bookToDelete = getBookById(id);
    // lo cancelliamo
    bookRepository.delete(bookToDelete);
    // aggiungo un messaggio di successo come flashAttribute
    redirectAttributes.addFlashAttribute("message",
        new AlertMessage(AlertMessageType.SUCCESS,
            "Book " + bookToDelete.getTitle() + " deleted!"));
    // facciamo la redirect alla lista dei book
    return "redirect:/books";
  }

  /*
   * UTILITY METHODS
   * */
  // metodo per verificare se su database c'è già un book con lo stesso isbn del book passato come parametro
  private boolean isUniqueIsbn(Book formBook) {
    Optional<Book> result = bookRepository.findByIsbn(formBook.getIsbn());
    return result.isEmpty();
  }

  // metodo per selezionare il book da database o tirare un'eccezione
  private Book getBookById(Integer id) {
    // verificare se esiste un book con quell'id
    Optional<Book> result = bookRepository.findById(id);
    // se non esiste ritorno un http 404
    if (result.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "book with id " + id + " not found");
    }
    return result.get();
  }
}
