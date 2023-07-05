package org.lessons.springlibrary.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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
  @NotNull
  private Integer numberOfCopies = 0;

  private LocalDateTime createdAt;

  @Lob
  @Column(length = 16777215)
  private byte[] cover;

  @JsonIgnore
  @OneToMany(mappedBy = "book", cascade = {CascadeType.REMOVE})
  private List<Borrowing> borrowings = new ArrayList<>(); // relazione con i borrowing

  @ManyToMany
  @JoinTable(
      name = "book_category",
      joinColumns = @JoinColumn(name = "book_id"),
      inverseJoinColumns = @JoinColumn(name = "category_id")
  )
  private List<Category> categories = new ArrayList<>();

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

  public List<Borrowing> getBorrowings() {
    return borrowings;
  }

  public void setBorrowings(List<Borrowing> borrowings) {
    this.borrowings = borrowings;
  }

  public List<Category> getCategories() {
    return categories;
  }

  public void setCategories(List<Category> categories) {
    this.categories = categories;
  }

  public byte[] getCover() {
    return cover;
  }

  public void setCover(byte[] cover) {
    this.cover = cover;
  }

  // getter custom per il timestamp formattato
  @JsonIgnore
  public String getFormattedCreatedAt() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MMMM dd 'at' HH:mm");
    return createdAt.format(formatter);
  }

  // getter custom per calcolare quante copie sono disponibili
  public int getAvailableCopies() {
    List<Borrowing> activeBorrowings = new ArrayList<>();
    // filtro la lista di prestiti
    // tengo solo i prestiti non restituiti (returnDate == null)
    for (Borrowing b : borrowings) {
      if (b.getReturnDate() == null) {
        activeBorrowings.add(b);
      }
    }
    // sottraggo al numero di copie la size di questa lista
    return numberOfCopies - activeBorrowings.size();
  }
}
