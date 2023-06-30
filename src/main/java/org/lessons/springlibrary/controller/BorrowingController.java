package org.lessons.springlibrary.controller;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.Optional;
import org.lessons.springlibrary.model.Book;
import org.lessons.springlibrary.model.Borrowing;
import org.lessons.springlibrary.repository.BookRepository;
import org.lessons.springlibrary.repository.BorrowingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/borrowings")
public class BorrowingController {

  @Autowired
  BookRepository bookRepository;
  @Autowired
  BorrowingRepository borrowingRepository;

  @GetMapping("/create")
  public String create(@RequestParam("bookId") Integer bookId, Model model) {
    // creo un nuovo borrowing
    Borrowing borrowing = new Borrowing();
    // precarico la data di prestito con la data odierna
    borrowing.setBorrowingDate(LocalDate.now());
    // precarico il book con quello passato come parametro
    Optional<Book> book = bookRepository.findById(bookId);
    if (book.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,
          "book with id " + bookId + " not found");
    }
    borrowing.setBook(book.get());
    // aggiungo al Model l'attributo col borrowing
    model.addAttribute("borrowing", borrowing);
    return "/borrowings/form";
  }

  @PostMapping("/create")
  public String doCreate(@Valid @ModelAttribute("borrowing") Borrowing formBorrowing,
      BindingResult bindingResult) {
    // valido
    if (bindingResult.hasErrors()) {
      // se ci sono errori ricreo il template del form
      return "/borrowings/form";
    }
    // se non ci sono errori salvo il borrowing
    borrowingRepository.save(formBorrowing);
    // faccio una redirect alla pagina di dettaglio del libro
    return "redirect:/books/" + formBorrowing.getBook().getId();
  }


  @GetMapping("/edit/{id}")
  public String edit(@PathVariable Integer id, Model model) {
    // cerco il borrowing su database
    Optional<Borrowing> borrowing = borrowingRepository.findById(id);
    if (borrowing.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    // passo il borrowing al model
    model.addAttribute("borrowing", borrowing.get());
    return "/borrowings/form";
  }

  @PostMapping("/edit/{id}")
  public String doEdit(@PathVariable Integer id,
      @Valid @ModelAttribute("borrowing") Borrowing formBorrowing, BindingResult bindingResult) {
    // valido il formBorrowing
    if (bindingResult.hasErrors()) {
      // se ci sono errori ricreo il template del form
      return "/borrowings/form";
    }
    // verifico che esiste il borrowing da modificare
    Optional<Borrowing> borrowingToEdit = borrowingRepository.findById(id);
    if (borrowingToEdit.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    // setto l'id del borrowing al formBorrowing
    formBorrowing.setId(id);
    // salvo il formBorrowing su database (UPDATE)
    borrowingRepository.save(formBorrowing);
    // faccio una redirect alla pagina di dettaglio del libro
    return "redirect:/books/" + formBorrowing.getBook().getId();
  }

  @PostMapping("/delete/{id}")
  public String delete(@PathVariable Integer id) {
    // verifico che il borrowing esiste
    Optional<Borrowing> borrowingToDelete = borrowingRepository.findById(id);
    if (borrowingToDelete.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    // se esiste lo cancello
    borrowingRepository.delete(borrowingToDelete.get());
    // faccio una redirect alla pagina di dettaglio del libro
    return "redirect:/books/" + borrowingToDelete.get().getBook().getId();
  }
}
