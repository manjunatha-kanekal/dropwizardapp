package webapps.dropwizardapp;

import java.util.EnumSet;

import javax.servlet.DispatcherType;

import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import webapps.dropwizardapp.core.AuthorizationFilter;
import webapps.dropwizardapp.core.Transactions;
import webapps.dropwizardapp.core.Users;
import webapps.dropwizardapp.db.TransactionsDAO;
import webapps.dropwizardapp.db.UsersDAO;
import webapps.dropwizardapp.resources.HelloWorldResource;
import webapps.dropwizardapp.resources.TransactionsResource;
import webapps.dropwizardapp.resources.UsersResource;

public class dropwizardappApplication extends Application<dropwizardappConfiguration> {

	public static void main(final String[] args) throws Exception {
		new dropwizardappApplication().run(args);
	}
	
	private final HibernateBundle<dropwizardappConfiguration> hibernate = new HibernateBundle<dropwizardappConfiguration>(Users.class, Transactions.class) {
	    @Override
	    public DataSourceFactory getDataSourceFactory(dropwizardappConfiguration configuration) {
	        return configuration.getDataSourceFactory();
	    }
	};

	@Override
	public String getName() {
		return "dropwizardapp";
	}

	@Override
	public void initialize(final Bootstrap<dropwizardappConfiguration> bootstrap) {
		bootstrap.addBundle(hibernate);
		
		bootstrap.addBundle(new MigrationsBundle<dropwizardappConfiguration>() {
	        @Override
	        public DataSourceFactory getDataSourceFactory(dropwizardappConfiguration configuration) {
	            return configuration.getDataSourceFactory();
	        }
	    });
	}

	@Override
	public void run(final dropwizardappConfiguration configuration, final Environment environment) {
		
		final UsersDAO dao = new UsersDAO(hibernate.getSessionFactory());
	    environment.jersey().register(new UsersResource(dao));
	    
	    AuthorizationFilter proxyAuth = new UnitOfWorkAwareProxyFactory(hibernate).create(AuthorizationFilter.class, UsersDAO.class, dao);
	    
	    environment.servlets().addFilter("AuthorizationFilter", proxyAuth)
        .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/txn/*");
	    
	    final TransactionsDAO txnDao = new TransactionsDAO(hibernate.getSessionFactory());
	    environment.jersey().register(new TransactionsResource(txnDao, dao));
		
		final HelloWorldResource resource = new HelloWorldResource(configuration.getTemplate(), configuration.getDefaultName());
		environment.jersey().register(resource);
	}

}
