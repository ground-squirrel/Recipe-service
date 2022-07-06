package recipes.presentation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import recipes.business.Recipe;
import recipes.business.RecipeService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class RecipeController {

    @Autowired
    RecipeService recipeService;

    @PostMapping("/api/recipe/new")
    @ResponseStatus(code= HttpStatus.OK)
    public Map<String, Long> postRecipe(@Valid @RequestBody Recipe recipe) {
        LocalDateTime ldt = LocalDateTime.now();
        return recipeService.save(
                new Recipe(recipe.getName(),
                        recipe.getDescription(),
                        recipe.getIngredients(),
                        recipe.getDirections(),
                        recipe.getCategory(),
                        ldt));
    }

    @PutMapping("/api/recipe/{id}")
    @ResponseStatus(code=HttpStatus.NO_CONTENT)
    public void updateRecipe(@PathVariable long id, @Valid @RequestBody Recipe recipe) {
        if (recipeService.findById(id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else {
            LocalDateTime ldt = LocalDateTime.now();
            recipeService.save(
                    new Recipe(id,
                            recipe.getName(),
                            recipe.getDescription(),
                            recipe.getIngredients(),
                            recipe.getDirections(),
                            recipe.getCategory(),
                            ldt));
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

        if (name.isPresent()) {
            return recipeService.findAllByNameContaining(name.get());
        }

        return null;
    }

    @DeleteMapping("/api/recipe/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRecipe(@PathVariable long id) {
        if (!recipeService.deleteById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

}
