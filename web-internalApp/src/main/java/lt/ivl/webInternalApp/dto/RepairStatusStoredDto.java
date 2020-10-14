package lt.ivl.webInternalApp.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class RepairStatusStoredDto {
    @NotNull
    @NotEmpty(message = "Laukas negali būti tuščias.")
    private String stored;

    public RepairStatusStoredDto() {
    }

    public String getStored() {
        return stored;
    }

    public void setStored(String stored) {
        this.stored = stored;
    }
}
