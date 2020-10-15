SET @employee_id = 113;

DELETE FROM employee_reset_password_token WHERE employee_id = @employee_id;
DELETE FROM employee_role WHERE employee_id = @employee_id;
DELETE FROM employee WHERE id = @employee_id;