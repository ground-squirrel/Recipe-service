package recipes.persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import recipes.business.Recipe;

import java.util.List;
import java.util.Map;

@Repository
public interface RecipeRepository extends CrudRepository<Recipe, Long> {
    default Map<String, Long> saveRecipe(Recipe recipe) {
        Recipe saved = this.save(recipe);
        return Map.of("id", saved.getId());
    }

    List<Recipe> findByCategoryIgnoreCaseOrderByDateDesc(String category);

    List<Recipe> findByNameContainingIgnoreCaseOrderByDateDesc(String name);
}
