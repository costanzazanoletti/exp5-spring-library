package org.lessons.springlibrary.controller;

import org.lessons.springlibrary.exceptions.BookNotFoundException;
import org.lessons.springlibrary.model.Book;
import org.lessons.springlibrary.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/files")
public class FileController {

  @Autowired
  BookService bookService;

  // metodo che cerca il libro per id e ne restituisce la cover come image
  @GetMapping("/cover/{bookId}")
  public ResponseEntity<byte[]> getBookCover(@PathVariable Integer bookId) {
    try {
      Book book = bookService.getById(bookId);
      MediaType mediaType = MediaType.IMAGE_JPEG;
      return ResponseEntity.ok().contentType(mediaType).body(book.getCover());
    } catch (BookNotFoundException e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }
}
