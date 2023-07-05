package org.lessons.springlibrary.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import org.lessons.springlibrary.model.Category;
import org.springframework.web.multipart.MultipartFile;

public class BookForm {

  private Integer id;

  @NotBlank(message = "Title must not be null or blank")
  private String title;

  @NotBlank
  @Size(min = 10, max = 13)
  private String isbn;
  private String publisher;
  private String authors;

  @Min(0)
  private Integer year;

  private String synopsis;

  @Min(0)
  @NotNull
  private Integer numberOfCopies = 0;


  private MultipartFile coverFile;


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

  public MultipartFile getCoverFile() {
    return coverFile;
  }

  public void setCoverFile(MultipartFile coverFile) {
    this.coverFile = coverFile;
  }

  public List<Category> getCategories() {
    return categories;
  }

  public void setCategories(List<Category> categories) {
    this.categories = categories;
  }
}
