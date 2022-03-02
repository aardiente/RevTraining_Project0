-- Tables
create table useraccount
(
	user_id	 				integer 	 	generated always as identity,
	user_name				varchar(20)		not null unique,
	user_password			varchar(20)		not null,
	first_name 				varchar(32)		not null,
	last_name 				varchar(32)		not null,
	date_created			date			not null,
	account_activated 		boolean 		default false,
	primary key(user_id)
)

create table customer
(
	customer_id	  			integer 	 			generated always as identity,
	balance					float					not null,
	primary key(customer_id),
	fk_userid       		integer     references useraccount(user_id) on delete cascade
)

create table employee
(
	employee_id	 			integer 		generated always as identity,
	primary key(employee_id),
	fk_userid       		integer    		references useraccount(user_id) on delete cascade
)

create table customertransaction
(
	transaction_id	 		integer 	generated always as identity,
	transaction_amount		float		not null,
	transaction_date		date		not null,
	transaction_approved	boolean		default false,
	transaction_denied 		boolean 	default false,
	primary key(transaction_id),
	fk_customerid_sender    integer     references customer(customer_id) on delete cascade,
	fk_customerid_reciever  integer     references customer(customer_id) on delete cascade
)

-- Procedures, just using the 2 | 1. for Customer 1 to Customer 2 Transactions
-- Customer 1 to Customer 1 is handled by CustomerDAO interface
-- 2. Is just a shortcut for PreparedStatements to update customers

-- 1)
create or replace procedure processTransaction(
	id integer,
   sender integer,
   receiver integer,
   amount float
)
language plpgsql    
as $$
begin
	begin
		update customer 
			set balance = balance - amount
			where customer_id = sender;
		update customer
			set balance = balance + amount
			where customer_id = receiver ;
		update customertransaction 
			set transaction_approved = true
			where transaction_id = id;
		commit;
	end;
end$$

--2)
create or replace procedure createWithdrawalTransaction(
   sender int,
   amount float
)
language plpgsql    
as $$
begin

   INSERT INTO customertransaction	  ( transaction_amount, transaction_date, transaction_approved, fk_customerid_sender, fk_customerid_reciever)
									VALUES( amount, current_date, false, sender, null);
 
    commit;
end;$$

-- useful queries

select customer_id, user_name, user_password, first_name, last_name, account_activated, balance from useraccount join customer on fk_userid = user_id -- gets all Customers and UA data
select transaction_id, transaction_amount, transaction_approved ,fk_customerid_sender, fk_customerid_reciever from customertransaction				  -- gets all transactions