package org.lessons.springlibrary.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.lessons.springlibrary.exceptions.BookNotFoundException;
import org.lessons.springlibrary.exceptions.NotUniqueIsbnException;
import org.lessons.springlibrary.model.Book;
import org.lessons.springlibrary.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookService {

  @Autowired
  BookRepository bookRepository;

  // metodo che restituisce la lista di tutti i libri filtrata o no a seconda del parametro
  public List<Book> getAll(Optional<String> keywordOpt) {
    if (keywordOpt.isEmpty()) {
      return bookRepository.findAll();
    } else {
      return bookRepository.findByTitle(keywordOpt.get());
    }
  }

  // metodo che restituisce il libro preso per id o un'eccezione se non lo trova
  public Book getById(Integer id) throws BookNotFoundException {
    Optional<Book> bookOpt = bookRepository.findById(id);
    if (bookOpt.isPresent()) {
      return bookOpt.get();
    } else {
      throw new BookNotFoundException("Book with id " + id);
    }
  }

  // metodo che salva un nuovo libro a partire da quello passato come parametro
  public Book create(Book book) throws NotUniqueIsbnException {
    // valido l'isbn del book
    if (!isUniqueIsbn(book)) {
      throw new NotUniqueIsbnException(book.getIsbn());
    }
    // creo il book da salvare
    Book bookToPersist = new Book();
    // genero il timestamp di createdAt
    bookToPersist.setCreatedAt(LocalDateTime.now());
    // copio tutti i campi di book che mi interessano
    bookToPersist.setTitle(book.getTitle());
    bookToPersist.setAuthors(book.getAuthors());
    bookToPersist.setPublisher(book.getPublisher());
    bookToPersist.setIsbn(book.getIsbn());
    bookToPersist.setYear(book.getYear());
    bookToPersist.setNumberOfCopies(book.getNumberOfCopies());
    bookToPersist.setSynopsis(book.getSynopsis());
    bookToPersist.setCategories(book.getCategories());
    // persisto il book
    return bookRepository.save(bookToPersist);
  }

  // metodo per verificare se su database c'è già un book con lo stesso isbn del book passato come parametro
  private boolean isUniqueIsbn(Book formBook) {
    Optional<Book> result = bookRepository.findByIsbn(formBook.getIsbn());
    return result.isEmpty();
  }
}
