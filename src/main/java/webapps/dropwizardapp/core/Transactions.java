package webapps.dropwizardapp.core;

import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Pattern;

@Entity
@Table(name = "transactions")
@NamedQueries(
    {
        @NamedQuery(
            name = "webapps.dropwizardapp.core.Transactions.findAll",
            query = "SELECT txns FROM Transactions txns"
        ),
        @NamedQuery(
            name = "webapps.dropwizardapp.core.Transactions.findByPhone",
            query = "SELECT txns FROM Transactions txns WHERE txns.phone =:phone"
        )
    })
public class Transactions {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
	
	@Column(name = "phone", nullable = false)
	@Pattern(regexp="(^$|[0-9]{10})")
    private String phone;
	
	@Column(name = "amount", precision=10, scale=2, nullable = false)
    private double amount;
	
	@Column(name = "description", nullable = true)
    private String description;
	
	@Column(name = "timestamp", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;
	
	public Transactions() {
		
	}
	
	public Transactions(String phone, double amount, String description, Date timestamp) {
		this.phone = phone;
		this.amount = amount;
		this.description = description;
		this.timestamp = timestamp;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Transactions)) {
            return false;
        }

        final Transactions that = (Transactions) o;

        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.phone, that.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, phone);
    }
	
	
	
}
