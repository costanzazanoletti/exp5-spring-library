package org.lessons.springlibrary.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "books")
public class Book {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotBlank(message = "Title must not be null or blank")
  @Column(nullable = false)
  private String title;

  @NotBlank
  @Size(min = 10, max = 13)
  @Column(nullable = false, unique = true)
  private String isbn;
  private String publisher;
  private String authors;

  @Min(0)
  private Integer year;

  @Lob
  private String synopsis;

  @Min(0)
  private Integer numberOfCopies;

  private LocalDateTime createdAt;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getIsbn() {
    return isbn;
  }

  public void setIsbn(String isbn) {
    this.isbn = isbn;
  }

  public String getPublisher() {
    return publisher;
  }

  public void setPublisher(String publisher) {
    this.publisher = publisher;
  }

  public String getAuthors() {
    return authors;
  }

  public void setAuthors(String authors) {
    this.authors = authors;
  }

  public Integer getYear() {
    return year;
  }

  public void setYear(Integer year) {
    this.year = year;
  }

  public String getSynopsis() {
    return synopsis;
  }

  public void setSynopsis(String synopsis) {
    this.synopsis = synopsis;
  }

  public Integer getNumberOfCopies() {
    return numberOfCopies;
  }

  public void setNumberOfCopies(Integer numberOfCopies) {
    this.numberOfCopies = numberOfCopies;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  // getter custom per il timestamp formattato
  public String getFormattedCreatedAt() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MMMM dd 'at' HH:mm");
    return createdAt.format(formatter);
  }
}
