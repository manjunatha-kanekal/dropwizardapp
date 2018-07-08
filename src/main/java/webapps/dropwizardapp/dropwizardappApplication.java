package webapps.dropwizardapp;

import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bendb.dropwizard.redis.JedisBundle;
import com.bendb.dropwizard.redis.JedisFactory;

import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import redis.clients.jedis.JedisPool;
import webapps.dropwizardapp.core.AuthorizationFilter;
import webapps.dropwizardapp.core.Transactions;
import webapps.dropwizardapp.core.Users;
import webapps.dropwizardapp.db.TransactionsDAO;
import webapps.dropwizardapp.db.UsersDAO;
import webapps.dropwizardapp.resources.HelloWorldResource;
import webapps.dropwizardapp.resources.TransactionsResource;
import webapps.dropwizardapp.resources.UsersResource;

public class dropwizardappApplication extends Application<dropwizardappConfiguration> {
	private static final Logger LOGGER = LoggerFactory.getLogger(dropwizardappApplication.class);
	
	public static void main(final String[] args) throws Exception {
		new dropwizardappApplication().run(args);
	}
	
	private final HibernateBundle<dropwizardappConfiguration> hibernate = new HibernateBundle<dropwizardappConfiguration>(Users.class, Transactions.class) {
	    @Override
	    public DataSourceFactory getDataSourceFactory(dropwizardappConfiguration configuration) {
	        return configuration.getDataSourceFactory();
	    }
	};
	
	private final JedisBundle<dropwizardappConfiguration> jedis = new JedisBundle<dropwizardappConfiguration>() {
        @Override
        public JedisFactory getJedisFactory(dropwizardappConfiguration configuration) {
            return configuration.getJedisFactory();
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
		
		bootstrap.addBundle(jedis);
	}

	@Override
	public void run(final dropwizardappConfiguration configuration, final Environment environment) {
		
		LOGGER.info("Registering resources.....");
		final UsersDAO dao = new UsersDAO(hibernate.getSessionFactory());
	    environment.jersey().register(new UsersResource(dao));
	    
	    //AuthorizationFilter proxyAuth = new UnitOfWorkAwareProxyFactory(hibernate).create(AuthorizationFilter.class, UsersDAO.class, dao);
	    AuthorizationFilter proxyAuth = new UnitOfWorkAwareProxyFactory(hibernate).create(AuthorizationFilter.class, JedisPool.class, jedis.getPool());
	    
	    environment.servlets().addFilter("AuthorizationFilter", proxyAuth)
        .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/txn/*");
	    
	    final TransactionsDAO txnDao = new TransactionsDAO(hibernate.getSessionFactory());
	    environment.jersey().register(new TransactionsResource(txnDao, dao));
		
		final HelloWorldResource resource = new HelloWorldResource(configuration.getTemplate(), configuration.getDefaultName());
		environment.jersey().register(resource);
	}

}
