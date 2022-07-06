package recipes.presentation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import recipes.business.Recipe;
import recipes.business.RecipeService;
import recipes.business.User;
import recipes.persistence.UserRepository;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class RecipeController {

    @Autowired
    RecipeService recipeService;

    @Autowired
    UserRepository userRepo;

    @PostMapping("/api/recipe/new")
    @ResponseStatus(code= HttpStatus.OK)
    @Transactional
    public Map<String, Long> postRecipe(@Valid @RequestBody Recipe recipe,
                                        @AuthenticationPrincipal UserDetails details) {
        LocalDateTime ldt = LocalDateTime.now();
        User user = userRepo.findByEmail(details.getUsername());
        return recipeService.save(
                new Recipe(recipe.getName(),
                        recipe.getDescription(),
                        recipe.getIngredients(),
                        recipe.getDirections(),
                        recipe.getCategory(),
                        ldt,
                        user));
    }

    @PutMapping("/api/recipe/{id}")
    @ResponseStatus(code=HttpStatus.NO_CONTENT)
    @Transactional
    public void updateRecipe(@PathVariable long id, @Valid @RequestBody Recipe recipe,
                             @AuthenticationPrincipal UserDetails details) {
        Optional<Recipe> recipeFromDB = recipeService.findById(id);
        if (recipeFromDB.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else {
            LocalDateTime ldt = LocalDateTime.now();
            User userUpdating = userRepo.findByEmail(details.getUsername());
            User userAuthor = recipeFromDB.get().getUser();
            if (userUpdating == userAuthor) {
                recipeService.save(
                        new Recipe(id,
                                recipe.getName(),
                                recipe.getDescription(),
                                recipe.getIngredients(),
                                recipe.getDirections(),
                                recipe.getCategory(),
                                ldt,
                                userUpdating));
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }

        }
    }

//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public void handleValidationExceptions(
//            MethodArgumentNotValidException ex) {
//    }

    @GetMapping("/api/recipe/{id}")
    @ResponseStatus(code=HttpStatus.OK)
    public Recipe getRecipe(@PathVariable long id) {
        Optional<Recipe> recipe = recipeService.findById(id);
        if (recipe.isPresent()) {
            return recipe.get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("api/recipe/search")
    @ResponseStatus(code=HttpStatus.OK)
    public List<Recipe> search(@RequestParam Optional<String> category, @RequestParam Optional<String> name) {
        if ((category.isPresent() && name.isPresent())
        || (category.isEmpty()) && name.isEmpty()) {
            throw  new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if (category.isPresent()) {
            return recipeService.findAllByCategory(category.get());
        }

        return name.map(s -> recipeService.findAllByNameContaining(s)).orElse(null);

    }

    @DeleteMapping("/api/recipe/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void deleteRecipe(@PathVariable long id, @AuthenticationPrincipal UserDetails details) {

        Optional<Recipe> recipeFromDB = recipeService.findById(id);
        if (recipeFromDB.isPresent()) {
            User userDeleting = userRepo.findByEmail(details.getUsername());
            User userAuthor = recipeFromDB.get().getUser();

            if (userDeleting == userAuthor) {
               if (!recipeService.deleteById(id)) {
                   throw new ResponseStatusException(HttpStatus.NOT_FOUND);
               }
            } else {
                   throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

}
