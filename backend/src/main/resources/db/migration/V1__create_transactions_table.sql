create table transactions
(
    id    serial primary key,
    card  varchar(255),
    cpf   varchar(255),
    date  date,
    hour  time(6),
    store_owner varchar(255),
    store_name varchar(255),
    type  varchar(255)
        constraint transactions_type_check
            check ((type)::text = ANY
                   ((ARRAY ['DEBIT'::character varying, 'BANK_SLIP'::character varying, 'FINANCING'::character varying, 'CREDIT'::character varying, 'LOAN_RECEIPT'::character varying, 'SALES'::character varying, 'TED_RECEIPT'::character varying, 'DOC_RECEIPT'::character varying, 'RENT'::character varying])::text[])),
    value numeric(38, 2),
    constraint uc_transactions unique (type, date, value, cpf, card, hour)
);