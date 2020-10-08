SET @customer_id = 118;

DELETE FROM repair WHERE created_by_customer_id = @customer_id;
DELETE FROM customer_reset_password_token WHERE customer_id = @customer_id;
DELETE FROM customer_verification_token WHERE customer_id = @customer_id;
DELETE FROM customer WHERE id = @customer_id;