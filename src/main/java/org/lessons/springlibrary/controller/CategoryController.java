package org.lessons.springlibrary.controller;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.lessons.springlibrary.model.Book;
import org.lessons.springlibrary.model.Category;
import org.lessons.springlibrary.repository.CategoryRepository;
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
@RequestMapping("/categories")
public class CategoryController {

  @Autowired
  private CategoryRepository categoryRepository;

  // controller index gestisce sia la lista delle categorie presenti sul db, sia il form
  // per creare o editare una categoria
  @GetMapping
  public String index(Model model, @RequestParam("edit") Optional<Integer> categoryId) {
    // recupero da db tutte le categorie
    List<Category> categoryList = categoryRepository.findAll();
    // passo al model un attributo categories con tutte le categorie
    model.addAttribute("categories", categoryList);

    Category categoryObj;
    // se ho il parametro categoryId allora cerco la categoria su database
    if (categoryId.isPresent()) {
      Optional<Category> categoryDb = categoryRepository.findById(categoryId.get());
      // se è presente valorizzo categoryObj con la categoria da db
      if (categoryDb.isPresent()) {
        categoryObj = categoryDb.get();
      } else {
        // se non è presente valorizzo categoryObj con una categoria vuota
        categoryObj = new Category();
      }
    } else {
      // se non ho il parametro categoryObj con una categoria vuota
      categoryObj = new Category();
    }
    // passo al model un attributo categoryObj per mappare il form su un oggetto di tipo Category
    model.addAttribute("categoryObj", categoryObj);
    return "/categories/index";
  }

  @PostMapping("/save")
  public String save(@Valid @ModelAttribute("categoryObj") Category formCategory,
      BindingResult bindingResult, Model model) {
    // verfichiamo se ci sono errori
    if (bindingResult.hasErrors()) {
      model.addAttribute("categories", categoryRepository.findAll());
      return "/categories/index";
    }
    // salvare la categoria
    categoryRepository.save(formCategory);
    // fa la redirect alla index
    return "redirect:/categories";
  }

  @PostMapping("/delete/{id}")
  public String delete(@PathVariable Integer id) {
    // prima di eliminare la categoria la dissocio da tutti i libri
    Optional<Category> result = categoryRepository.findById(id);
    if (result.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    // categoria che devo eliminare
    Category categoryToDelete = result.get();
    // per ogni libro associato alla categoria da eliminare
    for (Book book : categoryToDelete.getBooks()) {
      book.getCategories().remove(categoryToDelete);
    }

    categoryRepository.deleteById(id);
    return "redirect:/categories";
  }
}
