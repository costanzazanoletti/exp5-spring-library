package org.lessons.springlibrary.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.lessons.springlibrary.dto.BookForm;
import org.lessons.springlibrary.exceptions.BookNotFoundException;
import org.lessons.springlibrary.exceptions.NotUniqueIsbnException;
import org.lessons.springlibrary.model.Book;
import org.lessons.springlibrary.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    bookToPersist.setCover(book.getCover());
    // persisto il book
    return bookRepository.save(bookToPersist);
  }

  // metodo che crea un nuovo Book a partire da un BookForm
  public Book create(BookForm bookForm) throws NotUniqueIsbnException {
    // converto il BookForm in un Book
    Book book = mapBookFormToBook(bookForm);
    // salvo il Book to database
    return create(book);
  }

  // metodo per creare un BookForm a partire dall'id di un Book salvato su db
  public BookForm getBookFormById(Integer id) throws BookNotFoundException {
    Book book = getById(id);
    return mapBookToBookForm(book);
  }

  public Book update(BookForm bookForm) throws BookNotFoundException, NotUniqueIsbnException {
    // converto il bookForm in book
    Book book = mapBookFormToBook(bookForm);
    // cerco il book su database
    Book bookDb = getById(book.getId());
    // valido ISBN
    if (!book.getIsbn().equals(bookDb.getIsbn()) && !isUniqueIsbn(book)) {
      throw new NotUniqueIsbnException(bookDb.getIsbn());
    }
    bookDb.setTitle(book.getTitle());
    bookDb.setAuthors(book.getAuthors());
    bookDb.setPublisher(book.getPublisher());
    bookDb.setCover(book.getCover());
    bookDb.setIsbn(book.getIsbn());
    bookDb.setYear(book.getYear());
    bookDb.setNumberOfCopies(book.getNumberOfCopies());
    bookDb.setSynopsis(book.getSynopsis());
    // salvo il book in update
    return bookRepository.save(bookDb);
  }

  // metodo per verificare se su database c'è già un book con lo stesso isbn del book passato come parametro
  private boolean isUniqueIsbn(Book formBook) {
    Optional<Book> result = bookRepository.findByIsbn(formBook.getIsbn());
    return result.isEmpty();
  }

  // metodo per convertire un BookForm in un Book
  private Book mapBookFormToBook(BookForm bookForm) {
    // creo un nuovo Book vuoto
    Book book = new Book();
    // copio i campi con corrispondenza esatta
    book.setId(bookForm.getId());
    book.setTitle(bookForm.getTitle());
    book.setAuthors(bookForm.getAuthors());
    book.setPublisher(bookForm.getPublisher());
    book.setYear(bookForm.getYear());
    book.setNumberOfCopies(bookForm.getNumberOfCopies());
    book.setSynopsis(bookForm.getSynopsis());
    book.setCategories(bookForm.getCategories());
    book.setIsbn(bookForm.getIsbn());
    // converto il campo cover
    byte[] coverBytes = multipartFileToByteArray(bookForm.getCoverFile());
    book.setCover(coverBytes);

    return book;
  }

  private BookForm mapBookToBookForm(Book book) {
    // creo un nuovo BookForm vuoto
    BookForm bookForm = new BookForm();
    // copio i campi con corrispondenza esatta
    bookForm.setId(book.getId());
    bookForm.setTitle(book.getTitle());
    bookForm.setAuthors(book.getAuthors());
    bookForm.setPublisher(book.getPublisher());
    bookForm.setYear(book.getYear());
    bookForm.setNumberOfCopies(book.getNumberOfCopies());
    bookForm.setSynopsis(book.getSynopsis());
    bookForm.setCategories(book.getCategories());
    bookForm.setIsbn(book.getIsbn());

    return bookForm;
  }

  private byte[] multipartFileToByteArray(MultipartFile mpf) {
    byte[] bytes = null;
    if (mpf != null && !mpf.isEmpty()) {
      try {
        bytes = mpf.getBytes();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return bytes;
  }
}
