package webapps.dropwizardapp.db;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.hibernate.SessionFactory;

import io.dropwizard.hibernate.AbstractDAO;
import webapps.dropwizardapp.core.LoginResponse;
import webapps.dropwizardapp.core.Users;

public class UsersDAO extends AbstractDAO<Users> {
	public UsersDAO(SessionFactory factory) {
        super(factory);
    }

    public Optional<Users> findById(Long id) {
        return Optional.ofNullable(get(id));
    }

    public Users create(Users user) {
        return persist(user);
    }
    
    public List<Users> findAll() {
        return list(namedQuery("webapps.dropwizardapp.core.Users.findAll"));
    }

	public List<Users> findByPhone(String phone) {
		return list(
                namedQuery("webapps.dropwizardapp.core.Users.findByPhone")
                .setParameter("phone", phone)
        );
	}

	public LoginResponse login(String phone) {
		LoginResponse e = new LoginResponse();
		
		List<Users> userList = findByPhone(phone);
		if(userList != null && userList.size() > 0) {
			String token = UUID.randomUUID().toString();
			Users u = userList.get(0);
			u.setToken(token);
			create(u);
			e.setMessage("Login Success!");
			e.setToken(token);
		} else {
			e.setMessage("Phone number doesn't exists! Please register first");
		}
		
		return e;
	}
}
