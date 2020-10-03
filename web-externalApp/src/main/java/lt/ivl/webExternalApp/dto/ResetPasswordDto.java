package lt.ivl.webExternalApp.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class ResetPasswordDto {
    @NotNull
    @NotEmpty(message = "Laukas negali būti tuščias.")
    private String password;

    @NotNull
    @NotEmpty(message = "Laukas negali būti tuščias.")
    private String passwordVerify;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordVerify() {
        return passwordVerify;
    }

    public void setPasswordVerify(String passwordVerify) {
        this.passwordVerify = passwordVerify;
    }
}
