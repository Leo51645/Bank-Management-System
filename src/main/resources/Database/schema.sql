create Database bank_management_database;

create TABLE bank_members (
  MyBank_Id int PRIMARY KEY auto_increment,
  IBAN varchar(26) unique,
  Account_balance decimal(14, 2) default 0,
  First_Name varchar(50),
  Last_Name varchar(50),
  Phone_Number varchar(50),
  Email varchar(50) unique,
  account_pin varchar(4),
  Password varchar(50),
  account_number varchar(8)
);

DELIMITER //

CREATE FUNCTION GenerateAccountNumber(myBankId INT)
RETURNS VARCHAR(8)
DETERMINISTIC
BEGIN
    DECLARE accountNumber VARCHAR(8);

    -- Auffüllen der MyBank_Id mit Nullen bis zu 8 Stellen
    SET accountNumber = LPAD(myBankId, 8, '0');

    RETURN accountNumber;
END //

DELIMITER ;