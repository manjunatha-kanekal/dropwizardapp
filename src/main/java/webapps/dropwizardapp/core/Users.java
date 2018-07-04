package webapps.dropwizardapp.core;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "users")
@NamedQueries(
    {
        @NamedQuery(
            name = "webapps.dropwizardapp.core.Users.findAll",
            query = "SELECT u FROM Users u"
        ),
        @NamedQuery(
            name = "webapps.dropwizardapp.core.Users.findByPhone",
            query = "SELECT u FROM Users u where u.phone =:phone"
        )
    })
public class Users {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "fullName", nullable = false)
    private String fullName;

    @Column(name = "phone", nullable = false)
    private String phone;

    public Users() {
    }

    public Users(String fullName, String phone) {
        this.fullName = fullName;
        this.phone = phone;
    }

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
    
	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Users)) {
            return false;
        }

        final Users that = (Users) o;

        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.fullName, that.fullName) &&
                Objects.equals(this.phone, that.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fullName, phone);
    }
}
