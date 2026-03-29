package business.entities;

import java.io.Serializable;
import java.util.Date;

public abstract class BaseDTO implements Serializable {
    private Long id;
    private boolean active;

    private Date createdOn;

    private Date lastModifiedOn;

    // Base Contructor
    public BaseDTO(){
        this.active = true;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }


}
