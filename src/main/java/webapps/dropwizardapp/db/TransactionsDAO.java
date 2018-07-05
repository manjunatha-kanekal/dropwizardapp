package webapps.dropwizardapp.db;

import java.util.List;
import java.util.Optional;

import org.hibernate.SessionFactory;

import io.dropwizard.hibernate.AbstractDAO;
import webapps.dropwizardapp.core.Transactions;
import webapps.dropwizardapp.core.Users;

public class TransactionsDAO extends AbstractDAO<Transactions> {

	public TransactionsDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}
	
	public Optional<Transactions> findById(Long id) {
		return Optional.ofNullable(get(id));
    }

    public Transactions create(Transactions txn) {
        return persist(txn);
    }
    
    @SuppressWarnings("unchecked")
	public List<Transactions> findAll() {
        return list(namedQuery("webapps.dropwizardapp.core.Transactions.findAll"));
    }

	@SuppressWarnings("unchecked")
	public List<Users> findByPhone(String phone) {
		return list(
                namedQuery("webapps.dropwizardapp.core.Transactions.findByPhone")
                .setParameter("phone", phone)
        );
	}

}
