package lt.ivl.webInternalApp.dto;

import javax.validation.constraints.NotNull;

public class RepairStatusQuestionDto {
    @NotNull(message = "Pasirinkite atsakymą Taip arba Ne.")
    private Boolean answer;

    public Boolean getAnswer() {
        return answer;
    }

    public void setAnswer(Boolean answer) {
        this.answer = answer;
    }
}
