package org.lessons.springlibrary.controller;

import java.util.List;
import org.lessons.springlibrary.model.Book;
import org.lessons.springlibrary.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/books")
public class BookController {

  // dipende da BookRepository
  @Autowired
  private BookRepository bookRepository;

  @GetMapping
  public String list(
      Model model) { // Model è la mappa di attributi che il controller passa alla view
    // recupero la lista di libri dal database
    List<Book> books = bookRepository.findAll();
    // passo la lista dei libri alla view
    model.addAttribute("bookList", books);
    // restituisco il nome del template della view
    return "/books/list";
  }
}
