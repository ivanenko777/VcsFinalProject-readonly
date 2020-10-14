package lt.ivl.webInternalApp.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class RepairStatusNoteDto {
    @NotNull
    @NotEmpty(message = "Laukas negali būti tuščias.")
    private String note;

    public RepairStatusNoteDto() {
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
