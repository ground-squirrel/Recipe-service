package recipes.business;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name="recipes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recipe {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotEmpty
    @Size(min=1)
    private String[] ingredients;

    @NotEmpty
    @Size(min=1)
    private String[] directions;

    @NotBlank
    private String category;

    private LocalDateTime date;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    public Recipe(String name, String description, String[] ingredients,
                  String[] directions, String category, LocalDateTime date, User user) {
        this.name = name;
        this.description = description;
        this.ingredients = ingredients.clone();
        this.directions = directions.clone();
        this.category = category;
        this.date = date;
        this.user = user;
    }
}
