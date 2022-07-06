package recipes.persistence;

import org.springframework.data.repository.CrudRepository;
import recipes.business.User;

public interface UserRepository  extends CrudRepository<User, Long> {

    default boolean addUser(User user) {
        if (null == this.findByEmail(user.getEmail())) {
            this.save(user);
            return true;
        }

        return false;
    }

    User findByEmail(String email);
}
